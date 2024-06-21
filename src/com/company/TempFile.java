package com.company;

public class TempFile {
    private String xCoordinator;
    private String yCoordinator;
    private String timeInterval;

    public TempFile(String xCoordinator, String yCoordinator, String timeInterval) {
        this.xCoordinator = xCoordinator;
        this.yCoordinator = yCoordinator;
        this.timeInterval = timeInterval;
    }

    public String getxCoordinator() {
        return xCoordinator;
    }

    public String getyCoordinator() {
        return yCoordinator;
    }

    public String getTimeInterval() {
        return timeInterval;
    }
}
