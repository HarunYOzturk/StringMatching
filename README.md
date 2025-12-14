# String Matching Algorithms - Homework Assignment

## Overview
This homework assignment focuses on implementing and analyzing string matching algorithms. You will implement two algorithms and create a pre-analysis system to intelligently choose the best algorithm for different scenarios.

Test cases are loaded from JSON files, making it easy to add, modify, and share test cases.

## Project Structure

```
StringMatching/
├── src/
│   ├── Solution.java              - Base class for all algorithms
│   ├── Analysis.java              - Contains all algorithm implementations
│   ├── PreAnalysis.java           - Pre-analysis interface and implementations
│   ├── TestCase.java              - Test case data structure
│   ├── TestCaseLoader.java        - Loads test cases from JSON files
│   ├── TestRunner.java            - Test execution and comparison
│   ├── PreAnalysisComparison.java - Pre-analysis performance comparison
│   ├── ManualTest.java            - Main program entry point
│   └── ManualTestRunner.java      - Detailed test execution with timing
├── testcases/
│   ├── shared/                    - Test cases for students (JSON files)
│   │   ├── 01_simple_match.json
│   │   ├── 02_no_match.json
│   │   └── ... (10 test files)
│   └── hidden/                    - Test cases for grading (JSON files)
│       ├── 11_case_sensitive.json
│       └── ... (5 test files)
├── run.sh                         - Quick run script
└── test.sh                        - Main test runner script
```

## Your Tasks

### Task 1: Implement Boyer-Moore Algorithm (Required)
**File:** `src/Analysis.java` - Class `BoyerMoore`

Implement the Boyer-Moore string matching algorithm. This algorithm is known for its efficiency, especially with large alphabets.

**Key concepts:**
- Bad character rule
- Good suffix rule
- Preprocessing phase

**Current status:** Throws `UnsupportedOperationException`

### Task 2: Implement Your Own Algorithm (Not Required)
**File:** `src/Analysis.java` - Class `GoCrazy`

Create your own string matching algorithm! Be creative and try to optimize for specific cases.

**Ideas:**
- Combine multiple algorithms
- Optimize for specific pattern characteristics
- Use heuristics to skip unnecessary comparisons

**Current status:** Throws `UnsupportedOperationException`

### Task 3: Implement Pre-Analysis Logic (Required)
**File:** `src/PreAnalysis.java` - Class `StudentPreAnalysis`

Implement the `chooseAlgorithm()` method to analyze the text and pattern, then choose the best algorithm.

**Goal:** Maximize the number of times you choose the fastest algorithm!

**Considerations:**
- Pattern length
- Text length
- Pattern characteristics (repeating characters, etc.)
- Alphabet size
- Algorithm time complexities

**Current status:** Returns `null` (no algorithm selection)

## Already Implemented Algorithms

The following algorithms are already implemented for reference:

1. **Naive Algorithm** - Simple brute force approach
   - Time Complexity: O(n*m)
   - Good for: Short patterns, small texts

2. **KMP (Knuth-Morris-Pratt)** - Uses failure function
   - Time Complexity: O(n+m)
   - Good for: Patterns with repeating prefixes

3. **Rabin-Karp** - Uses rolling hash
   - Time Complexity: O(n+m) average, O(n*m) worst case
   - Good for: Multiple pattern matching, long patterns

## How to Run

### Quick Start:
```bash
# Run all tests with full comparison tables
./run.sh

# Or use test.sh directly
./test.sh
```

### Run Specific Tests:
```bash
# Run specific test indices
./test.sh 0 1 2

# Run a range of tests
./test.sh 0-5

# Run only shared tests (for students)
./test.sh share

# Run only hidden tests (for grading)
./test.sh hidden

# List all available tests
./test.sh list

# Run with pre-analysis comparison
./test.sh preanalysis
```

### Manual Compilation and Run:
```bash
# Compile all files
javac src/*.java

# Run from src directory
cd src
java ManualTest [arguments]
```

## Output

The program generates multiple comparison tables:

### Table 1: Detailed Test Results - Execution Time Comparison
Shows execution time for each algorithm on each test:
- **Test Case** - Name of the test
- **Algorithm columns** - Status and execution time in microseconds (μs)
- **✓ PASS** - Algorithm solved the test correctly
- **✗ FAIL** - Algorithm produced incorrect output
- **✗ ERROR** - Algorithm threw an exception
- **N/A** - Algorithm not implemented

### Table 2: Summary Statistics
Shows aggregate statistics for each algorithm:
- **Passed/Failed** - Number of tests passed and failed
- **Not Implemented** - Number of tests where algorithm is not implemented
- **Avg/Min/Max** - Average, minimum, and maximum execution times

### Table 3: Fastest Algorithm Comparison
Shows which algorithm was fastest for each test case

### Table 4: Algorithm Comparison Table
Shows which algorithms successfully solved each test case (Pass/Fail/N/A)

