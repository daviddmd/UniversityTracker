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

    /**
     * Construtor para a classe Evento.
     * <p>
     * Um evento ou movimento é composto pelo identificador único da pessoa responsável por o gerar, o objeto Pessoa
     * correspondente a esse mesmo identifiador (se existir presentemente no sistema), a localização associada ao
     * evento gerado, a hora de início do evento (quando o movimento foi registado) e a hora de fim do evento, que
     * corresponde ao evento que imediatamente sucede este evento associado à pessoa que o gerou. Caso este seja o
     * último evento temporalmente, a hora de fim irá assumir a hora máxima, que é 23:59:59.
     *
     * @param person Objeto pessoa associado ao evento gerado, se existente
     * @param personId Identificador único associadao ao evento gerado
     * @param location Objeto localização associado à localização do evento gerado
     * @param startTime Hora de início do evento, associado à hora em que o movimento foi capturado
     */
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
        sb.append("Person: ").append(person == null ? String.format("Desconhecido (%s)", personId) : person.toString());
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

    /**
     * Verifica se um Evento se sobrepõe a outro
     * Um evento sobrepõe-se a outro, caso:
     * <ul>
     *     <li>O mesmo não seja igual ao evento a verificar</li>
     *     <li>A localização do mesmo seja igual à do evento a verificar</li>
     *     <li>A hora de início e fim do evento a verificar sobrepõe-se à hora de início e fim do próprio evento</li>
     * </ul>
     *
     * @param event Evento a verificar sobreposição
     * @return true se o mesmo se sobrepõe, false caso contrário
     */
    public boolean overlaps(Event event) {
        //Overlap de datas/horas
        //https://stackoverflow.com/questions/325933/determine-whether-two-date-ranges-overlap
        return this != event && getLocation().equals(event.getLocation()) &&
                (getStartTime().compareTo(event.getEndTime()) <= 0 && getEndTime().compareTo(event.getStartTime()) >= 0);
    }


}
