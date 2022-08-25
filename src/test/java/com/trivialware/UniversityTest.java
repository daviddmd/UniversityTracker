package com.trivialware;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class UniversityTest {
    University university;

    @BeforeEach
    void setUp() {
        UnorderedListADT<Person> people;
        UnorderedListADT<Location> locations;
        UnorderedListADT<Event> events;
        UndirectedNetworkADT<Location> network;
        try (InputStream input = UniversityTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            String movementsFileName = prop.getProperty("app.movements_file_name");
            String mapFileName = prop.getProperty("app.map_file_name");
            String peopleFileName = prop.getProperty("app.people_file_name");
            if (Files.exists(Paths.get(peopleFileName))) {
                people = FileHelper.importPeople(peopleFileName);
            }
            else {
                people = new ArrayList<>();
            }
            locations = FileHelper.importLocations(mapFileName);
            events = FileHelper.importEvents(people, locations, movementsFileName);
            network = FileHelper.buildNetwork(locations, mapFileName);
            university = new University(locations, events, people, network);

        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void printStuff() {
        for (Event event : university.getEvents()) {
            System.out.println(event);
        }
        ListADT<Event> events = university.getEventsOfPersonInTimeFrame("1", LocalTime.of(12, 0), LocalTime.of(13, 50));
        for (Event event : events) {
            System.out.println(event.getLocation());
        }
        System.out.println("----");
        ListADT<Event> overlappingEvents = university.getOverlappingEvents(events);
        for (Event event : overlappingEvents) {
            System.out.println(event);
        }
    }

    @Test
    void addPerson() {
        Person person;
        assertEquals(7, university.getPeople().size());
        person = new Person("2", Person.Role.STUDENT, "Repetido");
        assertFalse(university.addPerson(person));
        assertEquals(7, university.getPeople().size());
        person = new Person("8", Person.Role.STUDENT, "Mário Leigo");
        assertTrue(university.addPerson(person));
        assertEquals(8, university.getPeople().size());
        assertEquals(person.getId(), university.getPeople().getLast().getId());
        Event event = university.getCurrentEventByPerson("55");
        assertNull(event.getPerson());
        person = new Person("55", Person.Role.STUDENT, "Mário Existente");
        assertTrue(university.addPerson(person));
        assertEquals(9, university.getPeople().size());
        assertNotNull(event.getPerson());
        assertEquals(person, event.getPerson());
        assertEquals("Mário Existente", event.getPerson().getName());
    }

    @Test
    void removePerson() {
        Person person;
        person = university.getPersonById("1");
        assertEquals("Carlos Sousa", person.getName());
        assertEquals(7, university.getPeople().size());
        assertTrue(university.removePerson(person));
        assertEquals(6, university.getPeople().size());
        Event event = university.getCurrentEventByPerson("2");
        assertNotNull(event.getPerson());
        person = new Person("2", Person.Role.STUDENT, "Pedro Santos");
        assertEquals(person, university.getPersonById("2"));
        assertTrue(university.removePerson(person));
        assertNull(event.getPerson());
        assertEquals(5, university.getPeople().size());
        person = new Person("11", Person.Role.OTHER, "Inês Istente");
        assertFalse(university.removePerson(person));
        assertEquals(5, university.getPeople().size());
    }

    @Test
    void getAccessViolations() {
        ListADT<Event> violations = university.getAccessViolations();
        String[] expectedOrder = new String[]{"1", "55", "55", "7", "3", "5"};
        int currentIndex = 0;
        assertEquals(expectedOrder.length, violations.size());
        for (Event violation : violations) {
            assertEquals(expectedOrder[currentIndex++], violation.getPersonId());
        }
    }

    @Test
    void setNumberOfPeopleCurrentlyInLocations() {
        university.setNumberOfPeopleCurrentlyInLocations();
        assertEquals(3, university.getLocationById("A2").getCurrentNumberPeople());
        assertEquals(1, university.getLocationById("A3").getCurrentNumberPeople());
        assertEquals(1, university.getLocationById("A4").getCurrentNumberPeople());
        assertEquals(1, university.getLocationById("SA").getCurrentNumberPeople());
        assertEquals(2, university.getLocationById("G1").getCurrentNumberPeople());
    }

    /*




    @Test
    void getOverlappingEvents() {
    }

    @Test
    void getEventsOfPersonInTimeFrame() {
    }

    @Test
    void getCurrentEventByPerson() {
    }

    @Test
    void getNetwork() {
    }

    @Test
    void addEvent() {
    }

     */
}