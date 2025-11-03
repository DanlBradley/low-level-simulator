package src;

import java.io.IOException;

public class Computer {
    public CPU cpu;
    public Cache cache;
    private boolean halted;
    private boolean waitingForInput;
    private int waitingRegister;
    private ComputerSimulatorGUI gui;

    private static final int LDR = 1;
    private static final int STR = 2;
    private static final int LDA = 3;
    private static final int HLT = 0;
    private static final int LDX = 33;  //41 octal is 33
    private static final int STX = 34;

    //transfer instructions
    private static final int JZ =  8; //10 octal is 8
    private static final int JNE =  9;
    private static final int JCC =  10;
    private static final int JMA =  11;
    private static final int JSR =  12;
    private static final int RFS =  13;
    private static final int SOB =  14;
    private static final int JGE =  15;

    //arithmetic instructions
    private static final int AMR =  4;
    private static final int SMR =  5;
    private static final int AIR =  6;
    private static final int SIR =  7;

    //register-register instructions
    private static final int MLT =  56;
    private static final int DVD =  57;
    private static final int TRR =  58;
    private static final int AND =  59;
    private static final int ORR =  60;
    private static final int NOT =  61;

    //shift/rotate instructions
    private static final int SRC =  25;
    private static final int RRC =  26;

    //io operations
    private static final int IN =  49;
    private static final int OUT =  50;

    public Computer() {
        cpu = new CPU();
        cache = new Cache();
        halted = false;
        waitingForInput = false;
        waitingRegister = -1;
    }

    public void setGUI(ComputerSimulatorGUI gui) {
        this.gui = gui;
    }

    public static void main(String[] args) {
        System.out.println("Running the computer!\n");
        src.Assembler.assembleFile("data/load_store_test.txt", "data/listing.txt", "data/load.txt");
        Computer computer = new Computer();
        computer.IPL("data/load.txt", 9);
        computer.run();
    }

    public void IPL(String programFile, int startAddr) {
        System.out.println("IPL: Initial Program Load and reset\n");
        cpu.reset();
        cache.reset();
        halted = false;

        ROMLoader.loadProgram(cache, programFile);
        if(startAddr >= 0) {
            cpu.PC = (short)startAddr;
        } else {
            throw new IllegalArgumentException("Invalid start address");
        }
        System.out.println("PC set to " + cpu.PC);
    }

    public boolean isWaitingForInput() {
        return waitingForInput;
    }

    public void continueFromInput() {
        if (!waitingForInput) return;

        if (gui != null && gui.hasConsoleInput()) {
            String input = gui.getConsoleInput();
            char ch = input.charAt(0);
            cpu.R[waitingRegister] = (short)(ch & 0xFF);
            gui.clearConsoleInput();
            System.out.println("IN: Read '" + ch + "' (ASCII " + (int)ch + ") into R" + waitingRegister);

            waitingForInput = false;
            waitingRegister = -1;

            cpu.PC++;

            if (!halted) {
                singleStep();
                if (gui != null) {
                    gui.updateDisplay();
                }
            }
        }
    }

    /**
     * Runs thru all single steps.
     */
    public void run() {
        System.out.println("\nRunning Program");
        while(!halted && !waitingForInput) {
            singleStep();
        }
        if (waitingForInput) {
            System.out.println("\nProgram paused - waiting for console input");
        } else {
            System.out.println("\nProgram execution completed");
        }
    }

    /**
     * The core "cycle" of the simulated computer.
     */
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
        cpu.MBR = cache.read(cpu.MAR);
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
        int al = cpu.getAL(cpu.IR);
        int lr = cpu.getLR(cpu.IR);
        int count = cpu.getCount(cpu.IR);

