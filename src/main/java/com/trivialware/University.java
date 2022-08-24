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
    public static final String EMERGENCY_SPOT_ID = "EMERGENCY_SPOT";
    private final UnorderedListADT<Location> locations;
    //Pode vir a ser uma AVL Tree
    private final UnorderedListADT<Event> events;
    private UnorderedListADT<Person> people;

    private final UndirectedNetworkADT<Location> network;

    /**
     * Construtor para a classe Universidade. A classe universidade contém diversos métodos e funções para auxiliar
     * as tarefas inerentes ás mesmas, e é construída através de uma lista de localizações, eventos (não necessariamente
     * temporalmente organizados), pessoas e a rede (grafo pesado não dirigido) correspondente às relações entre as
     * localizações. Apenas a lista das pessoas pode estar vazia, sendo que é esperado que a lista de localizações e
     * eventos não esteja para um funcionamento lógico do programa.
     * <p>
     * No instanciamento desta classe, a lista interna dos eventos não será diretamente associada à lista de eventos
     * recebida, sendo que a mesma será organizada temporalmente, e cada evento será adicionado à lista instanciada no
     * construtor com o número exato de eventos recebidos para otimização espacial. Os eventos são organizados de modo
     * a no momento da adição permitir definir a data de fim do anterior como a data de início do próximo (o evento
     * a inserir), construindo uma linha cronológica de eventos associado a uma pessoa, que irá ser útil nas operações
     * de rastreamento de pessoas.
     * <p>
     * Cada evento pode estar associado a uma pessoa que não foi importada, e a pessoa associada a esse evento irá
     * aparecer como desconhecida, porém continuará a ser possível rastrear a mesma a partir do seu Identificador único.
     * No momento da adição dessa pessoa ao sistema (se o identificador único for igual), todos os eventos serão associados
     * a esta nova pessoa, deixando o seu modo de apresentação de ser "Desconhecido", estando associado ao nome da mesma.
     * No momento da remoção da pessoa, o inverso irá acontecer, em que todos os eventos previamente associados a uma
     * pessoa irão agora estar associados a uma pessoa desconhecida, sendo que o identificador da pessoa responsável
     * por esse evento/movimento é preservado, continuando a ser possível o rastreamento da mesma.
     *
     * @param locations Lista de Localizações
     * @param events    Lista de Eventos
     * @param people    Lista de Pessoas
     * @param network   Rede (Grafo Pesado não dirigido) associado às relações das localizações da universidade
     */
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
     * Atribui a cada localização o número de pessoas <strong>presentemente</strong>presentemente nela.
     * Define-se como presentemente aquelas pessoas em cuja última atividade registada pelas mesmas
     * estavam na localização em questão
     */
    public void setNumberOfPeopleCurrentlyInLocations() {
        for (Location location : getLocations()) {
            location.setCurrentNumberPeople(0);
        }
        Location location;
        for (Event event : getEvents()) {
            if (event.getEndTime() == LocalTime.MAX) {
                location = event.getLocation();
                location.setCurrentNumberPeople(location.getCurrentNumberPeople() + 1);
            }
        }
    }

    /**
     * Atualiza o número de pessoas em cada localização num dado intervalo temporal
     *
     * @param start Hora de início
     * @param end   Hora de fim
     */
    public void setNumberOfPeopleInLocationsInTimeFrame(LocalTime start, LocalTime end) {
        for (Location location : getLocations()) {
            location.setCurrentNumberPeople(0);
        }
        Location location;
        for (Event event : getEvents()) {
            if (start.compareTo(event.getEndTime()) <= 0 && end.compareTo(event.getStartTime()) >= 0) {
                location = event.getLocation();
                location.setCurrentNumberPeople(location.getCurrentNumberPeople() + 1);
            }
        }
    }

    /**
     * Obtém os eventos temporalmente sobrepostos aos eventos numa lista de eventos.
     * Um evento está sobreposto a outro, se o mesmo não for o próprio, se a localização do mesmo for igual à do próprio
     * e se o seu início e fim se se sobrepuserem com o início e fim do próprio.
     *
     * @param personEvents Lista de eventos a encontrar sobreposições
     * @return Lista de eventos sobrepostos aos eventos passados na lista de eventos por argumento
     */
    public ListADT<Event> getOverlappingEvents(ListADT<Event> personEvents) {
        UnorderedListADT<Event> eventList = new DoublyLinkedList<>();
        //O(n*m)
        for (Event event : getEvents()) {
            for (Event personEvent : personEvents) {
                if (personEvent.overlaps(event)) {
                    eventList.addLast(event);
                }
            }
        }
        return eventList;
    }

    /**
     * Obtém os eventos (movimentos) de uma dada pessoa num intervalo temporal.
     * Permite indiretamente obter a localização de uma pessoa num dado intervalo temporal através de obter o primeiro
     * evento nesse dado intervalo.
     *
     * @param personId Identificador único de uma certa pessoa
     * @param start    Hora de início do intervalo temporal (inclusive)
     * @param end      Hora de fim do intervalo temporal (inclusive)
     * @return Lista de movimentos de uma pessoa num intervalo temporal, contendo a hora de início e fim (se aplicável)
     * associada a uma localização e pessoa correspondente (se existente no sistema), assim como o identificador da mesma
     */
    public ListADT<Event> getEventsOfPersonInTimeFrame(String personId, LocalTime start, LocalTime end) {
        UnorderedListADT<Event> eventList = new DoublyLinkedList<>();
        for (Event event : getEvents()) {
            if (event.getPersonId().equals(personId) && (start.compareTo(event.getEndTime()) <= 0 && end.compareTo(event.getStartTime()) >= 0)) {
                eventList.addLast(event);
            }
        }
        return eventList;
    }

    /**
     * Obtém os contactos (eventos) de uma pessoa num dado intervalo temporal. Faz uso da função
     * {@link #getEventsOfPersonInTimeFrame(String, LocalTime, LocalTime) getEventsOfPersonInTimeFrame} para obter
     * todos os eventos de uma pessoa num dado intervalo temporal em conjunção com a função
     * {@link #getOverlappingEvents(ListADT) getOverlappingEvents}
     * para obter todos os eventos (contactos) que estão sobrepostos a estes mesmos eventos, determinando os contactos
     * que a pessoa realizou nos vários movimentos efetuados em várias localizações da universidade.
     *
     * @param personId Identificador único da pessoa no sistema
     * @param start Hora de início do intervalo temporal (inclusive)
     * @param end Hora de fim do intervalo temporal (inclusive)
     * @return Lista com Eventos (Contactos) efetuados pela pessoa num dado intervalo temporal
     */
    public ListADT<Event> getOverlappingEventsOfPersonInTimeFrame(String personId, LocalTime start, LocalTime end) {
        ListADT<Event> eventsOfPersonInTimeFrame = getEventsOfPersonInTimeFrame(personId, start, end);
        return getOverlappingEvents(eventsOfPersonInTimeFrame);
    }

    /**
     * Obtém a primeira localização da pessoa num dado intervalo temporal, determinada pelo primeiro movimento/atividade
     * que uma pessoa registou num dado intervalo temporal. Faz uso da função
     * {@link #getEventsOfPersonInTimeFrame(String, LocalTime, LocalTime) getEventsOfPersonInTimeFrame} e retorna
     * o primeiro evento caso existam eventos nesse intervalo temporal, caso contrário não retorna nada.
     * @param personId Identificador único da pessoa no sistema
     * @param start Hora de início do intervalo temporal (inclusive)
     * @param end Hora de fim do intervalo temporal (inclusive)
     * @return Primeira localização cronológica da pessoa num dado intervalo temporal, se existir
     */
    public Location getLocationOfPersonInTimeFrame(String personId, LocalTime start, LocalTime end) {
        ListADT<Event> eventsOfPersonInTimeFrame = getEventsOfPersonInTimeFrame(personId, start, end);
        if (eventsOfPersonInTimeFrame.size() == 0) {
            return null;
        }
        return eventsOfPersonInTimeFrame.getFirst().getLocation();
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

    /**
     * Remove uma pessoa do sistema
     *
     * @param person Objeto que representa a pessoa a remover do sistema. É desassociada de todos os eventos a que está
     *               associada se for removida com sucesso (o ID da pessoa continuará a estar associado ao evento,
     *               mas a mesma irá aparecer como desconhecida).
     * @return true se a pessoa existia e foi removida do sistema, false em caso contrário
     */
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

    /**
     * Define a lista das pessoas do sistema
     *
     * @param people Lista das pessoas a substituir a lista presente
     */
    public void setPeople(UnorderedListADT<Person> people) {
        this.people = people;
    }

    /**
     * Obtém a rede (grafo pesado não direcionado) das localizações do sistema
     *
     * @return Rede associado às relações das localizações do sistema
     */
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
            //FIXME avaliar se é necessário o decremento
            currentEventByPerson.setEndTime(event.getStartTime().minusSeconds(1));
            events.addLast(event);
        }
    }

    /**
     * Esta função é utilizada quando se aciona a função de importação de pessoas para o sistema. Irá atualizar
     * todas as pessoas de todos os eventos consoante os novos valores, sejam os valores atuais das pessoas nos
     * eventos diferentes de null ou não.
     */
    public void updateEventsPeople() {
        for (Event event : getEvents()) {
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
        for (Event event : getEvents()) {
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
        for (Event event : getEvents()) {
            if (event.getPersonId().equals(person.getId())) {
                event.setPerson(null);
            }
        }
    }

    /**
     * Obtém a lista de violações de acesso registadas pelos movimentos do sistema.
     * As violações podem ser do tipo de utilizador desconhecido ou do tipo de papel de utilizador insuficiente para
     * acesso a localização. O modo como os mesmos se diferenciam é que eventos do primeiro tipo de violação terão
     * o utilizador como um elemento nulo.
     *
     * @return Lista de Eventos com violações de acesso
     */
    public ListADT<Event> getAccessViolations() {
        /*
        Irá haver dois tipos de notificações, pessoas não existentes e falta de autorização. Detetar no runtime se
        a pessoa do evento é nula, caso contrário é uma violação do tipo papel
         */
        UnorderedListADT<Event> violations = new DoublyLinkedList<>();
        for (Event event : getEvents()) {
            if (event.getPerson() == null) {
                violations.addLast(event);
            }
            else {
                if (event.getLocation().getRestrictedTo() != null) {
                    switch (event.getLocation().getRestrictedTo()) {
                        case TEACHER -> {
                            if (event.getPerson().getRole() == Person.Role.WORKER ||
                                    event.getPerson().getRole() == Person.Role.STUDENT) {
                                violations.addLast(event);
                            }
                        }
                        case WORKER -> {
                            if (event.getPerson().getRole() == Person.Role.TEACHER ||
                                    event.getPerson().getRole() == Person.Role.STUDENT) {
                                violations.addLast(event);
                            }
                        }
                    }
                }
            }
        }
        return violations;
    }
}
