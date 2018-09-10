/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fogcloudproject;

import java.util.List;

/**
 *
 * @author saeid
 */
public class EmbededDevice extends DeviceClass{

    
    

    public EmbededDevice(int index, int number_of_core, List<Core> core_list) {
        super(index, number_of_core, core_list,0);
    }
   
}
