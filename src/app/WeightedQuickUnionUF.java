package app;

//implement an efficient algorithm for quick union find
public class WeightedQuickUnionUF {

    private final int[] id;
    private final int[] sz;
    private int[] large;

    public WeightedQuickUnionUF(final int N) {
        id = new int[N];
        sz = new int[N];
        large = new int[N];
        for (int i = 0; i < N; i++) {
            id[i] = i;
            sz[i] = 1;
            large[i] = i;
        }
    }

    private int root(int i) {
        while (i != id[i])
            i = id[i];
        // here is a variant to flatten tree
        // id[i] = id[id[i]];
        return i;
    }

    public int find(int i) {
        while (i != id[i])
            i = id[i];

        return large[i];
    }

    public boolean connected(final int p, final int q) {
        if (p < id.length && q < id.length) {
            return (root(p) == root(q));
        } else {
            return false;
        }
    }

    // return the number of connected nodes to this node
    public int getSize(final int p) {
        final int pid = root(p);
        return sz[pid];
    }

    //only connect two nodes if it makes for a longer graph
    public boolean conditionalUnion(final int p, final int q) {

        int sizeP = 0;
        int sizeQ = 0;

        if (p >= id.length || q >= id.length) {
            return false;
        }

        final int pid = root(p);
        final int qid = root(q);

        if (sz[pid] < sz[qid]) {
            sizeQ += sz[pid];
        } else {
            sizeP += sz[qid];
        }
        //if either sizeQ or sizeP > 2, then the graph will be
        //connected to a bigger graph, so perform the union
        if ((sizeP > 2) || (sizeQ > 2)) {
            union(p, q);
            return true;
        }
        else {
            return false;
        }
        
    }

    public void union(final int p, final int q) {
        if (p >= id.length || q >= id.length) {
            return;
        }

        final int pid = root(p);
        final int qid = root(q);

        if (sz[pid] < sz[qid]) {
            id[pid] = qid;
            sz[qid] += sz[pid];
        } else {
            id[qid] = pid;
            sz[pid] += sz[qid];
        }

        if (large[pid] < large[qid]) {
            large[pid] = large[qid];
        } else {
            large[qid] = large[pid];
        }

    }




    public void PrintConnections() {
        for (int i = 0; i < large.length; i++) {
            System.out.printf("Array index %d has value %3d\n", i, large[i]);
        }
    }

}