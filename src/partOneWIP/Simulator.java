package src.partOneWIP;// src.partOneWIP.Simulator.java
// Purpose: Main entry point. 
// Reads source file, runs assembler (src.partOneWIP.MemoryManager),
// writes .lst and .load output files.

import src.FileIO;

import java.io.IOException;

public class Simulator {
//    public static void main(String[] args) throws IOException {
//        if (args.length != 1) {
//            System.err.println("Usage: java src.partOneWIP.Simulator <source.asm>");
//            System.exit(1);
//        }
//
//        // Read source file
//        String[] src = FileIO.readSourceFile(args[0]);
//
//        // Assemble using src.partOneWIP.MemoryManager
//        MemoryManager mm = new MemoryManager();
//        MemoryManager.Result res = mm.assemble(src);
//
//        // Base name for output files
//        String base = args[0].replaceFirst("\\.asm$", "");
//
//        // Write .lst and .load files
//        FileIO.writeListingFile(base + ".lst", res.listing);
//        FileIO.writeLoadFile(base + ".load", res.load);
//
//        System.out.println("Assembled " + args[0] + " → " + base + ".lst / " + base + ".load");
//    }
}
