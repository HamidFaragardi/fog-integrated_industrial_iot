/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fogcloudproject;

/**
 *
 * @author saeid
 */
public class SplitedVM {
    private int svm_index;
    private int vm_index;
    private double svm_period;
    private double svm_utilization;
    public SplitedVM(int svm_index,int vm_index,double svm_period,double svm_utilization)
    {
        this.svm_index=svm_index;
        this.vm_index=vm_index;
        this.svm_period=svm_period;
        this.svm_utilization=svm_utilization;
    }
     public int getIndex()
   {
       return svm_index;
   }
   public double getPeriod()
   {
       return svm_period;
   }
   
  
   public double getUtilization()
   {
       return svm_utilization;
   }
}
