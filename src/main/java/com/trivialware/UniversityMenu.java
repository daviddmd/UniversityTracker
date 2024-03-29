package com.trivialware;

import com.trivialware.helpers.ConsoleColors;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Classe que possibilita a interação por terminal com uma instância do Objeto Universidade com um método público
 * para o menu inicial.
 */
public class UniversityMenu {
    private final University university;
    private final String peopleFileName;
    private final Scanner scanner;

    /**
     * Construtor para a classe de Menu da Universidade, que recebe um Objeto Universidade, e o caminho do ficheiro
     * das pessoas, caso seja necessário realizar operações de importação/exportação de pessoas.
     *
     * @param university     Objeto que representa a universidade
     * @param peopleFileName Caminho do ficheiro das pessoas
     */
    public UniversityMenu(University university, String peopleFileName) {
        this.university = university;
        this.peopleFileName = peopleFileName;
        scanner = new Scanner(System.in, StandardCharsets.UTF_8);
    }

    private String getPersonIdMenu() {
        System.out.println("Introduza o Identificador da Pessoa ou deixe em branco para cancelar a operação:");
        System.out.print("ID: ");
        String personId = scanner.nextLine();
        if (personId.isBlank()) {
            return null;
        }
        return personId;
    }

    private LocalTime getTimeMenu() {
        LocalTime time = null;
        String timeString;
        do {
            try {
                System.out.print("Hora: ");
                timeString = scanner.nextLine();
                time = LocalTime.parse(timeString);
            }
            catch (DateTimeParseException | NullPointerException e) {
                System.out.println("Hora Inserida Inválida.");
            }
        } while (time == null);
        return time;
    }

    private void listPeople() {
        for (Person person : university.getPeople()) {
            System.out.printf("| ID: %s | Nome: %s | Papel: %s |%n", person.getId(), person.getName(), person.getRole());
        }
    }

    private void addPersonMenu() {
        int roleChoice;
        String id, name;
        System.out.print("Nome: ");
        name = scanner.nextLine();
        System.out.print("Identificador Único: ");
        id = scanner.nextLine();
        System.out.println("Escolha o papel da Pessoa a Adicionar:");
        do {
            for (Person.Role role : Person.Role.values()) {
                System.out.printf("%d - %s%n", role.ordinal(), role);
            }
            try {
                System.out.print("Opção: ");
                roleChoice = Integer.parseInt(scanner.nextLine());
            }
            catch (NumberFormatException e) {
                roleChoice = -1;
            }
        } while (roleChoice < 0 || roleChoice >= Person.Role.values().length);
        if (!(name.isBlank() || id.isBlank())) {
            Person person = new Person(id, Person.Role.values()[roleChoice], name);
            if (!university.addPerson(person)) {
                System.out.println("Já existe uma pessoa no sistema com o mesmo identificador.");
            }
            else {
                System.out.printf("A pessoa com Identificador %s foi adicionada com sucesso ao sistema.%n", id);
            }
        }
    }

    private void removePersonMenu() {
        String personId = getPersonIdMenu();
        if (personId != null) {
            Person person = university.getPersonById(personId);
            if (person != null) {
                if (university.removePerson(person)) {
                    System.out.printf("A pessoa com identificador único %s foi removida do sistema.%n", person.getId());
                }
                else {
                    System.out.println("Erro ao remover a pessoa do sistema.");
                }
            }
            else {
                System.out.println("Não existe uma pessoa com este identificador no sistema.");
            }
        }

    }

