import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.Random;

/**
 * Main class to simulate the interaction between patients, receptionists,
 * nurses, and doctors in a doctor's office
 */
public class Project2 {
    // Semaphores to control access to critical sections
    public static Semaphore receptionist = new Semaphore(0, true);
    public static Semaphore reception = new Semaphore(1, true);
    public static Semaphore register = new Semaphore(0, true);
    public static Semaphore patientRdy = new Semaphore(0, true);

    // Arrays of semaphores and other variables for managing threads
    public static Semaphore nurse[];
    public static Semaphore yesNo[];
    public static Semaphore enterOff[];
    public static Semaphore doctor[];
    public static Semaphore advice[];
    public static Semaphore adviceRec[];

    // Number of doctor threads and assigned doctors for patients
    public static int docThreads;
    public static int assignedDoc[];

    // Data structures to manage patient assignment and doctor-patient interactions
    public static HashMap<Integer, LinkedList<Integer>> docBuffer = new HashMap<>();
    public static Queue<Integer> receptionBuffer = new LinkedList<>();

    /**
     * Main method to start the simulation.
     *
     * @param args Command-line arguments: number of doctors and number of patients
     */
    public static void main(String[] args) {
        // Validate and parse command line arguments
        if (args.length < 2) {
            System.out.println("Usage: java Project2 <number_of_doctors> <number_of_patients>");
            return;
        }

        docThreads = Integer.parseInt(args[0]);
        int patThreads = Integer.parseInt(args[1]);

        // Validate input parameters
        if (docThreads <= 0 || docThreads > 3 || patThreads <= 0 || patThreads > 15) {
            System.out.println(
                    "Invalid input! Ensure the number of doctors (1-3) and patients (1-15) are within the valid range.");
            return;
        }

        System.out.println("Run with " + patThreads + " patients, " + docThreads + " nurses, " + docThreads + " doctors\n");
        // Initialize semaphores and data structures
        initializeSemaphores(patThreads);

        // Start receptionist thread
        Thread reception = new Thread(new Receptionist());
        reception.start();

        // Start nurse and doctor threads
        
        startDoctorThreads();
        startNurseThreads();
        startPatientThreads(patThreads); 
    }

    /**
     * Initialize all semaphores and data structures required for synchronization.
     *
     * @param patThreads Number of patient threads
     */
    private static void initializeSemaphores(int patThreads) {
        nurse = new Semaphore[docThreads];
        enterOff = new Semaphore[docThreads];
        doctor = new Semaphore[docThreads];
        advice = new Semaphore[docThreads];
        adviceRec = new Semaphore[docThreads];
        yesNo = new Semaphore[docThreads];

        // Initialize arrays of semaphores
        for (int i = 0; i < docThreads; i++) {
            nurse[i] = new Semaphore(0, true); // Nurse semaphore
            enterOff[i] = new Semaphore(0, true); // Semaphore for patient entering doctor's office
            doctor[i] = new Semaphore(0, true); // Doctor semaphore
            advice[i] = new Semaphore(0, true); // Semaphore for doctor giving advice
            adviceRec[i] = new Semaphore(0, true); // Semaphore for patient receiving advice
            yesNo[i] = new Semaphore(1, true); // Mutex semaphore
            docBuffer.put(i, new LinkedList<>()); // Initialize patient queue for each doctor
        }

        // Initialize array for assigned doctors for patients
        assignedDoc = new int[patThreads]; 
    }

    /**
     * Start nurse threads.
     */
    private static void startNurseThreads() {
        Thread nurse_threads[] = new Thread[docThreads];
        Nurse nurses[] = new Nurse[docThreads];
        for (int i = 0; i < docThreads; i++) {

            // Create nurse object
            nurses[i] = new Nurse(i); 

            // Create nurse thread
            nurse_threads[i] = new Thread(nurses[i]);

            // Start nurse thread
            nurse_threads[i].start(); 
        }
    }

    /**
     * Start doctor threads.
     */
    private static void startDoctorThreads() {
        Thread doctor_threads[] = new Thread[docThreads];
        Doctor doctors[] = new Doctor[docThreads];
        for (int i = 0; i < docThreads; i++) {

            // Create doctor object
            doctors[i] = new Doctor(i); 

            // Create doctor thread
            doctor_threads[i] = new Thread(doctors[i]); 

            // Start doctor thread
            doctor_threads[i].start(); 
        }
    }

