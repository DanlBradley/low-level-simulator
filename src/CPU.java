package src;


public class CPU {
    // Registers
    public short PC;      // Program Counter
    public short MAR;     // Memory Address Register
    public short MBR;     // Memory Buffer Register
    public short IR;      // Instruction Register
    public short[] R;     // General Purpose Registers R0-R3
    public short[] IX;    // Index Registers IX1-IX3 (IX0 doesn't exist)

    public CPU() {
        R = new short[4];   // R0-R3
        IX = new short[4];  // IX0-IX3, but IX0 unused
        reset();
    }

    public void reset() {
        PC = 0;
        MAR = 0;
        MBR = 0;
        IR = 0;
        for(int i = 0; i < 4; i++) {
            R[i] = 0;
            IX[i] = 0;
        }
    }

    // Decode instruction into parts
    public void decodeInstruction(short instruction) {
        // Instruction format: OOOOOO RR XX I AAAAA
        // O=opcode(6), R=register(2), X=index(2), I=indirect(1), A=address(5)
        int opcode = (instruction >> 10) & 0x3F;  // 6 bits
        int reg = (instruction >> 8) & 0x03;      // 2 bits
        int ix = (instruction >> 6) & 0x03;       // 2 bits
        int indirect = (instruction >> 5) & 0x01; // 1 bit
        int address = instruction & 0x1F;         // 5 bits

        System.out.println("Decoded: OP=" + opcode + " R=" + reg + " IX=" + ix +
                " I=" + indirect + " ADDR=" + address);
    }

    public int getOpcode(short instruction) {
        int unsigned = instruction & 0xFFFF;
        return (unsigned >> 10) & 0x3F;
    }

    public int getRegister(short instruction) {
        int unsigned = instruction & 0xFFFF;
        return (unsigned >> 8) & 0x03;
    }

    public int getIndexReg(short instruction) {
        int unsigned = instruction & 0xFFFF;
        return (unsigned >> 6) & 0x03;
    }

    public int getIndirect(short instruction) {
        int unsigned = instruction & 0xFFFF;
        return (unsigned >> 5) & 0x01;
    }

    public int getAddress(short instruction) {
        int unsigned = instruction & 0xFFFF;
        return unsigned & 0x1F;
    }
}