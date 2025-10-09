package src;

public class Memory {
    private short[] memory;
    private static final int MEMORY_SIZE = 2048;

    /**
     * Initializes memory of size MEMORY_SIZE with 0's.
     */
    public Memory() {
        memory = new short[MEMORY_SIZE];
        reset();
    }

    public void reset() {
        for(int i = 0; i < MEMORY_SIZE; i++) {
            memory[i] = 0;
        }
    }

    public short read(int address) {
        if(address >= 0 && address < MEMORY_SIZE) {
            int unsigned = memory[address] & 0xFFFF;
            String octal = String.format("%06o", unsigned);
            String binary = String.format("%16s", Integer.toBinaryString(unsigned)).replace(' ', '0');

            System.out.println("Memory READ: addr=" + address +
                    " value=" + unsigned +
                    " (octal: " + octal +
                    ", binary: " + binary + ")");
            return memory[address];
        }
        System.out.println("Memory READ ERROR: Invalid address " + address);
        return 0;
    }

    public void write(int address, short value) {
        if(address >= 0 && address < MEMORY_SIZE) {
            int unsigned = value & 0xFFFF;
            String octal = String.format("%06o", unsigned);
            String binary = String.format("%16s", Integer.toBinaryString(unsigned)).replace(' ', '0');

            System.out.println("Memory WRITE: addr=" + address +
                    " value=" + unsigned +
                    " (octal: " + octal +
                    ", binary: " + binary + ")");
            memory[address] = value;
        } else {
            System.out.println("Memory WRITE ERROR: Invalid address " + address);
        }
    }

    public void load(int address, short value) {
        // Direct load for ROM loader
        if(address >= 0 && address < MEMORY_SIZE) {
            memory[address] = value;
        }
    }
}