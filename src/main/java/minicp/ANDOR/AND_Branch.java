package minicp.ANDOR;

import minicp.engine.core.IntVar;

public class AND_Branch {

    private IntVar[] variables = null;
    private AND_Branch[] branches = null;
    private boolean next = false;

    public AND_Branch(IntVar[] variables, AND_Branch[] branches,boolean next) {
        this.variables = variables;
        this.branches = branches;
        this.next = next;
    }

    public AND_Branch(IntVar[] variables) {
        this.variables = variables;
    }
    public AND_Branch(AND_Branch[] branches) {
        this.branches = branches;
    }

    public IntVar[] getVariables() {
        return this.variables;
    }

    public AND_Branch[] getBranches() {
        return this.branches;}

}
