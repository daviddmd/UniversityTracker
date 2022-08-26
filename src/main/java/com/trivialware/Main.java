package com.trivialware;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {

        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            String movementsFileName = prop.getProperty("app.movements_file_name");
            String mapFileName = prop.getProperty("app.map_file_name");
            String peopleFileName = prop.getProperty("app.people_file_name");
            if (!Files.exists(Paths.get(movementsFileName)) || !Files.exists(Paths.get(mapFileName))) {
                System.out.println("Os ficheiros de Mapa ou Movimentos não existem, por favor, crie-os.");
                return;
            }
            UnorderedListADT<Person> people;
            if (Files.exists(Paths.get(peopleFileName))) {
                people = FileHelper.importPeople(peopleFileName);
            }
            else {
                people = new ArrayList<>();
            }
            UnorderedListADT<Location> locations = FileHelper.importLocations(mapFileName);
            UnorderedListADT<Event> events = FileHelper.importEvents(people, locations, movementsFileName);
            UndirectedNetworkADT<Location> network = FileHelper.buildNetwork(locations, mapFileName);
            University university = new University(locations, events, people, network);
            UniversityMenu menu = new UniversityMenu(university, peopleFileName);
            menu.mainMenu();

        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}