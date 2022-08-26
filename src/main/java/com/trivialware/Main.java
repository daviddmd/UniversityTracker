package com.trivialware;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Ponto inicial de execução do programa, responsável por importar os dois ficheiros JSON de mapa da universidade
 * e lista de movimentos e convertê-los na lista de localizações e movimentos respetivamente, e grafo das localizações
 * e as suas respetivas localizações. Adicionalmente, se o ficheiro de pessoas existir, uma lista de pessoas será
 * instanciada com as pessoas nos mesmos. Posteriormente, um objeto Universidade é criado com todas estas listas e
 * grafos, e o menu da universidade é invocado com este objeto Universidade criado.
 */
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