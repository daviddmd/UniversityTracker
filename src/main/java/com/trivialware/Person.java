package com.trivialware;

public class Person {
    private String id;
    private Role role;
    private String name;

    public Person(String id, Role role, String name) {
        this.id = id;
        this.role = role;
        this.name = name;
    }

    public Person(String id, String name) {
        this.id = id;
        this.name = name;
        this.role = Role.STUDENT;
    }

    enum Role {
        STUDENT,
        TEACHER,
        WORKER,
        OTHER;

        @Override
        public String toString() {
            return switch (this) {
                case STUDENT -> "Student";
                case TEACHER -> "Teacher";
                case WORKER -> "Worker";
                case OTHER -> "Other";
            };
        }

        static Role fromString(String role) {
            return switch (role) {
                case "STUDENT" -> STUDENT;
                case "TEACHER" -> TEACHER;
                case "WORKER" -> WORKER;
                case "OTHER" -> OTHER;
                default -> null;
            };
        }

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

    public void setId(String id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
