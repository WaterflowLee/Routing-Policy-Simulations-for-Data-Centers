import java.util.Random;
import java.util.Scanner;

/**
 * Created by ksagh on 2017-02-05.
 */
public class SidepartureRatelator {

    // Instance variables
    private int numServers, numReps, numArrivals, numDepartures, numJobsInSystem;
    private double arrivalRate, departureRate, simTime, timeToNextArrival, timeToNextDeparture, currentTime;
    private double[] throughput, avgJobsInSystem;
    private Server[] servers;
    private Random rng;

    // initializer
    public SidepartureRatelator(double arrivalRate, double departureRate, int numServers, double simTime, int numReps) {
        // Initialize values based on arguments
        this.numServers = numServers;
        this.simTime = simTime;
        this.numReps = numReps;
        this.arrivalRate = arrivalRate;
        this.departureRate = departureRate;
        // Intialize default 0 values - int
        this.numJobsInSystem = 0;
        this.numDepartures = 0;
        this.numArrivals = 0;
        // Intialize default 0 values - double
        this.currentTime = 0d;
        this.timeToNextArrival = 0d;
        this.timeToNextDeparture = 0d;
        // Empty arrays for simulation results. Each array has a size equal to the number of repetitions
        this.avgJobsInSystem = new double[numReps];
        this.throughput = new double[numReps];
        // Random number generator default initializer
        this.rng = new Random();
        // Fill the server queue with unique server instances
        this.servers = new Server[numServers];
        for (int i = 0; i < this.servers.length; ++i) {
            this.servers[i] = new Server();
        }
    }


    // Getter for the simulation's throughput results
    public double[] getThroughput() {
        return throughput;
    }

    // Getter for the simulation's average number of jobs in system results
    public double[] getAvgJobsInSystem() {
        return avgJobsInSystem;
    }

    // Certain variables need to be reset for each repetition of the simulation.
    // essentially repeats the initializer for a subset of the instance variables
    private void resetVariablesForRepetition() {
        this.numJobsInSystem = 0;
        this.numDepartures = 0;
        this.numArrivals = 0;

        this.currentTime = 0d;
        this.timeToNextArrival = 0d;
        this.timeToNextDeparture = 0d;

        this.servers = new Server[numServers];
        for (int i = 0; i < this.servers.length; ++i) {
            this.servers[i] = new Server();
        }
    }

    // Generates a random time using the instance RNG (uniform between 0 and 1)
    // Depending on the parameter passed in (lambda or mu) can be used as an arrival time or a processing time, respectively
    private double generateRandomTime(double param) {
        return -1d * Math.log(Math.abs(rng.nextDouble())) / param;
    }

    // Use the generateProcessingTime() using the lambda parameter to generate and arrival time
    private double generateArrivalTime() {
        return generateRandomTime(arrivalRate);
    }

    // Use the generateProcessingTime() using the mu parameter to generate and departure time
    private double generateProcessingTime() {
        return generateRandomTime(departureRate);
    }

    // Find the server with the shortest queue, improvements will be made to keep track of the shortest queue at each iteration,
    // which should improve the efficiency of the method
    // Should be used for JSQ simulations
    private int getShortestQueueLengthIndex() {
        int serverIndex = 0;
        for (int i = 1; i < servers.length; i++) {
            if (servers[i].getQueueLength() < servers[serverIndex].getQueueLength()) {
                serverIndex = i;
            }
        }
        return serverIndex;
    }

    // Gets a random server index
    // Should be used for Random Routing simulations
    private int getRandomServerIndex() {
        return Math.abs(rng.nextInt()) % servers.length;
    }

    // Generates a processing time and then adds the new processing time to a server based on the desired routing Policy
    // Current routing policies supported:
    // Join the Shortest Queue (JSQ)
    // Random Routing (RR)
    private void addNewJobToServer() {
        // Use for RR
        // int serverIndex = getRandomServerIndex();
        // Use for JSQ
        int serverIndex = getShortestQueueLengthIndex();
        // Add the processing time to the server chosen by the routing policy
        servers[serverIndex].addNewArrival(generateProcessingTime());
    }

