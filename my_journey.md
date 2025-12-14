# Project Journey: String Matching Algorithms

## 1. Introduction
In this project, our objective was to implement and compare string matching algorithms, specifically Boyer-Moore and our custom design named "GoCrazy". [cite_start]We also aimed to develop a smart Pre-analysis strategy to select the most efficient algorithm dynamically based on the input characteristics[cite: 7].

## 2. Our Research & Algorithm Design

### Boyer-Moore Implementation
We focused on optimizing the standard Boyer-Moore algorithm. [cite_start]Standard versions often use fixed-size arrays which can crash with multi-byte characters, or HashMaps which introduce heavy startup overhead[cite: 10, 11].
* [cite_start]**Optimization:** We utilized a Collision-Tolerant Array Strategy using a fixed-size `int[256]` array paired with bitwise masking `(char & 0xFF)`[cite: 13, 14].
* [cite_start]**Result:** This approach removed allocation overhead, significantly cutting startup time while remaining safe for Unicode characters[cite: 15, 16].

### The "GoCrazy" Algorithm (Adaptive Horspool++)
[cite_start]Our custom algorithm, GoCrazy, is a hybrid built around Horspool's skip table with specific performance tweaks[cite: 18].
* [cite_start]**Mechanism:** It scans right-to-left like Boyer-Moore but focuses on the Bad Character rule to reduce complexity[cite: 22].
* [cite_start]**Optimization:** It caches the pattern's last character before the loop to avoid repeated lookups and uses a shift logic inspired by Sunday's algorithm[cite: 23, 26].
* [cite_start]**Use Case:** It proved to be the clear winner for medium-to-large inputs, outperforming Boyer-Moore in tests like "Very Long Text" (8.158 µs vs 12.679 µs)[cite: 60, 61].

### Pre-Analysis Strategy
[cite_start]Our strategy follows the philosophy that "simple beats clever"[cite: 33].
* [cite_start]**Naive:** Selected for tiny patterns or short texts (<500 chars) because the overhead of building tables in advanced algorithms is costlier than a simple scan[cite: 36, 54].
* [cite_start]**KMP:** Triggered for repetitive patterns (e.g., "ABCABC") where the prefix table provides a mathematical advantage[cite: 39, 40].
* [cite_start]**GoCrazy/Boyer-Moore:** Used for large texts and huge alphabets where the skip-table preparation pays off[cite: 46, 47].

## 3. The Journey

### Motivation
Our journey began after a difficult lab exam where many students struggled. [cite_start]To avoid repeating this in the midterm, we decided to deeply study string matching algorithms and implement them from scratch[cite: 338].

### Challenges & Iterations
* **The HashMap Trap:** Initially, we implemented our GoCrazy algorithm using `HashMap`, `ArrayList`, and `HashSet`. [cite_start]While theoretically correct, we realized this was extremely costly in terms of space and time due to object overhead[cite: 341].
* **The Pivot:** We refactored the code to use primitive `int[256]` arrays. [cite_start]This change resulted in a massive performance boost, especially on older processors[cite: 342, 69].

### The "Aha!" Moment
During the Pre-analysis phase, we were confused because the Naive algorithm kept winning in our initial test cases. [cite_start]We thought our analysis was flawed[cite: 345]. After consulting with Mr. Öztürk, we realized we needed to consider a broader range of edge cases (different lengths, patterns) rather than just the provided tests. [cite_start]This feedback allowed us to design a more robust selection strategy[cite: 346, 347].

## 4. LLM & AI Usage
We utilized Large Language Models to assist in optimizing our code and strategy:
* [cite_start]**Gemini 3 Pro:** Used for code refactoring, specifically helping us transition from HashMaps to efficient array structures and analyzing complexity[cite: 343, 352].
* [cite_start]**Claude Sonnet 4.5:** Assisted in the comparative analysis of search algorithms and helped design the logic for our Pre-analysis technique[cite: 348, 353].

## 5. References
1.  Öztürk, M. and Nar, F. (2025). CENG303 Algorithm Analysis Lecture Contents.
2.  Google. (2025). Gemini 3 Pro.
3.  Anthropic. (2025). Claude Sonnet 4.5.
4.  GeeksforGeeks. (2024). Boyer Moore Algorithm for Pattern Searching.
5.  Encora Insights. (n.d.). The Boyer-Moore-Horspool Algorithm.

---
**Students:**
M. Sefa Soysal - 23050111037
Sıla Dolaş - 22050111071