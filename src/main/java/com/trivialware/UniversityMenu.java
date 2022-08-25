package com.trivialware;

import java.io.IOException;
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
            System.out.printf("|ID: %s | Nome: %s | Papel: %s|%n", person.getId(), person.getName(), person.getRole());
        }
    }

    private void addPersonMenu() {

    }

    private void removePersonMenu() {

    }

    private Person getPersonByIdMenu() {
        System.out.println("Introduza o Identificador da Pessoa ou deixe em branco para cancelar a operação:");
        System.out.print("ID: ");
        String id = scanner.nextLine();
        if (id.isBlank()) {
            return null;
        }
        Person person = university.getPersonById(id);
        if (person == null) {
            System.out.println("Não existe uma pessoa no sistema com o identificador inserido.");
        }
        return person;
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

    private void peopleLocationMenu() {

    }

    private void messageMenu() {

    }

    private void contactsMenu() {

    }

    private void emergencyMenu() {

    }

    public void mainMenu() {
        int menuOption;
        do {
            System.out.println("0-Sair");
            System.out.println("1-Gerir Pessoas");
            System.out.println("2-Obter Localização Pessoa");
            System.out.println("3-Ver Mensagens (Avisos/Alertas)");
            System.out.println("4-Consultar Contactos Efectuados por Pessoa");
            System.out.println("5-Lançar Emergência");
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
