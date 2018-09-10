package fogcloudproject;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class EPR {

	private int M; //Total number of processors = parallelism level
	private double[][] gamma;
	private int Pi = 0;
	private double[] utilization;
	private double[][] delta;
	public List<Subcomponent> subComponents;
	private boolean[] calculated;
	private boolean initializaed = false;
	public EPR() 
	{
		
	}
	public EPR(int inM) 
	{
		this.init(inM);
	}
	private void init(int inM)
	{
		M = inM;
		subComponents = new ArrayList<Subcomponent>();
		utilization = new double[M];
		calculated = new boolean[M];
		for(int i=0;i<this.M;i++)
			calculated[i] = false;
		initializaed = true;
	}
	public boolean isInitialized(int inM)
	{
		if(inM != this.M)
			return false;
		return initializaed;
	}
	private void initStructs()
	{
		gamma = new double[subComponents.size()][];
		initDelta(subComponents.size(), M);
		//initCalculated(subComponents.size(), M);
	}
 	private void initDelta(int row, int col)
	{
		delta = new double[row][];
		for(int i=0;i<row;i++)
		{
			delta[i] = new double[col]; 
		}
	}

	public void setM(int inM) 
	{
		init(inM);
	}
	
	public void deriveEPR(PeriodicTaskSet taskSet, String partitioningAlg,int device_index)
	{	
		if(calculated[this.M-1])
			return; // the EPR is already calculated
		decompose(taskSet, partitioningAlg,device_index);
		for(int i=0;i<subComponents.size();i++)
		{
			gamma[i] = new double[this.M];
			Subcomponent tmpSubcomp = subComponents.get(i);
			tmpSubcomp.deriveInterface(this.M);
			gamma[i] = tmpSubcomp.getEPR();
		}
		for(int i=0;i<this.M;i++)
			calculated[i] = true;
	}

	/**
	 * This overload assumes that the subcomponents are already created
	 */
	public void deriveEPR()
	{	
		initStructs();
		for(int i=0;i<subComponents.size();i++)
		{
			gamma[i] = new double[this.M];
			Subcomponent tmpSubcomp = subComponents.get(i);
			tmpSubcomp.deriveInterface(this.M);
			gamma[i] = tmpSubcomp.getEPR();
		}
		for(int i=0;i<this.M;i++)
			calculated[i] = true;
	}
	public void setPi(int inPi)
	{
		this.Pi = inPi;
	}
	public void Print()
	{
		for(int j=0;j<this.M;j++)
		{
			for(int i=0;i<this.subComponents.size();i++)
			{
				System.out.printf("gamma[%d][%d]: %f, ", i, j, gamma[i][j]);
				//epr[i][j].Print();
			}
			System.out.printf("\n");
		}
	}
	public double[] getUtilization()
	{
		calcUtilization();
		return utilization;
	}
	public void calcUtilization()
	{
		for(int j=0;j<this.M;j++)
		{
			utilization[j] = 0;
			for(int i=0;i<this.subComponents.size();i++)
			{
				utilization[j] += gamma[i][j];				
			}
		}
	}
	public void calcDelta()
	{
		for(int i=0;i<this.subComponents.size();i++)
		{
			delta[i][0] = 0;
		}
		for(int j=2;j<this.M;j++) //gamma[0] is null, gamma[1] is pr
		{
			for(int i=0;i<this.subComponents.size();i++)
			{
				delta[i][j] = gamma[i][j] - gamma[i][j-1];				
			}
		}
	}
	public double getMeanDelta(int p)
	{
		double sum = 0;
		calcDelta();
		for(int i=0;i<this.subComponents.size();i++)
		{
			sum += delta[i][p];				
		}
		return sum/this.subComponents.size();
	}
	public int getMinDeltaSubcomp(int[] parallelisms)
	{
		calcDelta();
		double minDelta = Double.MAX_VALUE;
		int subCompIndx = 0;//To avoid exception, if 0 is not the best then the epr will be infinity--> not integratable
		for(int i=0;i<this.subComponents.size();i++)
		{
			double currentDelta;
			if(parallelisms[i]+1 < delta[i].length) // to avoid null pointers
			{
				currentDelta = this.delta[i][parallelisms[i]+1]; //delta is calculated from 2, 2 means difference of 2 and 3
			}
			else
				currentDelta = Double.MAX_VALUE;
			if(currentDelta < minDelta)
			{
				minDelta = currentDelta;
				subCompIndx = i;
			}
		}
		return subCompIndx;
	}
	public void SetParallelisms(int[] parallelisms)
	{
		for(int i=0;i<this.subComponents.size();i++)
		{
			this.subComponents.get(i).setUsedParallelism(parallelisms[i]);		
		}
	}
	public double GetAverageParallelisms()
	{
		int sum = 0;
		for(int i=0;i<this.subComponents.size();i++)
		{
			sum += this.subComponents.get(i).getUsedParallelism();		
		}
		return ((double) sum)/this.subComponents.size();
	}
	public double getEPR(int subCompID, int parallelism)
	{
		if(parallelism > this.M -1)
			return Double.MAX_VALUE; //To make the integration impossible
		return gamma[subCompID][parallelism];
	}
	public int getNoSubcomps()
	{
		return subComponents.size();
	}
	private int createNewSubcomponent(PeriodicTaskSet taskSet,int device_index)
	{
		if(taskSet.getUtilization_on_specefic_Device(device_index)> 1)
		{
			System.err.print("utilization more than one \n");
		}
		int index = -1;
		index = subComponents.size();
		Subcomponent newSubcomp = new Subcomponent(this.Pi, taskSet,device_index);
		this.subComponents.add(newSubcomp);
		return index;
	}
	/**
	 * This function decomposed the component using the first fit heuristic.
	 */
	private void FFDecompose(PeriodicTaskSet taskSet,int device_index)
	{
		if(!subComponents.isEmpty())//Already decomposed
		{
			return;
			//System.err.println("Utilization is less than one, does not require decomposing");
			//return; decompose to the exact same component
		}
		taskSet.SortTasksUtilization(device_index);
		int i = 0;
		while(i<taskSet.getSize())
		{
			PeriodicTaskSet newTaskSet = new PeriodicTaskSet();
			while(newTaskSet.getUtilization_on_specefic_Device(i)<= 1 && i<taskSet.getSize())
			{
				PeriodicTask task = taskSet.getTask(i);
				if(task.getUtilization_on_specefic_device(device_index)+ newTaskSet.getUtilization_on_specefic_Device(device_index)<=1)
				{
					newTaskSet.AddTask(task);
					i++;
				}
				else
					break;
			}
			createNewSubcomponent(newTaskSet,device_index);
		}
		initStructs(); // Now that we have the subcomponents, we can initialize some structs
	}
	/**
	 * This function decomposed the component using the worst fit heuristic.
	 */
	private void WFDecompose(PeriodicTaskSet taskSet,int device_index)
	{
		int noProcessors = (int) Math.ceil(taskSet.getUtilization_on_specefic_Device(device_index));
		boolean decomposable = false;
		PeriodicTaskSet[] newTaskSetList = null;
		while(!decomposable)
		{
			newTaskSetList = new PeriodicTaskSet[noProcessors];
			for(int i=0;i<noProcessors;i++)
			{
				newTaskSetList[i] = new PeriodicTaskSet();	
			}
			if(!subComponents.isEmpty())//Already decomposed
			{
				return;
				//System.err.println("Utilization is less than one, does not require decomposing");
				//return; decompose to the exact same component
			}
			taskSet.SortTasksUtilization(device_index);
			decomposable = true;
			for(int i=0;i<taskSet.getSize();i++)
			{
				PeriodicTask newTask = taskSet.getTask(i);
				int indx = getWorstTaskSet(newTask, newTaskSetList,device_index);
				if(indx < 0)
				{
					decomposable = false;
					noProcessors++;
					break;
				}
				newTaskSetList[indx].AddTask(newTask);
			}
		}
		for(int i=0;i<noProcessors;i++)
		{
			createNewSubcomponent(newTaskSetList[i],device_index);
		}
		initStructs(); // Now that we have the subcomponents, we can initialize some structs
	}
	private void BFDecompose(PeriodicTaskSet taskSet,int device_index)
	{
		int noProcessors = (int) Math.ceil(taskSet.getUtilization_on_specefic_Device(device_index));
		PeriodicTaskSet[] newTaskSetList = null;
		boolean decomposable = false;
		while(!decomposable)
		{
			newTaskSetList = new PeriodicTaskSet[noProcessors];
			for(int i=0;i<noProcessors;i++)
			{
				newTaskSetList[i] = new PeriodicTaskSet();	
			}
			if(!subComponents.isEmpty())//Already decomposed
			{
				return;
				//System.err.println("Utilization is less than one, does not require decomposing");
				//return; decompose to the exact same component
			}
			taskSet.SortTasksUtilization(device_index);
			decomposable = true;
			for(int i=0;i<taskSet.getSize();i++)
			{
				PeriodicTask newTask = taskSet.getTask(i);
				int indx = getBestTaskSet(newTask, newTaskSetList,device_index);
				if(indx < 0 )
				{
					//System.err.print("indx -1");
					decomposable = false;
					noProcessors++;
					break;
				}
				newTaskSetList[indx].AddTask(newTask);
			}
		}
		for(int i=0;i<noProcessors;i++)
		{
			createNewSubcomponent(newTaskSetList[i],device_index);
		}
		initStructs(); // Now that we have the subcomponents, we can initialize some structs
	}
	private int getWorstTaskSet(PeriodicTask newTask, PeriodicTaskSet[] taskSetList,int device_index)
	{
		int indx = -1;
		double maxSlack = 0;
		for(int i=0;i<taskSetList.length;i++)
		{
			double currentSlack = 1 - taskSetList[i].getUtilization_on_specefic_Device(device_index)- newTask.getUtilization_on_specefic_device(device_index); 
			if(currentSlack > maxSlack && currentSlack >= 0)
			{
				indx = i;
				maxSlack = currentSlack;
			}
		}
		return indx;
	}
	private int getBestTaskSet(PeriodicTask newTask, PeriodicTaskSet[] taskSetList,int device_index)
	{
		int indx = -1;
		double leastSlack = Double.MAX_VALUE;
		for(int i=0;i<taskSetList.length;i++)
		{
			double currentSlack = 1 - taskSetList[i].getUtilization_on_specefic_Device(device_index)- newTask.getUtilization_on_specefic_device(device_index);
			currentSlack = Math.floor(currentSlack*1000)/1000.0;//To avoid double precision problem
			if(currentSlack < leastSlack && currentSlack >= 0)
			{
				indx = i;
				leastSlack = currentSlack;
			}
		}
		return indx;
	}
	public void decompose(PeriodicTaskSet taskSet, String alg,int device_index)
	{
		switch(alg)
		{
			case "WF": WFDecompose(taskSet,device_index); break;
			case "FF": FFDecompose(taskSet,device_index); break;
			case "BF": BFDecompose(taskSet,device_index); break;
			default: {System.err.print(alg+" is not implemented"); FFDecompose(taskSet,device_index);}
		}
	}

	public void PrintSubComponents()
	{
		for(int i=0;i<subComponents.size();i++)
		{
			System.out.printf("SubComponent[%d] \n", i);
			subComponents.get(i).PrintComponentTaskSet();
		}
	}

	public List<Subcomponent> getSubcomponents(PeriodicTaskSet taskSet, String alg,int device_index)
	{
		decompose(taskSet, alg,device_index);
		return this.subComponents;
	}
	/**
	 * Sort based on utilization
	 * @param p: parallelsim level
	 */
	public void SortSubcompsUtilization(int p)
	{
		int swapped = 1;
		while (swapped == 1)
		{
			swapped = 0;
			for(int i=1;i<this.subComponents.size();i++)
			{
				Subcomponent prev = this.subComponents.get(i-1);
				Subcomponent curr = this.subComponents.get(i);
				if(prev.getEPR()[p] < curr.getEPR()[p])
					{
					swapSubcomps(i-1, i);
						swapped = 1;
					}
			}
		}
	}
	private void swapSubcomps(int A, int B)
	{
		Collections.swap(this.subComponents, A, B);
	}
	public void importEPRSubcomponents(List<Subcomponent> inSubcomponents)
	{
		for(int i=0;i<inSubcomponents.size();i++)
		{
			this.subComponents.add(inSubcomponents.get(i));
		}
	}
	public void ClearSubcomponents()
	{
		this.subComponents.clear();
	}
	public void PrintParallelisms()
	{
		for(int i=0;i<this.subComponents.size();i++)
		{
			int p = this.subComponents.get(i).getUsedParallelism();
			System.out.printf("p:%d, ", p);
			//epr[i][j].Print();
		}
		System.out.printf("\n");
	
	}
}
