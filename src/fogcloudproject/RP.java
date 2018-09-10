/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fogcloudproject;

import java.sql.Time;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.stream.IntStream;
/**
 *this class is declared for Replacement Presentation of VMs on Servers
 * @author saeid
 */
public class RP implements Cloneable{
    int index;
    private int vm_count;
    private int[]replacement;
    private int[]replacement_type;
    private List<DeviceClass>devices;
    private List<VMonDevicesClass>outsourced_hard_vms;
    private List<VMonDevicesClass>outsourced_soft_vms;
    List<PeriodicTaskSet> taskset;
    int number_of_localservers;
    double max_data_on_cloud_link;
    int number_of_em_devices;
    PeriodicTaskSet[]tasksets_on_devices;
    double []device_utilization;
    int number_of_used_local_servers=0;
    public RP()
    {
        
    }
    public RP(int vm_count)
    {
        replacement=new int[vm_count];
       
    }
    public int[]getDeviceTypeArray()
    {
        return replacement_type;
    }
    public RP(List<DeviceClass>deviceClasses,List<VMonDevicesClass>outsourced_hard_vms,List<VMonDevicesClass>outsourced_soft_vms,int number_of_local_servers,double max_data_on_cloud_link,int number_of_em_devices)
    {
        this.devices=deviceClasses;
        this.outsourced_hard_vms=outsourced_hard_vms;
        this.outsourced_soft_vms=outsourced_soft_vms;
        this.replacement=new int[number_of_em_devices];
        this.replacement_type=new int[number_of_em_devices];
        this.number_of_localservers=number_of_local_servers;
        this.max_data_on_cloud_link=max_data_on_cloud_link;
        this.number_of_em_devices=number_of_em_devices;
        this.tasksets_on_devices=new PeriodicTaskSet[devices.size()];
        this.device_utilization=new double[devices.size()];
    }
    /*public RP(int vm_count,List<DeviceClass>devices,List<VMonDevicesClass>vms,int[]replacement,List<PeriodicTaskSet> taskset)
    {
        index=(int)(Math.random()*10000);
        this.vm_count=vm_count;
        this.devices=devices;
        this.vms=vms;
        this.replacement=replacement;
        this.taskset=taskset;
    }*/
    public void printUsedServersUtilization()
    {
        double sum=0;
        for(int i=devices.size()-number_of_localservers-1;i<devices.size()-number_of_localservers-1+getNumberOfUsedLocalServers();i++)
        {
            System.out.println("The local server with index "+devices.get(i).getIndex()+" is used and it`s utilization is equal to "+(device_utilization[i]/9));
            sum+=(device_utilization[i]/9);
        }
        System.out.println("Avg utilization:"+(sum/getNumberOfUsedLocalServers()));
    }
    //this function is declared for assignment of a vm to a server
    public void Assignment(int vm_index,int device_index)
    {
        this.replacement[vm_index]=device_index;
    }
    
