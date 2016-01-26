package edu.Brandeis.cs131.Common.Abstract;

public enum Industry {

    TECHNOLOGY("TECHNOLOGY"),
    FINANCE("FINANCE"),
    ENERGY("ENERGY"),
    RESTAURANT("RESTAURANT"),
    CONSTRUCTION("CONSTRUCTION"),
    HEALTH("HEALTH"),
    ENTERTAINMENT("ENTERTAINMENT");
    
    private final String name;

    private Industry(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static Industry random() {
        int i = (int) (Math.random() * Industry.values().length);
        return Industry.values()[i];
    }
};