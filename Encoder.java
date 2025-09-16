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
            throw new IllegalArgumentException("Assembler directive passed to encoder: " + line.trim());
        }

        String[] parts = instructionPart.trim().split("[ ,]+");
        String opcode = parts[0];

        Integer opcodeValue = opcodeMap.get(opcode);
        if (opcodeValue == null) {
            throw new RuntimeException("Unknown opcode: " + opcode);
        }

        //Packed instruction starts at 0000 0000 0000 0000 (all bits zero)
        int instruction = 0;
        instruction |= (opcodeValue & 0x3F) << 10; // Opcode bits 15-10

        switch (opcode) {
            // === MISCELLANEOUS INSTRUCTIONS ===
            case "HLT":
                // Format: |Opcode(6)|unused(10)|
                // No additional bits to set
                break;

            case "TRAP":
                // Format: |Opcode(6)|unused(6)|TrapCode(4)|
                int trapCode = Integer.parseInt(parts[1]);
                instruction |= (trapCode & 0xF); // TrapCode bits 3-0
                break;

            // === STANDARD LOAD/STORE INSTRUCTIONS ===
            case "LDR": case "STR": case "LDA":
            case "JZ": case "JNE": case "JCC": case "JMA": case "JSR": case "SOB": case "JGE":
            case "AMR": case "SMR":
                // Format: |Opcode(6)|R(2)|IX(2)|I(1)|Address(5)|
                int register = Integer.parseInt(parts[1]);
                int index = Integer.parseInt(parts[2]);
                int address = Integer.parseInt(parts[3]);
                int indirect = (parts.length > 4 && parts[4].equals("1")) ? 1 : 0;

                instruction |= (register & 0x3) << 8;    // R bits 9-8
                instruction |= (index & 0x3) << 6;       // IX bits 7-6
                instruction |= (indirect & 0x1) << 5;    // I bit 5
                instruction |= (address & 0x1F);         // Address bits 4-0
                break;

            // === INDEX REGISTER LOAD/STORE ===
            case "LDX": case "STX":
                // Format: |Opcode(6)|IX(2)|unused(2)|I(1)|Address(5)|
                int indexReg = Integer.parseInt(parts[1]);
                int addr = Integer.parseInt(parts[2]);
                int ind = (parts.length > 3 && parts[3].equals("1")) ? 1 : 0;

                instruction |= (indexReg & 0x3) << 8;    // IX bits 9-8
                instruction |= (ind & 0x1) << 5;         // I bit 5
                instruction |= (addr & 0x1F);            // Address bits 4-0
                break;

            // === SPECIAL TRANSFER INSTRUCTIONS ===
            case "RFS":
                // Format: |Opcode(6)|unused(2)|IX(2)|I(1)|Address(5)|
                int returnCode = (parts.length > 1) ? Integer.parseInt(parts[1]) : 0;
                instruction |= (returnCode & 0x1F);      // Address bits 4-0
                break;

            // === IMMEDIATE INSTRUCTIONS ===
            case "AIR": case "SIR":
                // Format: |Opcode(6)|R(2)|unused(3)|Immediate(5)|
                int reg = Integer.parseInt(parts[1]);
                int immediate = Integer.parseInt(parts[2]);

                instruction |= (reg & 0x3) << 8;         // R bits 9-8
                instruction |= (immediate & 0x1F);       // Immediate bits 4-0
                break;

            // === REGISTER-TO-REGISTER OPERATIONS ===
            case "MLT": case "DVD": case "TRR": case "AND": case "ORR":
                // Format: |Opcode(6)|Rx(2)|Ry(2)|unused(6)|
                int rx = Integer.parseInt(parts[1]);
                int ry = Integer.parseInt(parts[2]);

                instruction |= (rx & 0x3) << 8;          // Rx bits 9-8
                instruction |= (ry & 0x3) << 6;          // Ry bits 7-6
                break;

            case "NOT":
                // Format: |Opcode(6)|Rx(2)|unused(8)|
                int regx = Integer.parseInt(parts[1]);
                instruction |= (regx & 0x3) << 8;        // Rx bits 9-8
                break;

            // === SHIFT/ROTATE OPERATIONS ===
            case "SRC": case "RRC":
                // Format: |Opcode(6)|R(2)|Count(4)|L/R(1)|A/L(1)|unused(2)|
                int r = Integer.parseInt(parts[1]);
                int count = Integer.parseInt(parts[2]);
                int lr = Integer.parseInt(parts[3]);
                int al = Integer.parseInt(parts[4]);

                instruction |= (r & 0x3) << 8;           // R bits 9-8
                instruction |= (count & 0xF) << 4;       // Count bits 7-4
                instruction |= (lr & 0x1) << 3;          // L/R bit 3
                instruction |= (al & 0x1) << 2;          // A/L bit 2
                break;

            // === I/O OPERATIONS ===
            case "IN": case "OUT": case "CHK":
                // Format: |Opcode(6)|R(2)|unused(3)|DeviceID(5)|
                int ioReg = Integer.parseInt(parts[1]);
                int deviceId = Integer.parseInt(parts[2]);

                instruction |= (ioReg & 0x3) << 8;       // R bits 9-8
                instruction |= (deviceId & 0x1F);        // DeviceID bits 4-0
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