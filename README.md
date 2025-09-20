# low-level-simulator
Repo for the CSCI 6461 Computer System Architecture Project F25 - Team 6

## C6461 src.Assembler - Part 0

## Overview
This is the assembler component for the C6461 Computer Architecture project. The assembler translates human-readable 
assembly language into machine code that can be executed by the C6461 simulator.

## Quick Start
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
For part 0, the main class being run as a jar is src.Assembler. This file reads source files and outputs listing 
and load files via the src.FileIO class. The Assembler class is essentially a simplified 2-pass assembler, first 
collecting labels as a map, then generating listing and load file output thru system directives and instructions. The 
src.Encoder class is responsible for all instruction encodings, while the src.Assembler class directly handles 
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

## Implementation Notes
- **Two-pass assembler:** Pass 1 builds symbol table, Pass 2 generates code
- **16-bit instruction format:** `|Opcode(6)|R(2)|IX(2)|I(1)|Address(5)|`
- **Octal output:** All addresses and instructions in 6-digit octal
- **Files:** Generate listing file (with source) + load file (addresses/instructions only)

## Team Deliverables for Part 0
1. JAR file with runnable assembler
2. Documentation on how to use the assembler
3. Basic design notes
4. Test case assembly file
5. Expected listing output for test case
6. GitHub commit logs