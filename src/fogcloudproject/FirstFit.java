package fogcloudproject;

/**
 * @author Siyanew
 */

import fogcloudproject.FitnessCalculator;
import fogcloudproject.VMArray;
import fogcloudproject.VMonDevicesClass;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static fogcloudproject.FitnessCalculator.LOCALSERVER_MEMORY;

public class FirstFit {

    private final FitnessCalculator fc;
    private VMArray vmArray;
    private List<DeviceClass> devices;
    private int fog_devices;
    private int embeded_devices;
    private int[] index_list;
    private int[] replaceVM;
    ArrayList<FogDevice> localServer = new ArrayList<>();


    public FirstFit(VMArray vmArray, FitnessCalculator fc, List<DeviceClass> devices, int number_of_embeded_devices, int number_of_fog_devices_except_local_servers) {
        this.fc = fc;
        this.vmArray = vmArray;
        this.devices = devices;
        this.fog_devices = number_of_fog_devices_except_local_servers;
        this.embeded_devices = number_of_embeded_devices;
        this.index_list = new int[vmArray.length()];
        this.replaceVM = new int[vmArray.length()];
        for (int i = 0; i < vmArray.getVMs().size(); i++) {
            index_list[i] = vmArray.getVMs().get(i).index;
        }
        doFirstFit();
    }

