// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2021T2, Assignment 3
 * Name: Shae West
 * Username: westshae
 * ID: 300565911
 */

import ecs100.*;
import java.util.*;
import java.io.*;

/**
 * Simulation of a Hospital ER
 * 
 * The hospital has a collection of Departments, including the ER department, each of which has
 *  and a treatment room.
 * 
 * When patients arrive at the hospital, they are immediately assessed by the
 *  triage team who determine the priority of the patient and (unrealistically) a sequence of treatments 
 *  that the patient will need.
 *
 * The simulation should move patients through the departments for each of the required treatments,
 * finally discharging patients when they have completed their final treatment.
 *
 *  READ THE ASSIGNMENT PAGE!
 */

public class HospitalERCompl{

    // Fields for recording the patients waiting in the waiting room and being treated in the treatment room
    private static final int MAX_PATIENTS = 5;   // max number of patients currently being treated
    private HashMap<Integer, Department> departments = new HashMap<>();//Creates a hashmap to contain all different waiting rooms, accessed by name of department


    // fields for the statistics
    private int treated = 0;
    private ArrayList<Integer> waitTimes = new ArrayList<>();

    // Fields for the simulation
    private boolean priority = false;
    private boolean running = false;
    private int time = 0; // The simulated time - the current "tick"
    private int delay = 300;  // milliseconds of real time for each tick

    // fields controlling the probabilities.
    private int arrivalInterval = 5;   // new patient every 5 ticks, on average
    private double probPri1 = 0.1; // 10% priority 1 patients
    private double probPri2 = 0.2; // 20% priority 2 patients
    private Random random = new Random();  // The random number generator.

    /**
     * Construct a new HospitalERCore object, setting up the GUI, and resetting
     */
    public static void main(String[] arguments){
        HospitalERCompl er = new HospitalERCompl();
        er.setupGUI();
        er.reset(true);   // initialise with an priority queue.
    }

    /**
     * Set up the GUI: buttons to control simulation and sliders for setting parameters
     */
    public void setupGUI(){
        UI.addButton("Reset (Queue)", () -> {this.reset(false); });
        UI.addButton("Reset (Pri Queue)", () -> {this.reset(true);});
        UI.addButton("Start", ()->{if (!running){ run(); }});   //don't start if already running!
        UI.addButton("Pause & Report", ()->{running=false;});
        UI.addSlider("Speed", 1, 400, (401-delay),
                (double val)-> {delay = (int)(401-val);});
        UI.addSlider("Av arrival interval", 1, 50, arrivalInterval,
                (double val)-> {arrivalInterval = (int)val;});
        UI.addSlider("Prob of Pri 1", 1, 100, probPri1*100,
                (double val)-> {probPri1 = val/100;});
        UI.addSlider("Prob of Pri 2", 1, 100, probPri2*100,
                (double val)-> {probPri2 = Math.min(val/100,1-probPri1);});
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(1000,600);
        UI.setDivider(0.5);
    }

    /**
     * Reset the simulation:
     *  stop any running simulation,
     *  reset the waiting and treatment rooms
     *  reset the statistics.
     */
    public void reset(boolean usePriorityQueue){

        running=false;
        UI.sleep(2*delay);  // to make sure that any running simulation has stopped
        time = 0;           // set the "tick" to zero.

        // reset the waiting room, the treatment room, and the statistics.
        /*# YOUR CODE HERE */
        //Reinitializes the hashmaps
        departments = new HashMap<>();

        //Initializes departments and puts them into the hashmap of all departments
        departments.put(1,new Department("er", 5, false));
        departments.put(2,new Department("xray", 2, false));
        departments.put(3,new Department("surgery", 2, false));
        departments.put(4,new Department("ultrasound", 2, false));
        departments.put(5,new Department("mri", 2, false));

        treated = 0;//Resets treated number
        waitTimes = new ArrayList<Integer>(); // Reinitializes array of wait times

        priority = usePriorityQueue; // Determines if the program should use priority queue systems

        UI.clearGraphics();
        UI.clearText();
    }

    /**
     * Main loop of the simulation
     */
    public void run(){
        if (running) { return; } // don't start simulation if already running one!
        reset(priority);
        running = true;
        while (running){         // each time step, check whether the simulation should pause.
            time++;//Increases tick by one

            patientArrival(departments);// Get any new patient that has arrived and add them to the waiting room
            patientTreatmentCheck();//Checks patient's treatments for if they are completed
            patientTicks();//Iterates through all patients in waiting room and adds a tick
            movePatientsFromWaitingToTreatment();//Moves patients from department waiting rooms to treatment rooms

            redraw(departments);
            UI.sleep(delay);
        }
        // paused, so report current statistics
        reportStatistics();
    }

    // Additional methods used by run() (You can define more of your own)

    //Function which adds a patient to the ER waiting room if time is = 1 or based on arrivalInterval
    public void patientArrival(HashMap<Integer, Department> hashmap){
        if (time==1 || Math.random()<1.0/arrivalInterval){
            Patient newPatient = new Patient(time, randomPriority());//Creates patient at current time with random priority
            Department department = hashmap.get(1);//Gets the ER department from the hashmap

            Queue<Patient> waitingRoom = department.getWaitingRoom();//Gets the waiting room queue
            waitingRoom.offer(newPatient);//Adds the patient to the waiting room
            department.setWaitingRoom(waitingRoom);//Sets the waiting room to the updated room.
        }
    }

