package fogcloudproject;

/**
 * @author Siyanew
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomFit
{

    private final FitnessCalculator fc;
    private VMArray vmArray;
    private List<DeviceClass> devices;
    private int fog_devices;
    private int embeded_devices;
    private int[] index_list;
    private int[] replaceVM;
    ArrayList<FogDevice> localServer = new ArrayList<>();

    public RandomFit(VMArray vmArray, FitnessCalculator fc, List<DeviceClass> devices, int number_of_embeded_devices, int number_of_fog_devices_except_local_servers) {
        this.fc = fc;
        this.vmArray = vmArray;
        this.devices = devices;
        this.fog_devices = number_of_fog_devices_except_local_servers;
        this.embeded_devices = number_of_embeded_devices;
        this.index_list = new int[vmArray.length()];
        this.replaceVM = new int[vmArray.length()];
        randomFit();
    }

    public void randomFit(){
        for (int i = 0; i < replaceVM.length; i++) {
            replaceVM[i] = -1;
        }
        Random r = new Random();
        for (int i = 0; i < vmArray.getVMsHARD().size(); i++) {
            replaceVM[i] = r.nextInt(fog_devices+1);
            while (memoryPenalty() != 0 || utilizationPenalty() != 0 ){
                replaceVM[i] = r.nextInt(fog_devices+1);
            }
        }
        for (int i = vmArray.getVMsHARD().size(); i < vmArray.length(); i++) {
            replaceVM[i] = r.nextInt(fog_devices+2);
            while (memoryPenalty() != 0 || utilizationPenalty() != 0 ){
                replaceVM[i] = r.nextInt(fog_devices+2);
            }
        }
    }

    public double memoryPenalty() {
        // TODO FOG NODES PENALTY
        double penaltyFogNode = 0;
        // Version 2 --> Best Version
        for (int i = 0; i < fog_devices; i++) {
            double totalRam = 0;
            for (int j = 0; j < replaceVM.length; j++) {
                if (i == replaceVM[j]) {
                    totalRam += vmArray.getVMs().get(j).getOutsourcedTaskset().getTotal_ram();
                }
            }
            if (devices.get(embeded_devices + i).getRam() < totalRam) {
                penaltyFogNode += totalRam - devices.get(embeded_devices + i).getRam();
            }
        }
        return penaltyFogNode;
    }

    public double utilizationPenalty() {
        double penalty = 0;
        for (int i = 0; i < fog_devices; i++) {
            double totalUtil = 0;
            for (int j = 0; j < replaceVM.length; j++) {
                if (i == replaceVM[j]) {
                    totalUtil += vmArray.getVMs().get(j).getOutsourcedTaskset().getUtilization_on_specefic_Device(embeded_devices + replaceVM[j]);
                }
            }
            if (devices.get(embeded_devices + i).getAvailableUtilization() < totalUtil) {
                penalty += totalUtil - devices.get(embeded_devices + i).getAvailableUtilization();
            }
        }
        return penalty;
    }

    public int[] getReplaceVM() {
        return replaceVM;
    }

}
