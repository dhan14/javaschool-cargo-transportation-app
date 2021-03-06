package com.katekozlova.cargo.data.entity;

public enum TruckState {
    SERVICEABLE("Serviceable"), DEFECTIVE("Defective");

    public final String label;

    TruckState(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getName() {
        return name();
    }

    @Override
    public String toString() {
        return this.label;
    }
}
