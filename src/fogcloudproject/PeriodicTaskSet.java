/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fogcloudproject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author saeid
 */
public class PeriodicTaskSet implements Cloneable {
    /**
     * @param args
     */
    private int minPeriod;
    private int deltaPeriod;
    private long index;
    private double utilization;
    private int total_ram = 0;
    private List<PeriodicTask> taskList;
    private int[][] communication;
    private double timeSensitive;

    public double getTimeSensitive() {
        return timeSensitive;
    }

    public void setTimeSensitive(double timeSensitive) {
        this.timeSensitive = timeSensitive;
    }

    public int getTotal_ram() {
        return total_ram;
    }

    public List<PeriodicTask> getTaskList() {
        return taskList;
    }

    public PeriodicTaskSet() {
        taskList = new ArrayList<PeriodicTask>();
    }

    public PeriodicTask getTask(int index) {
        if (index > taskList.size() - 1) {
            System.err.println("indx out of range!");
            System.out.printf("index %d \n", index);
            return null;
        }
        return taskList.get(index);
    }

    public void AddTask(PeriodicTask task) {
        taskList.add(task);
    }

    public void initCommunication(){
        communication = new int[taskList.size()][taskList.size()];
//        Random random = new Random();
//        for (int i = 0; i <taskList.size(); i++) {
//            for (int j = 0; j < taskList.size(); j++) {
//                int r = random.nextInt(2000) + 500;
//                communication[i][j] = r;
//                communication[j][i] = r;
//            }
//        }
////        for (int i = 0; i < number_of_embeded_devices; i++) {
//            try {
//                String filename = "GeneratedTestCase/ApplicationCom/" + index + ".txt";
//                FileWriter filewriter = new FileWriter(filename);
//                BufferedWriter bufferwriter = new BufferedWriter(filewriter);
//
//                for (int k = 0; k < communication.length; k++) {
//                    String line = "";
//                    for (int i = 0; i <communication.length; i++) {
//                         line += communication[k][i] + ",";
//                    }
//                    line = line.substring(0,line.length()-1);
//                    bufferwriter.write(line);
//                    bufferwriter.newLine();
//                }
//                bufferwriter.close();
//            } catch (Exception ex2) {
//                System.out.println("error in generating task sets");
//            }

        try {
            String filename = "GeneratedTestCase/ApplicationCom/" + index + ".txt";
            FileReader filereader = new FileReader(filename);
            BufferedReader bufferreader = new BufferedReader(filereader);
            String comm = null;
            PeriodicTaskSet generated_taskset = new PeriodicTaskSet();
            int i = 0;
            while ((comm = bufferreader.readLine()) != null) {
                String[] comm_splitted = comm.split(",");
                for (int j = 0; j < comm_splitted.length; j++) {
                    communication[i][j] = Integer.parseInt(comm_splitted[j]);
                }
                i++;
            }
        } catch (Exception ex2) {
            System.out.println("error in generating COM");
        }

    }

    public int[][] getCommunication() {
        return communication;
    }

    public void RemoveTask(int taskIndex) {
        taskList.remove(taskIndex);
    }

