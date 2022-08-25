package com.trivialware;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.List;
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
        assertEquals(3, university.getLocationById("A2").getCurrentNumberPeople());
        assertEquals(1, university.getLocationById("A3").getCurrentNumberPeople());
        assertEquals(1, university.getLocationById("A4").getCurrentNumberPeople());
        assertEquals(1, university.getLocationById("SA").getCurrentNumberPeople());
        assertEquals(2, university.getLocationById("G1").getCurrentNumberPeople());
    }

    @Test
    void getEventsOfPersonInTimeFrame() {
        ListADT<Event> events;
        String[] expectedOrder;
        int currentIndex;
        expectedOrder = new String[]{"SA", "A1", "A3"};
        currentIndex = 0;
        events = university.getEventsOfPersonInTimeFrame("1", LocalTime.of(12, 0, 0), LocalTime.of(13, 55, 30));
        for (Event event : events) {
            assertEquals(expectedOrder[currentIndex++], event.getLocation().getId());
        }
        expectedOrder = new String[]{"SA", "A1", "A3", "A4"};
        currentIndex = 0;
        events = university.getEventsOfPersonInTimeFrame("1", LocalTime.of(12, 0, 0), LocalTime.of(13, 55, 31));
        for (Event event : events) {
            assertEquals(expectedOrder[currentIndex++], event.getLocation().getId());
        }
        expectedOrder = new String[]{"A1", "A3", "A4"};
        currentIndex = 0;
        events = university.getEventsOfPersonInTimeFrame("1", LocalTime.of(12, 30, 31), LocalTime.of(15, 55, 31));
        for (Event event : events) {
            assertEquals(expectedOrder[currentIndex++], event.getLocation().getId());
        }
    }

    @Test
    void getOverlappingEventsOfPersonInTimeFrame() {
        String[] contactsAtTimeFrame;
        ListADT<Event> overLappingEvents;
        int currentIndex;
        contactsAtTimeFrame = new String[]{"55", "6", "4"};
        currentIndex = 0;
        overLappingEvents = university.getOverlappingEventsOfPersonInTimeFrame("2", LocalTime.of(15, 50, 55), LocalTime.of(16, 30, 0));
        assertEquals(contactsAtTimeFrame.length, overLappingEvents.size());
        for (Event event : overLappingEvents) {
            assertEquals(contactsAtTimeFrame[currentIndex++], event.getPersonId());
        }

        contactsAtTimeFrame = new String[]{"3", "55", "2", "2"};
        currentIndex = 0;
        overLappingEvents = university.getOverlappingEventsOfPersonInTimeFrame("1", LocalTime.of(12, 0, 0), LocalTime.of(13, 55, 0));
        assertEquals(contactsAtTimeFrame.length, overLappingEvents.size());
        for (Event event : overLappingEvents) {
            assertEquals(contactsAtTimeFrame[currentIndex++], event.getPersonId());
        }

        contactsAtTimeFrame = new String[]{"55", "4"};
        currentIndex = 0;
        overLappingEvents = university.getOverlappingEventsOfPersonInTimeFrame("2", LocalTime.of(18, 20, 10), LocalTime.MAX);
        assertEquals(contactsAtTimeFrame.length, overLappingEvents.size());
        for (Event event : overLappingEvents) {
            assertEquals(contactsAtTimeFrame[currentIndex++], event.getPersonId());
        }

    }

    @Test
    void getCurrentLocationOfPerson() {
        assertEquals("G1", university.getCurrentLocationOfPerson("5").getId());
        assertEquals("G1", university.getCurrentLocationOfPerson("3").getId());
        assertEquals("SA", university.getCurrentLocationOfPerson("7").getId());
        assertEquals("A3", university.getCurrentLocationOfPerson("6").getId());
        assertEquals("A2", university.getCurrentLocationOfPerson("4").getId());
        assertEquals("A2", university.getCurrentLocationOfPerson("2").getId());
        assertEquals("A2", university.getCurrentLocationOfPerson("55").getId());
    }

    @Test
    void emergencyTest() {
        StackADT<Location> pathToEmergency;
        String[] expectedLocations;
        int currentIndex;
        expectedLocations = new String[]{"A4", "A3", "A2", "A1", "SA", "EMERGENCY_SPOT"};
        currentIndex = 0;
        pathToEmergency = university.getShortestPathToEmergencyPerson("1");
        assertEquals(expectedLocations.length, pathToEmergency.size());
        while (!pathToEmergency.empty()) {
            assertEquals(expectedLocations[currentIndex++], pathToEmergency.pop().getId());
        }
    }
}