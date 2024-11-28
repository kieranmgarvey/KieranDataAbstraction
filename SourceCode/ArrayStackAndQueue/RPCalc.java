package csci2320;

import java.util.Map;

/**
 * This class represents a reverse Polish calculator.
 */
public class RPCalc {
  /**
   * Evaluate a space separated string in RPC format that can contain variables.
   * 
   * @param expr      the expression to evaluate.
   * @param variables a map of the variable names to their values.
   * @return the value of that RPC expression.
   */
  public static double eval(String expr, Map<String, Double> variables) {
    Stack<Double> RPCStack = new ArrayStack<>();
    String[] tokens = expr.split("\\s+");

    for (String token : tokens) {
      if (isOperator(token)) {
        double b = RPCStack.pop();
        double a = RPCStack.pop();
        double result = applyOperator(a, b, token);
        RPCStack.push(result);
      } else if (variables.containsKey(token)) {
        RPCStack.push(variables.get(token));
      } else {
        RPCStack.push(Double.parseDouble(token));
      }
    }

    if (RPCStack.isEmpty()) {
      throw new IllegalArgumentException("Stack is Empty!");
    }

    return RPCStack.pop();
  }

  private static boolean isOperator(String token) {
    return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/");
  }

  private static double applyOperator(double a, double b, String operator) {
    switch (operator) {
      case "+":
        return a + b;
      case "-":
        return a - b;
      case "*":
        return a * b;
      case "/":
        if (b == 0) {
          throw new ArithmeticException("Division by zero");
        }
        return a / b;
      default:
        throw new IllegalArgumentException("Operator cannot be used!");
    }
  }
}
