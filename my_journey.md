# My Journey: String Matching Algorithms

## Introduction
This homework assignment was a fascinating dive into the world of string matching algorithms. Initially, I thought implementing these algorithms would be straightforward, but I quickly realized that the devil is in the details—especially when it comes to performance optimization in Java.

## Challenges Faced

### 1. The "UnsupportedOperationException"
Starting with empty classes that threw exceptions was a bit daunting. I had to carefully read the requirements and understand the structure of the `Solution` base class before writing a single line of code.

### 2. Boyer-Moore Complexity
Understanding the **Bad Character Rule** was relatively easy, but implementing it efficiently was tricky. My first implementation used a massive integer array (`int[65536]`) to support Unicode. While this is theoretically O(1) access, in practice, initializing this array for every single test case (especially short ones) killed the performance. My algorithm was correct but 100x slower than Naive!

**The Fix:** I switched to using a `HashMap<Character, Integer>`. This reduced the initialization cost from constant-but-huge to proportional-to-pattern-length. The performance improvement was immediate and massive.

### 3. GoCrazy (Horspool) Strategy
For the "GoCrazy" algorithm, I researched various optimizations. I learned about the **Boyer-Moore-Horspool** algorithm, which is a simplified version of Boyer-Moore. It drops the complex "Good Suffix" rule and relies only on a modified Bad Character rule based on the last character of the window. I chose this because in many practical scenarios (like natural language text), the overhead of the Good Suffix rule doesn't pay off.

### 4. Pre-Analysis Logic
The hardest part was deciding *when* to use which algorithm.
- **Naive** is surprisingly fast in Java. The JIT compiler optimizes simple loops incredibly well. For short patterns, the overhead of creating HashMaps or arrays in BM/KMP makes them slower than Naive.
- **KMP** is unbeatable for patterns like "AAAAA" because it never backtracks in the text.
- **Boyer-Moore/Horspool** shine when the pattern is long, allowing them to skip huge chunks of text.

My final strategy focuses on using Naive for short/medium patterns (<= 20 chars) and switching to Horspool for longer ones, with a special check for repeating prefixes to trigger KMP.

### 5. The "Aha!" Moment
The biggest realization came when I saw the test results after switching from arrays to HashMaps. I went from being "correct but slow" to "correct and fast". It really drove home the point that in software engineering, *how* you implement something is just as important as *what* algorithm you use. Also, seeing Naive beat complex algorithms on short strings was a humbling lesson in practical vs. theoretical efficiency.

## What I Learned
- **Big-O isn't everything:** An O(n) algorithm can be slower than an O(n*m) algorithm if the constant factors (initialization, object creation) are high and n/m are small.
- **Java Performance:** Object creation (like HashMaps) has a cost. Primitive arrays are faster but can be memory-heavy if sparse.
- **Algorithm Selection:** There is no "one size fits all" algorithm. The best choice depends on the data.

## Conclusion
This assignment taught me to look beyond theoretical complexity and consider practical implementation details. It was satisfying to see the "Winner" column change as I optimized my code.


---

Name: Burhan Koçak

Student_ID: 21050111044