import java.io.*;
import java.util.*;

public class ScheduleAlgorithms {
    private int[][] taskTimes;      // arrival and service time array
    private int size;               // total number of tasks
    private List<String> taskNames; // task names

    public ScheduleAlgorithms(Queue<Integer> queue, List<String> taskNames) {
        this.size = queue.size();
        this.taskNames = taskNames;

        initTaskTimes(queue);
    }

    /**
     * initialize the arrival and service time array
     * @param queue the arrival and service time queue
     * @return task time array
     */
    private void initTaskTimes(Queue<Integer> queue) {
        taskTimes = new int[size / 2][2];

        for (int i = 0; i < size / 2; i++) {
            taskTimes[i][0] = queue.poll();
            taskTimes[i][1] = queue.poll();
        }

    }

    /**
     * First Come First Serve algorithm
     * return a scheduling time intervals map related to each task
     * @return map
     */
    public Map<String, Queue<Integer>> firstComeFirstServe() {
        Map<String, Queue<Integer>> resultMap = new HashMap<>(); // store task scheduling time intervals

        int curPos = taskTimes[0][0];
        for (int i = 0; i < taskTimes.length; i++) {
            String taskName = taskNames.get(i);
            Queue<Integer> timeIntervals = new LinkedList<>();
            timeIntervals.add(curPos);
            timeIntervals.add(curPos + taskTimes[i][1]);
            resultMap.put(taskName, timeIntervals);

            if (i != taskTimes.length - 1)
                // compare the current task's finish time with the next task's arrival time
                // if next task's arrival time is larger, update curPos to the next task's arrival time
                // otherwise, update curPos to the current task's finish time
                curPos = taskTimes[i + 1][0] > curPos + taskTimes[i][1] ? taskTimes[i + 1][0] : curPos + taskTimes[i][1];
        }

        return resultMap;
    }

    /**
     * Round Robin algorithm
     * return a scheduling time intervals map related to each task
     * here the quantum is 1
     * @return map
     */
    public Map<String, Queue<Integer>> roundRobin() {
        Map<String, Queue<Integer>> resultMap = new HashMap<>();
        List<Integer> arrivalTimeList = new ArrayList<>();
        List<Integer> remainTimeList = new ArrayList<>();
        for (int i = 0; i < taskNames.size(); i++) {
            arrivalTimeList.add(taskTimes[i][0]);
            remainTimeList.add(taskTimes[i][1]);
        }

        String selectedTask = taskNames.get(0);
        int selectedTaskIdx = 0;
        int selectedTaskStartT = taskTimes[0][0];
        int time = taskTimes[0][0]; // time starts from the first task's arrival time
        int taskFinished = 0;

        // add the first task into task queue
        Queue<String> taskQueue = new LinkedList<>();
        taskQueue.add(selectedTask);
        int idx = selectedTaskIdx;
        // in case there are multiple tasks arrive at the same time
        while (true) {
            idx++;
            if (idx < taskNames.size() && arrivalTimeList.get(idx).equals(time)) {
                String task1 = taskNames.get(idx);
                taskQueue.add(task1);
            } else {
                break;
            }
        }

        while (true) {
            String task = taskQueue.poll();
            if (task == null) { // this is when the previous task finished, but the next task doesn't arrive
                time++;
                if (arrivalTimeList.contains(time)) {
                    int index = arrivalTimeList.indexOf(time);
                    String nextTask = taskNames.get(index);
                    taskQueue.add(nextTask);  // first add the new arrival task, then add the current task back and the task should not be completed
                    // in case there are multiple tasks arrive at the same time
                    while (true) {
                        index++;
                        if (index < taskNames.size() && arrivalTimeList.get(index).equals(time)) {
                            String task1 = taskNames.get(index);
                            taskQueue.add(task1);
                        } else {
                            break;
                        }
                    }
                }
            } else {
                if (!task.equals(selectedTask)) {
                    if (remainTimeList.get(selectedTaskIdx) != 0) { // only not finished task's status will be saved here
                        Queue<Integer> queue = resultMap.get(selectedTask);
                        if (queue == null)
                            queue = new LinkedList<>();

                        // save previous task's time intervals
                        queue.add(selectedTaskStartT);
                        queue.add(time);
                        resultMap.put(selectedTask, queue);
                    }

                    // update new selected task information
                    selectedTask = task;
                    selectedTaskIdx = taskNames.indexOf(selectedTask);
                    selectedTaskStartT = time;
                }

                // update remaining time list
                int remainT = remainTimeList.get(selectedTaskIdx);
                remainTimeList.set(selectedTaskIdx, remainT - 1);
                time++; // time moves forward

                if (arrivalTimeList.contains(time)) {
                    int index = arrivalTimeList.indexOf(time);
                    String nextTask = taskNames.get(index);
                    taskQueue.add(nextTask);  // first add the new arrival task, then add the current task back and the task should not be completed
                    // in case there are multiple tasks arrive at the same time
                    while (true) {
                        index++;
                        if (index < taskNames.size() && arrivalTimeList.get(index).equals(time)) {
                            String task1 = taskNames.get(index);
                            taskQueue.add(task1);
                        } else {
                            break;
                        }
                    }
                }

                if (remainT - 1 == 0) {
                    // finished task's status will be saved here
                    Queue<Integer> queue = resultMap.get(selectedTask);
                    if (queue == null)
                        queue = new LinkedList<>();

                    queue.add(selectedTaskStartT);
                    queue.add(time);
                    resultMap.put(selectedTask, queue);

                    taskFinished++;
                    if (taskFinished == taskNames.size())
                        break;
                } else {
                    taskQueue.add(selectedTask); // if the task doesn't complete, add back to the queue
                }
            }

        }

        return resultMap;
    }

   
    