    public int[] getRP()
    {
        return replacement;
    }
    public int[] CreateRandomAssignment(List<DeviceClass>devices,List<VMonDevicesClass>vms)
    {
        replacement=new int[vms.size()];
        Arrays.fill(replacement, -1);
        double[]freeUtilization=new double[devices.size()];
        for(int i=0;i<freeUtilization.length;i++)
        {
            freeUtilization[i]=devices.get(i).getAvailableUtilization();
        }
        Random random=new Random();
       for(int i=0;i<vms.size();i++)
       {
           
            //double vmiUtilization=vms.get(i).getUtilization();
            VMonDevicesClass currentVMonDevices=vms.get(i);
            int []selectedServer=IntStream.range(0, devices.size())
                    .filter(k->freeUtilization[k]>=currentVMonDevices.get_vm_in_specific_device(devices.get(k).getIndex()).getUtilization())
                    .toArray();
            int selectedServerNumber;
            if(selectedServer.length!=0)
            {
                selectedServerNumber=random.nextInt(selectedServer.length);
                freeUtilization[selectedServer[selectedServerNumber]]-=currentVMonDevices.get_vm_in_specific_device(devices.get(selectedServer[selectedServerNumber]).getIndex()).getUtilization();
                this.replacement[i]=selectedServer[selectedServerNumber];
            }
            else
            {
                 selectedServerNumber=random.nextInt(devices.size());
                 freeUtilization[selectedServerNumber]-=currentVMonDevices.get_vm_in_specific_device(devices.get(selectedServerNumber).getIndex()).getUtilization();
                 this.replacement[i]=selectedServerNumber;
            }
           // int selectedServerNumber=random.nextInt(selectedServer.length);
            
       }
       return replacement;
    }
    public PeriodicTaskSet getTasksetOnDevice(int device_index,PeriodicTaskSet input_taskset)
    {
        PeriodicTaskSet new_ts=new PeriodicTaskSet();
        if(tasksets_on_devices[device_index]!=null)
        {
            for(int i=0;i<tasksets_on_devices[device_index].getSize();i++)
            {
                new_ts.AddTask(tasksets_on_devices[device_index].getTask(i));
            }
        }
        for(int i=0;i<input_taskset.getSize();i++)
        {
            new_ts.AddTask(input_taskset.getTask(i));
        }
        return new_ts;
    }
    public void Schedule()
    {
        
        System.out.println("scheduling outsourced tasksets started");
        Arrays.fill(replacement, -1);
        /*double[]freeUtilization=new double[devices.size()];
        for(int i=0;i<freeUtilization.length;i++)
        {
            freeUtilization[i]=devices.get(i).getAvailableUtilization();
        }*/
        List<DeviceClass> fog_nodes=new ArrayList<DeviceClass>();
        for(int i=number_of_em_devices;i<devices.size()-number_of_localservers-1;i++)
        {
            fog_nodes.add(devices.get(i));
        }
        //devices.subList(number_of_em_devices, devices.size()-number_of_localservers-1);//excluding local servers at first
        
        Experiment ex=new Experiment();
       System.out.println("number of Hard-VMs  :"+outsourced_hard_vms.size()); 
       for(int i=0;i<outsourced_hard_vms.size();i++)
       {
          // System.out.println("number of Hard-VMs  :"+outsourced_hard_vms.size());
            //double vmiUtilization=vms.get(i).getUtilization();
            VMonDevicesClass currentVMonDevices=outsourced_hard_vms.get(i);
            boolean flag=false;
            for(int k=0;k<fog_nodes.size();k++)
            {
                if(device_utilization[fog_nodes.get(k).getIndex()]+currentVMonDevices.getOutsourcedTaskset().getUtilization_on_specefic_Device(currentVMonDevices.getIndex())>fog_nodes.get(k).getAvailableUtilization())
                    continue;
                PeriodicTaskSet task_set_on_device=this.getTasksetOnDevice(fog_nodes.get(k).getIndex(), currentVMonDevices.getOutsourcedTaskset());
                VM vm_on_device=ex.VMSpecify(task_set_on_device, fog_nodes.get(k).getIndex(),fog_nodes.get(k).getNumber_of_core());
                if(/*fog_nodes.get(k).getNumber_of_core()>=vm_on_device.getMax_required_core()&&*/fog_nodes.get(k).getAvailableCapacityforMprimeCores((int)vm_on_device.getMax_required_core())>=vm_on_device.getUtilization()&&currentVMonDevices.get_vm_in_specific_device(fog_nodes.get(k).getIndex()).is_schedulable)
                {
                    int selectedServerNumber=fog_nodes.get(k).getIndex();
                    this.replacement[outsourced_hard_vms.get(i).getIndex()]=selectedServerNumber;
                    this.replacement_type[outsourced_hard_vms.get(i).getIndex()]=fog_nodes.get(k).getType_id();
                    this.tasksets_on_devices[selectedServerNumber]=task_set_on_device;
                    flag=true;
                    device_utilization[fog_nodes.get(k).getIndex()]=vm_on_device.getUtilization();
                    break;
                }
            }
            if(!flag)
            {
                VM vm=ex.VMSpecify(currentVMonDevices.get_ts_in_specific_device(devices.get(number_of_em_devices+fog_nodes.size()).getIndex()), devices.get(number_of_em_devices+fog_nodes.size()).getIndex(),devices.get(number_of_em_devices+fog_nodes.size()).getNumber_of_core());
                if(/*vm.getMax_required_core()<=devices.get(number_of_em_devices+fog_nodes.size()).getNumber_of_core()&&*/vm.getUtilization()<=devices.get(number_of_em_devices+fog_nodes.size()).getAvailableCapacityforMprimeCores((int)vm.getMax_required_core())&&currentVMonDevices.get_vm_in_specific_device(devices.get(number_of_em_devices+fog_nodes.size()).getIndex()).is_schedulable)
                {
                    fog_nodes.add(devices.get(number_of_em_devices+fog_nodes.size()));//add a free local server
                    int selectedServerNumber=number_of_em_devices+fog_nodes.size()-1;//the index of added local server
                    replacement[outsourced_hard_vms.get(i).getIndex()]=selectedServerNumber;
                    this.replacement_type[outsourced_hard_vms.get(i).getIndex()]=fog_nodes.get(fog_nodes.size()-1).getType_id();
                    this.tasksets_on_devices[selectedServerNumber]=currentVMonDevices.get_ts_in_specific_device(selectedServerNumber);
                    this.number_of_used_local_servers++;
                }
                else
                {
                    System.out.println("taskset can not run on any devices");
                    System.exit(0);
                }
                 
                 //freeUtilization[fog_nodes.size()-1]=devices.get(fog_nodes.size()-1).getAvailableUtilization();
                 
                 //double selectedDeviceUtilization=currentVMonDevices.get_vm_in_specific_device(fog_nodes.get(selectedServerNumber).getIndex()).getUtilization();
                 
                 //freeUtilization[selectedServerNumber]-=selectedDeviceUtilization;
                 
                 
            }
           // int selectedServerNumber=random.nextInt(selectedServer.length);
            System.out.println("scheduling Hard VM"+ i +" finished");
       }
       
       System.out.println("scheduling Hard-VMs  finished");
     Collections.sort(outsourced_soft_vms, new Comparator<VMonDevicesClass>(){
        public int compare(VMonDevicesClass vm1,VMonDevicesClass vm2)
        {
            if(vm1.get_vm_in_specific_device(devices.size()-1).getData_mass()>vm2.get_vm_in_specific_device(devices.size()-1).getData_mass())
                return -1;
            else if(vm1.get_vm_in_specific_device(devices.size()-1).getData_mass()==vm2.get_vm_in_specific_device(devices.size()-1).getData_mass())
                return 0;
            return 1;
        }
        });
     
     List<VMonDevicesClass> soft_vms_cant_run_in_fog=new ArrayList<VMonDevicesClass>();
     
     for(int i=0;i<outsourced_soft_vms.size();i++)
     {
         VMonDevicesClass currentVMonDevices=outsourced_soft_vms.get(i);
         
         boolean flag=false;
         for(int k=0;k<fog_nodes.size();k++)
         {
             if(device_utilization[fog_nodes.get(k).getIndex()]+currentVMonDevices.getOutsourcedTaskset().getUtilization_on_specefic_Device(fog_nodes.get(k).getIndex())>fog_nodes.get(k).getAvailableUtilization())
                    continue;
            PeriodicTaskSet task_set_on_device=this.getTasksetOnDevice(fog_nodes.get(k).getIndex(), currentVMonDevices.get_ts_in_specific_device(fog_nodes.get(k).getIndex()));
            VM vm_on_device=ex.VMSpecify(task_set_on_device, fog_nodes.get(k).getIndex(),fog_nodes.get(k).getNumber_of_core());
                if(/*fog_nodes.get(k).getNumber_of_core()>=vm_on_device.getMax_required_core()&&*/fog_nodes.get(k).getAvailableCapacityforMprimeCores((int)vm_on_device.getMax_required_core())>=vm_on_device.getUtilization()&&currentVMonDevices.get_vm_in_specific_device(fog_nodes.get(k).getIndex()).is_schedulable)
                {
                    int selectedServerNumber=fog_nodes.get(k).getIndex();
                    this.replacement[outsourced_soft_vms.get(i).getIndex()]=selectedServerNumber;
                    this.replacement_type[outsourced_soft_vms.get(i).getIndex()]=fog_nodes.get(k).getType_id();
                    this.tasksets_on_devices[selectedServerNumber]=task_set_on_device;
                    flag=true;
                    device_utilization[fog_nodes.get(k).getIndex()]=vm_on_device.getUtilization();
                    break;
                }
        }
        if(!flag)
        {
            soft_vms_cant_run_in_fog.add(outsourced_soft_vms.get(i));
        }
          System.out.println("scheduling Soft VM"+ i +" finished");
     }
     
     System.out.println("number of soft-vms that can run on fog devices "+(outsourced_soft_vms.size()-soft_vms_cant_run_in_fog.size()));
     System.out.println("scheduling soft-vms that can run on fog finished ");
      Collections.sort(soft_vms_cant_run_in_fog, new Comparator<VMonDevicesClass>(){
        public int compare(VMonDevicesClass vm1,VMonDevicesClass vm2)
        {
            if(vm1.get_vm_in_specific_device(devices.size()-1).getData_mass()>vm2.get_vm_in_specific_device(devices.size()-1).getData_mass())
                return 1;
            else if(vm1.get_vm_in_specific_device(devices.size()-1).getData_mass()==vm2.get_vm_in_specific_device(devices.size()-1).getData_mass())
                return 0;
            return -1;
        }
        });
      double communication_data_to_cloud=0;
      List<VMonDevicesClass>vms_should_run_on_local_servers=new ArrayList<VMonDevicesClass>();
      for(VMonDevicesClass soft_vm:soft_vms_cant_run_in_fog)
      {
          if(communication_data_to_cloud+soft_vm.get_vm_in_specific_device(devices.size()-1).getData_mass()<=this.max_data_on_cloud_link)
          {
                int selectedServerNumber=devices.size()-1;
                //double selectedDeviceUtilization=soft_vm.get_vm_in_specific_device(selectedServerNumber).getUtilization();
                //freeUtilization[selectedServerNumber]-=selectedDeviceUtilization;
                this.replacement[soft_vm.getIndex()]=devices.get(selectedServerNumber).getIndex();
                this.replacement_type[soft_vm.getIndex()]=devices.get(selectedServerNumber).getType_id();
                communication_data_to_cloud+=soft_vm.get_vm_in_specific_device(selectedServerNumber).getData_mass();
          }
          else
          {
              vms_should_run_on_local_servers.add(soft_vm);
              break;
          }
      }
      System.out.println("number of soft-vms that can not run on fog devices and shoud redirect to cloud  :"+(soft_vms_cant_run_in_fog.size()));
      //(soft_vms_cant_run_in_fog.size()-vms_should_run_on_local_servers.size()));
     System.out.println("scheduling soft-vms that can run on cloud finished ");
     System.out.println("number of soft-vms that can not run on cloud because of max load on network and should redirect to local servers :"+vms_should_run_on_local_servers.size());
      for(int i=0;i<vms_should_run_on_local_servers.size();i++)
      {
        VMonDevicesClass currentVMonDevices=vms_should_run_on_local_servers.get(i);
        boolean flag=false;
        double selectedServerUtilization=Double.NEGATIVE_INFINITY;
        int selectedServerNumber=0;
        PeriodicTaskSet taskset_on_selected_server=null;
        double selectedUt=0;
        for(int k=number_of_em_devices+fog_nodes.size();k<devices.size()-1;k++)
        {
            if(device_utilization[devices.get(k).getIndex()]+currentVMonDevices.getOutsourcedTaskset().getUtilization_on_specefic_Device(devices.get(k).getIndex())>devices.get(k).getAvailableUtilization())
                    continue;
            PeriodicTaskSet task_set_on_device=this.getTasksetOnDevice(devices.get(k).getIndex(), currentVMonDevices.getOutsourcedTaskset());
            VM vm_on_device=ex.VMSpecify(task_set_on_device, devices.get(k).getIndex(),devices.get(k).getNumber_of_core());
            //System.out.println("vm ut"+vm_on_device.getMax_required_core());
            if(/*devices.get(k).getNumber_of_core()>=vm_on_device.getMax_required_core()&&*/devices.get(k).getAvailableCapacityforMprimeCores((int)vm_on_device.getMax_required_core())>=vm_on_device.getUtilization()&&currentVMonDevices.get_vm_in_specific_device(devices.get(k).getIndex()).is_schedulable&&vm_on_device.getUtilization()>selectedServerUtilization)
            {
                selectedServerNumber=k;
                selectedServerUtilization=vm_on_device.getUtilization();
                taskset_on_selected_server=task_set_on_device;
                selectedUt=vm_on_device.getUtilization();
                flag=true;
               
            }
        }
          
        if(!flag)
        {
            System.out.println("not feasible!");
            System.exit(0);
        }
        this.replacement[vms_should_run_on_local_servers.get(i).getIndex()]=selectedServerNumber;
        this.replacement_type[vms_should_run_on_local_servers.get(i).getIndex()]=devices.get(selectedServerNumber).getType_id();
        this.tasksets_on_devices[selectedServerNumber]=taskset_on_selected_server;
        device_utilization[selectedServerNumber]=selectedUt;
        System.out.println("scheduling soft_vm"+vms_should_run_on_local_servers.get(i).getIndex()+"_that_should_run_on_local servers was finished");
      }
        System.out.println("assignment generated...");
    }
    /*public List<RP> getNeighborsOfRP(int vmNumber)
    {
        double[]freeUtilization=new double[devices.size()];
        
        for(int i=0;i<freeUtilization.length;i++)
        {
            freeUtilization[i]=devices.get(i).getAvailableUtilization();
        }
        for(int i=0;i<vm_count;i++)
        {
            freeUtilization[replacement[i]]-=vms.get(i).get_vm_in_specific_device(devices.get(replacement[i]).getIndex()).getUtilization();
        }
        freeUtilization[replacement[vmNumber]]+=vms.get(vmNumber).get_vm_in_specific_device(devices.get(replacement[vmNumber]).getIndex()).getUtilization();
        List<RP>neighbors=new ArrayList<RP>();
        for(int i=1;i<devices.size();i++)
        {
            if(freeUtilization[i]>=vms.get(vmNumber).get_vm_in_specific_device(devices.get(i).getIndex()).getUtilization())
            {
                 RP newNeighbor=new RP(vm_count, devices, vms, replacement.clone(),taskset);
                 newNeighbor.Assignment(vmNumber, i);
                 neighbors.add(newNeighbor);
            }
        }
        return neighbors;
       
    }
    */
    /*public double TP()
    {
        List[]vmsOnServer=new List[servers.size()];
        for(int i=0;i<servers.size();i++)
        {
            vmsOnServer[i]=new ArrayList<VM>();
        }
        for(int i=0;i<this.getRP().length;i++)
        {
            vmsOnServer[this.getRP()[i]].add(vms.get(i));
        }
        double powerConsumption=0;
        for(int i=0;i<servers.size();i++)
        {
            powerConsumption+=servers.get(i).PowerConsumption(vmsOnServer[i]);
        }
        return powerConsumption;
    }
    public double memoryPenalty(double alpha)
    {
        List[]vmsOnServer=new List[servers.size()];
        for(int i=0;i<servers.size();i++)
        {
            vmsOnServer[i]=new ArrayList<VM>();
        }
        for(int i=0;i<this.getRP().length;i++)
        {
            vmsOnServer[this.getRP()[i]].add(vms.get(i));
        }
        double memoryPenalty=0;
        for(int i=0;i<servers.size();i++)
        {
            memoryPenalty+=servers.get(i).memoryPenalty(vmsOnServer[i]);
        }
        return alpha*memoryPenalty;
    }
    
    public double FailureDegree(double beta)
    {
        List[]vmsOnServer=new List[servers.size()];
        for(int i=0;i<servers.size();i++)
        {
            vmsOnServer[i]=new ArrayList<VM>();
        }
        for(int i=0;i<this.getRP().length;i++)
        {
            vmsOnServer[this.getRP()[i]].add(vms.get(i));
        }
        double failureDegree=0;
        for(int i=0;i<servers.size();i++)
        {
            HostScheduler hsch=new HostScheduler(servers.get(i), vmsOnServer[i]);
            failureDegree+=hsch.Schedule2(taskset);
        }
        return beta*failureDegree;
    }
    public double Fitness(double alpha,double beta)
    {
        return TP()+memoryPenalty(alpha)+FailureDegree(beta);
    }
    */
    public double getCost(double constant_cloud_network_cost)
    {
        return getCloudCost(constant_cloud_network_cost)+getLocalServersCost();
    }
    private double getCloudCost(double constant_network_cost)
    {
        if(getVMsonCloud().size()==0)
        {
            return 0;
        }
        return getCostServiceCloud()+constant_network_cost;
    }
    public int getNumberOfHardVM()
    {
        return outsourced_hard_vms.size();
    }
    private double getLocalServersCost()
    {
        return getNumberOfUsedLocalServers()*getCostOneLocalServer();
    }
    public double getNumberOfUsedLocalServers()
    {
        int []number_of_vm_on_each_localserver_fog_devices=new int[devices.size()];
        for(int i=0;i<replacement.length;i++)
        {
            if(replacement[i]!=-1)
            {
                if(devices.get(replacement[i]).getType_id()==2 )
                {
                    number_of_vm_on_each_localserver_fog_devices[replacement[i]]=1;
                }
            }
        }
        int number_of_used_local_servers=0;
        for(int i=0;i<number_of_vm_on_each_localserver_fog_devices.length;i++)
        {
            number_of_used_local_servers+=number_of_vm_on_each_localserver_fog_devices[i];
        }
        return number_of_used_local_servers;
     
    }
    private double getCostServiceCloud()
    {
        double initialCost=100;
        List<VM>vms_on_cloud=getVMsonCloud();
       
        double sum_service_cost=initialCost;
        for(VM vm:vms_on_cloud)
        {
            sum_service_cost+=getCostVMCloud(vm);
        }
        return sum_service_cost;
        
    }
    public int getNumberOfOutsourcedApplication()
    {
        return outsourced_hard_vms.size()+outsourced_soft_vms.size();
    }
    
