// Max Lichter
// CS 4348.004 Project 1
// Professor Ozbirn
// Due: 3/2/24

//Importing all of my libaries that I'll be using throughout the program
import java.io.*;
import java.util.Scanner;

//Creating my project titled class
public class Project1 {

    private static class CPU {

        // Creating all of my variables I will be using throughout the program
        private final Scanner inpMem;
        private final PrintWriter outMem;
        private int Clock, interruptTimeout, SP, AC, PC, IR, X, Y;
        private boolean Kernel;

        // Constructor that intializes the CPU object with the provided memory input,
        // memory output, and interrupt timeout values
        public CPU(int interruptTimeout, PrintWriter outMem, Scanner inpMem) {

            this.interruptTimeout = interruptTimeout; // Represents the duration before an interrupt occurs

            // These two are used to interact with the memory component
            this.outMem = outMem;
            this.inpMem = inpMem;

            Kernel = false; // Indicates that the CPu starts in the User mode

            // Initializing all of my CPU states to zero
            AC = 0;
            X = 0;
            Y = 0;
            PC = 0;
            IR = 0;
            Clock = 0;

            SP = 1000; // Kernel mode entry point is 1000
        }

        // Method responsible for effectively controlling the execution flow of the CPU
        public void Exe() {

            boolean Transmitting = true; // Indicating the CPU execution loop to start

            // Execution loop
            while (Transmitting) {
                IR = read(PC++); // Reads the next value of the PC into the IR
                Transmitting = instructionExe(); // Uses the stored instruction from the IR runs the instruction
                Clock++; // Increments the number of cycles executed by the CPU

                // If we're not in the kernel and the clock has a higher value then the
                // interrupt int, reset the clock back to 0, enter the kernel, and set the PC to
                // 1000
                if (Kernel == false && Clock >= interruptTimeout) {
                    Clock = 0;

                    Kernel = true; // Flagging to enter the kernel
                    // Saving the stack pointer (2000 indicates the start of the kernel mode stack
                    // (top of it))
                    int SPTemporary = SP;
                    SP = 2000;

                    // Saving the values of the PC, IR, AC, X, and Y onto the stack. Each is
                    // decremented by the stack pointer position '--SP' to ensure that they are
                    // stored sequentially in the memory
                    writeStack(--SP, SPTemporary, PC, IR, AC, X, Y);

                    PC = 1000;
                }
            }
        }

        // Writes x amount of values to the stack
        public void writeStack(int... values) {
            for (int value : values) {
                write(--SP, value);
            }
        }

        // Method responsible for sneding a write command to the memory process with the
        // specified address and data
        public void write(int address, int data) {

            outMem.printf("w%d,%d\n", address, data); // Writes the command "w" followed by the integers of address and
                                                      // data. "w" is used in the Memory Java file
            outMem.flush(); // Ensures that any buffered data is immediately sent over the stream

        }

        // Method responsible for reading data from the memory process at the specified
        // memory address
        public int read(int address) {

            // if the address is more than 1k AND not in kernel mode, print out this error
            // message
            if (Kernel == false && address >= 1000) {
                System.err.println("Memory violation: accessing system address 1000 in user mode ");
                System.exit(-1);
            }

            // Sending the "r" command (from the Memory java file) which indicates a read
            // option with the memory address specified and sends it to the outMem
            // printwriter
            outMem.println("r" + address);
            outMem.flush(); // Ensures that any buffered data is immediately sent over the stream

            // Reads the sponse from the memory process
            return Integer.parseInt(inpMem.nextLine());
        }