    private void peopleMenu() {
        int menuOption;
        do {
            System.out.println("0-Voltar para o Menu Principal");
            System.out.println("1-Importar Pessoas de Ficheiro");
            System.out.println("2-Exportar Pessoas para Ficheiro");
            System.out.println("3-Listar Pessoas no Sistema");
            System.out.println("4-Adicionar Pessoa");
            System.out.println("5-Remover Pessoa");
            try {
                System.out.print("Escolha: ");
                menuOption = Integer.parseInt(scanner.nextLine());
                switch (menuOption) {
                    case 1 -> {
                        university.setPeople(FileHelper.importPeople(peopleFileName));
                        university.updateEventsPeople();
                    }
                    case 2 -> FileHelper.exportPeople(university.getPeople(), peopleFileName);
                    case 3 -> listPeople();
                    case 4 -> addPersonMenu();
                    case 5 -> removePersonMenu();
                }
            }
            catch (NumberFormatException e) {
                menuOption = -1;
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        } while (menuOption != 0);
    }


    private void personCurrentLocationMenu() {
        String personId = getPersonIdMenu();
        if (personId != null) {
            Location location = university.getCurrentLocationOfPerson(personId);
            if (location != null) {
                System.out.printf("Localização Atual: %s%n", location);
            }
            else {
                System.out.println("A pessoa com o identificador inserido não tem movimentos registados.");
            }
        }

    }

    private void personFirstHistoricalLocationMenu() {
        String personId = getPersonIdMenu();
        if (personId != null) {
            System.out.println("Introduza a Hora de Início (HH:MM:SS)");
            LocalTime startTime = getTimeMenu();
            System.out.println("Introduza a Hora de Fim (HH:MM:SS)");
            LocalTime endTime = getTimeMenu();
            if (endTime.compareTo(startTime) > 0) {
                Location location = university.getFirstLocationOfPersonInTimeFrame(personId, startTime, endTime);
                if (location != null) {
                    System.out.printf("Localização Atual: %s%n", location);
                }
                else {
                    System.out.println("A pessoa com o identificador inserido não tem movimentos registados.");
                }
            }
            else {
                System.out.println("A hora de início não pode ser superior à hora de fim");
            }
        }
    }

    private void personHistoricalMovementsMenu() {
        String personId = getPersonIdMenu();
        if (personId != null) {
            System.out.println("Introduza a Hora de Início (HH:MM:SS)");
            LocalTime startTime = getTimeMenu();
            System.out.println("Introduza a Hora de Fim (HH:MM:SS)");
            LocalTime endTime = getTimeMenu();
            if (endTime.compareTo(startTime) > 0) {
                ListADT<Event> events = university.getEventsOfPersonInTimeFrame(personId, startTime, endTime);
                if (events.size() == 0) {
                    System.out.println("A pessoa com o identificador inserido não tem movimentos registados.");
                }
                else {
                    for (Event event : events) {
                        System.out.printf("| Hora: %s | Localização: %s |%n",
                                event.getStartTime(), event.getLocation());
                    }
                }
            }
            else {
                System.out.println("A hora de início não pode ser superior à hora de fim.");
            }
        }
    }

    private void personAllMovementsMenu() {
        String personId = getPersonIdMenu();
        if (personId != null) {
            ListADT<Event> events = university.getEventsOfPerson(personId);
            if (events.isEmpty()) {
                System.out.println("A pessoa com este identificador não tem movimentos registados");
            }
            else {
                for (Event event : events) {
                    System.out.printf("| Hora: %s | Localização: %s |%n",
                            event.getStartTime(), event.getLocation());
                }
            }
        }
    }

    private void currentLocationAllPeopleMenu() {
        Location location;
        for (Person person : university.getPeople()) {
            location = university.getCurrentLocationOfPerson(person.getId());
            if (location != null) {
                System.out.printf("Localização Atual %s: %s%n", person, location);
            }
        }
    }

    private void peopleLocationMenu() {
        if (university.getPeople().isEmpty()) {
            System.out.println("Não existem pessoas no sistema, por favor, registe ou importe pessoas.");
            return;
        }
        int menuOption;
        do {
            System.out.println("0-Voltar para o Menu Anterior");
            System.out.println("1-Ver a Localização Atual de uma Pessoa");
            System.out.println("2-Ver a Localização Atual de Todas as Pessoas");
            System.out.println("3-Ver a Primeira Localização de uma Pessoa num Intervalo de Tempo");
            System.out.println("4-Ver os Movimentos de uma Pessoa num Intervalo de Tempo");
            System.out.println("5-Ver todos os Movimentos de uma Pessoa");
            System.out.println("6-Listar todas as Pessoas no Sistema");
            try {
                System.out.print("Escolha: ");
                menuOption = Integer.parseInt(scanner.nextLine());
                switch (menuOption) {
                    case 1 -> personCurrentLocationMenu();
                    case 2 -> currentLocationAllPeopleMenu();
                    case 3 -> personFirstHistoricalLocationMenu();
                    case 4 -> personHistoricalMovementsMenu();
                    case 5 -> personAllMovementsMenu();
                    case 6 -> listPeople();
                }
            }
            catch (NumberFormatException e) {
                menuOption = -1;
            }
        } while (menuOption != 0);
    }

    private void messageMenu() {
        for (Event event : university.getAccessViolations()) {
            //Pessoa Desconhecida
            if (event.getPerson() == null) {
                System.out.println(ConsoleColors.RED + "[Alerta Pessoa Desconhecida]:" + ConsoleColors.RESET +
                        " | ID Pessoa: " + event.getPersonId() + " | Hora: " +
                        event.getStartTime() + " | Localização: " + event.getLocation() + " |");
            }
            //Violação Controlo Acesso de Role
            else {
                System.out.println(ConsoleColors.YELLOW + "[Aviso Entrada Sem Permissão]" + ConsoleColors.RESET +
                        " | ID Pessoa: " + event.getPersonId() +
                        " | Nome Pessoa: " + event.getPerson().getName() +
                        " | Papel Pessoa: " + event.getPerson().getRole() +
                        " | Hora: " + event.getStartTime() +
                        " | Localização: " + event.getLocation() +
                        " | Papel Acesso Localização: " + event.getLocation().getRestrictedTo() +
                        " |"
                );
            }
        }
        int currentNumberPeople, maximumCapacity;
        for (Location location : university.getLocations()) {
            currentNumberPeople = location.getCurrentNumberPeople();
            maximumCapacity = location.getMaximumCapacity();
            //Se está entre capacidade máxima -1 e capacidade máxima
            if (currentNumberPeople >= maximumCapacity - 2 && currentNumberPeople <= maximumCapacity) {
                System.out.println(ConsoleColors.YELLOW + "[Aviso Capacidade Divisão Próxima de Ser Ultrapassada]:" + ConsoleColors.RESET +
                        " | Divisão: " + location +
                        " | Capacidade Máxima: " + maximumCapacity +
                        " | Ocupação Atual: " + currentNumberPeople + " |"
                );
            }
            else if (currentNumberPeople > maximumCapacity) {
                System.out.println(ConsoleColors.RED + "[Alerta Capacidade Divisão Ultrapassada]:" + ConsoleColors.RESET +
                        " | Divisão: " + location +
                        " | Capacidade Máxima: " + maximumCapacity +
                        " | Ocupação Atual: " + currentNumberPeople + " |"
                );
            }
        }
    }

    private void contactsPersonTimeRange() {
        String personId = getPersonIdMenu();
        if (personId == null) {
            return;
        }
        System.out.println("Introduza a Hora de Início (HH:MM:SS)");
        LocalTime startTime = getTimeMenu();
        System.out.println("Introduza a Hora de Fim (HH:MM:SS)");
        LocalTime endTime = getTimeMenu();
        ListADT<Event> events = university.getOverlappingEventsOfPersonInTimeFrame(personId, startTime, endTime);
        printPersonContacts(events, personId);
    }

    private void contactsPersonAll() {
        String personId = getPersonIdMenu();
        if (personId == null) {
            return;
        }
        ListADT<Event> events = university.getOverlappingEventsOfPersonInTimeFrame(personId, LocalTime.MIN, LocalTime.MAX);
        printPersonContacts(events, personId);
    }

    private void contactsPersonLastHours() {
        String personId = getPersonIdMenu();
        if (personId == null) {
            return;
        }
        Event event = university.getCurrentEventByPerson(personId);
        if (event == null) {
            System.out.println("A pessoa com o ID introduzido não tem qualquer movimento registado");
            return;
        }
        System.out.println(ConsoleColors.YELLOW + "Último desta pessoa foi registado às " +
                event.getStartTime() + " na localização " + event.getLocation() + ConsoleColors.RESET);
        int lastEventsHours = event.getStartTime().getHour();
        int numberHours;
        System.out.println("Defina o número de horas desde o último movimento que pretende visualizar os movimentos:");
        do {
            try {
                System.out.print("Horas: ");
                numberHours = Integer.parseInt(scanner.nextLine());
                if (lastEventsHours - numberHours < 0) {
                    System.out.println("Número de Horas inseridas inválidas.");
                }
            }
            catch (NumberFormatException e) {
                numberHours = -1;
            }
        } while (numberHours < 0 || lastEventsHours - numberHours < 0);
        ListADT<Event> events = university.getOverlappingEventsOfPersonInTimeFrame(personId,
                LocalTime.of(lastEventsHours - numberHours, event.getStartTime().getMinute(), event.getStartTime().getSecond()),
                event.getStartTime());
        printPersonContacts(events, personId);
    }

    private void printPersonContacts(ListADT<Event> events, String personId) {
        UnorderedListADT<String> uniquePeople = new ArrayList<>(events.size());
        if (events.isEmpty()) {
            System.out.printf("A pessoa com identificador %s não teve contactos neste intervalo temporal.%n", personId);
            return;
        }
        for (Event event : events) {
            if (!uniquePeople.contains(event.getPersonId())) {
                uniquePeople.addLast(event.getPersonId());
            }
            if (event.getPerson() == null) {
                System.out.printf("| Hora: %s | Localização: %s | Identificador Pessoa Desconhecida: %s |%n",
                        event.getStartTime(), event.getLocation(), event.getPersonId());
            }
            else {
                System.out.printf("| Hora: %s | Localização: %s | Pessoa: %s |%n",
                        event.getStartTime(), event.getLocation(), event.getPerson());
            }
        }
        Person person;
        String separator = "";
        StringBuilder sb = new StringBuilder();
        for (String uniquePersonId : uniquePeople) {
            sb.append(separator);
            person = university.getPersonById(uniquePersonId);
            if (person != null) {
                sb.append(person);
            }
            else {
                sb.append(String.format("Desconhecido (%s)", uniquePersonId));
            }
            separator = ",";
        }
        person = university.getPersonById(personId);
        if (person == null) {
            System.out.printf("A pessoa com identificador %s teve contacto com as pessoas: %s%n", personId, sb);
        }
        else {
            System.out.printf("%s teve contacto com as pessoas: %s%n", person, sb);
        }
    }

    private void contactsMenu() {
        int menuOption;
        do {
            System.out.println("0-Voltar para o Menu Anterior");
            System.out.println("1-Apresentar Contactos de uma pessoa num Intervalo Temporal");
            System.out.println("2-Apresentar Todos os Contactos de uma Pessoa durante o Dia");
            System.out.println("3-Apresentar Contactos de uma pessoa nas últimas x Horas");
            try {
                System.out.print("Escolha: ");
                menuOption = Integer.parseInt(scanner.nextLine());
                switch (menuOption) {
                    case 1 -> contactsPersonTimeRange();
                    case 2 -> contactsPersonAll();
                    case 3 -> contactsPersonLastHours();

                }
            }
            catch (NumberFormatException e) {
                menuOption = -1;
            }
        } while (menuOption != 0);
    }

    private void emergencyForPerson(String personId) {
        StackADT<Location> path;
        double cost;
        UndirectedNetworkADT<Location> network = university.getNetwork();
        StringBuilder sb;
        String separator;
        path = new LinkedStack<>();
        separator = "";
        sb = new StringBuilder();
        cost = network.getCheapestPath(university.getCurrentLocationOfPerson(personId), university.getLocationById(University.EMERGENCY_SPOT_ID), path);
        if (cost > 0 && cost != Double.MAX_VALUE) {
            sb.append(ConsoleColors.GREEN).append(String.format("Percurso de Emergência para Pessoa com Identificador %s: ", personId)).append(ConsoleColors.RESET);
            while (!path.empty()) {
                sb.append(ConsoleColors.BLUE).append(separator).append(ConsoleColors.RESET);
                sb.append(path.pop());
                separator = "→";
            }
            sb.append(". ");
            sb.append(ConsoleColors.GREEN).append("Distância: ").append(ConsoleColors.RESET).append(cost).append(" metros.");
            System.out.println(sb);
        }
        else {
            System.out.printf(ConsoleColors.RED + "Não existe percurso de emergência para pessoa com identificador %s%n" + ConsoleColors.RESET, personId);
        }
    }

    private void emergencyForAllPeople() {
        for (Person person : university.getPeople()) {
            emergencyForPerson(person.getId());

        }
    }

    private void emergencyForOnePersonMenu() {
        String personId = getPersonIdMenu();
        if (personId != null) {
            emergencyForPerson(personId);
        }
    }

    private void emergencyMenu() {
        int menuOption;
        do {
            System.out.println("0-Voltar para o Menu Anterior");
            System.out.println("1-Simular Emergência para Todas as Pessoas no Sistema");
            System.out.println("2-Simular Emergência para uma Pessoa com o seu Identificador");
            try {
                System.out.print("Escolha: ");
                menuOption = Integer.parseInt(scanner.nextLine());
                switch (menuOption) {
                    case 1 -> emergencyForAllPeople();
                    case 2 -> emergencyForOnePersonMenu();
                }
            }
            catch (NumberFormatException e) {
                menuOption = -1;
            }
        } while (menuOption != 0);
    }

    private void printMap() {
        UndirectedNetworkADT<Location> network = university.getNetwork();
        StringBuilder sb;
        String separator;
        for (Location location : university.getLocations()) {
            sb = new StringBuilder();
            separator = "";
            for (Location neighbour : network.getNeighbours(location)) {
                sb.append(separator);
                sb.append(neighbour);
                separator = ",";
            }
            System.out.println("Localização: " + ConsoleColors.GREEN + location + ConsoleColors.RESET +
                    " Ligações: " + ConsoleColors.BLUE + sb + ConsoleColors.RESET);
        }

    }

    /**
     * Menu principal da aplicação. Permite aceder aos sub-menus de:
     * <ul>
     *     <li>Gerir Pessoas:
     *     <ul>
     *         <li>Importar Pessoas de um ficheiro para o sistema corrente</li>
     *         <li>Exportar Pessoas do sistema corrente para o ficheiro definido</li>
     *         <li>Listar as Pessoas no Sistema</li>
     *         <li>Adicionar uma Pessoa ao Sistema, com o seu respetivo identificador único, nome e papel</li>
     *         <li>Remover uma Pessoa Existente no sistema a partir do seu identificador único</li>
     *     </ul>
     *     A localização dos ficheiros de mapa, pessoas e movimentos é definida em
     *     <pre>{@code resources/config.properties}</pre>
     *     </li>
     *     <li>Obter a Localização de Pessoas:
     *     <ul>
     *         <li>Ver a Localização Atual de uma Pessoa (associada ao seu último movimento registado)</li>
     *         <li>Ver a primeira Localização de uma Pessoa num Intervalo de Tempo</li>
     *         <li>Ver todos os movimentos (localização e hora) de uma Pessoa num Intervalo de Tempo</li>
     *         <li>Ver todos os movimentos registados de uma Pessoa</li>
     *     </ul>
     *     Em todos estes menus as pessoas são identificadas pelo seu identificador único, possibilitando obter
     *     informações sobre pessoas com movimentos registados, mas cuja identidade ainda não é conhecida pelo sistema.
     *     </li>
     *     <li>Ver Mensagens como Avisos ou Alertas, sendo que o tipo de mensagens poderão ser alertas de pessoas não
     *     identificadas, avisos em relação a acessos de pessoas em localizações que as mesmas não têm permissão para
     *     aceder devido ao seu papel relativo ao papel necessário para aceder a essa mesma localização ou
     *     avisos/alertas pertinentes à capacidade máxima em relação à ocupação atual de cada localização, se a mesma
     *     estiver perto ou tiver sido ultrapassada.</li>
     *     <li>Consultar Contactos Efectuados por Pessoa:
     *     <ul>
     *         <li>Apresentar Contactos realizados por uma pessoa num intervalo temporal</li>
     *         <li>Apresentar todos os Contactos realizados por uma pessoa no dia</li>
     *         <li>Apresentar todos os contactos realizados por uma pessoa, dede o último movimento registado
     *          até um número definido de horas no passado</li>
     *     </ul>
     *     Todos os contactos apresentados irão apresentar a lista de eventos pertinente (Quem realizou os contactos, e
     *     quando/onde foi registado o movimento que gerou o contacto em questão), e a lista única de pessoas com que a
     *     pessoa em questão entrou em contacto no período definido.
     *     </li>
     *     <li>
     *         Simular Emergência:
     *         <ul>
     *             <li>Simular Emergência para Todas as Pessoas no Sistema, construindo o caminho mais próximo/menos
     *             custo em termos de distância de todas as pessoas registadas no sistema até ao ponto de emergência,
     *             ignorando as restrições de acesso associadas a cada localização.</li>
     *             <li>Simular Emergência para uma Pessoa a partir do seu identificador único, permitindo simular
     *             emergências para pessoas que ainda não estejam registadas, porém tenham eventos associados às
     *             mesmas, obtendo a localização do evento mais recente, e construindo o caminho mais próximo/com
     *             menor custo em termos de distância até ao pontode emergência, ignorando restrições de acesso.</li>
     *         </ul>
     *         Em cada um destes, será apresentado o caminho, representado pela ordem das localizações que cada pessoa
     *         deve percorrer para chegar à saída de emergência, juntamente com a distância total desse mesmo caminho.
     *         Se não for possível uma pessoa chegar a uma emergência (provavelmente devido à localização onde se situa
     *         não se ligar a mais nenhuma localização), o sistema irá reportar que não existe um caminho para o ponto
     *         de emergência para essa pessoa.
     *     </li>
     *     <li>Ver Mapa da Universidade, onde o utilizador poderá ver cada localização da universidade, juntamente
     *     com as ligações que esse ponto realiza com outras localizações.</li>
     * </ul>
     */
    public void mainMenu() {
        int menuOption;
        do {
            System.out.println("0-Sair");
            System.out.println("1-Gerir Pessoas");
            System.out.println("2-Obter Localização Pessoa");
            System.out.println("3-Ver Mensagens (Avisos/Alertas)");
            System.out.println("4-Consultar Contactos Efectuados por Pessoa");
            System.out.println("5-Simular Emergência");
            System.out.println("6-Ver Mapa Universidade");
            try {
                System.out.print("Escolha: ");
                /*
                Uso assim em vez de nextInt para evitar fazer chamadas desnecessárias a nextLine para consumir a
                newline que o nextInt não consome
                 */
                menuOption = Integer.parseInt(scanner.nextLine());
                switch (menuOption) {
                    case 1 -> peopleMenu();
                    case 2 -> peopleLocationMenu();
                    case 3 -> messageMenu();
                    case 4 -> contactsMenu();
                    case 5 -> emergencyMenu();
                    case 6 -> printMap();
                }
            }
            catch (NumberFormatException e) {
                menuOption = -1;
            }
        } while (menuOption != 0);
    }

}
