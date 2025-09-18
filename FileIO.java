import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * FileIO handles all input and output for the assembler.
 * - Reads source assembly (.asm) files
 * - Writes listing (.lst) files
 * - Writes load (.load) files
 */
public class FileIO {

    /**
     * Reads all lines from the given source file (.asm).
     * Each line is kept exactly as written, including spaces and comments.
     *
     * @param fileName the name of the assembly source file
     * @return list of lines in the file
     * @throws IOException if the file cannot be found or read
     */
    public static List<String> readSourceFile(String fileName) throws IOException {
        Path path = Paths.get(fileName);

        if (!Files.exists(path)) {
            throw new FileNotFoundException("Source file not found: " + fileName);
        }

        // Read every line into a List<String>
        return Files.readAllLines(path);
    }

    /**
     * Writes the assembler listing (.lst) file.
     * The listing file usually shows:
     *   - memory address (octal)
     *   - machine code (octal)
     *   - original source line
     *
     * @param fileName the output file name (should end with .lst)
     * @param lines    the formatted listing lines
     * @throws IOException if the file cannot be written
     */
    public static void writeListingFile(String fileName, List<String> lines) throws IOException {
        Path path = Paths.get(fileName);
        Files.write(path, lines);
    }

    /**
     * Writes the assembler load file (.load).
     * The load file usually contains only:
     *   - memory address (octal)
     *   - machine code (octal)
     * one pair per line, ready for the simulator to load.
     *
     * @param fileName the output file name (should end with .load)
     * @param lines    the load lines
     * @throws IOException if the file cannot be written
     */
    public static void writeLoadFile(String fileName, List<String> lines) throws IOException {
        Path path = Paths.get(fileName);
        Files.write(path, lines);
    }

    // A small demo to show how to use the FileIO methods
    public static void main(String[] args) {
        try {
            // Example: read an .asm file
            List<String> source = readSourceFile("test.asm");
            System.out.println("Source file lines:");
            for (String line : source) {
                System.out.println(line);
            }

            // Example: write a fake listing file
            List<String> listing = new ArrayList<>();
            listing.add("000006 003417 LDR 3,0,15");
            listing.add("000007 000000 HLT");
            writeListingFile("test.lst", listing);

            // Example: write a fake load file
            List<String> load = new ArrayList<>();
            load.add("000006 003417");
            load.add("000007 000000");
            writeLoadFile("test.load", load);

            System.out.println("\nFiles test.lst and test.load written successfully.");

        } catch (IOException e) {
            System.err.println("I/O Error: " + e.getMessage());
        }
    }
}

// How to run just your I/O part
//  Compile:  javac FileIO.java
//  Run:   java FileIO





//The reason you don’t see octal addresses/machine codes is because your assembler teammates need to plug their encoding logic into your I/O methods.

//Once the assembler gives you a list of formatted lines 
//("000006 003417 LDR 3,0,15"), your FileIO.writeListingFile() and FileIO.writeLoadFile() will save them exactly as required.