    // TODO - Optimize
    // Find the server with the smallest processing time left
    private int getServerIndexWithNextDeparture() {
        int serverIndex = 0;
        for (int i = 1; i < servers.length; i++) {
            if (servers[i].getNextDeparture() < servers[serverIndex].getNextDeparture()) {
                serverIndex = i;
            }
        }
        return serverIndex;
    }

    // Updates the processing time of the jobs for all server queues
    private void updateServerTimes(double elapsedTime) {
        for (int i = 0; i < servers.length; ++i) {
            servers[i].updateProcessingTimes(elapsedTime);
        }
    }

    // Rolling average calculation for the number of jobs in the system at a given time
    private void updateAverageNumJobsInSystem(int repNum, double t1, double t2) {
        avgJobsInSystem[repNum] = avgJobsInSystem[repNum] * t1 / (t1 + t2) + numJobsInSystem * t2 / (t1 + t2);
    }

    // Used for logging
    private void logState() {
        System.out.println("**********************************************");
        System.out.println("timeToNextArrival: " + timeToNextArrival);
        System.out.println("timeToNextDeparture: " + servers[getServerIndexWithNextDeparture()].getNextDeparture());        System.out.println("Current time: " + currentTime);
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

    // Main function
    public void runSidepartureRatelation() {
      // Outer loop for the number of times the simulation is executed
        for (int simNum = 0; simNum < numReps; ++simNum) {
            // Reseed the instance RNG
            rng.setSeed(rng.nextInt());
            // Reset variables for the simulation to occur
            resetVariablesForRepetition();
            // Generate an arrival time
            timeToNextArrival = generateArrivalTime();
            // Simulation will run from times = 0 until the alloted simulation time
            while (currentTime < simTime) {
                // If no jobs are in the system, the job is assigned to a random server in the current iteration of the simulator
                if (numJobsInSystem == 0) {
                  // Update rolling average for number of jobs in the system
                    updateAverageNumJobsInSystem(simNum, currentTime, timeToNextArrival);
                    // Update the time
                    currentTime += timeToNextArrival;
                    // Arrivals updated by 1
                    numArrivals += 1;
                    // The arriving job is added to a server
                    // A processing time is added to the server based on the routing policy being used
                    addNewJobToServer();
                    // Jobs in the system has increased
                    numJobsInSystem += 1;
                    // Next arrival time is generated
                    timeToNextArrival = generateArrivalTime();
                } else {
                    // Find the server index of the next departure
                    int serverWithNextDeparture = getServerIndexWithNextDeparture();
                    // Get the time to the next departure from the index
                    timeToNextDeparture = servers[serverWithNextDeparture].getNextDeparture();
                    // Check if an arrival occurs first or a departure
                      // Arrival if handeled
                    if (timeToNextArrival < timeToNextDeparture) {
                      // Update rolling average for number of jobs in the system
                        updateAverageNumJobsInSystem(simNum, currentTime, timeToNextArrival);
                        // Update time
                        currentTime += timeToNextArrival;
                        // Update server processing times
                        updateServerTimes(timeToNextArrival);
                        // Arrival increments
                        numArrivals += 1;
                        // Job added to server based on routing policy
                        addNewJobToServer();
                        // New job added to system
                        numJobsInSystem += 1;
                        // Generate the next arrival time
                        timeToNextArrival = generateArrivalTime();
                      // Departure is handeled
                    } else {
                      // Update rolling average for number of jobs in the system
                        updateAverageNumJobsInSystem(simNum, currentTime, timeToNextDeparture);
                        // Update the current time
                        currentTime += timeToNextDeparture;
                        // Udpate the time to the next job arrival
                        timeToNextArrival -= timeToNextDeparture;
                        // Update server processing times
                        updateServerTimes(timeToNextDeparture);
                        // A departure occured, increment number of departures
                        numDepartures += 1;
                        // One less job in the system
                        numJobsInSystem -= 1;
                        // Process the departure from the server that has finished its task
                        servers[serverWithNextDeparture].processNextDeparture();
                    }
                }
            }
            // The throughput for the current repetition is calculates as the total number of departures
            // from the system divided by the total time the simulation took.
            throughput[simNum] = ((double)numDepartures) / currentTime;
        }
    }
}
