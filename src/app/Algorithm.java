 package app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.Integer;

public class Algorithm {

    public static int[][] DistanceArray;
    public static Cities[] testCities;
    public static int lowerBound;
    public static int upperBound;
    public static Edge[] edges;

    // create random distances between cities
    public static void initializeDistanceArray() {
        ArrayList<Edge> initEdges = new ArrayList<Edge>();
        Map<Key, Boolean> map = new HashMap<Key, Boolean>();
        for (Cities firstCity : Cities.values()) {
            for (Cities secondCity : Cities.values()) {
                if (firstCity == secondCity) {
                    DistanceArray[firstCity.getCityCode()][secondCity.getCityCode()] = 0;
                } else {
                    if (DistanceArray[firstCity.getCityCode()][secondCity.getCityCode()] == 0) {
                        int distance = (int) (Math.random() * ((3000 - 0) + 1));
                        DistanceArray[firstCity.getCityCode()][secondCity.getCityCode()] = distance;
                        DistanceArray[secondCity.getCityCode()][firstCity.getCityCode()] = distance;
                        Edge ed = new Edge(firstCity.getCityCode(), secondCity.getCityCode(), distance);
                        Key key1 = new Key(firstCity.getCityCode(), secondCity.getCityCode());
                        Key key2 = new Key(secondCity.getCityCode(), firstCity.getCityCode());
                        if (!(map.containsKey(key1) || map.containsKey(key2))) {
                            initEdges.add(ed);
                        }
                    }
                }
            }
        }
        Collections.sort(initEdges);
        Edge[] initedges = new Edge[initEdges.size()];
        edges = initEdges.toArray(initedges);
        int totalNoOfCities = DistanceArray.length - 1;
        int edgesSize = edges.length;
        System.out.printf("Total Cities (vertices) = %d, Edges array size = %d\n", totalNoOfCities, edgesSize);
        boolean isTriangular = edges.length == ((totalNoOfCities * (totalNoOfCities - 1)) / 2);
        System.out.printf("Edges form a upper triangular array = %b\n", isTriangular);
    }

    public static void printDistanceArray() {
        for (Cities firstCity : Cities.values()) {
            for (Cities secondCity : Cities.values()) {
                System.out.printf("%5d ", DistanceArray[firstCity.getCityCode()][secondCity.getCityCode()]);
            }
            System.out.printf("\n");
        }
        System.out.printf("\n");
    }

    public static void main(String[] args) throws Exception {
        DistanceArray = new int[Cities.values().length + 1][Cities.values().length + 1];
        // for a complete graph the number of edges is N*(N-1)/2
        initializeDistanceArray();
        // printDistanceArray();
        System.out.println("Enter the number of cities (33 or less)");
        // Enter data using BufferReader
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        // Reading data using readLine
        String noOfCities = reader.readLine();
        // convert to number
        int noCities = Integer.parseInt(noOfCities);
        testCities = getRandomSlice(noCities);
        ComputeUpperBounds();
        // Kruskal kk = new Kruskal(edges, testCities, Cities.values().length,
        // DistanceArray);
        Greedy gd = new Greedy(edges, testCities, Cities.values().length, DistanceArray);
        // kk.TestMinimumSpanningTree();
        // int anotherUpperBound = kk.UpperBoundGreedy(testCities.length);
        // if (anotherUpperBound > 0) {
        // we have a valid path, that is a Hamiltonian circuit
        // System.out.printf("Upper Bound Distance (Greedy Algorithm): %d\n",
        // anotherUpperBound);
        // }

        // int bestLowerbound = kk.ComputeLowerBounds();
        // if (bestLowerbound < 100000) {
        // System.out.printf("The best lower bound(Kruskal): %d\n", bestLowerbound);
        // }

        Permutations perm = new Permutations(testCities, DistanceArray);

        // create a genetic algorithm to solve the problem
        GeneticAlgo genalgo = new GeneticAlgo(testCities, DistanceArray, testCities.length * 2, testCities.length * 50);
        // System.out.println("Initial Population for Genetic Algorithm\n");
        genalgo.PrintSummary();
        SimulationAnnealing sa = new SimulationAnnealing();
        sa.simulateTravellingProblem();
        return;

    }

    public static Cities[] getRandomSlice(int noOfCities) {
        if (noOfCities < 33) {
            Cities[] allcities = new Cities[32];
            for (int i = 0; i < Cities.values().length; i++) {
                allcities[i] = Cities.values()[i];
            }
            // now shuffle these cities randomly
            List<Cities> shuffleList = Arrays.asList(allcities);
            Collections.shuffle(shuffleList);
            shuffleList.toArray(allcities);
            // return only a subset
            Cities[] rcities = new Cities[noOfCities];
            System.arraycopy(allcities, 0, rcities, 0, noOfCities);
            return rcities;
        } else {
            Cities[] rcities = new Cities[0];
            return rcities;
        }
    }

    public static Cities[] ShiftCitiesLeft(Cities[] cities) {
        Cities city = cities[0];
        for (int i = 1; i < cities.length; i++) {
            // make the shift one element left
            cities[i - 1] = cities[i];
        }
        cities[cities.length - 1] = city;
        return cities;
    }

    // determine a theoretical worst case distance
    // by going to the shortest unused vertex, there are more than one
    // upper bound, depending on the starting point, so calculate all upper
    // bounds and return the least one
    public static void ComputeUpperBounds() {
        int bestDistance = 100000000;
        int distance = 0;
        Cities[] shiftedCities = new Cities[testCities.length];
        // copy the testCiites array into the shiftedCities array
        System.arraycopy(testCities, 0, shiftedCities, 0, testCities.length);
        // now compute upper bound for each order by shifting one city left
        for (int i = 0; i < shiftedCities.length; i++) {
            shiftedCities = ShiftCitiesLeft(shiftedCities);
            distance = ComputeUpperBound(shiftedCities);
            if (distance < bestDistance) {
                bestDistance = distance;
            }
        }
        System.out.printf("Upper bound path (nearest neighbor) = %d\n", bestDistance);
    }

    public static int ComputeUpperBound(Cities[] cities) {
        int upperCitybound = 10000;
        int upperPathbound = 0;
        int startingCityCode = cities[0].getCityCode();
        int nextCityCode = 0;
        int cityCounter = 0;
        // keep track of cities we have visited, only go to
        // unvisted cities
        Map<Integer, Boolean> visited = new HashMap<Integer, Boolean>();
        // to start off, we have visited the starting city
        visited.put(startingCityCode, true);
        while (cityCounter < cities.length - 1) {
            for (int j = 0; j < cities.length; j++) {
                // see if we have visited this city
                if (!visited.containsKey(cities[j].getCityCode())) {
                    int localDistance = DistanceArray[startingCityCode][cities[j].getCityCode()];
                    if (localDistance < upperCitybound) {
                        upperCitybound = localDistance;
                        nextCityCode = cities[j].getCityCode();
                    }
                }
            }
            // new starting point is nextCityCode, mark as visited
            visited.put(nextCityCode, true);
            startingCityCode = nextCityCode;
            upperPathbound += upperCitybound;
            upperCitybound = 10000;
            cityCounter++;
        }

        // one last step, return to the origin
        int returnDistance = DistanceArray[nextCityCode][cities[0].getCityCode()];
        upperPathbound += returnDistance;
        return upperPathbound;

    }

}