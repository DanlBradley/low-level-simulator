# Program 2 Implementation Guide
## Text Search in Paragraph (6 Sentences)

---

## Table of Contents
1. [Overview](#overview)
2. [Requirements Analysis](#requirements-analysis)
3. [Key Challenges](#key-challenges)
4. [Memory Layout](#memory-layout)
5. [Reusable Components from Program 1](#reusable-components-from-program-1)
6. [New Subroutines Needed](#new-subroutines-needed)
7. [Algorithm Design](#algorithm-design)
8. [Implementation Steps](#implementation-steps)
9. [Testing Strategy](#testing-strategy)
10. [Assembly Code Structure](#assembly-code-structure)

---

## Overview

**Objective**: Read a paragraph of 6 sentences from input, print them, ask the user for a search word, find the word in the paragraph, and print the word along with its sentence number and position within that sentence.

**Input**:
- A paragraph of 6 sentences (from keyboard/console, since card reader device 2 is not implemented)
- A search word from the user

**Output**:
- The 6 sentences printed to console
- If word found: the word, sentence number, and word position
- If word not found: appropriate message

---

## Requirements Analysis

### Functional Requirements
1. **Input Reading**: Read 6 sentences character-by-character until a paragraph terminator (e.g., period followed by space or newline for each sentence)
2. **Storage**: Store the entire paragraph in memory with sentence boundaries marked
3. **Display**: Print all 6 sentences to console printer
4. **Search Input**: Prompt and read a search word from user
5. **Search Algorithm**: Locate the word in the stored paragraph
6. **Result Output**: Display word, sentence number (1-6), and word position within sentence

### Non-Functional Requirements
- Efficient memory usage (paragraph storage ~500-1000 characters max)
- Handle edge cases: word not found, word at sentence boundaries, case sensitivity
- Reuse existing subroutines where possible

---

## Key Challenges

### 1. File Input Limitation
**Problem**: The simulator's card reader (device 2) is NOT implemented (see `Computer.java:473-475`).

**Solution Options**:
- **Option A (Recommended)**: Use keyboard input (device 0) and have user type the paragraph interactively
- **Option B**: Extend the simulator to implement card reader functionality (requires Java code modification)
- **Option C**: Pre-load the paragraph into memory using `Data` directives

**Recommended Approach**: Use keyboard input with a special terminator sequence (e.g., "###" or double period "..") to mark end of paragraph input.

### 2. Sentence Boundary Detection
**Challenge**: Identify where each sentence ends.

**Solution**:
- Detect period (ASCII 46) followed by space or newline
- Maintain a sentence counter
- Store sentence start addresses in a table

### 3. Word Boundary Detection
**Challenge**: Identify individual words separated by spaces, punctuation, newlines.

**Solution**:
- Use space (ASCII 32), period (46), comma (44), newline (10) as delimiters
- Build word-by-word comparison during search

### 4. String Comparison
**Challenge**: Compare search word with words in paragraph character-by-character.

**Solution**: Implement a string matching subroutine similar to strcmp in C.

---

## Memory Layout

### Low Memory (6-99): Constants and Variables

```assembly
LOC 6
; Jump table address
JUMPTABLEADDR:  Data JUMPTABLE

; ASCII Constants
PERIOD:         Data 46    ; '.'
SPACE:          Data 32    ; ' '
NEWLINE:        Data 10    ; '\n'
COMMA:          Data 44    ; ','
HASH:           Data 35    ; '#' (for end-of-input marker)
ZERO:           Data 0
ONE:            Data 1
SIX:            Data 6

; Working Variables
SENTENCECOUNT:  Data 0     ; Current sentence number (1-6)
CHARCOUNT:      Data 0     ; Characters read so far
WORDCOUNT:      Data 0     ; Words in current sentence
MATCHFLAG:      Data 0     ; 1 if word found, 0 otherwise
FOUNDSENTENCE:  Data 0     ; Sentence number where found
FOUNDWORD:      Data 0     ; Word position where found
TEMP:           Data 0
TEMP2:          Data 0

; Memory addresses
PARAGRAPHADDR:  Data PARAGRAPH    ; Start of text storage
SEARCHWORDADDR: Data SEARCHWORD   ; Search word storage
SENTENCETABLE:  Data SENTTABLE    ; Sentence start addresses
MSG1ADDR:       Data MSG1          ; Prompt messages
MSG2ADDR:       Data MSG2
MSG3ADDR:       Data MSG3

; Index register temp storage
TEMPX1:         Data 0
TEMPX2:         Data 0
TEMPX3:         Data 0
```

### Text Storage Area (100-700)

```assembly
LOC 100
PARAGRAPH:      Data 0     ; Reserve ~600 words for paragraph
                           ; (enough for ~600 characters)

LOC 700
SEARCHWORD:     Data 0     ; Reserve ~50 words for search word

LOC 750
SENTTABLE:      Data 0     ; 6 entries for sentence start addresses
```

### Messages Area (800-900)

```assembly
LOC 800
MSG1:   Data 69    ; 'E'
        Data 78    ; 'N'
        Data 84    ; 'T'
        ...
        Data 10    ; '\n'
        Data 0     ; null terminator

LOC 850
MSG2:   Data 69    ; "ENTER SEARCH WORD:"
        ...

LOC 880
MSG3:   Data 70    ; "FOUND IN SENTENCE"
        ...
```

### Code Section (1000+)

```assembly
LOC 1000
JUMPTABLE:
        Data READPARAGRAPH      ; 0
        Data PRINTPARAGRAPH     ; 1
        Data READWORD           ; 2
        Data SEARCHWORD_SUB     ; 3
        Data PRINTRESULT        ; 4
        Data STRCMP             ; 5
        Data ISDELIMITER        ; 6
        Data PRINTSTR           ; 7 (reuse from Program 1)
        ...

LOC 1100
START:
        ; Main program logic
```

---

## Reusable Components from Program 1

### 1. PRINTSTR Subroutine
**Location**: `program_part_one.txt:451-484`

**Purpose**: Print null-terminated string to console.

**Usage**:
- Print prompt messages
- Can be used directly without modification

**How to Reuse**:
```assembly
LDX 2, MSG1ADDR          ; Load message address into X2
LDX 1, JUMPTABLEADDR
JSR 1, 7, 1              ; Call PRINTSTR (offset 7 in jump table)
```

### 2. Index Register Save/Restore Pattern
**Location**: Throughout Program 1 subroutines

**Pattern**:
```assembly
SUBROUTINE:
    ; Save index registers
    STX 1, TEMPX1
    STX 2, TEMPX2
    STX 3, TEMPX3

    ; ... subroutine logic ...

    ; Restore index registers
    LDX 1, TEMPX1
    LDX 2, TEMPX2
    LDX 3, TEMPX3
    RFS 0
```

### 3. Character Input Pattern
**Location**: `program_part_one.txt:279-295`

**Pattern**:
```assembly
READCHAR:
    IN 1, 0                  ; Read character into R1
    LDR 2, 0, NEWLINE        ; Check for newline
    TRR 1, 2
    JCC 3, 1, DONE_OFFSET, 1 ; Jump if newline
    ; Process character...
```

### 4. Loop and Counter Pattern
**Location**: `program_part_one.txt:152-174` (READLOOP)

**Pattern**:
```assembly
    LDR 0, 0, ZERO
    AIR 0, 6             ; R0 = 6 (for 6 sentences)
    STR 0, 0, COUNTER

LOOP:
    ; ... process item ...

    LDR 0, 0, COUNTER
    SIR 0, 1             ; Decrement
    STR 0, 0, COUNTER
    JZ 0, 1, DONE, 1     ; Exit if zero
    JMA 1, LOOP, 1       ; Continue loop
```

### 5. Memory Pointer Increment
**Location**: `program_part_one.txt:162-166`

**Pattern**:
```assembly
    STX 2, TEMP
    LDR 0, 0, TEMP
    AIR 0, 1             ; Increment pointer
    STR 0, 0, TEMP
    LDX 2, TEMP
```

---

## New Subroutines Needed

### 1. READPARAGRAPH
**Purpose**: Read 6 sentences from keyboard input and store in memory.

**Input**: None (reads from device 0)

**Output**:
- Paragraph stored at PARAGRAPH address
- Sentence table populated with start addresses
- Returns total character count

**Algorithm**:
```
Initialize:
    sentenceCount = 0
    charPointer = PARAGRAPH

While sentenceCount < 6:
    Store current address in SENTTABLE[sentenceCount]

    While not end of sentence:
        Read character
        Store at charPointer
        Increment charPointer

        If character is period:
            If next character is space/newline:
                Increment sentenceCount
                Break inner loop

Store null terminator
Return
```

**Key Instructions**:
- `IN r, 0` - Read character
- `STR r, x, address` - Store character
- Index register X2 for character pointer
- Index register X3 for sentence table pointer

### 2. PRINTPARAGRAPH
**Purpose**: Print stored paragraph to console.

**Input**: None (uses PARAGRAPH memory area)

**Output**: Text printed to console printer

**Algorithm**:
```
charPointer = PARAGRAPH

While *charPointer != 0:
    Load character at charPointer
    Print character using OUT
    Increment charPointer

Print final newline
Return
```

**Key Instructions**:
- `LDR r, x, 0` - Load character
- `OUT r, 1` - Print to console
- `JZ` - Check for null terminator

### 3. READWORD
**Purpose**: Read search word from keyboard.

**Input**: None (reads from device 0)

**Output**: Word stored at SEARCHWORD address, null-terminated

**Algorithm**:
```
charPointer = SEARCHWORD

While true:
    Read character

    If character is newline or space:
        Break

    Store character at charPointer
    Increment charPointer

Store null terminator at charPointer
Return
```

**Reuse**: Similar to READPARAGRAPH but simpler (single word).

### 4. SEARCHWORD_SUB
**Purpose**: Search for word in paragraph and record position.

**Input**:
- SEARCHWORD contains target word
- PARAGRAPH contains text
- SENTTABLE contains sentence boundaries

**Output**:
- MATCHFLAG = 1 if found, 0 otherwise
- FOUNDSENTENCE = sentence number (1-6)
- FOUNDWORD = word position in sentence

**Algorithm**:
```
For sentenceNum = 1 to 6:
    sentenceStart = SENTTABLE[sentenceNum - 1]
    wordNum = 1
    wordStart = sentenceStart

    While not end of sentence:
        Skip delimiters to find word start

        If STRCMP(wordStart, SEARCHWORD) == match:
            MATCHFLAG = 1
            FOUNDSENTENCE = sentenceNum
            FOUNDWORD = wordNum
            Return

        Skip to next delimiter (end of current word)
        wordNum++

MATCHFLAG = 0
Return
```

**Key Instructions**:
- Nested loops (sentence iteration, word iteration)
- Call STRCMP subroutine
- Use index registers carefully

### 5. STRCMP
**Purpose**: Compare two null-terminated strings.

**Input**:
- X1 points to string 1
- X2 points to string 2

**Output**:
- R0 = 1 if match, 0 if no match
- Condition code set appropriately

**Algorithm**:
```
While true:
    char1 = *string1
    char2 = *string2

    If char1 == 0 AND char2 == 0:
        Return 1 (match)

    If char1 != char2:
        Return 0 (no match)

    string1++
    string2++
```

**Key Instructions**:
- `LDR` with index registers
- `TRR` for comparison
- `JZ`, `JNE` for conditional logic

### 6. ISDELIMITER
**Purpose**: Check if character is a word delimiter.

**Input**: R0 contains character

**Output**: R1 = 1 if delimiter, 0 otherwise

**Algorithm**:
```
If char == SPACE: return 1
If char == PERIOD: return 1
If char == COMMA: return 1
If char == NEWLINE: return 1
Else: return 0
```

**Optimization**: Use series of comparisons with early exit.

### 7. PRINTRESULT
**Purpose**: Print search results.

**Input**:
- MATCHFLAG
- FOUNDSENTENCE
- FOUNDWORD
- SEARCHWORD

**Output**: Formatted result to console

**Algorithm**:
```
If MATCHFLAG == 0:
    Print "WORD NOT FOUND"
    Return

Print "FOUND: "
Print search word (using PRINTSTR)
Print "IN SENTENCE "
Print sentence number (convert to ASCII)
Print "WORD "
Print word position (convert to ASCII)
Print newline
```

**Reuse**: Can adapt PARSE subroutine from Program 1 for number printing.

---

## Algorithm Design

### Main Program Flow

```assembly
START:
    ; 1. Print prompt: "ENTER 6 SENTENCES:"
    LDX 2, MSG1ADDR
    LDX 1, JUMPTABLEADDR
    JSR 1, 7, 1              ; Call PRINTSTR

    ; 2. Read paragraph
    JSR 1, 0, 1              ; Call READPARAGRAPH

    ; 3. Print the paragraph
    JSR 1, 1, 1              ; Call PRINTPARAGRAPH

    ; 4. Print prompt: "ENTER SEARCH WORD:"
    LDX 2, MSG2ADDR
    JSR 1, 7, 1              ; Call PRINTSTR

    ; 5. Read search word
    JSR 1, 2, 1              ; Call READWORD

    ; 6. Search for word
    JSR 1, 3, 1              ; Call SEARCHWORD_SUB

    ; 7. Print result
    JSR 1, 4, 1              ; Call PRINTRESULT

    ; 8. Halt
    HLT
```

### READPARAGRAPH Detailed Algorithm

```assembly
READPARAGRAPH:
    ; Save registers
    STX 1, TEMPX1
    STX 2, TEMPX2
    STX 3, TEMPX3

    ; Initialize
    LDR 0, 0, ZERO
    STR 0, 0, SENTENCECOUNT

    LDX 1, JUMPTABLEADDR
    LDX 2, PARAGRAPHADDR      ; X2 = character pointer
    LDX 3, SENTENCETABLE      ; X3 = sentence table pointer

SENTENCE_LOOP:
    ; Store sentence start address in table
    STX 2, TEMP
    LDR 0, 0, TEMP
    STR 0, 3, 0               ; SENTTABLE[X3] = current address

    ; Increment sentence table pointer
    STX 3, TEMP
    LDR 0, 0, TEMP
    AIR 0, 1
    STR 0, 0, TEMP
    LDX 3, TEMP

CHAR_LOOP:
    ; Read character
    IN 1, 0

    ; Store character
    STR 1, 2, 0

    ; Increment character pointer
    STX 2, TEMP
    LDR 0, 0, TEMP
    AIR 0, 1
    STR 0, 0, TEMP
    LDX 2, TEMP

    ; Check for period
    LDR 2, 0, PERIOD
    TRR 1, 2
    JNE 1, 1, CHAR_LOOP, 1    ; If not period, continue

    ; Found period - check for sentence end
    ; (Simplified: assume period always ends sentence)

    ; Increment sentence count
    LDR 0, 0, SENTENCECOUNT
    AIR 0, 1
    STR 0, 0, SENTENCECOUNT

    ; Check if 6 sentences read
    LDR 2, 0, SIX
    TRR 0, 2
    JCC 3, 1, PARA_DONE, 1    ; If equal, done

    ; Continue to next sentence
    JMA 1, SENTENCE_LOOP, 1

PARA_DONE:
    ; Store null terminator
    LDR 0, 0, ZERO
    STR 0, 2, 0

    ; Restore registers
    LDX 1, TEMPX1
    LDX 2, TEMPX2
    LDX 3, TEMPX3

    RFS 0
```

### SEARCHWORD_SUB Detailed Algorithm

```assembly
SEARCHWORD_SUB:
    ; Save registers
    STX 1, TEMPX1
    STX 2, TEMPX2
    STX 3, TEMPX3

    ; Initialize
    LDR 0, 0, ZERO
    STR 0, 0, MATCHFLAG
    AIR 0, 1
    STR 0, 0, SENTENCECOUNT   ; Start with sentence 1

    LDX 1, JUMPTABLEADDR
    LDX 3, SENTENCETABLE

SENT_SEARCH_LOOP:
    ; Get sentence start address
    LDR 0, 3, 0               ; R0 = SENTTABLE[X3]
    STR 0, 0, TEMP            ; Store as sentence pointer

    ; Initialize word count
    LDR 0, 0, ONE
    STR 0, 0, WORDCOUNT

    ; Load sentence pointer into X2
    LDX 2, TEMP

WORD_SEARCH_LOOP:
    ; Skip delimiters to find word start
    JSR 1, SKIP_DELIM, 1      ; Advances X2 to word start

    ; Check if end of sentence (period found)
    LDR 1, 2, 0
    LDR 2, 0, PERIOD
    TRR 1, 2
    JCC 3, 1, NEXT_SENTENCE, 1

    ; Compare word at X2 with search word
    ; Set up for STRCMP
    STX 2, TEMPX2             ; Save X2
    LDX 1, SEARCHWORDADDR     ; X1 = search word
    ; X2 already points to current word

    JSR 1, 5, 1               ; Call STRCMP

    ; Check result in R0
    LDR 1, 0, ONE
    TRR 0, 1
    JCC 3, 1, WORD_FOUND, 1

    ; Restore X2
    LDX 2, TEMPX2

    ; Skip to next word
    JSR 1, SKIP_WORD, 1

    ; Increment word count
    LDR 0, 0, WORDCOUNT
    AIR 0, 1
    STR 0, 0, WORDCOUNT

    ; Continue word search
    JMA 1, WORD_SEARCH_LOOP, 1

WORD_FOUND:
    ; Set match flag
    LDR 0, 0, ONE
    STR 0, 0, MATCHFLAG

    ; Store sentence number
    LDR 0, 0, SENTENCECOUNT
    STR 0, 0, FOUNDSENTENCE

    ; Store word position
    LDR 0, 0, WORDCOUNT
    STR 0, 0, FOUNDWORD

    ; Restore and return
    LDX 1, TEMPX1
    LDX 2, TEMPX2
    LDX 3, TEMPX3
    RFS 0

NEXT_SENTENCE:
    ; Increment sentence count
    LDR 0, 0, SENTENCECOUNT
    AIR 0, 1
    STR 0, 0, SENTENCECOUNT

    ; Check if done (> 6)
    LDR 1, 0, SIX
    SMR 0, 0, ONE             ; R0 = count - 1
    TRR 0, 1
    ; If count > 6, done (not found)
    JGE 0, 1, SEARCH_DONE, 1

    ; Increment sentence table pointer
    STX 3, TEMP
    LDR 0, 0, TEMP
    AIR 0, 1
    STR 0, 0, TEMP
    LDX 3, TEMP

    ; Continue sentence search
    JMA 1, SENT_SEARCH_LOOP, 1

SEARCH_DONE:
    ; Not found - MATCHFLAG already 0
    LDX 1, TEMPX1
    LDX 2, TEMPX2
    LDX 3, TEMPX3
    RFS 0
```

---

## Implementation Steps

### Phase 1: Setup and Infrastructure (20%)
1. **Define memory layout**
   - Constants area (ASCII values, counters)
   - Variables area (flags, results)
   - Text storage (paragraph, search word)
   - Message storage (prompts)
   - Jump table

2. **Create message strings**
   - "ENTER 6 SENTENCES:"
   - "ENTER SEARCH WORD:"
   - "FOUND IN SENTENCE"
   - "WORD"
   - "NOT FOUND"

3. **Port PRINTSTR from Program 1**
   - Copy subroutine exactly
   - Add to jump table
   - Test with simple message

### Phase 2: Input Handling (25%)
1. **Implement READPARAGRAPH**
   - Start with single sentence reading
   - Add sentence delimiter detection
   - Implement sentence table population
   - Test with 2-3 sentences first
   - Scale to 6 sentences

2. **Implement READWORD**
   - Similar to character reading in Program 1
   - Simpler than READPARAGRAPH
   - Test independently

3. **Implement PRINTPARAGRAPH**
   - Simple loop through stored text
   - Print until null terminator
   - Test echoing what was read

### Phase 3: Helper Subroutines (20%)
1. **Implement ISDELIMITER**
   - Check character against space, period, comma, newline
   - Return boolean flag
   - Test with various characters

2. **Implement STRCMP**
   - Character-by-character comparison
   - Handle null terminators
   - Test with known matching/non-matching strings

3. **Implement SKIP_DELIM and SKIP_WORD**
   - Advance pointer past delimiters
   - Advance pointer past word characters
   - Test pointer advancement

### Phase 4: Search Algorithm (25%)
1. **Implement SEARCHWORD_SUB**
   - Outer loop: iterate sentences
   - Inner loop: iterate words in sentence
   - Call STRCMP for each word
   - Track positions
   - Test with known word locations

2. **Verify edge cases**
   - Word at start of sentence
   - Word at end of sentence
   - Word not found
   - Multiple occurrences (find first)

### Phase 5: Output and Integration (10%)
1. **Implement PRINTRESULT**
   - Print word (use PRINTSTR)
   - Print numbers (adapt PARSE from Program 1)
   - Format output nicely

2. **Integrate main program**
   - Connect all subroutines
   - Test complete flow
   - Add error handling

---

## Testing Strategy

### Unit Tests (Individual Subroutines)

1. **Test PRINTSTR**
   ```
   Input: "HELLO"
   Expected: "HELLO" on console
   ```

2. **Test READWORD**
   ```
   Input: "test" + newline
   Expected: SEARCHWORD = ['t','e','s','t',0]
   ```

3. **Test STRCMP**
   ```
   Test 1: "hello" vs "hello" → R0 = 1
   Test 2: "hello" vs "world" → R0 = 0
   Test 3: "hi" vs "high" → R0 = 0
   ```

4. **Test ISDELIMITER**
   ```
   Input: ' ' → R1 = 1
   Input: '.' → R1 = 1
   Input: 'a' → R1 = 0
   ```

### Integration Tests

1. **Test Read and Print**
   ```
   Input: "Hello. World."
   Expected: Print "Hello. World."
   ```

2. **Test Search - Word Found**
   ```
   Paragraph: "The cat sat. The dog ran."
   Search: "cat"
   Expected: Sentence 1, Word 2
   ```

3. **Test Search - Word Not Found**
   ```
   Paragraph: "The cat sat. The dog ran."
   Search: "bird"
   Expected: "NOT FOUND"
   ```

4. **Test Full Program**
   ```
   Input: 6 sentences with known word
   Search: Known word in sentence 4
   Expected: Correct sentence and word number
   ```

### Edge Cases

1. **Empty word search** - Handle gracefully
2. **Word appears multiple times** - Return first occurrence
3. **Case sensitivity** - Define behavior (exact match recommended)
4. **Punctuation attached to word** - "hello," vs "hello"
5. **Very short sentences** - "Hi." (one word)
6. **Word at boundaries** - First/last word in sentence

---

## Assembly Code Structure

### Template Outline

```assembly
; ========================================
; PROGRAM 2: PARAGRAPH WORD SEARCH
; ========================================

; ========================================
; LOW MEMORY: Constants and Variables (6-99)
; ========================================
        LOC 6

; Jump table address
JUMPTABLEADDR:  Data JUMPTABLE

; ASCII Constants
PERIOD:         Data 46
SPACE:          Data 32
NEWLINE:        Data 10
COMMA:          Data 44
ZERO:           Data 0
ONE:            Data 1
SIX:            Data 6

; Working Variables
SENTENCECOUNT:  Data 0
CHARCOUNT:      Data 0
WORDCOUNT:      Data 0
MATCHFLAG:      Data 0
FOUNDSENTENCE:  Data 0
FOUNDWORD:      Data 0
TEMP:           Data 0
TEMP2:          Data 0

; Memory Addresses
PARAGRAPHADDR:  Data PARAGRAPH
SEARCHWORDADDR: Data SEARCHWORD
SENTENCETABLE:  Data SENTTABLE
MSG1ADDR:       Data MSG1
MSG2ADDR:       Data MSG2
MSG3ADDR:       Data MSG3
MSG4ADDR:       Data MSG4

; Index Register Storage
TEMPX1:         Data 0
TEMPX2:         Data 0
TEMPX3:         Data 0

; ========================================
; TEXT STORAGE AREA (100-799)
; ========================================
        LOC 100
PARAGRAPH:      Data 0     ; ~600 words

        LOC 700
SEARCHWORD:     Data 0     ; ~50 words

        LOC 750
SENTTABLE:      Data 0     ; 6 sentence addresses

; ========================================
; MESSAGES (800-999)
; ========================================
        LOC 800
MSG1:   Data 69    ; "ENTER 6 SENTENCES:"
        Data 78
        ; ... (define full message)
        Data 0

        LOC 850
MSG2:   Data 69    ; "ENTER SEARCH WORD:"
        ; ... (define full message)
        Data 0

        LOC 880
MSG3:   Data 70    ; "FOUND IN SENTENCE"
        ; ... (define full message)
        Data 0

        LOC 920
MSG4:   Data 78    ; "NOT FOUND"
        ; ... (define full message)
        Data 0

; ========================================
; JUMP TABLE (1000-1099)
; ========================================
        LOC 1000
JUMPTABLE:
        Data READPARAGRAPH      ; 0
        Data PRINTPARAGRAPH     ; 1
        Data READWORD           ; 2
        Data SEARCHWORD_SUB     ; 3
        Data PRINTRESULT        ; 4
        Data STRCMP             ; 5
        Data ISDELIMITER        ; 6
        Data PRINTSTR           ; 7
        Data SKIP_DELIM         ; 8
        Data SKIP_WORD          ; 9
        ; ... add more as needed

; ========================================
; MAIN PROGRAM (1100+)
; ========================================
        LOC 1100

START:
        ; Print "ENTER 6 SENTENCES:"
        LDX 2, MSG1ADDR
        LDX 1, JUMPTABLEADDR
        JSR 1, 7, 1

        ; Read paragraph
        JSR 1, 0, 1

        ; Print paragraph
        JSR 1, 1, 1

        ; Print "ENTER SEARCH WORD:"
        LDX 2, MSG2ADDR
        JSR 1, 7, 1

        ; Read search word
        JSR 1, 2, 1

        ; Search for word
        JSR 1, 3, 1

        ; Print result
        JSR 1, 4, 1

        HLT

; ========================================
; SUBROUTINES (1200+)
; ========================================

; ----------------------------------------
; READPARAGRAPH
; Reads 6 sentences from keyboard
; ----------------------------------------
        LOC 1200
READPARAGRAPH:
        STX 1, TEMPX1
        STX 2, TEMPX2
        STX 3, TEMPX3

        ; ... implementation ...

        LDX 1, TEMPX1
        LDX 2, TEMPX2
        LDX 3, TEMPX3
        RFS 0

; ----------------------------------------
; PRINTPARAGRAPH
; Prints stored paragraph
; ----------------------------------------
PRINTPARAGRAPH:
        ; ... implementation ...
        RFS 0

; ----------------------------------------
; READWORD
; Reads search word from keyboard
; ----------------------------------------
READWORD:
        ; ... implementation ...
        RFS 0

; ----------------------------------------
; SEARCHWORD_SUB
; Searches for word in paragraph
; ----------------------------------------
SEARCHWORD_SUB:
        ; ... implementation ...
        RFS 0

; ----------------------------------------
; STRCMP
; Compares two strings
; Input: X1 = string1, X2 = string2
; Output: R0 = 1 if match, 0 otherwise
; ----------------------------------------
STRCMP:
        ; ... implementation ...
        RFS 0

; ----------------------------------------
; ISDELIMITER
; Checks if character is delimiter
; Input: R0 = character
; Output: R1 = 1 if delimiter, 0 otherwise
; ----------------------------------------
ISDELIMITER:
        ; ... implementation ...
        RFS 0

; ----------------------------------------
; PRINTSTR
; Prints null-terminated string
; Input: X2 = string address
; ----------------------------------------
PRINTSTR:
        ; Copy from Program 1
        RFS 0

; ----------------------------------------
; PRINTRESULT
; Prints search result
; ----------------------------------------
PRINTRESULT:
        ; ... implementation ...
        RFS 0

; Additional helper subroutines as needed...
```

---

## Key Instructions Reference

Based on `README.md` and simulator implementation:

### I/O Operations
- `IN r, devid` - Input character to register (devid=0 for keyboard)
- `OUT r, devid` - Output character from register (devid=1 for console)

### Load/Store
- `LDR r, x, address` - Load register from memory
- `STR r, x, address` - Store register to memory
- `LDX x, address` - Load index register
- `STX x, address` - Store index register

### Arithmetic
- `AIR r, immed` - Add immediate to register
- `SIR r, immed` - Subtract immediate from register
- `AMR r, x, address` - Add memory to register
- `SMR r, x, address` - Subtract memory from register

### Transfer/Control
- `JZ r, x, address` - Jump if zero
- `JNE r, x, address` - Jump if not equal
- `JCC cc, x, address` - Jump if condition code set
- `JMA x, address` - Unconditional jump
- `JSR x, address` - Jump and save return (R3 = return address)
- `RFS immed` - Return from subroutine
- `JGE r, x, address` - Jump if greater/equal

### Register Operations
- `TRR rx, ry` - Test equality (sets CC bit 3 if equal)
- `NOT rx` - Logical NOT

### Special
- `HLT` - Halt machine

---

## Differences from Program 1

| Aspect | Program 1 | Program 2 |
|--------|-----------|-----------|
| **Input Source** | Keyboard (numbers) | Keyboard (text) - ideally file |
| **Input Type** | Integers (-32768 to 32767) | ASCII characters/strings |
| **Processing** | Numeric comparison (closest) | String search and matching |
| **Data Structure** | Array of numbers | Array of characters (text) |
| **Search Algorithm** | Linear scan with min difference | String matching with position tracking |
| **Output** | Single closest number | Word, sentence #, word # |
| **Complexity** | Moderate (1 loop, arithmetic) | High (nested loops, string ops) |

---

## Conceptual Borrowing from Program 1

### 1. Memory Organization Pattern
- Low memory for constants/variables
- High memory for data storage
- Message area for prompts
- Jump table for subroutines

### 2. Subroutine Design Pattern
- Save index registers on entry
- Use X1 for jump table
- Use X2 and X3 for data pointers
- Restore registers before RFS

### 3. Input Reading Pattern
- Character-by-character using IN
- Check for terminators
- Store in memory array
- Increment pointer pattern

### 4. Loop Control Pattern
- Initialize counter
- Process item
- Decrement counter
- Check zero and branch

### 5. Output Pattern
- Use OUT for character printing
- PRINTSTR for messages
- Convert numbers to ASCII for display

---

## Additional Considerations

### Case Sensitivity
**Decision Required**: Should search be case-sensitive?

**Recommendation**: Implement exact match (case-sensitive) first, then optionally add case-insensitive variant.

**Implementation**: If case-insensitive needed, add character normalization in STRCMP (convert both to uppercase/lowercase before comparing).

### Multiple Occurrences
**Behavior**: If word appears multiple times, return first occurrence.

**Reason**: Simpler logic, meets requirements.

**Enhancement**: Could track all occurrences and print count.

### Word Boundaries
**Challenge**: How to handle punctuation?

**Example**: "hello," vs "hello"

**Solution Options**:
1. **Strict**: Only match if delimiters match exactly
2. **Relaxed**: Strip punctuation before comparison
3. **Smart**: Check if search word ends with punctuation

**Recommendation**: Use relaxed approach - consider "hello," as matching "hello"

### Input Format
**For 6 Sentences**:
- User types sentences one at a time
- Press Enter after each sentence ending with period
- After 6 sentences, program automatically continues

**Alternative**: Special terminator like "###" after all sentences.

---

## Performance Considerations

### Memory Usage
- Paragraph: ~600 bytes (100 chars per sentence × 6)
- Search word: ~50 bytes
- Sentence table: 6 words
- Variables: ~20 words
- Messages: ~200 bytes
- Code: ~400-600 words

**Total Estimate**: ~1300-1500 words (well within 2048 limit)

### Execution Time
- Input: User-dependent
- Search: O(n×m) where n = paragraph length, m = search word length
- Worst case: ~3600 character comparisons (600 chars × 6 avg word length)
- Acceptable for this simulator

---

## Summary

This implementation guide provides:

1. **Complete memory layout** with specific addresses
2. **Detailed algorithms** for all 7+ subroutines
3. **Reusable components** from Program 1 (PRINTSTR, patterns)
4. **Step-by-step implementation** in 5 phases
5. **Comprehensive testing strategy** with unit and integration tests
6. **Full assembly code template** ready to fill in
7. **Edge case handling** and design decisions

**Key Success Factors**:
- Build incrementally (test each subroutine independently)
- Reuse proven patterns from Program 1
- Handle edge cases explicitly
- Test with simple inputs before complex paragraphs
- Use the jump table consistently
- Document register usage in each subroutine

**Estimated Implementation Time**:
- Phase 1: 2 hours
- Phase 2: 4 hours
- Phase 3: 3 hours
- Phase 4: 4 hours
- Phase 5: 2 hours
- **Total**: ~15 hours of focused work

Good luck with the implementation!
