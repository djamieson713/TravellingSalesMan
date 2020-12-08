package app;

public class Edge implements Comparable<Edge> {
    public int CityCodeBegin;
    public int CityCodeEnd;
    public int distance;

    Edge(int begin, int end, int d) {
        this.CityCodeBegin = begin;
        this.CityCodeEnd = end;
        this.distance = d;
    }

    @Override
    public String toString() {
        return "Edge: BeginCity " + String.valueOf(this.CityCodeBegin) + " End City " + String.valueOf(this.CityCodeEnd)
                + " Distance " + String.valueOf(this.distance);
    }

    @Override
    public int compareTo(Edge o) {
        if (o.distance < this.distance) {
            return 1;
        } else if (o.distance > this.distance) {
            return -1;
        } else {
            return 0;
        }

    }
}
