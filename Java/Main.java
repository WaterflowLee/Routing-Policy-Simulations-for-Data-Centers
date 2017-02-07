package com.company;

public class Main {

    public static void main(String[] args) {
        double lamb = 4d;
        double mu = 1d;
        double simTime = 10000;
        int c = 2;
        int reps = 3;

        Simulator s = new Simulator(lamb, mu, c, simTime, reps);
        s.runSimulation();

        double[] throughput = s.getThroughput();
        double[] avgjobs = s.getAvgJobsInSystem();

        for (double elem : throughput) {
            System.out.print(" " + elem + " ");
        }
        System.out.println();

        for (double elem : avgjobs) {
            System.out.print(" " + elem + " ");
        }
        System.out.println();
    }
}
