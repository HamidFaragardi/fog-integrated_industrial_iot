/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fogcloudproject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author saeid
 */
public class FogDeviceScheduler {
    private FogDevice fogDevice;
    private List<VMonDevicesClass> vmsOnDeivice;
    private List<SplitedVM> svmsOnDevice;
    private int lastSelectedindex = 0;
    private List[] assignment;

    public FogDeviceScheduler(FogDevice fogDevice, List<VMonDevicesClass> vmsOnDevice) {
        this.fogDevice = fogDevice;
        this.vmsOnDeivice = vmsOnDevice;
        this.svmsOnDevice = new ArrayList<SplitedVM>();
        this.assignment = new List[fogDevice.getNumber_of_core()];
        for (int i = 0; i < fogDevice.getNumber_of_core(); i++)
            assignment[i] = new ArrayList<SplitedVM>();
        for (Core core : fogDevice.getCore_list())
            core.setCore_busy_utilization(1 - core.getAvailable_power());
    }

    public void CompactSplittingVM(VMonDevicesClass vm) {
        for (int i = 0; i < Math.floor(vm.get_vm_in_specific_device(fogDevice.getIndex()).getUtilization()); i++) {
            SplitedVM newSplittedVM = new SplitedVM(lastSelectedindex + 1, vm.getIndex(), vm.get_vm_in_specific_device(fogDevice.getIndex()).getPeriod(), 1);
            svmsOnDevice.add(newSplittedVM);
            lastSelectedindex++;
        }
        if ((vm.get_vm_in_specific_device(fogDevice.getIndex()).getUtilization() - Math.floor(vm.get_vm_in_specific_device(fogDevice.getIndex()).getUtilization())) != 0) {
            SplitedVM newSplittedVM = new SplitedVM(lastSelectedindex + 1, vm.getIndex(), vm.get_vm_in_specific_device(fogDevice.getIndex()).getPeriod(), vm.get_vm_in_specific_device(fogDevice.getIndex()).getUtilization() - Math.floor(vm.get_vm_in_specific_device(fogDevice.getIndex()).getUtilization()));
            svmsOnDevice.add(newSplittedVM);
            lastSelectedindex++;
        }

    }

    public void BalancedSplittingVM(VMonDevicesClass vm) {
        for (int i = 0; i < vm.get_vm_in_specific_device(fogDevice.getIndex()).getMax_required_core(); i++) {
            SplitedVM newSplittedVM = new SplitedVM(lastSelectedindex + 1, vm.getIndex(), vm.get_vm_in_specific_device(fogDevice.getIndex()).getPeriod(), vm.get_vm_in_specific_device(fogDevice.getIndex()).getUtilization() / vm.get_vm_in_specific_device(fogDevice.getIndex()).getMax_required_core());
            svmsOnDevice.add(newSplittedVM);
            lastSelectedindex++;
        }

    }

    /*
    splitting type=0 is for compact splitting and splitting type=1 is for balanced splitting
    */
    public double Schedule(int splittingType) {
        if (splittingType == 0) {
            for (VMonDevicesClass vm : vmsOnDeivice) {
                CompactSplittingVM(vm);
            }
        } else {
            for (VMonDevicesClass vm : vmsOnDeivice) {
                BalancedSplittingVM(vm);
            }
        }
        Collections.sort(svmsOnDevice, new Comparator<SplitedVM>() {
            public int compare(SplitedVM svm1, SplitedVM svm2) {
                if (svm1.getUtilization() < svm2.getUtilization())
                    return 1;
                else if (svm1.getUtilization() == svm2.getUtilization())
                    return 0;
                return -1;
            }
        });
        List<Core> cores = fogDevice.getFreeCores();

        double err = 0;
        for (SplitedVM svm : svmsOnDevice) {
            cores.sort(new Comparator<Core>() {
                public int compare(Core c1, Core c2) {
                    if (c1.getFreeUtilization() < c2.getFreeUtilization())
                        return 1;
                    else if (c1.getFreeUtilization() == c2.getFreeUtilization())
                        return 0;
                    return -1;
                }
            });
            int i = 0;
            boolean assigned = false;
            while (!assigned && i < cores.size()) {
                if (cores.get(i).getFreeUtilization() >= svm.getUtilization()) {
                    assignment[cores.get(i).getIndex()].add(svm);
                    cores.get(i).setCore_busy_utilization(cores.get(i).getCore_busy_utilization() + svm.getUtilization());
                    assigned = true;
                }
                i++;
            }
            if (!assigned)
                err += svm.getUtilization();

        }
        return err;
    }
    
    
    /*
    public double Schedule2(List<PeriodicTaskSet> taskset)
    {
        
        
        PeriodicTaskSet wholets=new PeriodicTaskSet();
        for(VMonDevicesClass vm:vmsOnDeivice)
        {
            PeriodicTaskSet currentTs=taskset.get(vm.getIndex());
            for(int i=0;i<currentTs.getSize();i++)
            {
                wholets.AddTask(currentTs.getTask(i));
            }
            
        }
        if(wholets.getSize()>0)
        {
        Experiment ex=new Experiment();
        VM currentVM=ex.VMSpecify(wholets,fogDevice.getIndex());
        double err=0;
        if(fogDevice.getNumber_of_core()-currentVM.getMaxCoreReq()<0)
        {
            err=-(server.getCoreCount()-currentVM.getMaxCoreReq());
        }
        
        err=err/currentVM.getMaxCoreReq();
        return err;
        }
        return 0;
    }
*/
}
