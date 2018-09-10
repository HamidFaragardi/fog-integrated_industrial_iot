package fogcloudproject;

public class VMOnCloud {
    private VMTypes type;
    private double core_busy_utilization = 0;

    public VMOnCloud(VMTypes type) {
        this.type = type;
    }

    public double changeType(VMTypes type){
        double newCost = type.getCost() - this.type.getCost();
        this.type = type;
        return newCost;
    }

    public double getChangeTypeCost(VMTypes type){
        return type.getCost() - this.type.getCost();
    }

    public double getCore_free_utilization() {
        return type.getCoresNumber() - core_busy_utilization;
    }

    public void setOnVm(double utilization){
        core_busy_utilization += utilization;
    }

    public boolean isSchedulable(int noOfCore, int memory, double utilization){
        return noOfCore <= type.getCoresNumber() && utilization <= (type.getCoresNumber() - core_busy_utilization) && memory < type.getMemory();
    }

    public VMTypes getType() {
        return type;
    }
}
