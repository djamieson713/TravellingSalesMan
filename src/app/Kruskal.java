package app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

//find the minimum spanning tree for a weighted, connected and undirected graph
public class Kruskal {

    private static Edge[] edges;
    private static Cities[] cities;
    private static int noOfAllCities;
    public static int[][] DistanceArray;

    Kruskal(Edge[] edgs, Cities[] cits, int totalCities, int[][] dArray) {
        edges = edgs;
        cities = cits;
        noOfAllCities = totalCities;
        DistanceArray = dArray;
        FilterEdgesByCities();
    }

    public void TestMinimumSpanningTree() {
        ArrayList<Edge> testEdges = new ArrayList<Edge>();
        Edge eg = new Edge(0, 1, 10);
        testEdges.add(eg);
        eg = new Edge(1, 3, 15);
        testEdges.add(eg);
        eg = new Edge(3, 2, 4);
        testEdges.add(eg);
        eg = new Edge(3, 0, 5);
        testEdges.add(eg);
        eg = new Edge(2, 0, 6);
        testEdges.add(eg);
        eg = new Edge(1, 4, 5);
        testEdges.add(eg);
        Collections.sort(testEdges);
        Edge[] testEdgeArray = new Edge[testEdges.size()];
        testEdgeArray = testEdges.toArray(testEdgeArray);
        int spanningDistance = CreateMinimunSpanningTree(testEdgeArray, 5, 1);
        System.out.printf("Kruskals minimum spanning distance for these 5 vertices is %d\n", spanningDistance);
    }

    public int ComputeLowerBounds() {
        int bestLowerBounds = 100000;
        int numCities = cities.length;
        for (int i = 0; i < cities.length; i++) {
            Edge[] subsetEdges = FilterEdgesBySubsetCities(cities[i].getCityCode());
            // printFilteredEdgeArray(subsetEdges);
            int holdOutCityCode = cities[i].getCityCode();
            // add the two shortest edges to the missing vertex, I don't think this is
            // correct, connecting the minimum edge makes sense, but the other edge must
            // form a Hamiltoniancycle, so it should connect to a node with only one edge
            int spanningDistance = CreateMinimunSpanningTree(subsetEdges, numCities - 1, holdOutCityCode);
            // if (spanningDistance > 0) {
            // int counter = 0;
            // int edgeCounter = 0;
            // int edgeCount = edges.length;
            // while ((counter < 1) && (edgeCounter < edgeCount)) {
            // Edge eg = edges[edgeCounter];
            // if ((eg.CityCodeBegin == cities[i].getCityCode()) || (eg.CityCodeEnd ==
            // cities[i].getCityCode())) {
            // spanningDistance += eg.distance;
            // counter++;
            // edgeCounter++;
            // } else {
            // edgeCounter++;
            // }
            // }
            if ((spanningDistance > 0) && (spanningDistance < bestLowerBounds)) {
                bestLowerBounds = spanningDistance;
            }
        }

        return bestLowerBounds;
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

    // all edges that do not reference the city, which has been removed
    public boolean isInSubsetSet(Edge ed, int cityCode) {
        for (int i = 0; i < cities.length; i++) {
            if ((ed.CityCodeBegin == cities[i].getCityCode()) && (ed.CityCodeBegin != cityCode)) {
                for (int j = 0; j < cities.length; j++) {
                    if ((ed.CityCodeEnd == cities[j].getCityCode()) && (ed.CityCodeEnd != cityCode)) {
                        return true;
                    }
                }
            }
        }

        return false;
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

    // remove one vertex (i.e. cityCode) and find minimum spanning tree of remaining
    // cities;
    public Edge[] FilterEdgesBySubsetCities(int cityCode) {
        ArrayList<Edge> filteredEdges = new ArrayList<Edge>();
        for (int i = 0; i < edges.length; i++) {
            Edge ed = edges[i];
            if (isInSubsetSet(ed, cityCode)) {
                filteredEdges.add(ed);
            }
        }

        // these are the only edges to be considered
        Edge[] filteredEdgeArray = new Edge[filteredEdges.size()];
        return filteredEdges.toArray(filteredEdgeArray);
    }

    // use the greedy algorithm to link the shortest path, check if all cities
    // linked and then add return path

    // validate that all cities are connected
    public int ValidationConnectionCheck(WeightedQuickUnionUF wf, ArrayList<Edge> acceptedEdges) {
        boolean bFlag = true;
        for (int i = 0; i < cities.length - 1; i++) {
            boolean isConnected = wf.connected(cities[i].getCityCode(), cities[i + 1].getCityCode());
            if (!isConnected) {
                System.out.printf("City %s is not connected to city %s\n", cities[i].toString(),
                        cities[i + 1].toString());
                bFlag = false;
            }
        }
        if (bFlag) {
            // System.out.printf("For Greedy algorithm, all cities are connected.\n");
            int totalDistance = 0;
            for (Edge edge : acceptedEdges) {
                totalDistance += edge.distance;

                // System.out.printf("Connecting %s with %s with distance %d\n",
                // Cities.valueOfCode(edge.CityCodeBegin).toString(),
                // Cities.valueOfCode(edge.CityCodeEnd),
                // edge.distance).toString();
            }
            // all cities must have two edges to form a hamiltonian cycle, find the two
            // cities
            // that have only a single edge
            HashMap<Integer, Integer> cityHash = new HashMap<Integer, Integer>();
            // initialize hash
            for (int i = 0; i < cities.length; i++) {
                cityHash.put(cities[i].getCityCode(), 0);
            }
            // now determine edges for each city code
            for (Edge edge : acceptedEdges) {
                int edgeCount = cityHash.get(edge.CityCodeBegin);
                cityHash.put(edge.CityCodeBegin, edgeCount + 1);
                edgeCount = cityHash.get(edge.CityCodeEnd);
                cityHash.put(edge.CityCodeEnd, edgeCount + 1);
            }

            // locate the two city codes that only has one edge, this is the city
            // we need to close the loop for
            ArrayList<Integer> lastCitiesToClose = new ArrayList<Integer>();
            Set<Integer> cityCodes = cityHash.keySet();
            for (Integer code : cityCodes) {
                if (cityHash.get(code) == 1) {
                    lastCitiesToClose.add(code);
                }
            }

            for (Integer cityCode : lastCitiesToClose) {
                // System.out.printf("Cities with one edge %s\n",
                // Cities.valueOfCode(cityCode).toString());
            }

            if (lastCitiesToClose.size() == 2) {
                // we need to link these last two cities
                // System.out.print("Connecting last two cities\n");
                int roundTripDistance = DistanceArray[lastCitiesToClose.get(0)][lastCitiesToClose.get(1)];
                totalDistance += roundTripDistance;
                // System.out.printf("Greedy algorithm distance = %d\n", totalDistance);
                return totalDistance;
            } else {
                // System.out.printf("Could not find two remaining cities with one edge\n");
                return -1;// problem with algorithm
            }
        }
        return -1; // problem with algorithm
    }

    // this algorithm does not take into account that every city must
    // be travelled to one time
    public int UpperBoundGreedy(int numCities) {
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
                int totalDistance = ValidationConnectionCheck(wf, acceptedEdges);
                if (totalDistance > 0) {
                    return totalDistance;
                }
                return -1; // problem with algorithm
            }

        }

        return -1; // problem with the algorithm
    }

