package com.libris.model;

public class ReportStat {
    private String label;
    private int value;
    private double doubleValue;
    private String stringValue;

    public ReportStat() {}

    public ReportStat(String label, int value) {
        this.label = label;
        this.value = value;
    }

    public ReportStat(String label, double doubleValue) {
        this.label = label;
        this.doubleValue = doubleValue;
    }

    public ReportStat(String label, String stringValue) {
        this.label = label;
        this.stringValue = stringValue;
    }

    // Getters and Setters
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
    public double getDoubleValue() { return doubleValue; }
    public void setDoubleValue(double doubleValue) { this.doubleValue = doubleValue; }
    public String getStringValue() { return stringValue; }
    public void setStringValue(String stringValue) { this.stringValue = stringValue; }
}