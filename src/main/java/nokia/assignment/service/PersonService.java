package nokia.assignment.service;

import nokia.assignment.exception.entity.EntityNotFoundException;
import nokia.assignment.model.Person;

import java.util.List;

public interface PersonService {
    List<Person> get(String name) throws EntityNotFoundException;
    
    boolean add(Person person);

    int delete(String id);
}
