// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2021T2, Assignment 3
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.awt.Color;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import javax.swing.JButton;

/**
 *  Simple 'Minesweeper' program.
 *  There is a grid of squares, some of which contain a mine.
 *  
 *  The user can click on a square to either expose it or to
 *  mark/unmark it.
 *  
 *  If the user exposes a square with a mine, they lose.
 *  Otherwise, it is uncovered, and shows a number which represents the
 *  number of mines in the eight squares surrounding that one.
 *  If there are no mines adjacent to it, then all the unexposed squares
 *  immediately adjacent to it are exposed (and so on)
 *
 *  If the user marks a square, then they cannot expose the square,
 *  (unless they unmark it first)
 *  When all the squares without mines are exposed, the user has won.
 */
public class MineSweeper {

    public static final int ROWS = 15;
    public static final int COLS = 15;

    public static final double LEFT = 10; 
    public static final double TOP = 10;
    public static final double SQUARE_SIZE = 20;

    // Fields
    private boolean marking;

    private Square[][] squares;

    private JButton mrkButton;
    private JButton expButton;
    Color defaultColor;

    /** 
     * Construct a new MineSweeper object
     * and set up the GUI
     */
    public static void main(String[] arguments){
        MineSweeper ms = new MineSweeper();
        ms.setupGUI();
        ms.setMarking(false);
        ms.makeGrid();
    }

    /** Set up the GUI: buttons and mouse to play the game */
    public void setupGUI(){
        UI.setMouseListener(this::doMouse);
        UI.addButton("New Game", this::makeGrid);
        this.expButton = UI.addButton("Expose", ()->setMarking(false));
        this.mrkButton = UI.addButton("Mark", ()->setMarking(true));

        UI.addButton("Quit", UI::quit);
        UI.setDivider(0.0);
    }

    /** Respond to mouse events */
    public void doMouse(String action, double x, double y) {
        if (action.equals("released")){
            int row = (int)((y-TOP)/SQUARE_SIZE);
            int col = (int)((x-LEFT)/SQUARE_SIZE);
            if (row>=0 && row < ROWS && col >= 0 && col < COLS){
                if (marking) { mark(row, col);}
                else         { tryExpose(row, col); }
            }
        }
    }


    // Other Methods

    /** 
     * The player has clicked on a square to expose it
     * - if it is already exposed or marked, do nothing.
     * - if it's a mine: lose (call drawLose()) 
     * - otherwise expose it (call exposeSquareAt)
     * then check to see if the player has won and call drawWon() if they have.
     * (This method is not recursive)
     */
    public void tryExpose(int row, int col){
        Square clicked = squares[row][col];
        if(clicked.isExposed()){return;}
        if(clicked.hasMine()){ drawLose(); }
        else { exposeSquareAt(row, col); }

//        if (hasWon()){
//            drawWin();
//        }
    }

    /** 
     *  Ensures that the square at row and col is exposed.
     *  If it is already exposed, do nothing.
     *  Otherwise,
     *    Expose it and redraw it.
     *    If the number of adjacent mines of this square is 0, then none of
     *      its neighbours have mines, so
     *      expose all its eight neighbours 
     *      (and if they have no adjacent mines, expose their neighbours, and ....)
     *      (be careful not to go over the edges of the map)
     */
    public void exposeSquareAt(int row, int col){
        try {
            if (row < 0 || row > 15 || col < 0 || col > 15) {
                return;
            }
            UI.sleep(1);
            System.out.println(row + ":" + col);
            Square clicked = squares[row][col];
            if (clicked.getAdjacentMines() == 0 && !clicked.isExposed()) {
                clicked.setExposed();
                clicked.draw(row, col);
                if(row != 15 && col != 0)exposeSquareAt(row+1, col-1);
                if(row != 15)exposeSquareAt(row+1, col);
                if(row != 15 && col != 15)exposeSquareAt(row+1, col+1);
                if(col != 0)exposeSquareAt(row, col-1);
                if(col != 0)exposeSquareAt(row, col+1);
                if(row != 0 && col != 0)exposeSquareAt(row-1, col-1);
                if(row != 0)exposeSquareAt(row-1, col);
                if(row != 0 && col != 15)exposeSquareAt(row-1, col+1);


            } else {
                clicked.setExposed();
                clicked.draw(row, col);
                return;
            }
        }catch (ArrayIndexOutOfBoundsException e){ return;}
    }

