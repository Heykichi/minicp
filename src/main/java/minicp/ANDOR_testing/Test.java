package minicp.ANDOR_testing;

import minicp.ANDOR.AND_MiniCP;
import minicp.ANDOR.ConstraintGraph;
import minicp.cp.Factory;
import minicp.engine.core.Constraint;
import minicp.engine.core.IntVar;
import minicp.engine.core.MiniCP;
import minicp.engine.core.Solver;
import minicp.state.Trailer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Test {
    public static void main(String[] args) {
        AND_MiniCP cp = (AND_MiniCP) Factory.makeANDSolver(false);
        ConstraintGraph g = cp.getGraph();
        IntVar Y = Factory.makeIntVar(cp,5);
        IntVar Z = Factory.makeIntVar(cp,4);
        IntVar X = Factory.makeIntVar(cp,4);
        cp.post(Factory.notEqual(Y, Z));
        cp.post(Factory.notEqual(X, Z));

        g.addEdge(Z,Y);
        g.addEdge(Z,X);
        System.out.println(g.findIndependentSubgraphs());
        System.out.println(g);

        // System.identityHashCode(node)


    }
}
