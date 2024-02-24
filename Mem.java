import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Mem {

    static int[] Memory; // Our memory array to store the data

    // This method takes in an address as an input and returns the specified data at
    // that address
    public static int read(int address) {
        return Memory[address]; // Reads and returns the data stored at the specified memory address
    }

    // This method takes in a address and data parameter. We basically write code to
    // a specified memory address
    public static void write(int address, int data) {
        Memory[address] = data; // Writes the given data to the specified memory address
    }

    // Main method of the Mem class that serves as the entry point of the program
    public static void main(String[] args) {

        // Initializing the memory array with a size of 2000
        Memory = new int[2000];

        // Creating a scanner to read the file inputted (fileScanner)
        try (Scanner fileScanner = new Scanner(new File(args[0]));

                // Creating another scanner to read from the standard input
                Scanner sc = new Scanner(System.in)) {

            int index = 0; // Variable that keeps track of the memory address

            // Loops through each line of the input file
            while (fileScanner.hasNextLine()) {

                // Reads the next line from the file and removes the front and back whitespace
                String line = fileScanner.nextLine().trim();

                // Checking to see if the array is empty or not
                if (!line.isEmpty()) {

                    // Now we are looking at the case of all first characters of the line
                    switch (line.charAt(0)) {

                        // If the first char is a '.', it means a memory address marker. Index = the
                        // memory address
                        case '.' -> index = Integer.parseInt(line.substring(1).split("\\s+")[0]);

                        // If the first char is a digit (0-9), it indicates to be stored in the memory
                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                            String[] splitLine = line.split("\\s+"); // Splitting the line by whitespace

                            // Checking to see if there is at least one element after splitting. If there
                            // is, parse the first element of the data and store it in memory at the current
                            // index
                            if (splitLine.length >= 1) {
                                Memory[index++] = Integer.parseInt(splitLine[0]);
                            }
                        }
                    }
                }
            }

            // This while loop loops until there is a next line available
            while (sc.hasNextLine()) {

                // Reads the next line and stores it in nextLine
                String nextLine = sc.nextLine();

                // Reads nextLine and stores the first char of it in nextCommand
                char nextCommand = nextLine.charAt(0);

                // This switch statement is based on the command character
                switch (nextCommand) {

                    // If the command is 'r', it indicates a read operation
                    case 'r' -> {

                        // This line extracts the address from the rest of the line
                        int address = Integer.parseInt(nextLine.substring(1));

                        // reading from the memory at the specified address and printing out the results
                        System.out.println(read(address));
                    }

                    // If the command is 'w', it indicates a write operation
                    case 'w' -> {

                        // Splitting the line to extract parameters
                        String[] params = nextLine.substring(1).split(",");

                        // Writing the data to the specified memory address
                        write(Integer.parseInt(params[0]), Integer.parseInt(params[1]));
                    }

                    // If the command is 'e', it indicates a exit operation
                    case 'e' -> {

                        // Printing a message indicating the exitting of the program and then exits with
                        // a successful status code (0)
                        System.out.println("Exiting the memory program.");
                        System.exit(0);
                    }

                    // If the command isn't recognized, print an error message
                    default -> System.out.println("Invalid command.");
                }
            }

        // Exception handling for NumberFormatException and FileNotFoundException
        } catch (NumberFormatException | FileNotFoundException e) {
            e.printStackTrace(); //Printing out the stack trace of the exception
        }
    }
}