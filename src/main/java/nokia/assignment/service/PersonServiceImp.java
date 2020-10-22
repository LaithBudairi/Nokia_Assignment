package nokia.assignment.service;


import nokia.assignment.model.Person;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PersonServiceImp implements PersonService {

    private static List<Person> personList = new ArrayList<>();

    @Override
    public List<Person> get(String name) {
        return personList.stream().filter(person -> person.getName().equals(name)).collect(Collectors.toList());
    }

    @Override
    public boolean add(Person person) {
        // Check mem
        double memUsage = ((double) Runtime.getRuntime().freeMemory() / (double) Runtime.getRuntime().totalMemory()) * 100;
        if (memUsage >= 95) {
            return false;
        }

        // Check if there is a person with same id
        synchronized (personList) {
            String id = person.getId();
            Optional<Person> existingPerson = personList.stream().filter(p -> p.getId().equals(id)).findFirst();
            if (!existingPerson.isEmpty()) {
                return false;
            }
            personList.add(person);
        }

        return true;
    }

    @Override
    public synchronized int delete(String name) {
        int count = (int) personList.stream().filter(p -> p.getName().equals(name)).count();
        personList.removeIf(person -> person.getName().equals(name));

        return count;
    }

    public static void clearStorage() {
        personList = new ArrayList<>();
    }
}