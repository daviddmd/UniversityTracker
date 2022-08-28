package com.trivialware;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;

/**
 * Classe que contém todos os métodos estáticos auxiliares para lidar com ficheiros
 */

public class FileHelper {

    private static Location getLocationById(ListADT<Location> locations, String locationId) {
        for (Location location : locations) {
            if (location.getId().equals(locationId)) {
                return location;
            }
        }
        return null;
    }

    private static Person getPersonById(ListADT<Person> people, String personId) {
        for (Person person : people) {
            if (person.getId().equals(personId)) {
                return person;
            }
        }
        return null;
    }

    /**
     * Método para importar localizações do ficheiro JSON de localizações para uma Lista não ordenada de localizações
     *
     * @param fileName Caminho do ficheiro de Localizações
     * @return Lista não ordenada de Localizações
     * @throws IOException Caso o ficheiro não exista ou exista um erro de JSON no ficheiro
     */
    public static UnorderedListADT<Location> importLocations(String fileName) throws IOException {
        FileReader fr = new FileReader(fileName, StandardCharsets.UTF_8);
        JSONTokener tokener = new JSONTokener(fr);
        JSONObject object = new JSONObject(tokener);
        JSONArray locationsJSON = object.getJSONArray("locations");
        UnorderedListADT<Location> locations = new ArrayList<>(locationsJSON.length());
        JSONObject locationJSON;
        String id, name;
        int maximumCapacity;
        Person.Role restrictedTo;
        for (int i = 0; i < locationsJSON.length(); i++) {
            locationJSON = locationsJSON.getJSONObject(i);
            id = locationJSON.getString("id");
            name = locationJSON.getString("name");
            restrictedTo = Person.Role.fromString(locationJSON.getString("restricted_to"));
            maximumCapacity = locationJSON.getInt("maximum_capacity");
            locations.addLast(new Location(id, name, maximumCapacity, restrictedTo));
        }
        return locations;
    }

    /**
     * Método para importar Pessoas do ficheiro JSON de Pessoas (caso exista) para uma Lista não ordenada de Pessoas
     *
     * @param fileName Caminho do ficheiro de Pessoas
     * @return Lista não ordenada de Pessoas
     * @throws IOException Caso o ficheiro não exista ou exista um erro de JSON no ficheiro
     */
    public static UnorderedListADT<Person> importPeople(String fileName) throws IOException {
        FileReader fr = new FileReader(fileName, StandardCharsets.UTF_8);
        JSONTokener tokener = new JSONTokener(fr);
        JSONArray peopleJSON = new JSONArray(tokener);
        JSONObject personJSON;
        UnorderedListADT<Person> people = new ArrayList<>(peopleJSON.length() * 2);
        String id, name;
        Person.Role role;
        for (int i = 0; i < peopleJSON.length(); i++) {
            personJSON = peopleJSON.getJSONObject(i);
            id = personJSON.getString("id");
            name = personJSON.getString("name");
            role = Person.Role.fromString(personJSON.getString("role"));
            people.addLast(new Person(id, role, name));
        }
        return people;
    }

    /**
     * Método para exportar Pessoas do sistema para um Ficheiro JSON
     *
     * @param peopleList Lista não Ordenada com todos os Objetos de Pessoas do Sistema
     * @param fileName   Caminho do ficheiro das pessoas
     * @throws IOException Caso o ficheiro não exista
     */
    public static void exportPeople(UnorderedListADT<Person> peopleList, String fileName) throws IOException {
        JSONArray peopleJSON = new JSONArray();
        JSONObject personJSON;
        for (Person person : peopleList) {
            personJSON = new JSONObject();
            personJSON.put("id", person.getId());
            personJSON.put("name", person.getName());
            personJSON.put("role", person.getRole() == null ? "" : Person.Role.fromRole(person.getRole()));
            peopleJSON.put(personJSON);
        }
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(fileName), StandardCharsets.UTF_8)) {
            writer.write(peopleJSON.toString(4));
        }
    }

    /**
     * Constrói a rede associado a todas as localizações importadas e a todas as relações entre as localizações
     * no ficheiro JSON que representa o mapa da Universidade
     *
     * @param locations Lista não ordenada com as localizações importadas
     * @param fileName  Ficheiro JSON que contém o mapa das localizações, contendo a lista de localizações e especialmente
     *                  as relações entre cada localização, como a origem, destino e peso (distância em metros)
     * @return Objeto que representa o grafo pesado (rede) não direcionado representando as localizações e respetivas
     * localizações da universidade
     * @throws IOException Caso o ficheiro JSON de mapa não exista ou exista um erro de sintaxe JSON no mesmo
     */
    public static UndirectedNetworkADT<Location> buildNetwork(ListADT<Location> locations, String fileName) throws IOException {
        UndirectedNetworkADT<Location> network = new AdjacencyListUndirectedNetwork<>();
        for (Location location : locations) {
            network.addVertex(location);
        }
        FileReader fr = new FileReader(fileName, StandardCharsets.UTF_8);
        JSONTokener tokener = new JSONTokener(fr);
        JSONObject object = new JSONObject(tokener);
        JSONArray relationshipsJSON = object.getJSONArray("relationships");
        String from, to;
        Location fromLocation, toLocation;
        double distance;
        JSONObject relationshipJSON;
        for (int i = 0; i < relationshipsJSON.length(); i++) {
            relationshipJSON = relationshipsJSON.getJSONObject(i);
            from = relationshipJSON.getString("from");
            to = relationshipJSON.getString("to");
            distance = relationshipJSON.getDouble("distance");
            fromLocation = getLocationById(locations, from);
            toLocation = getLocationById(locations, to);
            if (fromLocation != null && toLocation != null) {
                network.addEdge(fromLocation, toLocation, distance);
            }
        }
        return network;
    }

    /**
     * Importa todos os movimentos no ficheiro de movimentos. É esperado que os mesmos estejam fora de ordem, portanto
     * os mesmos serão ordenados consoante a sua ordem de chegada, para assegurar que uma linha cronológica correta
     * de cada pessoa é construída (como a data de fim de cada evento sendo definida como a data de início do próximo).
     *
     * @param fileName Nome do ficheiro que contém os movimentos
     * @return Lista com todos os Movimentos ocorridos no ficheiro
     */
    public static UnorderedListADT<Event> importEvents(ListADT<Person> people, ListADT<Location> locations, String fileName) throws IOException {
        FileReader fr = new FileReader(fileName, StandardCharsets.UTF_8);
        JSONTokener tokener = new JSONTokener(fr);
        JSONArray eventsJSON = new JSONArray(tokener);
        JSONObject eventJSON;
        String locationId, personId, timeString;
        Location location;
        Person person;
        LocalTime time;
        UnorderedListADT<Event> events = new ArrayList<>(eventsJSON.length());
        for (int i = 0; i < eventsJSON.length(); i++) {
            eventJSON = eventsJSON.getJSONObject(i);
            locationId = eventJSON.getString("location_id");
            personId = eventJSON.getString("person_id");
            if (locationId.equals("") || personId.equals("")) {
                throw new IOException("Empty Person or Location ID found");
            }
            timeString = eventJSON.getString("time");
            time = LocalTime.parse(timeString);
            location = getLocationById(locations, locationId);
            person = getPersonById(people, personId);
            if (location != null) {
                events.addLast(new Event(person, personId, location, time));
            }
            else {
                throw new IOException("Invalid Location Found");
            }
        }
        return events;
    }


}
