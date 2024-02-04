import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class Project1 {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Project1 <input_file>");
            System.exit(1);
        }
        String fileName = args[0];

        try {
            File file = new File(fileName);
            Scanner scan = new Scanner(file);

            int PC = 0, SP = 0, IR = 0;
            int latestNumberGenerated = 0;
            int lastFilledIndex = -1;
            int X = 0, Y = 0;
            int sum = 0;

            int[] AC = new int[2000];
            int[] Memory = new int[2000];

            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                switch (line) {

                    case "Load value":
                    int loadIndex = findNextAvailableIndex(AC);
                    if (loadIndex != -1) {
                        AC[loadIndex] = sum;
                        System.out.println("Loaded value into AC[" + loadIndex + "]: " + AC[loadIndex]);
                        break;
                    }

                    case "Get":
                        int randomNumber = new Random().nextInt(100) + 1;
                        int index = findNextAvailableIndex(AC);
                        if (index != -1) {
                            AC[index] = randomNumber;
                            System.out.println("Random number assigned to AC[" + index + "]: " + AC[index]);
                            latestNumberGenerated = randomNumber;
                            lastFilledIndex = index;
                        } else {
                            System.out.println("AC is full. Cannot assign the random number.");
                        }
                        break;

                    case "Put":
                        if (lastFilledIndex != -1) {
                            sum = AC[lastFilledIndex];
                            System.out.println(sum);
                        } else {
                            System.out.println("No number generated yet.");
                        }
                        break;

                    case "AddX":
                        if (lastFilledIndex != -1) {
                            AC[lastFilledIndex] += X;
                            System.out.println("Added X to AC[" + lastFilledIndex + "]: " + AC[lastFilledIndex]);
                        } else {
                            System.out.println("No number generated yet.");
                        }
                        break;

                    case "AddY":
                        if (lastFilledIndex != -1) {
                            AC[lastFilledIndex] += Y;
                            System.out.println("Added Y to AC[" + lastFilledIndex + "]: " + AC[lastFilledIndex]);
                        } else {
                            System.out.println("No number generated yet.");
                        }
                        break;

                    case "CopyToX":
                        X = latestNumberGenerated;
                        System.out.println("Copied last number to X: " + X);
                        break;

                    case "CopyToY":
                        Y = latestNumberGenerated;
                        System.out.println("Copied last number to Y: " + Y);
                        break;

                    case "End":
                        System.out.println("End of execution.");
                        return; // Terminate the program

                    default:
                        System.out.println("Not Buzz Word");
                }
            }
            scan.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        }
    }

    // Find the next available index in the array AC
    private static int findNextAvailableIndex(int[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 0) {
                return i;
            }
        }
        return -1; // Array is full
    }
}