    public static void main(String[] args) {
        String file = "jobs.txt";
        String command = args[0]; // command can be FCFS/RR/SPN/SRT/HRRN/FB/ALL

        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            List<String> taskNames = new ArrayList<>();
            Queue<Integer> queue = new LinkedList<>();
            String line = br.readLine();
            while (line != null) {
                String[] split = line.split("\\t");
                taskNames.add(split[0]);
                // TODO consider start time and service time will be float type???
                int arrivalTime = Integer.valueOf(split[1]);
                int serviceTime = Integer.valueOf(split[2]);
                queue.add(arrivalTime);
                queue.add(serviceTime);

                line = br.readLine();
            }

            ScheduleAlgorithms sa = new ScheduleAlgorithms(queue, taskNames);
            Map<String, Map<String, Queue<Integer>>> result = new HashMap<>();
            switch (command) {
                case "FCFS": {
                    Map<String, Queue<Integer>> resultMap = sa.firstComeFirstServe();
                    result.put("FCFS", resultMap);
                    break;
                }
                case "RR": {
                    Map<String, Queue<Integer>> resultMap = sa.roundRobin();
                    result.put("RR", resultMap);
                    break;
                }
                case "ALL": {
                    Map<String, Queue<Integer>> resultMapFCFS = sa.firstComeFirstServe();
                    result.put("FCFS", resultMapFCFS);
                    Map<String, Queue<Integer>> resultMapRR = sa.roundRobin();
                    result.put("RR", resultMapRR);
                    break;
                }
            }

            Set<String> keySet = result.keySet();
            for (String algo : keySet) {
                System.out.println("The scheduling of " + algo + " is:");
                Map<String, Queue<Integer>> scheduleMap = result.get(algo);

                for (int i = 0; i < scheduleMap.size(); i++) {
                    String taskName = taskNames.get(i);
                    System.out.print(taskName + " ");
                    Queue<Integer> timeIntervals = scheduleMap.get(taskName);
                    int prevTimeStamp = 0;
                    while (!timeIntervals.isEmpty()) {
                        int start = timeIntervals.poll();
                        int end = timeIntervals.poll();

                        // print spaces, mean the task is not executed
                        for (int j = 0; j < start - prevTimeStamp; j++)
                            System.out.print(" ");
                        // print X, means the task executed
                        for (int j = 0; j < end - start; j++)
                            System.out.print("X");
                        prevTimeStamp = end; // TODO previous forgot to update prevTimeStamp, write into summary bug 1
                    }
                    System.out.println();
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}