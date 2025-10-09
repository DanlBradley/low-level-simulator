package src;

public class Computer {
    private final CPU cpu;
    private final Memory memory;
    private boolean halted;

    private static final int LDR = 1;
    private static final int STR = 2;
    private static final int LDA = 3;
    private static final int HLT = 0;
    private static final int LDX = 33;  //41 octal is 33
    private static final int JZ =  8; //10 octal is 8

    public Computer() {
        cpu = new CPU();
        memory = new Memory();
        halted = false;
    }

    public static void main(String[] args) {
        System.out.println("Running the computer!\n");
        Computer computer = new Computer();
        computer.IPL("data/load.txt", 14); //How to get the start address from the load file?
        computer.run();
    }

    public void IPL(String programFile, int startAddr) {
        System.out.println("IPL: Initial Program Load and reset\n");
        cpu.reset();
        memory.reset();
        halted = false;

        ROMLoader.loadProgram(memory, programFile);
        if(startAddr >= 0) {
            cpu.PC = (short)startAddr;
        } else {
            throw new IllegalArgumentException("Invalid start address");
        }
        System.out.println("PC set to " + cpu.PC);
    }

    public void singleStep() {
        if(halted) {
            System.out.println("Computer is halted\n");
            return;
        }

        System.out.println("\n\nSingle Step Execution\n");

        //initialize the cpu - MAR (Memory Address Register) and MBR (Memory Buffer Register) and IR (Instruction Register)
        //set MAR to the PC and MBR to the value stored at MAR. Then set the IR to the value stored at MBR.
        //Finally, increment the PC by one.
        cpu.MAR = cpu.PC;
        cpu.MBR = memory.read(cpu.MAR);
        cpu.IR = cpu.MBR;
        cpu.PC++;

        int unsignedIR = cpu.IR & 0xFFFF;
        System.out.println("FETCH: PC=" + (cpu.PC-1) + " IR=" + unsignedIR +
                " (octal: " + String.format("%06o", unsignedIR) + ")");

        // decode step
        int opcode = cpu.getOpcode(cpu.IR);
        int reg = cpu.getRegister(cpu.IR);
        int ix = cpu.getIndexReg(cpu.IR);
        int indirect = cpu.getIndirect(cpu.IR);
        int address = cpu.getAddress(cpu.IR);

        // execute step
        executeInstruction(opcode, reg, ix, indirect, address);
    }


    /**
     * Get the effective address for an instruction. Implementation from pp. 7 of C6461 doc.
     * @param address
     * @param ix
     * @param ind
     * @return effective address
     */
    private int getEffectiveAddress(int address, int ix, int ind) {
        int effectiveAddress = address;
        if (ix >0 && ix <= 3) {
            effectiveAddress += cpu.IX[ix];
        }
        if (ind == 1) {
            effectiveAddress = memory.read(effectiveAddress);
        }
        return effectiveAddress;
    }

    /**
     * Execute an instruction. Parses instruction differently depending on opcode. It may eventually make more sense
     * to break these switch case statements out into separate methods but for now I think they're small enough they
     * can be handled within this method directly.
     * @param opcode
     * @param reg
     * @param ix
     * @param indirect
     * @param address
     */
    private void executeInstruction(int opcode, int reg, int ix, int indirect, int address) {
        int effectiveAddress = getEffectiveAddress(address, ix, indirect);

        String opcodeName = Encoder.getOpcodeName(opcode);
        System.out.println("EXECUTE: " + opcodeName + " (Opcode=" + opcode + ") EA=" + effectiveAddress);

        switch(opcode) {
            case HLT:
                System.out.println("HALT instruction");
                halted = true;
                break;

            case LDR:
                cpu.R[reg] = memory.read(effectiveAddress);
                System.out.println("LDR: R" + reg + " = M[" + effectiveAddress + "] = " + cpu.R[reg]);
                break;

            case STR:
                memory.write(effectiveAddress, cpu.R[reg]);
                System.out.println("STR: M[" + effectiveAddress + "] = R" + reg + " = " + cpu.R[reg]);
                break;

            case LDA:
                if (reg >= 0 && reg <= 3) {
                    cpu.R[reg] = (short)effectiveAddress;
                    System.out.println("LDA: R" + reg + " = " + effectiveAddress);
                } else {
                    System.out.println("ERROR: Invalid register " + reg + " for LDA");
                }

                break;

            case LDX:
                if(ix >= 1 && ix <= 3) {
                    cpu.IX[ix] = memory.read(effectiveAddress);
                    System.out.println("LDX: X" + ix + " = M[" + effectiveAddress + "] = " + cpu.IX[ix]);
                } else {
                    System.out.println("ERROR: Invalid index register " + ix + " for LDX");
                }
                break;

                //JZ not necessary but it's used in the example listing file so we included it here.
            case JZ:
                if(cpu.R[reg] == 0) {
                    cpu.PC = (short)effectiveAddress;
                    System.out.println("JZ: R" + reg + " is zero, jumping to " + effectiveAddress);
                } else {
                    System.out.println("JZ: R" + reg + " = " + cpu.R[reg] + " (not zero), no jump");
                }
                break;

            default:
                System.out.println("Unknown opcode: " + opcode);
                halted = true;
        }
    }

    public void run() {
        System.out.println("\nRunning Program");
        while(!halted) {
            singleStep();
        }
        System.out.println("Program execution completed");
    }
}