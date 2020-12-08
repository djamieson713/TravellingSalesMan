package app;

import java.util.ArrayList;

public class Dynamic {
    private static Cities[] cities;
    public static int[][] DistanceArray;

    public Dynamic(Cities[] cits, int[][] dArray) {
        cities = cits;
        DistanceArray = dArray;
    }

    public int ComputeDistance(Cities[] subset, int cityCode) {
        if (subset.length < 2) {
            return 0;
        }

        if (subset.length == 2) {
            return DistanceArray[subset[0].getCityCode()][subset[1].getCityCode()];
        } else {
            ArrayList<Integer> distArray = new ArrayList<Integer>();
            for (int i = 1; i < subset.length; i++) {
                // create a new subset, remember first city must be in every subset
                Cities[] rcities = new Cities[subset.length - 1];
                // add all cities to the new subset, except the ith
                for (int j = 0, k = 0; j < subset.length; j++, k++) {
                    if (i != j) {
                        rcities[j] = subset[k];
                    }
                }
                // int distance = ComputeDistance(rcities, j) + DistanceArray[]

            }
        }

        return 0;
    }
}
