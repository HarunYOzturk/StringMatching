# My Journey – String Matching Algorithms Project

## Project Overview

This project was part of the **CENG303 – Design and Analysis of Algorithms** course. The main objective was to study, implement, and experimentally evaluate classical **string matching algorithms**, and to design a **Pre-Analysis mechanism** that selects the most suitable algorithm based on the characteristics of the input text and pattern.

The provided template already included implementations of **Naive**, **Knuth–Morris–Pratt (KMP)**, and **Rabin–Karp** algorithms. Our primary implementation task was to complete the **Boyer–Moore** algorithm and integrate it correctly into the system. In addition, we designed and tested a heuristic-based **Pre-Analysis** strategy.

---

## Development Process

### 1. Understanding the Project Structure

At the beginning, we carefully examined the project structure, test cases, and evaluation framework. Understanding how algorithms were registered, tested, and compared was essential before starting any implementation. This step helped us avoid integration errors later in the project.

### 2. Boyer–Moore Algorithm Implementation

We implemented the **Boyer–Moore string matching algorithm** using the **Bad Character Rule**.

Key design decisions:

* We **did not implement the Good Suffix Rule** intentionally, as it would significantly increase complexity and was not required for correctness.
* Our priority was correctness, robustness, and compatibility with all test cases rather than maximum theoretical speed.

#### Dynamic Bad Character Table

Instead of using a fixed-size table (e.g., 256 entries), we designed a **dynamic bad character table** based on the maximum character value in the pattern.

This decision:

* Prevented out-of-bounds errors,
* Allowed correct handling of **Unicode characters**,
* Avoided unnecessary memory allocation.

### 3. Handling Edge Cases

Special cases were explicitly considered:

* Empty pattern
* Pattern longer than text
* Unicode characters

Ensuring consistent behavior across all algorithms was crucial for passing the full test suite.

---

## Pre-Analysis Strategy Design

The **Pre-Analysis module** was designed to select the most appropriate string matching algorithm using heuristic rules.

### Heuristic Rules

Some of the rules we applied include:

* Very short patterns → **Naive**
* Repetitive prefixes → **KMP**
* Large alphabet size → **Boyer–Moore**
* Long text and moderate pattern length → **Rabin–Karp**

This heuristic-based approach does not guarantee optimal selection in all cases, but it aims to perform well on average.

### Observations

* In many test cases, **Naive** performed unexpectedly well.
* **Boyer–Moore** showed strength in long-text scenarios but suffered from preprocessing overhead on smaller inputs.
* **KMP** was reliable when patterns contained repetitive structures.

These results highlighted the difference between **theoretical complexity** and **practical performance**.

---

## Experimental Evaluation

All implemented algorithms passed **100% of the correctness tests**.

Performance measurements revealed:

* Naive often outperformed more advanced algorithms on small and medium inputs.
* Boyer–Moore excelled in long-text scenarios.
* Pre-Analysis achieved reasonable accuracy but sometimes selected suboptimal algorithms due to aggressive heuristics.

This reinforced the idea that algorithm selection is highly data-dependent.

---

## Challenges Faced

During the project, we encountered several challenges:

* **Character encoding issues** (UTF-8 vs Windows encodings) initially caused compilation and runtime errors.
* Test cases did not execute correctly at first due to an incorrect directory structure.
* Unicode support required rethinking traditional Boyer–Moore implementations.
* Early versions of Pre-Analysis were overly biased toward the Naive algorithm and required refinement.

Each issue helped us better understand real-world software development and debugging.

---

## Research and External Resources

During development, we consulted the following resources:

* Lecture notes and course materials
* CP-Algorithms
* GeeksforGeeks
* ChatGPT (used **only for conceptual clarification**, not for code generation)

ChatGPT was particularly helpful for:

* Revisiting the theoretical foundations of Boyer–Moore,
* Understanding trade-offs in heuristic-based algorithm selection.

All implementation and final design decisions were made by us.

---

## What We Learned

This project significantly improved our understanding of:

* Practical differences between theoretical and real-world algorithm performance,
* The importance of handling edge cases and input characteristics,
* Designing heuristic systems and evaluating their effectiveness,
* Writing robust, maintainable algorithm implementations.

Overall, this assignment was a valuable learning experience that combined **algorithm theory**, **implementation**, and **experimental evaluation**.

---

## Student Information

**Name:** Mustafa Kesim - Tevfik Han Parlak
**Student Number:** 22050111064 - 22050111025
