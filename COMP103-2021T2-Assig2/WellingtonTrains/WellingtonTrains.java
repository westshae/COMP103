// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2021T2, Assignment 2
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;

import java.sql.Array;
import java.util.*;
import java.util.Map.Entry;
import java.io.*;
import java.nio.file.*;

/**
 * WellingtonTrains
 * A program to answer queries about Wellington train lines and timetables for
 *  the train services on those train lines.
 *
 * See the assignment page for a description of the program and what you have to do.
 */

public class WellingtonTrains{
    //Fields to store the collections of Stations and Lines
    private ArrayList<Station> allStations = new ArrayList<>();
    private ArrayList<TrainLine> allLines = new ArrayList<>();
    private ArrayList<TrainService> allServices = new ArrayList<>();

    private HashMap<String, ArrayList<Integer>> stationData = new HashMap<>();

    // Fields for the suggested GUI.
    private String stationName;        // station to get info about, or to start journey from
    private String lineName;           // train line to get info about.
    private String destinationName;
    private int startTime = 0;         // time for enquiring about

    /**
     * main method:  load the data and set up the user interface
     */
    public static void main(String[] args){
        WellingtonTrains wel = new WellingtonTrains();
        wel.loadData();   // load all the data
        wel.setupGUI();   // set up the interface
    }

    /**
     * Load data files
     */
    public void loadData(){
        getStationData();

        loadStationData();
        UI.println("Loaded Stations");
//        loadTrainLineData();
        UI.println("Loaded Train Lines");
        // The following is only needed for the Completion and Challenge
//        loadTrainServicesData();
        UI.println("Loaded Train Services");
    }

    /**
     * User interface has buttons for the queries and text fields to enter stations and train line
     * You will need to implement the methods here.
     */
    public void setupGUI(){
        UI.addButton("All Stations",        this::listAllStations);
//        UI.addButton("Stations by name",    this::listStationsByName);
//        UI.addButton("All Lines",           this::listAllTrainLines);
        UI.addTextField("Station",          (String name) -> {this.stationName=name;});
        UI.addTextField("Train Line",       (String name) -> {this.lineName=name;});
        UI.addTextField("Destination",      (String name) -> {this.destinationName=name;});
        UI.addTextField("Time (24hr)",      (String time) ->
            {try{this.startTime=Integer.parseInt(time);}catch(Exception e){UI.println("Enter four digits");}});
//        UI.addButton("Lines of Station",    () -> {listLinesOfStation(this.stationName);});
//        UI.addButton("Stations on Line",    () -> {listStationsOnLine(this.lineName);});
//        UI.addButton("Stations connected?", () -> {checkConnected(this.stationName, this.destinationName);});
//        UI.addButton("Next Services",       () -> {findNextServices(this.stationName, this.startTime);});
//        UI.addButton("Find Trip",           () -> {findTrip(this.stationName, this.destinationName, this.startTime);});

        UI.addButton("Quit", UI::quit);
        UI.setMouseListener(this::doMouse);

        UI.setWindowSize(900, 400);
        UI.setDivider(0.2);
        // this is just to remind you to start the program using main!
//        if (allStations.isEmpty()){
//            UI.setFontSize(36);
//            UI.drawString("Start the program from main", 2, 36);
//            UI.drawString("in order to load the data", 2, 80);
//            UI.sleep(2000);
//            UI.quit();
//        }
//        else {
//            UI.drawImage("data/geographic-map.png", 0, 0);
//            UI.drawString("Click to list closest stations", 2, 12);
//        }
    }

    public void doMouse(String action, double x, double y){
        if (action.equals("released")){
            /*# YOUR CODE HERE */

        }
    }

    // Methods for loading data and answering queries

    //Loads the file using the arguments
    public Scanner loadFile(String message){
        Scanner scanner = null;
        try {
            String path = UIFileChooser.open(message);
            File file = new File(path);
            scanner = new Scanner(file);

        } catch(IOException e){UI.println("File reading failed");}

        return scanner;
    }

    public void getStationData(){
        Scanner sc = loadFile("Select the stations.data file");
        while(sc.hasNext()){
            String line = sc.nextLine();
            String[] splitLine = line.split(" ");
            ArrayList<Integer> toSave = new ArrayList<>();
            toSave.add(Integer.parseInt(splitLine[1]));
            toSave.add(Integer.parseInt(splitLine[2]));
            toSave.add(Integer.parseInt(splitLine[3]));

            stationData.put(splitLine[0], toSave);
        }
    }


    public void loadStationData(){
        Scanner sc = loadFile("Select a stations.data file");

        while(sc.hasNext()){
            String name = sc.nextLine();

            ArrayList<Integer> data = stationData.get(name);

            int zone = data.get(0);
            int x = data.get(1);
            int y = data.get(2);
            Station station = new Station(name, zone, x ,y);
            allStations.add(station);
        }
    }

    //Queries

    public void listAllStations(){
        System.out.println(allStations);
        for(int i = 0; i < allStations.size(); i++){
            UI.println(allStations.get(i));
        }
    }

}
