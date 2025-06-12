package minicp.ANDOR;

import minicp.engine.core.IntVar;
import minicp.util.Procedure;

public class Branch {

    private IntVar[] variables = null;
    private Branch[] branches = null;
    private int end = -1;

    public Branch(IntVar[] variables, Branch[] branches, int end) {
        this.variables = variables;
        this.branches = branches;
        this.end = end;
    }

    public Branch(IntVar[] variables, Branch[] branches) {
        this.variables = variables;
        this.branches = branches;
    }

    public Branch(IntVar[] variables, int end) {
        this.variables = variables;
        this.end = end;
    }
    public Branch(IntVar[] variables) {
        this.variables = variables;
    }
    public Branch(Branch[] branches, int end) {
        this.branches = branches;
        this.end = end;
    }

    public void setBranches(Branch[] branches) {this.branches = branches;}

    public void setVariables(IntVar[] variables) {this.variables = variables;}

    public void setEnd(int end) {this.end = end;}

    public IntVar[] getVariables() {
        return this.variables;
    }

    public Branch[] getBranches() {return this.branches;}

    public int getEnd() {return this.end;}
}
