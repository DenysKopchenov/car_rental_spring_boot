package com.dkop.car.rental.model.car;

public enum CategoryClass {

    BUDGET("Budget"), COMFORT("Comfort"), CROSSOVER("Crossover"), BUSINESS("Business"), PREMIUM_SUV("Premium SUV");

    private final String name;

    CategoryClass(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
