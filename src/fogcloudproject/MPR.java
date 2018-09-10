package fogcloudproject;

;
import java.util.Arrays;


public class MPR {

	public MPR() 
	{
		
	}
	
	private int mPrime;
	private int Pi = 0;
	private int Theta;
	private double epsilon = .001; //used for calculating AkMax - based on the email from Arvind
	private int M = -1;
	private double[] procAllocations;
	public void setPi(int inPi)
	{
		Pi = inPi;
	}
	public void setM(int inM)
	{
		this.M = inM;
		procAllocations = new double[this.M];
	}
	public double[] getProcAllocations()
	{
		return procAllocations;
	}
	public void setProcAllocation(int id, double portion)
	{
		this.procAllocations[id] = portion;
	}
	public int getPi()
	{
		return this.Pi;
	}
	public void setTheta(int inTheta)
	{
		this.Theta = inTheta;
	}
	public int getTheta()
	{
		return this.Theta;
	}
	public int getMprime()
	{
		return this.mPrime;
	}
	public void DeductTheta(int amount)
	{
		this.Theta -= amount;
	}
	/**
	 * This function calculates budget assuming gEDF task-scheduler.
	 * It assumes that the period Pi is already set.
	 * @param taskSet
	 */
	public void calcMinimalInterface(PeriodicTaskSet taskSet,int device_index)
	{
		double tmpTheta = 0;
		PeriodicTask taskk;
		Theta = 0;
		if(Pi==0)
		{
			  System.err.println("Period is not set!");
			  return;
		}
		mPrime=(int) Math.ceil(taskSet.getUtilization_on_specefic_Device(device_index));
		//for(mPrime<maxM;mPrime++)
		boolean calc = true;
		while (calc)
		{
			for(int k=0;k<taskSet.getSize();k++)
			{
				taskk = taskSet.getTask(k);
				tmpTheta = deriveThetaForTauk(taskSet, taskk, k,device_index);
				updateThetaIfLarger((int) Math.ceil(tmpTheta));
			}
			
			if(this.getUtilization() <= this.mPrime||this.mPrime>17)
				calc = false; //condition for an acceptable interface
			else
				mPrime++; //try again with mPrime++ 
		}
		//System.err.println("Could not find MPR for maxM!");
	}
        
