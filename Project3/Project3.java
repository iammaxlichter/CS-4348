import java.io.*;
import java.util.*;

public class Project3 {
    private int[][] taskTimes;
    private List<String> taskNames;

    public Project3(List<String> taskNames, int[][] taskTimes) {
        this.taskNames = taskNames;
        this.taskTimes = taskTimes;
    }

    public Map<String, Queue<Integer>> firstComeFirstServe() {
        Map<String, Queue<Integer>> resultMap = new HashMap<>();
        int curPos = taskTimes[0][0];
        for (int i = 0; i < taskTimes.length; i++) {
            String taskName = taskNames.get(i);
            Queue<Integer> timeIntervals = new LinkedList<>();
            timeIntervals.add(curPos);
            timeIntervals.add(curPos + taskTimes[i][1]);
            resultMap.put(taskName, timeIntervals);
            if (i != taskTimes.length - 1)
                curPos = taskTimes[i + 1][0] > curPos + taskTimes[i][1] ? taskTimes[i + 1][0] : curPos + taskTimes[i][1];
        }
        return resultMap;
    }

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
        int time = taskTimes[0][0];
        int taskFinished = 0;
        Queue<String> taskQueue = new LinkedList<>();
        taskQueue.add(selectedTask);
        int idx = selectedTaskIdx;
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
            if (task == null) {
                time++;
                if (arrivalTimeList.contains(time)) {
                    int index = arrivalTimeList.indexOf(time);
                    String nextTask = taskNames.get(index);
                    taskQueue.add(nextTask);
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
                    if (remainTimeList.get(selectedTaskIdx) != 0) {
                        Queue<Integer> queue = resultMap.get(selectedTask);
                        if (queue == null)
                            queue = new LinkedList<>();
                        queue.add(selectedTaskStartT);
                        queue.add(time);
                        resultMap.put(selectedTask, queue);
                    }
                    selectedTask = task;
                    selectedTaskIdx = taskNames.indexOf(selectedTask);
                    selectedTaskStartT = time;
                }
                int remainT = remainTimeList.get(selectedTaskIdx);
                remainTimeList.set(selectedTaskIdx, remainT - 1);
                time++;
                if (arrivalTimeList.contains(time)) {
                    int index = arrivalTimeList.indexOf(time);
                    String nextTask = taskNames.get(index);
                    taskQueue.add(nextTask);
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
                    taskQueue.add(selectedTask);
                }
            }
        }
        return resultMap;
    }

    private static void printScheduling(Map<String, Queue<Integer>> scheduleMap, List<String> taskNames) {
        for (String taskName : taskNames) {
            System.out.print(taskName + " ");
            Queue<Integer> timeIntervals = scheduleMap.get(taskName);
            int prevTimeStamp = 0;
            while (!timeIntervals.isEmpty()) {
                int start = timeIntervals.poll();
                int end = timeIntervals.poll();
                for (int j = 0; j < start - prevTimeStamp; j++)
                    System.out.print(" ");
                for (int j = 0; j < end - start; j++)
                    System.out.print("X");
                prevTimeStamp = end;
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

            Project3 project3 = new Project3(taskNames, taskTimesList.toArray(new int[0][0]));

            Map<String, Queue<Integer>> resultMapFCFS = project3.firstComeFirstServe();
            Map<String, Queue<Integer>> resultMapRR = project3.roundRobin();

            System.out.println("The scheduling of FCFS is:");
            printScheduling(resultMapFCFS, taskNames);

            System.out.println("\nThe scheduling of RR is:");
            printScheduling(resultMapRR, taskNames);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
