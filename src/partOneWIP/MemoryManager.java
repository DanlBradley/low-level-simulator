package src.partOneWIP;// src.partOneWIP.MemoryManager.java
// Minimal 2-pass assembler driver.
// Pass 1: collects labels and assigns addresses.
// Pass 2: generates memory words for Data/Instructions, builds .lst and .load outputs.

import java.util.*;

public final class MemoryManager {
    private final Memory mem = new Memory();                 // simulated RAM
    private final Map<String,Integer> symbols = new HashMap<>(); // label -> address
    private final List<String> listingLines = new ArrayList<>(); // for .lst file
    private final List<String> loadLines = new ArrayList<>();    // for .load file

    /**
     * Container for results returned after assembly:
     * - listing (.lst) lines
     * - load (.load) lines
     * - memory image with encoded program
     */
    public static class Result {
        public final List<String> listing, load;
        public final Memory memory;
        private Result(List<String> lst, List<String> ld, Memory m){
            listing = lst; load = ld; memory = m;
        }
    }

    /**
     * Assemble source program (two-pass).
     * @param src lines of assembly source code
     * @return Result containing listing, load output, and memory image
     */
    public Result assemble(String[] src) {
        pass1(src); // build symbol table
        pass2(src); // generate code and data
        return new Result(listingLines, loadLines, mem);
    }

    /**
     * PASS 1: Walk through the source program, record all labels
     * with their memory addresses in the symbol table.
     */
    private void pass1(String[] src) {
        int lc = 0; // location counter
        for (String raw : src) {
            String line = stripComment(raw).trim();
            if (line.isEmpty()) continue;

            int c = line.indexOf(':');
            if (c >= 0) { // label definition
                String label = line.substring(0, c).trim();
                symbols.put(label, lc);
                line = line.substring(c + 1).trim();
                if (line.isEmpty()) continue;
            }

            if (line.startsWith("LOC")) lc = parseNum(line.substring(3).trim());
            else lc++; // Data or Instruction each use 1 word
        }
    }

    /**
     * PASS 2: Using the symbol table, generate numeric values
     * for Data and instructions, write them into memory, and
     * produce .lst and .load file outputs.
     */
    private void pass2(String[] src) {
        int lc = 0;
        for (String raw : src) {
            String original = raw;
            String line = stripComment(raw).trim();
            if (line.isEmpty()) continue;

            int c = line.indexOf(':');
            if (c >= 0) {
                line = line.substring(c + 1).trim();
                if (line.isEmpty()) continue;
            }

            if (line.startsWith("LOC")) {
                lc = parseNum(line.substring(3).trim());
                continue;
            }

            if (line.startsWith("Data")) {
                String tok = line.substring(4).trim();
                int value = symbols.containsKey(tok) ? symbols.get(tok) : parseNum(tok);
                mem.write(lc, value);
                String a = String.format("%06o", lc);
                String w = String.format("%06o", value & 0xFFFF);
                listingLines.add(a + " " + w + " " + original);
                loadLines.add(a + " " + w);
                lc++;
                continue;
            }

            // Instruction case
//            String enc = Encoder.encodeInstruction(lc, line); // "AAAAAA IIIIII mnemonic..."
//            String[] parts = enc.split("\\s+");
//            int addr = Integer.parseInt(parts[0], 8);
//            int word = Integer.parseInt(parts[1], 8);
//            mem.write(addr, word);
//            listingLines.add(enc);
//            loadLines.add(parts[0] + " " + parts[1]);
//            lc++;
        }
    }

    /**
     * Remove comments from a line (anything after ';').
     */
    private static String stripComment(String s) {
        int i = s.indexOf(';');
        return (i >= 0) ? s.substring(0, i) : s;
    }

    /**
     * Parse a number string.
     * Supports decimal and octal (if string starts with 0 and has only 0–7).
     */
    private static int parseNum(String s) {
        s = s.trim();
        if (s.startsWith("0") && s.matches("^0[0-7]+$")) return Integer.parseInt(s, 8);
        return Integer.parseInt(s);
    }
}
