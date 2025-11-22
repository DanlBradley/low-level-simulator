# Program 2 Usage Guide
## Paragraph Word Search

---

## Quick Start

### 1. Assemble the Program

```bash
java -jar build/assembler/*.jar data/program_part_two.txt data/listing_part_two.txt data/load_part_two.txt
```

This will create:
- `data/listing_part_two.txt` - Listing file with addresses
- `data/load_part_two.txt` - Machine code ready for simulator

### 2. Run the Simulator

```bash
java -jar build/simulator/*.jar
```

### 3. Load the Program

1. In the GUI, update the load file path to `data/load_part_two.txt`
2. Set the PC (Program Counter) to **1100** (octal: `002114`)
3. Click **IPL** to load the program
4. Click the button under **PC** to set PC to 1100
5. Click **Run** to execute

---

## How to Use the Program

### Step-by-Step Execution

#### **Step 1: Enter 6 Sentences**

The program will display:
```
ENTER 6 SENTENCES:
```

**Instructions**:
- Type one sentence at a time in the console input field
- Each sentence should end with a period (`.`)
- Press Enter after typing each period
- The program reads character-by-character until 6 periods are detected

**Example Input**:
```
The cat sat on the mat.
The dog ran in the yard.
Birds fly in the sky.
Fish swim in the ocean.
Trees grow in the forest.
Stars shine at night.
```

**Important Notes**:
- Each sentence MUST end with a period
- The program counts sentences by periods, so after the 6th period, it stops reading
- Spaces after periods are fine
- You can have multi-word sentences

#### **Step 2: View the Paragraph**

After entering 6 sentences, the program will echo them back:
```
The cat sat on the mat.The dog ran in the yard.Birds fly in the sky.Fish swim in the ocean.Trees grow in the forest.Stars shine at night.
```

#### **Step 3: Enter Search Word**

The program will display:
```
ENTER SEARCH WORD:
```

**Instructions**:
- Type the word you want to search for
- Press Enter
- The word should NOT include punctuation

**Example**:
```
cat
```

#### **Step 4: View Results**

The program will output one of two results:

**If Word Found**:
```
FOUND IN SENTENCE 1 WORD 2
```

This means the word "cat" was found in sentence 1, and it's the 2nd word in that sentence.

**If Word Not Found**:
```
NOT FOUND
```

---

## Example Test Cases

### Test Case 1: Word in First Sentence

**Input**:
```
Sentences:
1. The quick brown fox jumps.
2. Over the lazy dog rests.
3. Under a shady tree sits.
4. A wise old owl watches.
5. The moon shines brightly tonight.
6. Stars twinkle in darkness above.

Search Word: fox
```

**Expected Output**:
```
FOUND IN SENTENCE 1 WORD 4
```

### Test Case 2: Word in Middle Sentence

**Input**:
```
Sentences:
1. Apple trees grow tall here.
2. Banana plants need warm weather.
3. Cherry blossoms bloom in spring.
4. Dates come from palm trees.
5. Elderberries are small and dark.
6. Figs grow in Mediterranean regions.

Search Word: bloom
```

**Expected Output**:
```
FOUND IN SENTENCE 3 WORD 2
```

### Test Case 3: Word Not Found

**Input**:
```
Sentences:
1. Cats sleep during the day.
2. Dogs play in the park.
3. Birds sing early morning songs.
4. Fish swim in clear water.
5. Horses run across open fields.
6. Rabbits hop through the grass.

Search Word: elephant
```

**Expected Output**:
```
NOT FOUND
```

### Test Case 4: Word at Sentence Start

**Input**:
```
Sentences:
1. Programming is fun and challenging.
2. Computers process data very quickly.
3. Networks connect the world together.
4. Software runs on hardware systems.
5. Algorithms solve complex problems efficiently.
6. Data structures organize information well.

Search Word: Programming
```

**Expected Output**:
```
FOUND IN SENTENCE 1 WORD 1
```

### Test Case 5: Word at Sentence End

**Input**:
```
Sentences:
1. She walks to school daily.
2. He reads books every night.
3. They play games together often.
4. We study hard for tests.
5. You work on projects diligently.
6. I practice piano regularly.

Search Word: regularly
```

**Expected Output**:
```
FOUND IN SENTENCE 6 WORD 3
```

---

## Program Architecture

### Memory Map

| Address Range | Purpose |
|---------------|---------|
| 6-99 | Constants, variables, pointers |
| 100-699 | Paragraph storage (~600 chars) |
| 700-749 | Search word storage (~50 chars) |
| 750-755 | Sentence table (6 addresses) |
| 800-999 | Message strings |
| 1000-1099 | Jump table |
| 1100-1199 | Main program |
| 1200+ | Subroutines |

### Subroutines Implemented

1. **READPARAGRAPH** (1200) - Reads 6 sentences from keyboard
2. **PRINTPARAGRAPH** (1400) - Echoes paragraph to console
3. **READWORD** (1500) - Reads search word
4. **ISDELIMITER** (1600) - Checks if character is space/period/comma/newline
5. **STRCMP** (1700) - Compares two null-terminated strings
6. **SKIP_DELIM** (1900) - Advances pointer past delimiters
7. **SKIP_WORD** (2000) - Advances pointer past word characters
8. **SEARCHWORD_SUB** (2100) - Main search algorithm
9. **PRINTRESULT** (2500) - Displays search results
10. **PRINTSTR** (2700) - Prints null-terminated string
11. **PRINTNUM** (2850) - Prints single-digit number

