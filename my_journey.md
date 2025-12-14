# Project Journey: String Matching Algorithms Implementation

## 1. Introduction
In this project, we aimed to implement and analyze various string-matching algorithms to understand their efficiency in different scenarios. Our goal was not only to implement standard algorithms like KMP, Rabin-Karp, and Boyer-Moore but also to develop a smart pre-analysis strategy to select the best algorithm dynamically.

## 2. Our Journey & Implementation Steps

### Step 1: The Advanced Heuristic (Boyer-Moore)
Implementing Boyer-Moore was the most complex part of the assignment. We implemented both heuristics:
1.  **Bad Character Heuristic:** To skip mismatched characters rapidly.
2.  **Good Suffix Heuristic:** To align matching suffixes.
We combined these by taking the maximum shift: `Math.max(badCharShift, goodSuffixShift)`. This makes the algorithm extremely efficient for long alphabets and long patterns.

### Step 2: Designing the "GoCrazy" Algorithm
For the custom algorithm, we adopted a **Hybrid Approach**. Instead of reinventing the wheel, we focused on "Adaptability":
* We observed that for random strings without repetition, a slightly optimized Naive approach is faster than complex algorithms because of CPU caching and zero preprocessing.
* However, if the pattern is repetitive (e.g., "AAAA"), Naive fails.
* **Our Solution:** The `GoCrazy` algorithm first scans the pattern. If it detects high repetitiveness (>50%), it switches to **KMP**. Otherwise, it uses an **Optimized Naive** (checking the first character before entering the loop).

## 3. Pre-Analysis Strategy (The Decision Tree)
In `StudentPreAnalysis`, we designed a decision logic based on input characteristics:
* **Pattern Length ≤ 4:** Use **Naive**. (Preprocessing overhead of KMP/BM is too "expensive" for such short searches).
* **Pattern is Periodic:** Use **KMP**. (Guaranteed O(n) linear time).
* **Pattern Length > 25:** Use **Boyer-Moore**. (Long patterns benefit most from the skip heuristics).
* **Text Length > 10,000:** Use **Rabin-Karp**. (Efficient scanning for large documents).
* **Default:** Use **GoCrazy** (Our smart hybrid fallback).
* **Part we struggle with:**
* We got a low accuracy rate in the pre-analysis phase. There were times when it dropped even further while we were trying to fix it, so we shared the version that gave the highest accuracy rate. We struggled with how to choose the right algorithm.

## 4. Research & Resources
During the development, we utilized the following resources to understand the mathematical proofs and implementation details:

* Course Lecture Notes and Videos (String Matching)
* GeeksforGeeks (Boyer-Moore Good Suffix Rule explanation)
* Java Documentation (StringBuilder and Map optimizations)
* Google Gemini(just for Assistance and to understand boyer-moore)

---

### Student Information
**Muhammet Enes Varol - 22050111041**
**Mehmet Emin Kaya - 22050111034**
