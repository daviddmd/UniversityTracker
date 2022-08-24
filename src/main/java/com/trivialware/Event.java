package com.trivialware;

import java.time.LocalTime;

public class Event implements Comparable<Event> {
    //null se não existente, Desconhecido
    private Person person;

    private String personId;
    //Não pode ser null
    private Location location;
    private LocalTime startTime;
    /*
    Por defeito, é a hora máxima (23:59:59). Para uma determinada pessoa, ao inserir um novo evento, o último evento
    (o mais recente) da mesma terá como a sua hora de fim a hora de início deste novo evento. Caso contrário pode-se
    considerar que o último evento da pessoa durou (teoricamente) até ao fim do dia.
     */
    private LocalTime endTime;

    public Event(Person person, String personId, Location location, LocalTime startTime) {
        this.person = person;
        this.personId = personId;
        this.location = location;
        this.startTime = startTime;
        this.endTime = LocalTime.MAX;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Person: ").append(person == null ? "Desconhecido" : person.toString());
        sb.append(" Location: ").append(location.toString());
        sb.append(" Start Time: ").append(startTime);
        if (endTime != LocalTime.MAX) {
            sb.append(" End Time: ").append(endTime);
        }
        return sb.toString();
    }


    @Override
    public int compareTo(Event o) {
        return startTime.compareTo(o.getStartTime());
    }


}
