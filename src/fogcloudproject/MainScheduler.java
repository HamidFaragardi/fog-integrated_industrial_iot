/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fogcloudproject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author saeid
 */
public class MainScheduler {
    
    List<DeviceClass> devices;
    List<PeriodicTaskSet>tasksets;
    int number_of_embeded_devices;
    int number_of_whole_devices;
    int number_of_local_servers;
    Bandwidth bandwidth;

    public MainScheduler(List<DeviceClass> devices, List<PeriodicTaskSet> tasksets,int number_of_whole_devices, int number_of_embeded_devices, Bandwidth bandwidth,int number_of_local_servers) {
        this.devices = devices;
        this.tasksets = tasksets;
        this.number_of_whole_devices=number_of_whole_devices;
        this.number_of_embeded_devices = number_of_embeded_devices;
        this.bandwidth = bandwidth;
        this.number_of_local_servers=number_of_local_servers;
    }
    
    
    
    /*public RP VMsScheduler(int iterationCount,double uFactor,double initialTemperature,double finalTemperature,double alpha,double beta,double evaporationFactor) throws InterruptedException
    {
        
        ExecutorService es=Executors.newCachedThreadPool();
        SA[]threads=new SA[4];
       
        for(int i=0;i<4;i++)
        {
            RP newRRP;
          
           newRRP=new RP(vms.size(), servers, vms,(new RP()).CreateRandomAssignment(servers, vms));
         
          threads[i]=new SA(i, uFactor, initialTemperature, finalTemperature,newRRP , alpha, beta);
          
           es.execute(threads[i]);
        }

        es.shutdown();
        es.awaitTermination(1, TimeUnit.DAYS);
      

        List<RP>returnedRPs=new ArrayList<>();
        for(int i=0;i<threads.length;i++)
        {
            returnedRPs.add(threads[i].getBestRP());
        }
        RP bestRP=returnedRPs.stream().min(new Comparator<RP>(){
            public int compare(RP rp1,RP rp2)
            {
                if(rp1.Fitness(alpha,beta)>rp2.Fitness(alpha, beta))
                    return 1;
                else if(rp1.Fitness(alpha,beta)==rp2.Fitness(alpha, beta))
                    return 0;
                return -1;
                
            }
        }).get();
       
      
      
        double [][]PH=new double[servers.size()][vms.size()];
        for(int m=0;m<servers.size();m++)
                for(int n=0;n<vms.size();n++)
                {
                    PH[m][n]=0;
                }
        double [][]prob=new double[servers.size()][vms.size()];
        double uufactor=uFactor;
        for(int iteration=0;iteration<iterationCount;iteration++)
        {
             double []sumColumnPH=new double[servers.size()];
             Arrays.fill(sumColumnPH, 0);
            for(int m=0;m<servers.size();m++)
                for(int n=0;n<vms.size();n++)
                {
                    PH[m][n]=evaporationFactor*PH[m][n]+(bestRP.getRP()[n]==m?1:0)/bestRP.Fitness(alpha, beta);
                    sumColumnPH[m]+=PH[m][n];
                }
    
            for(int m=0;m<servers.size();m++)
                for(int n=0;n<vms.size();n++)
                {
                    prob[m][n]=PH[m][n]/sumColumnPH[m];
                }
        
         es=Executors.newCachedThreadPool();
        threads=new SA[4];
        
        uufactor=uufactor*1;
        for(int i=0;i<4;i++)
        {
            RP newRRP=createRPbasedProbability(prob);
            threads[i]=new SA(i, uufactor, initialTemperature, finalTemperature,newRRP , alpha, beta);

           es.execute(threads[i]);
           
        }
        es.shutdown();
        es.awaitTermination(1, TimeUnit.DAYS);
        returnedRPs=new ArrayList<>();
        for(int i=0;i<threads.length;i++)
        {
            returnedRPs.add(threads[i].getBestRP());
        }
        returnedRPs.add(bestRP);
        //bestRP=returnedRPs.stream().filter(item->item.FailureDegree(beta)==0).collect(Collectors.toList()).get(0);
        bestRP=returnedRPs.stream().min(new Comparator<RP>(){
            public int compare(RP rp1,RP rp2)
            {
                 if(rp1.Fitness(alpha,beta)>rp2.Fitness(alpha, beta))
                    return 1;
                else if(rp1.Fitness(alpha,beta)==rp2.Fitness(alpha, beta))
                    return 0;
                return -1;
              
            }
        }).get();
            System.out.println("iteration:"+iteration);
            System.out.println("Cooling Factor:"+uufactor);
            System.out.println(bestRP.Fitness(alpha, beta));
            System.out.println(bestRP.TP());
        }
        
        return bestRP;
    }
*/
    /*
    public RP createRPbasedProbability(double [][] probMatrix)
    {
        List[]probrand=new List[vms.size()];
        
        for(int n=0;n<vms.size();n++)
        {
            probrand[n]=new ArrayList();
            for(int m=0;m<servers.size();m++)
            {
                for(int k=0;k<probMatrix[m][n]*100;k++)
                    probrand[n].add(m);
            }
        }
        int []replacement=new int[vms.size()];
        Random rand=new Random();
        for(int n=0;n<vms.size();n++)
        {
           replacement[n]=(Integer)probrand[n].get(rand.nextInt(probrand[n].size()));
        }
        RP newRP=new RP(vms.size(), servers, vms, replacement);
        return newRP;
    }
*/
    public VMArray Schedule()
    {
        List<VMonDevicesClass>outsourced_hard_vms=new ArrayList<>();
        List<VMonDevicesClass>outsourced_soft_vms=new ArrayList<>();
        int count = 0;
        for(int i=0;i<number_of_embeded_devices;i++)
        {
            EmbededDeviceScheduler em_scheduler=new EmbededDeviceScheduler((EmbededDevice)devices.get(i), tasksets.get(i), number_of_whole_devices, number_of_whole_devices-number_of_embeded_devices,devices);
//            em_scheduler.setTaskSet(tasksets.get(i));
            PeriodicTaskSet outsourced_taskset=em_scheduler.Schedule(bandwidth);
            if (outsourced_taskset.getSize()==0) {count++; continue;}
            boolean flag_has_timeStrict = false;
            for (int j = 0; j < outsourced_taskset.getSize(); j++) {
               if (outsourced_taskset.getTask(j).isTimeRestricted(bandwidth)){
                   flag_has_timeStrict = true;
                   break;
               }
            }
//            if(outsourced_vm.getSize()!=0)
//            {
//                if(outsourced_vm.get_vm_in_specific_device(number_of_whole_devices-1).getIs_schedulable())
//                {
//                    outsourced_soft_vms.add(outsourced_vm);
//                }
//                else
//                {
//                    outsourced_hard_vms.add(outsourced_vm);
//                }
//            }
            Experiment ex = new Experiment();
            if (flag_has_timeStrict){
                PeriodicTaskSet outsourced_soft=new PeriodicTaskSet();
                outsourced_soft.setCommunication(outsourced_taskset.getCommunication());
                PeriodicTaskSet outsourced_hard=new PeriodicTaskSet();
                outsourced_hard.setCommunication(outsourced_taskset.getCommunication());
                for (int j = 0; j < outsourced_taskset.getSize(); j++) {
                    if (outsourced_taskset.getTask(j).isTimeRestricted(bandwidth)){
                        outsourced_hard.AddTask(outsourced_taskset.getTask(j));
                    }
                    else {
                        outsourced_soft.AddTask(outsourced_taskset.getTask(j));
                    }
                }
                VMonDevicesClass outsourced_vm_soft=new VMonDevicesClass(devices.get(i).index, number_of_whole_devices, number_of_whole_devices-number_of_embeded_devices);
                for(int k=number_of_embeded_devices;k<number_of_whole_devices;k++)
                {
                    VM vm_on_device_k=ex.VMSpecify(outsourced_soft, k,devices.get(k).getNumber_of_core());
                    outsourced_vm_soft.AddVMonDevice(k, vm_on_device_k);
                    outsourced_vm_soft.AddTasksetOnDevice(k, outsourced_soft);
                    outsourced_vm_soft.setOutsourcedTaskset(outsourced_soft);
                }
                outsourced_vm_soft.setEmb_index(em_scheduler.embeded_device.index);
                int embToSoft = 0;
                for (PeriodicTask periodicTaskS:em_scheduler.taskSet.getTaskList()) {
                    for (PeriodicTask periodicTaskD:outsourced_soft.getTaskList()) {
                        embToSoft += outsourced_soft.getCommunication()[periodicTaskS.index][periodicTaskD.index];
                    }
                }
                outsourced_vm_soft.getOutsourcedTaskset().calculateRam();
                System.out.println("Application " + i + " Soft Vm with Util "+ outsourced_vm_soft.getOutsourcedTaskset().getUtilization_on_specefic_Device(0) + " and "+outsourced_vm_soft.getOutsourcedTaskset().getTotal_ram()+" Ram");
                outsourced_soft_vms.add(outsourced_vm_soft);
                VMonDevicesClass outsourced_vm_hard=new VMonDevicesClass(devices.get(i).index, number_of_whole_devices, number_of_whole_devices-number_of_embeded_devices);
                for(int k=number_of_embeded_devices;k<number_of_whole_devices;k++)
                {
                    VM vm_on_device_k=ex.VMSpecify(outsourced_hard, k,devices.get(k).getNumber_of_core());
                    outsourced_vm_hard.AddVMonDevice(k, vm_on_device_k);
                    outsourced_vm_hard.AddTasksetOnDevice(k, outsourced_hard);
                    outsourced_vm_hard.setOutsourcedTaskset(outsourced_hard);
                }
                int embToHard = 0;
                for (PeriodicTask periodicTaskS:em_scheduler.taskSet.getTaskList()) {
                    for (PeriodicTask periodicTaskD:outsourced_hard.getTaskList()) {
                        embToHard += outsourced_hard.getCommunication()[periodicTaskS.index][periodicTaskD.index];
                    }
                }

                int softToHard = 0;
                for (PeriodicTask periodicTaskS:outsourced_hard.getTaskList()) {
                    for (PeriodicTask periodicTaskD:outsourced_soft.getTaskList()) {
                        softToHard += outsourced_soft.getCommunication()[periodicTaskS.index][periodicTaskD.index];
                    }
                }

                outsourced_vm_soft.setEmbToSoft(embToSoft);
                outsourced_vm_soft.setEmb_index(em_scheduler.embeded_device.index);
                outsourced_vm_soft.setEmbToHard(embToHard);
                outsourced_vm_soft.setHardToSoft(softToHard);
                outsourced_vm_soft.setType(0);
                outsourced_vm_hard.setEmbToSoft(embToSoft);
                outsourced_vm_hard.setEmb_index(em_scheduler.embeded_device.index);
                outsourced_vm_hard.setEmbToHard(embToHard);
                outsourced_vm_hard.setHardToSoft(softToHard);
                outsourced_vm_hard.setType(1);

                outsourced_vm_hard.getOutsourcedTaskset().calculateRam();
                System.out.println("Application " + i + " Hard Vm with Util "+ outsourced_vm_hard.getOutsourcedTaskset().getUtilization_on_specefic_Device(0) + " and "+outsourced_vm_hard.getOutsourcedTaskset().getTotal_ram()+" Ram");
                outsourced_hard_vms.add(outsourced_vm_hard);

            } else {
                VMonDevicesClass outsourced_vm_soft=new VMonDevicesClass(devices.get(i).index, number_of_whole_devices, number_of_whole_devices-number_of_embeded_devices);
                for(int k=number_of_embeded_devices;k<number_of_whole_devices;k++)
                {
                    VM vm_on_device_k=ex.VMSpecify(outsourced_taskset, k,devices.get(k).getNumber_of_core());
                    outsourced_vm_soft.AddVMonDevice(k, vm_on_device_k);
                    outsourced_vm_soft.AddTasksetOnDevice(k, outsourced_taskset);
                    outsourced_vm_soft.setOutsourcedTaskset(outsourced_taskset);
                }
                outsourced_vm_soft.setEmb_index(em_scheduler.embeded_device.index);
                int embToSoft = 0;
                for (PeriodicTask periodicTaskS:em_scheduler.taskSet.getTaskList()) {
                    for (PeriodicTask periodicTaskD:outsourced_taskset.getTaskList()) {
                        embToSoft += tasksets.get(em_scheduler.embeded_device.index).getCommunication()[periodicTaskS.index][periodicTaskD.index];
                    }
                }
                outsourced_vm_soft.setEmbToSoft(embToSoft);
                outsourced_vm_soft.setType(0);

                outsourced_vm_soft.getOutsourcedTaskset().calculateRam();
                System.out.println("Application " + i + " SOFT Vm with Util "+ outsourced_vm_soft.getOutsourcedTaskset().getUtilization_on_specefic_Device(0) + " and "+outsourced_vm_soft.getOutsourcedTaskset().getTotal_ram()+" Ram");

                outsourced_soft_vms.add(outsourced_vm_soft);
            }
            System.out.println("Embeded Device "+i+" Scheduled...");
            System.out.println(outsourced_taskset.getSize()+" tasks outsourced");
        }
        System.out.println("Embeded Devices Scheduled...");
        System.out.println("Application Just On Embeded Devices --> " + count);
        System.out.println("Application Outsourced tasks --> "+(number_of_embeded_devices-count));
//        RP final_rp=new RP(devices,outsourced_hard_vms,outsourced_soft_vms,number_of_local_servers,60000000,number_of_embeded_devices);
//        final_rp.Schedule();
//        return final_rp;
        return new VMArray(outsourced_hard_vms,outsourced_soft_vms);
    }
}
