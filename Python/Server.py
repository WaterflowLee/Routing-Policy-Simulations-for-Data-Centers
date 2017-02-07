from sys import maxint

class Server(object):

    _numInstances = 0

    def __init__(self):
        super(Server, self).__init__()
        self.queue = []
        self.serverID = Server._numInstances
        self.isBusy = False

        Server._numInstances += 1

    def processNextDeparture(self):
        if (len(self.queue) > 0):
            # self.queue.sort()
            self.queue.pop(0)
            if (len(self.queue) == 0):
                self.isBusy = False

    def addNewArrival(self, processingTime):
        if (processingTime > 0):
            self.queue.append(processingTime)
            # self.queue.sort()
            self.isBusy = True

    def getServerID(self):
        return self.serverID

    def getQueueLength(self):
        return len(self.queue)

    def getNextDepartureTime(self):
        if (len(self.queue) > 0):
            # self.queue.sort()
            return self.queue[0]
        return maxint

    def getIsBusy(self):
        return self.isBusy

    def updateProcessingTimes(self, elapsedTime):
        if (len(self.queue) > 0):
            self.queue[0] -= elapsedTime
        # for i in range(0, len(self.queue)):
        #     self.queue[i] -= elapsedTime

    def printServerState(self):
        print 'ServerID: ', self.serverID, ' - queue length ', len(self.queue), ' jobs:'
        print self.queue
