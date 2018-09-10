/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fogcloudproject;

import java.util.Random;

/**
 *
 * @author saeid
 */
public class PeriodicTask {
    public static final int IS_RESTRICTED_RATIO = 10;

    int index;
    int period;
    int absolute_deadline;
    double absolute_execution_time;
    double []deadline;
    double []execution_time;
    int data_mass;
    double ram;
    boolean isRestricted=false;
    int input_data_mass;
    int output_data_mass;
    double timeSensitive;
    int offset = 200;

    public PeriodicTask()
    {}

    public double getRam() {
        return ram;
    }

    public double getAbsolute_execution_time() {
        return absolute_execution_time;
    }

    public void setAbsolute_execution_time(int absolute_execution_time) {
        this.absolute_execution_time = absolute_execution_time;
    }

    public double[] getExecution_time() {
        return execution_time;
    }

    public void setExecution_time(double[] execution_time) {
        this.execution_time = execution_time;
    }
   
public int setRandomExecTime(Random rand)
	{
		if(period==0)
		{
			  System.err.println("Period is not set!");
			  System.exit(0);
		}
		//Random rand = new Random();
                return rand.nextInt(period);
	}

    public boolean isRestricted() {
        return isRestricted;
    }

    public void setRestricted(boolean restricted) {
        isRestricted = restricted;
    }

    public void setRam(double ram) {
        this.ram = ram;
    }

    public PeriodicTask(int index, int period, int absolute_deadline, double absolute_execution_time, int input_data_mass, int output_data_mass, Bandwidth bandwidth, int taskset_index, int numberOfdevices) {
        this.index = index;
        this.period = period;
        this.absolute_deadline = absolute_deadline;
        this.absolute_execution_time = absolute_execution_time;
        this.data_mass = input_data_mass+output_data_mass;
        this.input_data_mass = input_data_mass;
        this.output_data_mass = output_data_mass;
        this.deadline=new double[numberOfdevices];
        this.execution_time=new double[numberOfdevices];
        //this.data_status = data_status;
        double [][]bandwidthMatrix=bandwidth.GetBandwidthMatrix();
        for(int i=0;i<numberOfdevices;i++)
        {
            double bandwidth_to_deviceI=bandwidth.getBandwidth(taskset_index, i);
            if(bandwidth_to_deviceI==0)
            {
                deadline[i]=absolute_deadline;
                execution_time[i]=absolute_execution_time;
            }
            else
            {
                 deadline[i]=absolute_deadline-(output_data_mass/bandwidth.getBandwidth(taskset_index, i));
                 execution_time[i]=absolute_execution_time+(input_data_mass/bandwidth.getBandwidth(taskset_index, i));
            }
            
           
        }
    }

    
    
    public double getAbsolute_deadline() {
        return absolute_deadline;
    }

    public void setAbsolute_deadline(int absolute_deadline) {
        this.absolute_deadline = absolute_deadline;
    }

    public double[] getDeadline() {
        return deadline;
    }

    public void setDeadline(double[] deadline) {
        this.deadline = deadline;
    }


    public double getOutput_data_mass() {
        return output_data_mass;
    }

    public void setOutput_data_mass(int output_data_mass) {
        this.output_data_mass = output_data_mass;
    }
    boolean data_status;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public double getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    
    public double getDeadlineOnSpecificDevice(int deviceNumber)
    {
       
        return deadline[deviceNumber];
    }
    public double getExecutionTimeOnSpecificDevice(int deviceNumber)
    {
       
        return execution_time[deviceNumber];
    }

    public int getData_mass() {
        return data_mass;
    }

    public void setData_mass(int data_mass) {
        this.data_mass = data_mass;
    }

    public int getInput_data_mass() {
        return input_data_mass;
    }

    public void setInput_data_mass(int input_data_mass) {
        this.input_data_mass = input_data_mass;
    }

    public boolean isData_status() {
        return data_status;
    }

    public void setData_status(boolean data_status) {
        this.data_status = data_status;
    }
    
    public double getExecution_time_on_specific_device(int device_id)
    {
        return execution_time[device_id];
    }
    private double calcUtilization(int device_index)
    {
        return  ((double) execution_time[device_index]) /  period;
    }
    public double getUtilization_on_specefic_device(int device_index)
    {
	return calcUtilization(device_index);
	
    }
    
    public double getDeadlineOnDevice(int device_index)
    {
        return deadline[device_index];
    }
    public double getExecutionTimeOnDevice(int device_index)
    {
        return execution_time[device_index];
    }
    public double getMaxExecutionTimeOnFogDevices(int device_index)
    {
        double max=absolute_execution_time;
        for(int i=device_index;i<execution_time.length;i++)
        {
            if(execution_time[i]>max)
            {
                max=execution_time[i];
            }
        }
        return max;
    }

    public double getAvgExecutionTimeOnFogDevices(int device_index)
    {
        double total=0;
        for(int i=device_index;i<execution_time.length;i++)
        {
                total+=execution_time[i];
        }
        return total/(execution_time.length);
    }

    public double getLaxity(Bandwidth bandwidth,int device_index)
    {
        double laxity=period-(output_data_mass/bandwidth.getAvgBandwidthFromSpeceficDevice(device_index)+getAvgExecutionTimeOnFogDevices(device_index)+input_data_mass/bandwidth.getAvgBandwidthFromSpeceficDevice(device_index));
        return laxity;
    }

    public boolean isTimeRestricted(Bandwidth bandwidth){
        this.timeSensitive =  output_data_mass/(bandwidth.getCloudBand()/bandwidth.number_Taskof_embeded_device) + execution_time[execution_time.length-1] + offset + input_data_mass/(bandwidth.getCloudBand()/bandwidth.number_Taskof_embeded_device);
        return period < this.timeSensitive;
    }
    
}
