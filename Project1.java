import java.io.*;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

public class Project1 {
    int[] Memory = new int[20];

    public void ParentFunction() {
        int PC = 0, SP = 0, IR = 0, AC = 0;
        int X = 0, Y = 0;
        Switch(PC, SP, IR, AC, X, Y);
    }

    public void Switch(int PC, int SP, int IR, int AC, int X, int Y) {
        int i = 0;
        int returnAddress = 0;
        while (i < Memory.length && Memory[i] != 0) {
            String line = Integer.toString(Memory[i]).trim();
            String[] parts = line.split("//");
            if (parts.length > 0) {
                line = parts[0].trim();
            }

            if (line.isEmpty()) {
                i++;
                continue;
            }

            
            switch (line) {
                case "1": // Load Value
                
                    if (++i < Memory.length) {
                        AC = Memory[i];
                    }
                    break;

                case "8": // Get
                    int randomNumber = new Random().nextInt(100) + 1;
                    AC = randomNumber;
                    break;

                    case "9": // Put
                    if (++i < Memory.length) {
                        String nextLine = Integer.toString(Memory[i]);
                        //System.out.println("\n" + nextLine + " " + AC);
                        
                        switch (nextLine) {
                            case "1":
                                System.out.print(AC);
                                break;
                            case "2":
                                System.out.print((char) AC);
                                
                                break;
                            default:
                                // Handle other cases if necessary
                        }
                    }
                    break;

                case "10": // AddX
                    AC = AC + X;
                    break;

                case "11": // AddY
                    AC = AC + Y;
                    break;

                case "12": // SubX
                    AC = AC - X;
                    break;

                case "13": // SubY
                    AC = AC - Y;
                    break;

                case "14": // CopyToX
                    X = AC;
                    break;

                case "15": // CopyFromX
                    AC = X;
                    break;

                case "16": // CopyToY
                    Y = AC;
                    break;

                case "17": // CopyFromY
                    AC = Y;
                    break;

                case "18": // CopyToSP
                    SP = AC;
                    break;

                case "19": // CopyFromSP
                    AC = SP;
                    break;

                case "23": // call address
                    if (++i < Memory.length) {
                        returnAddress = i + 1; // Store the return address
                        i = Memory[i] - 1; // Jump to the specified address
                    }
                    break;

                case "24": // return address
                    i = returnAddress - 1; // Jump back to the return address
                    break;

                case "25": // IncX
                    X++;
                    break;

                case "26": // DecX
                    X--;
                    break;

                case "27": // Push
                    break;

                case "28": // Pop
                    break;

                case "29": // Int System Call
                    break;

                case "30": // IRet Return from system call
                    break;

                case "50":
                    return;

                default:
                    System.out.println("Not a recognized command: " + line);
            }
            i++;
            
        }
    }

    public void FileRead(String fileName) {
        try {
            File file = new File(fileName);
            Scanner scan = new Scanner(file);

            int i = 0;
            while (scan.hasNextLine() && i < Memory.length) {
                String line = scan.nextLine().trim();

                String[] parts = line.split("//");
                if (parts.length > 0) {
                    line = parts[0].trim();
                }

                Memory[i++] = Integer.parseInt(line);
            }

            scan.close();
            ParentFunction();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error parsing instruction: " + e.getMessage());
        }
    }

   

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Project1 <input_file>");
            System.exit(1);
        }
        String fileName = args[0];

        Project1 project = new Project1();
        project.FileRead(fileName);
    }

}
