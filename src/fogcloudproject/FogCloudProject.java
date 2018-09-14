/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fogcloudproject;

import fogcloudproject.Honeybee.Drone;
import fogcloudproject.Honeybee.Honeybee;
import fogcloudproject.SA.SimulatedAnealing;
import fogcloudproject.utils.Importer;

import java.util.*;
import java.io.*;

/**
 * @author saeid
 */
public class FogCloudProject {


    // PeriodicTask task0=new PeriodicTask(0,2,2,1.9,)

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        Scanner reader = new Scanner(System.in);
        int pretest = 1;
        if (pretest != 1) {
            System.out.println("Enter the number of embeddded devices(applications)");
            //System Config
            int number_of_embeded_devices = reader.nextInt();
            System.out.println("Enter the number of fog devices except local servers");
            int number_of_fog_devices_except_local_servers = reader.nextInt();
            System.out.println("Enter the number local servers");
            int number_of_local_servers = reader.nextInt();
            int number_of_applications = number_of_embeded_devices;
            System.out.println("number of embeded devices:" + number_of_embeded_devices);
            System.out.println("number of fog devices:" + number_of_fog_devices_except_local_servers);
            System.out.println("number of local servers:" + number_of_local_servers);
            System.out.println("/////////////////////////////////////////////////");
            try {
                String filename = "GeneratedTestCase/System/systemconfig.txt";
                FileWriter filewriter = new FileWriter(filename);
                BufferedWriter bufferwriter = new BufferedWriter(filewriter);
                bufferwriter.write(Integer.toString(number_of_embeded_devices));
                bufferwriter.newLine();
                bufferwriter.write(Integer.toString(number_of_fog_devices_except_local_servers));
                bufferwriter.newLine();
                bufferwriter.write(Integer.toString(number_of_local_servers));
                bufferwriter.close();
            } catch (IOException ex) {
                System.out.println("error in configuring system");
            }
            ////////Generating Applications

            //is equal to number of Applications(Embeded Devices)
            // List<PeriodicTaskSet>taskset=ex.RandomComponents(true, total_util, number_of_applications);

            //////////////////////////////

            List<DeviceClass> devices = new ArrayList<DeviceClass>();
            Bandwidth bandwidth = new Bandwidth(number_of_embeded_devices, number_of_fog_devices_except_local_servers + number_of_local_servers);
            try {
                String filename = "GeneratedTestCase/System/bandwidth.txt";
                FileWriter filewriter = new FileWriter(filename);
                BufferedWriter bufferwriter = new BufferedWriter(filewriter);
                for (int i = 0; i < number_of_embeded_devices; i++) {
                    for (int j = number_of_embeded_devices; j < number_of_embeded_devices + number_of_fog_devices_except_local_servers + number_of_local_servers; j++) {
                        bandwidth.setBandwidth(i, j, (100 * (1000000)) / 1000);
                        String bandwidth_description = Integer.toString(i) + "," + Integer.toString(j) + "," + Double.toString((100 * (1000000)) / 1000);
                        bufferwriter.write(bandwidth_description);
                        bufferwriter.newLine();
                    }
                    bandwidth.setBandwidth(i, number_of_embeded_devices + number_of_fog_devices_except_local_servers + number_of_local_servers/*cloud index*/, (12000000 / number_of_embeded_devices) / 1000);
                    String bandwidth_description = Integer.toString(i) + "," + Integer.toString(number_of_embeded_devices + number_of_fog_devices_except_local_servers + number_of_local_servers) + "," + Double.toString((12000000 / number_of_embeded_devices) / 1000);
                    bufferwriter.write(bandwidth_description);
                    bufferwriter.newLine();
                }
                bufferwriter.close();
            } catch (Exception ex) {
                System.err.println("error in generating bandwidth");
            }
            ///////////////////////////
            Random random = new Random();
            int new_device_index = 0;
            int new_core_index = 0;
           /* TasksetGenerator ts_generator=new TasksetGenerator(number_of_embeded_devices+number_of_fog_devices_except_local_servers+number_of_local_servers+1, bandwidth);

                    List<Core>core_list_of_new_em_device0=new ArrayList<Core>();
                    core_list_of_new_em_device0.add(new Core(new_core_index,new_device_index, 1, 0));
                    EmbededDevice new_em_device0=new EmbededDevice(new_device_index,1,core_list_of_new_em_device0);
                    devices.add(new_em_device0);
                    new_core_index++;
                    ts_generator.CreateHardTaskset(1);
                    System.out.println("New Embeded Device with index"+0+"was generated with 1 cores");
             new_device_index++;
                    List<Core>core_list_of_new_em_device1=new ArrayList<Core>();
                    core_list_of_new_em_device1.add(new Core(new_core_index,new_device_index, 1, 0));
                    core_list_of_new_em_device1.add(new Core(new_core_index+1,new_device_index, 1, 0));
                    EmbededDevice new_em_device1=new EmbededDevice(new_device_index,1,core_list_of_new_em_device1);
                    devices.add(new_em_device1);
                    new_core_index+=2;
                    ts_generator.CreateHardTaskset(1);
                    System.out.println("New Embeded Device with index"+1+"was generated with 1 cores");
            new_device_index++;
            */
            try {
                String filename = "GeneratedTestCase/System/embeddeddevices.txt";
                FileWriter filewriter = new FileWriter(filename);
                BufferedWriter bufferwriter = new BufferedWriter(filewriter);
                for (int i = new_device_index; i < number_of_embeded_devices; i++) {
                    int em_device_type = random.nextInt(3);

                    if (em_device_type == 0) {
                        List<Core> core_list_of_new_em_device = new ArrayList<Core>();
                        core_list_of_new_em_device.add(new Core(new_core_index, new_device_index, 1, 0));
                        EmbededDevice new_em_device = new EmbededDevice(new_device_index, 1, core_list_of_new_em_device);
                        devices.add(new_em_device);
                        String new_device_description = Integer.toString(new_device_index) + "," + Integer.toString(new_core_index);
                        bufferwriter.write(new_device_description);

                        new_core_index++;
                        //      ts_generator.GenerateTaskSet(10);
                        System.out.println("New Embeded Device with index" + i + "was generated with 1 cores");
                    } else if (em_device_type == 1) {
                        List<Core> core_list_of_new_em_device = new ArrayList<Core>();
                        core_list_of_new_em_device.add(new Core(new_core_index, new_device_index, 1, 0));
                        core_list_of_new_em_device.add(new Core(new_core_index + 1, new_device_index, 1, 0));
                        EmbededDevice new_em_device = new EmbededDevice(new_device_index, 2, core_list_of_new_em_device);
                        devices.add(new_em_device);
                        String new_device_description = Integer.toString(new_device_index) + "," + Integer.toString(new_core_index) + "," + Integer.toString(new_core_index + 1);
                        bufferwriter.write(new_device_description);

                        new_core_index += 2;
                        //    ts_generator.GenerateTaskSet(20);
                        System.out.println("New Embeded Device with index" + i + "was generated with 2 cores");

                    } else {
                        List<Core> core_list_of_new_em_device = new ArrayList<Core>();
                        core_list_of_new_em_device.add(new Core(new_core_index, new_device_index, 1, 0));
                        core_list_of_new_em_device.add(new Core(new_core_index + 1, new_device_index, 1, 0));
                        core_list_of_new_em_device.add(new Core(new_core_index + 2, new_device_index, 1, 0));
                        core_list_of_new_em_device.add(new Core(new_core_index + 3, new_device_index, 1, 0));
                        EmbededDevice new_em_device = new EmbededDevice(new_device_index, 4, core_list_of_new_em_device);
                        devices.add(new_em_device);
                        String new_device_description = Integer.toString(new_device_index) + "," + Integer.toString(new_core_index) + "," + Integer.toString(new_core_index + 1) + "," + Integer.toString(new_core_index + 2) + "," + Integer.toString(new_core_index + 3);
                        bufferwriter.write(new_device_description);

                        new_core_index += 4;
                        //  ts_generator.GenerateTaskSet(50);
                        System.out.println("New Embedded Device with index" + i + "was generated with 4 cores");
                    }
                    new_device_index++;
                    bufferwriter.newLine();
                }

                System.out.println("embeded devices were generated");
                System.out.println("////////////////////////////////////////////////");
                bufferwriter.close();
            } catch (IOException ex) {
                System.out.println("error in generating embedded devices ");
            }

            try {
                String filename = "GeneratedTestCase/System/fogdevices.txt";
                FileWriter filewriter = new FileWriter(filename);
                BufferedWriter bufferwriter = new BufferedWriter(filewriter);
                int last_index = new_device_index + number_of_fog_devices_except_local_servers;
                for (int i = new_device_index; i < last_index; i++) {
                    int em_device_type = random.nextInt(2);
                    if (em_device_type == 0) {
                        List<Core> core_list_of_new_fog_device = new ArrayList<Core>();
                        core_list_of_new_fog_device.add(new Core(new_core_index, new_device_index, 0.5, 0));
                        FogDevice new_fog_device = new FogDevice(new_device_index, 1, core_list_of_new_fog_device, 1);
                        devices.add(new_fog_device);
                        String new_device_description = Integer.toString(new_device_index) + "," + Integer.toString(new_core_index) + ",0.5";
                        bufferwriter.write(new_device_description);
                        new_core_index++;
                        System.out.println("New Fog Device with index" + i + "was generated with 1 core and available capacity 0.5 per core");
                    } else {
                        List<Core> core_list_of_new_fog_device = new ArrayList<Core>();
                        core_list_of_new_fog_device.add(new Core(new_core_index, new_device_index, 0.5, 0));
                        core_list_of_new_fog_device.add(new Core(new_core_index + 1, new_device_index, 0.5, 0));
                        FogDevice new_fog_device = new FogDevice(new_device_index, 2, core_list_of_new_fog_device, 1);
                        devices.add(new_fog_device);
                        String new_device_description = Integer.toString(new_device_index) + "," + Integer.toString(new_core_index) + ",0.5," + Integer.toString(new_core_index + 1) + ",0.5";
                        bufferwriter.write(new_device_description);
                        new_core_index += 2;
                        System.out.println("New Fog Device with index" + i + "was generated with 2 cores and available capacity 0.5 per core");

                    }
                    bufferwriter.newLine();
                    new_device_index++;

                }
                bufferwriter.close();
                System.out.println("fog  devices were generated");
                System.out.println("////////////////////////////////////////////////");
            } catch (IOException ex) {
                System.out.println("error in generating fog devices");
            }
            try {
                //////////////////////////////
                String filename = "GeneratedTestCase/System/servers.txt";
                FileWriter filewriter = new FileWriter(filename);
                BufferedWriter bufferwriter = new BufferedWriter(filewriter);
                int last_index = new_device_index + number_of_local_servers;
                for (int i = new_device_index; i < last_index; i++) {
                    int last_core = new_core_index + 9;
                    List<Core> core_list_of_new_em_device = new ArrayList<Core>();
                    String new_device_description = Integer.toString(new_device_index);

                    for (int j = new_core_index; j < last_core; j++) {
                        core_list_of_new_em_device.add(new Core(j, new_device_index, 1, 0));
                        new_device_description += "," + Integer.toString(new_core_index);
                        new_core_index++;
                    }
                    System.out.println("New local server  with index" + i + "was generated with 9 cores and available capacity 1.0 per core");
                    bufferwriter.write(new_device_description);
                    bufferwriter.newLine();
                    FogDevice new_local_server = new FogDevice(new_device_index, 9, core_list_of_new_em_device, 2);
                    devices.add(new_local_server);
                    new_device_index++;
                }
                bufferwriter.close();
                System.out.println("local servers were generated");
                System.out.println("///////////////////////////////////////////////////");
            } catch (IOException ex) {
                System.out.println("error in generating local servers");
            }
            //////////////////////

            //////////////////////////
            try {
                String filename = "GeneratedTestCase/System/cloud.txt";
                FileWriter filewriter = new FileWriter(filename);
                BufferedWriter bufferwriter = new BufferedWriter(filewriter);
                String new_device_description = Integer.toString(new_device_index);
                List<Core> core_list_of_new_cloud_device = new ArrayList<Core>();
                int last_core_index = new_core_index + 100;
                for (int j = new_core_index; j < last_core_index; j++) {
                    core_list_of_new_cloud_device.add(new Core(j, new_device_index, 1, 0));
                    new_device_description += "," + Integer.toString(new_core_index);
                    new_core_index++;
                }

                bufferwriter.write(new_device_description);
                bufferwriter.newLine();
                bufferwriter.close();
                CloudNode new_cloud = new CloudNode(new_device_index, 100, core_list_of_new_cloud_device);
                devices.add(new_cloud);
                System.out.println("Cloud was generated with index" + new_device_index);
                new_device_index++;
                System.out.println("///////////////////////////////////////////////////");
            } catch (IOException ex) {
                System.out.println("error in generatin cloud");
            }

            //////////////////////////

            //////////////////////////
            Experiment ex = new Experiment();
            List<PeriodicTaskSet> tasksets = ex.RandomComponents(true, number_of_embeded_devices, bandwidth);//ts_generator.getTasksets();//ex.RandomComponents(true, number_of_embeded_devices, number_of_embeded_devices, bandwidth);//ts_generator.getTasksets();
            //System.out.println(ts_generator.getTasksets().size());

            for (int i = 0; i < number_of_embeded_devices; i++) {
                try {
                    String filename = "GeneratedTestCase/Application/" + Integer.toString(i) + ".txt";
                    FileWriter filewriter = new FileWriter(filename);
                    BufferedWriter bufferwriter = new BufferedWriter(filewriter);

                    for (int k = 0; k < tasksets.get(i).getSize(); k++) {
                        PeriodicTask task = tasksets.get(i).getTask(k);
                        String new_task_description = task.index + "," + task.period + "," + task.absolute_execution_time + "," + task.absolute_deadline + "," + task.input_data_mass + "," + task.output_data_mass;
                        bufferwriter.write(new_task_description);
                        bufferwriter.newLine();
                    }
                    bufferwriter.close();
                } catch (Exception ex2) {
                    System.out.println("error in generating task sets");
                }

            }

//
//            MainScheduler mainscheduler = new MainScheduler(devices, tasksets, number_of_embeded_devices + number_of_fog_devices_except_local_servers + number_of_local_servers + 1, number_of_embeded_devices, bandwidth, number_of_local_servers);
//            RP rp = mainscheduler.Schedule();
//            /////////////////////////////
//            //System.out.println(rp.getCost(200));
//            System.out.println("number of Used local Servers:" + rp.getNumberOfUsedLocalServers());
//            System.out.println("number of VMs on each fog,local server and cloud:");
//            int[] replacement = rp.getRP();
//            int[] deviceType = rp.getDeviceTypeArray();
//            for (int i = 0; i < number_of_embeded_devices; i++) {
//                if (replacement[i] != -1) {
//                    if (deviceType[i] == 1) {
//                        System.out.println("outsourced Taskset from embeded device" + i + " is assigned to a fog device with index" + replacement[i]);
//                    } else if (deviceType[i] == 2) {
//                        System.out.println("outsourced Taskset from embeded device" + i + " is assigned to a local server with index" + replacement[i]);
//                    } else if (deviceType[i] == 3) {
//                        System.out.println("outsourced Taskset from embeded device" + i + " is assigned to cloud");
//                    }
//                }
//            }
//
//            System.out.println("////////////////////////////////////////////////////////");
//            System.out.println("Number of Applications that scheduled on EM-Device Cpmpletely:" + (number_of_embeded_devices - rp.getNumberOfOutsourcedApplication()));
//            System.out.println("Number of Applications that some of tasks outsourced:" + rp.getNumberOfOutsourcedApplication());
//            System.out.println("Number of VMs on Fog(fogdevice,local server):" + (rp.getNumberOfOutsourcedApplication() - rp.getVMsonCloud().size()));
//            System.out.println("Number of VMs on Cloud:" + rp.getVMsonCloud().size());
//            System.out.println("Number of Used local Servers:" + rp.getNumberOfUsedLocalServers());
//            System.out.println("Number of Hard-VM:" + rp.getNumberOfHardVM());
//            rp.printUsedServersUtilization();


        } else {
            int number_of_embeded_devices = 0;
            int number_of_fog_devices_except_local_servers = 0;
            int number_of_local_servers = 0;
            try {
                String filename = "GeneratedTestCase/System/systemconfig.txt";
                FileReader filereader = new FileReader(filename);
                BufferedReader bufferreader = new BufferedReader(filereader);

                number_of_embeded_devices = Integer.parseInt(bufferreader.readLine());
                number_of_fog_devices_except_local_servers = Integer.parseInt(bufferreader.readLine());
                number_of_local_servers = Integer.parseInt(bufferreader.readLine());
                System.out.println("Select a benchmarck : ");
                System.out.println("1 --> Small");
                System.out.println("2 --> Medium");
                System.out.println("3 --> Large");
                switch (reader.nextInt()){
                    case 1:
                        number_of_embeded_devices = 100;
                        break;
                    case 2:
                        number_of_embeded_devices = 300;
                        break;
                    case 3:
                        number_of_embeded_devices = 500;
                        break;
                    default:
                        System.out.println("Invalid Input ...");
                        System.exit(1);
                }
                bufferreader.close();
            } catch (IOException ex) {
                System.out.println("error in configuring system");
            }

            int number_of_applications = number_of_embeded_devices;
            System.out.println("number of embeded devices:" + number_of_embeded_devices);
            System.out.println("number of fog devices:" + number_of_fog_devices_except_local_servers);
            System.out.println("number of local servers:" + number_of_local_servers);
            System.out.println("/////////////////////////////////////////////////");

            ////////Generating Applications

            //is equal to number of Applications(Embeded Devices)
            // List<PeriodicTaskSet>taskset=ex.RandomComponents(true, total_util, number_of_applications);

            //////////////////////////////

            List<DeviceClass> devices = new ArrayList<DeviceClass>();
            Bandwidth bandwidth = new Bandwidth(number_of_embeded_devices, number_of_fog_devices_except_local_servers + number_of_local_servers);
            try {
                String filename = "GeneratedTestCase/System/bandwidth.txt";
                FileReader filereader = new FileReader(filename);
                BufferedReader bufferreader = new BufferedReader(filereader);
                for (int i = 0; i < number_of_embeded_devices; i++) {
                    for (int j = number_of_embeded_devices; j < number_of_embeded_devices + number_of_fog_devices_except_local_servers + number_of_local_servers; j++) {
                        String bandwidth_description = bufferreader.readLine();
                        String splitted_bandwidth[] = bandwidth_description.split(",");
                        bandwidth.setBandwidth(i, j, Double.parseDouble(splitted_bandwidth[2]));


                    }
                    String bandwidth_description = bufferreader.readLine();
                    String splitted_bandwidth[] = bandwidth_description.split(",");
                    bandwidth.setBandwidth(i, number_of_embeded_devices + number_of_fog_devices_except_local_servers + number_of_local_servers/*cloud index*/, Double.parseDouble(splitted_bandwidth[2]));

                }
                bufferreader.close();
            } catch (Exception ex) {
                System.err.println("error in generating bandwidth");
            }
            ///////////////////////////
            Random random = new Random();
            int new_device_index = 0;
            int new_core_index = 0;
           /* TasksetGenerator ts_generator=new TasksetGenerator(number_of_embeded_devices+number_of_fog_devices_except_local_servers+number_of_local_servers+1, bandwidth);

                    List<Core>core_list_of_new_em_device0=new ArrayList<Core>();
                    core_list_of_new_em_device0.add(new Core(new_core_index,new_device_index, 1, 0));
                    EmbededDevice new_em_device0=new EmbededDevice(new_device_index,1,core_list_of_new_em_device0);
                    devices.add(new_em_device0);
                    new_core_index++;
                    ts_generator.CreateHardTaskset(1);
                    System.out.println("New Embeded Device with index"+0+"was generated with 1 cores");
             new_device_index++;
                    List<Core>core_list_of_new_em_device1=new ArrayList<Core>();
                    core_list_of_new_em_device1.add(new Core(new_core_index,new_device_index, 1, 0));
                    core_list_of_new_em_device1.add(new Core(new_core_index+1,new_device_index, 1, 0));
                    EmbededDevice new_em_device1=new EmbededDevice(new_device_index,1,core_list_of_new_em_device1);
                    devices.add(new_em_device1);
                    new_core_index+=2;
                    ts_generator.CreateHardTaskset(1);
                    System.out.println("New Embeded Device with index"+1+"was generated with 1 cores");
            new_device_index++;
            */
            try {
                String filename = "GeneratedTestCase/System/embeddeddevices.txt";
                FileReader filereader = new FileReader(filename);
                BufferedReader bufferreader = new BufferedReader(filereader);
                for (int i = 0; i < number_of_embeded_devices; i++) {
                    String device_description = bufferreader.readLine();
                    String[] device_splitted = device_description.split(",");
                    List<Core> core_list_of_new_em_device = new ArrayList<Core>();
                    int device_index = Integer.parseInt(device_splitted[0]);
                    int ram = Integer.parseInt(device_splitted[1]);
                    for (int k = 2; k < device_splitted.length; k++) {
                        core_list_of_new_em_device.add(new Core(Integer.parseInt(device_splitted[k]), device_index, 1, 0));
                    }
                    EmbededDevice new_em_device = new EmbededDevice(device_index, device_splitted.length - 2, core_list_of_new_em_device);
                    new_em_device.setRam(ram);
                    devices.add(new_em_device);
                    System.out.println("New Embeded Device with index" + device_index + "was generated with " + (device_splitted.length - 1) + " cores");
                    int em_device_type = random.nextInt(3);
                }

                System.out.println("embeded devices were generated");
                System.out.println("////////////////////////////////////////////////");
                bufferreader.close();
            } catch (IOException ex) {
                System.out.println("error in generating embedded devices ");
            }

            try {
                String filename = "GeneratedTestCase/System/fogdevices.txt";
                FileReader filereader = new FileReader(filename);
                BufferedReader bufferreader = new BufferedReader(filereader);
                for (int i = 0; i < number_of_fog_devices_except_local_servers; i++) {
                    List<Core> core_list_of_new_fog_device = new ArrayList<Core>();
                    String device_description = bufferreader.readLine();
                    String[] device_splitted = device_description.split(",");
                    int device_index = Integer.parseInt(device_splitted[0]);
                    for (int k = 2; k < device_splitted.length; k = k + 2) {
                        core_list_of_new_fog_device.add(new Core(Integer.parseInt(device_splitted[k]), device_index, Double.parseDouble(device_splitted[k + 1]), 0));
                    }
                    FogDevice new_fog_device = new FogDevice(device_index, (device_splitted.length - 1) / 2, core_list_of_new_fog_device, 1);
                    System.out.println("New Fog Device with index" + device_index + "was generated with " + ((device_splitted.length - 1) / 2) + " core and available capacity " + device_splitted[device_splitted.length - 1] + " per core");
                    new_fog_device.setRam(Integer.parseInt(device_splitted[1]));
                    devices.add(new_fog_device);

                }
                bufferreader.close();
                System.out.println("fog  devices were generated");
                System.out.println("////////////////////////////////////////////////");
            } catch (IOException ex) {
                System.out.println("error in generating fog devices");
            }
            try {
                //////////////////////////////
                String filename = "GeneratedTestCase/System/servers.txt";
                FileReader filereader = new FileReader(filename);
                BufferedReader bufferreader = new BufferedReader(filereader);
                for (int i = 0; i < number_of_local_servers; i++) {
                    List<Core> core_list_of_new_em_device = new ArrayList<Core>();
                    String device_description = bufferreader.readLine();
                    String[] device_splitted = device_description.split(",");
                    int device_index = Integer.parseInt(device_splitted[0]);
                    for (int j = 1; j < device_splitted.length; j++) {
                        core_list_of_new_em_device.add(new Core(Integer.parseInt(device_splitted[j]), device_index, 1, 0));

                    }
                    FogDevice new_local_server = new FogDevice(device_index, device_splitted.length - 1, core_list_of_new_em_device, 2);
                    System.out.println("New local server  with index" + device_index + "was generated with " + (device_splitted.length - 1) + " cores and available capacity 1.0 per core");


                    devices.add(new_local_server);
                    new_device_index++;
                }
                bufferreader.close();
                System.out.println("local servers were generated");
                System.out.println("///////////////////////////////////////////////////");
            } catch (IOException ex) {
                System.out.println("error in generating local servers");
            }
            //////////////////////

            //////////////////////////
            try {
                String filename = "GeneratedTestCase/System/cloud.txt";
                FileReader filereader = new FileReader(filename);
                BufferedReader bufferreader = new BufferedReader(filereader);
                String device_description = bufferreader.readLine();
                String[] device_splitted = device_description.split(",");
                int device_index = Integer.parseInt(device_splitted[0]);
                List<Core> core_list_of_new_cloud_device = new ArrayList<Core>();

                for (int j = 1; j < device_splitted.length; j++) {
                    core_list_of_new_cloud_device.add(new Core(Integer.parseInt(device_splitted[j]), device_index, 1, 0));

                }
                CloudNode new_cloud = new CloudNode(device_index, device_splitted.length - 1, core_list_of_new_cloud_device);
                devices.add(new_cloud);

                bufferreader.close();


                System.out.println("Cloud was generated with index" + new_device_index);
                new_device_index++;
                System.out.println("///////////////////////////////////////////////////");
            } catch (IOException ex) {
                System.out.println("error in generatin cloud");
            }

            //////////////////////////

            ArrayList<VMTypes> vms = Importer.importVMs();

            //////////////////////////
            Experiment ex = new Experiment();
            List<PeriodicTaskSet> tasksets = new ArrayList<PeriodicTaskSet>();//ts_generator.getTasksets();//ex.RandomComponents(true, number_of_embeded_devices, number_of_embeded_devices, bandwidth);//ts_generator.getTasksets();
            //System.out.println(ts_generator.getTasksets().size());

            for (int i = 0; i < number_of_embeded_devices; i++) {
                try {
                    String filename = "GeneratedTestCase/Application/" + Integer.toString(i) + ".txt";
                    FileReader filereader = new FileReader(filename);
                    BufferedReader bufferreader = new BufferedReader(filereader);
                    String task = null;
                    PeriodicTaskSet generated_taskset = new PeriodicTaskSet();
                    while ((task = bufferreader.readLine()) != null) {
                        String[] task_splitted = task.split(",");
                        PeriodicTask new_task = new PeriodicTask(Integer.parseInt(task_splitted[0]), Integer.parseInt(task_splitted[1]), Integer.parseInt(task_splitted[3]), Double.parseDouble(task_splitted[2]), Integer.parseInt(task_splitted[4]), Integer.parseInt(task_splitted[5]), bandwidth, i, number_of_embeded_devices + number_of_fog_devices_except_local_servers + number_of_local_servers + 1); //tasksets.get(i).getTask(k);

                        new_task.setRam(Double.parseDouble(task_splitted[6]));
//                        new_task.setRam(rand.nextInt(1047552) + 1024);
                        new_task.setRestricted(Boolean.parseBoolean(task_splitted[7]));
                        generated_taskset.AddTask(new_task);
                    }
                    generated_taskset.calculateRam();
                    generated_taskset.setIndex(i);
                    generated_taskset.initCommunication();
                    tasksets.add(generated_taskset);
                } catch (Exception ex2) {
                    System.out.println("error in generating task sets");
                }

            }


            MainScheduler mainscheduler = new MainScheduler(devices, tasksets, number_of_embeded_devices + number_of_fog_devices_except_local_servers + number_of_local_servers + 1, number_of_embeded_devices, bandwidth, number_of_local_servers);
            VMArray vmArray = mainscheduler.Schedule();

            int[] best;
            FitnessCalculator fc = new FitnessCalculator(devices, vmArray, number_of_embeded_devices, number_of_fog_devices_except_local_servers, vms, bandwidth.bandwidth_matrix);



            /////// TODO : HONEYBEE
            final int iteration = 15;
            RandomFit rf;

            int result[] = new int[iteration];
            double result2[] = new double[iteration];
            double extime[] = new double[iteration];
            int answers[][] = new int[iteration][];
            for (int i = 0; i < iteration; i++) {
                System.out.println("RUN " + (i+1));
                long start = System.currentTimeMillis();

                Honeybee hb = new Honeybee(100, 8, fc);
                rf = new RandomFit(vmArray, fc, devices, number_of_embeded_devices, number_of_fog_devices_except_local_servers);
                best = rf.getReplaceVM();
                result2[i] = fc.calculateFitness(best);
                hb.getQueen().getChro().setGene(best);
                hb.startAlgorithm(100);
                result[i] = hb.lastResult();
                answers[i] = hb.getQueen().getChro().getGene();

                long stop = System.currentTimeMillis();

                extime[i] = stop - start;

            }

            int index = getMinimum(result);
            best = answers[index];
            System.out.println("----------RUN WITH HONEYBEE---------");
            printResult(fc, best);

            System.out.println("\n");
            System.out.println("Fitness of HoneyBee runs : " + Arrays.toString(result));
            System.out.println("Fitness of Random Fit runs : " + Arrays.toString(result2));
            System.out.println("Execution Times (MILLISEC) : " + Arrays.toString(extime));

            double sumResult = 0;
            double sumResult2 = 0;
            double sumtime = 0;

            for (int i = 0; i < iteration; i++) {
                sumResult += result[i];
                sumtime += extime[i];
                sumResult2 += result2[i];
            }

            System.out.println("Average Fitness of HoneyBee: " + sumResult / iteration);
            System.out.println("Average Fitness of Random Fit: " + sumResult2 / iteration);
            System.out.println("Average Execution Times (MILLISEC) : " + sumtime / iteration);

            ////// TODO : Simulated Anealing
//                SimulatedAnealing sa = new SimulatedAnealing(number_of_fog_devices_except_local_servers + 2, vmArray.length(), fc2);


            ////// TODO : First Fit
            long start = System.currentTimeMillis();
            FirstFit ff = new FirstFit(vmArray, fc, devices, number_of_embeded_devices, number_of_fog_devices_except_local_servers);
            long stop = System.currentTimeMillis();
            System.out.println("Stop - start --> " + (stop - start));
            best = ff.getReplaceVM();
            System.out.println("----------RUN WITH FIRSTFIT---------");
            printResult(fc, best);


        }

    }

    public static int getMinimum(int[] res) {
        int index = 0;
        for (int i = 1; i < res.length; i++) {
            if (res[index] > res[i]) {
                index = i;
            }
        }
        return index;
    }

    public static void printResult(FitnessCalculator fc, int[] best) {
        fc.calculateFitness(best);
        if (fc.utilPenalty + fc.memoryPenalty + fc.hardPenalty + fc.comPenalty == 0) {
            System.out.println("Feasible Solution");
        } else {
            System.out.println("Infeasible Solution");
        }
        System.out.println("");
        System.out.println("Fitness --> " + fc.calculateFitness(best));
        System.out.println("Number Generated VMs --> " + best.length);
        System.out.println("Allocation --> " + Arrays.toString(best).replaceAll((FitnessCalculator.noFogDevices) + "", "pc").replaceAll((FitnessCalculator.noFogDevices) + 1 + "", "c"));
        System.out.println("");
        System.out.println("Cost Cloud --> " + fc.cloudCost());
        System.out.println("VM_CONSTANT_COST + VM_NETWORK_COST + Container_COST = Cost Cloud");
        System.out.println("Instances on Cloud --> " + fc.containerReplacement.vmOnClouds.size());

        System.out.println(FitnessCalculator.VM_CONSTANT_COST + " + " + FitnessCalculator.VM_NETWORK_COST + " + " + fc.containerCost + " +  = " + fc.cloudCost());
        System.out.println("");
        System.out.println("Cost Private Cloud --> " + fc.privateCloudCost());
        System.out.println("Number of Servers in Private Cloud --> " + fc.private_cloud_size);

        System.out.println("LOCALSERVER_MAINTANANCE_COST_ENERGY : " + FitnessCalculator.LOCALSERVER_MAINTANANCE_COST_ENERGY);
        System.out.println("LOCALSERVER_MAINTANANCE_COST_IT_MAN : " + FitnessCalculator.LOCALSERVER_MAINTANANCE_COST_IT_MAN);
        System.out.println("LOCALSERVER_PURCHASE_COST : " + FitnessCalculator.LOCALSERVER_PURCHASE_COST);
        System.out.println("");
//        System.out.println("PRIVATE CLOUD COST FORMULA -->PRIVATE CLOUD SERVERS * (LOCALSERVER_MAINTANANCE_COST_ENERGY + 2 * LOCALSERVER_MAINTANANCE_COST_IT_MAN + LOCALSERVER_PURCHASE_COST");

        System.out.println("");
        System.out.println("Utilization Penalty --> " + fc.utilPenalty);
        System.out.println("Memory Penalty --> " + fc.memoryPenalty);
        System.out.println("Hard Penalty --> " + fc.hardPenalty);
        System.out.println("Com Penalty --> " + fc.comPenalty);

        System.out.println("BETA1 --> " + FitnessCalculator.BETA1);
        System.out.println("BETA2 --> " + FitnessCalculator.BETA2);

//        System.out.println("FITNESS FORMULA --> cloudCost + privateCloudCost + BETA1 * utilizationPenalty() + BETA2 * (MemoryPenalty + HardVmPenalty + CommunicationPenalty");
//        System.out.println("Cost Cloud --> " + fc.containerReplacement.getTotalCost());
        int fogCount = 0;
        for (int i = 0; i < fc.noFogDevices; i++) {
            for (int j = 0; j < fc.replaceVM.length; j++) {
                if (i == fc.replaceVM[j]) {
                    fogCount++;
                    break;
                }
            }

        }
        double temps = fogCount * 100 / fc.noFogDevices;
        System.out.println("Number of Used fog nodes over All fog nodes --> " + temps + "%");
    }

}
