import java.io.*;
import java.util.*;

//This is the main class I use to represent my project for scheduling algorithms
public class Project3 {

    // Here are my lists of task names (A, B, C, D, and E) and task times (arrival
    // time & service time)
    private List<String> taskNames;
    private List<int[]> taskTimes;

    // This is my constructor to initalize task names and task times
    public Project3(List<String> taskNames, List<int[]> taskTimes) {
        this.taskNames = taskNames;
        this.taskTimes = taskTimes;
    }

    // This function performs First-Come First-Serve scheduling
    public Map<String, Queue<Integer>> firstComeFirstServe() {

        // Here is my result map to store task schedules and below that is the current
        // time which is intialized to 0
        Map<String, Queue<Integer>> resultMap = new HashMap<>();
        int currentTime = 0;

        // This loop iterates through each task
        for (int i = 0; i < taskTimes.size(); i++) {
            String taskName = taskNames.get(i); // get's the task name
            Queue<Integer> timeIntervals = new LinkedList<>(); // this queue stores the time intervals
            timeIntervals.add(currentTime); // add start time
            int endTime = currentTime + taskTimes.get(i)[1]; // calculating the end time
            timeIntervals.add(endTime); // add the end time
            resultMap.put(taskName, timeIntervals); // this stores the task schedule in the result map that we defined
                                                    // earlier
            currentTime = endTime; // then we update the current time with the end time of the job we just looked
                                   // at
        }

        // returning the result map of FCFS
        return resultMap;
    }

    // This function performs Round Robin
    public Map<String, Queue<Integer>> roundRobin() {

        // Result map to store the task schedules
        Map<String, Queue<Integer>> resultMap = new HashMap<>();

        // These are the list's to store the arrival times and remaining service times
        List<Integer> arrivalTimes = new ArrayList<>();
        List<Integer> remainingTimes = new ArrayList<>();

        // populating the arrival times and remaining times lists
        for (int i = 0; i < taskNames.size(); i++) {
            arrivalTimes.add(taskTimes.get(i)[0]);
            remainingTimes.add(taskTimes.get(i)[1]);
        }

        // initializing a bunch of variables such as current task, index, start time,
        // time, and a tasks finished counter
        String currentTask = taskNames.get(0);
        int currentIndex = 0;
        int currentStartTime = taskTimes.get(0)[0];
        int currentTime = taskTimes.get(0)[0];
        int tasksFinished = 0;

        // This is the queue are going to use to store tasks
        Queue<String> taskQueue = new LinkedList<>();

        taskQueue.add(currentTask); // adding current task to the queue
        int index = currentIndex; // initializing index

        // This loop iterates through tasks to populate the task queue
        while (true) {
            index++;
            if (index < taskNames.size() && arrivalTimes.get(index).equals(currentTime)) {
                String nextTask = taskNames.get(index);
                taskQueue.add(nextTask);
            } else {
                break;
            }
        }

        // This is the main scheduling loop that we'll be using
        while (true) {
            String task = taskQueue.poll(); // getting the next task from the queue

            if (task == null) {

                currentTime++; // If there is not task available, increment current time

                // checking for any new arrivals and then adding them to the task queue
                if (arrivalTimes.contains(currentTime)) {
                    int arrivalIndex = arrivalTimes.indexOf(currentTime);
                    String nextTask = taskNames.get(arrivalIndex);
                    taskQueue.add(nextTask);

                    // add the subsequent arrivals to the task queue
                    while (true) {
                        arrivalIndex++;
                        if (arrivalIndex < taskNames.size() && arrivalTimes.get(arrivalIndex).equals(currentTime)) {
                            String nextArrivalTask = taskNames.get(arrivalIndex);
                            taskQueue.add(nextArrivalTask);
                        } else {
                            break;
                        }
                    }
                }

            } else {

                // Process the current task
                if (!task.equals(currentTask)) {

                    // But, if the task has changed, update the schedule for the previous task
                    if (remainingTimes.get(currentIndex) != 0) {
                        Queue<Integer> queue = resultMap.get(currentTask);
                        if (queue == null)
                            queue = new LinkedList<>();
                        queue.add(currentStartTime);
                        queue.add(currentTime);
                        resultMap.put(currentTask, queue);
                    }

                    // now update the current task and index
                    currentTask = task;
                    currentIndex = taskNames.indexOf(currentTask);
                    currentStartTime = currentTime;
                }

                // Update the remaining time for the current task now
                int remainingTime = remainingTimes.get(currentIndex);
                remainingTimes.set(currentIndex, remainingTime - 1);
                currentTime++;

                // Now we are checking for new arrivals and adding them to the task queue
                if (arrivalTimes.contains(currentTime)) {
                    int arrivalIndex = arrivalTimes.indexOf(currentTime);
                    String nextTask = taskNames.get(arrivalIndex);
                    taskQueue.add(nextTask);

                    // Add the subsequent arrivals to the task queue
                    while (true) {
                        arrivalIndex++;
                        if (arrivalIndex < taskNames.size() && arrivalTimes.get(arrivalIndex).equals(currentTime)) {
                            String nextArrivalTask = taskNames.get(arrivalIndex);
                            taskQueue.add(nextArrivalTask);
                        } else {
                            break;
                        }
                    }
                }

                // Now we check if the current task has finished
                if (remainingTime - 1 == 0) {

                    // If it has, update the schedule
                    Queue<Integer> queue = resultMap.get(currentTask);

                    if (queue == null)
                        queue = new LinkedList<>();
                    queue.add(currentStartTime);
                    queue.add(currentTime);
                    resultMap.put(currentTask, queue);
                    tasksFinished++;

                    // checks if all tasks have finished
                    if (tasksFinished == taskNames.size())
                        break;
                } else {

                    // If the task hasn't finished, add it back to the task queue
                    taskQueue.add(currentTask);

                }
            }
        }

        // returning the result map of RR
        return resultMap;
    }

