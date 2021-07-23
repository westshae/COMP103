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

    private Stack<ArrayList<Line>> strokes = new Stack();
    private ArrayList<Line> stroke = new ArrayList<>();


    /**
     * Setup the GUI
     */
    public void setupGUI(){
        UI.setMouseMotionListener(this::doMouse);
        UI.addButton("Quit", UI::quit);
        UI.addButton("Undo", this::undo);
        UI.setLineWidth(3);
        UI.setDivider(0.0);
    }

    //Undoes a drawing
    public void undo(){
        UI.clearGraphics();
        strokes.pop();
        for(int i = 0; i < strokes.size(); i++){
            ArrayList<Line> stroke = strokes.get(i);
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
            strokes.push(stroke);
            System.out.println(strokes);
        }
    }

    public static void main(String[] arguments){
        new Pencil().setupGUI();
    }

}
