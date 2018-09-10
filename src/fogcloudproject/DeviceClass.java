/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fogcloudproject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 *
 * @author saeid
 */
public class DeviceClass {

    int index;
    int number_of_core;
    List<Core> core_list;
    int type_id;//em_device=0    router and switch=1    localserver=2   cloud=3
    int ram;
    public double busy = 0;

    public int getType_id() {
        return type_id;
    }
    public DeviceClass(int index, int number_of_core, List<Core>core_list,int type_id) {
        this.index = index;
        this.number_of_core = number_of_core;
        this.core_list = core_list;
        this.type_id=type_id;

//        Random rand = new Random();
//        ram = rand.nextInt(402653184) + 134217728;
    }

    public int getRam() {
        return ram;
    }

    public void setRam(int ram) {
        this.ram = ram;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getNumber_of_core() {
        return number_of_core;
    }

    public void setNumber_of_core(int number_of_core) {
        this.number_of_core = number_of_core;
    }

    public List<Core> getCore_list() {
        return core_list;
    }

    public void setCore_list(List<Core> core_list) {
        this.core_list = core_list;
    }
    public double getAvailableUtilization()
    {
        double available_utilization=0;
        for(Core core:core_list)
        {
            available_utilization += core.available_power;
        }
        return available_utilization;
    }
    public double getRealAvailableUtilization()
    {
        double available_utilization=0;
        for(Core core:core_list)
        {
            available_utilization += (core.available_power - core.getCore_busy_utilization());
        }
        return available_utilization;
    }
     public List<Core> getFreeCores()
    {
       List<Core> freeCores=core_list.stream()
               .filter(it -> it.getFreeUtilization()>0)
               .collect(Collectors.toList());
       return freeCores;
       
    }
     public double getAvailableCapacityforMprimeCores(int Mprime)
     {
         Collections.sort(core_list, new Comparator<Core>(){
        public int compare(Core core1,Core core2)
        {
            if(core1.getAvailable_power()>core2.getAvailable_power())
                return -1;
            else if(core1.getAvailable_power()==core2.getAvailable_power())
                return 0;
            return 1;
        }
        });
         double utilization=0;
         int i=0;
         while (i<Mprime&&i<number_of_core) {
             utilization+=core_list.get(i).getAvailable_power();
             i++;
         }
        
         return utilization;
     }


}
