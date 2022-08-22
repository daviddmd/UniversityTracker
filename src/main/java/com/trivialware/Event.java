package com.trivialware;

import java.time.LocalTime;

public class Event implements Comparable<Event> {
    //null se não existente
    private Person person;
    //null se não existente
    private Location location;
    private LocalTime startTime;
    /*
    Por defeito, é a hora máxima (23:59:59). Para uma determinada pessoa, ao inserir um novo evento, o último evento
    (o mais recente) da mesma terá como a sua hora de fim a hora de início deste novo evento. Caso contrário pode-se
    considerar que o último evento da pessoa durou (teoricamente) até ao fim do dia.
     */
    private LocalTime endTime;

    public Event(Person person, Location location, LocalTime startTime) {
        this.person = person;
        this.location = location;
        this.startTime = startTime;
        this.endTime = LocalTime.MAX;
    }

    public Event(Person person, Location location, LocalTime startTime, LocalTime endTime) {
        this.person = person;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
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

    @Override
    public String toString() {
        return "Event{" +
                "person=" + person +
                ", location=" + location +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }


    @Override
    public int compareTo(Event o) {
        return startTime.compareTo(o.getStartTime());
    }
}
