/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fogcloudproject;

/**
 * @author saeid
 */
public class VMonDevicesClass {
    int index;
    int emb_index=-1;
    int embToSoft=0;
    int embToHard=0;
    int hardToSoft=0;
    int type = -1;

    public boolean isFirstSchedule = false;

    public int getIndex() {
        return index;
    }

    public int getSize() {
        if (taskset_on_devices[0] != null)
            return taskset_on_devices[0].getSize();
        return 0;
    }

    public int getEmb_index() {
        return emb_index;
    }

    public void setEmb_index(int emb_index) {
        this.emb_index = emb_index;
    }

    public int getEmbToSoft() {
        return embToSoft;
    }

    public void setEmbToSoft(int embToSoft) {
        this.embToSoft = embToSoft;
    }

    public int getEmbToHard() {
        return embToHard;
    }

    public void setEmbToHard(int embToHard) {
        this.embToHard = embToHard;
    }

    public int getHardToSoft() {
        return hardToSoft;
    }

    public void setHardToSoft(int hardToSoft) {
        this.hardToSoft = hardToSoft;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    int number_of_fog_cloud_devices;
    int number_of_whole_devices;
    VM[] vm_on_devices;
    PeriodicTaskSet[] taskset_on_devices;
    PeriodicTaskSet outsourcedTaskSet;

    public VMonDevicesClass(int index, int number_of_whole_devices, int number_of_fog_cloud_devices) {
        this.index = index;
        this.number_of_fog_cloud_devices = number_of_fog_cloud_devices;
        this.number_of_whole_devices = number_of_whole_devices;
        this.vm_on_devices = new VM[number_of_fog_cloud_devices];
        this.taskset_on_devices = new PeriodicTaskSet[number_of_fog_cloud_devices];
    }

    public void AddVMonDevice(int device_index, VM vm) {
        int index_in_array = device_index - (number_of_whole_devices - number_of_fog_cloud_devices);
        this.vm_on_devices[index_in_array] = vm;
    }

    public void AddTasksetOnDevice(int device_index, PeriodicTaskSet ts) {
        int index_in_array = device_index - (number_of_whole_devices - number_of_fog_cloud_devices);
        this.taskset_on_devices[index_in_array] = ts;
    }

    public VM get_vm_in_specific_device(int device_index) {
        int index_in_array = device_index - (number_of_whole_devices - number_of_fog_cloud_devices);
        return vm_on_devices[index_in_array];
    }

    public PeriodicTaskSet getOutsourcedTaskset() {
        return outsourcedTaskSet;
    }

    public void setOutsourcedTaskset(PeriodicTaskSet ts) {
        this.outsourcedTaskSet = ts;
    }

    public PeriodicTaskSet get_ts_in_specific_device(int device_index) {
        int index_in_array = device_index - (number_of_whole_devices - number_of_fog_cloud_devices);
        return taskset_on_devices[index_in_array];
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType(){
        return type;
    }
}
