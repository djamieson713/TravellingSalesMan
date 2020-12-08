package app;

import java.math.BigInteger;

public class Permutations {
    public static int[][] DistanceArray;
    public Cities[] testCities;
    public int testDistance;
    public int smallestDistance;
    public Cities[] bestCities;

    Permutations(Cities[] testCities, int[][] dArray) {
        this.testCities = testCities;
        DistanceArray = dArray;
        testDistance = ComputeDistance(testCities);
        smallestDistance = testDistance;
        bestCities = new Cities[testCities.length];
        Stopwatch sw = new Stopwatch();
        ComputePermutations();
        double calcTime = sw.elapsedTime();
        PrintSummary();
        System.out.printf("Total Permutations Calculated Time = %f seconds \n", calcTime);
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

    public static void swap(Cities[] perm, int i, int j) {
        Cities temp = perm[i];
        perm[i] = perm[j];
        perm[j] = temp;
    }

    public static void reverse(Cities[] perm, int startPos) {
        // reverse the elements of an array from x to the end
        // and return new array, find the front and back of this array
        for (int i = startPos, j = perm.length - 1; i < j; i++, j--) {
            Cities temp = perm[i];
            perm[i] = perm[j];
            perm[j] = temp;
        }

    }

    public static boolean generatePermutation(Cities[] perm) {
        int maxx = -1; // this is a non-existent index
        int maxy = -1;
        // find the greatest k such there exists at least one element in the
        // set{P[k=1]...P[n]} > P[k], in an ordered array, this would be the
        // second to the last entry
        for (int x = 0; x < perm.length - 1; x++) {
            if (perm[x].getCityCode() < perm[x + 1].getCityCode()) {
                maxx = x;
            }
        }

        // there are no more permutations
        if (maxx == -1) {
            return false;
        }

        for (int y = 0; y < perm.length; y++) {
            if (perm[y].getCityCode() > perm[maxx].getCityCode()) {
                maxy = y;
            }
        }

        // System.out.printf("Maxx = %d, Maxy = %d \n", maxx, maxy);
        swap(perm, maxx, maxy);
        // System.out.println("Here are the cities before partial reversal");
        // printCities(perm);
        reverse(perm, maxx + 1);
        return true;
    }

    static void printCities(Cities[] perm, int distance) {
        for (int i = 0; i < perm.length; i++) {
            System.out.printf(" %s ", perm[i].toString());
        }
        System.out.println();
        System.out.printf("Total Distance = %d \n", distance);
        System.out.println();
    }

    public void PrintSummary() {
        System.out.println("Summary:");
        System.out.printf("The smallest tour distance is: %d\n", smallestDistance);
        System.out.println("Best Path:");
        printCities(bestCities, smallestDistance);
    }

    public void ComputePermutations() {
        boolean morePermutations = true;
        BigInteger noPermutations = factorialHavingLargeResult(testCities.length);
        System.out.printf("There are %d permutations\n", noPermutations);
        BigInteger counter = BigInteger.ONE;
        while (morePermutations) {
            testDistance = ComputeDistance(testCities);
            if (testDistance <= smallestDistance) {
                smallestDistance = testDistance;
                // Copy elements of testCities[] to bestCities[]
                System.arraycopy(testCities, 0, bestCities, 0, testCities.length);
            }
            if (counter.mod(BigInteger.valueOf(100L)) == BigInteger.ZERO) {
                PrintProgress(counter, noPermutations);
            }

            morePermutations = generatePermutation(testCities);
        }
    }

    public void PrintProgress(BigInteger counter, BigInteger noPermutations) {
        BigInteger perCentDone = counter.divide(noPermutations).multiply(BigInteger.valueOf(100L));
        System.out.printf("Percent done = %d\n", perCentDone);
        System.out.printf("Smallest distance so far = %d\n", smallestDistance);
    }

    public BigInteger factorialHavingLargeResult(int n) {
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++)
            result = result.multiply(BigInteger.valueOf(i));
        return result;
    }

}