    public static void printEdgeArray() {
        for (int i = 0; i < edges.length; i++) {
            System.out.printf("%s\n", edges[i].toString());
        }
    }

    public static void printFilteredEdgeArray(Edge[] edges) {
        for (int i = 0; i < edges.length; i++) {
            System.out.printf("%s\n", edges[i].toString());
        }
    }

    public static int CreateMinimunSpanningTree(Edge[] subsetEdges, int numCities, int holdOutCityCode) {
        // edges that participate in the solution are acceptedEdges
        ArrayList<Edge> acceptedEdges = new ArrayList<Edge>();
        // the minimum spanning tree should have N-1 edges
        // where N is the number of cities
        int maxEdges = numCities - 1;
        int szConnectedGraph = 0;
        int spanningDistance = 0;
        WeightedQuickUnionUF wf = new WeightedQuickUnionUF(noOfAllCities);
        for (int i = 0; i < subsetEdges.length; i++) {
            boolean isConnected = wf.connected(subsetEdges[i].CityCodeBegin, subsetEdges[i].CityCodeEnd);
            // connect these cities using union
            if (!isConnected) {
                wf.union(subsetEdges[i].CityCodeBegin, subsetEdges[i].CityCodeEnd);
                acceptedEdges.add(subsetEdges[i]);
                // the number of vertices in this graph is points - 1
                szConnectedGraph = wf.getSize(subsetEdges[i].CityCodeBegin);
                if ((szConnectedGraph - 1) <= maxEdges) {
                    spanningDistance += subsetEdges[i].distance;
                } else {
                    break;
                }
            }
        }

        HashMap<Integer, Integer> cityHash = new HashMap<Integer, Integer>();
        // initialize hash
        for (int i = 0; i < cities.length; i++) {
            cityHash.put(cities[i].getCityCode(), 0);
        }
        // now determine edges for each city code
        for (Edge edge : acceptedEdges) {
            int edgeCount = cityHash.get(edge.CityCodeBegin);
            if (edgeCount >= 2) {
                return -1; // although graph is effecient some cities
                // are being visited more than once
            }
            cityHash.put(edge.CityCodeBegin, edgeCount + 1);
            edgeCount = cityHash.get(edge.CityCodeEnd);
            cityHash.put(edge.CityCodeEnd, edgeCount + 1);
        }

        // locate all city codes that only has one edge, this is the city
        // we need to close the loop for
        ArrayList<Integer> lastCitiesToClose = new ArrayList<Integer>();
        Set<Integer> cityCodes = cityHash.keySet();
        for (Integer code : cityCodes) {
            if (cityHash.get(code) == 1) {
                lastCitiesToClose.add(code);
            }
        }

        for (Integer cityCode : lastCitiesToClose) {
            // System.out.printf("Cities with one edge %s\n",
            // Cities.valueOfCode(cityCode).toString());
        }

        if (lastCitiesToClose.size() == 2) {
            // we need to link these last two cities
            // System.out.print("Connecting last two cities\n");
            int dist1 = DistanceArray[lastCitiesToClose.get(0)][holdOutCityCode];
            spanningDistance += dist1;
            int dist2 = DistanceArray[lastCitiesToClose.get(1)][holdOutCityCode];
            spanningDistance += dist1;
            // System.out.printf("Greedy algorithm distance = %d\n", spanningDistance);
            return spanningDistance;
        } else {
            // System.out.printf("Could not find two remaining cities with one edge\n");
            return -1;
        }

    }

    public static void PrintConnectedCities(ArrayList<Edge> acceptedEdges) {
        for (Edge edge : acceptedEdges) {
            System.out.printf("%s is connected to %s with distance %d\n",
                    Cities.valueOfCode(edge.CityCodeBegin).toString(), Cities.valueOfCode(edge.CityCodeEnd),
                    edge.distance);
        }
    }

    public static void PrintConnected(int cityCode1, int cityCode2, int distance) {
        System.out.printf("%s (holdout) is connected to %s with distance %d\n",
                Cities.valueOfCode(cityCode1).toString(), Cities.valueOfCode(cityCode2), distance);
    }

}
