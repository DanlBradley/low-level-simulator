package src;

/**
 * CPU stores registers as well as the program counter. It also handles instruction decoding.
 */
public class CPU {
    public short PC;
    public short MAR;
    public short MBR;
    public short IR;
    public byte CC;
    public short[] R;
    public short[] IX;

    public CPU() {
        R = new short[4];   // R0-R3
        IX = new short[4];  // X1-X3, 0 value indicates no indexing (pp. 7)
        reset();
    }

    /**
     * Reset the CPU to its initial state.
     */
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

    /**
     * Get the opcode from an instruction.
     * @param instruction
     * @return opcode (bits 15-10)
     */
    public int getOpcode(short instruction) {
        int unsigned = instruction & 0xFFFF;
        return (unsigned >> 10) & 0x3F;
    }

    /**
     * Get the register from an instruction. Note: this can also be used for Rx
     * @param instruction
     * @return register (bits 9-8)
     */
    public int getRegister(short instruction) {
        int unsigned = instruction & 0xFFFF;
        return (unsigned >> 8) & 0x03;
    }

    /**
     * Get the index register from an instruction. Note: this can also be used for Ry
     * @param instruction
     * @return index register (bits 7-6)
     */
    public int getIndexReg(short instruction) {
        int unsigned = instruction & 0xFFFF;
        return (unsigned >> 6) & 0x03;
    }

    public int getAL(short instruction) {
        int unsigned = instruction & 0xFFFF;
        return (unsigned >> 8) & 0x01;
    }
    public int getLR(short instruction) {
        int unsigned = instruction & 0xFFFF;
        return (unsigned >> 8) & 0x01;
    }
    public int getCount(short instruction) {
        int unsigned = instruction & 0xFFFF;
        return unsigned & 0x07;
    }

    /**
     * Get the indirect flag from an instruction.
     * @param instruction
     * @return 1 if indirect, 0 if not (bit 5)
     */
    public int getIndirect(short instruction) {
        int unsigned = instruction & 0xFFFF;
        return (unsigned >> 5) & 0x01;
    }

    /**
     * Get the address from an instruction.
     * @param instruction
     * @return address (bits 1-4)
     */
    public int getAddress(short instruction) {
        int unsigned = instruction & 0xFFFF;
        return unsigned & 0x1F;
    }
}