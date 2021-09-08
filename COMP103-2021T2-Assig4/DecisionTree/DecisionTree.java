// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2021T2, Assignment 4
 * Name: Shae West  
 * Username: westshae
 * ID: 300565911
 */

/**
 * Implements a decision tree that asks a user yes/no questions to determine a decision.
 * Eg, asks about properties of an animal to determine the type of animal.
 * 
 * A decision tree is a tree in which all the internal nodes have a question, 
 * The answer to the question determines which way the program will
 *  proceed down the tree.  
 * All the leaf nodes have the decision (the kind of animal in the example tree).
 *
 * The decision tree may be a predermined decision tree, or it can be a "growing"
 * decision tree, where the user can add questions and decisions to the tree whenever
 * the tree gives a wrong answer.
 *
 * In the growing version, when the program guesses wrong, it asks the player
 * for another question that would help it in the future, and adds it (with the
 * correct answers) to the decision tree. 
 *
 */

import ecs100.*;
import java.util.*;
import java.io.*;
import java.net.http.HttpResponse.ResponseInfo;
import java.nio.file.*;
import java.awt.Color;

public class DecisionTree {

    public DTNode theTree;    // root of the decision tree;

    /**
     * Setup the GUI and make a sample tree
     */
    public static void main(String[] args){
        DecisionTree dt = new DecisionTree();
        dt.setupGUI();
        dt.loadTree("sample-animal-tree.txt");
    }

    /**
     * Set up the interface
     */
    public void setupGUI(){
        UI.addButton("Load Tree", ()->{loadTree(UIFileChooser.open("File with a Decision Tree"));});
        UI.addButton("Print Tree", this::printTree);
        UI.addButton("Run Tree", this::runTree);
        UI.addButton("Grow Tree", this::growTree);
        UI.addButton("Save Tree", this::saveTree);  // for completion
        // UI.addButton("Draw Tree", this::drawTree);  // for challenge
        UI.addButton("Reset", ()->{loadTree("sample-animal-tree.txt");});
        UI.addButton("Quit", UI::quit);
        UI.setDivider(0.5);
    }

    /**  
     * Print out the contents of the decision tree in the text pane.
     * The root node should be at the top, followed by its "yes" subtree,
     * and then its "no" subtree.
     * Needs a recursive "helper method" which is passed a node.
     * 
     * COMPLETION:
     * Each node should be indented by how deep it is in the tree.
     * The recursive "helper method" is passed a node and an indentation string.
     *  (The indentation string will be a string of space characters)
     */
    public void printTree(){
        recursivePrintTree(theTree, "",0);//Calls recursive "helper method"
    }

    public void recursivePrintTree(DTNode node, String yesNo, int indent){
        //CORE CODE
        // if(node != null){//Only runs if the getYes/getNo node exists
        //     UI.println(node.getText());
        //     //Recursively calls the function for both children
        //     recursivePrintTree(node.getYes(), "yes", current);
        //     recursivePrintTree(node.getNo(), "no", current);
        // }

        if(node != null){//Only runs if the getYes/getNo node exists

            //Creates string builder, and for each indent number, creates 4 spaces at the beginning of the message
            StringBuilder builder = new StringBuilder("");
            for(int i = 0; i < indent; i++){
                builder.append("    ").toString();
            }

            //If current node is answer, increase indent, else decrease indent
            if(node.isAnswer()){indent--;}
            else{indent++;}


            UI.println(builder.toString() + yesNo + node.getText());
            
            //Recursively calls the function for both children
            recursivePrintTree(node.getYes(), "y:", indent);
            recursivePrintTree(node.getNo(), "n:", indent);
        }
    }

    /**
     * Run the tree by starting at the top (of theTree), and working
     * down the tree until it gets to a leaf node (a node with no children)
     * If the node is a leaf it prints the answer in the node
     * If the node is not a leaf node, then it asks the question in the node,
     * and depending on the answer, goes to the "yes" child or the "no" child.
     */
    public void runTree() {
        DTNode node = theTree;
        while(true){
            if(node.isAnswer()){//If the node is the answer, only prints the node
                UI.println("The answer is: " +node.getText());
                break;
            }
            else{//If the node isn't the answer, asks the current node's question
                String response = UI.askString(node.getText());
                switch(response.toLowerCase()){
                    case "yes":
                        node = node.getYes();
                        continue;                            

                    case "no":
                        node = node.getNo();
                        continue;
                    
                    default:
                        UI.println("invalid answer, only yes/no are accepted");
                        break;
                }
            }
        }
    }

