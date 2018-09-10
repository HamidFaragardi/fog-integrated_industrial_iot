package fogcloudproject.Honeybee;

public class Spermatheca {

    Chromosome[] chromosome;
    int[] fitArray;
    int spermathecaSize;
    int chroLength;

    Spermatheca(int spermathecaSize, int chroLength){

        this.spermathecaSize = spermathecaSize;
        chromosome = new Chromosome[spermathecaSize];
        this.chroLength = chroLength;

    }

    public void setSpermatheca(Chromosome chIn, int number){
        chromosome[number] = chIn;
    }

    public int[] getSpermatheca(int number){

        return chromosome[number].getGene();

    }

    public Chromosome getchroSpermatheca(int number){

        return chromosome[number];

    }

    public int getSpermathecaSize()
    {
        return chromosome.length;
    }

    public void sortFitness(int select){

        fitArray = new int[select];

        for(int i=0; i < select; i++){

            fitArray[i] = Honeybee.fitness(chromosome[i]);
        }

        //bubble sort
        for(int i=0; i < fitArray.length-1; i++){

            for(int j= 0; j < fitArray.length-i-1; j++){

                if(fitArray[j] < fitArray[j+1]){

                    int temp = fitArray[j+1];
                    Chromosome temp2 = chromosome[j+1];

                    fitArray[j+1] = fitArray[j];
                    chromosome[j+1] = chromosome[j];
                    fitArray[j] = temp;
                    chromosome[j] = temp2;
                }
            }
        }

    }
}
