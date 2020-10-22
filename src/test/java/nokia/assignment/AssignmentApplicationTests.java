package nokia.assignment;

import nokia.assignment.model.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    private TestRestTemplate testRestTemplate;

    @BeforeEach
    void contextLoads() throws URISyntaxException {
        testRestTemplate = new TestRestTemplate();
    }

    @Test
    public void addPerson_addsNewPerson_addedSuccessfully200() throws URISyntaxException {
        Person person = new Person("1", "Steve");
        HttpEntity<Person> request = new HttpEntity<>(person);

        URI uri = new URI("http://localhost:" + port + "/api/v1/person/");
        ResponseEntity<Boolean> res = testRestTemplate.postForEntity(uri, request, Boolean.class);

        Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
        Assertions.assertEquals(true, res.getBody());
    }

    @Test
    public void deletePerson_deletesPerson_deletedSuccessfully200() throws URISyntaxException {
        URI uri;
        Person[] person = new Person[]{
                new Person("1", "Steve"),
                new Person("2", "Mark"),
                new Person("3", "Steve")
        };

        // add person
        for (int i = 0; i < person.length; i++) {
            uri = new URI("http://localhost:" + port + "/api/v1/person/");
            HttpEntity<Person> request = new HttpEntity<>(person[i]);
            testRestTemplate.postForEntity(uri, request, Boolean.class);
        }

        // delete person
        uri = new URI("http://localhost:" + port + "/api/v1/person/1");
        testRestTemplate.delete(uri);

        // check if deleted (count = 2)
        String name = "Steve";
        uri = new URI("http://localhost:" + port + "/api/v1/person/" + name);
        ResponseEntity<Person[]> res = testRestTemplate.getForEntity(uri, Person[].class);
        Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
        Assertions.assertEquals(2, res.getBody().length);
    }

    @Test
    public void searchPerson_searchesForPersonsWithSpecifiedName_retrievedSuccessfully200() throws URISyntaxException {

        URI uri;
        Person[] person = new Person[]{new Person("1", "Steve"), new Person("2", "Mark"), new Person("3", "Steve")};

        for (int i = 0; i < person.length; i++) {
            uri = new URI("http://localhost:" + port + "/api/v1/person/");
            HttpEntity<Person> request = new HttpEntity<>(person[i]);
            testRestTemplate.postForEntity(uri, request, Boolean.class);
        }

        // find person (count = 2)
        String name = "Steve";
        uri = new URI("http://localhost:" + port + "/api/v1/person/" + name);

        ResponseEntity<Person[]> res = testRestTemplate.getForEntity(uri, Person[].class);

        Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
        Assertions.assertEquals(2, res.getBody().length);
    }

    @Test
    public void addPersonConcurrently_addsTwoPersonsWithSameID_addedOnlyOnePersonSuccessfully() throws Exception {
        boolean[] b = new boolean[2];
        Thread t1 = new Thread(() -> {
            Thread.currentThread().setName("T1");
            Person person = new Person("1", "Steve");
            HttpEntity<Person> request = new HttpEntity<>(person);
            URI uri = null;
            try {
                uri = new URI("http://localhost:" + port + "/api/v1/person/");
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            ResponseEntity<Boolean> res = testRestTemplate.postForEntity(uri, request, Boolean.class);

            b[0] = res.getBody();

        });

        Thread t2 = new Thread(() -> {
            Thread.currentThread().setName("T2");
            Person person = new Person("1", "Steve");
            HttpEntity<Person> request = new HttpEntity<>(person);
            URI uri = null;
            try {
                uri = new URI("http://localhost:" + port + "/api/v1/person/");
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            ResponseEntity<Boolean> res = testRestTemplate.postForEntity(uri, request, Boolean.class);

            b[1] = res.getBody();

        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        Assertions.assertEquals(false, b[0] && b[1]);
    }
}