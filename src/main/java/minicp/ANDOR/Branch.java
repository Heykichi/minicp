package minicp.ANDOR;

import minicp.engine.core.IntVar;
import minicp.util.Procedure;

public class Branch {

    private IntVar[] variables = null;
    private Branch[] branches = null;
    private boolean end = true;

    public Branch(IntVar[] variables, Branch[] branches, boolean end) {
        this.variables = variables;
        this.branches = branches;
        this.end = end;
    }

    public Branch(IntVar[] variables, boolean end) {
        this.variables = variables;
        this.end = end;
    }
    public Branch(Branch[] branches, boolean end) {
        this.branches = branches;
        this.end = end;
    }

    public void setBranches(Branch[] branches) {this.branches = branches;}

    public void setVariables(IntVar[] variables) {this.variables = variables;}

    public void setEnd(boolean end) {this.end = end;}

    public IntVar[] getVariables() {
        return this.variables;
    }

    public Branch[] getBranches() {return this.branches;}

    public boolean isEnd() {return this.end;}
}
