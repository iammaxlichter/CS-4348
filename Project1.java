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

            int PC = 0, SP = 0, IR = 0, AC = 0;
            int X = 0, Y = 0;
            int[] Memory = new int[2000];

            while (scan.hasNextLine()) {
                String line = scan.nextLine().trim();

                // Split the line by "//" and only consider the part before the delimiter
                String[] parts = line.split("//");
                if (parts.length > 0) {
                    line = parts[0].trim(); // Take the part before the delimiter
                }

                // Ignore empty lines
                if (line.isEmpty()) {
                    continue;
                }

                switch (line) {

                    case "1": // Load value
                        if (scan.hasNextLine()) {
                            String userInput = scan.nextLine();
                            try {
                                int value = Integer.parseInt(userInput);
                                AC = value;
                                System.out.println("Loaded value " + value + " into AC.");
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid input. Please enter a valid integer.");
                            }
                        } else {
                            System.out.println("No input found after '1'.");
                        }
                        break;

                    case "2": // Load addr

                        break;

                    case "8": // Get
                        int randomNumber = new Random().nextInt(100) + 1;

                        AC = randomNumber;
                        System.out.println("Random number assigned to AC is: " + AC);
                        break;

                    case "9": // Put
                        if (scan.hasNextLine()) {
                            String nextLine = scan.nextLine();
                            switch (nextLine) {
                                case "1":
                                    System.out.println("AC as an int: " + AC);
                                    break;
                                case "2":
                                    System.out.println("AC as a char: " + (char) AC);
                                    break;
                                default:
                                    System.out.println("Invalid input after '9': " + nextLine);
                            }
                        } else {
                            System.out.println("No input found after '9'");
                        }
                        break;

                    case "10": // AddX
                        AC = X + AC;
                        System.out.println("adding " + X + " to the AC, AC is now: " + AC);
                        break;

                    case "11": // AddY
                        AC = Y + AC;
                        System.out.println("adding " + Y + " to the AC, AC is now: " + AC);
                        break;

                    case "14": // CopyToX
                        X = AC;
                        System.out.println("X is now: " + X);
                        break;

                    case "16": // CopyToY
                        Y = AC;
                        System.out.println("Y is now: " + Y);
                        break;

                    case "50": // End
                        System.out.println("End of execution.");
                        return; // Terminate the program

                    default:
                        System.out.println("Not a recognized command: ");
                }
            }
            scan.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        }
    }

    // // Find the next available index in the array AC
    // private static int findNextAvailableIndex(int[] array) {
    // for (int i = 0; i < array.length; i++) {
    // if (array[i] == 0) {
    // return i;
    // }
    // }
    // return -1; // Array is full
    // }
}