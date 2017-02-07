from Server import Server
from sys import maxint
from math import log
import random

class Simulator(object):

    def __init__(self, lamb, mu, numServers, simTime, numReps):
        super(Simulator, self).__init__()

        # Inistialize based on argument
        self.lamb = lamb
        self.mu = mu
        self.numServers = numServers
        self.simTime = simTime
        self.numRepetitions = numReps

        # Fill an array with C server objects
        self.servers = [Server() for i in range(0, numServers)]

        # Initialize to default values
        self.timeToNextArrival = 0
        self.timeToNextDeparture = 0
        self.currentTime = 0
        self.numArrivals = 0
        self.numDepartures = 0
        self.numJobsInSystem = 0

        # Return arrays
        self.throughput = []
        self.avgNumJobsInSystem = []

        # For Plots
        self.avgJobsTracker = []
        self.timeTracker = []

    def getXAxis(self):
        return self.timeTracker

    def getYAxis(self):
        return self.avgJobsTracker

    def resetVariablesForNewRepetition(self):
        self.servers = [Server() for i in range(0, self.numServers)]
        self.timeToNextArrival = 0
        self.timeToNextDeparture = 0
        self.numJobsInSystem = 0
        self.currentTime = 0
        self.numArrivals = 0
        self.numDepartures = 0

    def addNewJobToServers(self):
        # Change decision making policy here
        processingTime = self.generateNextProcessingTime()
        self.servers[getShortestQueueLength()].addNewArrival(processingTime)

    def generateRandomTime(self, param):
        return -1.0 * log(random.random()) / (float)(param)

    def generateNextArrival(self):
        return self.generateRandomTime(self.lamb)

    def generateNextProcessingTime(self):
        return self.generateRandomTime(self.mu)

    # Use for Join the Shortest Queue
    def getShortestQueueLength(self):
        shortestQueue = self.servers[0]
        for server in self.servers:
            if (server.getQueueLength() < shortestQueue.getQueueLength()):
                shortestQueue = server
        return self.servers.index(shortestQueue)

    # Use for Random Routing
    def getRandomServer(self):
        return (random.randint(0,100)) % len(self.servers)

    def getServerWithNextDeparture(self):
        nextDeparture = self.servers[0]
        for server in self.servers:
            if (server.getNextDepartureTime() < nextDeparture.getNextDepartureTime()):
                nextDeparture = server
        return self.servers.index(nextDeparture)

    def getThroughput(self):
        return self.throughput

    def getAvgJobsInSimulation(self):
        return self.avgNumJobsInSystem

    def updateServerTimes(self, elapsedTime):
        for server in self.servers:
            server.updateProcessingTimes(elapsedTime)

    def updateAverageNumJobsInSystem(self, i, t1, t2):
        self.avgNumJobsInSystem[i] = self.avgNumJobsInSystem[i] * t1 / (float)(t1 + t2) \
        + self.numJobsInSystem * t2 / (float)(t1 + t2)

    def printState(self):
        print '-------------------------------------------------------'
        print 'TTNA\t', self.timeToNextArrival
        ttnd = self.servers[self.getServerWithNextDeparture()].getNextDepartureTime()
        print 'TTND\t', ttnd
        print 'Current Time\t', self.currentTime
        print 'Jobs in sys\t', self.numJobsInSystem
        print 'Arrivals\t', self.numArrivals
        print 'Departures\t', self.numDepartures
        print 'Avg jobs in sim\t', self.avgNumJobsInSystem
        for elem in self.servers:
            elem.printServerState()
        print '*******************************************************'
        if (ttnd < self.timeToNextArrival):
            print 'DEPART from: ', self.servers[self.getServerWithNextDeparture()].getServerID()
        else:
            print 'ARRIVAL to: ', self.servers[self.getShortestQueueLength()].getServerID()
        raw_input('Next Iteration...')

    def runSimulation(self):
        for simNumber in range(0, self.numRepetitions):
            print 'Iteration: ', simNumber+1
            random.seed()
            self.resetVariablesForNewRepetition()
            self.timeToNextArrival = self.generateNextArrival()
            self.avgNumJobsInSystem.append(0)
            while (self.currentTime < self.simTime):
                if (self.numJobsInSystem == 0):
                    self.updateAverageNumJobsInSystem(simNumber, self.currentTime, self.timeToNextArrival)
                    self.currentTime += self.timeToNextArrival
                    self.numArrivals += 1
                    # Add first job to random server
                    self.servers[self.getRandomServer()].addNewArrival(self.generateNextProcessingTime())
                    self.numJobsInSystem += 1
                    self.timeToNextArrival = self.generateNextArrival()
                else:
                    serverWithNextDeparture = self.getServerWithNextDeparture()
                    self.timeToNextDeparture = self.servers[serverWithNextDeparture].getNextDepartureTime()
                    if (self.timeToNextArrival < self.timeToNextDeparture):
                        # Arrival Occurs
                        self.updateAverageNumJobsInSystem(simNumber, self.currentTime, self.timeToNextArrival)
                        self.currentTime += self.timeToNextArrival
                        self.updateServerTimes(self.timeToNextArrival)
                        self.numArrivals += 1
                        self.servers[self.getShortestQueueLength()].addNewArrival(self.generateNextProcessingTime())
                        self.numJobsInSystem += 1
                        self.timeToNextArrival = self.generateNextArrival()
                    else:
                        # Departure Occurs
                        self.updateAverageNumJobsInSystem(simNumber, self.currentTime, self.timeToNextDeparture)
                        self.currentTime += self.timeToNextDeparture
                        self.timeToNextArrival -= self.timeToNextDeparture
                        self.numDepartures += 1
                        self.numJobsInSystem -= 1
                        self.updateServerTimes(self.timeToNextDeparture)
                        self.servers[serverWithNextDeparture].processNextDeparture()
                self.avgJobsTracker.append(self.avgNumJobsInSystem[simNumber])
                self.timeTracker.append(self.currentTime)
                # To debug uncomment the following line:
                # self.printState()
            throughputForRepetition = self.numDepartures / (float)(self.currentTime)
            self.throughput.append(throughputForRepetition)
