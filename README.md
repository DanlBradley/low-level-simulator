# low-level-simulator
Repo for the CSCI 6461 Computer System Architecture Project F25 - Team 6

## C6461 Simulator - Part 2
The simulator component has been updated from part 1 to include several new compoents:

1. All instructions have been implemented except CHK and part 4 instructions.
2. A cache of size 16 words has been implemented between the computer and memory. This cache utilizes a simple FIFO 
replacement policy: as soon as a cache miss occurs, the least recently used word is replaced.
3. In addition, we have developed the program located at `data/program.txt` to test the functionality of the 
simulator. This program first prompts the user to enter 20 numbers. Once they have done that, the program prompts the 
user to enter a guess number. Finally, the program prints out the closest of the first 20 numbers to the guess number.

# Quick Start - Part 2
The "Simulator Quick Start" section below still applies to part 2, except that now the UI displays the cache, an OUT 
display field, as well as a console input field to enter data through the IO stream.

To test the functionality of Program 1, follow the steps below:
1. First determine which program you want to run. Program 1 is located at `data/program.txt`.
2. Assemble the program selected using the following line:
```
java -jar build/assembler/*.jar data/program.txt
```
3. Run the simulator with the following line:
```
java -jar build/simulator/*.jar
```
4. The GUI should be preloaded with the correct data/load.txt file generated in step 2, and the 
octal input field should already be filled in with `000144`, which is the correct START location to run the program.
Select "IPL" to load the program into memory, and select the button under "PC" to set the PC to the correct location.
Finally, select "Run" to execute the program.
5. You will be prompted to enter the first 20 numbers into the console input field. You can do so by typing the number,
from -32768 to 32767, into the console input field, and pressing enter.
6. You will be prompted to enter a guess number into the console input field. You can do so by typing the number, from 
-32768 to 32767, into the console input field, and pressing enter.
7. The closest number will be displayed in the OUT display field.
8. Optionally, you can modify the line `AIR 0, 20           ; R0 = 20` in the `data/program.txt` file to change the 
number of numbers to be entered.
9. Finally, for troubleshooting, please refer to `6461 program one.mov` video for step by step instructions.

## C6461 Simulator - Part 1

## Overview
This is the Simulator component for the CSCI 6461 Computer Architecture project.
It provides a Java Swing–based user interface that allows users to:
1. View and modify CPU registers (GPRs, Index Registers, PC, MAR, MBR, IR)
2. Enter octal input and load values into registers
3. Load and execute assembled programs using IPL, Step, and Run controls
4. Monitor the current instruction in both octal and binary formats
5. Interact with and observe the internal state of the simulated machine
The simulator connects with the assembler output and serves as the main execution and testing interface for CSCI 
6461 assembly programs.
6. Note: The maximum memory value is `2048` 16-bit words. And as words are 16-bit, the simulator cannot accept 
octal values beyond `177777`.

## Simulator Quick Start
This project includes a runnable JAR file contained in the `build` directory. To run, the user must include an input
file as well as an output file. Test files have been provided in the `data` directory following the C6461 ISA
documentation.

To run the simulator, execute the following command from the root directory:
```
java -jar build/simulator/low-level-simulator.jar
```

You will be provided a GUI with several options to interact with the simulator. Steps to operate the simulator are as 
follows:
1. Determine which file you want to run. The `data/assembly.txt` file from the C6461 documentation is automatically 
filled into the GUI to test. There is also a `data/load_store_text.txt` file that contains all LD/STR instructions.
2. Determine where the first instruction of the program is located and enter that into the PC register by typing the 
correct octal into the "OCTAL INPUT" field and selecting the button located below the PC register.
   1. For `data/load_store_text.txt`, the first instruction is located at octal `000015`.
   2. For `data/assembly.txt`, the first instruction is located at octal `000016`.
3. Initiate the program using the IPL button.
4. Step through the program using the Step button. Alternatively, you can run the program using the Run button.
5. You can also manually load and store values into the MAR and MBR using the "OCTAL INPUT" and their 
corresponding buttons.




## Implementation Notes - Part 1

Part 1 implements the ComputerSimulatorGUI, a Java Swing interface that connects to the Computer class to control and
observe the CSCI 6461 machine simulator. It displays CPU registers (R0–R3, X1–X3, PC, MAR, MBR, IR) in octal format and
allows manual register loading using octal input. The GUI supports program loading through IPL, which assembles a
source file and initializes memory, and provides execution control via Step and Run functions. The IR is also shown in
binary for instruction inspection.

## C6461 Assembler - Part 0

## Overview
This is the assembler component for the C6461 Computer Architecture project. The assembler translates human-readable 
assembly language into machine code that can be executed by the C6461 simulator.

## Assembler Quick Start
This project includes a runnable JAR file contained in the `build` directory. To run, the user must include an input 
file as well as an output file. Test files have been provided in the `data` directory following the C6461 ISA 
documentation.

To run the assembler, execute the following command from the root directory:
```
java -jar build/assembler/*.jar data/assembly.txt data/listing.txt data/load.txt
```

The output files will placed as suggested above in the data/ folder.