        public int calcMinimalInterfaceEdited(PeriodicTaskSet taskSet,int pi,int device_index)
	{
		double tmpTheta = 0;
		PeriodicTask taskk;
		Theta = 0;
                this.Pi=pi;
		if(Pi==0)
		{
			  System.err.println("Period is not set!");
			  return 0;
		}
		mPrime=(int) Math.ceil(taskSet.getUtilization_on_specefic_Device(device_index));
		//for(mPrime<maxM;mPrime++)
		boolean calc = true;
		while (calc)
		{
			for(int k=0;k<taskSet.getSize();k++)
			{
				taskk = taskSet.getTask(k);
				tmpTheta = deriveThetaForTauk(taskSet, taskk, k,device_index);
				updateThetaIfLarger((int) Math.ceil(tmpTheta));
			}
			
			if(this.getUtilization() <= this.mPrime||this.mPrime>17)
				calc = false; //condition for an acceptable interface
			else
				mPrime++; //try again with mPrime++ 
                        
                        
		}
		//System.err.println("Could not find MPR for maxM!");
                return mPrime;
	}
	/**
	 * This function calculates budget assuming gEDF task-scheduler for an input m'.
	 * It assumes that the period Pi is already set.
	 * @param taskSet
	 */
	public void calcThetaForInM(PeriodicTaskSet taskSet, int inMPrime,int device_index)
	{
		double tmpTheta = 0;
		PeriodicTask taskk;
		Theta = 0;
		if(Pi==0)
		{
			  System.err.println("Period is not set!");
			  return;
		}
		if(taskSet.getUtilization_on_specefic_Device(device_index)> inMPrime)
		{
			  System.err.println("The taskset utilization is more than the number of processors");
			  Theta = -1;
			  return;
		}
		mPrime = inMPrime;
		for(int k=0;k<taskSet.getSize();k++)
		{
			taskk = taskSet.getTask(k);
			tmpTheta = deriveThetaForTauk(taskSet, taskk, k,device_index);
			updateThetaIfLarger((int) Math.ceil(tmpTheta));
		}
		
		if(this.getUtilization() > this.mPrime)
		{
			System.err.println("Could not find MPR for InM!");
			Theta = -1;
		}
	}
        
        
	private double deriveThetaForTauk(PeriodicTaskSet taskSet, PeriodicTask taskK, int k,int device_index)
	{
		double tmpTheta = 0;
		double finalTheta = 0;
		double tmpDemand = 0;
		int maxAk = (int) Math.ceil(deriveAkMax(taskSet, taskK,device_index));
		//System.out.printf("----maxAk: %d \n", maxAk);
		for(int Ak=0;Ak<=maxAk;Ak++)
		{
			tmpDemand = DEM(taskSet, k, Ak,device_index);
			tmpTheta = deriveTheta(Ak+taskK.getAbsolute_deadline(), tmpDemand);
			if(tmpTheta>finalTheta)
				finalTheta = tmpTheta;
		}
		return finalTheta;
	}
	private double deriveAkMax(PeriodicTaskSet taskSet, PeriodicTask taskK,int device_index)
	{
		double result = 0;
		double tmpTheta = taskSet.getUtilization_on_specefic_Device(device_index)+ epsilon*taskSet.getUtilization_on_specefic_Device(device_index);
		double AkStart = deriveAk(taskSet, taskK, tmpTheta,device_index);
		double AkEnd = deriveAk(taskSet, taskK, mPrime*Pi,device_index);
		//System.out.printf("----AkStart: %f, AkEnd %f \n", AkStart, AkEnd);
		result = Math.max(AkStart, AkEnd);
		if(result<0)
			result = 0; //Ak has to be >=0
		return result;
	}
	private double deriveAk(PeriodicTaskSet taskSet, PeriodicTask taskK, double tmpTheta,int divice_index)
	{
		double result = 0;
		double Utau = taskSet.getUtilization_on_specefic_Device(divice_index);
		//double tmpTheta = Utau + epsilon*Utau;
		double U = 0;
		for(int i=0;i<taskSet.getSize();i++)
		{
			PeriodicTask taski = taskSet.getTask(i);
			U += (taski.period - taski.getAbsolute_deadline())* ((double)taski.getAbsolute_execution_time()/taski.period);
		}
		double B = tmpTheta/Pi*(2+2*(Pi-tmpTheta/mPrime));
		double Csigma = getSumOfnLargest(taskSet.getAllExecTimes_on_specific_device(divice_index), mPrime);
		double tmpUtil = tmpTheta/Utau;
		double tmpUtilDiff = tmpUtil - Utau; // It will be epsilon
		result = (Csigma + mPrime*taskK.getAbsolute_execution_time() - taskK.getAbsolute_deadline()*tmpUtilDiff + U + B)/(tmpUtilDiff);
		return result;
	}
	private double deriveTheta(double t, double demand)
	{
		double result = 0;
		double a = (double)2/(mPrime*Pi);
		double b = (double) (t-2) / Pi - 2;
		double c = -demand;
		double delta = b*b-4*a*c;
		result = (-b+ Math.sqrt(delta))/(2*a);
		return result; 
	}
	private double DEM(PeriodicTaskSet taskSet, int k, int A_k,int device_index)
	{
		double demand = 0;
		double diff[] = new double[taskSet.getSize()];
		demand = 0;
	    demand = demand + mPrime * taskSet.getTask(k).getAbsolute_execution_time();
	    for(int i=0;i<taskSet.getSize();i++)
	    {
	    	PeriodicTask taski = taskSet.getTask(i);
	    	PeriodicTask taskk = taskSet.getTask(k);
	        demand = demand + I_hat(taski, taskk, A_k, i, k,device_index);
	    }
	    
	    for(int i=0;i<taskSet.getSize();i++)
	    {
	    	PeriodicTask taski = taskSet.getTask(i);
	    	PeriodicTask taskk = taskSet.getTask(k);
	        diff[i] = I_bar(taski, taskk, A_k, i, k,device_index) - I_hat(taski, taskk, A_k, i, k,device_index);
	    }
	    int diff_end;
	    if (mPrime-1>taskSet.getSize())
	        diff_end = taskSet.getSize();
	    else
	        diff_end = mPrime-1;
	    demand +=  getSumOfnLargest(diff, diff_end);
		return demand;
	}
	private double carryIn(PeriodicTask task, double t,int device_index)
	{
		double result = 0;
		double tmp;
		tmp = Math.floor((float)(t+(task.period-task.getAbsolute_deadline()))/task.period);
		result = Math.min(task.getAbsolute_execution_time(), Math.max(0, t-tmp*task.period));
		return result;
	}
	private double W(PeriodicTask task, double t,int device_index)
	{
		double result = 0;
		double tmp;
		tmp = Math.floor((float)(t+(task.period-task.getAbsolute_deadline()))/task.period);
		result = tmp*task.getAbsolute_execution_time() + carryIn(task, t,device_index);
		//System.out.printf("W %f \n", result);
		return result;
	}
	private double I_hat(PeriodicTask task_i, PeriodicTask task_k, int A_k, int i, int k,int device_index)
	{
		double result=0;
		if(i!=k)
		{
	        result = Math.min(W(task_i, A_k + task_k.getAbsolute_deadline(),device_index) - carryIn(task_i, A_k + task_k.getAbsolute_deadline(),device_index) , A_k + task_k.getAbsolute_execution_time() - task_k.getExecution_time_on_specific_device(device_index));
		}
	    else
	    {
	        result = Math.min(W(task_k, A_k + task_k.getAbsolute_deadline(),device_index) - task_k.getAbsolute_execution_time() - carryIn(task_k, A_k + task_k.getAbsolute_deadline(),device_index), A_k);
		}
		return result;
	}
	private double I_bar(PeriodicTask task_i, PeriodicTask task_k, int A_k, int i, int k,int device_index)
	{
		double result=0;
		if(i!=k)
		{
	        result = Math.min(W(task_i, A_k + task_k.getAbsolute_deadline(),device_index), A_k + task_k.getAbsolute_deadline() - task_k.getAbsolute_execution_time());
		}
	    else
	    {
	        result = Math.min(W(task_k, A_k + task_k.getAbsolute_deadline(),device_index) - task_k.getAbsolute_execution_time(), A_k);
		}
		return result;
	}
	/**
	 * get the sum of n largest elements
	 */
	private double getSumOfnLargest(double arr[], int n)
	{
		double sum = 0;
		int size = arr.length;
		if(n>size)
			n = size;
		Arrays.sort(arr);
		for(int i=size-1;i>size-n-1;i--)
	        sum += arr[i];
		return sum;
	}
	private void updateThetaIfLarger(int newTheta)
	{
		if(newTheta > Theta)
			Theta = newTheta;
	}
	public void PrintMPR()
	{
		System.out.print("--------------- MPR -------------  ");
		System.out.printf("Pi: %d Theta: %d m': %d U: %f \n", this.Pi, this.Theta, this.mPrime, getUtilization());
	}
	public double getUtilization()
	{
		return (double)Theta/Pi;
	}
	/*private double lsbf(int theta, int pi, int mprime, int t)
	{
		double result = 0;
		double tmp = 2*(pi-((double)theta/mprime))+2;
		result = ((double)theta/pi)*(t-tmp);
		return result;
	}*/
}
