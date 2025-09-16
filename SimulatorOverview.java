//import java.util.HashMap;
//import java.util.Map;
//
////1. Need a method to read in the file
//public string[] readFile(){
//    /*
//    input: the file (so read in the file here)
//    input: input.txt
//    output: a string list (line for line that can be read in a for loop)
//    ["LOC 6", "DATA 3", "DATA 10", "LDX 1,8", ...]
//     */
//}
//
//
////2. Memory management - loop thru output from readFile and generate encodedOutput
//public void memoryManager(){
//    /*
//    Input: string[] inputFile from readFile method
//    -Stores current location, and memory
//    Example:
//    currentLoc = 0;
//    1. LOC 6 -> currentLoc = 6;
//    2. DATA 10 -> memory[currentLoc] = 10; currentLoc++; ->
//        encodedInstructions.add(assemble(currentLoc, inputFile[currentLoc]));
//    3. DATA 3 -> memory[currentLoc] = 3;
//    4. LDR 2,1,8 -> memory[8] = memory[1] + memory[2];
//
//    OUTPUT:
//    string[] encodedOutput (formatted of octal codes i.e.000024 003110 LDR 2,1,8)
//     */
//    private static Map<Integer, Integer> memory = new HashMap<>();
//    private static integer currentLoc;
//    private static string[] encodedInstructions;
//}
//
//
////3. Write the file
//public void writeFile(){
//    /*
//    Input: formatted string[] from memoryManager
//    OUTPUT: output.txt
//     */
//}