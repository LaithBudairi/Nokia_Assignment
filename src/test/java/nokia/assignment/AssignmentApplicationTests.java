package nokia.assignment;

import nokia.assignment.model.Person;
import nokia.assignment.service.PersonServiceImp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.net.URISyntaxException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AssignmentApplicationTests {

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate testRestTemplate;
    private static String api;

    @BeforeEach
    void contextLoads() {
        api = "http://localhost:" + port + "/api/v1/person/";
    }

    @Test
    public void addPerson_addsNewPerson_addedSuccessfully200() throws URISyntaxException {
        clearStorage();

        Person person = new Person("1", "Steve");
        HttpEntity<Person> request = new HttpEntity<>(person);
        URI uri = new URI(api);
        ResponseEntity<Boolean> res = testRestTemplate.postForEntity(uri, request, Boolean.class);

        Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
        Assertions.assertEquals(true, res.getBody());
    }

    @Test
    public void deletePerson_deletesPersonsWithSpecifiedName_deletedSuccessfully200() throws URISyntaxException {
        clearStorage();

        String name = "Steve";
        URI uri = new URI(api + name);

        Person[] person = new Person[] {
                new Person("1", name),
                new Person("2", "Mark"),
                new Person("3", name)
        };

        // add person
        for (int i = 0; i < person.length; i++) {
            addPerson(person[i]);
        }

        // delete person
        testRestTemplate.delete(uri);


        // check if deleted (count = 0)
        ResponseEntity<Person[]> res = testRestTemplate.getForEntity(uri, Person[].class);

        Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
        Assertions.assertEquals(0, res.getBody().length);
    }

    @Test
    public void searchPerson_searchesForPersonsWithSpecifiedName_retrievedSuccessfully200() throws URISyntaxException {
        clearStorage();

        String name = "Steve";
        URI uri = new URI(api);
        Person[] person = new Person[] {
                new Person("1", name),
                new Person("2", "Mark"),
                new Person("3", name)
        };

        for (int i = 0; i < person.length; i++) {
            HttpEntity<Person> request = new HttpEntity<>(person[i]);
            testRestTemplate.postForEntity(uri, request, Boolean.class);
        }

        // search person (count = 2)
        uri = new URI(api + name);
        ResponseEntity<Person[]> res = testRestTemplate.getForEntity(uri, Person[].class);

        Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
        Assertions.assertEquals(2, res.getBody().length);
    }

    @Test
    public void addPersonConcurrently_addsTwoPersonsWithSameID_addedOnlyOnePersonSuccessfully() throws Exception {
        boolean[] b = new boolean[2];
        Person person = new Person("1", "Steve");

        Thread t1 = new Thread(() -> {
            try {
                b[0] = addPerson(person);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                b[1] = addPerson(person);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        Assertions.assertEquals(false, b[0] && b[1]);
    }

    private void clearStorage() {
        PersonServiceImp.clearStorage();
    }

    private boolean addPerson(Person person) throws URISyntaxException {
        HttpEntity<Person> request = new HttpEntity<>(person);
        URI uri = new URI(api);
        ResponseEntity<Boolean> res = testRestTemplate.postForEntity(uri, request, Boolean.class);
        return res.getBody();
    }
}