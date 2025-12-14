# Project Journey: String Matching Algorithms Analysis

## 1. Introduction
In this bonus project, our primary objective was to implement and compare string matching algorithms. specifically focusing on Boyer-Moore and our custom hybrid design named "GoCrazy". Furthermore, we aimed to develop an intelligent Pre-analysis strategy to dynamically select the most efficient algorithm based on the text and pattern characteristics.

## 2. Algorithm Design & Implementation

### Boyer-Moore Implementation
We implemented an optimized version of the Boyer-Moore algorithm. Standard implementations often rely on `int[256]` arrays or HashMaps.
* **The Problem:** HashMaps introduced heavy startup overhead (~40µs), and standard arrays risked crashing with Unicode/multi-byte characters.
* **Our Solution:** We adopted a **Collision-Tolerant Array Strategy** using a fixed-size array paired with bitwise masking `(char & 0xFF)`. This minimized startup time while maintaining safety for all character types.

### The "GoCrazy" Algorithm (Adaptive Horspool++)
"GoCrazy" is our custom hybrid algorithm designed to balance speed and complexity.
* **Design:** It is built around Horspool’s skip table but optimized with Sunday’s algorithm concepts. It scans right-to-left but drops the complex Good Suffix rule of Boyer-Moore, focusing instead on the Bad Character rule.
* **Optimization:** We cache the pattern's last character before the loop and check the last character in the window first. If it doesn't match, we skip ahead immediately.
* **Performance:** This algorithm proved to be the winner for medium-to-large inputs, significantly outperforming standard Boyer-Moore in "Very Long Text" scenarios.

### Pre-Analysis Strategy
Our strategy is built on the realization that "simple beats clever" for small inputs.
* **Naive:** We default to Naive for short texts (<500 chars) or tiny patterns because the overhead of building skip tables outweighs the benefits.
* **KMP:** Triggered specifically for repetitive patterns (e.g., "ABCABC") where its prefix table offers a mathematical advantage.
* **GoCrazy:** Selected for large texts and complex alphabets where the preprocessing cost is justified by the speed of skipping.

## 3. Our Journey

### Motivation
Our motivation started after a challenging Lab Exam where many students, including us, struggled. To ensure we wouldn't face the same difficulty in the upcoming midterm, we decided to implement these algorithms from scratch to fully understand the logic behind them.

### Challenges and Iterations
* **The HashMap Mistake:** Initially, we implemented the GoCrazy algorithm using `HashMap`, `ArrayList`, and `HashSet`. While the logic was correct, the performance was poor due to high memory and time costs.
* **The Optimization:** We refactored the entire code to use primitive `int` arrays and modulo operations. This simple change provided a massive performance boost, especially on standard processors.

### The Turning Point
During the Pre-analysis phase, we were initially confused because the Naive algorithm kept winning in the provided test cases. We thought our advanced algorithms were broken. However, after consulting with **Mr. Öztürk**, we realized we needed to think beyond the provided test cases and consider edge cases like very long texts or specific alphabets. This feedback was crucial in refining our final selector logic.

## 4. LLM & Research Resources
We utilized AI tools to assist in code refactoring and complexity analysis, ensuring our "journey" text and code comments were clear.
* **Gemini 3 Pro:** Used for optimizing the `GoCrazy` algorithm and transitioning from HashMaps to arrays.
* **Claude Sonnet 4.5:** Assisted in designing the Pre-analysis decision tree and comparing theoretical performance vs. actual results.

## 5. References
1. Öztürk, M. and Nar, F. (2025). CENG303 Algorithm Analysis Lecture Contents.
2. GeeksforGeeks. (2024). Boyer Moore Algorithm for Pattern Searching.
3. Encora Insights. (n.d.). The Boyer-Moore-Horspool Algorithm.

---
**Students:**
M. Sefa Soysal - 23050111037
Sıla Dolaş - 22050111071