        public boolean instructionExe() {
            switch (IR) {

                // Load value; Load the value into the AC.
                case 1: {
                    IR = read(PC++); // Reading the next line in the txt file and storing it in the IR
                    AC = IR;
                    break;
                } // Lets the AC equal the value of IR

                // Load addr; Load the value at the address into the AC
                case 2: {
                    IR = read(PC++); // Reading the next line in the txt file and storing it in the IR
                    AC = read(IR);
                    break;
                } // Lets the AC equal the value at the stored address IR

                // LoadInd addr; Load the value from the address found in the given address into
                // the AC
                case 3: {
                    IR = read(PC++); // Reading the next line in the txt file and storing it in the IR
                    AC = read(read(IR));
                    break;
                } // Lets the AC equal the value of the value of the value at the stored address

                // LoadIdxX addr; Load the value at (address+X) into the AC
                case 4: {
                    IR = read(PC++); // Reading the next line in the txt file and storing it in the IR
                    AC = read(IR + X);
                    break;
                } // Equating the AC to the addition of the previously read address and X

                // LoadIdxY addr; Load the value at (address+Y) into the AC
                case 5: {
                    IR = read(PC++); // Reading the next line in the txt file and storing it in the IR
                    AC = read(IR + Y);
                    break;
                } // Equating the AC to the addition of the previously read address and Y

                // LoadSpX; Load from (Sp+X) into the AC
                case 6: {
                    AC = read(SP + X);
                    break;
                } // Reading the value from the memory address calculated as SP + X

                // Store addr; Store the value in the AC into the address
                case 7: {
                    IR = read(PC++); // Reading the next line in the txt file and storing it in the IR
                    write(IR, AC);
                    break;
                } // Writing the value of the AC into memory address that IR is equated to from

                // Get; Gets a random int from 1 to 100 into the AC
                case 8: {
                    AC = (int) (Math.random() * 100 + 1);
                    break;
                } // Getting our random number 1-100 and putting it into the AC

                // Put Port; If port=1, writes AC as an int to the screen, If port=2, writes AC
                // as a char to the screen
                case 9: {
                    IR = read(PC++); // Reading the next line in the txt file
                    switch (IR) {
                        case 1:{
                            System.out.print(AC); // If the next line = 1, print it out as a int
                            break;
                        }
                        
                        case 2: { 
                            System.out.print((char) AC); // If the next line = 2, print it out as a char
                            break;
                        }
                            
                        default: {
                            System.out.println("error"); // If the next line is none of the above, return error
                        }
                    }
                }

                // AddX; Add the value in X to the AC.
                case 10: {
                    AC = AC + X;
                    break;
                } // Setting the AC to the value of AC plus X

                // AddY; Add the value in Y to the AC.
                case 11: {
                    AC = AC + Y;
                    break;
                } // Setting the AC to the value of AC plus Y

                // SubX; Subtract the value in X from the AC.
                case 12: {
                    AC = AC - X;
                    break;
                } // Setting the AC to the value of AC minus X

                // SubY; Subtract the value in Y from the AC.
                case 13: {
                    AC = AC - Y;
                    break;
                } // Setting the AC to the value of AC minus Y

                // CopyToX; Copy the value in the AC to X.
                case 14: {
                    X = AC;
                    break;
                } // Setting X to the value of the AC

                // CopyFromX; Copy the value in X to the AC.
                case 15: {
                    AC = X;
                    break;
                } // Setting the AC to the value of X

                // CopyToY; Copy the value in the AC to Y.
                case 16: {
                    Y = AC;
                    break;
                } // Setting Y to the value of the AC

                // CopyFromY; Copy the value in Y to the AC.
                case 17: {
                    AC = Y;
                    break;
                } // Setting the AC to the value of Y

                // CopyToSP; Copy the value in AC to the SP.
                case 18: {
                    SP = AC;
                    break;
                } // Setting the SP to the value of the AC

                // CopyFromSP; Copy the value in SP to the AC.
                case 19: {
                    AC = SP;
                    break;
                } // Setting the AC to the value of SP

                /*
                 * Jump addr; Jump to the address.
                 * Reading the val at the mem address specified by the PC and putting it in the
                 * IR
                 * Jumping to that addy specified in the last line by equating the PC to the new
                 * IR
                 */
                case 20: {
                    IR = read(PC++);
                    PC = IR;
                    break;
                }

                /*
                 * JumpIfEqual addr; Jump to the address only if the value in the AC is zero
                 * Reading the val at the mem address specified by the PC and putting it in the
                 * IR
                 * If the AC does equals zero, get that address from the last line into the PC
                 * (jumping back)
                 */
                case 21: {
                    IR = read(PC++);
                    if (AC == 0)
                        PC = IR;
                    break;
                }

                /*
                 * JumpIfNotEqual addr; Jump to the address only if the value in the AC is not
                 * zero
                 * Reading the val at the mem address specified by the PC and putting it in the
                 * IR
                 * If the AC != zero, get that address from the last line into the PC (jumping
                 * back)
                 */
                case 22: {
                    IR = read(PC++);
                    if (AC != 0)
                        PC = IR;
                    break;
                }

                /*
                 * Call addr; Push return address onto stack, jump to the address.
                 * Reads the instruction from memory at the PC and inc's the PC by 1.
                 * Pushes the address of the next instruction onto the stack.
                 * Setting the PC to the address specified in the instruction.
                 */
                case 23: {
                    IR = read(PC++);
                    write(--SP, PC);
                    PC = IR;
                    break;
                }

                /*
                 * Ret; Pop return address from the stack, jump to the address.
                 * Execution returns to the last value before the push of the address onto the
                 * stack
                 */
                case 24: {
                    PC = read(SP++);
                    break;
                }

                // IncX; Increment the value in X.
                case 25: {
                    X = X + 1;
                    break;
                } // Add 1 to X

                // DecX; Decrement the value in X.
                case 26: {
                    X = X - 1;
                    break;
                } // Subtract 1 from X

                // Push; Push AC onto stack.
                case 27: {
                    write(--SP, AC);
                    break;
                } // Calls the push of the value at AC onto the stack

                // Pop; Pop from stack into AC.
                case 28: {
                    AC = read(SP++);
                    break;
                } // AC is equal to the top of the stack

                case 29: { // Int; Perform system call. Enter's the kernel and sets the execution at
                             // address 1500
                    if (!Kernel) {

                        // Flagging to enter the kernel
                        Kernel = true;

                        /*
                         * Saving the stack pointer (2000 indicates the start of the kernel mode stack
                         * (top of it))
                         */
                        SP = 2000;

                        /*
                         * Saving the values of the PC, IR, AC, X, and Y onto the stack. Each is
                         * decremented by the stack pointer position '--SP' to ensure that they are
                         * stored sequentially in the memory
                         */
                        writeStack(SP, PC, IR, AC, X, Y);

                        PC = 1500; // executation is at address is 1500
                    }
                    break;
                }

                case 30: { // IRet; Return from system call. Essentially removes everything from the stack
                             // and returns from the kernel
                    Y = read(SP++); // Read the val from the top of the stack and assign it to the Y register
                    X = read(SP++); // Read the val from the top of the stack and assign it to the X register
                    AC = read(SP++); // Read the val from the top of the stack and assign it to the AC register
                    IR = read(SP++); // Read the val from the top of the stack and assign it to the Inst Reg (IR)
                    PC = read(SP++); // Read the val from the top of the stack and assign it to the Program Counter
                    SP = read(SP++); /*
                                      * Read the value from the top of the stack and assign it to the Stack Pointer
                                      * (SP), effectively removing everything from the stack
                                      */
                    Kernel = false; // Set the Kernel mode flag to false, indicating that the CPU is no longer in
                                    // Kernel mode
                    break;
                }

                case 50: { // End the execution
                    outMem.println("e");
                    outMem.flush();
                    return false;
                }

                default: { // Invalid instruction
                    System.out.println("Invalid instruction: " + IR);
                    outMem.println("e");
                    outMem.flush();
                    return false;
                }
            }
            return true;
        }

    }

