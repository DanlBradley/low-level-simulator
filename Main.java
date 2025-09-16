public class Main {

    /*
    This is the 2-pass assembler. It loops thru and also calls the "SimpleAssembler" class.
    I should have called the assembler something else since it just encodes. So like encoder.
     */
    public static string[] Assembler(String[] args) {
        // 1. Get input sourceCode as a string[] from the readFile method (doesn't exist yet, use fake example)
        string[] sourceCodeExample = {
                "LOC 6 ;BEGIN AT LOCATION 6",
                "Data 10 ;PUT 10 AT LOCATION 6",
                "Data 3 ;PUT 3 AT LOCATION 7",
                "LDX 2,7 ; X2 GETS 3"}

        // 2. Parse the code:
        integer currentLocation;
        for (string lineOfCode: sourceCodeExample) {
            //Actually run the assembler, part of which is the encoder

            //First pass..
            //some example code

            //Second pass: actually encode I suppose
            Encoder.assemble(currentLocation, lineOfCode);

        }

        // 3. EXPECTED OUTPUT - send out
        string[] output = {
                "LOC 6 ;BEGIN AT LOCATION 6",
                "000006 000012 Data 10 ;PUT 10 AT LOCATION 6",
                "000007 000003 Data 3 ;PUT 3 AT LOCATION 7"
        }
    }
}