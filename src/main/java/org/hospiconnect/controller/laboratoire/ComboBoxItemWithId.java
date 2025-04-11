package org.hospiconnect.controller.laboratoire;

public class ComboBoxItemWithId {

    private final Long id;
    private final String label;

    public ComboBoxItemWithId(Long id, String label) {
        this.id = id;
        this.label = label;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return label;
    }
}
