import java.util.*;
/**
 * This class implements the Quine-McCluskey algorithm
 * for simplifying boolean expressions.
 */
public class Main {

    /**
     * Converts an integer to its binary representation
     * with a specified number of bits.
     *
     * @param n The number to convert
     * @param number The number of bits in the binary representation
     * @return Binary string with leading zeros if needed
     */
    public static String intToBinary(int n, int number) {
        String binary;
        binary = Integer.toBinaryString(n);
        if (binary.length() == number) {
            return binary;
        }
        String temp = "";
        for(int i = 0; i < number - binary.length(); i++) {
            temp = temp.concat("0");
        }
        return temp.concat(binary);
    }

    /**
     * Performs a binary operation (AND, OR) on two binary characters.
     *
     * @param n First binary character ('0' or '1')
     * @param op Operator ('.' for AND, '+' for OR)
     * @param m Second binary character ('0' or '1')
     * @return Result of the operation as a character
     */
    public static char calculateBinary(char n, char op, char m) {
        switch (op) {
            case '.':
                return (char) (((n - '0') & (m - '0')) + '0');
            case '+':
                return (char) (((n - '0') | (m - '0')) + '0');
            default:
                return ' ';
        }
    }

    /**
     * Validates a boolean expression string based on syntax rules
     *
     * @param input The expression string to validate
     * @param number Number of allowed variables (for A-Z range check)
     * @return true if valid expression, false otherwise
     */
    public static boolean checkValidation(String input, int number) {
        char ch;
        boolean isParentheses = true;    // Currently processing parentheses?
        boolean operand = false;    // Previous token was an operand?
        boolean operator = false;     // Previous token was an operator?
        boolean complete = false;    // NOT operator (') completed?
        int parentheses = 0;    // Parentheses counter for balance check
        for (int i = 0; i < input.length(); i++) {
            ch = input.charAt(i);
            // Skip whitespace characters
            if (ch <= 32) {
                continue;
            }
            // Handle '(' character
            if (ch == '(') {
                isParentheses = true;
                operator = false;
                complete = false;
                if(operand){
                    return false;
                }
                parentheses++;
            }
            // Handle ')' character
            else if (ch == ')') {
                operand = true;
                complete = false;
                if(operator){
                    return false;
                }
                parentheses--;
                if (parentheses == -1) {    //Unmatched closing parenthesis
                    return false;
                }
            }
            // Handle OR (+) and AND (.) operators
            else if (ch == '+' || ch == '.') {
                if(isParentheses){
                    return false;
                }
                complete = false;
                if(operator){
                    return false;
                }
                operand = false;
                operator = true;
                if(i == input.length() - 1) {
                    // Operator cannot be last character
                    return false;
                }
            }
            // Handle variable operands (A-Z)
            else if (ch - 'A' >= 0 && ch - 'A' <= 25) {
                if(ch - 'A' >= number){
                    return false;
                }
                isParentheses = false;
                complete = false;
                if(operand){
                    return false;
                }
                operator = false;
                operand = true;
            }
            // Handle NOT operator (')
            else if (ch == '\'') {
                if(complete){
                    return false;
                }
                complete = true;
            }
            else {
                return false;
            }
        }
        return true;
    }

