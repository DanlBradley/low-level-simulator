package src;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Assembler {

    // this is a mapping of labels and their corresponding locations, generated in 1st pass and used in 2nd pass
    private static Map<String, Integer> labels = new HashMap<>();

    // current location (where addresses should be saved/operated on etc.)
    private static int currentLoc = 0;

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java src.Assembler <assembly.txt> <listing.txt> <load.txt>");
            System.exit(1);
        }

        // Allows user to specify input and output files
        String assemblyFile = args[0];
        String listingFile = args[1];
        String loadFile = args[2];

        try {
            assemble(assemblyFile, listingFile, loadFile);
            System.out.println("Listing file: " + listingFile);
            System.out.println("Load file: " + loadFile);
        } catch (Exception e) {
            System.err.println("Assembly failed: " + e.getMessage());
        }
    }

    /**
     * Assembles the input file into the output file.
     * @param assemblyFile
     * @param listingFile
     * @param loadFile
     * @throws IOException
     */
    public static void assemble(String assemblyFile,
                                String listingFile,
                                String loadFile) throws IOException {
        // Read all lines from input file
        List<String> inputLines = FileIO.readSourceFile(assemblyFile);

        // first pass - build the labels (for ex. a label might be referenced before it is defined)
        buildLabels(inputLines);

        // second pass - process lines as a list of strings (for both listing and load files)
        currentLoc = 0; // Reset location counter
        List<String> listingOutput = new ArrayList<>();
        List<String> loadOutput = new ArrayList<>();

        for (String line : inputLines) {
            ProcessedLine result = processLine(line.trim());

            if (result.listingLine != null) {
                listingOutput.add(result.listingLine);
            }

            if (result.loadLine != null) {
                loadOutput.add(result.loadLine);
            }
        }

        FileIO.writeFile(listingFile, listingOutput);
        FileIO.writeFile(loadFile, loadOutput);
    }

    private static void buildLabels(List<String> inputLines) {
        labels.clear();
        currentLoc = 0;

        for (String line : inputLines) {
            line = line.trim();

            // Skip empty lines and comment-only lines
            if (line.isEmpty() || line.startsWith(";")) {
                continue;
            }

            // Handle labels
            if (line.contains(":")) {
                String label = line.substring(0, line.indexOf(":")).trim();
                labels.put(label, currentLoc);
                line = line.substring(line.indexOf(":") + 1).trim();
            }

            // Remove comments for processing
            if (line.contains(";")) {
                line = line.substring(0, line.indexOf(";")).trim();
            }

            // Handle directives that affect location counter
            if (line.startsWith("LOC")) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 2) {
                    currentLoc = Integer.parseInt(parts[1]);
                }
            } else if (line.startsWith("Data") || (!line.isEmpty() && !line.startsWith("LOC"))) {
                // Data directive or instruction - increment location
                currentLoc++;
            }
        }
    }

    /**
     * Helper class to store the result of processing a line for both the listing and load files.
     */
    private static class ProcessedLine {
        String listingLine;
        String loadLine;

        ProcessedLine(String listingLine, String loadLine) {
            this.listingLine = listingLine;
            this.loadLine = loadLine;
        }
    }

    private static ProcessedLine processLine(String line) {
        // Skip empty lines and comment-only lines
        if (line.isEmpty() || line.startsWith(";")) {
            return new ProcessedLine(null, null);
        }

        // remove label segment
        String workingLine = line;
        if (line.contains(":")) {
            workingLine = line.substring(line.indexOf(":") + 1).trim();

            if (workingLine.isEmpty() || workingLine.startsWith(";")) {
                return new ProcessedLine(null, null);
            }
        }

        // strip out comments
        String instructionPart = workingLine;
        if (workingLine.contains(";")) {
            instructionPart = workingLine.substring(0, workingLine.indexOf(";")).trim();
        }

        // Handle assembler directives (only increment location with data directives)
        if (instructionPart.startsWith("LOC")) {
            return handleLOC(instructionPart, line);
        }
        if (instructionPart.startsWith("Data")) {
            return handleData(instructionPart, line);
        }

        // handle instructions and increment location
        if (!instructionPart.isEmpty()) {
            try {
                String listingLine = Encoder.encodeInstruction(currentLoc, line, false);
                String loadLine = Encoder.encodeInstruction(currentLoc, line, true);
                currentLoc++;
                return new ProcessedLine(listingLine, loadLine);
            } catch (Exception e) {
                throw new RuntimeException("Error encoding instruction at location " +
                        Encoder.convertToOctal(currentLoc, 6) + ": " + line + " - " + e.getMessage());
            }
        }

        return new ProcessedLine(null, null);
    }

    private static ProcessedLine handleLOC(String cleanLine, String originalLine) {
        String[] parts = cleanLine.split("\\s+");
        if (parts.length < 2) {
            throw new RuntimeException("LOC directive requires a location: " + originalLine);
        }

        try {
            currentLoc = Integer.parseInt(parts[1]);
            // LOC appears in listing but not in load file
            return new ProcessedLine(originalLine, null);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid location in LOC directive: " + originalLine);
        }
    }

    private static ProcessedLine handleData(String cleanLine, String originalLine) {
        String[] parts = cleanLine.split("\\s+");
        if (parts.length < 2) {
            throw new RuntimeException("Data directive requires a value: " + originalLine);
        }

        int dataValue;
        String dataValueStr = parts[1];

        // Handle numeric data
        try {
            dataValue = Integer.parseInt(dataValueStr);
        } catch (NumberFormatException e) {
            // Handle label references
            if (labels.containsKey(dataValueStr)) {
                dataValue = labels.get(dataValueStr);
            } else {
                throw new RuntimeException("Undefined label in Data directive: " +
                        dataValueStr + " in line: " + originalLine);
            }
        }

        String locationOctal = Encoder.convertToOctal(currentLoc, 6);
        String dataOctal = Encoder.convertToOctal(dataValue, 6);
        currentLoc++;

        String listingLine = locationOctal + " " + dataOctal + " " + originalLine;
        String loadLine = locationOctal + " " + dataOctal;

        return new ProcessedLine(listingLine, loadLine);
    }
}