    /**
     * Start patient threads.
     *
     * @param patThreads Number of patient threads
     */
    private static void startPatientThreads(int patThreads) {
        Thread patient_threads[] = new Thread[patThreads];
        Patient patients[] = new Patient[patThreads];
        for (int i = 0; i < patThreads; i++) {

            // Create patient object
            patients[i] = new Patient(i); 

            // Create patient thread
            patient_threads[i] = new Thread(patients[i]); 

             // Start patient thread
            patient_threads[i].start();
        }
        // Wait for patient threads to complete
        for (int i = 0; i < patThreads; i++) {
            try {
                patient_threads[i].join();
            } catch (InterruptedException e) {
                System.out.println("Error");
            }
        }

        
        System.out.println("Simulation complete");
        System.exit(0); // Exit the program
    }
}

/**
 * The Receptionist class represents a receptionist entity in the simulation.
 * It implements the Runnable interface to be executed as a thread.
 */
class Receptionist implements Runnable {
    private Random rand = new Random(); // Random number generator for assigning patients to doctors

    /**
     * Implements the behavior of the receptionist thread.
     * The receptionist waits for a patient to enter, assigns the patient to a
     * doctor,
     * adds the patient to the doctor's buffer, registers the patient, waits for the
     * patient to leave,
     * and signals the nurse that the patient is ready.
     */
    public void run() {
        while (true) {
            waitForPatientToEnter(); // Wait for patient to enter
            int patientID = getPatientFromBuffer(); // Get patient ID from buffer
            int doctorID = assignPatientToDoctor(); // Assign patient to a doctor
            addPatientToDoctorBuffer(patientID, doctorID); // Add patient to doctor's buffer
            assignDoctorInformation(patientID, doctorID); // Assign doctor information to patient
            registerPatient(patientID); // Register patient
            waitForPatientToLeave(); // Wait for patient to leave
            signalNurseThatPatientIsReady(doctorID); // Signal nurse that patient is ready
        }
    }

