/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fogcloudproject;

/**
 *
 * @author saeid
 */
public class Bandwidth {
    int number_Taskof_embeded_device;
    int number_of_fog_device;
    double[][]bandwidth_matrix;
    public static double CloudToPrivateCloud = 125000000;
    
    public Bandwidth(int number_of_embeded_device,int number_of_fog_device)
    {
        this.number_Taskof_embeded_device=number_of_embeded_device;
        this.number_of_fog_device=number_of_fog_device;
        bandwidth_matrix=new double[number_of_embeded_device+number_of_fog_device+1][number_of_embeded_device+number_of_fog_device+1];
    }
    public double getBandwidth(int start_index,int end_index)
    {
        return bandwidth_matrix[start_index][end_index];
    }
    public void setBandwidth(int start_index,int end_index,double bandwidth)
    {
        this.bandwidth_matrix[start_index][end_index]=bandwidth;
    }
    public double[][]GetBandwidthMatrix()
    {
        return bandwidth_matrix;
    }
    public double getMinBandwidthFromSpeceficDevice(int device_index)
    {
        double min=bandwidth_matrix[device_index][0];
        for(int i=1;i<number_Taskof_embeded_device+number_of_fog_device+1;i++)
        {
            if(bandwidth_matrix[device_index][i]<min)
                min=bandwidth_matrix[device_index][i];
        }
        return min;
    }
    public static double getCloudBand(){
        return CloudToPrivateCloud;
        //return bandwidth_matrix[device][number_Taskof_embeded_device+number_of_fog_device];
    }
    public double getAvgBandwidthFromSpeceficDevice(int device_index)
    {
        // TODO: CHECK ZERO INDEX
        double total=0;
        for(int i=1;i<number_Taskof_embeded_device+number_of_fog_device+1;i++)
        {
            total+=bandwidth_matrix[device_index][i];
        }
        return total/(number_Taskof_embeded_device+number_of_fog_device+1);
    }
}
