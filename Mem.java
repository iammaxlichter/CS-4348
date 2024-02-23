import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Mem {

    static int[] Memory;

    public static int read(int address) {
        return Memory[address];
    }

    public static void write(int address, int data) {
        Memory[address] = data;
    }

    public static void main(String[] args) {

        String txtFile = args[0];
        Scanner sc = new Scanner(System.in);

        Memory = new int[2000];
        try (Scanner fileScanner = new Scanner(new File(txtFile))) {
            int index = 0;

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();

                if (!line.isEmpty()) {
                    switch (line.charAt(0)) {
                        case '.' -> index = Integer.parseInt(line.substring(1).split("\\s+")[0]);
                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                            String[] splitLine = line.split("\\s+");
                            if (splitLine.length >= 1) {
                                Memory[index++] = Integer.parseInt(splitLine[0]);
                            }
                        }
                    }
                }
            }
        } catch (NumberFormatException | FileNotFoundException e) {
            e.printStackTrace();
        }

        while (sc.hasNextLine()) {
            String nextLine = sc.nextLine();
            char nextCommand = nextLine.charAt(0);

            switch (nextCommand) {
                case 'r' -> {
                    int address = Integer.parseInt(nextLine.substring(1));
                    System.out.println(read(address));
                }
                case 'w' -> {
                    String[] params = nextLine.substring(1).split(",");
                    write(Integer.parseInt(params[0]), Integer.parseInt(params[1]));
                }
                case 'e' -> {
                    System.out.println("Exiting the memory program.");
                    System.exit(0);
                }
                default -> System.out.println("Invalid command.");
            }
        }
        sc.close();
    }
}