    private void doFirstFit() {
        // sort
        vmArray.getVMsHARD().sort(new Comparator<VMonDevicesClass>() {
            @Override
            public int compare(VMonDevicesClass o1, VMonDevicesClass o2) {
                if (o1.getOutsourcedTaskset().getTimeSensitive() > o2.getOutsourcedTaskset().getTimeSensitive())
                    return 1;
                return -1;
            }
        });

        // put on fognodes
        for (VMonDevicesClass vmc : vmArray.getVMsHARD()) {
            for (int i = embeded_devices; i < devices.size() - 2; i++) {
                if (devices.get(i).getRealAvailableUtilization() > vmc.getOutsourcedTaskset().getUtilization_on_specefic_Device(i) &&
                        devices.get(i).getRam() > vmc.getOutsourcedTaskset().getTotal_ram()) {
                    vmc.isFirstSchedule = true;
                    devices.get(i).setRam(devices.get(i).getRam() - vmc.getOutsourcedTaskset().getTotal_ram());
                    double util = vmc.getOutsourcedTaskset().getUtilization_on_specefic_Device(i);
                    for (Core c : devices.get(i).getFreeCores()) {
                        if (util == 0) break;
                        if (c.getCore_busy_utilization() == c.getAvailable_power()) continue;
                        if (util < c.getAvailable_power() - c.getCore_busy_utilization()) {
                            c.setCore_busy_utilization(c.getCore_busy_utilization() + util);
                            util = 0;
                            break;
                        } else {
                            util -= (c.getAvailable_power() - c.getCore_busy_utilization());
                            c.setCore_busy_utilization(c.getAvailable_power());
                        }
                    }
                    replaceVM[findIndex(vmc.index,0)] = i;
                    if (util == 0) break;

                }
            }
        }


        for (VMonDevicesClass vmc : vmArray.getVMsHARD()) {
            if (!vmc.isFirstSchedule) {
                replaceVM[findIndex(vmc.index,0)] = fog_devices + embeded_devices;
            }
        }

//        int s = firstFitFindLocalServerCount();

        vmArray.getVMsSOFT().sort(new Comparator<VMonDevicesClass>() {
            @Override
            public int compare(VMonDevicesClass o1, VMonDevicesClass o2) {
                if (o1.embToSoft + o1.hardToSoft < o2.embToSoft + o2.hardToSoft) return 1;
                return -1;
            }
        });

        for (VMonDevicesClass vmc : vmArray.getVMsSOFT()) {
            for (int i = embeded_devices; i < devices.size() - 2; i++) {
                if (devices.get(i).getRealAvailableUtilization() > vmc.getOutsourcedTaskset().getUtilization_on_specefic_Device(i) &&
                        devices.get(i).getRam() > vmc.getOutsourcedTaskset().getTotal_ram()) {
                    vmc.isFirstSchedule = true;
                    devices.get(i).setRam(devices.get(i).getRam() - vmc.getOutsourcedTaskset().getTotal_ram());
                    double util = vmc.getOutsourcedTaskset().getUtilization_on_specefic_Device(i);
                    for (Core c : devices.get(i).getFreeCores()) {
                        if (util == 0) break;
                        if (c.getCore_busy_utilization() == c.getAvailable_power()) continue;
                        if (util < c.getAvailable_power() - c.getCore_busy_utilization()) {
                            c.setCore_busy_utilization(c.getCore_busy_utilization() + util);
                            util = 0;
                            break;
                        } else {
                            util -= (c.getAvailable_power() - c.getCore_busy_utilization());
                            c.setCore_busy_utilization(c.getAvailable_power());
                        }
                    }
                    replaceVM[findIndex(vmc.index, vmArray.getVMsHARD().size())] = i;
                    if (util == 0) break;

                }
            }
        }

        int total_bandwith = 0;


        for (VMonDevicesClass vmc : vmArray.getVMsSOFT()) {
            if (!vmc.isFirstSchedule & Bandwidth.getCloudBand() > total_bandwith + vmc.getOutsourcedTaskset().getDataMass()) {
                total_bandwith += vmc.getOutsourcedTaskset().getDataMass();
                replaceVM[findIndex(vmc.index, vmArray.getVMsHARD().size())] = fog_devices + embeded_devices + 1;
                vmc.isFirstSchedule = true;
            } else continue;
        }


        for (VMonDevicesClass vmc : vmArray.getVMsSOFT()) {
            if (!vmc.isFirstSchedule) {
                replaceVM[findIndex(vmc.index,vmArray.getVMsHARD().size())] = fog_devices + embeded_devices;
            }
        }


        System.out.println("finished ");
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

    public static double calculateUtilization(ArrayList<PeriodicTask> taskList, int device) {
        double util = 0;
        for (PeriodicTask periodicTask : taskList) {
            util += periodicTask.execution_time[device] / periodicTask.getPeriod();
        }
        return util;
    }

    private int firstFitFindLocalServerCount() {

        for (int i = 0; i < replaceVM.length; i++) {
            if (vmArray.getVMs().get(i).isFirstSchedule) continue;
            if (replaceVM[i] == -2) {
                boolean flagIfFind = false;
                for (int j = 0; j < localServer.size(); j++) {
                    if (localServer.get(j).getAvailableUtilization() > calculateUtilization((ArrayList<PeriodicTask>) vmArray.getVMs().get(i).getOutsourcedTaskset().getTaskList(), fog_devices + embeded_devices) && vmArray.getVMs().get(i).getOutsourcedTaskset().getTotal_ram() > LOCALSERVER_MEMORY) {
                        flagIfFind = true;
                        double util = calculateUtilization((ArrayList<PeriodicTask>) vmArray.getVMs().get(i).getOutsourcedTaskset().getTaskList(), fog_devices + embeded_devices);
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
                    double util = calculateUtilization((ArrayList<PeriodicTask>) vmArray.getVMs().get(i).getOutsourcedTaskset().getTaskList(), fog_devices + embeded_devices);
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
        return localServer.size();
    }

    public int findIndex(int index,int start) {
        for (int i = start; i < index_list.length; i++) {
            if (index_list[i] == index) {
                return i;
            }
        }
        return -1;
    }

    public int[] getReplaceVM() {
        for (int i = 0; i < replaceVM.length; i++) {
            if (replaceVM[i] == 0) continue;
            replaceVM[i] -= embeded_devices;
        }
        return replaceVM;
    }
}
