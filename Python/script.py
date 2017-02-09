from Simulator import Simulator
import numpy as np
import scipy as sp
import scipy.stats
import matplotlib.pyplot as plt


def mean_confidence_interval(data, confidence=0.95):
    a = 1.0*np.array(data)
    n = len(a)
    m, se = np.mean(a), scipy.stats.sem(a)
    h = se * sp.stats.t._ppf((1+confidence)/2., n-1)
    return m, m-h, m+h

accuracy = 4

lamb = 9.5
mu   = 1
c    = 10
st   = 100
reps = 10

util = lamb / (float)(mu * c)

# 	def __init__(lamb, mu, c, simTime, reps):
mySim = Simulator(lamb, mu, c, st, reps)

mySim.runSimulation()

throughput = mySim.getThroughput()
avgjobsinsys = mySim.getAvgJobsInSimulation()

roundedAvg = [round(elem, accuracy) for elem in avgjobsinsys]
roundedThroughput = [round(elem, accuracy) for elem in throughput]

print 'Params: (', lamb, ', ', mu, ', ', c, ', ', st, ', ', reps ,')'

print 'Utilization: ', round(util, accuracy)

print 'Throughput: ', roundedThroughput

print 'Avg # Jobs in System: ', roundedAvg

print 'Throughput Average: ', round((sum(roundedThroughput) / len(roundedThroughput)), accuracy)

print 'Average Average Number of Jobs in System: ', round((sum(roundedAvg) / len(roundedAvg)), accuracy)

mean, lower, upper = mean_confidence_interval(avgjobsinsys)

print 'CI: [', round(lower, accuracy), ',', round(upper, accuracy), ']'

mySim = Simulator(lamb, mu, 2, st, 1)
mySim.runSimulation()
xAxis = mySim.getXAxis()
yAxis = mySim.getYAxis()

plt.plot(xAxis, yAxis)
plt.title('RR - 95% Utilization 2 Servers')
plt.xlabel('Time')
plt.ylabel('Average Number of Jobs in System')
plt.show()
