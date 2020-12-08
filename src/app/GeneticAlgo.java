package app;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GeneticAlgo {
    // this will use techniques in genetic algorithms to
    // solve the travelling salesperson problem
    public Cities[][] population;
    public int noInPopulation;
    public static int[][] DistanceArray;
    public double[] fitness;
    public Cities[] bestOrder;
    public int bestGenerationDistance;
    public Cities[] bestGenerationOrder;

    GeneticAlgo(Cities[] cities, int[][] disarray, int noInPopulation, int maxGenerations) {
        this.bestGenerationDistance = 20000; // each generation will find a better path
        this.noInPopulation = noInPopulation;
        Stopwatch sw = new Stopwatch();
        population = new Cities[noInPopulation][cities.length];
        fitness = new double[noInPopulation];
        DistanceArray = disarray;
        generatePopulation(cities, noInPopulation);
        for (int i = 0; i < maxGenerations; i++) {
            createNewGeneration();
            if ((((i + 100) / 100) % 100) == 0) {
                System.out.printf("Generation: %d has best distance of %d\n", i, bestGenerationDistance);
            }
        }
        System.out.printf("Total Genetic Algo time = %f seconds \n", sw.elapsedTime());

    }

    // with each new ordering, insert the row into 2d array
    protected void insertRow(Cities[] cities, int rowNo) {
        for (int i = 0; i < cities.length; i++) {
            population[rowNo][i] = cities[i];
        }
    }

    // generate an initial population of various orders of cities
    public void generatePopulation(Cities[] cities, int noInPopulation) {
        // first copy the initial order of cities into a new array
        for (int i = 0; i < noInPopulation; i++) {
            Cities newOrderOfCities[] = new Cities[cities.length];
            System.arraycopy(cities, 0, newOrderOfCities, 0, cities.length);
            // now shuffle these cities randomly
            List<Cities> newCitiesList = Arrays.asList(newOrderOfCities);
            Collections.shuffle(newCitiesList);
            newCitiesList.toArray(newOrderOfCities);
            // now add to population
            insertRow(newOrderOfCities, i);
        }
    }

    public void printPopulation(int noOfRows) {
        for (int i = 0; i < noOfRows; i++) {
            for (int j = 0; j < population[0].length; j++) {
                System.out.printf("%s ", population[i][j]);
            }
            System.out.println("\n");
        }
    }

    public static int ComputeDistance(Cities[] cities) {
        int sum = 0;
        for (int i = 0; i < cities.length - 1; i++) {
            sum += DistanceArray[cities[i].getCityCode()][cities[i + 1].getCityCode()];
        }
        // remember this must be round trip back to origin
        sum += DistanceArray[cities[cities.length - 1].getCityCode()][cities[0].getCityCode()];
        return sum;
    }

    // compute the fitness of every member of the population, in this case
    // use the distance as measure of fitness, also find the best order in
    // the population
    public void ComputeFitness() {

        int bestDistance = 1000000;
        for (int i = 0; i < noInPopulation; i++) {
            double distance = ComputeDistance(population[i]);
            fitness[i] = 1 / (distance + 1);
            if (distance < bestDistance) {
                bestDistance = (int) distance;
                bestOrder = population[i];
            }
        }

        if (bestDistance < bestGenerationDistance) {
            bestGenerationDistance = bestDistance;
            bestGenerationOrder = bestOrder;
        }

    }

    public void NormalizeFitness() {
        double sum = 0;
        for (int i = 0; i < noInPopulation; i++) {
            sum += fitness[i];
        }

        for (int i = 0; i < noInPopulation; i++) {
            fitness[i] = fitness[i] / sum;
        }
    }

    public void createNewGeneration() {
        // first compute fitness of every member of the last generation
        // and normalize fitness
        ComputeFitness();
        NormalizeFitness();
        NextGeneration();
    }

    public void NextGeneration() {
        Cities[][] newPopulation = new Cities[noInPopulation][population[0].length];
        for (int i = 0; i < noInPopulation; i++) {
            Cities[] order1 = pickOne();
            Cities[] order2 = pickOne();
            Cities[] crossover = crossOver(order1, order2);
            mutate(crossover, 0.2);
            newPopulation[i] = crossover;
        }

        population = newPopulation;

    }

    // take a random slice of a given order, and crossover with
    // cities from a separate order
    public Cities[] crossOver(Cities[] order1, Cities[] order2) {
        int index1 = ThreadLocalRandom.current().nextInt(order1.length);
        int index2 = ThreadLocalRandom.current().nextInt(index1, order1.length);
        Cities[] newOrder = new Cities[order1.length];
        Cities[] subOrder = Arrays.copyOfRange(order1, index1, index2 + 1);
        for (int i = 0; i < subOrder.length; i++) {
            newOrder[i] = subOrder[i];
        }

        int j = subOrder.length;
        int i = 0;

        while (j < newOrder.length && i < order2.length) {
            if (!ContainsElement(newOrder, order2[i])) {
                newOrder[j] = order2[i];
                i++;
                j++;
            } else {
                i++;
            }
        }
        return newOrder;
    }

    public boolean ContainsElement(Cities[] cities, Cities city) {
        for (int i = 0; i < cities.length; i++) {
            if (cities[i] == city) {
                return true;
            }
        }

        return false;
    }

    private Cities[] pickOne() {
        int index = 0;
        double r = ThreadLocalRandom.current().nextDouble(1);
        while (r > 0) {
            r = r - fitness[index];
            index++;
        }

        index--;

        return population[index];
    }

    public static void swap(Cities[] perm, int i, int j) {
        Cities temp = perm[i];
        perm[i] = perm[j];
        perm[j] = temp;
    }

    public void mutate(Cities[] order, double mutationRate) {
        double threshold = ThreadLocalRandom.current().nextDouble(1);
        if (mutationRate > threshold) {
            int index1 = ThreadLocalRandom.current().nextInt(order.length);
            int index2 = ThreadLocalRandom.current().nextInt(order.length);
            swap(order, index1, index2);
        }
    }

    public int GetBestDistance() {
        return bestGenerationDistance;
    }

    public Cities[] GetBestOrder() {
        return bestGenerationOrder;
    }

    public void PrintSummary() {
        System.out.printf("The best generational distance was %d \n", this.GetBestDistance());
        System.out.printf("Best order of cities\n");
        for (int i = 0; i < this.GetBestOrder().length; i++) {
            System.out.printf("%s ", this.GetBestOrder()[i].toString());
        }
    }

}
