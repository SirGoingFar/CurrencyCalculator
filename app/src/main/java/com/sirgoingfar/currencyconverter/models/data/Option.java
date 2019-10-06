package com.sirgoingfar.currencyconverter.models.data;

public class Option {

    private String text;
    private String name;
    private String url;
    private boolean isSelected;

    public Option(String text, String name, String url) {
        this.text = text;
        this.name = name;
        this.url = url;
        this.isSelected = false;
    }

    public String getText() {
        return text;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }
}
