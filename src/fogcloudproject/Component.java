package fogcloudproject;


import java.util.List;

public class Component {
	private String partitioningAlg = "FF";
	private PeriodicTaskSet taskSet;
	private MPR mpr;
	private PR pr;
	private EPR epr;
	private int M;//The total number of available processors
	private long MPRTime = 0;
	private long EPRTime = 0;
	private int Pi;
	//public List<Subcomponent> subComponents;
	private int id;
        private int device_index;
	/**
	 * Task set and M must be provided later
	 */
	public Component()
	{
		M = 0;
		init();
	}
	private void init()
	{
		mpr = new MPR();
		pr = new PR();
		epr = new EPR();
	}
	/**
	 * Constructor that just takes the taskset as an input;
	 * @param inTasks is the taskset
	 */
	public Component(PeriodicTaskSet inTasks,int device_index)
	{
		M = 0;
		taskSet = inTasks;
                this.device_index=device_index;
		init();
	}
	/**
	 * Constructor that also takes the total number of as an input;
	 * @param inTasks is the taskset
	 * @param inM is the total number of available processors
	 */
	public Component(PeriodicTaskSet inTasks, int inM,int device_index)
	{
		M = inM;
		taskSet = inTasks;
                this.device_index=device_index;
		init();
	}
	public void setTaskSet(PeriodicTaskSet inTaskSet)
	{
		this.taskSet = inTaskSet;
	}
	public void setM(int inM)
	{
		this.M = inM;
		//mpr.setM(this.M);
		epr.setM(this.M);
	}
	public void setID(int inID)
	{
		this.id = inID;
	}
	public int getID()
	{
		return this.id;
	}
	public int getM()
	{
		return this.M;
	}
	public MPR getMPR()
	{
		//mpr.setM(M);
		long sTime = System.nanoTime();
		mpr.calcMinimalInterface(taskSet,device_index);
		this.MPRTime = System.nanoTime() - sTime;
		return mpr;
	}
	public PR getPR()
	{
		pr.calcInterface(taskSet);
		return pr;
	}
	public void setPi(int inPi)
	{
		this.Pi = inPi;
		mpr.setPi(this.Pi);
		pr.setPi(this.Pi);
		//epr.setPi(Pi);
	}
	
	public double getMPROverhead()
	{
		return this.mpr.getUtilization() - this.taskSet.getUtilization_on_specefic_Device(device_index);
	}
	public double getPROverhead()
	{
		return this.pr.getUtilization() - this.taskSet.getUtilization_on_specefic_Device(device_index);
	}
	public double getMPRUtilization()
	{
		return mpr.getUtilization();
	}
	public double[] getEPROverhead()
	{
		double[] overhead = new double[M];
		double[] utilization = epr.getUtilization();
		double taskSetUtil = taskSet.getUtilization_on_specefic_Device(device_index);
		for(int j=0;j<this.M;j++)
		{
			overhead[j] = utilization[j] - taskSetUtil;
		}
		return overhead;
	}
	public double getEPRUtilization(int paralellism)
	{
		double[] utilization = epr.getUtilization();
		return utilization[paralellism];
	}
	public void PrintEPROverhead()
	{
		double[] overhead = getEPROverhead();
		for(int j=0;j<this.M;j++)
		{
			System.out.printf("overhead[%d]: %f \n", j, overhead[j]);
		}
	}


	public void PrintComponentTaskSet()
	{
		taskSet.PrintTaskSet(device_index);
	}

	public PeriodicTaskSet getTaskSet()
	{
		return this.taskSet;
	}
	public EPR getEPR()
	{
		epr.setM(this.M);
		epr.setPi(this.Pi);
		long sTime = System.nanoTime();
		epr.deriveEPR(this.taskSet, this.partitioningAlg,device_index);
		this.EPRTime = System.nanoTime() - sTime;
		return epr;
	}
	public void Decompose()
	{
		epr.setM(this.M);
		epr.setPi(this.Pi);
		epr.decompose(this.taskSet, this.partitioningAlg,device_index);
		return;
	}
	public double getEPRMeanDelta(int p)
	{
		return epr.getMeanDelta(p);
	}
	public long getMPRTime()
	{
		return MPRTime;
	}
	public long getEPRTime()
	{
		return EPRTime;
	}

	public void setPartitioningAlg(String alg)
	{
		this.partitioningAlg = alg;
		if(this.epr.subComponents != null)
			this.epr.subComponents.clear();//because partitioning is changed now
	}
	public List<Subcomponent> getEPRSubcomponents()
	{
		return this.epr.getSubcomponents(this.taskSet, this.partitioningAlg,device_index);
	}
}