    public void CreateRandomTaskSet(int count, Bandwidth bandwidth) {
        if (minPeriod <= 0 || deltaPeriod <= 0) {
            System.err.println("minPeriod or deltaPeriod is not set!");
            return;
        }
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            int period = rand.nextInt((minPeriod + deltaPeriod - minPeriod) + 1) + minPeriod;
            int data_mass;
            int d_class = (new Random()).nextInt(3);
            if (d_class == 0) {
                data_mass = (int) (rand.nextDouble() * 1280 + 1360) * 70;
            } else if (d_class == 1) {
                data_mass = (int) (rand.nextDouble() * 1280 + 1360) * 80;
            } else {
                data_mass = (int) (rand.nextDouble() * 1280 + 1360) * 90;
            }


            int input_data = (int) (rand.nextDouble() * data_mass);
            int output_data = data_mass - input_data;
            PeriodicTask task = new PeriodicTask(i, period, period, rand.nextInt(period / 5 - 1), input_data, output_data, bandwidth, (int) this.getIndex(), bandwidth.bandwidth_matrix[0].length);
            int deadline = task.period;
            task.setRam(rand.nextInt(1047552) + 1024);
            //task.setRandomExecTime(rand);
            task.index = i;
            this.AddTask(task);
            //task.setDataMass((int)Math.random()*100+15);
        }
    }

    /*public void CreateRandomTaskSet(double setUtil, double maxTaskUtil)
    {
        if(minPeriod<=0 || deltaPeriod <= 0)
        {
              System.err.println("minPeriod or deltaPeriod is not set!");
              return;
        }
        Random rand = new Random();
        double Ur = setUtil;
        int i = 0;
        while (Ur > maxTaskUtil)
        {
            PeriodicTask task = new PeriodicTask();
            double util = rand.nextDouble()*maxTaskUtil;
            Ur -= util;
            task.period = rand.nextInt((deltaPeriod) + 1) + minPeriod;
            task.period = task.period - task.period % 10; // resolution of timer is set to 10
            task.deadline = task.period;
            task.executionTime = (int) (util*task.period);
            task.index = i;
                        task.setDataMass((int)Math.random()*100+15);
                        i++;

            this.AddTask(task);
        }
        //Last task
        PeriodicTask task = new PeriodicTask();
        double util = Ur*maxTaskUtil;
        task.period = rand.nextInt((deltaPeriod) + 1) + minPeriod;
        task.period = task.period - task.period % 10; // resolution of timer is set to 10
        task.deadline = task.period;
        task.executionTime = (int) (util*task.period);
        task.index = i; i++;
        this.AddTask(task);
    }
        */
	/*public void CreateSampleTaskSet(int countSmall, int countBig)
	{
		for(int i=0;i<countSmall;i++)
		{
			PeriodicTask task = new PeriodicTask();
			task.period = 100; 
			task.deadline = task.period;
			task.executionTime = 13;
			task.index = i;
			this.AddTask(task);
		}
		for(int i=0;i<countBig;i++)
		{
			PeriodicTask task = new PeriodicTask();
			task.period = 100; 
			task.deadline = task.period;
			task.executionTime = 31;
			task.index = i;
			this.AddTask(task);
		}
	}
	*/
    public void setPeriodRange(int min, int delta) {
        minPeriod = min;
        deltaPeriod = delta;
    }

    private void calcUtilization(int device_index) {
        utilization = 0;
        for (int i = 0; i < this.getSize(); i++) {
            utilization += taskList.get(i).getUtilization_on_specefic_device(device_index);
        }
    }


    public double getUtilization_on_specefic_Device(int device_index) {
        calcUtilization(device_index);
        return utilization;
    }

    public int getSize() {
        return taskList.size();
    }

    public void PrintTaskSet(int device_index) {
        System.out.printf("------------------------------------------------------------ \n");
        for (int i = 0; i < this.getSize(); i++) {
            PeriodicTask task = taskList.get(i);
            System.out.printf("-- task[%d]: period %d executionTime %d U:%.2f \n", task.index, task.period, task.getExecution_time_on_specific_device(device_index), task.getUtilization_on_specefic_device(device_index));
        }
        System.out.printf("------------------------------------------------------------ \n");
    }


    public double[] getAllExecTimes_on_specific_device(int device_index) {
        double[] execTimes = new double[this.getSize()];
        for (int i = 0; i < this.getSize(); i++)
            execTimes[i] = taskList.get(i).getExecution_time_on_specific_device(device_index);
        return execTimes;
    }

    public long[] getAllPeriods() {
        long[] periods = new long[this.getSize()];
        for (int i = 0; i < this.getSize(); i++)
            periods[i] = taskList.get(i).period;
        return periods;
    }

    public double getMeanPeriods() {
        double sum = 0;
        for (int i = 0; i < this.getSize(); i++)
            sum += taskList.get(i).period;
        return sum / this.getSize();
    }

    public int getMinPeriods() {
        int min = taskList.get(0).period;
        for (int i = 0; i < this.getSize(); i++) {
            int tmp = taskList.get(i).period;
            if (tmp < min)
                min = tmp;
        }
        return min;
    }

    public double getStdPeriods() {
        double mean = this.getMeanPeriods();
        double sum = 0;
        double[] array = new double[this.getSize()];
        for (int i = 0; i < this.getSize(); i++) {
            array[i] = Math.pow((taskList.get(i).period - mean), 2);
            sum += array[i];
        }
        return Math.sqrt(sum / this.getSize());
    }

    private static long gcd(long a, long b) {
        while (b > 0) {
            long temp = b;
            b = a % b; // % is remainder
            a = temp;
        }
        return a;
    }

    private static long gcd(long[] input) {
        long result = input[0];
        for (int i = 1; i < input.length; i++) result = gcd(result, input[i]);
        return result;
    }


    private static long lcm(long a, long b) {
        return a * (b / gcd(a, b));
    }

    private static long lcm(long[] input) {
        long result = input[0];
        for (int i = 1; i < input.length; i++) result = lcm(result, input[i]);
        return result;
    }

    public long getLCMOfPeriods() {
        long LCM = 0;
        LCM = lcm(getAllPeriods());
        return LCM;
    }

    public void SortTasksUtilization(int device_index) {
        int swapped = 1;
        while (swapped == 1) {
            swapped = 0;
            for (int i = 1; i < this.getSize(); i++) {
                if (taskList.get(i - 1).getUtilization_on_specefic_device(device_index) < taskList.get(i).getUtilization_on_specefic_device(device_index)) {
                    swapTasks(i - 1, i);
                    swapped = 1;
                }
            }
        }
    }

    private void swapTasks(int A, int B) {
        Collections.swap(taskList, A, B);
		/*Task tmp = new Task();
		tmp = tasks[A];
		tasks[A] = tasks[B];
		tasks[B] = tmp;*/
    }

    public void setIndex(long indx) {
        this.index = indx;
    }

    public long getIndex() {
        return this.index;
    }

    public int getDataMass() {
        int data_mass = 0;
        for (PeriodicTask task : taskList) {
            data_mass += task.getData_mass();
        }
        return data_mass;
    }

    public boolean Is_Schedulable(int device_index) {
        boolean is_schedulable = true;
        for (PeriodicTask task : taskList) {
            if (task.getExecution_time_on_specific_device(device_index) > task.getDeadlineOnSpecificDevice(device_index)) {
                is_schedulable = false;
                break;
            }
        }
        return is_schedulable;
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void calculateRam() {
        for (PeriodicTask pt : getTaskList()) {
            total_ram += pt.getRam();
        }
    }

    public void setCommunication(int[][] communication) {
        this.communication = communication;
    }
}
