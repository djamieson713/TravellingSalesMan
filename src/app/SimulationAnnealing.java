package app;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SimulationAnnealing {
    public double temperature = 100000;
    public double coolingRate = 0.00003;
    public Cities[] currentCities;

    public static int[][] DistanceArray = Algorithm.DistanceArray;
    public Cities[] bestOrderOfCities;
    public int bestTourDistance = 0;

    public void simulateTravellingProblem() {
        // generate a random solution
        Stopwatch sw = new Stopwatch();
        int annealingCount = 1;
        Cities[] testCities = Algorithm.testCities;
        Cities[] newOrderOfCities = new Cities[testCities.length];
        bestOrderOfCities = new Cities[testCities.length];
        System.arraycopy(testCities, 0, newOrderOfCities, 0, testCities.length);
        // now shuffle these cities randomly
        List<Cities> newCitiesList = Arrays.asList(newOrderOfCities);
        Collections.shuffle(newCitiesList);
        newCitiesList.toArray(bestOrderOfCities);
        bestTourDistance = ComputeTour(bestOrderOfCities);
        System.out.println();
        System.out.printf("Initial Simulated Annealing Distance is %d\n", bestTourDistance);

        while (temperature > 1) {
            Cities[] rcities = randomSwap(bestOrderOfCities);
            int newTourDistance = ComputeTour(rcities);

            if (acceptanceProbability(bestTourDistance, newTourDistance, temperature) > Math.random()) {
                bestOrderOfCities = rcities;
            }

            if (newTourDistance < bestTourDistance) {
                bestTourDistance = newTourDistance;
            }

            temperature *= (1 - coolingRate);
            if (annealingCount % 100 == 0) {
                System.out.printf("Current simulated annealing distance is %d, iteration %d\n", bestTourDistance,
                        annealingCount);
            }
        }

        System.out.printf("Final Simulated Annealing Distance is %d\n", bestTourDistance);
        System.out.printf("Simulated annealing time = %f seconds \n", sw.elapsedTime());
    }

    public Cities[] randomSwap(Cities[] currentCities) {
        Cities[] newOrderOfCities = new Cities[currentCities.length];
        System.arraycopy(currentCities, 0, newOrderOfCities, 0, currentCities.length);
        int travelPosition1 = ThreadLocalRandom.current().nextInt(0, currentCities.length);
        int travelPosition2 = ThreadLocalRandom.current().nextInt(0, currentCities.length);
        // System.out.printf("Travel position1 = % d, and travel position2 = %d\n",
        // travelPosition1, travelPosition2);
        // now swap them
        swap(newOrderOfCities, travelPosition1, travelPosition2);
        return newOrderOfCities;
    }

    public static void swap(Cities[] perm, int i, int j) {
        Cities temp = perm[i];
        perm[i] = perm[j];
        perm[j] = temp;
    }

    public static int ComputeTour(Cities[] cities) {
        int size = cities.length;
        int tourDistance = 0;
        for (int i = 0; i < size; i++) {
            int startingCityCode = cities[i].getCityCode();

            int destinationCityCode;
            if (i + 1 < size) {
                destinationCityCode = cities[i + 1].getCityCode();
            } else {
                destinationCityCode = cities[0].getCityCode();
            }

            tourDistance += DistanceArray[startingCityCode][destinationCityCode];
        }

        return tourDistance;

    }

    public double acceptanceProbability(int distance, int newDistance, double temperature) {

        if (newDistance < distance) {
            return 1.0;
        }

        return Math.exp((distance - newDistance) / temperature);
    }

}
