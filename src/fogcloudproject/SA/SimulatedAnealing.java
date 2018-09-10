package fogcloudproject.SA;


import fogcloudproject.FitnessCalculator;
import fogcloudproject.FogCloudProject;

import java.util.Arrays;
import java.util.Random;

public class SimulatedAnealing {

    double temp = 1000;
    double coolingRate = 0.0001; // 0.9999
    double finalTemperature = 0.001;
    int bound;
    int length;
    int equilibirium = 20;
    FitnessCalculator fc;

    public SimulatedAnealing(int bound, int length, FitnessCalculator fc) {
        this.bound = bound;
        this.length = length;
        this.fc = fc;
        SA();
    }

    public int getFourDigit(int d){
        String s = d + "";
        int l = s.length();
        if(l > 4){
            return ((int) (d / Math.pow(10 , -(4-l))));
        } else {
            return d;
        }
    }

    public double acceptanceProbability(int state, int newState, double temp) {
//        if (newState < state) {
//            return 1.0;
//        }
        newState = getFourDigit(newState);
        state = getFourDigit(state);
        double s = Math.exp((state - newState) / temp);
//        System.out.println(s);
        return s;
    }

    public void SA() {
        // INIT a RAND SOLOUTION
        Random rand = new Random();
        int[] replace = new int[length];
        int[] best = new int[length];
        double fitnessBest;
        for (int i = 0; i < length; i++) {
            replace[i] = rand.nextInt(bound);
        }

        for (int i = 0; i < replace.length; i++) {
            best[i] = replace[i];
        }

        fitnessBest = fc.calculateFitness(best);
        int [] neighbor;

        while (temp > finalTemperature) {

            for (int i = 0; i < equilibirium; i++) {
                neighbor = nextNeighbour(replace);

                int stateFitness = (int) fc.calculateFitness(replace);
                int neighbourStateFitness = (int) fc.calculateFitness(neighbor);

//                System.out.println(stateFitness);
//                System.out.println(neighbourStateFitness);
                if (neighbourStateFitness < stateFitness) {
                    replace = neighbor;
                    stateFitness = neighbourStateFitness;
                    if (neighbourStateFitness < fitnessBest){
                        best = neighbor;
                        fitnessBest = neighbourStateFitness;
                    }
                } else {
                    if (acceptanceProbability(stateFitness, neighbourStateFitness, temp) > Math.random()) {
                        replace = neighbor;
                        stateFitness = neighbourStateFitness;
                    }
                }
            }
            // Cool system
            temp *= 1 - coolingRate;
        }
        fc.calculateFitness(best);
        System.out.println("----------RUN WITH SA---------");
        FogCloudProject.printResult(fc,best);
    }


    public int[] nextNeighbour(int[] r) {
        int[] replace = r.clone();
        Random rand = new Random();
        int index = rand.nextInt(length);
        int changed = rand.nextInt(bound);
        while (replace[index]==changed)changed = rand.nextInt(bound);
        replace[index]=changed;
        return replace;

    }


}
