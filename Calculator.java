import java.util.*;

public class Calculator {
    public static void main(String[] args) {
        List<String> tokenisedExp = tokenise("cos((5 * sin(30) / 102 * 6 * (6 * 3^2) - 6))");
        System.out.println(tokenisedExp);
        double result = evaluateExp(tokenisedExp);
        System.out.println(result);
    }
    
    private static ArrayList<String> tokenise(String exp) {
        var tokenisedExp = new ArrayList<String>();

        for(int i = 0; i < exp.length(); i++) {
            if(Character.isWhitespace(exp.charAt(i))) continue;

            if(exp.charAt(i) == '(' || exp.charAt(i) == ')' || isSingle(exp.charAt(i))) {
                tokenisedExp.add("" + exp.charAt(i));
                continue;
            }


            String token = "";
            while(true) {
                token += "" + exp.charAt(i);

                if(Character.isDigit(exp.charAt(i))) {
                    if (i + 1 >= exp.length() || !Character.isDigit(exp.charAt(i + 1))) {
                        tokenisedExp.add(token);
                        break;
                    }
                } else if(!Character.isDigit(exp.charAt(i))) {
                    if (i + 1 >= exp.length() || Character.isDigit(exp.charAt(i + 1)) || exp.charAt(i + 1) == '(') {
                        tokenisedExp.add(token);
                        break;
                    }
                }
                i++;
            }
        }

        return tokenisedExp;
    }

    private static double evaluateExp(List<String> exp) {
        var simplifiedExp = new ArrayList<String>();
        
        for(int i = 0; i < exp.size(); i++) {
            String token = exp.get(i);
            if(token.matches("\\d+")) {
                simplifiedExp.add(token);
                if (i == 0 || i == exp.size() -1 || exp.get(i - 1).equals("(") || exp.get(i + 1).equals(")") || precedence(exp.get(i - 1)) < precedence(exp.get(i + 1))) continue;
                String op = simplifiedExp.get(simplifiedExp.size() - 2);
                Double secondOperand = Double.parseDouble(token);
                Double firstOperand = 1.0;
                if(!hasOnlyOneOperand(op)) {
                    firstOperand = Double.parseDouble(simplifiedExp.get(simplifiedExp.size() - 3));
                    simplifiedExp.subList(simplifiedExp.size() - 3, simplifiedExp.size()).clear();
                } else {
                    simplifiedExp.subList(simplifiedExp.size() - 2, simplifiedExp.size()).clear();
                }

                simplifiedExp.add(String.valueOf(evaluateTerm(firstOperand, secondOperand, op)));

            } else if(token.equals(")")) {
                while(true) {
                    String lastElm = simplifiedExp.get(simplifiedExp.size() - 1);
                    String op = simplifiedExp.get(simplifiedExp.size() - 2);
                    Double secondOperand = Double.parseDouble(lastElm);
                    Double firstOperand = 1.0;

                    if(simplifiedExp.get(simplifiedExp.size() - 2).equals("(")) {
                        simplifiedExp.remove(simplifiedExp.size() - 2);
                        break;
                    }
                    
                    if(!hasOnlyOneOperand(op)) {
                        firstOperand = Double.parseDouble(simplifiedExp.get(simplifiedExp.size() - 3));
                        simplifiedExp.subList(simplifiedExp.size() - 3, simplifiedExp.size()).clear();
                    } else {
                        simplifiedExp.subList(simplifiedExp.size() - 2, simplifiedExp.size()).clear();
                    }
                    simplifiedExp.add(String.valueOf(evaluateTerm(firstOperand, secondOperand, op)));
                }

                while(i < exp.size() - 2 && simplifiedExp.size() > 1 && !simplifiedExp.get(simplifiedExp.size() - 2).equals("(") && !exp.get(i + 1).equals(")") && precedence(simplifiedExp.get(simplifiedExp.size() - 2)) >= precedence(exp.get(i + 1))) {
                    String lastElm = simplifiedExp.get(simplifiedExp.size() - 1);
                    String op = simplifiedExp.get(simplifiedExp.size() - 2);
                    Double secondOperand = Double.parseDouble(lastElm);
                    Double firstOperand = 1.0;
                    
                    if(!hasOnlyOneOperand(op)) {
                        firstOperand = Double.parseDouble(simplifiedExp.get(simplifiedExp.size() - 3));
                        simplifiedExp.subList(simplifiedExp.size() - 3, simplifiedExp.size()).clear();
                    } else {
                        simplifiedExp.subList(simplifiedExp.size() - 2, simplifiedExp.size()).clear();
                    }
                    simplifiedExp.add(String.valueOf(evaluateTerm(firstOperand, secondOperand, op)));
                }

            } else {
                simplifiedExp.add(token);
            }
            
        }

        while(simplifiedExp.size() != 1) {
            String lastElm = simplifiedExp.get(simplifiedExp.size() - 1);
            String op = simplifiedExp.get(simplifiedExp.size() - 2);
            Double secondOperand = Double.parseDouble(lastElm);
            Double firstOperand = 1.0;
            
            if(!hasOnlyOneOperand(op)) {
                firstOperand = Double.parseDouble(simplifiedExp.get(simplifiedExp.size() - 3));
                simplifiedExp.subList(simplifiedExp.size() - 3, simplifiedExp.size()).clear();
            } else {
                simplifiedExp.subList(simplifiedExp.size() - 2, simplifiedExp.size()).clear();
            }
            simplifiedExp.add(String.valueOf(evaluateTerm(firstOperand, secondOperand, op)));
        }

        return Double.parseDouble(simplifiedExp.get(0));
    }

    private static double evaluateTerm(Double firstOperand, Double secondOperand, String op) {
        double angle = Math.toRadians(secondOperand);

        switch (op) {
            case "+":
                return firstOperand + secondOperand;
            case "-":
                return firstOperand - secondOperand;
            case "*":
                return firstOperand * secondOperand;
            case "/":
                return firstOperand / secondOperand; 
            case "%":
                return firstOperand % secondOperand;
            case "^":
                return Math.pow(firstOperand, secondOperand);
            case "sqrt":
                return Math.sqrt(secondOperand);
            case "root":
                return Math.pow(firstOperand, 1 / secondOperand);
            case "sin":
                return Math.sin(angle);
            case "cos":
                return Math.cos(angle);
            case "tan":
                return Math.tan(angle);
            default:
                return 0;
        }
    }

    private static int precedence(String op) {
        if(op.equals("+") || op.equals("-")) return 1;
        if(op.equals("*") || op.equals("/") || op.equals("%")) return 2;
        if(op.equals("^") || op.equals("sqrt")) return 3;
        if(op.equals("cos") || op.equals("sin") || op.equals("tan")) return 4;

        return 0;
    }

    private static boolean hasOnlyOneOperand(String op) {
        String[] OpsWithOnlyOneOperand = {"sin", "cos", "tan", "sqrt"};

        for(String c2: OpsWithOnlyOneOperand) {
            if(op.equals(c2)) {
                return true;
            }
        }

        return false;
    }
   
    private static boolean isSingle(char c) {
        char[] singleOperators = {'+', '-', '*', '/', '%', '^'};

        for(char c2: singleOperators) {
            if(c == c2) {
                return true;
            }
        }

        return false;
    }

}