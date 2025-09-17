package src;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Assembler {

    private static Map<String, Integer> symbolTable = new HashMap<>();
    private static int locationCounter = 0;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java src.main.java.Assembler <input.txt> <output.txt>");
            System.exit(1);
        }

        String inputFile = args[0];
        String outputFile = args[1];

        try {
            assemble(inputFile, outputFile);
            System.out.println("Output written to: " + outputFile);
        } catch (Exception e) {
            System.err.println("Assembly failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void assemble(String inputFile, String outputFile) throws IOException {
        // First pass: scan for labels and build symbol table
        buildSymbolTable(inputFile);

        // Second pass: generate code
        locationCounter = 0; // Reset location counter
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String processedLine = processLine(line.trim(), writer);
                if (processedLine != null && !processedLine.isEmpty()) {
                    writer.println(processedLine);
                }
            }
        }
    }

    private static void buildSymbolTable(String inputFile) throws IOException {
        symbolTable.clear();
        locationCounter = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Skip empty lines and comment-only lines
                if (line.isEmpty() || line.startsWith(";")) {
                    continue;
                }

                // Handle labels
                if (line.contains(":")) {
                    String label = line.substring(0, line.indexOf(":")).trim();
                    symbolTable.put(label, locationCounter);
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
                        locationCounter = Integer.parseInt(parts[1]);
                    }
                } else if (line.startsWith("Data") || (!line.isEmpty() && !line.startsWith("LOC"))) {
                    // Data directive or instruction - increment location
                    locationCounter++;
                }
            }
        }
    }

    private static String processLine(String line, PrintWriter writer) {
        // Skip empty lines and comment-only lines
        if (line.isEmpty() || line.startsWith(";")) {
            return null;
        }

        // Handle labels - extract and store in symbol table (already done in pass 1)
        String workingLine = line;
        if (line.contains(":")) {
            workingLine = line.substring(line.indexOf(":") + 1).trim();

            // If line is just a label, don't process further
            if (workingLine.isEmpty() || workingLine.startsWith(";")) {
                return null;
            }
        }

        // Get the instruction/directive part (before any comment)
        String instructionPart = workingLine;
        if (workingLine.contains(";")) {
            instructionPart = workingLine.substring(0, workingLine.indexOf(";")).trim();
        }

        // Handle assembler directives
        if (instructionPart.startsWith("LOC")) {
            return handleLOC(instructionPart, line);
        }

        if (instructionPart.startsWith("Data")) {
            return handleData(instructionPart, line);
        }

        // Handle instructions, calling src.main.java.Encoder class
        if (!instructionPart.isEmpty()) {
            try {
                String encodedLine = Encoder.encodeInstruction(locationCounter, line);
                locationCounter++;
                return encodedLine;
            } catch (Exception e) {
                throw new RuntimeException("Error encoding instruction at location " +
                        Encoder.convertToOctal(locationCounter, 6) + ": " + line + " - " + e.getMessage());
            }
        }

        return null;
    }

    private static String handleLOC(String cleanLine, String originalLine) {
        String[] parts = cleanLine.split("\\s+");
        if (parts.length < 2) {
            throw new RuntimeException("LOC directive requires a location: " + originalLine);
        }

        try {
            locationCounter = Integer.parseInt(parts[1]);
            return originalLine; // LOC directives appear in output without encoding
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid location in LOC directive: " + originalLine);
        }
    }

    private static String handleData(String cleanLine, String originalLine) {
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
            // Handle label references (for forward references, this will fail in single-pass)
            if (symbolTable.containsKey(dataValueStr)) {
                dataValue = symbolTable.get(dataValueStr);
            } else {
                throw new RuntimeException("Undefined label in Data directive (forward references not supported): " +
                        dataValueStr + " in line: " + originalLine);
            }
        }

        String locationOctal = Encoder.convertToOctal(locationCounter, 6);
        String dataOctal = Encoder.convertToOctal(dataValue, 6);
        locationCounter++;

        return locationOctal + " " + dataOctal + " " + originalLine;
    }
}