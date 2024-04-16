import java.io.*;
import java.util.*;

public class Project3 {
    private List<String> taskNames;
    private List<int[]> taskTimes;

    public Project3(List<String> taskNames, List<int[]> taskTimes) {
        this.taskNames = taskNames;
        this.taskTimes = taskTimes;
    }

    public Map<String, Queue<Integer>> firstComeFirstServe() {
        Map<String, Queue<Integer>> resultMap = new HashMap<>();
        int currentTime = 0;
        for (int i = 0; i < taskTimes.size(); i++) {
            String taskName = taskNames.get(i);
            Queue<Integer> timeIntervals = new LinkedList<>();
            timeIntervals.add(currentTime);
            int endTime = currentTime + taskTimes.get(i)[1];
            timeIntervals.add(endTime);
            resultMap.put(taskName, timeIntervals);
            currentTime = endTime;
        }
        return resultMap;
    }

    public Map<String, Queue<Integer>> roundRobin() {
        Map<String, Queue<Integer>> resultMap = new HashMap<>();
        List<Integer> arrivalTimes = new ArrayList<>();
        List<Integer> remainingTimes = new ArrayList<>();
        for (int i = 0; i < taskNames.size(); i++) {
            arrivalTimes.add(taskTimes.get(i)[0]);
            remainingTimes.add(taskTimes.get(i)[1]);
        }
        String currentTask = taskNames.get(0);
        int currentIndex = 0;
        int currentStartTime = taskTimes.get(0)[0];
        int currentTime = taskTimes.get(0)[0];
        int tasksFinished = 0;
        Queue<String> taskQueue = new LinkedList<>();
        taskQueue.add(currentTask);
        int index = currentIndex;
        while (true) {
            index++;
            if (index < taskNames.size() && arrivalTimes.get(index).equals(currentTime)) {
                String nextTask = taskNames.get(index);
                taskQueue.add(nextTask);
            } else {
                break;
            }
        }
        while (true) {
            String task = taskQueue.poll();
            if (task == null) {
                currentTime++;
                if (arrivalTimes.contains(currentTime)) {
                    int arrivalIndex = arrivalTimes.indexOf(currentTime);
                    String nextTask = taskNames.get(arrivalIndex);
                    taskQueue.add(nextTask);
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
                if (!task.equals(currentTask)) {
                    if (remainingTimes.get(currentIndex) != 0) {
                        Queue<Integer> queue = resultMap.get(currentTask);
                        if (queue == null)
                            queue = new LinkedList<>();
                        queue.add(currentStartTime);
                        queue.add(currentTime);
                        resultMap.put(currentTask, queue);
                    }
                    currentTask = task;
                    currentIndex = taskNames.indexOf(currentTask);
                    currentStartTime = currentTime;
                }
                int remainingTime = remainingTimes.get(currentIndex);
                remainingTimes.set(currentIndex, remainingTime - 1);
                currentTime++;
                if (arrivalTimes.contains(currentTime)) {
                    int arrivalIndex = arrivalTimes.indexOf(currentTime);
                    String nextTask = taskNames.get(arrivalIndex);
                    taskQueue.add(nextTask);
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
                if (remainingTime - 1 == 0) {
                    Queue<Integer> queue = resultMap.get(currentTask);
                    if (queue == null)
                        queue = new LinkedList<>();
                    queue.add(currentStartTime);
                    queue.add(currentTime);
                    resultMap.put(currentTask, queue);
                    tasksFinished++;
                    if (tasksFinished == taskNames.size())
                        break;
                } else {
                    taskQueue.add(currentTask);
                }
            }
        }
        return resultMap;
    }
    
    private static void printScheduling(Map<String, Queue<Integer>> scheduleMap, List<String> taskNames) {
        for (String taskName : taskNames) {
            System.out.print(taskName + " ");
            Queue<Integer> timeIntervals = scheduleMap.get(taskName);
            int previousEndTime = 0;
            while (!timeIntervals.isEmpty()) {
                int startTime = timeIntervals.poll();
                int endTime = timeIntervals.poll();
                for (int j = 0; j < startTime - previousEndTime; j++)
                    System.out.print(" ");
                for (int j = 0; j < endTime - startTime; j++)
                    System.out.print("X");
                previousEndTime = endTime;
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        try {
            List<String> taskNames = new ArrayList<>();
            List<int[]> taskTimesList = new ArrayList<>();
            String line;
            BufferedReader br = new BufferedReader(new FileReader("jobs.txt"));
            while ((line = br.readLine()) != null) {
                String[] split = line.split("\\t");
                taskNames.add(split[0]);
                int arrivalTime = Integer.valueOf(split[1]);
                int serviceTime = Integer.valueOf(split[2]);
                taskTimesList.add(new int[]{arrivalTime, serviceTime});
            }
            br.close();

            Project3 project3 = new Project3(taskNames, taskTimesList);

            Map<String, Queue<Integer>> resultMapFCFS = project3.firstComeFirstServe();
            Map<String, Queue<Integer>> resultMapRR = project3.roundRobin();

            System.out.println("First-Come-First-Serve Scheduling:");
            printScheduling(resultMapFCFS, taskNames);

            System.out.println("\nRound Robin Scheduling:");
            printScheduling(resultMapRR, taskNames);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
