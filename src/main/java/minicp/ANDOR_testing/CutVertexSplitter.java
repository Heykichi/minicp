package minicp.ANDOR_testing;

import java.util.*;

/** Undirected, unweighted graph — iterative Tarjan articulation-point splitter. */
public class CutVertexSplitter {

    /* ===== adjacency in dynamic-CSR form ===== */
    private final int n;
    private int[] head;          // head[v] = first outgoing edge index or -1
    private int[] to, next;      // parallel edge arrays
    private int m2 = 0;          // number of *directed* edges (2⋅|E|)

    public CutVertexSplitter(int nVertices) {
        n = nVertices;
        head = new int[n];
        Arrays.fill(head, -1);
        to   = new int[4];       // tiny start; grows automatically
        next = new int[4];
    }
    private void ensureCap() {           // double arrays when full
        if (m2 == to.length) {
            int newCap = to.length << 1;
            to   = Arrays.copyOf(to,   newCap);
            next = Arrays.copyOf(next, newCap);
        }
    }
    private void pushEdge(int u, int v) {
        ensureCap();
        to[m2]   = v;
        next[m2] = head[u];
        head[u]  = m2++;
    }
    public void addEdge(int u, int v) {  // 0-based endpoints
        pushEdge(u, v);
        pushEdge(v, u);
    }

    /* ===== Phase A: articulation points (iterative Tarjan) ===== */
    public boolean[] findArticulationPoints() {
        int[] tin    = new int[n];  Arrays.fill(tin, -1);
        int[] low    = new int[n];
        int[] iter   = new int[n];  // “next edge index to try” per vertex
        int[] parent = new int[n];  Arrays.fill(parent, -1);
        boolean[] isCut = new boolean[n];

        int timer = 0;
        int[] stk = new int[n]; int top = 0;    // manual DFS stack

        for (int root = 0; root < n; ++root) if (tin[root] == -1) {
            int rootChildren = 0;

            // initialise root
            stk[top++] = root;
            tin[root] = low[root] = timer++;
            iter[root] = head[root];

            while (top > 0) {
                int v = stk[top - 1];

                if (iter[v] == -1) {            // all neighbours processed
                    --top;                      // pop v
                    int p = parent[v];
                    if (p != -1) {
                        low[p] = Math.min(low[p], low[v]);
                        if (low[v] >= tin[p]) isCut[p] = true;
                    }
                    continue;
                }

                int eid = iter[v];
                iter[v] = next[eid];            // advance iterator
                int w = to[eid];

                if (tin[w] == -1) {             // tree edge (v -> w)
                    parent[w] = v;
                    tin[w] = low[w] = timer++;
                    iter[w] = head[w];
                    if (v == root) ++rootChildren;
                    stk[top++] = w;             // descend
                } else if (w != parent[v]) {    // back edge
                    low[v] = Math.min(low[v], tin[w]);
                }
            }
            if (rootChildren > 1) isCut[root] = true;
        }
        return isCut;
    }

    /* ===== Phase B: components after deleting articulation points ===== */
    public List<List<Integer>> splitIntoBlocks(boolean[] isCut) {
        List<List<Integer>> blocks = new ArrayList<>();
        boolean[] vis = new boolean[n];
        ArrayDeque<Integer> q = new ArrayDeque<>();

        for (int s = 0; s < n; ++s) if (!isCut[s] && !vis[s]) {
            List<Integer> comp = new ArrayList<>();
            q.add(s); vis[s] = true;
            while (!q.isEmpty()) {
                int v = q.poll();
                comp.add(v);
                for (int eid = head[v]; eid != -1; eid = next[eid]) {
                    int w = to[eid];
                    if (!isCut[w] && !vis[w]) {
                        vis[w] = true;
                        q.add(w);
                    }
                }
            }
            blocks.add(comp);
        }
        return blocks;
    }

    /* ===== convenience wrapper ===== */
    public List<List<Integer>> blocksAfterCut() {
        return splitIntoBlocks(findArticulationPoints());
    }

    /* ===== tiny sanity demo ===== */
    public static void main(String[] args) {
        CutVertexSplitter g = new CutVertexSplitter(7);
        g.addEdge(0,1); g.addEdge(1,2); g.addEdge(1,3);
        g.addEdge(3,4); g.addEdge(4,5); g.addEdge(4,6);
        g.addEdge(3,2);g.addEdge(5,6);g.addEdge(4,6);

        System.out.println("Articulation points : " +
                Arrays.toString(g.findArticulationPoints()));   // [false,true,false,true,true,false,false]

        System.out.println("Blocks after cut    : " + g.blocksAfterCut());
        // [[0, 1, 2], [3], [4, 5, 6]]
    }
}
