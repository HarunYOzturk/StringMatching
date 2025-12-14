# My Journey – String Matching Homework

## Overview

In this homework, I implemented and analyzed different string matching algorithms.  
The main goals were:

- Implementing the Boyer–Moore algorithm
- Designing a custom algorithm (GoCrazy)
- Implementing a pre-analysis strategy to choose the most suitable algorithm before execution

## Boyer–Moore Implementation

I implemented the Boyer–Moore algorithm using both:

- Bad Character Rule
- Good Suffix Rule

The preprocessing phase builds the necessary shift tables, and the matching phase uses these tables to skip unnecessary comparisons.  
This algorithm generally performs well on longer patterns and texts with larger alphabets.

## GoCrazy Algorithm

For the custom algorithm, I wanted to keep things simple but still meaningful.  
Instead of inventing a completely new paradigm, I implemented Sunday (Quick-Search) string matching.

Why Sunday:

- Very simple to implement
- Uses a shift based on the character after the current window
- Performs well in practice with low preprocessing cost

GoCrazy is implemented as a standalone algorithm and does not rely on other existing solutions.

## Pre-Analysis Strategy

I implemented StudentPreAnalysis to select an algorithm based on basic heuristics:

- Very small inputs → Naive
- Repetitive or border-heavy patterns → KMP
- Large texts → Rabin–Karp
- Other cases → Boyer–Moore

The goal was not to perfectly predict the fastest algorithm every time, but to make reasonable and explainable choices with minimal overhead.

## Results & Observations

- All algorithms produced correct results on shared test cases.
- Pre-analysis achieved around 60% correct fastest-algorithm selection on the provided tests.
- Due to small test sizes and microsecond-level timings, pre-analysis overhead was sometimes higher than its benefit, which is expected.

## Research & Resources

I used the following resources during this homework:

- Course lecture notes on string matching algorithms
- Online explanations of Boyer–Moore and Sunday (Quick-Search)
- Discussions and explanations with an LLM (ChatGPT, Gemini) to clarify algorithm behavior and design decisions

## What I Learned

- Practical performance can differ significantly from theoretical complexity
- Hybrid and heuristic-based strategies are common in real-world algorithm design
- Simplicity and correctness are often more valuable than over-optimization

## Final Thoughts

This homework helped me better understand how classical string matching algorithms work in practice and how to reason about algorithm selection based on input characteristics.

---

Salih Yıldız
22050111003
