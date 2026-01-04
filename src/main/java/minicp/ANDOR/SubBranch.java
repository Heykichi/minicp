package minicp.ANDOR;

import minicp.engine.core.IntVar;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SubBranch {
    private Set<IntVar> variables;
    private boolean toFix = false;

    public SubBranch(Set<IntVar> variables, boolean toFix) {
        this.variables = variables;
        this.toFix = toFix;
    }

    public SubBranch(IntVar[] varList, boolean toFix) {
        this.variables = new HashSet<>(Arrays.asList(varList));
        this.toFix = toFix;
    }

    public SubBranch(Set<IntVar> variables) {
        this.variables = variables;
    }

    public SubBranch(IntVar[] varList) {
        this.variables = new HashSet<>(Arrays.asList(varList));
    }

    public Set<IntVar> getVariables() {return this.variables;}

    public boolean getToFix() {return this.toFix;}
}