Additionally, if you wish to run a full test of the encoder, run this command from the root directory:
```
java -jar build/encoder-test/*.jar
```

In addition, the `data/output_expected.txt` file contains the expected output file from C6461 doc pp. 20.

## Implementation Notes - Part 0
For part 0, the main class being run as a jar is src.assembler.Assembler. This file reads source files and outputs listing 
and load files via the src.simulator.FileIO class. The Assembler class is essentially a simplified 2-pass assembler, first 
collecting labels as a map, then generating listing and load file output thru system directives and instructions. The 
src.assembler.Encoder class is responsible for all instruction encodings, while the src.assembler.Assembler class directly handles 
directives.

## Instruction Set - All Opcodes

### Miscellaneous Instructions
| Opcode (Octal) | Mnemonic | Format    | Description                              |
|----------------|----------|-----------|------------------------------------------|
| 00             | HLT      | HLT       | Halt - stops the machine                 |
| 30             | TRAP     | TRAP code | Trap to memory address 0 (Part III only) |

### Load/Store Instructions
| Opcode (Octal) | Mnemonic | Format              | Description                     |
|----------------|----------|---------------------|---------------------------------|
| 01             | LDR      | LDR r,x,address[,I] | Load Register from Memory       |
| 02             | STR      | STR r,x,address[,I] | Store Register to Memory        |
| 03             | LDA      | LDA r,x,address[,I] | Load Register with Address      |
| 41             | LDX      | LDX x,address[,I]   | Load Index Register from Memory |
| 42             | STX      | STX x,address[,I]   | Store Index Register to Memory  |

### Transfer Instructions
| Opcode (Octal) | Mnemonic | Format               | Description                  |
|----------------|----------|----------------------|------------------------------|
| 10             | JZ       | JZ r,x,address[,I]   | Jump if Zero                 |
| 11             | JNE      | JNE r,x,address[,I]  | Jump if Not Equal            |
| 12             | JCC      | JCC cc,x,address[,I] | Jump if Condition Code       |
| 13             | JMA      | JMA x,address[,I]    | Unconditional Jump           |
| 14             | JSR      | JSR x,address[,I]    | Jump and Save Return Address |
| 15             | RFS      | RFS immed            | Return From Subroutine       |
| 16             | SOB      | SOB r,x,address[,I]  | Subtract One and Branch      |
| 17             | JGE      | JGE r,x,address[,I]  | Jump Greater than or Equal   |

### Arithmetic and Logical Instructions
| Opcode (Octal) | Mnemonic | Format              | Description                      |
|----------------|----------|---------------------|----------------------------------|
| 04             | AMR      | AMR r,x,address[,I] | Add Memory to Register           |
| 05             | SMR      | SMR r,x,address[,I] | Subtract Memory from Register    |
| 06             | AIR      | AIR r,immed         | Add Immediate to Register        |
| 07             | SIR      | SIR r,immed         | Subtract Immediate from Register |

### Register to Register Operations
| Opcode (Octal) | Mnemonic | Format    | Description                   |
|----------------|----------|-----------|-------------------------------|
| 70             | MLT      | MLT rx,ry | Multiply Register by Register |
| 71             | DVD      | DVD rx,ry | Divide Register by Register   |
| 72             | TRR      | TRR rx,ry | Test Equality of Registers    |
| 73             | AND      | AND rx,ry | Logical AND                   |
| 74             | ORR      | ORR rx,ry | Logical OR                    |
| 75             | NOT      | NOT rx    | Logical NOT                   |

### Shift/Rotate Operations
| Opcode (Octal) | Mnemonic | Format              | Description              |
|----------------|----------|---------------------|--------------------------|
| 31             | SRC      | SRC r,count,L/R,A/L | Shift Register by Count  |
| 32             | RRC      | RRC r,count,L/R,A/L | Rotate Register by Count |

### I/O Operations
| Opcode (Octal) | Mnemonic | Format      | Description                    |
|----------------|----------|-------------|--------------------------------|
| 61             | IN       | IN r,devid  | Input Character to Register    |
| 62             | OUT      | OUT r,devid | Output Character from Register |
| 63             | CHK      | CHK r,devid | Check Device Status            |

### Floating Point Instructions (Part IV only)
| Opcode (Octal) | Mnemonic | Format                | Description                  |
|----------------|----------|-----------------------|------------------------------|
| 33             | FADD     | FADD fr,x,address[,I] | Floating Add                 |
| 34             | FSUB     | FSUB fr,x,address[,I] | Floating Subtract            |
| 35             | VADD     | VADD fr,x,address[,I] | Vector Add                   |
| 36             | VSUB     | VSUB fr,x,address[,I] | Vector Subtract              |
| 37             | CNVRT    | CNVRT r,x,address[,I] | Convert Fixed/Floating Point |
| 50             | LDFR     | LDFR fr,x,address[,I] | Load Floating Register       |
| 51             | STFR     | STFR fr,x,address[,I] | Store Floating Register      |

## Assembly Directives
- `LOC n` - Set location counter to n (decimal)
- `Data n` - Allocate word with value n (decimal)
- `Data labelname` - Allocate word with address of label
