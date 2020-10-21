package nokia.assignment.service;


import nokia.assignment.exception.entity.EntityNotFoundException;
import nokia.assignment.model.Person;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonServiceImp implements PersonService {

    List<Person> personList;

    public PersonServiceImp() {
        personList = new ArrayList<>();
    }

    @Override
    public List<Person> get(String name) throws EntityNotFoundException {
        List<Person> people = personList.stream().filter(person -> person.getName().equals(name)).collect(Collectors.toList());
        System.out.println(people);
        if (people.isEmpty()) {
            throw new EntityNotFoundException("No person with name=" + name + " was found");
        }
        return people;
    }

    @Override
    public boolean add(Person person) {
        String id = person.getId();
        boolean[] personExists = new boolean[1];

        try {
            personList.forEach(p -> {
                if (p.getId().equals(id)) {
                    personExists[0] = true;
                }
            });

            if (!personExists[0]) {
                personList.add(person);
            } else {
                return false;
            }
        } catch (OutOfMemoryError e) {
            personList.remove(person);
            e.printStackTrace();
            return false;
        }
        System.out.println(personList.get(0).getName());
        return true;
    }

    @Override
    public int delete(String name) {
        int count = (int) personList.stream().filter(p -> p.getName().equals(name)).count();
        personList.removeIf(person -> person.getName().equals(name));
        return count;
    }
}
