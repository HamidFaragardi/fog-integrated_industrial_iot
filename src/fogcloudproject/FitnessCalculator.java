package fogcloudproject;
/**
 * @author Siyanew
 */

import java.util.ArrayList;
import java.util.List;

public class FitnessCalculator {
    private static final boolean FIRSTFIT_CHECK = false;
    public static List<DeviceClass> devices;
    //List<PeriodicTaskSet> tasksets;
    public static int noFogDevices;
    public static int noEmbededDevices;
    public static ArrayList<VMTypes> vms;
    public ContainerReplacement containerReplacement;
    //    public static int[] replace;
    public static VMArray vmArray;
    public int[] replaceVM;
    private static double[][] bandwitch;
    public double containerCost;

    public int private_cloud_size;

    ArrayList<FogDevice> localServer = new ArrayList<>();

    public double utilPenalty = 0;
    public double memoryPenalty = 0;
    public double comPenalty = 0;
    public int hardPenalty = 0;

    public static double BETA2;
    public static double BETA1;


    public final static double LOCALSERVER_MEMORY = 20 * 1024 * 1024;

    public final static double VM_CONSTANT_COST = 0.138; // 100 / (30 * 24); // per month
    public final static double VM_NETWORK_COST = 2.08;
    public final static double LOCALSERVER_PURCHASE_COST = 0.03;
    public final static double LOCALSERVER_MAINTANANCE_COST_ENERGY = 1;
    public final static double LOCALSERVER_MAINTANANCE_COST_IT_MAN = 5;


    public FitnessCalculator(List<DeviceClass> devices, VMArray vmArray, int noEmbededDevices, int noFogDevices, ArrayList<VMTypes> vms, double[][] bandwitch) {
        this.devices = devices;
        this.noEmbededDevices = noEmbededDevices;
        this.noFogDevices = noFogDevices;
        this.vms = vms;
        this.vmArray = vmArray;
        this.bandwitch = bandwitch;
        this.replaceVM = new int[vmArray.length()];
        setBeta();
    }

    public void setBeta() {
        containerReplacement = new ContainerReplacement(getCloudVMS(), vms);
        double LOCALSERVER_COST = (LOCALSERVER_MAINTANANCE_COST_ENERGY + 2 * LOCALSERVER_MAINTANANCE_COST_IT_MAN + LOCALSERVER_PURCHASE_COST);
        double worstCase = VMArray.hardVMs * LOCALSERVER_COST;
        for (int i = VMArray.hardVMs; i < vmArray.length(); i++) {
            double cloud_cost = containerReplacement.costPerVm(vmArray.getVMs().get(i).get_vm_in_specific_device(devices.size() - 1));
            if (cloud_cost > LOCALSERVER_COST) {
                worstCase += cloud_cost;
            } else {
                worstCase += LOCALSERVER_COST;
            }
        }
        BETA2 = worstCase;
        BETA1 = 1000 * BETA2;
    }

    public FitnessCalculator() {
    }

    public VMArray getVmArray() {
        return vmArray;
    }


    private FogDevice localServerFactory() {
        int deviceIndex = 0;
        List<Core> coreList = new ArrayList<Core>();
        int noCores = 8;
        for (int i = 1; i < noCores; i++) {
            coreList.add(new Core(i, deviceIndex, 1, 0));

        }
        FogDevice localServer = new FogDevice(deviceIndex, 8, coreList, 2);
        return localServer;
    }

    //     public MainScheduler(List<DeviceClass> devices, List<PeriodicTaskSet> tasksets,int number_of_whole_devices, int number_of_embeded_devices, Bandwidth bandwidth,int number_of_local_servers) {
    public double calculateFitness(int[] replaceVM) {
        this.replaceVM = replaceVM;
        return cloudCost() + privateCloudCost() + BETA1 * utilizationPenalty() + BETA2 * (memoryPenalty() + HardVmPenalty() + calculateCommunicationPenalty());
    }

    public double cloudCost() {
        containerReplacement = new ContainerReplacement(getCloudVMS(), vms);
        if (containerReplacement.getTotalCost() == 0) return 0;
        containerCost = containerReplacement.getTotalCost();
        return VM_CONSTANT_COST + VM_NETWORK_COST + containerCost;
    }

    private ArrayList<VM> getCloudVMS() {
        ArrayList<VM> containers = new ArrayList<>();
        for (int i = 0; i < replaceVM.length; i++) {
            if (replaceVM[i] == noFogDevices + 1) {
                containers.add(vmArray.getVMs().get(i).get_vm_in_specific_device(noEmbededDevices + noFogDevices + 1)); // Cluod Index
            }
        }
        return containers;
    }