    public List<VM>getVMsonCloud()
    {
        List<VM>vms_on_cloud=new ArrayList<>();
        for(int i=0;i<outsourced_soft_vms.size();i++)
        {
            if(replacement_type[i]==3)
            {
//                if(i==outsourced_soft_vms.size()-1)
//                {
                    vms_on_cloud.add(outsourced_soft_vms.get(i).get_vm_in_specific_device(replacement[i]));
//                    vms_on_cloud.add(outsourced_hard_vms.get(i).get_vm_in_specific_device(replacement[i]));
//                }
                
            }
        }
        return vms_on_cloud;
    }

    public List<VM>getVMsonCloudByReplace(int[] replacement)
    {
        List<VM>vms_on_cloud=new ArrayList<>();
        for(int i=0;i<outsourced_soft_vms.size();i++)
        {
            if(replacement[i]==devices.size()-1)
            {
//                if(i==outsourced_soft_vms.size()-1)
//                {
                vms_on_cloud.add(outsourced_soft_vms.get(i).get_vm_in_specific_device(replacement[i]));
//                    vms_on_cloud.add(outsourced_hard_vms.get(i).get_vm_in_specific_device(replacement[i]));
//                }

            }
        }
        return vms_on_cloud;
    }

    private double getCostNetworkCloud()
    {
        double constant_net_cost=100;
       if(getVMsonCloud().size()!=0)
       {
           return constant_net_cost;
       }
       else
       {
           return 0;
       }
    }
    private double getCostOneLocalServer()
    {
        double const_server_cost=100;
        return const_server_cost;
    }
    private double getCostVMCloud(VM vm)
    {
        double const_vm_cost=100;
        return vm.max_required_core*const_vm_cost;
    }
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    public int getLength()
    {
        return vm_count;
    }
    /* public double PowerConsumption(int i,List<VM> vmsOnServer)
    {
    //    serv_vmsOnServe=vmsOnServer;
        double power;
        if(getUtilization(i,vmsOnServer)==0)
            power=0.15*(servers.get(i).getKfactor()*servers.get(i).getMaxPower());
        else
        {
            power=(servers.get(i).getKfactor()*servers.get(i).getMaxPower())+((1-servers.get(i).getKfactor())*servers.get(i).getMaxPower())*getUtilization(i,vmsOnServer);
        }
        return power;
    }
      public double getUtilization(int i,List<VM>vmsOnServer)
    {
        double utilization=0;
        for(VM vm:vmsOnServer)
        {
            utilization+=vm.getUtilization();
        }
        utilization/=servers.get(i).getCoreCount();
        return utilization;
    }*/
}
