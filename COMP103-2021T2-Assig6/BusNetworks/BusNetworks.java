// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2021T2, Assignment 6
 * Name: Shae West
 * Username: westshae
 * ID: 300565911
 */

import ecs100.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;

public class BusNetworks {

    /** Map of towns, indexed by their names */
    private Map<String,Town> busNetwork = new HashMap<String,Town>();

    /** CORE
     * Loads a network of towns from a file.
     * Constructs a Set of Town objects in the busNetwork field
     * Each town has a name and a set of neighbouring towns
     * First line of file contains the names of all the towns.
     * Remaining lines have pairs of names of towns that are connected.
     */
    public void loadNetwork(String filename) {
        try {
            busNetwork.clear();
            UI.clearText();
            List<String> lines = Files.readAllLines(Path.of(filename));
            String firstLine = lines.remove(0);

            //Creates a list of towns, which are put into the busNetwork hashmap
            String[] allTowns = firstLine.split(" ");
            for(String name : allTowns){
                busNetwork.put(name, new Town(name));
            }

            //For all lines, add neighbour to town
            for(String line : lines){
                String[] splitLine = line.split(" ");
                Town parent = busNetwork.get(splitLine[0]);
                Town child = busNetwork.get(splitLine[1]);
                parent.addNeighbour(child);
                child.addNeighbour(parent);
            }

            UI.println("Loaded " + busNetwork.size() + " towns:");

        } catch (IOException e) {throw new RuntimeException("Loading data.txt failed" + e);}
    }

    /**  CORE
     * Print all the towns and their neighbours:
     * Each line starts with the name of the town, followed by
     *  the names of all its immediate neighbours,
     */
    public void printNetwork() {
        UI.println("The current network: \n====================");
        /*# YOUR CODE HERE */
        for(Map.Entry<String, Town> entry : busNetwork.entrySet()){
            //Gets parent town and neighbours
            Town town = entry.getValue();
            Set<Town> neighbours = town.getNeighbours();

            //Prints out the parent, plus all neighbours
            UI.print(town.getName() + " => ");
            for(Town current : neighbours){
                UI.print(current.getName() + " ");
            }
            UI.print("\n");
        }
        

    }

    /** COMPLETION
     * Return a set of all the nodes that are connected to the given node.
     * Traverse the network from this node in the standard way, using a
     * visited set, and then return the visited set
     */
    public HashSet<Town> findAllConnected(Town town) {
        HashSet<Town> visited = new HashSet<>();
        findAllConnectedHelper(town, visited);//Calls helper function

        return visited;
    }

    private void findAllConnectedHelper(Town town, HashSet<Town> visited){
        visited.add(town);//Adds current down to visited list
        for(Town current : town.getNeighbours()){
            if(visited.contains(current)){continue;}//if the neightbour is already included in visited, ignore it
            findAllConnectedHelper(current, visited);//Recursive call on neighbour
        }
    }

    /**  COMPLETION
     * Print all the towns that are reachable through the network from
     * the town with the given name.
     * Note, do not include the town itself in the list.
     */
    public void printReachable(String name){
        Town town = busNetwork.get(name);
        if (town==null){
            UI.println(name+" is not a recognised town");
        }
        else {
            UI.println("\nFrom "+town.getName()+" you can get to:");
            /*# YOUR CODE HERE */
            //For all connected downs, print their name
            for(Town current : findAllConnected(town)){
                UI.println(current.getName());
            }

        }

    }

    /**  COMPLETION
     * Print all the connected sets of towns in the busNetwork
     * Each line of the output should be the names of the towns in a connected set
     * Works through busNetwork, using findAllConnected on each town that hasn't
     * yet been printed out.
     */
    public void printConnectedGroups() {
        UI.println("Groups of Connected Towns: \n================");

        //Initializes ArrayLists used.
        ArrayList<Town> targetSet = new ArrayList<Town>(busNetwork.values());
        ArrayList<HashSet<Town>> groups = new ArrayList<>();

        //While the targetSet has towns available
        while(targetSet.size() > 0){
            //Gets the current "first in line" town, as others have been removed.
            Town currentTown = targetSet.get(0);
            HashSet<Town> currentGroup = findAllConnected(currentTown);//Gets all connected towns
            for(Town town : currentGroup){//For each connected town, remove them from the target set.
                targetSet.remove(town);
            }
            groups.add(currentGroup);//Add all the connected towns to one group
        }

        //Prints each group, with their group number
        for(int i = 0; i < groups.size(); i++){
            HashSet<Town> towns = groups.get(i);

            UI.print("Group " + (i+1) + ": ");
            for(Town town : towns){
                UI.print(town.getName() + " ");
            }
            UI.print("\n");
        }
    }

    /**
     * Set up the GUI (buttons and mouse)
     */
    public void setupGUI() {
        UI.addButton("Load", ()->{loadNetwork(UIFileChooser.open());});
        UI.addButton("Print Network", this::printNetwork);
        UI.addTextField("Reachable from", this::printReachable);
        UI.addButton("All Connected Groups", this::printConnectedGroups);
        UI.addButton("Clear", UI::clearText);
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(1100, 500);
        UI.setDivider(1.0);
        loadNetwork("data-small.txt");
    }

    // Main
    public static void main(String[] arguments) {
        BusNetworks bnw = new BusNetworks();
        bnw.setupGUI();
    }

}
