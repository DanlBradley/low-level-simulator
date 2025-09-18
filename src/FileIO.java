package src;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * src.FileIO handles all input and output for the assembler.
 * - Reads source assembly (.asm) files
 * - Writes listing (.lst) files
 * - Writes load (.load) files
 */
public class FileIO {

    /**
     * Reads all lines from the given source file.
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
     * Writes the file. This works for both listing and load files.
     * @param fileName the output file name
     * @param lines    the formatted lines
     * @throws IOException if the file cannot be written
     */
    public static void writeFile(String fileName, List<String> lines) throws IOException {
        Path path = Paths.get(fileName);
        Files.write(path, lines);
    }
}