### Key Algorithms

#### READPARAGRAPH Algorithm
```
For each sentence (up to 6):
    1. Store sentence start address in table
    2. Read characters until period is found
    3. Store period in memory
    4. Increment sentence count
    5. Check if 6 sentences read
    6. If yes, terminate; else continue
End with null terminator
```

#### SEARCHWORD_SUB Algorithm
```
For each sentence (1 to 6):
    For each word in sentence:
        1. Skip delimiters to find word start
        2. Compare word with search word using STRCMP
        3. If match: record sentence and word number, return
        4. If no match: skip to next word
    Next word
Next sentence
If not found, set MATCHFLAG = 0
```

#### STRCMP Algorithm
```
Loop:
    Load char from string1 and string2
    If both are null: return MATCH
    If chars differ: return NO MATCH
    Advance both pointers
    Continue loop
```

---

## Debugging Tips

### If the program doesn't seem to read input:
1. Make sure you're entering text in the **Console Input Field** at the bottom of the GUI
2. Press Enter after each sentence ending with period
3. Check that PC is set to 1100 before running

### If search always returns "NOT FOUND":
1. Make sure your search word matches exactly (case-sensitive)
2. Don't include punctuation in the search word (search "cat" not "cat.")
3. Make sure you entered exactly 6 sentences with periods

### If the program crashes or hangs:
1. Check the console output for error messages
2. Use **Step** instead of **Run** to trace through execution
3. Watch register values to see where it's getting stuck
4. Look for infinite loops in the simulator console output

### Common Issues:

**Issue**: Program stops reading after first sentence
- **Cause**: Might be detecting period too early
- **Solution**: Make sure you're pressing Enter after the period

**Issue**: Search finds wrong word position
- **Cause**: Delimiter detection or word counting logic
- **Solution**: Check that spaces, commas, and periods are properly recognized as delimiters

**Issue**: Output shows garbage characters
- **Cause**: Null terminator missing or memory overflow
- **Solution**: Verify paragraph doesn't exceed ~600 characters

---

## Technical Details

### Word Delimiters
The program recognizes these as word boundaries:
- Space (ASCII 32)
- Period (ASCII 46)
- Comma (ASCII 44)
- Newline (ASCII 10)

### String Comparison
- **Case-sensitive**: "Cat" ≠ "cat"
- **Exact match**: Must match all characters
- **No partial matches**: "cat" won't match "cats"

### Position Counting
- Sentences numbered **1-6** (not 0-5)
- Words numbered starting from **1** in each sentence
- First word after delimiters is word 1

### Memory Limits
- Maximum paragraph length: ~600 characters
- Maximum search word length: ~50 characters
- Fixed 6 sentences (no more, no less)

---

## Modifications and Extensions

### To Support More Sentences:
1. Change `SIX: Data 6` to desired number
2. Increase `SENTTABLE` allocation
3. Update "ENTER 6 SENTENCES:" message

### To Add Case-Insensitive Search:
Modify STRCMP to convert both characters to uppercase before comparing:
```assembly
; Add uppercase conversion
; If char >= 97 AND char <= 122, subtract 32
```

### To Show All Occurrences:
Modify SEARCHWORD_SUB to continue searching after first match and store all positions in an array.

### To Handle Longer Words for Printing:
Modify PRINTNUM to handle multi-digit numbers (borrow PARSE from Program 1).

---

## Files Generated

After assembling:
- **listing_part_two.txt** - Human-readable listing with addresses and opcodes
- **load_part_two.txt** - Machine code in octal format for simulator

---

## Troubleshooting Assembly Errors

If assembly fails:

1. **Check label definitions**: All labels in jump table must exist
2. **Check LOC directives**: Addresses must be in decimal
3. **Check Data directives**: ASCII values must be in decimal
4. **Check instruction syntax**: Format must match ISA exactly

Common assembly errors:
- Undefined label: Label used but not defined
- Invalid opcode: Typo in instruction name
- Address out of range: Exceeds memory limit (2048 words)

---

## Performance Notes

### Expected Execution Time:
- Input: User-dependent (typing speed)
- Search: Very fast (< 1 second for typical input)
- Output: Immediate

### Memory Usage:
- Program code: ~650 words
- Data storage: ~700 words
- Total: ~1350 words (65% of available 2048)

---

## Summary

Program 2 successfully implements:
✓ Reading 6 sentences from keyboard input
✓ Storing text in memory with sentence boundaries
✓ Echoing paragraph to console
✓ Reading search word from user
✓ Searching paragraph with word-level granularity
✓ Tracking sentence and word positions
✓ Displaying results or "NOT FOUND" message

The implementation follows the design guide and reuses patterns from Program 1 where applicable.

---

## Contact / Support

For issues or questions:
- Check the implementation guide: `PROGRAM_TWO_IMPLEMENTATION_GUIDE.md`
- Review the source code: `data/program_part_two.txt`
- Examine the listing file after assembly: `data/listing_part_two.txt`

Happy searching! 🔍
