package minicp.ANDOR_testing;

import minicp.ANDOR_engine.AND_MiniCP;
import minicp.ANDOR_engine.ConstraintGraph;
import minicp.cp.Factory;
import minicp.engine.core.IntVar;
import minicp.state.StateManager;
import minicp.state.StateStack;

import java.util.*;

public class Test {
    public static void main(String[] args) {
        AND_MiniCP cp = (AND_MiniCP) Factory.makeANDSolver(true);
        ConstraintGraph g = cp.getGraph();
        IntVar Y = Factory.makeIntVar(cp,1);
        IntVar Z = Factory.makeIntVar(cp,2);
        IntVar X = Factory.makeIntVar(cp,3);
        cp.post(Factory.notEqual(Y, Z));
        cp.post(Factory.notEqual(X, Z));



        IntVar Z1 = Factory.makeIntVar(cp,4);
        IntVar X2 = Factory.makeIntVar(cp,4);
        cp.post(Factory.notEqual(X2, Z1));



        List<Set<IntVar>> L = g.findIndependentSubgraphs();
        g.printSubgraph();
        System.out.println(g);

        System.out.println(Z1.hashCode());
        System.out.println(System.identityHashCode(Z1));
        // System.identityHashCode(node)

        System.out.println("========");

        StateStack<ArrayList<IntVar>> s = new StateStack<ArrayList<IntVar>>(cp.getStateManager());

        StateManager sm = cp.getStateManager();
        ArrayList<IntVar> initialList = new ArrayList<>();
        initialList.add(Y);
        s.push(initialList);
        System.out.println(s.getLastElement());
        for (int i = 1; i < 10; i++) {
            final int a = i;
            sm.withNewState(() -> {
                ArrayList<IntVar> Li = new ArrayList<>(s.getLastElement());
                Li.add(Factory.makeIntVar(cp,a));
                s.push(Li);
                //s.getLastElement().add(Factory.makeIntVar(cp,a+1));
                //Li.remove(Y);

                //s.getLastElement().add(Factory.makeIntVar(cp,a+1));
                System.out.println(s.getLastElement());
            });
        }

        System.out.println("===========");

        //System.out.println(Z1);
        StateStack<ArrayList<Integer>> s2 = new StateStack<ArrayList<Integer>>(sm);
        ArrayList<Integer> L1 = new ArrayList<>();
        ArrayList<Integer> L2 = new ArrayList<>();
        s2.push(L1);
        L1.add(1);
        L2.add(2);
        System.out.println(s2.getLastElement());
        sm.saveState();
        s2.push(L2);
        System.out.println(s2.getLastElement());
        Z1.fix(1);
        sm.restoreState();
        //System.out.println(Z1);
        System.out.println(s2.getLastElement());

        s2.getLastElement().add(3);
        s2.getLastElement().add(3);
        s2.getLastElement().add(3);
        System.out.println(s2.getLastElement());
    }
}
