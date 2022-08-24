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
    void setNumberOfPeopleCurrentlyInLocations() {
    }

    @Test
    void getPeopleAtLocationAtTime() {
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
    void addLocation() {
    }

    @Test
    void getLocationById() {
    }

    @Test
    void addPerson() {
    }

    @Test
    void removePerson() {
    }

    @Test
    void getPersonById() {
    }

    @Test
    void getCurrentEventByPerson() {
    }

    @Test
    void getLocations() {
    }

    @Test
    void getEvents() {
    }

    @Test
    void getPeople() {
    }

    @Test
    void setPeople() {
    }

    @Test
    void getNetwork() {
    }

    @Test
    void addEvent() {
    }

    @Test
    void updateEventsPeople() {
    }
}