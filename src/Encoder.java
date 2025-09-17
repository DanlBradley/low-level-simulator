package src;

import java.util.HashMap;
import java.util.Map;

public class Encoder {

    // Opcode lookup table for O(1) opcode value retrieval
    private static Map<String, Integer> opcodeMap;

    static {
        opcodeMap = new HashMap<>();

        // Miscellaneous Instructions (pp. 6 of C6461 Spec)
        opcodeMap.put("HLT", 0);
        opcodeMap.put("TRAP", 30);

        // Load/Store Instructions (pp. 8 of C6461 Spec)
        opcodeMap.put("LDR", 1);
        opcodeMap.put("STR", 2);
        opcodeMap.put("LDA", 3);
        opcodeMap.put("LDX", 41);
        opcodeMap.put("STX", 42);

        // Transfer Instructions (pp. 9 of C6461 Spec)
        opcodeMap.put("JZ", 10);
        opcodeMap.put("JNE", 11);
        opcodeMap.put("JCC", 12);
        opcodeMap.put("JMA", 13);
        opcodeMap.put("JSR", 14);
        opcodeMap.put("RFS", 15);
        opcodeMap.put("SOB", 16);
        opcodeMap.put("JGE", 17);

        // Arithmetic and logical instructions (pp. 10 of C6461 Spec)
        opcodeMap.put("AMR", 4);
        opcodeMap.put("SMR", 5);
        opcodeMap.put("AIR", 6);
        opcodeMap.put("SIR", 7);

        // Register to Register Operations (pp. 11 of C6461 Spec)
        opcodeMap.put("MLT", 70);
        opcodeMap.put("DVD", 71);
        opcodeMap.put("TRR", 72);
        opcodeMap.put("AND", 73);
        opcodeMap.put("ORR", 74);
        opcodeMap.put("NOT", 75);

        // Shift/rotate Instructions (pp. 12 of C6461 Spec)
        opcodeMap.put("SRC", 31);
        opcodeMap.put("RRC", 32);

        // IO Operations (pp. 13 of C6461 Spec)
        opcodeMap.put("IN", 61);
        opcodeMap.put("OUT", 62);
        opcodeMap.put("CHK", 63);
    }

//    public static void main(String[] args) {
//        testEncoder(args);
//    }

    /**
     *  Right now this just assembles using predefined address location. The file goes like:
     *  1    LOC 6       ;BEGIN AT LOCATION 6
     *  2    Data 10     ;PUT 10 AT LOCATION 6
     *  3    Data 3      ;PUT 3 AT LOCATION 7
     *  4    Data End    ;PUT 1024 AT LOCATION 8
     *  5    Data 0
     *  6    Data 12
     *  7    Data 9
     *  8    Data 18
     *  9    Data 12
     *  10   LDX 2,7     ;X2 GETS 3
     * @param location Current location in memory.
     * @param instructionLine The instruction to encode.
     * @return Encded instruction in octal format.
     */
    public static String encodeInstruction(int location, String instructionLine) {
        String locationOctal = convertToOctal(location, 6);
        String instructionOctal = parseInstruction(instructionLine);

        return locationOctal + " " + instructionOctal + " " + instructionLine;
    }

