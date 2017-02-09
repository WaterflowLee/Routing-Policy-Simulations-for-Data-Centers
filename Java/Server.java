import java.util.ArrayList;
import java.util.List;

/**
 * Created by ksagh on 2017-02-05.
 */
public class Server {
    // Keep track of the number of servers created
    private static int numInstances;
    // Keep track of the processing times of the jobs in queue
    private List<Double> jobQueue;
    // Keep track of the server's state
    private boolean isBusy;
    // For now this will just track the instance number
    private int serverID;

    /*
      Default initializer
      Jobqueue is set to an empty array list of doubles (processing times)
      ServerID is set to the instance number for now
      Server is set to not busy
      Instance number is incremented
    */
    public Server() {
        // Create an empty job queue
        this.jobQueue = new ArrayList<>();
        this.serverID = Server.numInstances;
        this.isBusy = false;
        Server.numInstances += 1;
    }

    /*
      Remove the currently processing job from the server queue
    */
    public void processNextDeparture() {
        // Ensure there is actually a job to remove
        if (jobQueue.size() > 0) {
            jobQueue.remove(0);
            // Server is no longer busy if no jobs exist
            if (jobQueue.size() == 0) {
                isBusy = false;
            }
        }
    }

    // Add a new job with a specified processing time to the server queue
    public void addNewArrival(double processingTime) {
        if (processingTime > 0) {
            jobQueue.add(processingTime);
            isBusy = true;
        }
    }

    // Getter for serverID
    public int getServerID() {
        return serverID;
    }

    // Getter for queue length
    public int getQueueLength() {
        return jobQueue.size();
    }

    // Getter for server busy state
    public boolean getIsBusy() {
        return isBusy;
    }

    // Getter for the next departure time from the server (the smallest processing time left)
    public double getNextDeparture() {
        if (jobQueue.size() > 0) {
            return jobQueue.get(0);
        }
        // If no jobs processing then the time is essentially infinite
        // Assumes for all jobs the processing time of the job is less than Double.MAX_VALUE
        return Double.MAX_VALUE;
    }

    // Update the processing time of the currently processing job
    // Models a server with 1 thread no parallel processing
    // Model also assumes a first come first server queue
    public void updateProcessingTimes(double elapsedTime) {
        if (jobQueue.size() > 0) {
            jobQueue.set(0, jobQueue.get(0) - elapsedTime);
        }
    }

    // Prints information about the server and the server queue
    public void printServerState() {
        System.out.println("ServerID: " + serverID + " - has " + jobQueue.size() + " job(s).");
        System.out.println(jobQueue);
    }
}
