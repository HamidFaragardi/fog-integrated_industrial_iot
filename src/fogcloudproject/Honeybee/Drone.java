package fogcloudproject.Honeybee;

import fogcloudproject.RandomFit;

import java.util.Random;

public class Drone {
    private final static double RandomFitUsageProbability = 0.01;
    Chromosome chr;
    private RandomFit rf;

    int[] produceDrone(int length){

        chr = new Chromosome(length);
        Random rnd = new Random();

        if (rnd.nextDouble() < RandomFitUsageProbability){
            rf.randomFit();
            chr.setGene(rf.getReplaceVM());
        } else {
            chr.initChromosome();
        }

        return chr.getGene();
    }

    Chromosome getChro(){

        return chr;
    }

    void setChr(Chromosome chroIn){

        chr = chroIn;
    }

    int chroLength(){

        return chr.gene.length;
    }

    int[] getGene(){
        return chr.getGene();
    }

    public void setRf(RandomFit rf) {
        this.rf = rf;
    }
}