    /**
     * Grow the tree by allowing the user to extend the tree.
     * Like runTree, it starts at the top (of theTree), and works its way down the tree
     *  until it finally gets to a leaf node. 
     * If the current node has a question, then it asks the question in the node,
     * and depending on the answer, goes to the "yes" child or the "no" child.
     * If the current node is a leaf it prints the decision, and asks if it is right.
     * If it was wrong, it
     *  - asks the user what the decision should have been,
     *  - asks for a question to distinguish the right decision from the wrong one
     *  - changes the text in the node to be the question
     *  - adds two new children (leaf nodes) to the node with the two decisions.
     */
    public void growTree () {
        DTNode node = theTree;
        while(true){
            if(!node.isAnswer()){//If the current node is a question
                String response = UI.askString(node.getText());
                switch(response.toLowerCase()){//Sets node to answer, or requests valid response
                    case "yes":
                        node = node.getYes();
                        continue;     

                    case "no":
                        node = node.getNo();
                        continue;

                    default:
                        UI.println("invalid response, only yes/no are accepted");
                        break;
                }
                
            }

            //Only reaches this point if the current node is the answer
            String correct = UI.askString(node.getText() + " : Is this correct?");
            switch (correct.toLowerCase()) {//If yes, ignore, if no, add new node, else request valid response.
                case "yes":
                    break;

                case "no":
                    //Asks for new answer and true/false question
                    String shouldBe = UI.askString("What should it be?  ");
                    String question = UI.askString("Question where " + shouldBe + " is true, " + node.getText() + " is false?: ");

                    //Creates and updates node
                    DTNode yes = new DTNode(shouldBe);
                    DTNode no = new DTNode(node.getText());
                    node.setText(question + ":");
                    node.setChildren(yes, no);
                    UI.println("New node added to tree");
                    break;
                
                default:
                    UI.println("invalid response, only yes/no are accepted");
                    break;
            }
        }
    }

    private ArrayList<String> toSave = new ArrayList<String>();//Global string to save list of lines to save to file

    public void saveTree(){
        toSave = new ArrayList<String>();//Resets list of lines
        recursiveSaveTree(theTree);

        try{//Creates the file, asks for a filename, saves the list of lines to the file, then saves the file
            FileWriter file = new FileWriter(UI.askString("File name?") + ".txt"); 
            for(String currentString: toSave) {
                file.write(currentString + "\n");
            }
            file.close();
        }catch(IOException e){
            System.out.println(e);
        }
    }

    public void recursiveSaveTree(DTNode node){
        if(node != null){//Only runs if the getYes/getNo node exists
            if(node.isAnswer()){
                UI.println("Answer: " + node.getText());
                toSave.add("Answer: " + node.getText());
            }else{
                UI.println("Question: " + node.getText());
                toSave.add("Question: " + node.getText());
            }
            
            //Recursively calls the function for both children
            recursiveSaveTree(node.getYes());
            recursiveSaveTree(node.getNo());
        }
    }

    // You will need to define methods for the Completion and Challenge parts.

    // Written for you

    /** 
     * Loads a decision tree from a file.
     * Each line starts with either "Question:" or "Answer:" and is followed by the text
     * Calls a recursive method to load the tree and return the root node,
     *  and assigns this node to theTree.
     */
    public void loadTree (String filename) { 
        if (!Files.exists(Path.of(filename))){
            UI.println("No such file: "+filename);
            return;
        }
        try{theTree = loadSubTree(new ArrayDeque<String>(Files.readAllLines(Path.of(filename))));}
        catch(IOException e){UI.println("File reading failed: " + e);}
    }

    /**
     * Loads a tree (or subtree) from a Scanner and returns the root.
     * The first line has the text for the root node of the tree (or subtree)
     * It should make the node, and 
     *   if the first line starts with "Question:", it loads two subtrees (yes, and no)
     *    from the scanner and add them as the  children of the node,
     * Finally, it should return the  node.
     */
    public DTNode loadSubTree(Queue<String> lines){
        Scanner line = new Scanner(lines.poll());
        String type = line.next();
        String text = line.nextLine().trim();
        DTNode node = new DTNode(text);
        if (type.equals("Question:")){
            DTNode yesCh = loadSubTree(lines);
            DTNode noCh = loadSubTree(lines);
            node.setChildren(yesCh, noCh);
        }
        return node;

    }



}