    public double privateCloudCost() {
        int neededFogNodes = firstFitFindLocalServerCount();
        return neededFogNodes * LOCALSERVER_MAINTANANCE_COST_ENERGY + 2 * neededFogNodes * LOCALSERVER_MAINTANANCE_COST_IT_MAN + neededFogNodes * LOCALSERVER_PURCHASE_COST;
    }

    private int firstFitFindLocalServerCount() {
        localServer = new ArrayList<>();
        for (int i = 0; i < replaceVM.length; i++) {
            if (replaceVM[i] == noFogDevices) {
                boolean flagIfFind = false;
                for (int j = 0; j < localServer.size(); j++) {
                    if (localServer.get(j).getAvailableUtilization() > calculateUtilization((ArrayList<PeriodicTask>) vmArray.getVMs().get(i).getOutsourcedTaskset().getTaskList(), noFogDevices + noEmbededDevices) && vmArray.getVMs().get(i).getOutsourcedTaskset().getTotal_ram() > LOCALSERVER_MEMORY) {
                        flagIfFind = true;
                        double util = calculateUtilization((ArrayList<PeriodicTask>) vmArray.getVMs().get(i).getOutsourcedTaskset().getTaskList(), noFogDevices + noEmbededDevices);
                        for (int k = 0; k < localServer.get(j).core_list.size(); k++) {
                            if (util <= 0) break;
                            if (localServer.get(j).core_list.get(k).getFreeUtilization() != 0) {
                                if (localServer.get(j).core_list.get(k).getFreeUtilization() > util) {
                                    util = 0;
                                    localServer.get(j).core_list.get(k).setCore_busy_utilization(util);
                                } else {
                                    util -= localServer.get(j).core_list.get(k).getFreeUtilization();
                                    localServer.get(j).core_list.get(k).setCore_busy_utilization(1);
                                }
                            }
                        }

                    }
                }
                if (flagIfFind) continue;
                else {
                    FogDevice singleLocalServer = localServerFactory();
                    double util = calculateUtilization((ArrayList<PeriodicTask>) vmArray.getVMs().get(i).getOutsourcedTaskset().getTaskList(), noFogDevices + noEmbededDevices);
                    for (int k = 0; k < singleLocalServer.core_list.size(); k++) {
                        if (util <= 0) break;
                        if (singleLocalServer.core_list.get(k).getFreeUtilization() != 0) {
                            if (singleLocalServer.core_list.get(k).getFreeUtilization() > util) {
                                util = 0;
                                singleLocalServer.core_list.get(k).setCore_busy_utilization(util);
                            } else {
                                util -= singleLocalServer.core_list.get(k).getFreeUtilization();
                                singleLocalServer.core_list.get(k).setCore_busy_utilization(1);
                            }
                        }
                    }
                    localServer.add(singleLocalServer);
                }
            }
        }
        private_cloud_size = localServer.size();
        return localServer.size();
    }

    public double utilizationPenalty() {
        double penalty = 0;

        for (int i = 0; i < noFogDevices; i++) {
            double totalUtil = 0;
            for (int j = 0; j < replaceVM.length; j++) {
                if (i == replaceVM[j]) {
                    totalUtil += vmArray.getVMs().get(j).getOutsourcedTaskset().getUtilization_on_specefic_Device(noEmbededDevices + replaceVM[j]);
                }
            }
            if (devices.get(noEmbededDevices + i).getAvailableUtilization() < totalUtil) {
                penalty += totalUtil - devices.get(noEmbededDevices + i).getAvailableUtilization();
            }
        }
        utilPenalty = penalty;
        return penalty;
    }

    public int HardVmPenalty() {
        int count = 0;
        for (int i = 0; i < VMArray.hardVMs; i++) {
            if (replaceVM[i] == noFogDevices + 1) count++;
        }
        hardPenalty = count;
        return count;
    }

    public double memoryPenalty() {
        // TODO FOG NODES PENALTY
        double penaltyFogNode = 0;
//        for (int i = 0; i < vmArray.length(); i++) {
////            if (rp.getRP()[i] == -1) continue;
//            if (replaceVM[i] == noFogDevices) {
//                penaltyFogNode += LOCALSERVER_MEMORY - vmArray.getVMs().get(i).getOutsourcedTaskset().getTotal_ram();
//            } else if (replaceVM[i] != noFogDevices+1){
//                penaltyFogNode += 512 * 1024 - vmArray.getVMs().get(i).getOutsourcedTaskset().getTotal_ram();
//            }
//        }
//        if (penaltyFogNode < 0) penaltyFogNode = 0;
//        return penaltyFogNode + containerReplacement.getCloudMemoryPenalty();
        // Version1
//        for (int i = 0; i < replaceVM.length; i++) {
//            if (replaceVM[i]>=noFogDevices) continue;
//            if(vmArray.getVMs().get(i).getOutsourcedTaskset().getTotal_ram() > devices.get(noEmbededDevices + replaceVM[i]).getRam()){
//                penaltyFogNode += vmArray.getVMs().get(i).getOutsourcedTaskset().getTotal_ram() - devices.get(noEmbededDevices + replaceVM[i]).getRam();
//            }
//        }
        // Version 2 --> Best Version
        for (int i = 0; i < noFogDevices; i++) {
            double totalRam = 0;
            for (int j = 0; j < replaceVM.length; j++) {
                if (i == replaceVM[j]) {
                    totalRam += vmArray.getVMs().get(j).getOutsourcedTaskset().getTotal_ram();
                }
            }
            if (devices.get(noEmbededDevices + i).getRam() < totalRam) {
                penaltyFogNode += totalRam - devices.get(noEmbededDevices + i).getRam();
            }
        }
        memoryPenalty = penaltyFogNode;
        return penaltyFogNode;
    }


