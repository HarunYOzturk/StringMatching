# Project Report: String Matching Algorithms Analysis

**Student Name:** Muhammed Enes Uluç  
**Student Number:** 22050111038

---

## 1. Boyer–Moore Implementation & Research

### My Implementation Approach
For the Boyer–Moore algorithm, I focused primarily on the **Bad Character Rule**. During my research, I examined the two main components using several sources (CLRS, GeeksforGeeks, and Medium articles).

### Why I Skipped the "Good Suffix" Rule
After benchmarking on the dataset provided in the homework, I made a conscious design decision to exclude the Good Suffix Rule:

* **Heavy Preprocessing:** The Good Suffix Rule requires significant preprocessing time.
* **Short Patterns:** Many patterns in our dataset are short, where complex preprocessing does not pay off.
* **Performance Cost:** The preprocessing cost often exceeded the actual search time savings.

> **Conclusion:** I implemented a more lightweight Boyer-Moore using only the Bad Character Rule, which proved far more efficient for our specific input types.

---

## 2. Pre-Analysis Strategy – Philosophy & Design

### Philosophy: "Avoid Big Mistakes"
My PreAnalysis selects the correct algorithm **40–60%** of the time. This is not a failure — it is intentional.

> If the analysis takes **5µs** to "think" but only saves **2µs**, the system becomes slower.

Therefore, my priorities were:
1.  **Simple** > Perfect
2.  **Fast** > Overly Detailed
3.  **Risk Mitigation** > Complete Accuracy

### Core Strategy
* **Be Fast:** No expensive loops or deep pattern analysis.
* **Catch Only Large Differences:** Ignore micro-optimizations.
* **Allow Small Mistakes:** Because small mistakes cost almost nothing in execution time.

### Heuristics Table
These rules target the "big wins" rather than tiny optimizations:

| Scenario | Selected Algorithm |
| :--- | :--- |
| **Very long text** | Boyer–Moore |
| **Long pattern** | Boyer–Moore |
| **Repeating prefix pattern** | KMP |
| **Numeric / Symbol-heavy** | Rabin–Karp |
| **Small text** | Naive |

---

## 3. What I Learned While Designing PreAnalysis

### Engineering Is Trade-Offs
I learned that the goal is **not perfection**. The goal is to **avoid catastrophic predictions**.

### Diminishing Returns
I discovered the concept of diminishing returns in algorithm selection. Spending huge engineering effort for a tiny speed gain is not worth it.

* **Focus:** I focused only on large mistakes (10–20 µs errors) and "Big Red Zones" in my performance table.
* **Ignored:** Tiny losses (like 0.1 µs) were intentionally ignored to keep the selector fast.

---

## 4. Analysis of the "Red Performance Table"

In the attached report photos, I analyzed the "Red Zones" (error areas) to compare my chosen algorithm vs. the optimal one.

### Analysis Criteria
I evaluated:
1.  Why certain cases still show red.
2.  Whether the overhead of adding another rule would fix them.
3.  Whether fixing them hurts total performance.

### Outcome
* Most mistakes resulted in a lag of only **0–1 ms**.
* The worst cases dropped to **1–3 ms**, which is acceptable.
* **Conclusion:** Fixing every "red cell" would slow down the entire selector. PreAnalysis was tuned to avoid heavy losses, not microsecond-level imperfections.

---

## 5. Unexpected Finding: Naive Algorithm Is Amazing

Initially, I assumed the Naive algorithm was "the bad one." Real-world testing corrected this misunderstanding.

### Why Naive Wins
For small text patterns, Naive is often the fastest because:
* **No Preprocessing:** It starts searching immediately.
* **CPU Cache-Friendly:** It has excellent locality.
* **Minimal Overhead:** Simple logic executes very quickly.

> **Example Optimal Case:**
> * **Text:** 30–40 chars
> * **Pattern:** 2–3 chars
> * **Result:** Naive beats KMP and Boyer-Moore.

---

## 6. How KMP Behaved (Insights)

Before this homework, I never truly understood the practical strength of Knuth-Morris-Pratt (KMP).

* **Strength:** KMP became powerful especially when the pattern has **repeating prefixes**.
* **Example Pattern:** `ABABABABABAC`
* **Reason:** Its prefix-function avoids repeated comparisons, allowing it to skip ahead efficiently.

---

## 7. Key Lessons From This Homework

### General Insights
* **Theory ≠ Practice:** Big-O notation doesn't always represent reality; constant factors matter.
* **Preprocessing Costs:** These can dominate real performance, especially for short tasks.
* **Naive is Underrated:** It is highly effective for small inputs.
* **BM & KMP Niches:** BM shines with long text/patterns; KMP is excellent for repeating prefixes.
* **Prediction Risks:** Bad predictions can cause more harm than simply using a "wrong" but decent algorithm.

### Engineering Lessons
* **Trade-offs beat perfection.**
* A tiny gain is not worth a huge overhead.
* Selectors need to be **fast**, not necessarily "smart."
* **Avoiding bad decisions** is more important than chasing optimal ones.

---

## 8. Usage of AI Assistance (GPT)

During the project, I used ChatGPT as a **pair programmer**. I verified all suggestions through my own tests.

**Primary Uses:**
* Verifying algorithm theory.
* Brainstorming PreAnalysis heuristics.
* Interpreting the "Red Tables" (performance gaps).
* Optimizing minor parts of the code.
* Understanding the overhead of the BM "Good Suffix" rule.

---

## 9. Sources

* **Introduction to Algorithms** — CLRS (Pattern Matching Chapter)
* **GeeksforGeeks:** Documentation on KMP, Boyer–Moore, and Rabin–Karp.
* **Medium Article:** https://medium.com/@AlexanderObregon/finding-patterns-with-boyer-moore-in-java-75c8a22d741a
* **ChatGPT:** Used for conceptual understanding, debugging, and strategy reasoning.