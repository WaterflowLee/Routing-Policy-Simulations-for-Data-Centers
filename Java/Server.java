package com.company;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ksagh on 2017-02-05.
 */
public class Server {
    private static int numInstances;

    private List<Double> jobQueue;
    private boolean isBusy;
    private int serverID;

    public Server() {
        this.jobQueue = new ArrayList<>();
        this.serverID = Server.numInstances;
        this.isBusy = false;
        Server.numInstances += 1;
    }

    public void processNextDeparture() {
        if (jobQueue.size() > 0) {
            jobQueue.remove(0);
            if (jobQueue.size() == 0) {
                isBusy = false;
            }
        }
    }

    public void addNewArrival(double processingTime) {
        if (processingTime > 0) {
            jobQueue.add(processingTime);
            isBusy = true;
        }
    }

    public int getServerID() {
        return serverID;
    }

    public int getQueueLength() {
        return jobQueue.size();
    }

    public boolean getIsBusy() {
        return isBusy;
    }

    public double getNextDeparture() {
        if (jobQueue.size() > 0) {
            return jobQueue.get(0);
        }
        return Double.MAX_VALUE;
    }

    public void updateProcessingTimes(double elapsedTime) {
        if (jobQueue.size() > 0) {
            jobQueue.set(0, jobQueue.get(0) - elapsedTime);
        }
    }

    public void printServerState() {
        System.out.println("ServerID: " + serverID + " - has " + jobQueue.size() + " job(s).");
        System.out.println(jobQueue);
    }
}
