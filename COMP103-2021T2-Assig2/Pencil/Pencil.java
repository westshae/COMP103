// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2021T2, Assignment 2
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.util.*;

/** Pencil   */
public class Pencil{
    private double lastX;
    private double lastY;

    private Stack<ArrayList<Line>> undo = new Stack();
    private Stack<ArrayList<Line>> redo = new Stack();

    private ArrayList<Line> stroke = new ArrayList<>();


    /**
     * Setup the GUI
     */
    public void setupGUI(){
        UI.setMouseMotionListener(this::doMouse);
        UI.addButton("Quit", UI::quit);
        UI.addButton("Undo", this::handleUndo);
        UI.addButton("Redo", this::handleRedo);
        UI.setLineWidth(3);
        UI.setDivider(0.0);
    }

    //Undoes a drawing
    public void handleUndo(){
        UI.clearGraphics();
        ArrayList<Line> removedStroke = undo.pop();
        redo.push(removedStroke);
        for(int i = 0; i < undo.size(); i++){
            ArrayList<Line> stroke = undo.get(i);
            for(int j = 0; j < stroke.size(); j++){
                Line line = stroke.get(j);
                UI.drawLine(line.x1,line.y1,line.x2,line.y2);
            }
        }
    }

    //Redoes a drawing
    public void handleRedo(){
        UI.clearGraphics();

        ArrayList<Line> strokeToDo = redo.pop();
        undo.push(strokeToDo);

        for(int i = 0; i < undo.size(); i++){
            ArrayList<Line> stroke = undo.get(i);
            for(int j = 0; j < stroke.size(); j++){
                Line line = stroke.get(j);
                UI.drawLine(line.x1,line.y1,line.x2,line.y2);
            }
        }
    }


    //Object that saves the lines drawn
    public class Line{
        double x1;
        double y1;

        double x2;
        double y2;

        public Line(double x1, double y1, double x2, double y2){
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
    }

    /**
     * Respond to mouse events
     */
    public void doMouse(String action, double x, double y) {
        if (action.equals("pressed")){
            redo = new Stack<ArrayList<Line>>();
            stroke = new ArrayList<>();
            lastX = x;
            lastY = y;
        }
        else if (action.equals("dragged")){
            UI.drawLine(lastX, lastY, x, y);

            Line line = new Line(x,y,lastX,lastY);
            stroke.add(line);

            lastX = x;
            lastY = y;
//            System.out.println(stroke);

        }
        else if (action.equals("released")){
            UI.drawLine(lastX, lastY, x, y);
            undo.push(stroke);
            System.out.println(undo);
        }
    }

    public static void main(String[] arguments){
        new Pencil().setupGUI();
    }

}
