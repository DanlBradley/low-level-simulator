package src;

public class DEBUGTest {

    public static void main(String[] args) {
        System.out.println(Encoder.encodeInstruction(30, "LDR 1,1,15,1", false));
        System.out.println(Encoder.encodeInstruction(30, "STR 1,1,15,1", false));
        System.out.println(Encoder.encodeInstruction(30, "LDA 1,1,15,1", false));
        System.out.println(Encoder.encodeInstruction(1, "LDX 2,15,1", false));
        System.out.println(Encoder.encodeInstruction(1, "STX 2,15,1", false));
        System.out.println(Encoder.encodeInstruction(1, "JZ 2,1,15", false));
        System.out.println(Encoder.encodeInstruction(1, "JNE 2,1,15", false));
        System.out.println(Encoder.encodeInstruction(1, "JCC 2,3,15,1", false));
        System.out.println(Encoder.encodeInstruction(1, "JMA 2,15,1", false));
        System.out.println(Encoder.encodeInstruction(1, "JSR 2,15,1", false));
        System.out.println(Encoder.encodeInstruction(1, "RFS 2", false));
        System.out.println(Encoder.encodeInstruction(1, "SOB 2,1,15,1", false));
        System.out.println(Encoder.encodeInstruction(1, "JGE 2,1,15,1", false));
//        System.out.println(Encoder.encodeInstruction(1, "LDX 2,7", false));
//        System.out.println(Encoder.encodeInstruction(1, "LDX 2,7", false));
//        System.out.println(Encoder.encodeInstruction(1, "LDX 2,7", false));
//        System.out.println(Encoder.encodeInstruction(1, "LDX 2,7", false));
//        System.out.println(Encoder.encodeInstruction(1, "LDX 2,7", false));
//        System.out.println(Encoder.encodeInstruction(1, "LDX 2,7", false));
//        System.out.println(Encoder.encodeInstruction(1, "LDX 2,7", false));
//        System.out.println(Encoder.encodeInstruction(1, "LDX 2,7", false));
//        System.out.println(Encoder.encodeInstruction(1, "LDX 2,7", false));
//        System.out.println(Encoder.encodeInstruction(1, "LDX 2,7", false));
//        System.out.println(Encoder.encodeInstruction(1, "LDX 2,7", false));
//        System.out.println(Encoder.encodeInstruction(1, "LDX 2,7", false));
    }

}
