package fogcloudproject;

import java.util.ArrayList;

public class ContainerReplacement {
    public ArrayList<VM> containers;
    public ArrayList<VMTypes> VMs;
    public ArrayList<VMOnCloud> vmOnClouds;
    public int[] containersOnVm;

    public ContainerReplacement(ArrayList<VM> containers, ArrayList<VMTypes> VMs) {
        this.containers = containers;
        this.VMs = VMs;
        vmOnClouds = new ArrayList<>();
        containersOnVm = new int[containers.size()];
        calculateContainersCount();
    }

    public void calculateContainersCount(){
//        System.out.println("----------------Containers---------------");
        for (int i = 0; i < containers.size(); i++) {
            double neededUtil = containers.get(i).utilization;
            double neededUtil2 = containers.get(i).utilization/containers.get(i).max_required_core;
//            System.out.printf("container %d with utilization %.2f and %d core(s)\n",i,neededUtil,(int) containers.get(i).max_required_core);

            VMTypes vmTypes = null;
            VMTypes changedVmTypes = null;

            double minCostOnPreviousVMs = Double.MAX_VALUE;
            int previousVmsIndex = -1;

            boolean canRunFlag = false;
            for (int j = 0; j < vmOnClouds.size(); j++) {
                if (vmOnClouds.get(j).isSchedulable((int) containers.get(i).max_required_core,containers.get(i).ram,neededUtil)){
                    containersOnVm[i] = j;
                    vmOnClouds.get(j).setOnVm(neededUtil);
                    canRunFlag = true;
                    break;
                }
            }
            if (canRunFlag) continue;

            for (int j = 0; j < vmOnClouds.size(); j++) {
                if (containers.get(i).getUtilization() > vmOnClouds.get(j).getCore_free_utilization()) continue;
                for (int k = 0; k < VMs.size(); k++) {
                    if (VMs.get(k).canRun((int) containers.get(i).max_required_core,containers.get(i).ram) && minCostOnPreviousVMs > vmOnClouds.get(j).getChangeTypeCost(VMs.get(k))) {
                        minCostOnPreviousVMs = vmOnClouds.get(j).getChangeTypeCost(VMs.get(k));
                        changedVmTypes = VMs.get(k);
                        previousVmsIndex = j;
                    }
                }

            }



            double minCostOnNewVM = Double.MAX_VALUE;

            for (int j = 0; j < VMs.size(); j++) {
                if (VMs.get(j).canRun((int) containers.get(i).max_required_core,containers.get(i).ram) && minCostOnNewVM > VMs.get(j).getCost()){
                    minCostOnNewVM = VMs.get(j).getCost();
                    vmTypes = VMs.get(j);
                }
            }

            if (minCostOnNewVM < minCostOnPreviousVMs){
                VMOnCloud vmOnCloud = new VMOnCloud(vmTypes);
                containersOnVm[i] = vmOnClouds.size();
                vmOnCloud.setOnVm(neededUtil);
                vmOnClouds.add(vmOnCloud);
            } else {
                if (previousVmsIndex == -1) {
                    System.out.printf("container %d with utilization %.2f Could Not Be run On Any Cloud VMs\n",i,neededUtil);
                    continue;
                }
                vmOnClouds.get(previousVmsIndex).changeType(changedVmTypes);
                containersOnVm[i] = vmOnClouds.size();
                vmOnClouds.get(previousVmsIndex).setOnVm(neededUtil);

            }

        }
//        System.out.println("------------------------------------");
//        System.out.println("----------------VMs---------------");
//        for (int i = 0; i < vmOnClouds.size(); i++) {
//            System.out.printf("VM No %d with free utilization %.2f with %d core(s) ,  %dB memory with cost %.6f $ per hour \n",i,vmOnClouds.get(i).getCore_free_utilization(),vmOnClouds.get(i).getType().getCoresNumber() , vmOnClouds.get(i).getType().getMemory(),vmOnClouds.get(i).getType().getCost());
//        }
//        System.out.println("------------------------------------");
//        System.out.println("-----------Container On VM----------");
//        System.out.printf("[");
        for (int i = 0; i < containersOnVm.length; i++) {
//            System.out.printf("%d, ",containersOnVm[i]);
        }
//        System.out.printf("]\n");
//        System.out.println("------------------------------------");
    }

    public double getTotalCost(){
        double cost = 0;
        for (VMOnCloud vmOnCloud:vmOnClouds) {
            cost += vmOnCloud.getType().getCost();
        }
        return cost;
    }

//    public double getCloudMemoryPenalty(){
//        double memory = 0;
//        for (VMOnCloud vmOnCloud:vmOnClouds) {
//            memory += vmOnCloud.getType().getMemory();
//        }
//        double usedMemory = 0;
//        for (VM vm:containers) {
//            usedMemory += vm.getData_mass();
//        }
//        if (usedMemory>memory) return 0;
//        else return memory - usedMemory;
//    }

    public double costPerVm(VM vm){
        for (int k = 0; k < VMs.size(); k++) {
            if (VMs.get(k).canRun((int) vm.max_required_core, vm.ram)) {
                return VMs.get(k).getCost();
            }
        }
        return 0;
    }
}
