// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2021T2, Assignment 3
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.util.*;

/**
 * A treatment Department (Surgery, X-ray room,  ER, Ultrasound, etc)
 * Each department will need
 * - A name,
 * - A maximum number of patients that can be treated at the same time
 * - A Set of Patients that are currently being treated
 * - A Queue of Patients waiting to be treated.
 *    (ordinary queue, or priority queue, depending on argument to constructor)
 */

public class Department{

    private String name;
    private int maxPatients;
    private boolean queueType; // False == normal queue, True == priority queue
    private Queue<Patient> waitingRoom;
    private HashSet<Patient> treatmentRoom;

    /*# YOUR CODE HERE */

    //Constructor
    public Department(String name, Integer maxPatients, boolean queueType){
        this.name = name;
        this.maxPatients = maxPatients;
        this.queueType = queueType;
        this.waitingRoom = new ArrayDeque<>();
        this.treatmentRoom = new HashSet<>();
    }

    //Getters
    public String getName(){ return this.name; }
    public Integer getMaxPatients(){ return this.maxPatients; }
    public Boolean getQueueType(){ return this.queueType; }
    public Queue<Patient> getWaitingRoom(){ return this.waitingRoom; }
    public HashSet<Patient> getTreatmentRoom(){ return this.treatmentRoom; }
    public Patient getFirstWaiting(){ return this.waitingRoom.poll(); }//Returns the first patient in the queue


    public void setQueueType(boolean queueType){ this.queueType = queueType; }//Sets the type of queue, priority or regular
    public void offerWaitingRoom(Patient patient){ this.waitingRoom.offer(patient); } // Adds a patient to the end of the queue

    public void moveToTreatment(Patient patient){//Moves the patient from the waiting room to the treatment room
        this.waitingRoom.remove(patient);
        this.treatmentRoom.add(patient);
    }

    public void moveToNewDepartment(Department department, Patient patient){//Removes the patient from the current department and moves to the new department's waiting room
        this.treatmentRoom.remove(patient);
        department.offerWaitingRoom(patient);
    }

    /**
     * Draw the department: the patients being treated and the patients waiting
     * You may need to change the names if your fields had different names
     */
    public void redraw(double y){
        UI.setFontSize(14);
        UI.drawString(name, 0, y-35);
        double x = 10;
        UI.drawRect(x-5, y-30, maxPatients*10, 30);  // box to show max number of patients
        for(Patient p : treatmentRoom){
            p.redraw(x, y);
            x += 10;
        }
        x = 200;
        for(Patient p : waitingRoom){
            p.redraw(x, y);
            x += 10;
        }
    }

}
