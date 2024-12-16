package minicp.ANDOR;

import minicp.cp.Factory;
import minicp.engine.core.IntVar;
import minicp.engine.core.Solver;
import minicp.util.Procedure;

import java.util.Arrays;

public class Base_AND_Problem {

    private static void branchSolver(Solver cp, IntVar[] Variable) {
        boolean out = true;
        while (out){

            int idx = -1; // index of the first variable that is not fixed
            for (int k = 0; k < Variable.length; k++)
                if (Variable[k].size() > 1) {
                    idx = k;
                    break;
                }
            if (idx == -1) {
                out = false;
            } else {
                IntVar qi = Variable[idx];
                int v = qi.min();
                cp.post(Factory.equal(qi, v));
            }
        }
    }

    public static void main(String[] args) {

        Solver cp = Factory.makeSolver(false);
        IntVar[] V = Factory.makeIntVarArray(cp, 4, 4);
        IntVar[] H = Factory.makeIntVarArray(cp, 4, 4);

        cp.post(Factory.equal(V[2], H[2]));
        for (int i = 0; i < 4; i++){
            for (int j = i + 1; j < 4; j++) {
                cp.post(Factory.notEqual(V[i], V[j]));
                cp.post(Factory.notEqual(H[i], H[j]));
            }
        }


        branchSolver(cp,new IntVar[]{V[2]});
        branchSolver(cp,H);
        branchSolver(cp,V);

        System.out.println("solution:" + Arrays.toString(H) + " - " + Arrays.toString(V));
    }
}
