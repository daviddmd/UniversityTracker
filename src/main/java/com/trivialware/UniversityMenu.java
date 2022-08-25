package com.trivialware;

import com.trivialware.helpers.ConsoleColors;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class UniversityMenu {
    private final University university;
    private final String peopleFileName;
    private final Scanner scanner = new Scanner(System.in);

    public UniversityMenu(University university, String peopleFileName) {
        this.university = university;
        this.peopleFileName = peopleFileName;
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

        do {
            for (Person.Role role : Person.Role.values()) {
                System.out.printf("%d - %s%n", role.ordinal(), role);
            }
            try {
                System.out.print("Escolha: ");
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

    private String getPersonIdMenu() {
        System.out.println("Introduza o Identificador da Pessoa ou deixe em branco para cancelar a operação:");
        System.out.print("ID: ");
        String personId = scanner.nextLine();
        if (personId.isBlank()) {
            return null;
        }
        return personId;
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
                    case 1 -> university.setPeople(FileHelper.importPeople(peopleFileName));
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

    private void contactsMenu() {
        String personId = getPersonIdMenu();
        if (personId == null) {
            return;
        }
        System.out.println("Introduza a Hora de Início (HH:MM:SS)");
        LocalTime startTime = getTimeMenu();
        System.out.println("Introduza a Hora de Fim (HH:MM:SS)");
        LocalTime endTime = getTimeMenu();
        ListADT<Event> events = university.getOverlappingEventsOfPersonInTimeFrame(personId, startTime, endTime);
        UnorderedListADT<String> uniquePeople = new ArrayList<>(events.size());
        if (events.isEmpty()) {
            System.out.printf("A pessoa com identificador %s não teve contactos neste intervalo temporal.%n", personId);
        }
        else {
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
            System.out.printf("A pessoa com identificador %s teve contacto com as pessoas %s%n", personId, sb);
        }
        else {
            System.out.printf("%s teve contacto com as pessoas %s%n", person, sb);
        }
    }

    private void emergencyForAllPeople() {
        StackADT<Location> path;
        double cost;
        UndirectedNetworkADT<Location> network = university.getNetwork();
        StringBuilder sb;
        String separator;
        for (Person person : university.getPeople()) {
            path = new LinkedStack<>();
            separator = "";
            sb = new StringBuilder();
            cost = network.getCheapestPath(university.getCurrentLocationOfPerson(person.getId()), university.getLocationById(University.EMERGENCY_SPOT_ID), path);
            if (cost > 0) {
                sb.append(ConsoleColors.GREEN).append(String.format("Percurso de Emergência para %s: ", person)).append(ConsoleColors.RESET);
                while (!path.empty()) {
                    sb.append(ConsoleColors.BLUE).append(separator).append(ConsoleColors.RESET);
                    sb.append(path.pop());
                    separator = "→";
                }
                sb.append(". ");
                sb.append(ConsoleColors.GREEN).append("Distância: ").append(ConsoleColors.RESET).append(cost).append(" metros.");
                System.out.println(sb);
            }

        }
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
        if (cost > 0) {
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
            System.out.printf("Não existe percurso de emergência para pessoa com identificador %s%n", personId);
        }
    }

    private void emergencyMenu() {
        int menuOption;
        String personId;
        do {
            System.out.println("0-Voltar para o Menu Anterior");
            System.out.println("1-Simular Emergência para Todas as Pessoas no Sistema");
            System.out.println("2-Simular Emergência para uma Pessoa com o seu Identificador");
            try {
                System.out.print("Escolha: ");
                menuOption = Integer.parseInt(scanner.nextLine());
                switch (menuOption) {
                    case 1 -> emergencyForAllPeople();
                    case 2 -> {
                        personId = getPersonIdMenu();
                        emergencyForPerson(personId);
                    }
                }
            }
            catch (NumberFormatException e) {
                menuOption = -1;
            }
        } while (menuOption != 0);
    }

    public void mainMenu() {
        int menuOption;
        do {
            System.out.println("0-Sair");
            System.out.println("1-Gerir Pessoas");
            System.out.println("2-Obter Localização Pessoa");
            System.out.println("3-Ver Mensagens (Avisos/Alertas)");
            System.out.println("4-Consultar Contactos Efectuados por Pessoa");
            System.out.println("5-Simular Emergência");
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
                }
            }
            catch (NumberFormatException e) {
                menuOption = -1;
            }
        } while (menuOption != 0);
    }

}