    static class Mem {

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
            case '.':
                index = Integer.parseInt(line.substring(1).split("\\s+")[0]);
                continue;

            // If the first char is a digit (0-9), it indicates to be stored in the memory
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                String[] splitLine = line.split("\\s+"); // Splitting the line by whitespace

                // Checking to see if there is at least one element after splitting. If there
                // is, parse the first element of the data and store it in memory at the current
                // index
                if (splitLine.length >= 1) {
                    Memory[index++] = Integer.parseInt(splitLine[0]);
                }
                break;
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
                        case 'r': {

                            // This line extracts the address from the rest of the line
                            int address = Integer.parseInt(nextLine.substring(1));

                            // reading from the memory at the specified address and printing out the results
                            System.out.println(read(address));
                            break;
                        }

                        // If the command is 'w', it indicates a write operation
                        case 'w': {

                            // Splitting the line to extract parameters
                            String[] params = nextLine.substring(1).split(",");

                            // Writing the data to the specified memory address
                            write(Integer.parseInt(params[0]), Integer.parseInt(params[1]));
                            break;
                        }

                        // If the command is 'e', it indicates a exit operation
                        case 'e': {

                            // Printing a message indicating the exitting of the program and then exits with
                            // a successful status code (0)
                            System.out.println("Exiting the memory program.");
                            System.exit(0);
                            break;
                        }

