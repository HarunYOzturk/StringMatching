Bonus Homework Report: String Matching Algorithms

Authors:

    Zeki Furkan Yıldız (21050111019)

    Sudenur Akdoğan (2350111017)

1. Introduction

In this homework, we worked on string matching algorithms. The main goal was to implement the Boyer-Moore algorithm and create a custom algorithm called "GoCrazy". We also wrote a Pre-Analysis class to choose the best algorithm automatically. We realized that theoretical speed is not always the same as real-world speed because of Java's overhead costs.
2. Boyer-Moore Implementation

We implemented the Boyer-Moore algorithm in the Analysis.java file. We focused on the Bad Character Heuristic.

For the implementation, we used an integer array of size 256 for ASCII characters. One important optimization we added is the Threshold Check. We found that creating the badChar array takes time. If the pattern is very short (less than 20 characters), it is faster to just use the Naive algorithm.
Resources Used

To understand the Boyer-Moore logic and the Bad Character rule, we used these websites:

    https://www.tutorialspoint.com/data_structures_algorithms/boyer_moore_algorithm.htm

    https://www.geeksforgeeks.org/dsa/boyer-moore-algorithm-for-pattern-searching/

We also checked this GitHub repository to understand the code structure:

    https://github.com/likejazz/boyer-moore-string-search

3. GoCrazy Algorithm

For the creative part, we wanted to implement something faster than standard Boyer-Moore. We decided to use a hybrid approach based on Sunday's Algorithm and FJS (Fast-Jesus-Sunday).

The logic is simple: When a mismatch occurs, instead of looking at the character in the pattern, Sunday's algorithm looks at the character in the text immediately after the current window. This allows for larger jumps.

We also combined this with KMP logic to be safe against repetitive patterns.
Resources Used

We researched hybrid algorithms on GitHub and adapted the logic from these repositories:

    https://github.com/johannburkard/StringSearch (For Sunday's Algorithm logic)

    https://github.com/CGJennings/fjs-string-matching (For FJS/KMP hybrid logic)

4. Pre-Analysis Logic

This was the most critical and complex part of the assignment. When we looked at the initial console output, we saw that the Naive algorithm was winning almost all standard test cases. We realized that for modern Java, "simple is faster" until the data gets really big or complex.
Why Naive Wins?

We analyzed the results and identified two main reasons:

    Overhead: Creating lookup tables (like HashMap or int[256]) takes microseconds. For a test that only lasts 1-2 microseconds, this setup cost is huge.

    JIT Optimization: The Java Virtual Machine (JVM) optimizes simple loops (like Naive) extremely well, making them faster than complex logic for small inputs.

Our Strategy

Based on our findings, we wrote a highly detailed StudentPreAnalysis class with specific layers of logic:

    Instant Edge Cases: If the pattern is empty or longer than the text, return "Naive" immediately.

    Ultra Short Patterns (M≤3): The analysis overhead is greater than the search time itself. We force Naive.

    Repetitive Patterns: We implemented a helper method (hasHighRepetition) that checks the first 8 characters. If the pattern repeats (e.g., "AAAA"), we switch to KMP to avoid the worst-case O(N⋅M) behavior of Naive.

    Small Text Optimization (N<128): This was our critical threshold. If the text is shorter than 128 characters, the overhead of building heuristic tables outweighs the speedup. We force Naive here.

    Large Texts (N>512):

        DNA Detection: We implemented a fast bitmask check (isSmallAlphabet). If the text uses a small alphabet (like A, C, G, T), simple skipping algorithms often fail due to high collision rates. In this case, we prefer KMP for stability.

        Long Patterns (M>30): For very long patterns, Boyer-Moore's "Good Suffix" rule allows for massive jumps.

    The "Sweet Spot" (GoCrazy): For everything else (standard text, medium patterns), we default to GoCrazy (Sunday's algorithm) as its aggressive skipping strategy proved fastest in our benchmarks.

5. Analysis of Results

Looking at our final detailed test results, our strategy proved successful in identifying the "winners" for different scenarios:

    Overall Accuracy: Our logic correctly predicted the fastest algorithm in 20 out of 30 test cases (66.7%), which is a significant improvement over a single static choice.

    Very Long Text

        Naive Time: 34.98μs

        GoCrazy Time: 21.03μs (Note: Chosen algorithm overhead led to slightly different total times, but the raw speed advantage is clear). Our strategy successfully switched away from Naive for this massive input.

    Repetitive Patterns: In the "Simple Match" test case, our logic detected repetition and chose KMP (9.68μs). Although Naive was technically faster due to low overhead, KMP provided the necessary safety guarantee against worst-case scenarios.

    Pattern Longer Than Text: Our strategy correctly identified this edge case and instantly chose Naive, resulting in a lightning-fast execution of 0.47μs.

6. Our Journey

This homework was interesting because it showed us that Big-O notation is not everything. Theoretically, Boyer-Moore is O(N/M), which should be fast. But in reality, for small strings, the initialization cost makes it slower than the O(N⋅M) Naive algorithm.

We spent a lot of time debugging because we thought our Boyer-Moore code was wrong. Later we realized it was just the overhead cost. Researching different repositories (like FJS and Sunday) helped us write a much better GoCrazy algorithm.

Thanks to this homework, we painfully learned that optimization does not lie solely in theoretical formulas, but also in knowing where and when to defeat that microsecond startup cost. Lastly, we understood that the best algorithm is not always the most complex, but the one most suitable for the current workload.
AI Utilization & Methodology

During the development of the strategy, comparative analysis, and debugging complex overhead issues, we utilized AI assistants (specifically Gemini) to quickly analyze console output and evaluate performance trade-offs.

To ensure transparency regarding our code generation and optimization process, we guided the AI using specific technical resources we found online. Below are the exact prompts we used to refine our algorithms:

    For the GoCrazy Algorithm (Sunday/FJS Logic):

        _"I found these resources online: https://github.com/johannburkard/StringSearch, https://github.com/CGJennings/fjs-string-matching. Can you update the GoCrazy algorithm using the logic of these algorithms? We will cite them as references. Please provide renewed, efficient Java code that looks unique and original, not generic code."_

    For Boyer-Moore Optimization:

        _"https://www.tutorialspoint.com/data_structures_algorithms/boyer_moore_algorithm.htm, https://www.geeksforgeeks.org/dsa/boyer-moore-algorithm-for-pattern-searching/, https://github.com/likejazz/boyer-moore-string-search. How can we make Boyer-Moore more efficient suitable for the instructor's requirements, influenced by these sites? Give me original code, only change the Boyer-Moore part."_

These prompts helped us combine theoretical knowledge from tutorials with practical implementation details, allowing us to write efficient code while maintaining academic integrity by referencing our sources.
