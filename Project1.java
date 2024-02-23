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
        private Scanner inpMem;
        private PrintWriter outMem;
        private int Clock, interruptTimeout;
        private int SP;
        private int AC;
        private int PC;
        private int IR;
        private int X;
        private int Y;
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
                    write(--SP, SPTemporary);
                    write(--SP, PC);
                    write(--SP, IR);
                    write(--SP, AC);
                    write(--SP, X);
                    write(--SP, Y);

                    PC = 1000;
                }
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

                case 1: // Load value; Load the value into the AC.
                    IR = read(PC++); // Reading the next line in the txt file and storing it in the IR
                    AC = IR; // Lets the AC equal the value of IR
                    break;

                case 2: // Load addr; Load the value at the address into the AC
                    IR = read(PC++); // Reading the next line in the txt file and storing it in the IR
                    AC = read(IR); // Lets the AC equal the value at the stored address IR
                    break;

                /*
                 * Load the value from the address found in the given address into the AC (for
                 * example, if LoadInd 500, and 500 contains 100, then load from 100).
                 */
                case 3:
                    IR = read(PC++); // Reading the next line in the txt file and storing it in the IR
                    AC = read(read(IR)); // lets the AC equal the value of the value of the value at the stored address
                                         // IR above
                    break;

                /*
                 * LoadIdxX addr; Load the value at (address+X) into the AC
                 * (for example, if LoadIdxX 500, and X contains 10, then load from 510).
                 */
                case 4:
                    IR = read(PC++); // Reading the next line in the txt file and storing it in the IR
                    AC = read(IR + X); // Equating the AC to the addition of the previously read address and X
                    break;

                case 5: // LoadIdxY addr; Load the value at (address+Y) into the AC
                    IR = read(PC++); // Reading the next line in the txt file and storing it in the IR
                    AC = read(IR + Y); // Equating the AC to the addition of the previously read address and Y
                    break;

                case 6: // LoadSpX; Load from (Sp+X) into the AC (if SP is 990, and X is 1, load from
                        // 991).
                    AC = read(SP + X); // Reading the value from the memory address calculated as SP + X
                    break;

                case 7: // Store addr; Store the value in the AC into the address
                    IR = read(PC++); // Reading the next line in the txt file and storing it in the IR
                    write(IR, AC); // Writing the value of the AC into memory address that IR is equated to from
                                   // the above line
                    break;

                case 8: // Get; Gets a random int from 1 to 100 into the AC
                    AC = (int) (Math.random() * 100 + 1); // Getting our random number 1-100 and putting it into the AC
                    break;

                /*
                 * Put Port; If port=1, writes AC as an int to the screen, If port=2, writes
                 * AC as a char to the screen
                 */
                case 9:
                    IR = read(PC++); // Reading the next line in the txt file
                    switch(IR){
                        case 1: //If the next line = 1, print it out as a int
                            System.out.print(AC);
                            break;
                        case 2: //If the next line = 2, print it out as a char
                            System.out.print((char) AC);
                            break;
                        default: //If the next line is none of the above, return error
                            System.out.println("error");
                    }

                case 10: // AddX; Add the value in X to the AC.
                    AC = AC + X; // Setting the AC to the value of AC plus X
                    break;

                case 11: // AddY; Add the value in Y to the AC.
                    AC = AC + Y; // Setting the AC to the value of AC plus Y
                    break;

                case 12: // SubX; Subtract the value in X from the AC.
                    AC = AC - X; // Setting the AC to the value of AC minus X
                    break;

                case 13: // SubY; Subtract the value in Y from the AC.
                    AC = AC - Y; // Setting the AC to the value of AC minus Y
                    break;

                case 14: // CopyToX; Copy the value in the AC to X.
                    X = AC; // Setting X to the value of the AC
                    break;

                case 15: // CopyFromX; Copy the value in X to the AC.
                    AC = X; // Setting the AC to the value of X
                    break;

                case 16: // CopyToY; Copy the value in the AC to Y.
                    Y = AC; // Setting Y to the value of the AC
                    break;

                case 17: // CopyFromY; Copy the value in Y to the AC.
                    AC = Y; // Setting the AC to the value of Y
                    break;

                case 18: // CopyToSP; Copy the value in AC to the SP.
                    SP = AC; // Setting the SP to the value of the AC
                    break;

                case 19: // CopyFromSP; Copy the value in SP to the AC.
                    AC = SP; // Setting the AC to the value of SP
                    break;

                case 20: // Jump addr; Jump to the address.
                    IR = read(PC++); // Reading the value at the memory address specified by the PC and putting it in
                                     // the IR
                    PC = IR; // Jumping to that address specified in the last line by equating the PC to the
                             // new IR
                    break;

                case 21: // JumpIfEqual addr; Jump to the address only if the value in the AC is zero
                    IR = read(PC++); // Reading the value at the memory address specified by the PC and putting it in
                                     // the IR
                    if (AC == 0) // If the AC does equals zero, get that address from the last line into the PC
                                 // (jumping back)
                        PC = IR;
                    break;

                case 22: // JumpIfNotEqual addr; Jump to the address only if the value in the AC is not
                         // zero
                    IR = read(PC++); // Reading the value at the memory address specified by the PC and putting it in
                                     // the IR
                    if (AC != 0) 
                        PC = IR;
                    break;

                case 23: // Call addr; Push return address onto stack, jump to the address.
                    IR = read(PC++); // Reads the instruction from memory at the PC and inc's the PC by 1.
                    write(--SP, PC); // Pushes the address of the next instruction onto the stack.
                    PC = IR; // Setting the PC to the address specified in the instruction.
                    break;

                case 24: // Ret; Pop return address from the stack, jump to the address.
                    PC = read(SP++);
                    ; // Execution returns to the last value before the push of the address onto the
                      // stack
                    break;

                case 25: // IncX; Increment the value in X.
                    X = X + 1; // Add 1 to X
                    break;

                case 26: // DecX; Decrement the value in X.
                    X = X - 1; // Subtract 1 from X
                    break;

                case 27: // Push; Push AC onto stack.
                    write(--SP, AC); // Calls the push of the value at AC onto the stack
                    break;

                case 28: // Pop; Pop from stack into AC.
                    AC = read(SP++); // AC is equal to the top of the stack
                    break;

                case 29: // Int; Perform system call. Enter's the kernel and sets the execution at
                         // address 1500
                    if (!Kernel) {

                        Kernel = true; // Flagging to enter the kernel
                        // Saving the stack pointer (2000 indicates the start of the kernel mode stack
                        // (top of it))
                        int SPTemporary = SP;
                        SP = 2000;

                        // Saving the values of the PC, IR, AC, X, and Y onto the stack. Each is
                        // decremented by the stack pointer position '--SP' to ensure that they are
                        // stored sequentially in the memory
                        write(--SP, SPTemporary);
                        write(--SP, PC);
                        write(--SP, IR);
                        write(--SP, AC);
                        write(--SP, X);
                        write(--SP, Y);

                        PC = 1500; // executation is at address is 1500
                    }
                    break;

                case 30: // IRet; Return from system call. Essentially removes everything from the stack
                         // and returns from the kernel

                    Y = read(SP++); // Read the value from the top of the stack and assign it to the Y register
                    X = read(SP++); // Read the value from the top of the stack and assign it to the X register
                    AC = read(SP++); // Read the value from the top of the stack and assign it to the AC register
                    IR = read(SP++); // Read the value from the top of the stack and assign it to the Instruction
                                     // Register (IR)
                    PC = read(SP++); // Read the value from the top of the stack and assign it to the Program Counter
                                     // (PC)
                    SP = read(SP++); // Read the value from the top of the stack and assign it to the Stack Pointer
                                     // (SP), effectively removing everything from the stack

                    Kernel = false; // Set the Kernel mode flag to false, indicating that the CPU is no longer in
                                    // kernel mode
                    break;

                case 50: // End's the execution
                    outMem.println("e");
                    outMem.flush();
                    return false;

                default: // If the user submits a program without the correct instructions, the following
                         // happens
                    System.out.println("Instruction you typed in is NOT valid."); // Returns error statement
                    outMem.println("e");
                    outMem.flush();
                    return false;
            }
            return true;
        }
    }

    /*
     * Implements the runnable interface to handle the error stream asynchronously
     */
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
            @SuppressWarnings("deprecation")
            Process MemProc = runningTime.exec("java Mem " + txtInput);

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