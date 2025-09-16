import java.util.HashMap;
import java.util.Map;

public class SimpleAssembler {
    // Static fields
    private static Map<String, Integer> opcodes = new HashMap<>();
    private static Map<String, InstructionFormat> instructionFormats = new HashMap<>();
    private static Map<String, Integer> symbolTable = new HashMap<>();

    // Static initializers
    static {
        opcodes.put("HLT", 0);
        opcodes.put("LDR", 1);
        opcodes.put("JZ", 8);
    }

    static {
        // Miscellaneous
        instructionFormats.put("HLT", InstructionFormat.MISC);

        // Standard format
        instructionFormats.put("LDR", InstructionFormat.STANDARD);
        instructionFormats.put("STR", InstructionFormat.STANDARD);
        instructionFormats.put("JZ", InstructionFormat.STANDARD);
    }

    // Inner classes and enums
    static class Instruction {
        String opcode;
        int register;
        int index;
        int indirect;
        String address;

        public Instruction(String opcode, int register, int index, int indirect, String address) {
            this.opcode = opcode;
            this.register = register;
            this.index = index;
            this.indirect = indirect;
            this.address = address;
        }
    }

    //We can use this to separate codes by their different formats. (i.e., not all codes use indirect)
    enum InstructionFormat {
        MISC,
        STANDARD
    }

    // Main method - entry point
    public static void main(String[] args) {
        /*
        Right now this just assembles using predefined address location. The file goes like:
        1    LOC 6       ;BEGIN AT LOCATION 6
        2    Data 10     ;PUT 10 AT LOCATION 6
        3    Data 3      ;PUT 3 AT LOCATION 7
        4    Data End    ;PUT 1024 AT LOCATION 8
        5    Data 0
        6    Data 12
        7    Data 9
        8    Data 18
        9    Data 12
        10   LDX 2,7     ;X2 GETS 3

         So the LDX doesn't happen until line 10. We need a way to break those out.
         */
        findLabel("End: HLT", 15);
        int address = lookupLabel("End");
        System.out.println("EnAd is at address: " + address);

        String result1 = assemble(20, "LDR 2,1,8");
        System.out.println(result1);

        String result2 = assemble(10, "JZ 0,1,End");
        System.out.println(result2);
    }

    // Public methods - in order of call hierarchy
    public static String assemble(int location, String instructionLine) {
        Instruction inst = parseInstruction(instructionLine);
        String locationOctal = convertToOctal(location, 6);
        String instructionOctal = generateInstruction(inst);

        return locationOctal + " " + instructionOctal + " " + instructionLine;
    }

    public static Instruction parseInstruction(String line) {
        //remove comments and split
        if (line.contains(";")) {line = line.substring(0, line.indexOf(";"));}
        String[] parts = line.trim().split("[ ,]+");
        String opcode = parts[0];

        //look up the format and parse
        InstructionFormat format = instructionFormats.get(opcode);
        if (format == null) {
            throw new RuntimeException("Unknown instruction: " + opcode);
        }

        switch (format) {
            case MISC:
                return new Instruction(opcode, 0, 0, 0, "0");

            case STANDARD:
                int indirect = (parts.length > 4) ? Integer.parseInt(parts[4]) : 0;
                return new Instruction(opcode,
                        Integer.parseInt(parts[1]),  //register
                        Integer.parseInt(parts[2]),  //index
                        indirect,                    //indirect
                        parts[3]); //address

            default:
                throw new RuntimeException("Unhandled instruction format: " + format);
        }
    }

    /**
     * Pack into 16-bit instruction format:
     * |Opcode(6)|R(2)|IX(2)|I(1)|Address(5)|
     * @inst The instruction to pack
     */
    public static String generateInstruction(Instruction inst) {
        // Get opcode from map
        Integer opcodeValue = opcodes.get(inst.opcode);
        if (opcodeValue == null) {
            throw new RuntimeException("Unknown opcode: " + inst.opcode);
        }

        //start at 0000000000000000 (all bits zero)
        int instruction = 0;

        // opcode occupies bits 15-10 (the & 0x3 means drop everything but bottom 6 bits, then shift by 10)
        instruction |= (opcodeValue & 0x3F) << 10;

        // register occupies bits 9-8 (0x3 means drop everything but bottom 2 bits, then shift by 8)
        instruction |= (inst.register & 0x3) << 8;

        // index occupies bits 7-8 (0x3 means drop everything but bottom 2 bits, then shift by 8)
        instruction |= (inst.index & 0x3) << 6;

        // indirect occupies bit 5 (0x1 means drop everything but bottom 1 bit, then shift by 5)
        instruction |= (inst.indirect & 0x1) << 5;

        // address occupies bits 4-0 (0x1F means drop everything but bottom 5 bits, no shift)
        int addressValue;
        if (inst.address.matches("\\d+")) {
            addressValue = Integer.parseInt(inst.address);
        } else {
            addressValue = lookupLabel(inst.address);
        }
        instruction |= (addressValue & 0x1F);

        // Convert to 6-digit octal
        return convertToOctal(instruction, 6);
    }

    // Utility methods
    public static String convertToOctal(int value, int digits) {
        String octal = Integer.toOctalString(value);

        //pad with leading zeros so it's like 000020 when integer is 16 (so it is not just 20)
        while (octal.length() < digits) {octal = "0" + octal;}

        return octal;
    }

    public static void findLabel(String line, int address) {
        if (line.contains(":")) {
            String labelName = line.substring(0, line.indexOf(":")).trim();
            symbolTable.put(labelName, address);
            System.out.println("Found label: " + labelName + " at address " + address);
        }else {
            System.out.println("Line: " + line + " has no label");
        }
    }

    public static int lookupLabel(String name) {
        if (symbolTable.containsKey(name)) {
            return symbolTable.get(name);
        }
        throw new RuntimeException("Label not found: " + name);
    }
}