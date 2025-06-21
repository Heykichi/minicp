package minicp.ANDOR_testing;

import minicp.ANDOR.AND_MiniCP;
import minicp.ANDOR.ConstraintGraph;
import minicp.cp.Factory;
import minicp.engine.core.Constraint;
import minicp.engine.core.IntVar;
import minicp.engine.core.MiniCP;
import minicp.engine.core.Solver;
import minicp.state.StateManager;
import minicp.state.StateStack;
import minicp.state.Trailer;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) {
        AND_MiniCP cp = (AND_MiniCP) Factory.makeANDSolver(false);
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
        g.PrintSubgraph();
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
                sm.withNewState(() -> {
                            s.getLastElement().add(Factory.makeIntVar(cp,a+1));
                        });
                //Li.remove(Y);

                //s.getLastElement().add(Factory.makeIntVar(cp,a+1));
                System.out.println(s.getLastElement());
            });
        }
    }
}