### Table 5: Pre-Analysis Performance Comparison (when using `preanalysis` mode)
Shows the performance of your pre-analysis system:
- **Analysis(μs)** - Time spent in pre-analysis
- **Exec(μs)** - Time spent executing chosen algorithm
- **Total(μs)** - Total time (Analysis + Execution)
- **Fastest Alg** - The actually fastest algorithm
- **Time Diff(μs)** - Time saved (positive) or lost (negative)
- **✓** - Pre-analysis chose the fastest algorithm
- **✗** - Pre-analysis did NOT choose the fastest

## Test Cases

Test cases are stored as JSON files in the `testcases/` directory.

### Shared Tests (testcases/shared/)
These tests are shared with students for development and debugging:
1. Simple Match
2. No Match
3. Single Character
4. Pattern at End
5. Pattern at Beginning
6. Overlapping Patterns
7. Long Text Multiple Matches
8. Pattern Longer Than Text
9. Entire Text Match
10. Repeating Pattern

### Hidden Tests (testcases/hidden/)
These tests are kept hidden for instructor grading:
11. Case Sensitive
12. Numbers and Special Characters

### JSON Test Case Format
Each test case is a JSON file with the following structure:
```json
{
  "name": "Test Name",
  "text": "The text to search in",
  "pattern": "pattern",
  "expected": "0,5,10"
}
```

### Adding New Test Cases
To add a new test case:
1. Create a new JSON file in `testcases/shared/` or `testcases/hidden/`
2. Follow the naming convention: `##_test_name.json`
3. Fill in the name, text, pattern, and expected result
4. The system will automatically load it on next run

## Grading Criteria

1. **Boyer-Moore Implementation (30%)**
   - Correctness: Passes all test cases
   - Efficiency: Proper implementation of bad character rule
   - Code quality: Clean, well-commented code

2. **Your Research (30%)**
   - Documentation: Document how you used the internet or LLMs to create your strategy, and share all resources you used
   - Transparency: Be honest about your research process and sources

3. **Pre-Analysis Implementation (30%)**
   - Accuracy: Percentage of times fastest algorithm is chosen
   - Efficiency: Pre-analysis overhead is minimal
   - Logic: Well-reasoned algorithm selection strategy
   - Documentation: Clear explanation of strategy

4. **Your Journey (10%) - MANDATORY**
   - **⚠️ IMPORTANT: This section is MANDATORY. Without it, your homework will NOT be graded.**
   - Be honest and share your experience - there are no wrong answers here
   - Tell us what you learned, what challenges you faced, or anything you want to share
   - Examples of what you can write:
     - "I learned X and Y, and found Z challenging"
     - "I only had 30 minutes because my dog died"
     - "I couldn't finish because of [reason], but I learned [something]"
     - "This homework was [your honest opinion]"
   - Feedback: Any constructive comments about the homework assignment are welcome

## Tips

### For Boyer-Moore:
- Start with the bad character rule
- Test with simple cases first
- Handle edge cases (pattern longer than text, empty pattern)

### For GoCrazy:
- Study the existing algorithms
- Think about their strengths and weaknesses
- Consider hybrid approaches

### For Pre-Analysis:
- Look at the `ExamplePreAnalysis` class for inspiration
- Analyze the test results to understand algorithm performance
- Consider multiple factors, not just pattern length
- Balance accuracy with analysis overhead

## Example Pre-Analysis Strategy

```java
@Override
public String chooseAlgorithm(String text, String pattern) {
    int textLen = text.length();
    int patternLen = pattern.length();
    
    if (patternLen <= 3) {
        return "Naive"; // Fast for very short patterns
    } else if (hasRepeatingPrefix(pattern)) {
        return "KMP"; // Good for patterns with repeating prefixes
    } else if (patternLen > 10 && textLen > 1000) {
        return "RabinKarp"; // Good for long patterns in long texts
    } else {
        return "BoyerMoore"; // Generally efficient
    }
}
```

## Submission

Submit the following files:
1. `Analysis.java` - With your Boyer-Moore and GoCrazy implementations
2. `PreAnalysis.java` - With your StudentPreAnalysis implementation
3. A brief report (PDF) explaining:
   - Your Boyer-Moore implementation approach
   - Your GoCrazy algorithm design and rationale
   - Your pre-analysis strategy and why you chose it
   - Analysis of your results

## Resources

- [Boyer-Moore Algorithm](https://en.wikipedia.org/wiki/Boyer%E2%80%93Moore_string-search_algorithm)
- [String Matching Algorithms Overview](https://www.geeksforgeeks.org/algorithms-gq/pattern-searching/)
- Course lecture notes on string matching

## Questions?

Contact your instructor or TA for help!
Harun Yahya Öztürk
harunyahozturk@gmail.com (Mail's should be sent via subject of 'String Matching')
05413763007

Good luck! 🚀

