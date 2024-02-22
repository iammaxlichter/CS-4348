import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Memory {

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
						if (line.startsWith(".")) {
							index = Integer.parseInt(line.substring(1).split("\\s+")[0]);
						} else if (Character.isDigit(line.charAt(0))) {
							String[] splitLine = line.split("\\s+");
							if (splitLine.length >= 1) {
								Memory[index++] = Integer.parseInt(splitLine[0]);
							}
						}
					}
				}
				fileScanner.close();
			} catch (NumberFormatException | FileNotFoundException e) {
				e.printStackTrace();
			}
			while (sc.hasNextLine()) {
			
			String nextLine = sc.nextLine();
			char nextCommand = nextLine.charAt(0);

			switch (nextCommand) {
				case 'r':
					int address = Integer.parseInt(nextLine.substring(1));
					System.out.println(read(address));
					break;
			
				case 'w':
					String[] params = nextLine.substring(1).split(",");
					write(Integer.parseInt(params[0]), Integer.parseInt(params[1]));
					break;
			
				case 'e':
					System.out.println("Exiting the memory program.");
					System.exit(0);
			
				default:
					System.out.println("Invalid command.");
			}
		}
		sc.close();
	}
}