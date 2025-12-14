

MY JOURNEY
When we started working on this assignment, we were aware that string matching algorithms were conceptually deep, but we quickly realized that the real difficulty lies in the implementation details. Understanding the theory in class is one thing, but constructing a clean, correct, and efficient implementation from scratch is a completely different challenge. We experienced this most clearly while implementing the Boyer–Moore algorithm.

The preprocessing phase of Boyer–Moore—specifically the construction of the bad character and good suffix tables—turned out to be far more delicate than it initially appeared. Both tables directly influence the efficiency of the algorithm, and even a small indexing mistake can cause the entire matching process to break. Because of this, we found ourselves carefully stepping through the logic multiple times, revisiting lecture notes and external references to ensure correctness.

For the GoCrazy algorithm, our intention was not just to produce “something different,” but to design an approach that performs meaningfully well in certain scenarios. We experimented with hybrid ideas and eventually implemented a heuristic-based structure that attempts to reduce unnecessary comparisons for specific pattern types. It may not be perfect, but it helped us strengthen our experimental thinking and design skills.

The pre-analysis phase ended up being the most insightful part of the project. Rather than simply coding algorithms, we had to determine which algorithm is actually optimal under which circumstances. Through testing and observation, we discovered that theoretical performance does not always align with practical outcomes. For instance, algorithms that are asymptotically efficient may not outperform simpler ones on small inputs or highly repetitive patterns. Similarly, we observed that the naive algorithm—often considered slow—was sometimes the fastest depending on the data characteristics. This taught us a great deal about how input structure influences performance in real-world scenarios.

Overall, this assignment taught us three major lessons:

Implementation Reveals Complexity: Understanding an algorithm is not enough; implementing it is what reveals the real complexity.

Context Matters: Performance depends heavily on input characteristics. There is no single “best” algorithm for all cases.

Debugging Challenge: Debugging can be as challenging as the algorithm itself, especially for multi-stage methods like Boyer–Moore.

The assignment took more time and attention than we initially expected, but in the end, we genuinely feel that we improved both technically and analytically. Our implementation skills became more precise, and we gained a deeper understanding of performance analysis and algorithmic behavior. Overall, it was a meaningful and truly educational experience.


Students: Selman AKSU (21050111015), Yusif Jabbarzade (21050141026)