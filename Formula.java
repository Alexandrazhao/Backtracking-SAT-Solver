import java.util.ArrayList;
 
/**
 * Constructor for Formula class. This class has two parallel ArrayLists. The
 * ArrayList formula holds the values of each literal in the formula, including
 * 0s to separate clauses. The formulaVals ArrayList tells whether or not the
 * literal or clause is still active. The ArrayList "setVars" keeps track of
 * which literals were last altered for the purpose of backtracking.
 */
public class Formula {
 
	//the formula containing all literals
    ArrayList<Integer> formula;
    //parallel ArrayList that tells us if literal has been removed or not
    ArrayList<Boolean> formulaVals = new ArrayList<Boolean>();
    //Keeps track of which literals were changed during last "set" for backtracking purposes
    ArrayList<Integer> setVars = new ArrayList<Integer>();
    //Keeps locations where literals appear in formula, organized by which literal, to make removal faster.
    ArrayList<ArrayList<Integer>> location = new ArrayList<ArrayList<Integer>>();
 
    public Formula(ArrayList<Integer> inFormula, int inClauses,
            ArrayList<ArrayList<Integer>> location) {
        formula = inFormula;
        for (int i = 0; i < formula.size(); i++) {
            formulaVals.add(true);
        }
        this.location = location;
    }
 
    /**
     * Removes literals accordingly using their value in the partial solution.
     * 
     * @param var
     *            - literal to be changed.
     * @param tf
     *            - Boolean value of literal.
     */
    public void checkVar(int var, boolean tf) {
        setVars = new ArrayList<Integer>();
        ArrayList<Integer> temp = location.get(var - 1);
        if (tf == true) {
            for (int i = 0; i < temp.size(); i++) {
                int lit = temp.get(i);
                if (formulaVals.get(lit) == true) {
                    if (formula.get(lit) == var) {
                        removeClause(lit);
                    } else {
                        removeVariable(lit);
                        setVars.add(lit);
                    }
                }
            }
 
        } else {
            for (int i = 0; i < temp.size(); i++) {
                int lit = temp.get(i);
                if (formulaVals.get(lit) == true) {
                    if (formula.get(lit) == var) {
                        removeVariable(lit);
                        setVars.add(lit);
                    } else {
                        removeClause(lit);
                    }
                }
            }
        }
    }
 
    /**
     * Removes a single variable from the formula by marking it.
     * 
     * @param varPos
     *            - the position of the literal we want to mark.
     */
    private void removeVariable(int varPos) {
        if (formulaVals.get(varPos) == true) {
            formulaVals.remove(varPos);
            formulaVals.add(varPos, false);
        }
    }
 
 
    /**
     * Removes a whole clause from the formula by marking it. Marks the position
     * of the every literal in the clause, and the ending 0, as FALSE in the
     * parallel ArrayList "formulaVals"
     * 
     * @param varPos
     *            - the position of the literal in the clause that we want to
     *            remove.
     * @return
     */
    private void removeClause(int varPos) {
        int falsePos = 0;
        Boolean stop = false;
        for (int pos = varPos; pos >= 0 && !stop; pos--) {
            if (pos == 0) {
                falsePos = 0;
                stop = true;
            } else if (formula.get(pos) == 0) {
                falsePos = pos + 1;
                stop = true;
            }
        }
        int pos = falsePos;
        for (; formula.get(pos) != 0; pos++) {
            if (formulaVals.get(pos) == true) {
                setVars.add(pos);
                formulaVals.remove(pos);
                formulaVals.add(pos, false);
            }
        }
        setVars.add(pos);
        formulaVals.remove(pos);
        formulaVals.add(pos, false);
    }
 
    /**
     * Unsets literals that were changed during last step.
     * 
     * @param ArrayList
     *            - changeVars, the positions of the literals that were last
     *            changed.
     */
    public void unset(ArrayList<Integer> changeVars) {
        for (int i = 0; i < changeVars.size(); i++) {
            int remove = changeVars.get(i);
            formulaVals.remove(remove);
            formulaVals.add(changeVars.get(i), true);
 
        }
 
    }
 
    /**
     * Checks to see if a literal is still active in the formula.
     * 
     * @param var
     *            - the literal we are looking for.
     * @return - Boolean, true if the variable is still active.
     */
    public boolean stillExists(int var) {
        for (int i = 0; i < formula.size(); i++) {
            if (Math.abs(formula.get(i)) == Math.abs(var) && formulaVals.get(i)) {
                return true;
            }
        }
        return false;
    }
 
    public double findLiteral(int var) {
        double push = 0;
        Boolean found = false;
        for (int i = 0; i < formula.size(); i++) {
            if (Math.abs(formula.get(i)) == var && formulaVals.get(i)) {
                found = true;
                if (formula.get(i) > 0) {
                    push++;
                } else {
                    push--;
                }
            }
        }
        if (found) {
            return push;
        } else {
            return .5;
        }
    }
 
    /**
     * Checks to see if an empty clause exists in the formula.
     * 
     * @return Boolean - true if an empty clause exists
     */
    public boolean hasEmptyClause() {
        boolean checkZero = false;
        for (int i = formula.size() - 1; i >= 0; i--) {
            if (checkZero == true
                    && ((formula.get(i) == 0 && formulaVals.get(i)) || (i == 0 && formulaVals
                            .get(i) == false))) {
                return true;
            } else if (formula.get(i) == 0 && formulaVals.get(i)) {
                checkZero = true;
            } else if (formulaVals.get(i) == true && formula.get(i) != 0) {
                checkZero = false;
            }
        }
        return false;
    }
 
    /**
     * Checks to see if entire formula is empty.
     * 
     * @return Boolean - true if formula is empty
     */
    public boolean isEmpty() {
        for (int i = 0; i < formula.size(); i++) {
            if (formulaVals.get(i) == true) {
                return false;
            }
        }
        return true;
    }
 
    /**
     * Returns the literals that were changed during the last set.
     * 
     * @return ArrayList - variables last set
     */
    public ArrayList<Integer> getChanged() {
        return setVars;
    }
 
    /**
     * Prints the formula for testing purposes.
     */
    public void printFormula() {
    	String form="";
        for (int i = 0; i < formula.size(); i++) {
            form+=formula.get(i) + " ";
        }
    	System.out.println(form);
    }
 
    /**
     * Prints whether or not literals are still active.
     */
    public void printVals() {
    	String form="";
        for (int i = 0; i < formula.size(); i++) {
            if (formulaVals.get(i)) {
                form+="T ";
            } else {
                form+="F ";
            }
        }
    	System.out.println(form);
    }
 
    /**
     * Prints positions that were changed during last set.
     */
    public void printLastChange() {
    	String form="";
        for (int i = 0; i < setVars.size(); i++) {
            form+=setVars.get(i) + " ";
        }
        System.out.println(form);
    }
 
    /**
     * Prints the locations of different literals in the formula.
     */
    public void printLocations() {
        for (int i = 0; i < location.size(); i++) {
            System.out.println("Location of x" + (i + 1));
            for (int j = 0; j < location.get(i).size(); j++) {
                System.out.print(location.get(i).get(j) + " ");
            }
            System.out.println();
 
        }
    }
 
}
