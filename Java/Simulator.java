package com.company;

import java.util.Random;
import java.util.Scanner;

/**
 * Created by ksagh on 2017-02-05.
 */
public class Simulator {

    private int numServers, numReps, numArrivals, numDepartures, numJobsInSystem;
    private double lambda, mu, simTime, ttna, ttnd, currentTime;
    private double[] throughput, avgJobsInSystem;
    private Server[] servers;
    private Random rng;
    public Simulator(double lambda, double mu, int numServers, double simTime, int numReps) {
//
        this.numServers = numServers;
        this.simTime = simTime;
        this.numReps = numReps;
        this.lambda = lambda;
        this.mu = mu;
//
        this.numJobsInSystem = 0;
        this.numDepartures = 0;
        this.numArrivals = 0;
//
        this.currentTime = 0d;
        this.ttna = 0d;
        this.ttnd = 0d;
//
        this.avgJobsInSystem = new double[numReps];
        this.throughput = new double[numReps];
//
        this.rng = new Random();
//
        this.servers = new Server[numServers];
        for (int i = 0; i < this.servers.length; ++i) {
            this.servers[i] = new Server();
        }
    }

    public double[] getThroughput() {
        return throughput;
    }

    public double[] getAvgJobsInSystem() {
        return avgJobsInSystem;
    }

    private void resetVariablesForRepetition() {
//
        this.numJobsInSystem = 0;
        this.numDepartures = 0;
        this.numArrivals = 0;
//
        this.currentTime = 0d;
        this.ttna = 0d;
        this.ttnd = 0d;
//
        this.servers = new Server[numServers];
        for (int i = 0; i < this.servers.length; ++i) {
            this.servers[i] = new Server();
        }
    }

    private double generateRandomTime(double param) {
        return -1d * Math.log(Math.abs(rng.nextDouble())) / param;
    }

    private double generateArrivalTime() {
        return generateRandomTime(lambda);
    }

    private double generateProcessingTime() {
        return generateRandomTime(mu);
    }

    private int getShortestQueueLengthIndex() {
        int serverIndex = 0;
        for (int i = 1; i < servers.length; i++) {
            if (servers[i].getQueueLength() < servers[serverIndex].getQueueLength()) {
                serverIndex = i;
            }
        }
        return serverIndex;
    }

    private int getRandomServerIndex() {
        return Math.abs(rng.nextInt()) % servers.length;
    }

    private void addNewJobToServer() {
        int serverIndex = getShortestQueueLengthIndex();
        servers[serverIndex].addNewArrival(generateProcessingTime());
    }

    private int getServerIndexWithNextDeparture() {
        int serverIndex = 0;
        for (int i = 1; i < servers.length; i++) {
            if (servers[i].getNextDeparture() < servers[serverIndex].getNextDeparture()) {
                serverIndex = i;
            }
        }
        return serverIndex;
    }

    private void updateServerTimes(double elapsedTime) {
        for (int i = 0; i < servers.length; ++i) {
            servers[i].updateProcessingTimes(elapsedTime);
        }
    }

    private void updateAverageNumJobsInSystem(int repNum, double t1, double t2) {
        avgJobsInSystem[repNum] = avgJobsInSystem[repNum] * t1 / (t1 + t2) + numJobsInSystem * t2 / (t1 + t2);
    }

    private void debug() {
        System.out.println("**********************************************");
        System.out.println("TTNA: " + ttna);
        System.out.println("TTND: " + servers[getServerIndexWithNextDeparture()].getNextDeparture());        System.out.println("Current time: " + currentTime);
        System.out.println("Jobs in system: " + numJobsInSystem);
        System.out.println("Arrivals: "  + numArrivals);
        System.out.println("Departures: " + numDepartures);
        System.out.println("----------------Server States-----------------");
        for (Server s : servers)
            s.printServerState();
        System.out.println("----------------------------------------------");
        System.out.println("Press \"ENTER\" to continue...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    public void runSimulation() {
        for (int simNum = 0; simNum < numReps; ++simNum) {
            System.out.println("Iteration: " + (simNum+1));
            rng.setSeed(rng.nextInt());
            resetVariablesForRepetition();
            ttna = generateArrivalTime();
            while (currentTime < simTime) {
                if (numJobsInSystem == 0) {
                    updateAverageNumJobsInSystem(simNum, currentTime, ttna);
//                    avgJobsInSystem[simNum] = avgJobsInSystem[simNum] * currentTime / (currentTime + ttna);
                    currentTime += ttna;
                    numArrivals += 1;
                    addNewJobToServer();
                    numJobsInSystem += 1;
                    ttna = generateArrivalTime();
                } else {
                    int serverWithNextDeparture = getServerIndexWithNextDeparture();
                    ttnd = servers[serverWithNextDeparture].getNextDeparture();
                    if (ttna < ttnd) {
                        updateAverageNumJobsInSystem(simNum, currentTime, ttna);
//                        avgJobsInSystem[simNum] = avgJobsInSystem[simNum] * currentTime / (currentTime + ttna) +
//                                numJobsInSystem * ttna / (ttna + currentTime);

                        currentTime += ttna;
                        updateServerTimes(ttna);
                        numArrivals += 1;
                        addNewJobToServer();
                        numJobsInSystem += 1;
                        ttna = generateArrivalTime();
                    } else {
                        updateAverageNumJobsInSystem(simNum, currentTime, ttnd);
//                        avgJobsInSystem[simNum] = avgJobsInSystem[simNum] * currentTime / (currentTime + ttnd) +
//                                numJobsInSystem * ttnd / (ttnd + currentTime);

                        currentTime += ttnd;
                        ttna -= ttnd;
                        updateServerTimes(ttnd);
                        numDepartures += 1;
                        numJobsInSystem -= 1;
                        servers[serverWithNextDeparture].processNextDeparture();
                    }
                }
//          Uncomment the following line for state dumps
//                debug();
            }
            if (numDepartures > numArrivals)
                System.out.println("Too many departures");
            throughput[simNum] = ((double)numDepartures) / currentTime;

        }
    }
}
