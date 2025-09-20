package src;

public class EncoderTest {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("===  Encoder Testing ===\n");

        // Here we are Testing all instruction categories
        testMiscellaneousInstructions();
        testLoadStoreInstructions();
        testTransferInstructions();
        testArithmeticInstructions();
        testRegisterToRegisterInstructions();
        testShiftRotateInstructions();
        testIOInstructions();
        testEdgeCases();
        testAdditionalCases();

        // Print final results
        System.out.println("\n=== Test Results ===");
        System.out.println("Tests Passed: " + testsPassed);
        System.out.println("Tests Failed: " + testsFailed);
        System.out.println("Total Tests: " + (testsPassed + testsFailed));

        if (testsFailed == 0) {
            System.out.println("*** ALL TESTS PASSED! ***");
        } else {
            System.out.println("*** " + testsFailed + " tests failed. ***");
        }
    }

    //Here we are testing miscellaneous Instructions
    private static void testMiscellaneousInstructions() {
        System.out.println("--- Testing Miscellaneous Instructions ---");

        assertEncode("HLT", "000000", "Basic HLT instruction");
        assertEncode("TRAP 5", "060005", "TRAP with code 5");
        assertEncode("TRAP 15", "060017", "TRAP with code 15 (max)");
    }


    private static void testLoadStoreInstructions() {
        System.out.println("\n--- Testing Load/Store Instructions ---");

        assertEncode("LDR 3,0,15", "003417", "Project example LDR 3,0,15");
        assertEncode("LDR 0,0,0", "002000", "LDR with all zeros");
        assertEncode("LDR 1,2,10", "002612", "LDR 1,2,10");
        assertEncode("STR 2,1,5", "005105", "STR 2,1,5");
        assertEncode("LDA 1,0,20", "006424", "LDA 1,0,20");
        assertEncode("LDX 1,7", "102107", "LDX 1,7");
        assertEncode("LDX 3,15", "102317", "LDX 3,15");
        assertEncode("STX 2,10", "104212", "STX 2,10");
    }


    private static void testTransferInstructions() {
        System.out.println("\n--- Testing Transfer Instructions ---");

        assertEncode("JZ 0,0,5", "020005", "JZ 0,0,5");
        assertEncode("JZ 2,1,15", "021117", "JZ 2,1,15");
        assertEncode("JNE 1,0,10", "022412", "JNE 1,0,10");
        assertEncode("JCC 0,0,8", "024010", "JCC 0,0,8");
        assertEncode("JMA 1,20", "026124", "JMA 1,20");
        assertEncode("JSR 2,10", "030212", "JSR 2,10");
        assertEncode("RFS 10", "032012", "RFS with return code 10");
        assertEncode("RFS", "032000", "RFS with no return code");
        assertEncode("SOB 3,0,5", "035405", "SOB 3,0,5");
        assertEncode("JGE 2,1,12", "037114", "JGE 2,1,12");
    }

    private static void testArithmeticInstructions() {
        System.out.println("\n--- Testing Arithmetic Instructions ---");

        assertEncode("AMR 1,0,15", "010417", "AMR 1,0,15");
        assertEncode("SMR 2,1,8", "013110", "SMR 2,1,8");
        assertEncode("AIR 3,10", "015412", "AIR 3,10");
        assertEncode("AIR 0,31", "014037", "AIR 0,31 (max immediate)");
        assertEncode("SIR 1,5", "016405", "SIR 1,5");
    }


    private static void testRegisterToRegisterInstructions() {
        System.out.println("\n--- Testing Register-to-Register Instructions ---");

        assertEncode("MLT 0,2", "160200", "MLT 0,2");
        assertEncode("MLT 2,0", "161000", "MLT 2,0");
        assertEncode("DVD 0,2", "162200", "DVD 0,2");
        assertEncode("TRR 1,3", "164700", "TRR 1,3");
        assertEncode("AND 2,1", "167100", "AND 2,1");
        assertEncode("ORR 3,0", "171400", "ORR 3,0");
        assertEncode("NOT 2", "173000", "NOT 2");
    }


    private static void testShiftRotateInstructions() {
        System.out.println("\n--- Testing Shift/Rotate Instructions ---");

        assertEncode("SRC 1,4,1,0", "062504", "SRC r=1, count=4, L/R=1, A/L=0");
        assertEncode("SRC 2,8,0,1", "063210", "SRC r=2, count=8, L/R=0, A/L=1");
        assertEncode("RRC 3,2,1,1", "065702", "RRC r=3, count=2, L/R=1, A/L=1");
    }


    private static void testIOInstructions() {
        System.out.println("\n--- Testing I/O Instructions ---");

        assertEncode("IN 0,0", "142000", "IN 0,0 (console keyboard)");
        assertEncode("IN 1,1", "142401", "IN 1,1 (console printer)");
        assertEncode("IN 2,31", "143037", "IN 2,31 (max device ID)");
        assertEncode("OUT 3,1", "145401", "OUT 3,1 (console printer)");
        assertEncode("CHK 1,2", "146402", "CHK 1,2 (card reader)");
    }


    private static void testEdgeCases() {
        System.out.println("\n--- Testing Edge Cases ---");

        assertEncode("LDR 3,3,31", "003737", "LDR with max values (r=3, ix=3, addr=31)");
        assertEncode("AIR 3,31", "015437", "AIR with max immediate (r=3, immed=31)");
        assertEncode("TRAP 15", "060017", "TRAP with max trap code");
        assertEncode("LDR 0,0,0", "002000", "LDR with min values");
        assertEncode("AIR 0,0", "014000", "AIR with min values");
        assertEncode("LDR 1,0,5 1", "002445", "LDR with indirect addressing (I=1)");
        assertEncode("STR 2,1,10 1", "005152", "STR with indirect addressing");
        assertEncode("MLT 3,3", "161700", "MLT with max registers (rx=3, ry=3)");
        assertEncode("SRC 3,15,1,1", "063717", "SRC with max values (r=3, count=15, L/R=1, A/L=1)");
    }


    private static void testAdditionalCases() {
        System.out.println("\n--- Testing Additional Cases ---");

        // indirect addressing tests
        assertEncode("JMA 2,15 1", "026257", "JMA with indirect addressing");
        assertEncode("JSR 0,31 1", "030077", "JSR with indirect addressing");

        // Zero immediate values
        assertEncode("SIR 0,0", "016000", "SIR with zero immediate");
        assertEncode("SIR 3,0", "017400", "SIR register 3, zero immediate");

        // More shift/rotate combinations
        assertEncode("SRC 0,1,0,0", "062001", "SRC minimal values");
        assertEncode("RRC 0,15,0,0", "064017", "RRC max count");

        // Boundary register combinations
        assertEncode("DVD 3,3", "163700", "DVD with same max registers");
        assertEncode("TRR 0,0", "164000", "TRR with zero registers");

        // More I/O edge cases
        assertEncode("OUT 0,31", "144037", "OUT with max device ID");
        assertEncode("CHK 3,0", "147400", "CHK with min device ID");
    }

    private static void assertEncode(String instruction, String expected, String description) {
        try {
            String actual = Encoder.encodeInstruction(0, instruction, true);
            String actualInstruction = actual.split(" ")[1];

            if (actualInstruction.equals(expected)) {
                System.out.println("PASS: " + description);
                System.out.println("  Instruction: " + instruction);
                System.out.println("  Expected:    " + expected);
                System.out.println("  Actual:      " + actualInstruction);
                testsPassed++;
            } else {
                System.out.println("FAIL: " + description);
                System.out.println("  Instruction: " + instruction);
                System.out.println("  Expected:    " + expected);
                System.out.println("  Actual:      " + actualInstruction);

                // Showing which characters differ
                if (expected.length() == actualInstruction.length()) {
                    StringBuilder diff = new StringBuilder("  Difference:  ");
                    for (int i = 0; i < expected.length(); i++) {
                        if (expected.charAt(i) == actualInstruction.charAt(i)) {
                            diff.append("-");
                        } else {
                            diff.append("^");
                        }
                    }
                    System.out.println(diff.toString());
                }
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + description);
            System.out.println("  Instruction: " + instruction);
            System.out.println("  Exception: " + e.getMessage());
            testsFailed++;
        }
        System.out.println();
    }
}