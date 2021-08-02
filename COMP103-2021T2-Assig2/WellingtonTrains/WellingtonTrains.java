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
        UI.addButton("Lines of Station",    () -> {listLinesOfStation(this.stationName);});
        UI.addButton("Stations on Line",    () -> {listStationsOnLine(this.lineName);});
        UI.addButton("Stations connected?", () -> {checkConnected(this.stationName, this.destinationName);});
        UI.addButton("Next Services",       () -> {findNextServices(this.stationName, this.startTime);});
        UI.addButton("Find Trip",           () -> {findTrip(this.stationName, this.destinationName, this.startTime);});

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

    //Loads the station data
    public void loadStationData() {
        //Resets the currents station hashmap and initializes scanner to null
        allStations = new HashMap<>();
        Scanner scanner = null;

        //Tries to load a file then scanner for the stations.data file
        try {
            scanner = new Scanner(new File("data/stations.data"));
        } catch (FileNotFoundException e) {
            e.fillInStackTrace();
        }

        //Iterates through the file, saving the name as it is used more than once and saves it to a station.
        while (scanner.hasNext()) {
            String name = scanner.next();
            allStations.put(name, new Station(name, scanner.nextInt(), scanner.nextInt(), scanner.nextInt()));
        }
    }

    //Saves the line data
    public void loadTrainLineData() {
        //Resets the current lines hashmap and initializes scanner to null
        allLines = new HashMap<>();
        Scanner dataScanner = null;

        //Tries to load a file then scanner for the train-line.data file
        try {
            dataScanner = new Scanner(new File("data/train-lines.data"));
        } catch (FileNotFoundException e) {
            e.fillInStackTrace();
        }

        //Iterates through the train-line.data file, which is a list of the lines
        while (dataScanner.hasNext()) {
            //Saves the linename, creates a line using that name, then initializes the scanner to null
            String lineName = dataScanner.next();
            TrainLine line = new TrainLine(lineName);
            Scanner stationScanner = null;

            //Tries to load a file then scanner for the current line.
            try {
                stationScanner = new Scanner(new File("data/" + lineName + "-stations.data"));
            } catch (FileNotFoundException e) {
                e.fillInStackTrace();
            }

            //Iterates through the current line stations file.
            while (stationScanner.hasNext()) {
                //Saves the station name and uses it to get the station from all stations
                String stationName = stationScanner.next();
                Station station = allStations.get(stationName);

                //Adds the station to the line created in the previous scope and adds the station to the line, then saves to the the allStations hashmap
                line.addStation(station);
                station.addTrainLine(line);

                allStations.put(stationName, station);
            }
            //Saves the line to the allLines hashmap
            allLines.put(lineName, line);
        }
    }

    public void loadTrainServicesData() {
        //Initializes the scanner to null;
        Scanner dataScanner = null;

        //Attempts to the load the train-lines.data file
        try {
            dataScanner = new Scanner(new File("data/train-lines.data"));
        } catch (FileNotFoundException e) {
            e.fillInStackTrace();
        }

        //Iterates through the train-lines.data file
        while (dataScanner.hasNext()) {
            //Saves the name of the line, uses the name to get the line from allLines, then initializes a list for the services.
            String name = dataScanner.next();
            TrainLine line = allLines.get(name);
            List<String> servicesList = null;
            Scanner scanner = null;

            //Attempts to load a scanner from the line-services file
            try {
                scanner = new Scanner(new File("data/" + name + "-services.data"));
            } catch (FileNotFoundException e) {
                e.fillInStackTrace();
            }

            //Iterates through the lines of the line-services file
            while (scanner.hasNext()) {
                //Splits the line into a list of strings, then creates a trainService object using the line in the previous scope
                String[] currentLine = scanner.nextLine().split(" ");
                TrainService trainService = new TrainService(line);

                //Loops through the list, adding the times to the services, then adds the service to the line
                for (int i = 0; i < currentLine.length; i++) {
                    trainService.addTime(Integer.parseInt(currentLine[i]));
                }

                line.addTrainService(trainService);
            }
            //Saves the line to the hashmap
            allLines.put(lineName, line);
        }
    }





    //Queries

    public void listAllStations() {
        UI.println("");
        //Iterates through the hashmap and prints the values
        allStations.forEach((key, value) -> {
            UI.println(value);
        });
    }

    public void listStationsByName() {
        //Saves the allStations hashmap to a list of keys, then sorts those keys by alphabetical order
        List<String> newStations = new ArrayList(allStations.keySet());
        Collections.sort(newStations);
        UI.println("");

        //Iterates through the sorted list and uses the sorted keys to get the values
        for (int i = 0; i < newStations.size(); i++) {
            UI.println(allStations.get(newStations.get(i)));
        }
    }

    public void listAllTrainLines() {
        UI.println("");
        //Iterates through the hashmap and prints all the values
        allLines.forEach((key, value) -> {
            UI.println(value);
        });
    }

    public void listLinesOfStation(String stationName) {
        UI.println("");
        //Initializes the stations
        Station station;

        //Attempts to get the station from the hashmap, if not returns an error message and returns
        try {
            station = allStations.get(stationName);
        } catch (NullPointerException e) {
            UI.println("Station not found");
            return;
        }

        //Gets the set of trainLines from the station, then iterates through it and prints the line
        Set<TrainLine> trainLines = station.getTrainLines();
        trainLines.forEach((line) -> {
            UI.println(line);
        });
    }

    public void listStationsOnLine(String lineName) {
        UI.println("");
        //Initializes line
        TrainLine line = null;

        //Attempts to get the line from the hashmap, if not returns an error message and returns;
        try {
            line = allLines.get(lineName);
        } catch (NullPointerException e) {
            e.fillInStackTrace();
        }

        //Saves the stations of the line ot a list, then iterates through the list printing the location
        List<Station> trainStations = line.getStations();
        trainStations.forEach((station) -> {
            UI.println(station);
        });
    }

    public void checkConnected(String stationName, String destinationName) {
        UI.println("");

        //Initializes of objects
        Station origin;
        Station destination;

        //Attempts to get both stations from the hashmap, if not returns an error

        try {
            origin = allStations.get(stationName);
        } catch (NullPointerException e) {
            UI.println("Origin station not found");
            return;
        }

        try {
            destination = allStations.get(destinationName);
        } catch (NullPointerException e) {
            UI.println("Destination station not found");
            return;
        }

        boolean toLoop = true;
        for (TrainLine trainLine : origin.getTrainLines()) {//Loops through the origin's trainLines
            if (!toLoop) {
                break;
            }
            for (Station station : trainLine.getStations()) {//Loops through the trainline's stations
                if (!toLoop) {
                    break;
                }
                if (station.getName() == destination.getName()) {
                    for (TrainLine line : station.getTrainLines()) {// goes through all the lines through that station
                        if (!toLoop) {
                            break;
                        }
                        //Checks if the ends with the destination, then prints the final line, else it states there is no connection
                        if (line.getName().split("_")[1].equals(destinationName)) {
                            UI.println(line.getName());
                            toLoop = false;
                        } else {
                            UI.println("There is no connection between these two.");
                        }
                    }
                }
            }
        }
    }

    public void findNextServices(String stationName, int startTime) {
        UI.println("");


        for (TrainLine trainLine : allStations.get(stationName).getTrainLines()) { //Iterates through all the trainlines that pass through the station
            for (TrainService trainService : trainLine.getTrainServices()) { //Iterates through all the services for the line
                //Saves the times to a list, checks what position the station in the trainline list
                List<Integer> times = trainService.getTimes();
                int pos = trainLine.getStations().indexOf(allStations.get(stationName));

                //Checks if the time of the station is greater than the time specified
                if (times.get(pos) > startTime) {
                    UI.println(trainService);
                    break;
                }
            }
        }
    }

    public void findTrip(String originName, String destinationName, int startTime) {
        UI.println("");

        Station origin;
        Station destination;

        try {
            origin = allStations.get(originName);
        } catch (NullPointerException e) {
            UI.println("Origin station not found");
            return;
        }

        try {
            destination = allStations.get(destinationName);
        } catch (NullPointerException e) {
            UI.println("Destination station not found");
            return;
        }

        ArrayList<TrainLine> toSave = new ArrayList<>();

        boolean noMatch = true;
        for (TrainLine line : origin.getTrainLines()) {
            for (Station station : line.getStations()) {
                if (station == destination) {
                    toSave.add(line);
                    noMatch = false;
                }
            }
        }
        if (noMatch) {
            UI.println("These two stations aren't on the same line");
        }

        for(TrainLine line : toSave) {
            for (TrainService trainService : line.getTrainServices()) {
                List<Integer> times = trainService.getTimes();
                int originPos = line.getStations().indexOf(origin);
                int destPos = line.getStations().indexOf(destination);

                if (times.get(originPos) == -1 && times.get(destPos) == -1) {
                    continue;
                }

                if (times.get(originPos) > startTime && times.get(destPos) > startTime) {
                    if (originPos < destPos) {
                        int originZone = origin.getZone();
                        int destZone = destination.getZone();
                        int zones;
                        double fare = 0;
                        if (originZone > destZone) {
                            zones = originZone - destZone;
                        } else {
                            zones = destZone - originZone;
                        }

                        Scanner scanner = null;

                        try {
                            scanner = new Scanner(new File("data/fares.data"));
                        } catch (FileNotFoundException e) {
                            e.fillInStackTrace();
                        }

                        while (scanner.hasNext()) {
                            String[] splitLine = scanner.nextLine().split(" ");
                            if (splitLine[0].equals(Integer.toString(zones))) {
                                fare = Double.parseDouble(splitLine[1]);
                                break;
                            }
                        }
                        UI.println(toSave);
                        UI.println("Origin depart time: " + times.get(originPos));
                        UI.println("Destination arrival time: " + times.get(destPos));
                        UI.println("Fare: " + fare);
                        break;
                    }
                }
            }
        }
    }
}