    /**
     * Evaluates boolean expression for a specific variable combination
     *
     * @param expression Boolean expression to evaluate
     * @param binary Bit string representing variable values (e.g. "1010")
     * @return true if expression evaluates to true for this combination,
     * false otherwise
     */
    public static boolean findMin(String expression, String binary){
        expression = expression.replaceAll(" ", "");
        Stack operandStack = new Stack();
        Stack operatorStack = new Stack();
        char ch, temp;
        for(int i = 0; i < expression.length(); i++){
            ch = expression.charAt(i);
            // Handle opening parenthesis
            if(ch == '('){
                operatorStack.push(ch);
            }
            // Handle NOT operator
            else if(ch == '\''){
                if(operandStack.top() == '1'){
                    operandStack.pop();
                    operandStack.push('0');
                }
                else{
                    operandStack.pop();
                    operandStack.push('1');
                }
            }
            // Handle closing parenthesis
            else if(ch == ')'){
                while(operatorStack.top() != '(' && operatorStack.top() > 32){
                    temp = operandStack.top();
                    operandStack.pop();
                    temp = calculateBinary(temp, operatorStack.top(),
                            operandStack.top());
                    operandStack.pop();
                    operatorStack.pop();
                    operandStack.push(temp);
                }
                operatorStack.pop();
                if(operatorStack.top() == '.'){
                    temp = operandStack.top();
                    operandStack.pop();
                    temp = calculateBinary(operandStack.top(), '.', temp);
                    operandStack.pop();
                    operatorStack.pop();
                    operandStack.push(temp);
                }
            }
            // Handle variables
            else if(ch >= 'A' && ch <= 'Z'){
                operandStack.push(binary.charAt(ch - 'A'));
                if((i + 1 < expression.length()) &&
                        expression.charAt(i + 1) == '\''){
                    if(operandStack.top() == '1'){
                        operandStack.pop();
                        operandStack.push('0');
                    }
                    else{
                        operandStack.pop();
                        operandStack.push('1');
                    }
                    i++;
                }
                if(operatorStack.top() == '.'){
                    temp = operandStack.top();
                    operandStack.pop();
                    temp = calculateBinary(operandStack.top(), '.', temp);
                    operandStack.pop();
                    operatorStack.pop();
                    operandStack.push(temp);
                }
            }
            // Handle other operators
            else{
                operatorStack.push(ch);
            }
        }
        // Final evaluation of remaining operations
        while(!operatorStack.isEmpty()){
            temp = operandStack.top();
            operandStack.pop();
            temp = calculateBinary(operandStack.top(),
                    operatorStack.top(), temp);
            operandStack.pop();
            operatorStack.pop();
            operandStack.push(temp);
        }
        return operandStack.top() == '1';
    }