        // execute step
        executeInstruction(opcode, reg, ix, al, lr, count, indirect, address);
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
            effectiveAddress = cache.read(effectiveAddress);
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
    private void executeInstruction(int opcode, int reg, int ix, int al, int lr, int count, int indirect, int address) {
        int effectiveAddress = getEffectiveAddress(address, ix, indirect);

        String opcodeName = Encoder.getOpcodeName(opcode);
        System.out.println("EXECUTE: " + opcodeName + " (Opcode=" + opcode + ") EA=" + effectiveAddress);

        switch(opcode) {
            case HLT:
                System.out.println("HALT instruction");
                halted = true;
                break;

            case LDR:
                cpu.R[reg] = cache.read(effectiveAddress);
                System.out.println("LDR: R" + reg + " = M[" + effectiveAddress + "] = " + cpu.R[reg]);
                break;

            case STR:
                cache.write(effectiveAddress, cpu.R[reg]);
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
                    int ldxEA = getEffectiveAddress(address, 0, indirect);
                    cpu.IX[ix] = cache.read(ldxEA);
                    System.out.println("LDX: X" + ix + " = M[" + effectiveAddress + "] = " + cpu.IX[ix]);
                } else {
                    System.out.println("ERROR: Invalid index register " + ix + " for LDX");
                }
                break;
            case STX:
                if(ix >= 1 && ix <= 3) {
                    int stxEA = getEffectiveAddress(address, 0, indirect);
                    cache.write(stxEA, cpu.IX[ix]);
                    System.out.println("STX: M[" + effectiveAddress + "] = X" + ix + " = " + cpu.IX[ix]);
                } else {
                    System.out.println("ERROR: Invalid index register " + ix + " for STX");
                }
                break;

            //JZ not necessary but it's used in the example listing file so we included it here and we'll need
            // it in part 2 anyway.
            case JZ:
                if(cpu.R[reg] == 0) {
                    cpu.PC = (short)effectiveAddress;
                    System.out.println("JZ: R" + reg + " is zero, jumping to " + effectiveAddress);
                } else {
                    System.out.println("JZ: R" + reg + " = " + cpu.R[reg] + " (not zero), no jump");
                }
                break;

            case JNE: //transfer instructions
                if(cpu.R[reg] != 0) {
                    cpu.PC = (short)effectiveAddress;
                    System.out.println("JNE: R" + reg + " is NOT zero, jumping to " + effectiveAddress);
                } else {
                    System.out.println("JNE: R" + reg + " = " + cpu.R[reg] + " (zero), no jump");
                }
                break;
            case JCC:
                int ccBit = reg;
                boolean bitSet = ((cpu.CC >> ccBit) & 1) == 1;

                if (bitSet) {
                    cpu.PC = (short)effectiveAddress;
                    System.out.println("JCC: CC bit " + ccBit + " is 1, jumping to " + effectiveAddress);
                } else {
                    System.out.println("JCC: CC bit " + ccBit + " is 0, no jump");
                }
                break;
            case JMA:
                cpu.PC = (short)effectiveAddress;
                System.out.println("JMA: jumping to " + effectiveAddress);
                break;
            case JSR:
                cpu.R[3] = cpu.PC;
                cpu.PC = (short)effectiveAddress;
                System.out.println("JSR: jumping to " + effectiveAddress);
                System.out.println("JSR: R3 = " + cpu.R[3] + " (return address), PC = " + cpu.PC);
                System.out.println("Absolute address: " + address);
                break;
            case RFS:
                cpu.R[0] = (short)address;
                cpu.PC = cpu.R[3];
                System.out.println("RFS: R0 = " + address + " (return code), PC = R3 = " + cpu.PC);
                break;
            case SOB:
                cpu.R[reg] = (short)(cpu.R[reg] - 1);
                if (cpu.R[reg] > 0) {
                    cpu.PC = (short)effectiveAddress;
                    System.out.println("SOB: R" + reg + " = " + cpu.R[reg] + " > 0, branching to " + effectiveAddress);
                } else {
                    System.out.println("SOB: R" + reg + " = " + cpu.R[reg] + " <= 0, continuing to PC: " + cpu.PC);
                }
                break;
            case JGE:
                if (cpu.R[reg] >= 0) {
                    cpu.PC = (short)effectiveAddress;
                    System.out.println("JGE: R" + reg + " = " + cpu.R[reg] + " >= 0, branching to " + effectiveAddress);
                } else {
                    System.out.println("JGE: R" + reg + " = " + cpu.R[reg] + " < 0, continuing to PC: " + cpu.PC);
                }
                break;

            case AMR: //arithmetic/logical instructions
                cpu.R[reg] = (short)(cpu.R[reg] + cache.read(effectiveAddress));
                System.out.println("AMR: R" + reg + " = R" + reg + " + M[" + effectiveAddress + "] = " + cpu.R[reg]);
                break;
            case SMR: //subtract memory from register
                cpu.R[reg] = (short)(cpu.R[reg] - cache.read(effectiveAddress));
                System.out.println("SMR: R" + reg + " = R" + reg + " - M[" + effectiveAddress + "] = " + cpu.R[reg]);
                break;
            case AIR: //add immediate to register
                cpu.R[reg] = (short)(cpu.R[reg] + (short)address);
                System.out.println("AIR: R" + reg + " = R" + reg + " + " + address + " = " + cpu.R[reg]);
                break;
            case SIR: //sub imm from register
                cpu.R[reg] = (short)(cpu.R[reg] - (short)address);
                System.out.println("SIR: R" + reg + " = R" + reg + " - " + address + " = " + cpu.R[reg]);
                break;

            case MLT: // register-register instructions section - multiply
                if ((reg == 0 || reg == 2) && (ix == 0 || ix == 2)) {
                    int result = cpu.R[reg] * cpu.R[ix];

                    // split result
                    cpu.R[reg] = (short)(result >> 16);
                    cpu.R[reg + 1] = (short)(result & 0xFFFF);

                    // check overflow
                    if (result > Short.MAX_VALUE || result < Short.MIN_VALUE) {
                        cpu.CC |= 1;
                    }

                    System.out.println("MLT: R" + reg + " * R" + ix + " = " + result +
                            " -> R" + reg + "=" + cpu.R[reg] +
                            ", R" + (reg+1) + "=" + cpu.R[reg+1]);
                } else {
                    System.out.println("ERROR: MLT requires rx and ry to be 0 or 2");
                }
                break;
            case DVD:
                if (cpu.R[ix] == 0) { //DIVZERO: cpu.cc = 0100 || cpu.CC
                    cpu.CC |= 4;
                    break;
                }
                if ((reg == 0 || reg == 2) && (ix == 0 || ix == 2)) {
                    var quotient = (short)(cpu.R[reg] / cpu.R[ix]);
                    var remainder = (short)(cpu.R[reg] % cpu.R[ix]);
                    cpu.R[reg] = quotient;
                    cpu.R[reg+1] = remainder;
                } else {
                    System.out.println("ERROR: DVD requires rx and ry to be 0 or 2");
                }
                break;
            case TRR:
                if (cpu.R[reg] == cpu.R[ix]) { // EQ: cpu.cc = 1000 || cpu.CC
                    cpu.CC |= 0b1000;
                    System.out.println("TRR: R" + reg + " == R" + ix + " (EQUAL)");
                    System.out.println("TRR: R" + reg + " = " + cpu.R[reg] + " (R" + reg + ")");
                    System.out.println("TRR: R" + ix + " = " + cpu.R[ix] + " (R" + ix + ")");
                } else {
                    cpu.CC &= ~0b1000; //set 0 for not eq.
                    System.out.println("TRR: R" + reg + " != R" + ix + " (NOT EQUAL)");
                    System.out.println("TRR: R" + reg + " = " + cpu.R[reg] + " (R" + reg + ")");
                    System.out.println("TRR: R" + ix + " = " + cpu.R[ix] + " (R" + ix + ")");
                }
                break;
            case AND:
                cpu.R[reg] = (short)(cpu.R[reg] & cpu.R[ix]);
                System.out.println("AND: R" + reg + " & R" + ix + " = " + cpu.R[reg]);
                break;
            case ORR:
                cpu.R[reg] = (short)(cpu.R[reg] | cpu.R[ix]);
                System.out.println("ORR: R" + reg + " | R" + ix + " = " + cpu.R[reg]);
                break;
            case NOT:
                cpu.R[reg] = (short)(~cpu.R[reg]);
                System.out.println("NOT: ~R" + reg + " = " + cpu.R[reg]);
                break;

            case SRC: //shift/rotate instructions
                //c(r) is shifted left (lr == 1) or right (lr == 0) either logically (al == 1) or arithmetically (al == 0)
                //count is the number of bits to shift (0-7)
                if (count == 0) {
                    System.out.println("SRC: R" + reg + " no shift (count=0)");
                    break;
                }

                if (lr == 1) {
                    //shift left, al == 1: logical (arithmetic and logical the same for left shift)
                    int original = cpu.R[reg] & 0xFFFF;
                    cpu.R[reg] = (short)(cpu.R[reg] << count);

                    int mask = 0xFFFF << (16 - count);
                    if ((original & mask) != 0) {
                        cpu.CC |= 1; //overflow
                        System.out.println("SRC: R" + reg + " overflow");
                    } else {
                        System.out.println("SRC: R" + reg + " << " + count + " = " + cpu.R[reg]);
                    }
                } else {
                    //shift right, al == 1: logical
                    int original = cpu.R[reg] & 0xFFFF;
                    int mask = (1 << count) - 1;
                    if (al == 1) {
                        cpu.R[reg] = (short)(original >>> count);
                    } else {
                        //for right shift, arithmetic is different -- "march" the sign bit
                        cpu.R[reg] = (short)(cpu.R[reg] >> count);
                    }
                    if ((original & mask) != 0) {
                        cpu.CC |= 2;
                        String op = (al == 1) ? ">>>" : ">>";
                        System.out.println("SRC: R" + reg + " " + op + " " + count + " = " + cpu.R[reg] + " (UNDERFLOW)");
                    } else {
                        System.out.println("SRC: R" + reg + " >> " + count + " = " + cpu.R[reg]);
                    }
                }
                break;

            case RRC: //rotate instructions
                //c(r) is rotated left (lr == 1) or right (lr == 0) logically
                //count is the number of bits to rotate (0-15)

                if (count == 0) {
                    System.out.println("RRC: R" + reg + " no rotation (count=0)");
                    break;
                }

                int value = cpu.R[reg] & 0xFFFF; // treat as unsigned 16-bit

                if (lr == 1) {
                    // for ex: 0b10110011 rotated left by 2: 0b11001110
                    value = ((value << count) | (value >>> (16 - count))) & 0xFFFF;
                    cpu.R[reg] = (short) value;
                    System.out.println("RRC: R" + reg + " rotated left by " + count + " = " + cpu.R[reg]);
                } else {
                    // for ex: 0b10110011 rotated right by 2: 0b11101100
                    value = ((value >>> count) | (value << (16 - count))) & 0xFFFF;
                    cpu.R[reg] = (short) value;
                    System.out.println("RRC: R" + reg + " rotated right by " + count + " = " + cpu.R[reg]);
                }
                break;

            case IN:
                if (address == 0) {  //keyboard
                    if (gui != null) {
                        // Get input from GUI console
                        String input = gui.getConsoleInput();
                        if (input != null && !input.isEmpty()) {
                            char ch = input.charAt(0);
                            cpu.R[reg] = (short)(ch & 0xFF);
                            gui.clearConsoleInput();
                            System.out.println("IN: Read '" + ch + "' (ASCII " + (int)ch + ") into R" + reg);
                        } else {
                            System.out.println("IN: Waiting for console input for R" + reg);
                            waitingForInput = true;
                            waitingRegister = reg;
                            cpu.PC--; //back-up to read again if no input is given
                            return;
                        }
                    }
                } else if (address == 2) {  //card reader
                    System.out.println("IN: Card reader not implemented, R" + reg + " = 0");
                    cpu.R[reg] = 0;
                } else {
                    System.out.println("IN: Device " + address + " not implemented");
                }
                break;

            case OUT:
                if (address == 1) {  // Console Printer
                    char ch = (char)(cpu.R[reg] & 0xFF);
                    if (gui != null) {
                        //GUI output
                        gui.printToOutput(String.valueOf(ch));
                    }
                    //debugging
                    System.out.print(ch);
                    System.out.flush();
                    System.out.println("\nOUT: Printed '" + ch + "' (ASCII " + (int)ch + ") from R" + reg);
                } else {
                    System.out.println("OUT: Device " + address + " not implemented");
                }
                break;


            default:
                System.out.println("Unknown opcode: " + opcode);
                halted = true;
        }
    }
}