                        // If the command isn't recognized, print an error message
                        default: {
                            System.out.println("Invalid command.");
                            break;
                        } 
                    }
                }

                // Exception handling for NumberFormatException and FileNotFoundException
            } catch (NumberFormatException | FileNotFoundException e) {
                e.printStackTrace(); // Printing out the stack trace of the exception
            }
        }
    }

    // Implements the runnable interface to handle the error stream asynchronously
    static class ErrorStreamHandler implements Runnable {

        private final InputStream errorFlow; // This InputStream is to read the error stream from the process

        /*
         * This constructor initializes the ErrorStreamHandler with an InputStream
         * errorFlow is the input stream that reads error messages from
         */
        public ErrorStreamHandler(InputStream errorFlow) {
            this.errorFlow = errorFlow;
        }

        /*
         * Method that runs ErrorStreamHandler thread. It reads lines from the error
         * stream and then
         * prints them to the standard error output
         */
        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(errorFlow))) {

                String line;

                // Read's lines from the error stream until there are not more lines
                while ((line = reader.readLine()) != null) {
                    System.err.println(line); // Print's out each line
                }
            }

            catch (IOException Exception) {
                Exception.printStackTrace(); // If there is an IOException, print the stack trace
            }
        }
    }

    // My main method of my program which serves as the entry point for execution
    public static void main(String[] args) {

        /*
         * Setting the argument parameters. After typing "javac Project1.java", the user
         * will need to type "java Project1 txtInput Interrupt". txtInput would be
         * replaced by the txt file the user is using (ex. sample1.txt
         * and Interrupt would be replaced by the integer that the user wants to set the
         * timer interrupt value at (ex. 30).
         */
        String txtInput = args[0];
        int Interrupt = Integer.parseInt(args[1]);

        /*
         * This line essentially initiates a new process to run the 'Memory' class with
         * the input file the user provides.
         */
        Runtime runningTime = Runtime.getRuntime();

        try {

            /*
             * Since the exec() command it outdated in current java, we need to ignore the
             * warning provided. "@SuppressWarnings("deprecation")" is basically just a
             * warning the program gives. The line below it calls MemProc to be the new
             * process created.
             */
            Process MemProc = runningTime.exec("java Project1$Mem " + txtInput);

            /*
             * First line retrieves the error stream that is associated with the MemProc
             * process. Basically
             * it takes in if there are any errors. The line under that one creates a new
             * thread that is responsible
             * for handling our error stream. The final line starts the thread created in
             * the previous line (which will
             * be executed asynchronously).
             */
            InputStream errorStream = MemProc.getErrorStream();
            Thread errorThread = new Thread(new ErrorStreamHandler(errorStream));
            errorThread.start();

            /*
             * First line creates a scanner for our memory input (which allows reading data
             * from the process) that's
             * connected to MemProc. The Second line creates a printwriter which writes from
             * the Memproc process to create
             * formatted text to a character output stream.
             */
            PrintWriter memOut = new PrintWriter(MemProc.getOutputStream());
            Scanner memInp = new Scanner(MemProc.getInputStream());

            /*
             * Creating a new instance of my CPU class with three parameters for execution,
             * then executing it
             * with the execute() method (execut() contains the logic for emulating our
             * CPU/instruction/memory handling/
             * interrupt handling.
             */
            CPU newCPU = new CPU(Interrupt, memOut, memInp);
            newCPU.Exe();

        }

        /*
         * Catching IOExceptions that occur doing the intitation of the process. If
         * errors occur, the following message will print
         */
        catch (IOException Exception) {

            Exception.printStackTrace(); // Prints the stack trace of the caught exception
            System.err.println("Initiating the process did NOT work.");
            System.exit(1);
        }
    }
}
