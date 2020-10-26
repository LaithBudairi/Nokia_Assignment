package nokia.assignment.service;


import nokia.assignment.model.Person;
import org.springframework.stereotype.Service;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PersonServiceImp implements PersonService {

    private static ConcurrentHashMap<String, SoftReference<Person>> map = new ConcurrentHashMap<>();

    @Override
    public List<Person> get(String name) {
        List<Person> persons = new ArrayList<>();

        map.forEach((k, v) -> {
            if(v.get().getName().equals(name)) {
                persons.add(v.get());
            }
        });

        return persons;
    }

    @Override
    public boolean add(Person person) {
        SoftReference<Person> p = new SoftReference<>(person);
        SoftReference<Person> existingPerson = map.putIfAbsent(person.getId(), p);

        if(existingPerson != null) {
            return false;
        }

        return true;
    }

    @Override
    public int delete(String name) {
        int count = map.size();
        map.forEach((k, v) -> {
            if(v.get().getName().equals(name)) {
                map.remove(k);
            }
        });

        return count - map.size();
    }

    public static void clearStorage() {
        map.clear();
    }
}