/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fogcloudproject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author saeid
 */

public class EmbededDeviceScheduler {
    EmbededDevice embeded_device;
    PeriodicTaskSet taskSet;
    int number_of_whole_devices;
    int number_of_fog_cloud_devices;
    List<DeviceClass>devices;
    public EmbededDeviceScheduler(EmbededDevice embededDevice,PeriodicTaskSet taskset,int number_of_whole_devices,int number_of_fog_cloud_devices,List<DeviceClass>devices)
    {
        this.embeded_device=embededDevice;
        this.taskSet=taskset;
        this.number_of_whole_devices=number_of_whole_devices;
        this.number_of_fog_cloud_devices=number_of_fog_cloud_devices;
        this.devices=devices;
    }

//    public PeriodicTaskSet getTaskSet() {
//        return taskSet;
//    }
//
//    public void setTaskSet(PeriodicTaskSet taskSet) {
//        this.taskSet = taskSet;
//    }

    public PeriodicTaskSet Schedule(Bandwidth bandwidth)
    {
        List<PeriodicTask>tasksetlist=taskSet.getTaskList();
        Collections.sort(tasksetlist, new Comparator<PeriodicTask>(){
        public int compare(PeriodicTask task1,PeriodicTask task2)
        {
            if(task1.getLaxity(bandwidth,embeded_device.getIndex())>task2.getLaxity(bandwidth,embeded_device.getIndex()))
                return 1;
            else if(task1.getLaxity(bandwidth,embeded_device.getIndex())==task2.getLaxity(bandwidth,embeded_device.getIndex()))
                return 0;
            return -1;
        }
        });
        
        PeriodicTaskSet executed_taskset_on_embeded_device=new PeriodicTaskSet();
        executed_taskset_on_embeded_device.setCommunication(taskSet.getCommunication());
        
        Experiment ex=new Experiment();
        int i=0;
        VM vm=ex.VMSpecify(executed_taskset_on_embeded_device, embeded_device.getIndex(),embeded_device.getNumber_of_core());
        while(i<tasksetlist.size() && tasksetlist.get(i).isRestricted)
        {
            executed_taskset_on_embeded_device.AddTask(tasksetlist.get(i));
            vm=ex.VMSpecify(executed_taskset_on_embeded_device, embeded_device.getIndex(),embeded_device.getNumber_of_core());
            i++;
        }
        i=0;
        while(vm.getUtilization()<=embeded_device.getAvailableCapacityforMprimeCores((int)vm.getMax_required_core())&&i<tasksetlist.size())
        {
            if (tasksetlist.get(i).isRestricted){i++;continue;}
            executed_taskset_on_embeded_device.AddTask(tasksetlist.get(i));
            vm=ex.VMSpecify(executed_taskset_on_embeded_device, embeded_device.getIndex(),embeded_device.getNumber_of_core());
            i++;
        }
        
        PeriodicTaskSet outsourced_taskset=new PeriodicTaskSet();
        outsourced_taskset.setCommunication(taskSet.getCommunication());
        for(int j=i;j<tasksetlist.size();j++)
        {
            if (tasksetlist.get(j).isRestricted)continue;
            outsourced_taskset.AddTask(tasksetlist.get(j));

        }
//        VMonDevicesClass outsourced_vm=new VMonDevicesClass(embeded_device.getIndex(), number_of_whole_devices, number_of_fog_cloud_devices);
//        for(int k=number_of_whole_devices-number_of_fog_cloud_devices;k<number_of_whole_devices;k++)
//        {
//            VM vm_on_device_k=ex.VMSpecify(outsourced_taskset, k,devices.get(k).getNumber_of_core());
//            outsourced_vm.AddVMonDevice(k, vm_on_device_k);
//            outsourced_vm.AddTasksetOnDevice(k, outsourced_taskset);
//            outsourced_vm.setOutsourcedTaskset(outsourced_taskset);
//        }

        outsourced_taskset.calculateRam();
//        outsourced_taskset.initCommunication();
        return outsourced_taskset;
    }
}
