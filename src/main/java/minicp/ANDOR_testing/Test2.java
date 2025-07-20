package minicp.ANDOR_testing;

import minicp.ANDOR_engine.Branch;
import minicp.cp.Factory;
import minicp.engine.core.IntVar;
import minicp.engine.core.Solver;

import java.util.ArrayList;
import java.util.List;

import static minicp.ANDOR_example.AND_PS_example1_1.printSum;

public class Test2 {
    public static void main(String[] args) {

        Solver cp = Factory.makeSolver(false);

        IntVar[] X = Factory.makeIntVarArray(cp, 3, 4);
        IntVar[] Z = Factory.makeIntVarArray(cp, 1, 4);



        IntVar Y = Factory.makeIntVar(cp,5);

        printSum(X,Y);
        printSum(Z,Y);

        List<Branch> branches = new ArrayList<>();
        branches.add(new Branch(X));

        Branch[] b = branches.toArray(new Branch[0]);

        System.out.println(b.length);
        System.out.println(b[0].getVariables());

    }
}
