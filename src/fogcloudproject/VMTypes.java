package fogcloudproject;

public class VMTypes {
    private int CoresNumber;
    private int memory;
    private double cost;

    public VMTypes(int coresNumber, int memory, double cost) {
        CoresNumber = coresNumber;
        this.memory = memory;
        this.cost = cost;
    }

    public int getCoresNumber() {
        return CoresNumber;
    }

    public int getMemory() {
        return memory;
    }


    public double getCost() {
        return cost;
    }

    public boolean canRun(int noOfCore, int memory){
        return noOfCore <= getCoresNumber() && memory < getMemory();
    }


}