    public static double calculateUtilization(ArrayList<PeriodicTask> taskList, int device) {
        double util = 0;
        for (PeriodicTask periodicTask : taskList) {
            util += periodicTask.execution_time[device] / periodicTask.getPeriod();
        }
        return util;
    }

    public double calculateCommunicationPenalty() {
        double penalty = 0;
        // for cloud
        int cloudData = 0;
        for (int i = 0; i < replaceVM.length; i++) {
            if (replaceVM[i] == noFogDevices + 1) {
                if (vmArray.getVMs().get(i).getType() == 0)
                    cloudData += vmArray.getVMs().get(i).getEmbToSoft();
                else cloudData += vmArray.getVMs().get(i).getEmbToHard();
            }
        }
        if (cloudData > Bandwidth.getCloudBand()) {
            penalty += cloudData - Bandwidth.getCloudBand();
        }



        if (FIRSTFIT_CHECK){
            comPenalty = penalty;
            return penalty;
        }
        ArrayList<Integer> checked = new ArrayList<>();
        // SOFT & HARD VM on Same FOG NODE
        for (int i = vmArray.getVMsHARD().size(); i < replaceVM.length; i++) {
            if (replaceVM[i] < noFogDevices) {
                int index = findIndex(vmArray.getVMs().get(i).index);
                if (index == -1) {
                    continue;
                } else {
                    if (replaceVM[i] == replaceVM[index]) {
                        checked.add(i);
                        checked.add(index);
                        double all_data = vmArray.getVMs().get(i).getEmbToSoft() + vmArray.getVMs().get(i).getEmbToHard();
                        if (all_data > bandwitch[vmArray.getVMs().get(i).index][noEmbededDevices + replaceVM[index]]) {
                            penalty += all_data - bandwitch[vmArray.getVMs().get(i).index][noEmbededDevices + replaceVM[index]];
                        }
                    }
                }
            }
        }
        // HARD WITH OUT COUPLE
        for (int i = 0; i < vmArray.getVMsHARD().size(); i++) {
            if(checked.indexOf(i) != -1) continue;
            if (replaceVM[i] < noFogDevices) {
                double all_data = vmArray.getVMs().get(i).getEmbToHard();
                if (all_data > bandwitch[vmArray.getVMs().get(i).index][noEmbededDevices + replaceVM[i]]) {
                    penalty += all_data - bandwitch[vmArray.getVMs().get(i).index][noEmbededDevices + replaceVM[i]];
                }
            }
        }

        // SOFT WITH OUT COUPLE
        for (int i = vmArray.getVMsHARD().size(); i < vmArray.getVMs().size(); i++) {
            if(checked.indexOf(i) != -1) continue;
            if (replaceVM[i] < noFogDevices) {
                double all_data = vmArray.getVMs().get(i).getEmbToSoft();
                if (all_data > bandwitch[vmArray.getVMs().get(i).index][noEmbededDevices + replaceVM[i]]) {
                    penalty += all_data - bandwitch[vmArray.getVMs().get(i).index][noEmbededDevices + replaceVM[i]];
                }
            }
        }

        // HARD WITH OUT COUPLE
        for (int i = 0; i < vmArray.getVMsHARD().size(); i++) {
            if(checked.indexOf(i) != -1) continue;
            if (replaceVM[i] < noFogDevices) {
                double all_data = vmArray.getVMs().get(i).getEmbToHard();
                if (all_data > bandwitch[vmArray.getVMs().get(i).index][noEmbededDevices + replaceVM[i]]) {
                    penalty += all_data - bandwitch[vmArray.getVMs().get(i).index][noEmbededDevices + replaceVM[i]];
                }
            }
        }



        comPenalty = penalty;
        return penalty;
    }

    public int findIndex(int index) {
        for (int i = 0; i < vmArray.getVMsHARD().size(); i++) {
            if (vmArray.getVMs().get(i).index == index) {
                return i;
            }
        }
        return -1;
    }
}