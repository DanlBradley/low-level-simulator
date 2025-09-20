// Memory.java
// Purpose: Represents 16-bit word-addressable memory (2048 words).
// Provides read and write functions with range and 16-bit masking.

public final class Memory {
    public static final int WORDS = 2048;      // memory size
    public static final int WORD_MASK = 0xFFFF; // mask to keep values 16-bit

    private final int[] mem = new int[WORDS];  // backing array for memory

    // Read a word from memory
    public int read(int addr) {
        check(addr);
        return mem[addr] & WORD_MASK;
    }

    // Write a word into memory
    public void write(int addr, int value) {
        check(addr);
        mem[addr] = value & WORD_MASK;
    }

    // Make sure the address is valid (0–2047)
    private static void check(int addr) {
        if (addr < 0 || addr >= WORDS) {
            throw new IllegalArgumentException("Address out of range: " + addr);
        }
    }
}