    /** 
     * Returns true if the player has won:
     * If any square without a mine is not exposed, then the player has not won yet.
     * If all the squares without a mine have been exposed, then the player has won.
     * (It doesn't matter if the squares with a mine have been marked or not).
     */
    public boolean hasWon(){
        /*# YOUR CODE HERE */

        return true;
    }

    // completed methods
    
    /**
     * Mark/unmark the square.
     * If the square is exposed, don't do anything,
     * If it is marked, unmark it and redraw,
     * otherwise mark it and redraw.
     */
    public void mark(int row, int col){
        Square square = squares[row][col];
        if (square.isExposed())    { return; }
        else if (square.isMarked()){ square.unMark(); }
        else                       { square.mark(); }
        square.draw(row, col);
    }

    /**
     * Respond to the Mark and Expose buttons:
     * Remember whether the user is currently "Marking" or "Exposing"
     * Change the colour of the "Mark", "Expose" buttons
     */
    public void setMarking(boolean v){
        marking=v;
        if (marking) {
            mrkButton.setBackground(Color.red);
            expButton.setBackground(null);
        }
        else {
            expButton.setBackground(Color.red);
            mrkButton.setBackground(null);
        }
    }


    /**
     * Construct a grid with random mines.
     * Compute the number of adjacent mines in
     */
    public void makeGrid(){
        UI.clearGraphics();
        this.squares = new Square[ROWS][COLS];
        for (int row=0; row < ROWS; row++){
            for (int col=0; col<COLS; col++){
                boolean isMine = Math.random()<0.1;     // approx 1 in 10 squares is a mine 
                this.squares[row][col] = new Square(isMine);
                this.squares[row][col].draw(row, col);
            }
        }
        // now compute the number of adjacent mines for each square
        for (int row=0; row<ROWS; row++){
            for (int col=0; col<COLS; col++){
                int count = 0;
                //look at each square in the neighbourhood.
                for (int r=Math.max(row-1,0); r<Math.min(row+2, ROWS); r++){
                    for (int c=Math.max(col-1,0); c<Math.min(col+2, COLS); c++){
                        if (squares[r][c].hasMine())
                            count++;
                    }
                }
                if (this.squares[row][col].hasMine())
                    count--;  // we weren't suppose to count this square, just the adjacent ones.

                this.squares[row][col].setAdjacentMines(count);
            }
        }
    }

    /** Draw a message telling the player they have won */
    public void drawWin(){
        UI.setFontSize(28);
        UI.drawString("You Win!", LEFT + COLS*SQUARE_SIZE + 20, TOP + ROWS*SQUARE_SIZE/2);
        UI.setFontSize(12);
    }

    /**
     * Draw a message telling the player they have lost
     * and expose all the squares and redraw them
     */
    public void drawLose(){
        for (int row=0; row<ROWS; row++){
            for (int col=0; col<COLS; col++){
                squares[row][col].setExposed();
                squares[row][col].draw(row, col);
            }
        }
        UI.setFontSize(28);
        UI.drawString("You Lose!", LEFT + COLS*SQUARE_SIZE+20, TOP + ROWS*SQUARE_SIZE/2);
        UI.setFontSize(12);
    }

    /**
     * Return a grid of integers, showing the visible state of the board:
     * -1 for any square that is not exposed
     * 0 - 8 for any exposed square, saying how many mines are adjacent to it.
     */
    public int[][] getVisibleState(){
        int[][] ans = new int[ROWS][COLS];
        for (int r=0; r<ROWS ; r++){
            for (int c=0; c<COLS; c++){
                ans[r][c] = squares[r][c].isExposed()?(squares[r][c].getAdjacentMines()):-1;
            }
        }
        return ans;
    }


}
