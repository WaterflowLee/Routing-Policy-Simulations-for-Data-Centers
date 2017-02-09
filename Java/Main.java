public class Main {

    public static void main(String[] args) {
        double arrivalRate = 4d;
        double departRate  = 1d;
        double simTime     = 10000;
        int numServers     = 2;
        int reps           = 3;

        Simulator s = new Simulator(arrivalRate, departRate, numServers, simTime, reps);
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
