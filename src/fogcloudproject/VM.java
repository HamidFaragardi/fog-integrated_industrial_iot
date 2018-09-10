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
public class VM {

    int index;
    int period;
    double utilization;
    double max_required_core;
    int data_mass;
    int inpud_data_mass;
    int device_index;
    boolean is_schedulable;
    public int ram;


    public int getData_mass() {
        return data_mass;
    }
    public boolean getIs_schedulable() {
        return is_schedulable;
    }
    public double getMax_required_core() {
        return max_required_core;
    }
    
    public int getPeriod() {
        return period;
    }

    public int getDevice_index() {
        return device_index;
    }

    public void setDevice_index(int device_index) {
        this.device_index = device_index;
    }

    public VM(int index, int period, double utilization, double max_required_core, int data_mass, int inpud_data_mass,int device_index,boolean  is_schedulable) {
        this.index = index;
        this.period = period;
        this.utilization = utilization;
        this.max_required_core = max_required_core;
        this.data_mass = data_mass;
        this.inpud_data_mass = inpud_data_mass;
        this.device_index=device_index;
        this.is_schedulable=is_schedulable;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public void setUtilization(double utilization) {
        this.utilization = utilization;
    }

    public void setMax_required_core(double max_required_core) {
        this.max_required_core = max_required_core;
    }

    public void setData_mass(int data_mass) {
        this.data_mass = data_mass;
    }

    public void setInpud_data_mass(int inpud_data_mass) {
        this.inpud_data_mass = inpud_data_mass;
    }

    public double getUtilization()
    {
        return utilization;
    }
    
}
