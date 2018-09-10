package fogcloudproject.Honeybee;

import fogcloudproject.FitnessCalculator;

import java.util.Arrays;
import java.util.Random;

public class Chromosome {
    int[] gene;

    public Chromosome(int length){
        gene = new int[length];
        for(int i=0; i < length; i++){
            gene[i] = 0;
        }
    }

    public int[] initChromosome(){
        Random rnd = new Random();
        boolean random = false;

        for(int i=0; i < gene.length; i++){

            gene[i] = rnd.nextInt(FitnessCalculator.noFogDevices+2);
        }

        return gene;
    }

    public int[] getGene(){
        return gene;
    }

    public void setGene(int[] geneIn){
        gene = geneIn;
    }

    public String toString(){
        return(Arrays.toString(gene));
    }

    public int length(){

        return  gene.length;

    }
}
