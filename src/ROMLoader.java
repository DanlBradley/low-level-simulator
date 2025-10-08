package src;
import java.io.*;
import java.util.Scanner;

public class ROMLoader {

    public static void loadProgram(Memory memory, String filename) {

        try {
            Scanner scanner = new Scanner(new File(filename));
            System.out.println("Loading ROM from file: " + filename);
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if(line.isEmpty() || line.startsWith("#") || line.startsWith(";")) continue;

                String[] parts = line.split("\\s+");
                if(parts.length >= 2) {
                    int address = Integer.parseInt(parts[0], 8);
                    short instruction = (short)Integer.parseInt(parts[1], 8);

                    memory.load(address, instruction);
                }
//                System.out.println(line);
                int address = Integer.parseInt(parts[0], 8);
                int value = Integer.parseInt(parts[1], 8);
                System.out.println("Loading: addr=" + address + " (octal: " + parts[0] +
                        "), value=" + value + " (octal: " + parts[1] + ")");
            }
            scanner.close();


        } catch(Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}