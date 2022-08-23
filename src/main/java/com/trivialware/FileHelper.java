package com.trivialware;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;

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

    //No processo de adição ou importação de pessoas, atualizar campo de pessoa no objeto evento se o mesmo for null e se
    //id for correspondente
    public static UnorderedListADT<Person> importPeople(String fileName) throws IOException {
        FileReader fr = new FileReader(fileName, StandardCharsets.UTF_8);
        JSONTokener tokener = new JSONTokener(fr);
        JSONArray peopleJSON = new JSONArray(tokener);
        JSONObject personJSON;
        UnorderedListADT<Person> people = new ArrayList<>(peopleJSON.length());
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

    public static void exportPeople(UnorderedListADT<Person> peopleList, String fileName) {

    }

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
        int currentIndex = 0;
        Event[] eventArray = new Event[eventsJSON.length()];
        for (int i = 0; i < eventsJSON.length(); i++) {
            eventJSON = eventsJSON.getJSONObject(i);
            locationId = eventJSON.getString("location_id");
            personId = eventJSON.getString("person_id");
            timeString = eventJSON.getString("time");
            time = LocalTime.parse(timeString);
            location = getLocationById(locations, locationId);
            person = getPersonById(people, personId);
            if (location != null) {
                eventArray[currentIndex] = new Event(person, personId, location, time);
                currentIndex += 1;
            }
            else {
                throw new IOException("Invalid Location Found");
            }
        }
        /*
        Após termos um array todas as Localizações no ficheiro, iremos organizar o mesmo a partir da sua data de
        atividade e transferir os mesmos para uma lista não organizada. Iremos posteriormente usar esta mesma lista
        para adicionar cada elemento 1 a 1 à lista da classe Universidade, onde a data de fim do evento irá ser
        deduzida para cada evento de cada utilizador, incluindo os pseudo-anónimos.
         */
        ArraySorts.quickSort(eventArray);
        UnorderedListADT<Event> events = new ArrayList<>(currentIndex);
        for (int i = 0; i < eventArray.length; i++) {
            events.addLast(eventArray[i]);
        }
        return events;
    }


}
