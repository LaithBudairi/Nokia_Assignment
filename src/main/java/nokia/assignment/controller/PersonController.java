package nokia.assignment.controller;

import nokia.assignment.exception.entity.EntityNotFoundException;
import nokia.assignment.model.Person;
import nokia.assignment.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/v1/")
@RestController
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping(value = "person/{name}")
    public List<Person> search(@PathVariable(value = "name") String name) throws EntityNotFoundException {
        return personService.get(name);
    }

    @PostMapping(value = "person")
    public boolean add(@RequestBody Person person) {
        return personService.add(person);
    }

    @DeleteMapping(value = "person/{name}")
    public int delete(@PathVariable(value = "name") String name) {
        return personService.delete(name);
    }

}