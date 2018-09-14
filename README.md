#### An Optimization Framework For Fog-Integrated Industrial IoT

It is an optimization framework to design a fog-integrated industrial IoT system which can be used as a foundation for deployment of a Smart Factory. This gets a lot of information about system specifications such as number of end-nodes, number of fog nodes, the processing capacity of fog nodes, number and size of each industrial IoT application as inputs and produces several design decisions such as placement of the applications among end-nodes, fog nodes, local servers, and the cloud. To read more about the concept behind this framework please refer to this paper: 
https://ieeexplore.ieee.org/abstract/document/8397079/

#### How To Run
Run with java command and select one of the benchmarks (Small, Medium or Large).

```java - jar fog.jar```

#### Result
If Honeybee Algorithm finds a feasible solution, it writes 'Feasible Solution' otherwise it prints 'Infeasible Solution' (At least one constraint is violated).
```
FEASIBLE Solution
```

Fitness Value shows the total execution cost of applications if there is no penalty, otherwise, when there is a penalty, the fitness value is calculated according to equation `FITNESS = cloudCost + privateCloudCost + BETA1 * utilizationPenalty() + BETA2 * (MemoryPenalty + HardVmPenalty + CommunicationPenalty`

```Fitness --> 1029.9482```

Number 0 to 9 correspond fog nodes , 'c' correspond Cloud and 'pc' corresponds Private Cloud. 

For example the first vm is assigned to fog node number 6 and the second vm is assigned to the Private cloud. 

```
Allocation --> [6, pc, pc, 9, 6, 3, pc, 8, pc, pc, pc, 9, pc, pc, 3, 8, 7, pc, 4, pc, 1, pc, pc, pc, pc, pc, 4, pc, pc, 5, pc, 0, pc, 5, pc, pc, pc, pc, pc, pc, pc, 1, pc, pc, 9, pc, 4, pc, pc, pc, pc, pc, 5, pc, pc, 2, pc, pc, pc, 6, pc, pc, pc, pc, pc, pc, pc, 2, pc, pc, pc, pc, pc, pc, pc, pc, pc, pc, pc, pc, pc, pc, pc, pc, pc, 1, pc, pc, 7, pc, pc, pc, pc, pc, pc, pc, pc, pc, pc, pc, pc, pc, pc, pc, pc, 8, pc, pc, pc, pc, pc, pc, pc, pc, pc, pc, pc, pc, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, 9, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, 6, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c, c]
```

Cost Cloud = VM_CONSTANT_COST + VM_NETWORK_COST + Container_COST 

```
Cost Cloud --> 4.158200000000001
0.138 + 2.08 + 1.940200000000001 +  = 4.158200000000001

Instances on Cloud --> 73
```

PRIVATE CLOUD COST  = PRIVATE CLOUD SERVERS * (LOCALSERVER_MAINTANANCE_COST_ENERGY + 2 * LOCALSERVER_MAINTANANCE_COST_IT_MAN + LOCALSERVER_PURCHASE_COST
```
Cost Private Cloud --> 1025.79
Number of Servers in Private Cloud --> 93
LOCALSERVER_MAINTANANCE_COST_ENERGY : 1.0
LOCALSERVER_MAINTANANCE_COST_IT_MAN : 5.0
LOCALSERVER_PURCHASE_COST : 0.03
```

If it is zero there is no constraint violation and if it is greater than zero there is constraint violation.
```
Utilization Penalty --> 0.0
Memory Penalty --> 0.0
Hard Penalty --> 0
Com Penalty --> 0.0
```

If there is any constraint violation, Beta1 and Beta2 can be use to calculate the fitness.
```
BETA1 --> 3176640.0000000186
BETA2 --> 3176.6400000000185
```

Summary of Fitness and Execution time over several runs.
```
Number of Used fog nodes over All fog nodes --> 100.0%


Fitness of HoneyBee runs :  : [1052, 1040, 1085, 1052, 1029, 1040, 1074, 1040, 1085, 1063, 1074, 1062, 1040, 1085, 1063]
Fitness of Random Fit runs : [1900.4334000000001, 2032.5318, 1944.4694, 1977.6409999999998, 1955.479, 1955.4448, 2032.7852, 1966.5698, 1999.7534, 1966.4887999999999, 1889.3924000000002, 1878.53, 1933.4598, 1955.6242, 1878.2898]
Execution Times (MILLISEC) :  : [2942736.0, 3082095.0, 3026931.0, 3031991.0, 2912320.0, 2952094.0, 3157068.0, 2904336.0, 3083356.0, 3140370.0, 3054246.0, 3005997.0, 2993263.0, 2965946.0, 2902341.0]

Average Fitness of HoneyBee: 1058.9333333333334
Average Fitness of Random Fit: 1951.1261866666662
Average Execution Times (MILLISEC) : 3010339.3333333335
```
