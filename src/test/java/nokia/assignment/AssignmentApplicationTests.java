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

    private TestRestTemplate testRestTemplate;
    @LocalServerPort
    private int port;

    @BeforeEach
    void contextLoads() {
        testRestTemplate = new TestRestTemplate();
    }

    @Test
    public void addPerson_addsNewPerson_addedSuccessfully200() throws URISyntaxException {
        Person person = new Person("1", "Steve");
        URI uri = new URI("http://localhost:" + port + "/api/v1/person/");

        HttpEntity<Person> request = new HttpEntity<>(person);
        ResponseEntity<Boolean> res = testRestTemplate.postForEntity(uri, request, Boolean.class);

        Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
        Assertions.assertEquals(true, res.getBody());
    }

    @Test
    public void deletePerson_deletesPerson_deletedSuccessfully200() throws URISyntaxException {
        // add person
        Person addedPerson = new Person("1", "Steve");
        URI uri = new URI("http://localhost:" + port + "/api/v1/person/");

        HttpEntity<Person> request = new HttpEntity<>(addedPerson);
        testRestTemplate.postForEntity(uri, request, Boolean.class);

        // delete person
        uri = new URI("http://localhost:" + port + "/api/v1/person/1");
        testRestTemplate.delete(uri);

        // check if deleted
        String name = "Steve";
        uri = new URI("http://localhost:" + port + "/api/v1/person/" + name);
        ResponseEntity<Person[]> res = testRestTemplate.getForEntity(uri, Person[].class);
        Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    public void searchPerson_searchesForPersonsWithSpecifiedName_retrievedSuccessfully200() throws URISyntaxException, InterruptedException {
        // add person
        Person person = new Person("1", "Steve");
        URI uri = new URI("http://localhost:" + port + "/api/v1/person/");

        HttpEntity<Person> request = new HttpEntity<>(person);
        testRestTemplate.postForEntity(uri, request, Boolean.class);

        // find person
        String name = "Steve";
        uri = new URI("http://localhost:" + port + "/api/v1/person/" + name);

        ResponseEntity <Person[]>res = testRestTemplate.getForEntity(uri, Person[].class);
        Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    }
}