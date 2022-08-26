package com.trivialware;

/**
 * Classe que representa uma pessoa da Universidade existente no Sistema, composto pelo identificador único da mesma,
 * nome e papel na universidade (Docente, Aluno, Funcionário ou Outro).
 */
public class Person {
    private final String id;
    private final Role role;
    private final String name;

    /**
     * Representação de uma Pessoa existente no sistema.
     *
     * @param id   Identificador único da pessoa
     * @param role Papel da pessoa na universidade
     * @param name Nome da Pessoa
     */
    public Person(String id, Role role, String name) {
        this.id = id;
        this.role = role;
        this.name = name;
    }

    /**
     * Papéis possíveis para uma Pessoa no Sistema.
     */

    enum Role {
        STUDENT,
        TEACHER,
        WORKER,
        OTHER;

        @Override
        public String toString() {
            return switch (this) {
                case STUDENT -> "Aluno";
                case TEACHER -> "Docente";
                case WORKER -> "Funcionário";
                case OTHER -> "Outro";
            };
        }

        /**
         * Função para converter o papel de uma String para Objeto. Útil no processo de importação de pessoas
         * de ficheiro para objetos ou para atribuição do nível de autorização necessário para aceder a uma localização
         * (pode ser vazio caso não seja necessária nenhuma autorização em especial para acesso), devolvendo um objeto
         * enumerável nulo.
         *
         * @param role Representação textual do papel da pessoa
         * @return Objeto enumerável correspondente ao papel, null caso seja uma string vazia (não tem role)
         */
        static Role fromString(String role) {
            return switch (role.toUpperCase()) {
                case "STUDENT" -> STUDENT;
                case "TEACHER" -> TEACHER;
                case "WORKER" -> WORKER;
                case "OTHER" -> OTHER;
                default -> null;
            };
        }

        /**
         * Função para converter um objeto enumerável que represente um papel em string, útil para exportação dos campos
         * de papel/papel necessário para acesso de uma pessoa ou localização respetivamente para ficheiro.
         *
         * @param role Representação em objeto do Papel de uma Pessoa/Localização
         * @return Representação textual do Papel de uma Pessoa/Localização
         */
        static String fromRole(Role role) {
            return switch (role) {
                case STUDENT -> "STUDENT";
                case TEACHER -> "TEACHER";
                case WORKER -> "WORKER";
                case OTHER -> "OTHER";
            };
        }
    }

    public String getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", getName(), getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id.equals(person.id);
    }
}
