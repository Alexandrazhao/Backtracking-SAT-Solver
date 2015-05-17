import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
   
/**
 * 
 * This material is based upon work supported by 
 * the National Science Foundation under Grant No. 1140753.
 * 
 */
   
/**
 * This class is a skeleton for a backtracking SAT solver It may be used as a
 * handout to explain backtracking and provide a starting point for students to
 * build their respective backtracking solvers
 * 
 * @author Dr. Andrea Lobo
 * @author Dr. Ganesh R. Baliga
 * 
 *
 */
   
 /**
  * DP Solver for SAT Problem.
  * 
  * @author Darren Martin, Amandeep Singh, Joe Savin
  * 03/26/2015
  */
public class dpsolver {
   
    //Array that holds the partial assignment
    static Boolean[] solution;
   
    /**
     * Main function that starts program
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
     
        BufferedReader stdin = new BufferedReader(new InputStreamReader(
                System.in));
     
        String input = " ";
        while (!(input.equals(""))) {
            System.out.println("\n\nEnter file name: ");
            input = stdin.readLine();
            if (!(input.equals(""))) {
                 long start = System.currentTimeMillis();
                 solve(input);
                 long end = System.currentTimeMillis();
                 System.out.println("\nTime: " + (end - start) + " milliseconds");
            }
        }
     
    }
 
    /**
     * Attempts to solve the formula.
     * 
     * @param fileName - the name of the file containing the problem
     */
    public static void solve(String fileName) {
        Formula formula;
        formula = readFormula(fileName);
        formula.printFormula();
        if (dp(formula))
            success(formula);
        else
            failure(formula);
     
    }
 
    // Read the provided input formula
        static Formula readFormula(String fileName) {
            ArrayList<Integer> equation = new ArrayList<Integer>();
            ArrayList<ArrayList<Integer>> locations = new ArrayList<ArrayList<Integer>>();
            int literals = 0;
            int finalClauses = 0;
            try {
                int clauses = 1;
                File inputFile = new File("/Users/Darren/Documents/School/DAA/inputs/"
                        + fileName + ".cnf");
       
                FileReader fileReader = new FileReader(inputFile);
                Scanner scanner = new Scanner(fileReader);
                String line = null;
                boolean done = false;
                int formulaPos=0;
                while (!done && (line = scanner.nextLine()) != null) {
                    if (line.charAt(0) == 'p') {
                        String[] pLine = new String[4];
                        pLine = line.split(" ");
                        literals = Integer.parseInt(pLine[2]);
                        clauses = Integer.parseInt(pLine[3]);
                        finalClauses = clauses;
                        for(int i=0; i<literals; i++){
                            locations.add(new ArrayList<Integer>());
                        }
                    } else if (!(line.charAt(0) == 'c')) {
                        String[] variables;
                        variables = line.split(" ");
                        for (int i = 0; i < variables.length; i++) {
                            int tempLit = Integer.parseInt(variables[i]);
                            equation.add(tempLit);
                            if(tempLit!=0){
                              locations.get(Math.abs(tempLit)-1).add(formulaPos);
                            }
                            formulaPos++;
                        }
                        // subtracts number of clauses so this loop will not over
                        // execute
                        clauses--;
                    }
                    if (clauses == 0) {
                        // signifies that whole formula has been grabbed
                        done = true;
                    }
                }
       
            } catch (Exception ex) {
                // If no file is found with given parameter, error prints out.
                System.out.println("ERROR.");
            }
            Formula f = new Formula(equation, finalClauses, locations);
            solution = new Boolean[literals];
            return f;
       
        }
 
    /**
     * Recursively attempts to solve the problem and find a solution.
     * 
     * @param formula - the current Formula
     * @return - Boolean - true if formula is empty, false if it has an empty clause.
     */
    static boolean dp(Formula formula) {
     
        ArrayList<Integer> changeVars = new ArrayList<Integer>();
        formula.printVals();
        	System.out.print("Last Changed:");
        	formula.printLastChange();
        if (isEmpty(formula)) // First base case: solution found
        {
            return true;
        } else if (hasEmptyClause(formula)) // Second base case: dead end found
        {
            return false;
        } else {
     
            // Pick a branch variable
            int var = selectBranchVar(formula);
            formula = setVar(var, formula, true);
            changeVars = formula.getChanged();
            if (dp(formula))
                return true;
            else {
                formula = unset(var, formula, changeVars);        
                formula = setVar(var, formula, false);
                changeVars = formula.getChanged();
     
                if (dp(formula))
                    return true;
                else {
                    formula = unset(var, formula, changeVars);
                    return false;
                }
            }
        }
    }
 
    /**
     * Returns branch variable
     * 
     * @param f - Formula
     * @return int - Branch Variable
     */
    static int selectBranchVar(Formula f) {
        int branchVar = 1;
        boolean stop = false;
        for (int i = solution.length - 1; !stop && i >= 0; i--) {
            if (solution[i] != null) {
                branchVar = i + 1;
                stop = true;
            }
     
        }
        while (branchVar < solution.length && !f.stillExists(branchVar)) {
            solution[branchVar] = true;
            branchVar++;
        }
        return branchVar;
    }
 
    // 
    /**
     * Set given variable to given true/false value
     * Variable value may be positive or negative
     * 
     * @param var - variable we are setting
     * @param f - current Formula
     * @param tf - value of the variable
     * @return Formula - the formula after setting variable
     */
    static Formula setVar(int var, Formula f, boolean tf) {
        solution[var - 1] = tf;
        f.checkVar(var, tf);
        return f;
     
    }
 
    /**
     * Set given variable to "unassigned" in the given formula
     * 
     * @param var - variable we will unset
     * @param f - current Formula
     * @param changeVars - Position of literals we must change back
     * @return Formula - the formula after unsetting
     */
    static Formula unset(int var, Formula f, ArrayList<Integer> changeVars) {
        for (int i = var - 1; i < solution.length; i++) {
            solution[i] = null;
        }
        f.unset(changeVars);
        return f;
    }
 
    /**
     * Returns true if the formula has an empty clause, false otherwise
     * 
     * @param f - the current Formula
     * @return Boolean
     */
    static boolean hasEmptyClause(Formula f) {
        return f.hasEmptyClause();
    }
   
    /**
     * Returns true if the formula has no clauses left, false otherwise
     * 
     * @param f - the current Formula
     * @return Boolean
     */
    static boolean isEmpty(Formula f) {
        return f.isEmpty();
   
    }
   
    /**
     * Formula is satisfiable.
     * 
     * @param f - the current Formula
     */
    static void success(Formula f) {
        for(int i = solution.length-1; solution[i]==null; i--){
            solution[i]=true;
        }
        System.out.println("Formula is satisfiable");
        for (int i = 0; i < solution.length; i++) {
            System.out.print(solution[i] + " ");
        }
        // Prints satisfying assignment
    }
 
    /**
     * Formula is unsatisfiable.
     * 
     * @param f - current Formula
     */
    static void failure(Formula f) {
        // Stub
        System.out.println("Formula is unsatisfiable");
    }
   
}
