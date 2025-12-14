# Bonus Homework Report
## String Matching Algorithms

**Zeki Furkan Yıldız (21050111019)**  
**Sudenur Akdogan (23050111017)**

---

## Introduction

In this homework, we worked on string matching algorithms. The main goal was to implement the **Boyer–Moore** algorithm and create a custom algorithm called **GoCrazy**. We also wrote a **Pre-Analysis** class to choose the best algorithm automatically.

During this process, we realized that theoretical speed is not always the same as real-world speed, especially due to Java’s overhead costs and JVM optimizations.

---

## Boyer–Moore Implementation

We implemented the **Boyer–Moore** algorithm in the `Analysis.java` file, focusing specifically on the **Bad Character Heuristic**.

For the implementation, we used an integer array of size `256` for ASCII characters.

### Optimization: Threshold Check

We observed that building the `badChar` lookup table itself has a cost.  
If the pattern length is **less than 20 characters**, the **Naive** algorithm is often faster in practice. Therefore, we added a threshold check to fall back to Naive for short patterns.

### Resources Used

- https://www.tutorialspoint.com/data_structures_algorithms/boyer_moore_algorithm.htm
- https://www.geeksforgeeks.org/dsa/boyer-moore-algorithm-for-pattern-searching/
- https://github.com/likejazz/boyer-moore-string-search

---

## GoCrazy Algorithm

For the creative part, we wanted an algorithm faster than standard Boyer–Moore in real-world cases. We designed **GoCrazy**, a hybrid approach inspired by:

- **Sunday’s Algorithm**
- **FJS (Fast–Jesus–Sunday)**

### Core Idea

When a mismatch occurs, instead of examining the mismatched character inside the window, Sunday’s algorithm checks the character **immediately after the window**. This often allows larger jumps.

To avoid worst-case behavior on repetitive patterns, we also integrated **KMP-style prefix logic** for safety.

### Resources Used

- https://github.com/johannburkard/StringSearch
- https://github.com/CGJennings/fjs-string-matching

---

## Pre-Analysis Logic

This was the most critical and complex part of the assignment.

Initially, we noticed that the **Naive algorithm** was winning almost all standard test cases. This led us to an important realization:

> In modern Java, *simple often beats theoretically optimal* for small and medium inputs.

### Why Does Naive Win?

- **Overhead Cost**  
  Creating lookup tables takes microseconds, which dominates short searches.

- **JIT Optimization**  
  The JVM heavily optimizes simple loops like Naive string matching.

---

## Our Strategy

1. **Instant Edge Cases**  
   Empty pattern or pattern longer than text → **Naive**

2. **Ultra-Short Patterns (`M ≤ 3`)**  
   Analysis cost > search cost → **Naive**

3. **Repetitive Patterns**  
   Detected via `hasHighRepetition` → **KMP**

4. **Small Texts (`N < 128`)**  
   Heuristic overhead too high → **Naive**

5. **Large Texts (`N > 512`)**
    - Small alphabet (DNA) → **KMP**
    - Long patterns (`M > 30`) → **Boyer–Moore**

6. **Sweet Spot**  
   Everything else → **GoCrazy**

---

## Analysis of Results

- **Prediction Accuracy:** 20 / 30 test cases (**66.7%**)

- **Very Long Text**
    - Naive: `34.98 μs`
    - GoCrazy: `21.03 μs`

- **Repetitive Patterns**
    - Selected **KMP** for worst-case safety

- **Pattern Longer Than Text**
    - Execution time: `0.47 μs`

---

## Our Journey

This homework was interesting because it showed us that Big-O notation is not everything.  
Theoretically, Boyer-Moore is `O(N/M)`, which should be fast. But in reality, for small strings, the initialization cost makes it slower than the `O(N*M)` Naive algorithm.

We spent a lot of time debugging because we thought our Boyer-Moore code was wrong. Later we realized it was just the overhead cost. Researching different repositories (like FJS and Sunday) helped us write a much better GoCrazy algorithm.

Thanks to this homework, we painfully learned that optimization does not lie solely in theoretical formulas, but also in knowing where and when to defeat that microsecond startup cost. Lastly, we understood that the best algorithm is not always the most complex, but the one most suitable for the current workload.

---

### AI Utilization & Methodology

During the development of the strategy, comparative analysis, and debugging complex overhead issues, we utilized AI assistants (specifically **Gemini**) to quickly analyze console output and evaluate performance trade-offs.

To ensure transparency regarding our code generation and optimization process, we guided the AI using specific technical resources we found online. Below are the exact prompts we used to refine our algorithms:

- **For the GoCrazy Algorithm (Sunday / FJS Logic):**

  > *"I found these resources online:  
  > https://github.com/johannburkard/StringSearch,  
  > https://github.com/CGJennings/fjs-string-matching.  
  > Can you update the GoCrazy algorithm using the logic of these algorithms?  
  > We will cite them as references. Please provide renewed, efficient Java code that looks unique and original, not generic code."*

- **For Boyer-Moore Optimization:**

  > *"https://www.tutorialspoint.com/data_structures_algorithms/boyer_moore_algorithm.htm,  
  > https://www.geeksforgeeks.org/dsa/boyer-moore-algorithm-for-pattern-searching/,  
  > https://github.com/likejazz/boyer-moore-string-search.  
  > How can we make Boyer-Moore more efficient suitable for the instructor's requirements, influenced by these sites?  
  > Give me original code, only change the Boyer-Moore part."*

These prompts helped us combine theoretical knowledge from tutorials with practical implementation details, allowing us to write efficient code while maintaining academic integrity by referencing our sources.
