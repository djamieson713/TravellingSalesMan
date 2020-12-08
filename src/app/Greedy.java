package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Greedy {
    private static Edge[] edges;
    private static Cities[] cities;
    private static int noOfAllCities;
    public static int[][] DistanceArray;

    public Greedy(Edge[] edgs, Cities[] cits, int totalCities, int[][] dArray) {
        edges = edgs;
        cities = cits;
        noOfAllCities = totalCities;
        DistanceArray = dArray;
        FilterEdgesByCities();
        int status = LowerBoundGreedy(cities.length);
        if (status != -1) {
            System.out.printf("All cites connected in distance of %d, but not a tour\n", status);
        }
    }

    public void FilterEdgesByCities() {
        ArrayList<Edge> filteredEdges = new ArrayList<Edge>();
        for (int i = 0; i < edges.length; i++) {
            Edge ed = edges[i];
            if (isInSet(ed)) {
                filteredEdges.add(ed);
            }
        }
        // these are the only edges to be considered
        Edge[] filteredEdgeArray = new Edge[filteredEdges.size()];
        edges = filteredEdges.toArray(filteredEdgeArray);
    }

    // is the starting node and ending node, in the list of cities in the graph
    public boolean isInSet(Edge ed) {
        for (int i = 0; i < cities.length; i++) {
            if (ed.CityCodeBegin == cities[i].getCityCode()) {
                for (int j = 0; j < cities.length; j++) {
                    if (ed.CityCodeEnd == cities[j].getCityCode()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public int LowerBoundGreedy(int numCities) {
        ArrayList<Edge> acceptedEdges = new ArrayList<Edge>();
        int spanningDistance = 0;
        int szConnectedGraph = 0; // at the end every city must be connected
        // edges must be filtered by cities, going from shortest to longest
        WeightedQuickUnionUF wf = new WeightedQuickUnionUF(noOfAllCities);
        // start the ball rolling by connecting the smallest edge
        wf.union(edges[0].CityCodeBegin, edges[0].CityCodeEnd);
        acceptedEdges.add(edges[0]);
        for (int i = 1; i < edges.length; i++) {
            boolean isConnected = wf.connected(edges[i].CityCodeBegin, edges[i].CityCodeEnd);
            if (!isConnected) {
                // connect these cities using union
                wf.union(edges[i].CityCodeBegin, edges[i].CityCodeEnd);
                acceptedEdges.add(edges[i]);
                szConnectedGraph = wf.getSize(edges[i].CityCodeBegin);
                spanningDistance += edges[i].distance;
            }
            // check if we connected all the cities, and then add distance for return trip
            if (szConnectedGraph == numCities) {
                return spanningDistance;

            }
        }
        return -1;
    }

    public int CountEdges(ArrayList<Edge> acceptedEdges, HashMap<Integer, Integer> cityHash) {
        for (Edge edge : acceptedEdges) {
            int beginCityCode = edge.CityCodeBegin;
            int endCityCode = edge.CityCodeEnd;
            int edgeCount = cityHash.get(beginCityCode);
            if (edgeCount > 1) {
                return -2; // this means that a city was visited, more than once
            } else {
                cityHash.put(beginCityCode, edgeCount + 1);
            }

            edgeCount = cityHash.get(endCityCode);
            if (edgeCount > 1) {
                return -2; // this means that a city was visited, more than once
            } else {
                cityHash.put(endCityCode, edgeCount + 1);
            }
        }
        // at this point we know all cities are connected, and no
        // city is visited more than once.
        return 0;
    }

    public int LinkRemainingCities(ArrayList<Edge> acceptedEdges, HashMap<Integer, Integer> cityHash) {
        // indetify the last two cities that are not linked
        int code1 = 0;
        int code2 = 0;
        for (Map.Entry<Integer, Integer> cityEntry : cityHash.entrySet()) {
            int edgeCount = (int) cityEntry.getValue();
            if (edgeCount == 1) {
                if (code1 == 0) {
                    code1 = (int) cityEntry.getKey();
                } else {
                    code2 = (int) cityEntry.getKey();
                }
            }
        }

        if ((code1 == 0) || (code2 == 0)) {
            return -1; // could not find remaining cities to link
        } else {
            return DistanceArray[code1][code2];
        }

    }

}