    /**
     * Checks if a 2D Minterm array is empty (all elements are null).
     *
     * @param minterms The 2D array to check
     * @return true if all elements are null, false otherwise
     */
    public static boolean isEmpty(Minterm[][] minterms){
        for (Minterm[] minterm : minterms) {
            for (Minterm value : minterm) {
                if (value != null) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Finds Prime Implicants by comparing minterms
     * with Hamming distance of 1.
     *
     * @param minterms 2D array of minterms grouped by number of 1s
     * @param PIs ArrayList to store found Prime Implicants
     * @return New array of merged minterms
     */
    public static Minterm[][] foundPI(Minterm[][] minterms,
                                      ArrayList<Minterm> PIs){
        int n = 0;
        String distance;
        Minterm[][] newMinterms = new Minterm[minterms.length][100];
        char[][] newMintermsCounter = new char[minterms.length][100];
        // Compare each minterm with minterms in the next group
        for (int i = 0; i < minterms.length - 1; i++) {
            for (int j = 0; j < 100; j++) {
                if(minterms[i][j] == null){
                    break;
                }
                for(int k = 0; k < 100; k++){
                    if(i == minterms.length - 1){
                        return newMinterms;
                    }
                    if(minterms[i+1][k] == null){
                        break;
                    }
                    distance = minterms[i][j].distance(minterms[i+1][k]);
                    if(distance != null){
                        newMinterms[i][n] = new Minterm();
                        newMintermsCounter[i][j] = '*';
                        newMintermsCounter[i+1][k] = '*';
                        newMinterms[i][n].setBinary(distance);
                        newMinterms[i][n].setNumber
                                (minterms[i][j].getNumber()
                                + " " + minterms[i+1][k].getNumber());
                        n++;
                    }
                }
                // If minterm wasn't merged with any other,
                // it's a Prime Implicant
                if(newMintermsCounter[i][j] != '*'){
                    PIs.add(minterms[i][j]);
                }
            }
            n = 0;
            if(i == minterms.length - 2){
                break;
            }
        }
        // If no merges happened, add remaining minterms as PIs
        if(isEmpty(newMinterms)){
            for(int i = 0; i < minterms[0].length; i++){
                if(minterms[minterms.length-1][i] == null){
                    break;
                }
                newMinterms[0][n] = minterms[minterms.length-1][i];
                n++;

            }
        }
        return newMinterms;
    }

    /**
     * Finds the index of a minterm with a specific number string.
     *
     * @param minterms Array of minterms to search
     * @param s The number string to search for
     * @return Index of the minterm or -1 if not found
     */
    public static int returnIndex(Minterm[] minterms, String s){
        for(int i = 0; i < minterms.length; i++){
            if(minterms[i].getNumber().equals(s)){
                return i;
            }
        }
        return -1;
    }

    /**
     * Checks if a column in the cover table has exactly one star.
     *
     * @param coverTable The cover table
     * @param row The row to check
     * @return The index of the row with the star,
     * or -1 if multiple stars found
     */
    public static int oneStarInRow(char[][] coverTable, int row){
        int number = -1;
        for (int i = 0; i < coverTable.length; i++) {
            if (coverTable[i][row] == '*') {
                if (number != -1) {
                    return -1;
                }
                number = i;
            }
        }
        return number;
    }

    /**
     * Fills a row in the cover table with '-' to mark it as covered.
     *
     * @param coverTable The cover table
     * @param row The row to fill
     */
    public static void fillRow(char[][] coverTable, int row){
        for (int i = 0; i < coverTable.length; i++) {
            coverTable[i][row] = '-';
        }
    }

    /**
     * Checks if the cover table is completely covered
     * (all elements are '-').
     *
     * @param coverTable The cover table to check
     * @return true if all elements are '-', false otherwise
     */
    public static boolean isFull(char[][] coverTable){
        for (char[] chars : coverTable) {
            for (char aChar : chars) {
                if (aChar != '-') {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Helper method for generating
     * all subsets of a list of integers.
     *
     * @param subsets List to store all subsets
     * @param current Current subset being built
     * @param start Starting index
     * @param input Input list of integers
     */
    public static void generateSubsets(List<List<Integer>> subsets,
                                       List<Integer> current,
                                       int start,
                                       ArrayList<Integer> input){
        subsets.add(new ArrayList<>(current));
        for(int i = start; i < input.size(); i++){
            current.add(input.get(i));
            generateSubsets(subsets, current, i + 1, input);
            current.remove(current.size() - 1);
        }
    }

    /**
     * Generates all possible subsets of an input list.
     *
     * @param input The input list
     * @return List of all subsets
     */
    public static List<List<Integer>> Subsets
    (ArrayList<Integer> input){
        List<List<Integer>> subsets = new ArrayList<>();
        generateSubsets(subsets, new ArrayList<>(), 0, input);
        return subsets;
    }


    /**
     * Finds Essential Prime Implicants from
     * the list of Prime Implicants.
     *
     * @param PIs List of Prime Implicants
     * @param minterms Array of original minterms
     * @return List of Essential Prime Implicants
     */
    public static ArrayList<Minterm> foundEPI(
            ArrayList<Minterm> PIs
            , Minterm[] minterms){
        ArrayList <Minterm> EPIs = new ArrayList<>();
        char[][] coverTable = new char[PIs.size()][minterms.length];
        String[][] PINums = new String[PIs.size()][];
        // Build the cover table
        for (int i = 0; i < PIs.size(); i++) {
            PINums[i] = (PIs.get(i).getNumber().split(" "));
            for(int j = 0; j < PINums[i].length ; j++){
                coverTable[i][returnIndex(minterms,PINums[i][j])]
                        = '*';
            }
        }
        int[] columns = new int[coverTable.length];
        int column, index;
        // Find columns with exactly one star (essential PIs)
        for(int j = 0; j < minterms.length; j++){
            column = oneStarInRow(coverTable, j);
            if(column != -1){
                columns[column] = -1;
                EPIs.add(PIs.get(column));
                Arrays.fill(coverTable[column], '-');
                for(int k = 0; k < PINums[column].length; k++){
                    index = returnIndex(minterms,PINums[column][k]);
                    fillRow(coverTable, index);
                }
            }
        }
        if(isFull(coverTable)){
            return EPIs;
        }
        // Find remaining PIs needed to cover the function
        ArrayList<Integer> indexOfColumns = new ArrayList<>();
        for (int i = 0; i < columns.length; i++) {
            if(columns[i] != -1){
                indexOfColumns.add(i);
            }
        }
        List<List<Integer>> subsets = Subsets(indexOfColumns);
        int check = 0;
        boolean flag = false;
        List<List<Integer>> coverableSubsets = new ArrayList<>();
        // Find subsets that cover all remaining minterms
        for (List<Integer> subset : subsets) {
            for (int j = 0; j < minterms.length; j++) {
                for (Integer integer : subset) {
                    if (coverTable[integer][j] == '-'
                            || coverTable[integer][j] == '*') {
                        flag = true;
                        break;
                    }
                }
                if(flag){
                    flag = false;
                    check++;
                }
                else{
                    break;
                }
            }
            if (check == minterms.length) {
                coverableSubsets.add(subset);
            }
            check = 0;
        }
        // Find the smallest subset that covers all
        // remaining minterms
        int min = Integer.MAX_VALUE;
        List<Integer> EPI = new ArrayList<>();
        for (List<Integer> coverableSubset : coverableSubsets)
        {
            if (coverableSubset.size() < min) {
                min = coverableSubset.size();
                EPI = coverableSubset;
            }
        }
        for (Integer integer : EPI) {
            EPIs.add(PIs.get(integer));
        }
        return EPIs;
    }

    /**
     * Prints the final simplified boolean expression.
     *
     * @param EPIs List of Essential Prime Implicants
     */
    public static void printAnswer
    (ArrayList<Minterm> EPIs){
        int i;
        String s;
        for (int j = 0; j < EPIs.size(); j++) {
            i = 0;
            s = EPIs.get(j).getBinary();
            for(; i < s.length(); i++){
                if(s.charAt(i) == '1'){
                    System.out.print((char)('A' + i));
                }
                if(s.charAt(i) == '0'){
                    System.out.print((char)('A' + i));
                    System.out.print('\'');
                }
            }
            if(j == EPIs.size() - 1){
                break;
            }
            System.out.print(" + ");
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Scanner sc1 = new Scanner(System.in);
        System.out.println("Please enter the number " +
                "of variables: ");
        int number = sc.nextInt();
        if(number < 1){
            System.out.println("invalid number");
            return;
        }
        System.out.println("Please enter the expression: ");
        String expression = sc1.nextLine();
        if(!checkValidation(expression, number)){
            System.out.println("invalid expression");
            return;
        }
        // Generate all possible minterms
        String[] array = new String[(int)Math.pow(2, number)];
        for(int i = 0; i < array.length; i++) {
            array[i] = intToBinary(i, number);
        }
        ArrayList<String> mintermsList = new ArrayList<>();
        ArrayList<Integer> mintermsNum = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            if (findMin(expression, array[i])) {
                mintermsList.add(array[i]);
                mintermsNum.add(i);
            }
        }
        // Handle special cases
        if(mintermsList.isEmpty()){
            System.out.println("Answer: 0");
            return;
        }
        if(mintermsNum.size() == Math.pow(2, number)){
            System.out.println("Answer: 1");
            return;
        }

        Minterm[] minterms = new Minterm[mintermsNum.size()];
        for (int i = 0; i < mintermsList.size(); i++) {
            minterms[i] = new Minterm();
            minterms[i].setBinary(mintermsList.get(i));
            minterms[i].setNumber(String.valueOf(mintermsNum.get(i)));
        }
        // Group minterms by number of 1s
        int numberOfOnes;
        Minterm[][] mintermGroups = new Minterm[number + 1][30];
        for (Minterm minterm : minterms) {
            numberOfOnes = minterm.numberOfOnes();
            for (int j = 0; j < 30; j++) {
                if (mintermGroups[numberOfOnes][j] == null) {
                    mintermGroups[numberOfOnes][j] = new Minterm();
                    mintermGroups[numberOfOnes][j]
                            .setBinary(minterm.getBinary());
                    mintermGroups[numberOfOnes][j]
                            .setNumber(minterm.getNumber());
                    break;
                }
            }
        }
        ArrayList<Minterm> PIList = new ArrayList<>();
        Minterm[][] temp = mintermGroups;
        for (int i = 0; i < 4; i++) {
            temp = foundPI(temp, PIList);
        }
        // Remove duplicate PIs
        ArrayList<Minterm> PIs = new ArrayList<>();
        PIs.add(PIList.get(0));
        int index = 1;
        for (int i = 1; i < PIList.size(); i++) {
            if(PIList.get(i) == null){
                continue;
            }
            if(!PIs.get(index-1).getBinary().equals
                    ( PIList.get(i).getBinary())){
                if(i <= 2 || (!PIs.get(index - 2).getBinary()
                        .equals(PIList.get(i).getBinary()))){
                    PIs.add(PIList.get(i));
                    index++;
                }
            }
        }
        ArrayList<Minterm> EPIList = foundEPI(PIs, minterms);
        System.out.print("Answer: ");
        printAnswer(EPIList);
    }
}