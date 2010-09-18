package org.apache.jmeter.gui;

public class DataPoint {
    private long   time;
    private double value;

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
