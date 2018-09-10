package fogcloudproject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VMArray {
    private ArrayList<VMonDevicesClass> VMs;
    private ArrayList<VMonDevicesClass> VMsHARD;
    private ArrayList<VMonDevicesClass> VMsSOFT;
    public static int  hardVMs;

    public VMArray(List<VMonDevicesClass> outsourced_hard_vms, List<VMonDevicesClass> outsourced_soft_vms) {
        this.VMs = new ArrayList<>();
        this.VMsHARD = new ArrayList<>();
        this.VMsSOFT = new ArrayList<>();
        for (VMonDevicesClass vmc: outsourced_hard_vms) {
            if (vmc.getOutsourcedTaskset().getUtilization_on_specefic_Device(0) == 0) continue;
//            vmc.getOutsourcedTaskset().calculateRam();
            this.VMs.add(vmc);
            this.VMsHARD.add(vmc);
            System.out.println(String.format("Hard VM WITH %f UTIL and %d RAM", vmc.getOutsourcedTaskset().getUtilization_on_specefic_Device(0),vmc.getOutsourcedTaskset().getTotal_ram()));
        }
        for (VMonDevicesClass vmc: outsourced_soft_vms) {
            if (vmc.getOutsourcedTaskset().getUtilization_on_specefic_Device(0) == 0) continue;
//            vmc.getOutsourcedTaskset().calculateRam();
            this.VMs.add(vmc);
            this.VMsSOFT.add(vmc);
            System.out.println(String.format("Soft VM WITH %f UTIL and %d RAM", vmc.getOutsourcedTaskset().getUtilization_on_specefic_Device(0),vmc.getOutsourcedTaskset().getTotal_ram()));

        }

        System.out.println("HARD VMS --> " + this.getVMsHARD().size());
        System.out.println("SOFT VMS --> " + this.getVMsSOFT().size());
        System.out.println("TOTAL VMS --> " + this.getVMs().size());

        hardVMs = VMsHARD.size();
        setSensitive();
    }

    private void setSensitive() {
        for (VMonDevicesClass vmc:VMsHARD) {
            double max = -1;
            for (PeriodicTask pt:vmc.getOutsourcedTaskset().getTaskList()) {
                if(max < pt.timeSensitive) max = pt.timeSensitive;
            }
            vmc.getOutsourcedTaskset().setTimeSensitive(max);
        }
        for (VMonDevicesClass vmc:VMsSOFT) {
            double max = -1;
            for (PeriodicTask pt:vmc.getOutsourcedTaskset().getTaskList()) {
                if(max < pt.timeSensitive) max = pt.timeSensitive;
            }
            vmc.getOutsourcedTaskset().setTimeSensitive(max);
        }
    }

    public ArrayList<VMonDevicesClass> getVMsHARD() {
        return VMsHARD;
    }

    public ArrayList<VMonDevicesClass> getVMsSOFT() {
        return VMsSOFT;
    }

    public ArrayList<VMonDevicesClass> getVMs() {
        return VMs;
    }

    public void setVMs(ArrayList<VMonDevicesClass> VMs) {
        this.VMs = VMs;
    }


    public int length() {
        return VMs.size();
    }
}
