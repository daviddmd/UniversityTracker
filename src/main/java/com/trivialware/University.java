package com.trivialware;

import java.time.LocalTime;

/**
 * Classe que representa a Universidade. Contém a lista de Localizações, Eventos (indiretamente ligados através das suas
 * horas de inícios e fim que geram uma cronologia de eventos associados a várias localizações para cada pessoa) e
 * Pessoas. Contém vários métodos para resolver diversos problemas associados à monitorização dos movimentos das pessoas
 * num estabelecimento de ensino superior. Tem também o grafo pesado não direcionado (rede) que representa todas as
 * ligações entre as localizações da universidade.
 * É obrigatória a existência dos ficheiros das localizações e eventos, portanto irá sempre ser instanciado um objeto
 * universidade com as localizações, eventos e rede gerada a partir das localizações e as suas relações. Se existir
 * um ficheiro com as pessoas, o mesmo será também importado.
 */
/*
Não se usaram bastante linkedlists porque a natureza da informação neste programa é maioritariamente imutável, portanto
não há necessidade de operações que trariam vantagem às mesmas como remover o primeiro elemento ou inserir no meio da lista
 */
public class University {
    //Lista de Pessoas, Localizações e Eventos, métodos para caminhos mais curtos entre X e Ponto Emergência
    private static final String EMERGENCY_SPOT_ID = "EMERGENCY_SPOT";
    private final UnorderedListADT<Location> locations;
    //Pode vir a ser uma AVL Tree
    private final UnorderedListADT<Event> events;
    private UnorderedListADT<Person> people;

    private final UndirectedNetworkADT<Location> network;


    public University(UnorderedListADT<Location> locations, UnorderedListADT<Event> events, UnorderedListADT<Person> people, UndirectedNetworkADT<Location> network) {
        this.locations = locations;
        this.events = new ArrayList<>(events.size());
        /*
        Após termos um array todas as Localizações no ficheiro, iremos organizar o mesmo a partir da sua data de
        atividade e transferir os mesmos para uma lista não organizada. Iremos posteriormente usar esta mesma lista
        para adicionar cada elemento 1 a 1 à lista da classe Universidade, onde a data de fim do evento irá ser
        deduzida para cada evento de cada utilizador, incluindo os pseudo-anónimos.
         */
        int currentIndex = 0;
        Event[] eventArray = new Event[events.size()];
        for (Event event : events) {
            eventArray[currentIndex++] = event;
        }
        ArraySorts.quickSort(eventArray);
        for (Event event : eventArray) {
            addEvent(event);
        }
        this.people = people;
        this.network = network;
    }


    /**
     * Adiciona uma localização da universidade ao sistema caso uma localização com o identificador a adicionar já
     * não exista no sistema
     *
     * @param location Objeto que representa a localização a adicionar, com o Identificador único de uma Localização
     *                 da universidade, o nome extenso dessa localização, a capacidade máxima dessa localização
     *                 (inteiro) e o papel de Pessoa a que esta localização está restrito (Exemplo, apenas pode ser
     *                 utilizado por pessoas do papel Professor ou Funcionário; caso seja null, significa que pode
     *                 ser utilizado por qualquer pessoa independentemente do seu papel).
     * @return true se a Localização foi adicionada com sucesso, false caso já exista uma localização com o
     * identificador a adicionar no sistema.
     */
    public boolean addLocation(Location location) {
        if (locations.contains(location)) {
            return false;
        }
        locations.addLast(location);
        return true;
    }

    /**
     * Obtém uma localização no sistema a partir do seu identificador único, caso exista
     *
     * @param locationId Identificador único de uma localização na Universidade
     * @return Objeto que representa esta localização caso uma localização exista para um dado identificador, ou null
     * caso contrário
     */
    public Location getLocationById(String locationId) {
        for (Location location : locations) {
            if (location.getId().equals(locationId)) {
                return location;
            }
        }
        return null;
    }

    /**
     * Adiciona uma pessoa à lista de pessoas, caso já não exista uma pessoa com o mesmo identificador único
     *
     * @param person Objeto que representa uma Pessoa a adicionar, com o seu identificador único, papel e nome
     * @return true se a Pessoa foi adicionada com sucesso, false caso já exista uma Pessoa no sistema com o mesmo
     * identificador único
     */
    public boolean addPerson(Person person) {
        if (people.contains(person)) {
            return false;
        }
        people.addLast(person);
        addPersonToEvents(person);
        return true;
    }

    public boolean removePerson(Person person) {
        if (people.remove(person)) {
            removePersonFromEvents(person);
            return true;
        }
        return false;
    }