    // Waits for a patient to enter by acquiring the receptionist semaphore. If
    // interrupted, interrupts the thread.
    private void waitForPatientToEnter() {
        try {

            // Acquire receptionist semaphore
            Project2.receptionist.acquire();

        } catch (InterruptedException e) {
            // Interrupt the thread if interrupted
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Retrieves a patient ID from the reception buffer and removes it.
     * 
     * @return The ID of the patient
     */
    private int getPatientFromBuffer() {
        // Remove and return patient ID from reception buffer
        return Project2.receptionBuffer.poll();
    }

    /**
     * Assigns a patient to a doctor randomly.
     * If there are no doctors, assigns to a non-existent doctor (0).
     * 
     * @return The ID of the assigned doctor
     */
    private int assignPatientToDoctor() {
        // Randomly assign patient to a doctor
        return rand.nextInt(Project2.docThreads > 0 ? Project2.docThreads : 1);
    }

    /**
     * Adds the patient to the doctor's buffer.
     * 
     * @param patientID The ID of the patient
     * @param doctorID  The ID of the assigned doctor
     */
    private void addPatientToDoctorBuffer(int patientID, int doctorID) {

        // Add patient to doctor's buffer
        Project2.docBuffer.get(doctorID).add(patientID);
    }

    /**
     * Assigns doctor information to the patient.
     * 
     * @param patientID The ID of the patient
     * @param doctorID  The ID of the assigned doctor
     */
    private void assignDoctorInformation(int patientID, int doctorID) {

        // Assign doctor ID to the patient
        Project2.assignedDoc[patientID] = doctorID;
    }

    /**
     * Registers the patient and releases the register semaphore.
     * 
     * @param patientID The ID of the patient
     */
    private void registerPatient(int patientID) {

        // Print registration message
        System.out.println("Receptionist registers patient " + patientID);

        // Release register semaphore
        Project2.register.release();
    }

    // Waits for the patient to leave by acquiring the patient ready semaphore. If
    // interrupted, interrupts the thread.
    private void waitForPatientToLeave() {
        try {

            // Acquire patient ready semaphore
            Project2.patientRdy.acquire();

        } catch (InterruptedException e) {

            // Interrupt the thread if interrupted
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Signals the nurse that the patient is ready by releasing the nurse semaphore
     * for the assigned doctor.
     * 
     * @param doctorID The ID of the assigned doctor
     */
    private void signalNurseThatPatientIsReady(int doctorID) {

        // Release nurse semaphore for the assigned doctor
        Project2.nurse[doctorID].release();
    }
}

/**
 * The Patient class represents a patient entity in the simulation.
 * It implements the Runnable interface to be executed as a thread.
 */
class Patient implements Runnable {

    // The unique identifier of the patient thread
    private int threadID;

    /**
     * Constructor to initialize a Patient object with a specific threadID.
     * 
     * @param threadID The unique identifier of the patient thread
     */
    public Patient(int threadID) {
        this.threadID = threadID;
    }

    /**
     * Returns the threadID of the patient.
     * 
     * @return The threadID of the patient
     */
    public int getThreadID() {
        return threadID;
    }

    /**
     * Implements the behavior of the patient thread.
     * The patient enters the waiting room, registers with the receptionist,
     * sits in the waiting room, enters the doctor's office, receives advice from
     * the doctor,
     * leaves the office, and signals that the patient is done.
     */
    @Override
    public void run() {
        enterWaitingRoom(); // Enter waiting room
        registerWithReceptionist(); // Register with receptionist
        sitInWaitingRoom(); // Sit in waiting room
        enterDoctorOffice(); // Enter doctor's office
        receiveAdvice(); // Receive advice from doctor
        leave(); // Leave
        signalPatientDone(); // Signal patient is done
    }

    // Enters the waiting room and prints a message indicating the action.
    private void enterWaitingRoom() {
        System.out.println("Patient " + threadID + " enters waiting room, waits for receptionist");
    }

    // Registers with the receptionist by adding the patient's threadID to the
    // reception buffer and releasing the receptionist semaphore. It then waits to
    // be registered by acquiring the register semaphore.
    private void registerWithReceptionist() {
        try {

            // Acquire mutex reception semaphore to ensure exclusive access to reception
            // buffer
            Project2.reception.acquire();

        } catch (InterruptedException ignored) {
            // Exception ignored
        }

        // Add patient's threadID to reception buffer
        Project2.receptionBuffer.add(threadID);

        // Release receptionist semaphore to indicate a patient is ready for
        // registration
        Project2.receptionist.release();
        try {

            // Wait to be registered
            Project2.register.acquire();

        } catch (InterruptedException ignored) {
            // Exception ignored
        }
    }

    // Leaves the receptionist and sits in the waiting room. Prints a message
    // indicating the action and releases the patient ready semaphore and mutex
    // reception semaphore.
    private void sitInWaitingRoom() {
        System.out.println("Patient " + threadID + " leaves receptionist and sits in waiting room");

        // Release patient ready semaphore to indicate patient is ready to enter
        // doctor's office
        Project2.patientRdy.release();

        // Release mutex reception semaphore
        Project2.reception.release();
    }

    // Enters the doctor's office by acquiring the enter office semaphore for the
    // assigned doctor. Prints a message indicating the action.
    private void enterDoctorOffice() {
        // Get the ID of the assigned doctor
        int docID = Project2.assignedDoc[threadID];
        try {

            // Acquire enter office semaphore for the assigned doctor
            Project2.enterOff[docID].acquire();

        } catch (InterruptedException ignored) {
            // Exception ignored
        }
        System.out.println("Patient " + threadID + " enters doctor " + docID + "'s office");
    }

    // Receives advice from the doctor by acquiring the advice semaphore for the
    // assigned doctor. Prints a message indicating the action.
    private void receiveAdvice() {

        // Get the ID of the assigned doctor
        int docID = Project2.assignedDoc[threadID];

        try {

            // Acquire advice semaphore for the assigned doctor
            Project2.advice[docID].acquire();

        } catch (InterruptedException ignored) {
            // Exception ignored
        }
        System.out.println("Patient " + threadID + " receives advice from doctor " + docID);
    }

    // Leaves the doctor's office by printing a message indicating the action.
    private void leave() {
        System.out.println("Patient " + threadID + " leaves");
    }

    // Signals that the patient is done by releasing the advice received semaphore
    // for the assigned doctor.
    private void signalPatientDone() {

        // Get the ID of the assigned doctor
        int docID = Project2.assignedDoc[threadID];

        // Release advice received semaphore for the assigned doctor
        Project2.adviceRec[docID].release();
    }
}

/**
 * The Nurse class represents a nurse entity in the simulation.
 * It implements the Runnable interface to be executed as a thread.
 */
class Nurse implements Runnable {
    // The unique identifier of the nurse thread
    private int threadID;

    /**
     * Constructor to initialize a Nurse object with a specific threadID.
     * 
     * @param threadID The unique identifier of the nurse thread
     */
    public Nurse(int threadID) {
        this.threadID = threadID;
    }

    /**
     * Implements the behavior of the nurse thread.
     * The nurse continuously waits for receptionist calls, handles patients,
     * takes patients to doctor's offices, signals that patients have entered
     * offices,
     * and signals doctors to come to attend patients.
     */
    @Override
    public void run() {
        while (true) {
            waitForReceptionistCall(); // Wait for receptionist call
            handlePatient(); // Handle patient
            takePatientToOffice(); // Take patient to doctor's office
            signalPatientEnteredOffice(); // Signal patient entered office
            signalDoctorToCome(); // Signal doctor to come
        }
    }

    // Waits for a semaphore signal from the receptionist indicating that a patient
    // is ready to be taken to the doctor's office.
    private void waitForReceptionistCall() {
        try {

            // Acquire nurse semaphore for the specific thread
            Project2.nurse[threadID].acquire();

        } catch (InterruptedException ignored) {
            // Exception ignored
        }
    }

    // Handles the patient by acquiring the mutex semaphore.
    private void handlePatient() {
        try {

            // Acquire mutex semaphore for the specific thread
            Project2.yesNo[threadID].acquire();

        } catch (InterruptedException ignored) {
            // Exception ignored
        }
    }

    // Takes the next patient from the nurse's buffer to the doctor's office and
    // prints a message indicating the action.
    private void takePatientToOffice() {
        // Retrieve the ID of the first patient in the nurse's buffer
        int patientID = Project2.docBuffer.get(threadID).peekFirst();
        System.out.println("Nurse " + threadID + " takes patient " + patientID + " to doctor's office");
    }

    // Signals the semaphore to indicate that the patient has entered the doctor's
    // office.
    private void signalPatientEnteredOffice() {
        // Release the enter office semaphore for the specific nurse thread
        Project2.enterOff[threadID].release();
    }

    // Signals the semaphore to indicate that the doctor should come to attend to
    // the patient in the office.
    private void signalDoctorToCome() {
        // Release the doctor semaphore for the specific nurse thread
        Project2.doctor[threadID].release();
    }
}

// The Doctor class represents a doctor entity in the simulation. It implements
// the Runnable interface to be executed as a thread.
class Doctor implements Runnable {
    private int threadID; // The unique identifier of the doctor thread

    /**
     * Constructor to initialize a Doctor object with a specific threadID.
     * 
     * @param threadID The unique identifier of the doctor thread
     */
    public Doctor(int threadID) {
        this.threadID = threadID;
    }

    /**
     * Implements the behavior of the doctor thread.
     * The doctor continuously waits for nurse calls, retrieves patient information,
     * listens to patient symptoms, gives advice to patients, waits for patients to
     * leave,
     * and releases resources after patient visits.
     */
    @Override
    public void run() {
        while (true) {
            waitForNurseCall(); // Wait for nurse call
            int patientID = retrievePatientInformation(); // Retrieve patient information
            listenToSymptoms(patientID); // Listen to patient symptoms
            giveAdviceToPatient(); // Give advice to patient
            waitForPatientToLeave(); // Wait for patient to leave
            releaseResources(); // Release resources
        }
    }

    // Waits for a semaphore signal from the nurse indicating that a patient is
    // ready to be seen by the doctor.
    private void waitForNurseCall() {
        try {

            // Acquire doctor semaphore for the specific thread
            Project2.doctor[threadID].acquire();

        } catch (InterruptedException ignored) {
            // Exception ignored
        }
    }

    /**
     * Retrieves the ID of the next patient to be seen by the doctor from the
     * doctor's buffer.
     * 
     * @return The ID of the patient to be seen by the doctor
     */
    private int retrievePatientInformation() {
        // Retrieve and remove the first patient from the doctor's buffer
        return Project2.docBuffer.get(threadID).poll();
    }

    /**
     * Prints a message indicating that the doctor is listening to the symptoms
     * reported by the patient with the given ID.
     * 
     * @param patientID The ID of the patient whose symptoms are being listened to
     */
    private void listenToSymptoms(int patientID) {
        System.out.println("Doctor " + threadID + " listens to symptoms from patient " + patientID);
    }

    // Signals the semaphore to indicate that the doctor has finished giving advice
    // to the patient.
    private void giveAdviceToPatient() {

        // Release the advice semaphore for the specific doctor thread
        Project2.advice[threadID].release();
    }

    // Waits for a semaphore signal indicating that the patient has left the
    // doctor's office after receiving advice.
    private void waitForPatientToLeave() {
        try {

            // Acquire advice received semaphore for the specific thread
            Project2.adviceRec[threadID].acquire();

        } catch (InterruptedException ignored) {
            // Exception ignored
        }
    }

    // Releases the mutex semaphore to allow another patient to be processed by the
    // doctor.
    private void releaseResources() {
        // Release the mutex semaphore for the specific doctor thread
        Project2.yesNo[threadID].release();
    }
}