    /**
     * Parses the instruction line and directly packs it into 16-bit instruction format. This should ONLY parse
     * Operation Codes - not system directives or labels.
     * Note that ISA doc C6461 uses little-endian numbering, while Java bit operations use
     * right-to-left numbering.
     * @param line A string representing one line from the source file.
     * @return The packed instruction in octal format.
     */
    private static String parseInstruction(String line) {

        // remove comments - not needed
        if (line.contains(";")) {
            line = line.substring(0, line.indexOf(";"));
        }

        // Handle labels - not part of instruction (since encode method expects a decimal location)
        String instructionPart = line;
        if (line.contains(":")) {
            instructionPart = line.substring(line.indexOf(":") + 1).trim();

            // Labels with no instructions are not allowed
            if (instructionPart.isEmpty()) {
                throw new IllegalArgumentException("Label-only line passed to encoder: " + line.trim());
            }
        }

        // Don't put directives in the encoder!
        if (instructionPart.startsWith("LOC") ||  instructionPart.startsWith("Data")) {
            throw new IllegalArgumentException("src.main.java.Assembler directive passed to encoder: " + line.trim());
        }

        String[] parts = instructionPart.trim().split("[ ,]+");
        String opcode = parts[0];

        Integer opcodeValue = opcodeMap.get(opcode);
        if (opcodeValue == null) {
            throw new RuntimeException("Unknown opcode: " + opcode);
        }

        //Packed instruction starts at 0000 0000 0000 0000 (all bits zero)
        int instruction = 0;
        instruction |= (opcodeValue & 0x3F) << 10; // Opcode bits 0-5

        switch (opcode) {
            // === MISCELLANEOUS INSTRUCTIONS ===
            case "HLT":
                // Format: |0-5 Opcode|6-15 unused|
                // No additional bits to set
                break;

            case "TRAP":
                // Format: |0-5 Opcode|6-11 unused|12-15 TrapCode|
                int trapCode = Integer.parseInt(parts[1]);
                instruction |= (trapCode & 0xF); // TrapCode bits 12-15
                break;

            // === STANDARD INSTRUCTIONS ===
            case "LDR": case "STR": case "LDA": //LD/STR
            case "JZ": case "JNE": case "JCC": case "SOB": case "JGE": //Transfer
            case "AMR": case "SMR": //Arithmetic and logical
                // Format: |0-5 Opcode|6-7 R|8-9 IX|10 I|11-15 Address|
                int register = Integer.parseInt(parts[1]);
                int index = Integer.parseInt(parts[2]);
                int address = Integer.parseInt(parts[3]);
                int indirect = (parts.length > 4 && parts[4].equals("1")) ? 1 : 0;

                instruction |= (register & 0x3) << 8;    // R bits 6-7
                instruction |= (index & 0x3) << 6;       // IX bits 8-9
                instruction |= (indirect & 0x1) << 5;    // I bit 10
                instruction |= (address & 0x1F);         // Address bits 11-15
                break;

            // === SPECIAL TRANSFER INSTRUCTIONS ===
            case "JMA": case "JSR":
                // Format: |0-5 Opcode|6-7 unused|8-9 IX|10 I|11-15 Address|
                int ix = Integer.parseInt(parts[1]);        // x parameter
                int addr = Integer.parseInt(parts[2]);      // address parameter
                int ind = (parts.length > 3 && parts[3].equals("1")) ? 1 : 0;

                // r field is ignored, so we can put 0 there
                instruction |= (0 & 0x3) << 8;             // R bits 6-7 (ignored)
                instruction |= (ix & 0x3) << 6;            // IX bits 8-9
                instruction |= (ind & 0x1) << 5;           // I bit 10
                instruction |= (addr & 0x1F);              // Address bits 11-15
                break;

            case "RFS":
                // Format: |0-5 Opcode|6-10 unused|11-15 Immediate|
                int returnCode = (parts.length > 1) ? Integer.parseInt(parts[1]) : 0;
                instruction |= (returnCode & 0x1F);        // Immediate bits 11-15
                break;

            // === INDEX REGISTER LOAD/STORE ===
            case "LDX": case "STX":
                // Format: |0-5 Opcode|6-7 IX|8-9 unused|10 I|11-15 Address|
                int indexReg = Integer.parseInt(parts[1]);
                addr = Integer.parseInt(parts[2]);
                ind = (parts.length > 3 && parts[3].equals("1")) ? 1 : 0;

                instruction |= (indexReg & 0x3) << 8;    // IX bits 6-7
                instruction |= (ind & 0x1) << 5;         // I bit 10
                instruction |= (addr & 0x1F);            // Address bits 11-15
                break;

            // === IMMEDIATE INSTRUCTIONS ===
            case "AIR": case "SIR":
                // Format: |0-5 Opcode|6-7 R|8-10 unused|11-15 Immediate|
                int reg = Integer.parseInt(parts[1]);
                int immediate = Integer.parseInt(parts[2]);

                instruction |= (reg & 0x3) << 8;         // R bits 6-7
                instruction |= (immediate & 0x1F);       // Immediate bits 11-15
                break;

            // === REGISTER-TO-REGISTER OPERATIONS ===
            case "MLT": case "DVD": case "TRR": case "AND": case "ORR":
                // Format: |0-5 Opcode|6-7 Rx|8-9 Ry|10-15 unused|
                int rx = Integer.parseInt(parts[1]);
                int ry = Integer.parseInt(parts[2]);

                instruction |= (rx & 0x3) << 8;          // Rx bits 6-7
                instruction |= (ry & 0x3) << 6;          // Ry bits 8-9
                break;

            case "NOT":
                // Format: |0-5 Opcode|6-7 Rx|8-15 unused|
                int regx = Integer.parseInt(parts[1]);
                instruction |= (regx & 0x3) << 8;        // Rx bits 6-7
                break;

            // === SHIFT/ROTATE OPERATIONS ===
            case "SRC": case "RRC":
                // Format: |0-5 Opcode|6-7 R|8 A/L|9 L/R|10-11 unused|12-15 Count|
                int r = Integer.parseInt(parts[1]);
                int count = Integer.parseInt(parts[2]);
                int lr = Integer.parseInt(parts[3]);
                int al = Integer.parseInt(parts[4]);

                instruction |= (r & 0x3) << 8;           // R bits 6-7
                instruction |= (al & 0x1) << 7;          // A/L bit 8
                instruction |= (lr & 0x1) << 6;          // L/R bit 9
                // bits 10-11 unused (remain 0)
                instruction |= (count & 0xF);            // Count bits 12-15
                break;

            // === I/O OPERATIONS ===
            case "IN": case "OUT": case "CHK":
                // Format: |0-5 Opcode|6-7 R|8-10 unused|11-15 DeviceID|
                int ioReg = Integer.parseInt(parts[1]);
                int deviceId = Integer.parseInt(parts[2]);

                instruction |= (ioReg & 0x3) << 8;       // R bits 6-7
                instruction |= (deviceId & 0x1F);        // DeviceID bits 11-15
                break;

            default:
                throw new RuntimeException("Unknown instruction: " + opcode);
        }

        // Convert to 6-digit octal
        return convertToOctal(instruction, 6);
    }

    /**
     * Utility function that takes in a decimal value and converts to Octal, padded with the
     * correct number of leading zeros.
     * @param value Decimal value to convert
     * @param digits Number of digits to pad with leading zeros
     * @return Octal representation of the decimal value
     */
    public static String convertToOctal(int value, int digits) {
        String octal = Integer.toOctalString(value);

        //pad with leading zeros so it's like 000020 when integer is 16 (so it's not just 20)
        while (octal.length() < digits) {octal = "0" + octal;}

        return octal;
    }

    /**
     * Runs a simple test to ensure encoder is producing correct listing output file per C6461 pp. 20
     * @param args
     */
    public static void testEncoder(String[] args) {
        System.out.println(encodeInstruction(20, "LDX 2,7;X2 GETS 3"));
        System.out.println(encodeInstruction(20, "MyLabel: LDR 2,2,10"));
        //System directive example: Should produce error
        //System.out.println(encodeInstruction(20, "Data 9 ;PUT 9 AT LOCATION 2"));
        System.out.println(encodeInstruction(20, "LDR 1,2,10,1 ;R1 GETS 18"));
        System.out.println(encodeInstruction(20, "End: HLT ;STOP"));
    }
}