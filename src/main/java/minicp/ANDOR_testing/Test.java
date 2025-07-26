package minicp.ANDOR_testing;

import minicp.ANDOR_engine.AND_MiniCP;
import minicp.ANDOR_engine.ConstraintGraph;
import minicp.ANDOR_engine.SlicedTable;
import minicp.cp.Factory;
import minicp.engine.core.IntVar;
import minicp.state.StateManager;
import minicp.state.StateStack;

import java.util.*;

import static minicp.ANDOR_engine.SlicedTable.computeSlicedTable;

public class Test {
    public static void main(String[] args) {

        Map<Integer, Integer> h0a = new HashMap<>();
        h0a.put(0, 0);
        Map<Integer, Integer> h0b = new HashMap<>();
        h0b.put(0, 1);
        Map<Integer, Integer> h1a = new HashMap<>();
        h1a.put(1, 0);
        Map<Integer, Integer> h1b = new HashMap<>();
        h1b.put(1,1);
        Map<Integer, Integer> h3 = new HashMap<>();
        h3.put(3,0);
        Map<Integer, Integer> h3b = new HashMap<>();
        h3b.put(3,9);
        h3b.put(0,9);
        h3b.put(1,9);

        SlicedTable s1 = new SlicedTable(h1a);
        SlicedTable s2 = new SlicedTable(h1b);
        SlicedTable s3 = new SlicedTable(h3, Collections.singletonList(Arrays.asList(s1, s2)));

        List<List<SlicedTable>> l = new ArrayList<>();
        l.add(Collections.singletonList(s3));
        l.add(Arrays.asList(new SlicedTable(h0a),new SlicedTable(h0b)));


        List<SlicedTable> solu = new ArrayList<>();
        solu.add(new SlicedTable(null, l));
        List<Map<Integer, Integer>> listSolutions = computeSlicedTable(Arrays.asList(new SlicedTable(h3b),new SlicedTable(null,Collections.singletonList(solu))),10000);
        System.out.println("================");
        System.out.println(listSolutions.size());
        for (Map<Integer, Integer> sol : listSolutions) {
            System.out.println("====");
            System.out.println(sol);
        }

    }
}