    // This function prints the scheduling
    private static void printScheduling(Map<String, Queue<Integer>> scheduleMap, List<String> taskNames) {

        // Iterating through each task
        for (String taskName : taskNames) {

            // Printing the task name (A, B, C, D, E)
            System.out.print(taskName + " ");

            // Getting the time intervals for each task and initializing the previous end
            // time
            Queue<Integer> timeIntervals = scheduleMap.get(taskName);
            int previousEndTime = 0;

            // Iterate through the time intervals
            while (!timeIntervals.isEmpty()) {

                // Getting the start and end times
                int startTime = timeIntervals.poll();
                int endTime = timeIntervals.poll();

                // Printing writesapce before the start time
                for (int j = 0; j < startTime - previousEndTime; j++)
                    System.out.print(" ");

                // Printing Xs representing task execution
                for (int j = 0; j < endTime - startTime; j++)
                    System.out.print("X");

                // Updating the previous end time
                previousEndTime = endTime;
            }
            System.out.println(); // moves to the next line (next task)
        }
    }

    // This is the main method that we are using to run the program
    public static void main(String[] args) {
        // checking to see if the command line argument is sufficed
        if (args.length != 1) {

            // If not sufficed, print out how to do it
            System.err.println("Usage: java Project3 <filename>");
            System.exit(1);

        }

        // getting the filename from the command line arguments
        String filename = args[0];

        try {

            // initalizing the lists to store the task names and times
            List<String> taskNames = new ArrayList<>();
            List<int[]> taskTimesList = new ArrayList<>();
            String line;

            // Reading from the specified file
            BufferedReader br = new BufferedReader(new FileReader(filename));

            // Reading each line of said file
            while ((line = br.readLine()) != null) {

                // Splitting the lines by tab characters
                String[] split = line.split("\\t");

                // Extracting the task names from the first part of the split
                taskNames.add(split[0]);

                // Converting arrival time and service time to integers and then adding the to
                // the list
                int arrivalTime = Integer.valueOf(split[1]);
                int serviceTime = Integer.valueOf(split[2]);
                taskTimesList.add(new int[] { arrivalTime, serviceTime });
            }

            // closing the file reader
            br.close();

            // Creating a new instance of Project3 with the extracted task names and times
            Project3 project3 = new Project3(taskNames, taskTimesList);

            // Performing FCFS and RR scheduling and storing the results
            Map<String, Queue<Integer>> resultMapFCFS = project3.firstComeFirstServe();
            Map<String, Queue<Integer>> resultMapRR = project3.roundRobin();

            // Printing out the scheduling of First-Come-First-Serve
            System.out.println("First-Come-First-Serve Scheduling:");
            printScheduling(resultMapFCFS, taskNames);

            // Printing out the scheduling for Round Robin
            System.out.println("\nRound Robin Scheduling:");
            printScheduling(resultMapRR, taskNames);

        } catch (IOException e) {
            e.printStackTrace(); // Printing the stack trace if an IOException occurs
        }
    }

}
