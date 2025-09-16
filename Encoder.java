import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Encoder {

    //Holds opcodes for all instructions as (instruction, opcode in octal).
    private static List<Opcode> opcodes;
    //Hashmap lookup table to find opcodes in O(1) time.
    private static Map<String, Opcode> opcodeMap;

    enum InstructionFormat {
        MISC,
        LOAD_STORE,
        REGISTER_REG,
        SHIFT_ROTATE,
        IO,
        IMMEDIATE
    }

    static {
        opcodes = new ArrayList<>();

        opcodes.add(new Opcode("HLT", 0, InstructionFormat.MISC));
        opcodes.add(new Opcode("TRAP", 30, InstructionFormat.MISC));

        // Load/Store Instructions (pp. 8 of C6461 Spec)
        opcodes.add(new Opcode("LDR", 1, InstructionFormat.LOAD_STORE));
        opcodes.add(new Opcode("STR", 2, InstructionFormat.LOAD_STORE));
        opcodes.add(new Opcode("LDA", 3, InstructionFormat.LOAD_STORE));
        opcodes.add(new Opcode("LDX", 41, InstructionFormat.LOAD_STORE));
        opcodes.add(new Opcode("STX", 42, InstructionFormat.LOAD_STORE));

        // Transfer Instructions (pp. 9 of C6461 Spec)
        opcodes.add(new Opcode("JZ", 10, InstructionFormat.LOAD_STORE));
        opcodes.add(new Opcode("JNE", 11, InstructionFormat.LOAD_STORE));
        opcodes.add(new Opcode("JCC", 12, InstructionFormat.LOAD_STORE));
        opcodes.add(new Opcode("JMA", 13, InstructionFormat.LOAD_STORE));
        opcodes.add(new Opcode("JSR", 14, InstructionFormat.LOAD_STORE));
        opcodes.add(new Opcode("RFS", 15, InstructionFormat.LOAD_STORE));
        opcodes.add(new Opcode("SOB", 16, InstructionFormat.LOAD_STORE));
        opcodes.add(new Opcode("JGE", 17, InstructionFormat.LOAD_STORE));

        // Arithmetic and logical instructions (pp. 10 of C6461 Spec)
        opcodes.add(new Opcode("AMR", 4, InstructionFormat.LOAD_STORE));
        opcodes.add(new Opcode("SMR", 5, InstructionFormat.LOAD_STORE));
        opcodes.add(new Opcode("AIR", 6, InstructionFormat.IMMEDIATE));
        opcodes.add(new Opcode("SIR", 7, InstructionFormat.IMMEDIATE));

        // Register to Register Operations (pp. 11 of C6461 Spec)
        opcodes.add(new Opcode("MLT", 70, InstructionFormat.REGISTER_REG));
        opcodes.add(new Opcode("DVD", 71, InstructionFormat.REGISTER_REG));
        opcodes.add(new Opcode("TRR", 72, InstructionFormat.REGISTER_REG));
        opcodes.add(new Opcode("AND", 73, InstructionFormat.REGISTER_REG));
        opcodes.add(new Opcode("ORR", 74, InstructionFormat.REGISTER_REG));
        opcodes.add(new Opcode("NOT", 75, InstructionFormat.REGISTER_REG));

        // Shift/rotate Instructions (pp. 12 of C6461 Spec)
        opcodes.add(new Opcode("SRC", 31, InstructionFormat.SHIFT_ROTATE));
        opcodes.add(new Opcode("RRC", 32, InstructionFormat.SHIFT_ROTATE));

        // IO Operations (pp. 13 of C6461 Spec)
        opcodes.add(new Opcode("IN", 61, InstructionFormat.IO));
        opcodes.add(new Opcode("OUT", 62, InstructionFormat.IO));
        opcodes.add(new Opcode("CHK", 63, InstructionFormat.IO));

        opcodeMap = new HashMap<>();
        for (Opcode opcode : opcodes) {
            opcodeMap.put(opcode.instruction, opcode);
        }
    }

    //Basic class that holds opcode specifications.
    private static class Opcode {
        String instruction;
        int opcode;
        InstructionFormat format;

        public Opcode(String instruction, int opcode, InstructionFormat format) {
            this.instruction = instruction;
            this.opcode = opcode;
            this.format = format;
        }
    }

    /*
    Holds instruction data for all opcodes (whether needed or not). For example not all opcodes use
    an indirect (like AIR). But it's available if needed. IDK how yet to implement nullable.
    */
    private static class Instruction {
        String instruction;
        int register;
        int index;
        int indirect;
        int address;

        public Instruction(String opcode, int register, int index,
                           int indirect, int address) {
            this.instruction = opcode;
            this.register = register;
            this.index = index;
            this.indirect = indirect;
            this.address = address;
        }
    }

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
        String result = assemble(20, "LDR 2,1,8");
        System.out.println(result);
    }

    public static String assemble(int location, String instructionLine) {
        Instruction inst = parseInstruction(instructionLine);
        String locationOctal = convertToOctal(location, 6);
        String instructionOctal = generateInstruction(inst);

        return locationOctal + " " + instructionOctal + " " + instructionLine;
    }


    private static Instruction parseInstruction(String line) {
        //remove comments and split
        if (line.contains(";")) {line = line.substring(0, line.indexOf(";"));}
        String[] parts = line.trim().split("[ ,]+");
        String opcode = parts[0];

        Opcode opcodeInfo = opcodeMap.get(opcode);
        if (opcodeInfo == null) {
            throw new RuntimeException("Unknown instruction: " + opcode);
        }

        switch (opcodeInfo.format) {
            case MISC:
                if (opcode.equals("TRAP")) {
                    int trapCode = Integer.parseInt(parts[1]);
                    return new Instruction(opcode, 0, 0, 0, trapCode);
                }
                return new Instruction(opcode, 0, 0, 0, 0);

            case LOAD_STORE:
                int register = Integer.parseInt(parts[1]);
                int index = Integer.parseInt(parts[2]);
                int address = Integer.parseInt(parts[3]);
                int indirect = (parts.length > 4 && parts[4].equals("1")) ? 1 : 0;
                return new Instruction(opcode, register, index, indirect, address);

            case IMMEDIATE, IO:
                return new Instruction(opcode,
                        Integer.parseInt(parts[1]),
                        0,
                        0,
                        Integer.parseInt(parts[2]));

            case REGISTER_REG:
                return new Instruction(opcode,
                        Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]),
                        0, 0);

            case SHIFT_ROTATE:
                int reg = Integer.parseInt(parts[1]);
                int count = Integer.parseInt(parts[2]);
                int lr = Integer.parseInt(parts[3]);
                int al = Integer.parseInt(parts[4]);
                return new Instruction(opcode, reg, count, lr, al);

            default:
                throw new RuntimeException("Unhandled instruction format: " + opcodeInfo.format);
        }
    }

    /**
     * Pack into 16-bit instruction format:
     * |Opcode(6)|R(2)|IX(2)|I(1)|Address(5)|
     * @inst The instruction to pack
     */
    private static String generateInstruction(Instruction inst) {
        // Get opcode from map
        int opcodeValue = opcodeMap.get(inst.instruction).opcode;

        //start at 0000 0000 0000 0000 (all bits zero)
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
        instruction |= (inst.address & 0x1F);

        // Convert to 6-digit octal
        return convertToOctal(instruction, 6);
    }

    /*
    Utility function that takes in a decimal value and converts to Octal, padded with the
    correct number of leading zeros.
     */
    public static String convertToOctal(int value, int digits) {
        String octal = Integer.toOctalString(value);

        //pad with leading zeros so it's like 000020 when integer is 16 (so it's not just 20)
        while (octal.length() < digits) {octal = "0" + octal;}

        return octal;
    }
}