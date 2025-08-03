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
        int resultat = 1;
        for (int i = 0; i < 60; i++) {
            resultat *= 2;
            System.out.println(resultat);
        }
    }
}
