package fogcloudproject.Honeybee;

import com.sun.source.tree.SynchronizedTree;
import fogcloudproject.FitnessCalculator;
import fogcloudproject.RandomFit;

import java.util.Arrays;
import java.util.Random;

public class Honeybee {

    int spermathecaSize;
    Queen queen;
    Spermatheca spm;
    int time = 0;
    int threadNumber;
    int clength;
    public static FitnessCalculator fc;

    public Queen getQueen() {
        return queen;
    }

    // constructor
    public Honeybee(int spermathecaSize, int threadNumber, FitnessCalculator fc) {
        this.fc = fc;
        this.spermathecaSize = spermathecaSize;
        this.clength = fc.getVmArray().length();
        queen = new Queen(clength);
        spm = new Spermatheca(spermathecaSize, fc.getVmArray().length());
        this.threadNumber = threadNumber;
    }

    // function for mating and implementing multithreading
    void matingFlight() {

        Thread[] threadarray = new Thread[threadNumber];
        int threadSpmSize = spermathecaSize / threadNumber;
        Spermatheca[] threadSpermatheca = new Spermatheca[threadNumber];
        int[] timeThread = new int[threadNumber];

        for (int i = 0; i < threadNumber; i++) {

            int iter = i;

            threadarray[i] = new Thread(new Runnable() {
                @Override
                public void run() {

                    time = 0;
                    Random rnd = new Random();

                    Drone drone = new Drone();
                    drone.setRf(new RandomFit(FitnessCalculator.vmArray, fc, FitnessCalculator.devices, FitnessCalculator.noEmbededDevices, FitnessCalculator.noFogDevices));
                    drone.produceDrone(clength);
                    threadSpermatheca[iter] = new Spermatheca(threadSpmSize, clength);

                    final double beta = 0.6 + 0.3 * rnd.nextDouble();
                    double SMax = Math.abs((fitness(queen.getChro()) - fitness(drone.getChro())) / Math.log(beta));
                    double Smin = Math.abs((fitness(queen.getChro()) - fitness(drone.getChro())) / Math.log(0.05));
                    SMax /= 10;
                    Smin /= 10;
                    double queenSpeed = SMax;

                    while (queenSpeed > Smin && threadSpmSize > timeThread[iter]) {

                        if (probablity(fitness(queen.getChro()), fitness(drone.getChro()), queenSpeed) > rnd.nextDouble()) {

                            threadSpermatheca[iter].setSpermatheca(drone.getChro(), timeThread[iter]);
                            timeThread[iter]++;
                        }
                        queenSpeed = 0.999 * queenSpeed;
                        drone.produceDrone(clength);
                    }

                    for (int i = 0; i < timeThread[iter]; i++) {

                        Chromosome chro;
                        chro = crossOver(queen.getChro(), threadSpermatheca[iter].getchroSpermatheca(i));
                        droneLocalSearch(chro);

                        threadSpermatheca[iter].setSpermatheca(chro, i);
                    }

                }
            });

        }

        for (int i = 0; i < threadNumber; i++) {

            threadarray[i].start();

        }

        for (int i = 0; i < threadNumber; i++) {


            try {
                threadarray[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            time = time + timeThread[i];
        }


        int sum = 0;

        for (int i = 0; i < threadNumber; i++) {

            for (int j = 0; j < timeThread[i]; j++) {

                if (i == 0) {
                    spm.setSpermatheca(threadSpermatheca[i].getchroSpermatheca(j), j);
                    //System.out.println(Arrays.toString(spm.getSpermatheca(j)));
                } else {
                    spm.setSpermatheca(threadSpermatheca[i].getchroSpermatheca(j), j + sum);
                }

            }

            sum += timeThread[i];

        }

    }


    //calculating problility for acceptance
    double probablity(int queenFitness, int droneFitness, double queenSpeed) {

        if (droneFitness < queenFitness)
            return 1;
        else
            return Math.exp((queenFitness - droneFitness) / queenSpeed);

    }

    //calculating fitness

    public static int fitness(Chromosome chIn) {
        FitnessCalculator fitnessCalculator = new FitnessCalculator();
        return ((int) fitnessCalculator.calculateFitness(chIn.getGene()));
//        int fitness = 0;
//        int size = 0;
//        int maxSize = Test.w;
//
//        for(int i=0; i < benefit.length; i++){
//
//            if(chIn.getGene()[i] == 1){
//
//                size += volume[i];
//                fitness += benefit[i];
//            }
//        }
//
//        if(size <= maxSize){
//            return fitness;
//        }
//        else {
//            return 0;
//        }
    }

    // calculating crossover

    public Chromosome crossOver(Chromosome chr1, Chromosome chr2) {

        int[] getchr1 = chr1.getGene();
        int[] getchr2 = chr2.getGene();

        int[] gene1 = new int[getchr1.length];
        int[] gene2 = new int[getchr2.length];

        for (int i = 0; i < getchr1.length; i++) {

            gene1[i] = getchr1[i];
            gene2[i] = getchr2[i];
        }

        int l = gene1.length;

        int[] marked = createUniqueNumber(l);

        for (int i = 0; i < marked.length; i++) {
            //int temp = gene2[marked[i]];
            int temp = gene1[marked[i]];
            //gene2[marked[i]] = gene1[marked[i]];
            gene2[marked[i]] = temp;
            //gene1[marked[i]] = temp;
        }


        //Chromosome temp1 = new Chromosome(l);
        Chromosome temp2 = new Chromosome(l);
        //temp1.setGene(gene1);
        temp2.setGene(gene2);


        //int fit1 = fitness(temp1, Test.volume, Test.benefit);
        //int fit2 = fitness(temp2, Test.volume, Test.benefit);

        //if(fit1 > fit2){
        //    return temp1;
        //}
        //else{
        //    return temp2;
        return temp2;
        //}
    }


    int[] createUniqueNumber(int n) {

        int[] result = new int[n / 2];
        int a[] = new int[n];

        for (int i = 0; i < n; i++) {
            a[i] = i;
        }

        int x = n;
        Random rnd = new Random();
        for (int i = 0; i < n / 2; i++) {
            int k = rnd.nextInt(x);
            result[i] = a[k];
            a[k] = a[x - 1];
            x--;
        }

        return result;

    }


    // function for generating broods
    void generateBrood() {

        if (time != 0) {

            Random rnd = new Random();
            int randSelect = rnd.nextInt(time);

            spm.setSpermatheca(mutation(spm.getchroSpermatheca(randSelect)), randSelect);

            spm.sortFitness(time);
        }
        if (time != 0) {

            int broodBestFitness = fitness(spm.getchroSpermatheca(0));
            int queenFitness = fitness(queen.getChro());

            if (broodBestFitness < queenFitness) {
                queen.setChr(spm.getchroSpermatheca(0));
//                fc.replaceVM = queen.getGene();
            }
        }

    }

    //local search function
    public void localSearch(Queen qIn) {

        int[] fitArray = new int[qIn.chroLength()];
        int[] qGene = qIn.getGene();
        int[] tempgene = new int[qGene.length];
        int[] bestgene = qIn.getGene().clone();
        Chromosome chtemp = new Chromosome(qGene.length);
        int minNeighbour = Integer.MAX_VALUE;

        for (int i = 0; i < fc.getVmArray().length(); i++) {
            for (int j = 0; j < qGene.length; j++) {
                tempgene[j] = qGene[j];
            }
            int PreviousIndex = tempgene[i];
            for (int j = 0; j < FitnessCalculator.noFogDevices + 2; j++) {
                if (PreviousIndex != j) {
                    tempgene[i] = j;
                    FitnessCalculator fitnessCalculator = new FitnessCalculator();
                    int fitness = (int) fitnessCalculator.calculateFitness(tempgene);
                    if (fitness < minNeighbour) {
                        minNeighbour = fitness;
                        bestgene = tempgene.clone();
                    }
                }
            }
        }


//        for(int i = 0; i < tempgene.length;i++){
//            int a = qGene[i];
//            if(a == 1)
//                tempgene[i] = 0;
//            else
//                tempgene[i] = 1;
//
//            chtemp.setGene(tempgene);
//            fitArray[i] = fitness(chtemp, fc);
//
//            for(int j=0; j < qGene.length; j++)
//                tempgene[i] = qGene[i];
//
//        }
//
//        if(tempgene[findMax(fitArray)] == 1)
//            tempgene[findMax(fitArray)] = 0;
//        else
//            tempgene[findMax(fitArray)] = 1;

        chtemp.setGene(bestgene);


        int fit1 = fitness(queen.getChro());
        int fit2 = fitness(chtemp);

        if (fit2 < fit1) {
            queen.setChr(chtemp);
        }
    }

    public void droneLocalSearch(Chromosome CIn) {

        int[] fitArray = new int[CIn.length()];
        int[] qGene = CIn.getGene();
        int[] tempgene = new int[qGene.length];
        int[] bestgene = CIn.getGene().clone();
        Chromosome chtemp = new Chromosome(qGene.length);
        int minNeighbour = Integer.MAX_VALUE;

        for (int i = 0; i < fc.getVmArray().length(); i++) {
            for (int j = 0; j < qGene.length; j++) {
                tempgene[j] = qGene[j];
            }
            int PreviousIndex = tempgene[i];
            for (int j = 0; j < FitnessCalculator.noFogDevices + 2; j++) {
                if (PreviousIndex != j) {
                    tempgene[i] = j;
                    FitnessCalculator fitnessCalculator = new FitnessCalculator();
                    int fitness = (int) fitnessCalculator.calculateFitness(tempgene);
                    if (fitness < minNeighbour) {
                        minNeighbour = fitness;
                        bestgene = tempgene.clone();
                    }
                }
            }
        }


//        int MaxNeighbour = 0;
//
//        for (int i = 0; i < qGene.length; i++) {
//            tempgene[i] = qGene[i];
//        }
//
//
//        for (int i = 0; i < tempgene.length; i++) {
//            int a = qGene[i];
//            if (a == 1)
//                tempgene[i] = 0;
//            else
//                tempgene[i] = 1;
//
//            chtemp.setGene(tempgene);
//            fitArray[i] = fitness(chtemp);
//
//            for (int j = 0; j < qGene.length; j++)
//                tempgene[i] = qGene[i];
//
//        }
//
//        if (tempgene[findMax(fitArray)] == 1)
//            tempgene[findMax(fitArray)] = 0;
//        else
//            tempgene[findMax(fitArray)] = 1;

        chtemp.setGene(bestgene);


        int fit1 = fitness(queen.getChro());
        int fit2 = fitness(chtemp);

        if (fit2 < fit1) {
            queen.setChr(chtemp);
        }
    }

    public Chromosome mutation(Chromosome chroIn) {

        int[] chrArray;
        chrArray = chroIn.getGene();

        Random rnd = new Random();
        int rnd1 = rnd.nextInt(chrArray.length);
        int rnd2 = rnd.nextInt(chrArray.length);

        while (rnd1 == rnd2) {

            rnd2 = rnd.nextInt(chrArray.length);
        }

        int temp = chrArray[rnd1];
        chrArray[rnd1] = chrArray[rnd2];
        chrArray[rnd2] = temp;

        Chromosome chrtemp = new Chromosome(chrArray.length);
        chrtemp.setGene(chrArray);

        return chrtemp;
    }

    static int findMax(int[] arrIn) {
        int Max = arrIn[0];
        int MaxIndex = 0;

        for (int i = 0; i < arrIn.length; i++) {
            if (arrIn[i] >= Max) {
                Max = arrIn[i];
                MaxIndex = i;
            }
        }

        return MaxIndex;
    }

    public void result() {


        spm.sortFitness(time);

        for (int i = 0; i < time; i++) {
            //System.out.print(Arrays.toString(spm.getSpermatheca(i)));
            //System.out.println();
            Chromosome chro;
            chro = spm.getchroSpermatheca(i);
            System.out.print(chro + "\t");

            System.out.println(fitness(chro));
        }

    }

    public void startAlgorithm(int generationNumber) {

        for (int i = 0; i < generationNumber; i++) {
            localSearch(queen);
            matingFlight();
            generateBrood();
            localSearch(queen);
            //result();
            //System.out.println(lastResult());
        }

    }

    public int lastResult() {
        return fitness(queen.getChro());

    }

}
