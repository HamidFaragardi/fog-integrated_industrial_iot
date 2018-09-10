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
public class Core {

    
    int index;
    int device_index;
    double available_power;
    private double core_busy_utilization;
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getDevice_index() {
        return device_index;
    }

    public void setDevice_index(int device_index) {
        this.device_index = device_index;
    }

    public double getAvailable_power() {
        return available_power;
    }

    public void setAvailable_power(double available_power) {
        this.available_power = available_power;
    }

    
    public Core(int index, int device_index,double available_power,double core_busy_utilization) {
        this.index = index;
        this.device_index=device_index;
        this.available_power = available_power;
        this.core_busy_utilization=core_busy_utilization;
    }

    public double getCore_busy_utilization() {
        return core_busy_utilization;
    }

    public void setCore_busy_utilization(double core_busy_utilization) {
        this.core_busy_utilization = core_busy_utilization;
    }
    
    public double getFreeUtilization()
    {
        return (1-core_busy_utilization);
    }
    
}
