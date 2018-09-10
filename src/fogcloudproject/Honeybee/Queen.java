package fogcloudproject.Honeybee;

import java.util.Arrays;

public class Queen {

    Chromosome chr;

    Queen(int length){

        chr = new Chromosome(length);
        chr.initChromosome();

    }

    public Chromosome getChro(){

        return chr;
    }

    int[] getGene(){

        return chr.getGene();
    }

    void setChr(Chromosome chroIn){

        chr = chroIn;
    }

    int chroLength(){

        return chr.gene.length;
    }

    public String toString(){

        return Arrays.toString(chr.getGene());

    }

}
