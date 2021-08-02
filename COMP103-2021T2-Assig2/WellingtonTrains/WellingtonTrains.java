// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2021T2, Assignment 2
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;

import javax.sound.midi.SysexMessage;
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
    private HashMap<String, Station> allStations = new HashMap<>();
    private HashMap<String, TrainLine> allLines = new HashMap<>();
//    private HashMap<String, TrainService> allServices = new HashMap<>();

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
        loadStationData();
        UI.println("Loaded Stations");
        loadTrainLineData();
        UI.println("Loaded Train Lines");
        loadTrainServicesData();
        UI.println("Loaded Train Services");
    }

    /**
     * User interface has buttons for the queries and text fields to enter stations and train line
     * You will need to implement the methods here.
     */
    public void setupGUI(){
        UI.addButton("All Stations",        this::listAllStations);
        UI.addButton("Stations by name",    this::listStationsByName);
        UI.addButton("All Lines",           this::listAllTrainLines);
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

        UI.setWindowSize(1200, 800);
        UI.setDivider(0.2);
        // this is just to remind you to start the program using main!
        if (allStations.isEmpty()){
            UI.setFontSize(36);
            UI.drawString("Start the program from main", 2, 36);
            UI.drawString("in order to load the data", 2, 80);
            UI.sleep(2000);
            UI.quit();
        }
        else {
            UI.drawImage("data/geographic-map.png", 0, 0);
            UI.drawString("Click to list closest stations", 2, 12);
        }
    }

    public void doMouse(String action, double x, double y){
        if (action.equals("released")){
            /*# YOUR CODE HERE */

        }
    }

    // Methods for loading data and answering queries

    public void loadStationData(){
        allStations = new HashMap<>();
        Scanner scanner = null;
        try{ scanner = new Scanner(new File("data/stations.data"));}
        catch (FileNotFoundException e){e.fillInStackTrace();}

        while (scanner.hasNext()) {
            String name = scanner.next();
            allStations.put(name, new Station(name, scanner.nextInt(), scanner.nextInt(), scanner.nextInt()));
        }
    }

    public void loadTrainLineData(){
        allLines = new HashMap<>();
        Scanner dataScanner = null;

        try{dataScanner = new Scanner(new File("data/train-line.data"));}
        catch (FileNotFoundException e){e.fillInStackTrace();}


        while(dataScanner.hasNext()){
            String lineName = dataScanner.next();
            TrainLine line = new TrainLine(lineName);
            Scanner stationScanner = null;

            try{ stationScanner = new Scanner(new File("data/" + lineName + "-stations.data"));}
            catch (FileNotFoundException e){e.fillInStackTrace();}

            while(stationScanner.hasNext()){
                String stationName = stationScanner.next();
                Station station = allStations.get(stationName);

                line.addStation(station);
                station.addTrainLine(line);

                allStations.put(stationName, station);
            }
            allLines.put(lineName, line);
        }
    }

    public void loadTrainServicesData(){
        Scanner dataScanner = null;
        try{ dataScanner = new Scanner(new File("data/train-lines.data"));}
        catch(FileNotFoundException e){e.fillInStackTrace();}

        while(dataScanner.hasNext()) {
            String name = dataScanner.next();
            TrainLine line = allLines.get(name);
            List<String> servicesList = null;

            try { servicesList = Files.readAllLines(Path.of("data/" + name + "-services.data")); }
            catch (IOException e) { e.fillInStackTrace(); }

            for (int i = 0; i < servicesList.size(); i++) {
                Scanner servicesScanner = new Scanner(servicesList.get(i));
                TrainService trainService = new TrainService(line);

                while (servicesScanner.hasNext()) { trainService.addTime(servicesScanner.nextInt()); }

                line.addTrainService(trainService);
            }
            allLines.put(lineName, line);
        }
    }





    //Queries

    public void listAllStations(){
        UI.println("");
        allStations.forEach((key, value) ->{
            UI.println(key + ": " + value);
        });
    }

    public void listStationsByName(){
        List<String> newStations = new ArrayList(allStations.keySet());
        Collections.sort(newStations);
        UI.println("");

        for(int i = 0; i < newStations.size(); i++){
            UI.println(allStations.get(newStations.get(i)));
        }
    }

    public void listAllTrainLines(){
        UI.println("");
        allLines.forEach((key, value)->{
            UI.println(key + "::" + value);
        });
    }

    public void listLinesOfStation(String stationName){
        UI.println("");
        UI.println(stationName);
    }
}