    //Moves patients from department waiting rooms to treatment rooms
    public void movePatientsFromWaitingToTreatment(){
        for(var entry : departments.entrySet()){
            Department department = entry.getValue();
            if(department.getTreatmentRoom().size() < department.getMaxPatients()){
                if(!priority){//If the priority queue isn't being used
                    //Gets the first user in the waiting room and adds them to the treatment room
                    Patient patient = department.getWaitingRoom().poll();
                    department.getTreatmentRoom().add(patient);
                }
                else{//If the priority queue is being used
                    Patient current = null;
                    Queue<Patient> waitingRoom = department.getWaitingRoom();
                    for(Patient patient : waitingRoom){//For each patient in the waiting room
                        if(current == null){//If currentPatient doesn't exist, make first patient current highest priority
                            current = patient;
                        }else{//If currentPatient does exist, compares it to current iteration of patient
                            if(current.compareTo(patient) == 1){//If currentPriority patient is less then current patient
                                current = patient;//Change current highest priority patient to current iteration of patient
                            }
                        }
                    }
                    if(current != null){//Ensure the current patient has been initialized
                        department.getWaitingRoom().remove(current);//Removes highest priority patient from waiting room
                        department.getTreatmentRoom().add(current);//Adds the highest priority patient to the treatment room
                    }
                }
            }
        }
    }

    //Checks all patients for if their treatment is completed
    //If the treatment is completed, moves them to "cured" or to their next treatment
    //Else advances treatment by a tick
    public void patientTreatmentCheck(){
        for(var entry : departments.entrySet()){//Iterates through the treatmentRooms entries
            Department department = entry.getValue();//Gets current department
            HashSet<Patient> toRemove = new HashSet<>();

            for(Patient patient : department.getTreatmentRoom()){//Iterates through each department's treatment room's patients
                if(patient == null){return;}

                else if(patient.noMoreTreatments()){//If patient completes all treatments, removes from treatment rooms
                    toRemove.add(patient);
                    continue;
                }

                else if(patient.completedCurrentTreatment()){//If the patient has finished their treatment
                    if(!patient.noMoreTreatments()) {//If patient has more treatments, gets their next department and adds them to it
                        Department nextDepartment = departments.get(getDepartmentFromString(patient.getCurrentTreatment()));

                        toRemove.add(patient);
                        nextDepartment.getWaitingRoom().offer(patient);
                    }else{//If patient has more treatments, increment's their current treatment number
                        patient.incrementTreatmentNumber();
                    }
                }
                else{//If patient hasn't finished their treatments, advance treatment tick
                    patient.advanceTreatmentByTick();
                }
            }
            for(Patient patient : toRemove){//Removes all "to be removed" patients
                department.getTreatmentRoom().remove(patient);
            }
        }
    }

    //Takes a string of the current department and returns the integer of it.
    public Integer getDepartmentFromString(String string){
        switch (string.toLowerCase(Locale.ROOT)){//lower-case version of department name
            case "er":
                return 1;
            case "x-ray":
                return 2;
            case "surgery":
                return 3;
            case "ultrasound":
                return 4;
            case "mri":
                return 5;

        }
        return null;
    }

    //Adds "waiting" ticks for all patients in waiting rooms
    public void patientTicks(){
        for(var entry : departments.entrySet()){//Iterates through the waitingRooms entries
            Department department = entry.getValue();//Gets current department
            for(Patient patient : department.getWaitingRoom()){//Iterates through each department's treatment room's patients
                patient.waitForATick();
            }
        }
    }

    /**
     * Report summary statistics about all the patients that have been discharged.
     * (Doesn't include information about the patients currently waiting or being treated)
     * The run method should have been recording various statistics during the simulation.
     */
    public void reportStatistics(){
        UI.println("There have been " + treated + " treated patients.");
        int total = 0;
        for(Integer integer : waitTimes){//Adds each int in waitTimes set into total
            total += integer;
        }
        if(total != 0 && treated != 0) {//Only print average wait time if it doesn't equal 0.
            UI.println(total / treated);
        }
    }


    // HELPER METHODS FOR THE SIMULATION AND VISUALISATION
    /**
     * Redraws all the departments
     */
    public void redraw(HashMap<Integer, Department> hashmap){
        UI.clearGraphics();
        UI.setFontSize(14);
        UI.drawString("Treating Patients", 5, 15);
        UI.drawString("Waiting Queues", 200, 15);
        UI.drawLine(0,32,400, 32);

        double defaultY = 80;
        double changeY = 80;
        int count = 0;

        for(var entry : hashmap.entrySet()){//Draws all department data to screen UI.
            Department department = entry.getValue();
            department.redraw(defaultY + (changeY*count));
            count++;
        }
    }

    /**
     * Returns a random priority 1 - 3
     * Probability of a priority 1 patient should be probPri1
     * Probability of a priority 2 patient should be probPri2
     * Probability of a priority 3 patient should be (1-probPri1-probPri2)
     */
    private int randomPriority(){
        double rnd = random.nextDouble();
        if (rnd < probPri1) {return 1;}
        if (rnd < (probPri1 + probPri2) ) {return 2;}
        return 3;
    }

}
