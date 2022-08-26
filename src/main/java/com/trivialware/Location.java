package com.trivialware;

import java.util.Objects;

/*
 * Todas as localizações a instanciar existirão no grafo, excepto uma localização especial que é o ponto de emergência
 * Nem todos os edifícios/localizações podem estar ligados entre si (exemplo, auditórios com pavilhões), porém, todos os
 * edifícios onde localizações se encontram irão estar ligadas ao ponto de emergência central (que fica junto ao hall
 * de recepção), portanto o caminho mais curto irá ser feito de um ponto X até a esse ponto de emergência, percorrendo o
 * subgrafo correspondente do edifício em que a localização se encontra (que faz parte do grafo mais amplo da universidade).
 * O ponto imediatamente antes do ponto de emergência num percurso será uma saída válida.
 * Alternativamente, poder-se-ia implementar a funcionalidade de emergências através de atribuir um bool is_exit
 * a localizações que sejam saídas, e posteriormente calcular o caminho mais curto da localização atual para essa localização.
 * O que retorna um double e uma Queue. Cria-se uma classe para encapsular estes e considera-se o elemento de comparação
 * como o custo (distância total), e inserem-se objetos desta classe numa minHeap, retirando no final o menor. Este menor
 * será o menor caminho até à saída mais próxima (sendo que saídas que não se encontram no edifício irão ter uma distância
 * de infinito, caso não se estivesse a usar um ponto central de emergência).
 */

/**
 * Classe que representa uma Localização da Universidade, composta pelo seu identificador único, nome, capacidade
 * máxima e papel de utilizador a que esta localização está restrita. Após todos os eventos serem importados
 * tem também o número de pessoas que presentemente a ocupam.
 */
public class Location {
    private final String id;
    private final String name;
    private final int maximumCapacity;
    private int currentNumberPeople;
    private final Person.Role restrictedTo;

    /**
     * Construtor para classe que representa a localização a adicionar, com o Identificador único de uma Localização
     * da universidade, o nome extenso dessa localização, a capacidade máxima dessa localização
     * (inteiro) e o papel de Pessoa a que esta localização está restrito (Exemplo, apenas pode ser
     * utilizado por pessoas do papel Professor ou Funcionário; caso seja null, significa que pode
     * ser utilizado por qualquer pessoa independentemente do seu papel).
     *
     * @param id              Identificador único da localização da universidade
     * @param name            Nome da Localização na Universidade
     * @param maximumCapacity Capacidade Máxima definida da localização
     * @param restrictedTo    Papel de utilizador a que a localização está restrita
     */
    public Location(String id, String name, int maximumCapacity, Person.Role restrictedTo) {
        this.id = id;
        this.name = name;
        this.maximumCapacity = maximumCapacity;
        this.restrictedTo = restrictedTo;
    }

    public String getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public int getMaximumCapacity() {
        return maximumCapacity;
    }

    public Person.Role getRestrictedTo() {
        return restrictedTo;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", getName(), getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return id.equals(location.id);
    }

    public int getCurrentNumberPeople() {
        return currentNumberPeople;
    }

    public void setCurrentNumberPeople(int currentNumberPeople) {
        this.currentNumberPeople = currentNumberPeople;
    }
}
