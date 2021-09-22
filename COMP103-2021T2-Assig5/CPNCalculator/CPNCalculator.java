// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2021T2, Assignment 5
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.io.*;
import java.nio.file.*;

/** 
 * Calculator for Cambridge-Polish Notation expressions
 * (see the description in the assignment page)
 * User can type in an expression (in CPN) and the program
 * will compute and print out the value of the expression.
 * The template provides the method to read an expression and turn it into a tree.
 * You have to write the method to evaluate an expression tree.
 *  and also check and report certain kinds of invalid expressions
 */

public class CPNCalculator{

    //Saves
    HashSet<String> dict = new HashSet<>(Arrays.asList("+", "-", "/", "*"));

    /**
     * Setup GUI then run the calculator
     */
    public static void main(String[] args){
        CPNCalculator calc = new CPNCalculator();
        calc.setupGUI();
        calc.runCalculator();
    }

    /** Setup the gui */
    public void setupGUI(){
        UI.addButton("Clear", UI::clearText); 
        UI.addButton("Quit", UI::quit); 
        UI.setDivider(1.0);
    }

    /**
     * Run the calculator:
     * loop forever:  (a REPL - Read Eval Print Loop)
     *  - read an expression,
     *  - evaluate the expression,
     *  - print out the value
     * Invalid expressions could cause errors when reading or evaluating
     * The try-catch prevents these errors from crashing the program - 
     *  the error is caught, and a message printed, then the loop continues.
     */
    public void runCalculator(){
        UI.println("Enter expressions in pre-order format with spaces");
        UI.println("eg   ( * ( + 4 5 8 3 -10 ) 7 ( / 6 4 ) 18 )");
        while (true){
            UI.println();
            try {
                GTNode<ExpElem> expr = readExpr();
                double value = evaluate(expr);
                UI.println(" -> " + value);
            }catch(Exception e){UI.println("Something went wrong! "+e);}
        }
    }

    /**
     * Evaluate an expression and return the value
     * Returns Double.NaN if the expression is invalid in some way.
     * If the node is a number
     *  => just return the value of the number
     * or it is a named constant
     *  => return the appropriate value
     * or it is an operator node with children
     *  => evaluate all the children and then apply the operator.
     */
    public double evaluate(GTNode<ExpElem> expr){
        UI.println(expr.getItem().operator);

        /*# YOUR CODE HERE */
        //If the expression is a number, return the number
        if(expr.getItem().operator.equals("#")){
            return expr.getItem().value;
        }

        //If the dictionary of expressions contains the expr operator, run the operand.
        if(dict.contains(expr.getItem().operator)){
            String operand = expr.getItem().operator;
            
            //Creates values for operand
            ArrayList<Double> values = new ArrayList<>();
            double sum = 0;

            //Adds all children as numbers
            for(int i = 0; i < expr.numberOfChildren(); i++){
                double value = evaluate(expr.getChild(i));
                UI.println(value);
                values.add(value);
            }

            //Calculates sum based on operand
            switch(operand){
                case "+":
                    for(int i = 0; i < values.size(); i++){
                        sum += values.get(i);
                    }
                    break;
                
                case "-":
                    for(int i = 0; i < values.size(); i++){
                        sum -= values.get(i);
                    }
                    break;

                case "/":
                    for(int i = 0; i < values.size(); i++){
                        if(i == 0){
                            sum = values.get(i);
                        }else{
                            sum = sum / values.get(i);
                        }
                    }
                    break;
                
                case "*":
                    for(int i = 0; i < values.size(); i++){
                        if(i == 0){
                            sum = 1;
                        }
                        sum = sum * values.get(i);
                    }
                    break;
            }

            UI.println(sum);
            return sum;            
        }
        
        return Double.NaN;
    }

    /** 
     * Reads an expression from the user and constructs the tree.
     */ 
    public GTNode<ExpElem> readExpr(){
        String expr = UI.askString("expr:");
        return readExpr(new Scanner(expr));   // the recursive reading method
    }

    /**
     * Recursive helper method.
     * Uses the hasNext(String pattern) method for the Scanner to peek at next token
     */
    public GTNode<ExpElem> readExpr(Scanner sc){
        if (sc.hasNextDouble()) {                     // next token is a number: return a new node
            return new GTNode<ExpElem>(new ExpElem(sc.nextDouble()));
        }
        else if (sc.hasNext("\\(")) {                 // next token is an opening bracket
            sc.next();                                // read and throw away the opening '('
            ExpElem opElem = new ExpElem(sc.next());  // read the operator
            GTNode<ExpElem> node = new GTNode<ExpElem>(opElem);  // make the node, with the operator in it.
            while (! sc.hasNext("\\)")){              // loop until the closing ')'
                GTNode<ExpElem> child = readExpr(sc); // read each operand/argument
                node.addChild(child);                 // and add as a child of the node
            }
            sc.next();                                // read and throw away the closing ')'
            return node;
        }
        else {                                        // next token must be a named constant (PI or E)
                                                      // make a token with the name as the "operator"
            return new GTNode<ExpElem>(new ExpElem(sc.next()));
        }
    }

}

