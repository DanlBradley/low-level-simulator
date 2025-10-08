package src;

import java.util.HashMap;
import java.util.Map;

/**
 * Encodes instructions into a packed 16-bit effective addresses as a 6-digit octal number. Utilizes a hash map lookup table
 * for efficient opcode lookup, which is used in the switch-case statement to identify instruction method.
 *
 * Also provides a utility function to convert decimal values to octal.
 */
public class Encoder {

    // Opcode lookup table for O(1) opcode value retrieval
    private static Map<String, Integer> opcodeMap;
    private static Map<Integer, String> opcodeNameMap;

    static {
        opcodeMap = new HashMap<>();

        opcodeMap.put("HLT", 0);
        opcodeMap.put("TRAP", 24);

        opcodeMap.put("LDR", 1);
        opcodeMap.put("STR", 2);
        opcodeMap.put("LDA", 3);
        opcodeMap.put("LDX", 33);
        opcodeMap.put("STX", 34);

        opcodeMap.put("JZ", 8);
        opcodeMap.put("JNE", 9);
        opcodeMap.put("JCC", 10);
        opcodeMap.put("JMA", 11);
        opcodeMap.put("JSR", 12);
        opcodeMap.put("RFS", 13);
        opcodeMap.put("SOB", 14);
        opcodeMap.put("JGE", 15);

        opcodeMap.put("AMR", 4);
        opcodeMap.put("SMR", 5);
        opcodeMap.put("AIR", 6);
        opcodeMap.put("SIR", 7);

        opcodeMap.put("MLT", 56);
        opcodeMap.put("DVD", 57);
        opcodeMap.put("TRR", 58);
        opcodeMap.put("AND", 59);
        opcodeMap.put("ORR", 60);
        opcodeMap.put("NOT", 61);

        opcodeMap.put("SRC", 25);
        opcodeMap.put("RRC", 26);

        opcodeMap.put("IN", 49);
        opcodeMap.put("OUT", 50);
        opcodeMap.put("CHK", 51);

        opcodeNameMap = new HashMap<>();
        for(Map.Entry<String, Integer> entry : opcodeMap.entrySet()) {
            opcodeNameMap.put(entry.getValue(), entry.getKey());
        }
    }

    public static String getOpcodeName(int opcode) {
        return opcodeNameMap.getOrDefault(opcode, "UNKNOWN");
    }

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
    public static String encodeInstruction(int location,
                                           String instructionLine,
                                           boolean isLoadFile) {
        String locationOctal = convertToOctal(location, 6);
        String instructionOctal = parseInstruction(instructionLine);

        if (!isLoadFile) {
            return locationOctal + " " + instructionOctal + " " + instructionLine;
        } else {
            return locationOctal + " " + instructionOctal;
        }

    }

    /**
     * Parses the instruction line and directly packs it into 16-bit instruction format. This should ONLY parse
     * Operation Codes - not system directives or labels.
     * Note that ISA doc C6461 uses left-to-right order for the opcode, while the assembler uses right-to-left.
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
                // Format: |0-5 Opcode|6-11 unused|12-15 Trap Code|
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

                instruction |= (register & 0x3) << 8;    // R/cc bits 6-7 (15 - 8 = 7)
                instruction |= (index & 0x3) << 6;       // IX bits 8-9 (15 - 6 = 9)
                instruction |= (indirect & 0x1) << 5;    // I bit 10 (15 - 5 = 10)
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
                // Format: |0-5 Opcode|6-7 unused|8-9 IX|10 I|11-15 Address|
                int indexReg = Integer.parseInt(parts[1]);
                addr = Integer.parseInt(parts[2]);
                ind = (parts.length > 3 && parts[3].equals("1")) ? 1 : 0;

                // r field is ignored, so we can put 0 there
                instruction |= (0 & 0x3) << 8;             // R bits 6-7 (ignored)
                instruction |= (indexReg & 0x3) << 6;      // IX bits 8-9
                instruction |= (ind & 0x1) << 5;           // I bit 10
                instruction |= (addr & 0x1F);              // Address bits 11-15
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
//        System.out.println(convertToOctal(instruction, 6));
//        return Integer.toBinaryString(instruction);
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
}