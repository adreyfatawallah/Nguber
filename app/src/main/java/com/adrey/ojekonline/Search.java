package com.adrey.ojekonline;

/**
 * Created by Muh Adrey Fatawallah on 11/9/2016.
 */

class Search {

    private String main_text;
    private String secondary_text;
    private String reference;

    String getMain_text() {
        return main_text;
    }

    void setMain_text(String main_text) {
        this.main_text = main_text;
    }

    String getSecondary_text() {
        return secondary_text;
    }

    void setSecondary_text(String secondary_text) {
        this.secondary_text = secondary_text;
    }

    String getReference() {
        return reference;
    }

    void setReference(String reference) {
        this.reference = reference;
    }

    @Override
    public String toString() {
        return "Search{" +
                "main_text='" + main_text + '\'' +
                ", secondary_text='" + secondary_text + '\'' +
                ", reference='" + reference + '\'' +
                '}';
    }
}