    /**
     * Obtém uma pessoa pelo seu identificador único
     *
     * @param personId Identificador único da Pessoa
     * @return Objeto que representa essa pessoa ou null caso a mesma não exista no sistema
     */
    public Person getPersonById(String personId) {
        for (Person person : people) {
            if (person.getId().equals(personId)) {
                return person;
            }
        }
        return null;
    }

    /**
     * Retorna o último Evento, ou evento mais recente capturado de uma determinada pessoa num dado dia
     *
     * @param personId Identificador único da pessoa
     * @return O último evento de uma pessoa ou null caso a pessoa não tenha eventos nesse dia
     */
    public Event getCurrentEventByPerson(String personId) {
        for (Event event : events) {
            if (event.getPersonId().equals(personId) && event.getEndTime().equals(LocalTime.MAX)) {
                return event;
            }
        }
        return null;
    }

    /**
     * Obtém a lista de Localizações da Universidade no sistema. Cada localização tem um Identificador Único, Nome
     * extenso, lotação máxima (número inteiro não negativo) e papel a que esta localização está restrita (como se a
     * mesma é exclusiva a funcionários, docentes, ou null caso não esteja restrita a nenhum papel).
     *
     * @return Lista de Localizações no Sistema
     */
    public UnorderedListADT<Location> getLocations() {
        return locations;
    }

    /**
     * Obtém a lista de eventos do Sistema. Cada evento é composto pela pessoa que o gerou, a localização onde foi
     * capturado por um sensor (gerado), a hora de início (hora em que foi capturado) do mesmo e a hora de fim,
     * correspondente à data de início do evento que temporalmente sucede este, associado à pessoa que gerou
     * o mesmo
     *
     * @return Lista de Eventos no Sistema
     */
    public UnorderedListADT<Event> getEvents() {
        return events;
    }

    /**
     * Obtém a lista de Pessoas do sistema. Cada pessoa tem um identificador único, nome e papel correspondente da
     * Universidade.
     *
     * @return Lista de Pessoas no Sistema
     */
    public UnorderedListADT<Person> getPeople() {
        return people;
    }

    public void setPeople(UnorderedListADT<Person> people) {
        this.people = people;
    }

    public UndirectedNetworkADT<Location> getNetwork() {
        return network;
    }

    /**
     * Adiciona um evento ao Sistema. Cada evento contém uma Localização (obrigatoriamente existente), uma Pessoa
     * (que pode ser inexistente ou nula, mas cujo ID tem de existir caso o mesmo seja futuramente associado a uma pessoa
     * a criar) e uma Hora. Se a pessoa for desconhecida,
     *
     * @param event Evento a adicionar
     */
    public void addEvent(Event event) {
        Event currentEventByPerson = getCurrentEventByPerson(event.getPersonId());
        if (currentEventByPerson == null) {
            events.addLast(event);
        }
        else {
            currentEventByPerson.setEndTime(event.getStartTime());
            events.addLast(event);
        }
    }

    /**
     * Esta função é utilizada quando se aciona a função de importação de pessoas para o sistema. Irá atualizar
     * todas as pessoas de todos os eventos consoante os novos valores, sejam os valores atuais das pessoas nos
     * eventos diferentes de null ou não.
     */
    public void updateEventsPeople() {
        for (Event event : events) {
            event.setPerson(getPersonById(event.getPersonId()));
        }
    }

    /**
     * Esta função é utilizada no momento de adição de uma pessoa ao sistema. Atualiza o objeto de pessoa do evento
     * caso o ID da pessoa seja igual com o objeto da pessoa acabado de criar, deixando a pessoa associada ao evento
     * de ser desconhecida na interface (null no objeto pessoa)
     *
     * @param person Pessoa que acabou de ser adicionada ao sistema a associar aos eventos em que o seu ID está presente
     */
    private void addPersonToEvents(Person person) {
        for (Event event : events) {
            if (event.getPersonId().equals(person.getId())) {
                event.setPerson(person);
            }
        }
    }

    /**
     * Esta função é utilizada no momento da remoção de uma pessoa do sistema. Remove (define como null) o objeto de
     * pessoa do evento caso o ID da pessoa seja igual com o objeto da pessoa acabado de remover, sendo que o evento
     * será associado a um "Desconhecido", devido ao objeto pessoa de cada evento cujo ID de pessoa é igual ao ID de
     * pessoa do objeto Pessoa removido se tornar nulo.
     *
     * @param person Pessoa que acabou de ser removida do sistema a desassociar aos eventos em que o seu ID está presente
     */
    private void removePersonFromEvents(Person person) {
        for (Event event : events) {
            if (event.getPersonId().equals(person.getId())) {
                event.setPerson(null);
            }
        }
    }
}
