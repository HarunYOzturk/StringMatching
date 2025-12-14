# My Journey

> This file contains the full content of the project report **"String Matching Algorithms Project Report"**.
> The report was originally prepared in PDF format; its content is fully transferred here in accordance with the updated submission instructions.

---

## String Matching Algorithms Project Report

**Öykü Karaduman**
Student ID: 22050111033

**Ruslan Ibragimov**
Student ID: 21050141029

**Year:** 2025

---

## Abstract

This report presents the implementation and analysis of several string matching algorithms within the given project framework. The required Boyer–Moore algorithm was implemented using the bad character rule, and a custom hybrid algorithm named **GoCrazy** was designed using heuristic optimizations. Additionally, a pre-analysis module (**StudentPreAnalysis**) was developed to select the most suitable algorithm based on text and pattern characteristics. The algorithms were evaluated using the provided JSON test cases and the manual test runner.

---

## 1. Introduction

The goal of this homework assignment is to implement and analyze different string matching algorithms and to design an intelligent pre-analysis system which chooses the fastest algorithm for a given text–pattern pair.

The project includes:

* Implementing the Boyer–Moore algorithm (required)
* Designing a custom algorithm (GoCrazy)
* Implementing the StudentPreAnalysis strategy
* Running all tests using the provided infrastructure and interpreting the results

---

## 2. Implemented Algorithms

### 2.1 Naive Algorithm

The naive (brute-force) algorithm checks the pattern at every possible starting position in the text. It runs in (O(n \cdot m)) time and performs well for short patterns due to zero preprocessing overhead.

### 2.2 KMP (Knuth–Morris–Pratt)

KMP avoids redundant comparisons by using an LPS (Longest Proper Prefix which is also Suffix) array. It runs in (O(n + m)) time and is efficient for repetitive patterns.

### 2.3 Rabin–Karp

Rabin–Karp uses a rolling hash technique, achieving (O(n + m)) average time. It is effective for large texts and multiple pattern matching scenarios.

### 2.4 Boyer–Moore (Bad Character Rule)

Boyer–Moore compares the pattern from right to left and shifts based on mismatch information. In this assignment, the bad character rule was implemented.

A table stores the last occurrence of each character. On mismatch, the pattern is shifted by:

```
max(1, j − lastOccurrence(text[s + j]))
```

**Implementation Note:** To support all Unicode characters, the bad character table was implemented with a size of 65,536. While correct, this large initialization overhead impacts performance on short texts.

### 2.5 Custom Algorithm: GoCrazy

GoCrazy is a heuristic-based optimized brute-force algorithm designed to minimize character comparisons without the overhead of complex preprocessing tables. It relies on the observation that mismatches often occur at the ends of the pattern.

**Strategy:**

1. **End-First Check:** Compare the last character of the pattern against the corresponding character in the text window.
2. **Start Check:** If the last character matches, check the first character.
3. **Middle Check:** Verify the middle section only if both the start and end characters match.

This approach avoids the initialization costs of algorithms like KMP or Boyer–Moore while significantly reducing comparisons for random text inputs.

---

## 3. Experimental Setup

Testing was performed using:

* `ManualTest.java` and `ManualTestRunner.java`
* JSON test cases under `testcases/shared/`
* Hidden test cases for grading

The output tables include per-algorithm timing, correctness, and pre-analysis performance. Each timing is the average of 5 runs.

---

## 4. Results and Discussion

### 4.1 Correctness

According to the summary statistics, all algorithms (Naive, Rabin–Karp, Boyer–Moore, GoCrazy, KMP) passed all 30 shared test cases with **0 failures**.

### 4.2 Average Running Times

| Algorithm   | Avg (µs) | Min (µs) | Max (µs) |
| ----------- | -------- | -------- | -------- |
| Naive       | 7.040    | 0.160    | 42.120   |
| Rabin–Karp  | 9.310    | 0.220    | 56.600   |
| Boyer–Moore | 95.125   | 0.600    | 635.800  |
| GoCrazy     | 5.550    | 0.220    | 28.760   |
| KMP         | 8.186    | 0.560    | 55.760   |

**Observations:**

* **GoCrazy** was the fastest on average due to minimal setup overhead and effective pruning.
* **Boyer–Moore** performed slower than expected because of the Unicode table initialization cost.
* The **Naive** algorithm performed competitively on small inputs due to the absence of preprocessing.

---

## 5. Pre-Analysis Strategy

The **StudentPreAnalysis** module analyzes pattern length and characteristics to minimize total execution time.

**Decision Logic:**

* Pattern length ≤ 3 → **Naive**
* Pattern has repeating prefix → **KMP**
* All other cases → **Boyer–Moore**

---

## 6. Our Journey

To understand the project structure, we spent approximately **3 hours** analyzing the provided Java files. We utilized **Gemini** to explain specific code segments that were unclear, as we found its code analysis capabilities superior to ChatGPT.

The algorithm design phase took the most time. We relied on the course lecture slides and online resources such as **GeeksforGeeks** to draft the initial logic.

* **Coding Time:** Approximately 7 hours
* **Challenges:** Correct Unicode implementation of the Boyer–Moore bad character rule
* **GoCrazy Design:** Inspired by the observation that mismatches often occur at pattern boundaries

The development process spanned **2–3 days**. We collaborated in person during several sessions at **Coffy**, a local coffee shop near campus. AI tools were used to assist with report formatting and clarification.

---

## 7. Resources

* GeeksforGeeks – Algorithm logic
* W3Schools – Java syntax reference
* Gemini – Code analysis and explanation
* ChatGPT – Report formatting assistance
* Course Lecture Slides and Notes (Fatih Nar)

---

## 8. Conclusion

This project provided practical experience with implementing, analyzing, and comparing multiple string matching algorithms. The experiments demonstrated that a lightweight hybrid algorithm like **GoCrazy** can outperform classical approaches on specific workloads by minimizing overhead. Conversely, algorithms with heavy initialization costs may underperform on small inputs despite stronger theoretical guarantees.

---

### Authors

Öykü Karaduman – 22050111033
Ruslan Ibragimov – 21050141029
