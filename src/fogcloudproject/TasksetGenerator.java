/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fogcloudproject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author saeid
 */
public class TasksetGenerator {
    
    
    private List<PeriodicTaskSet>tasksets=new ArrayList<PeriodicTaskSet>();
    int new_task_index=0;
    int new_ts_index=0;
    Bandwidth bandwidth;
    int number_of_devices;
    public  TasksetGenerator(int number_of_devices,Bandwidth bandwidth)
    {
        this.number_of_devices=number_of_devices;
        this.bandwidth=bandwidth;
    }
    public void GenerateTaskSet(int numberOfTasks)
    {
        PeriodicTaskSet new_ts=new PeriodicTaskSet();
        new_ts.setIndex(new_ts_index);
        Random random=new Random();
        
        int number_of_class_1=(int)(numberOfTasks*0.03);
        int last_index=new_task_index+number_of_class_1;
        for(int i=new_task_index;i<last_index;i++)
        {   
            int data_mass=(int)(random.nextDouble()*40)*8;
            int input_data=(int)(random.nextDouble()*data_mass);
            int output_data=data_mass-input_data;
            PeriodicTask new_task=new PeriodicTask(i,2,2,(random.nextDouble()*29.77+0.34)/1000,input_data,output_data,bandwidth,new_ts_index,number_of_devices);
            new_ts.AddTask(new_task);
            new_task_index++;
        }
        
        int number_of_class_2=(int)(numberOfTasks*0.15);
        //int number_of_class_2=(int)(numberOfTasks*0.40);
        last_index=new_task_index+number_of_class_2;
        for(int i=new_task_index;i<last_index;i++)
        {   
            int data_mass=(int)(random.nextDouble()*1348+812)*8;
            int input_data=(int)(random.nextDouble()*data_mass);
            int output_data=data_mass-input_data;
            PeriodicTask new_task=new PeriodicTask(i,2,2,(random.nextDouble()*88.13+0.45)/1000,input_data,output_data,bandwidth,new_ts_index,number_of_devices);
            new_ts.AddTask(new_task);
            new_task_index++;
        }
        
        
        
        int number_of_class_4=(int)(numberOfTasks*0.02);
        //int number_of_class_4=(int)(numberOfTasks*0.15);
        last_index=new_task_index+number_of_class_4;
        for(int i=new_task_index;i<last_index;i++)
        {   
            int data_mass=(int)(random.nextDouble()*80+240)*8;
            int input_data=(int)(random.nextDouble()*data_mass);
            int output_data=data_mass-input_data;
            PeriodicTask new_task=new PeriodicTask(i,5,5,(random.nextDouble()*83.02+0.36)/1000,input_data,output_data,bandwidth,new_ts_index,number_of_devices);
            new_ts.AddTask(new_task);
            new_task_index++;
        }
        
        int number_of_class_5=(int)(numberOfTasks*0.15);
        last_index=new_task_index+number_of_class_5;
        for(int i=new_task_index;i<last_index;i++)
        {   
            int data_mass=(int)(random.nextDouble()*449+1401)*8;
            int input_data=(int)(random.nextDouble()*data_mass);
            int output_data=data_mass-input_data;
            PeriodicTask new_task=new PeriodicTask(i,10,10,(random.nextDouble()*309.66+0.21)/1000,input_data,output_data,bandwidth,new_ts_index,number_of_devices);
            new_ts.AddTask(new_task);
            new_task_index++;
        }
        
        int number_of_class_6=(int)(numberOfTasks*0.25);
        last_index=new_task_index+number_of_class_6;
        for(int i=new_task_index;i<last_index;i++)
        {   
            int data_mass=(int)(random.nextDouble()*1280+1360)*8;
            int input_data=(int)(random.nextDouble()*data_mass);
            int output_data=data_mass-input_data;
            PeriodicTask new_task=new PeriodicTask(i,20,20,(random.nextDouble()*291.17+0.25)/1000,input_data,output_data,bandwidth,new_ts_index,number_of_devices);
            new_ts.AddTask(new_task);
            new_task_index++;
        }
        int number_of_class_7=(int)(numberOfTasks*0.03);
        last_index=new_task_index+number_of_class_7;
        for(int i=new_task_index;i<last_index;i++)
        {   
            int data_mass=(int)(random.nextDouble()*269+91)*8;
            int input_data=(int)(random.nextDouble()*data_mass);
            int output_data=data_mass-input_data;
            PeriodicTask new_task=new PeriodicTask(i,50,50,(random.nextDouble()*92.69+0.29)/1000,input_data,output_data,bandwidth,new_ts_index,number_of_devices);
            new_ts.AddTask(new_task);
            new_task_index++;
        }
        int number_of_class_8=(int)(numberOfTasks*0.20);
        last_index=new_task_index+number_of_class_8;
        for(int i=new_task_index;i<last_index;i++)
        {   
            int data_mass=(int)(random.nextDouble()*1408+1792)*8;
            int input_data=(int)(random.nextDouble()*data_mass);
            int output_data=data_mass-input_data;
            PeriodicTask new_task=new PeriodicTask(i,100,100,(random.nextDouble()*420.22+0.21)/1000,input_data,output_data,bandwidth,new_ts_index,number_of_devices);
            new_ts.AddTask(new_task);
            new_task_index++;
        }
        int number_of_class_9=(int)(numberOfTasks*0.01);
        last_index=new_task_index+number_of_class_9;
        for(int i=new_task_index;i<last_index;i++)
        {   
            int data_mass=(int)(random.nextDouble()*10+40)*8;
            int input_data=(int)(random.nextDouble()*data_mass);
            int output_data=data_mass-input_data;
            PeriodicTask new_task=new PeriodicTask(i,200,200,(random.nextDouble()*21.73+0.22)/1000,input_data,output_data,bandwidth,new_ts_index,number_of_devices);
            new_ts.AddTask(new_task);
            new_task_index++;
        }
        int number_of_class_10=numberOfTasks-(number_of_class_1+number_of_class_2+number_of_class_4+number_of_class_5+number_of_class_6+number_of_class_7+number_of_class_8+number_of_class_9);
        last_index=new_task_index+number_of_class_10;
        for(int i=new_task_index;i<last_index;i++)
        {   
            int data_mass=(int)(random.nextDouble()*538+232)*8;
            int input_data=(int)(random.nextDouble()*data_mass);
            int output_data=data_mass-input_data;
            PeriodicTask new_task=new PeriodicTask(i,1000,100,(random.nextDouble()*0.09+0.37)/1000,input_data,output_data,bandwidth,new_ts_index,number_of_devices);
            new_ts.AddTask(new_task);
            new_task_index++;
        }

        tasksets.add(new_ts);
        new_ts_index++;
    }
    public void CreateHardTaskset(int numberOfTasks)
    {
        PeriodicTaskSet new_ts=new PeriodicTaskSet();
        new_ts.setIndex(new_ts_index);
        int number_of_class_3=(int)(numberOfTasks);
        //int number_of_class_3=(int)(numberOfTasks);
        int last_index=new_task_index+number_of_class_3;
        Random random=new Random();
        for(int i=new_task_index;i<last_index;i++)
        {   
            int data_mass=(int)((random.nextDouble())*20000+200000);
            int input_data=data_mass;//(int)(random.nextDouble()*data_mass);
            int output_data=0;//data_mass-input_data;
            PeriodicTask new_task=new PeriodicTask(i,5,5,1/*(random.nextDouble()*10.37+0.32)/100*/,input_data,output_data,bandwidth,new_ts_index,number_of_devices);
            new_ts.AddTask(new_task);
            new_task_index++;
        }
        tasksets.add(new_ts);
        new_ts_index++;
        
    }
    public List<PeriodicTaskSet>getTasksets()
    {
        return tasksets;
    }
}
