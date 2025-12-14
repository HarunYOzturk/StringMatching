### Boyer-Moore

My initial Boyer–Moore implementation was correct but suffered from significant preprocessing overhead due to re-initializing a large bad-character table for every call.

I optimized this by using a "stamping trick" with a fixed-size `int[]` array. This approach avoids re-initialization costs while correctly handling all Unicode characters. This resulted in O(m) preprocessing with very small constant factors, making Boyer–Moore perform as expected on the benchmarks.

### GoCrazy

For GoCrazy, I aimed for a single, focused algorithm rather than a dispatcher. I combined Boyer–Moore’s bad-character heuristic with Naive’s simplicity to create a "sparse bad-character" Boyer–Moore.

My implementation:

- Performs a standard right-to-left comparison.
- Tracks the last position of up to 4 distinct characters from the pattern using primitive variables (e.g., `c1`..`c4`, `p1`..`p4`). This is done by scanning the pattern backwards to prioritize characters near the end.
- On a mismatch:
  - If the mismatching character is one of the tracked ones, it applies a Boyer–Moore-style shift.
  - Otherwise, it safely shifts by 1 (like Naive).

This design offers:

- Zero additional allocations and O(m) preprocessing with tiny constant factors.
- BM-like skip behavior when effective, with a safe fallback to Naive behavior.

It’s not designed to dominate every case, but in the benchmark it:

- Wins some non-trivial tests (e.g., “DNA Sequence”, “Palindrome Pattern”, “Complex Overlap”, “Single Character Pattern”).
- Has an average runtime comparable to the classic algorithms.

Overall, GoCrazy is a simple, focused hybrid: one algorithm that mixes Boyer–Moore’s idea of skipping with Naive’s simplicity.

### Pre-Analysis Logic

My pre-analysis strategy was informed by the known strengths and weaknesses of each algorithm:

- Rabin–Karp: Best for multiple pattern matching and reusing hashes.
- Boyer–Moore: Excels with large alphabets, longer patterns/texts, and "Ctrl+F" type searches due to its skipping ability.
- KMP: Ideal for streaming data or patterns with significant self-overlap, as it never re-scans text.

Initially, I considered a weighted analysis based on factors like pattern length, text length, pattern characteristics, and alphabet size. However, I concluded that a weighted system, while theoretically sound, would introduce unnecessary complexity for this assignment. It would be harder to tune, reason about, and debug compared to a simpler approach, and the overhead might outweigh the benefits.

Therefore, my implementation strategy focused on a lightweight decision tree:

- Naive: Chosen for very short patterns or trivial cases where preprocessing overhead is not justified.
- KMP: Selected for patterns exhibiting clear self-overlap or high repetitiveness, detected via cheap pattern-only checks (e.g., inspecting the first few characters for runs or low variety).
- Boyer–Moore: Used only when both the text and pattern are large enough that its skipping behavior pays off.
- Naive: Kept as the general default for most non-trivial cases, because in the benchmarks it often outperformed more complex algorithms due to its zero preprocessing cost.

I prioritized cheap signals for pre-analysis, such as pattern length and simple checks for pattern repetitiveness. I deliberately avoided complex analysis on the full text, as it often costs more time than it saves. This approach ensures low overhead and clear decision rules, matching algorithm strengths to specific input characteristics without over-engineering.

### Research

#### Resources

- https://www.geeksforgeeks.org/dsa/boyer-moore-algorithm-for-pattern-searching/
- https://www.geeksforgeeks.org/dsa/applications-of-string-matching-algorithms/
- https://www.geeksforgeeks.org/algorithms-gq/pattern-searching/


#### How I use LLMs (Although I am not Andrej)

My strategy is to use LLMs mainly for getting help with the implementation of my thoughts as specifically as possible. For example:

I try not to use LLMs like this:

> Hey ChatGPT, implement me a pre-analysis method.

This is bad because it gives the “best possible solution” to the problem, while you don’t have the structure to truly understand why it’s the best solution. Also, it may overcomplicate things unnecessarily, as the training data are from enterprise-level, large codebases.

Instead:

- I try to be more specific in my prompts.
- I want to get my answers as specific as possible.
- I try to use a bottom-to-top approach, similar to a divide-and-conquer strategy.
- I try to give as much context as I can to correctly lead the model. Otherwise, it redirects me to random, complicated things.

Example:

I first build my plan, then try to get corrections from LLMs based on my understanding. This is very important, as it helps you learn from your mistakes.

> Hey, my strategy in xxx situation is yyy. What do you think?

(Do refinements on the plan. Repeat step 1 until the plan is indeed orthodox.)

> I have this plan. How can I implement this in Java?

(Compare with my own implementations.)

> What about these parts — couldn’t I implement them as follows: ...

(This method helps me learn from my mistakes.)

### Analysis of the Results

#### How the algorithms behaved

A few patterns stood out:

- **Naive**  
  Naive was surprisingly strong. It ended up with the lowest average time (~26.6 μs) and won a lot of the “normal” tests like:
  - Simple Match
  - No Match
  - Pattern at Beginning/End
  - Entire Text Match
  - Many small or medium-sized patterns

  This makes sense: Naive has basically no preprocessing cost, so on moderate-sized inputs it just does the work and finishes before the others have amortized their setup.

- **Boyer–Moore**  
  Boyer–Moore performed well in the scenarios where you’d expect it to:
  - “Long Text Multiple Matches”
  - “Long Pattern”
  - “Pattern at Boundaries”
  - “Near Matches”

  With the optimized bad-character table (using the stamping trick), the preprocessing cost was reasonable, and the skipping behavior shows up nicely when the pattern and text are large enough.

- **KMP**  
  KMP shined on the very “KMP-ish” cases:
  - “Overlapping Patterns”
  - “All Same Character”
  - “Alternating Pattern”
  - Some edge cases like “Empty Pattern”

  Here its “never go back in the text” property and use of the prefix function pay off. On more random-looking tests, it is correct but not consistently the fastest.

- **Rabin–Karp**  
  Rabin–Karp was always correct but rarely the winner. Its average time (~38.3 μs) was the highest of the five. That also matches the theory: its real advantage is with multiple patterns and hash reuse, which this assignment doesn’t really exercise (we only match a single pattern at a time).

- **GoCrazy**  
  GoCrazy, my “sparse bad-character” variant, did what I hoped: it behaved competitively overall and actually won a few interesting tests:
  - “Empty Text”
  - “Both Empty”
  - “Single Character Pattern”
  - “DNA Sequence”
  - “Worst Case for Naive”
  - “Best Case for Boyer–Moore”
  - “KMP Advantage Case”

  That shows that the idea of combining a tiny, variable-based bad-character table with a Boyer–Moore-style scan is not just theoretically cute, but actually works decently on a mix of cases.

In short, the benchmark more or less confirms the textbook intuition:

- Naive is underrated for small to medium inputs.
- Boyer–Moore and KMP shine in their “designed for this” scenarios.
- Rabin–Karp is a bit handicapped in a single-pattern world.
- GoCrazy finds a few spots where its hybrid idea pays off.

#### How the pre-analysis did

The pre-analysis comparison tool measures:
(time for pre-analysis + time for the chosen algorithm) vs. running each algorithm directly.

The results show that:

- My `StudentPreAnalysis` mostly switches between Naive and KMP.
- In many tests it’s just a bit slower than the best possible standalone algorithm, often by just a few microseconds.
- There are some cases where it makes a good choice (e.g. picking KMP on clearly repetitive or overlapping patterns), but generally the gains are modest.

This is actually consistent with the design choices I made:

- I deliberately avoided heavy analysis of the full text (like scanning everything to compute detailed statistics), because that can easily cost more than it saves.
- I used only cheap signals: pattern length, and simple checks for repetition and structure in the pattern.
- The goal wasn’t to always pick the mathematically fastest algorithm on every synthetic test, but to:
  - Avoid obviously bad choices (e.g. using something heavy for a trivial pattern).
  - Keep the logic simple enough that it’s easy to explain and reason about.
  - Ensure the pre-analysis itself doesn’t become the bottleneck.

So the takeaway from the pre-analysis side is:

- A lightweight decision tree can make reasonable choices with low overhead.
- Trying to be “too smart” would require more computation and complexity than this assignment really justifies.
- In practice, it’s often better to have a small, predictable heuristic than a perfect but expensive selector.

Overall, the experiments matched what I expected: the classic algorithms show their standard strengths, GoCrazy finds a few nice niches, and the pre-analysis stays simple and safe rather than over-optimized.

### Student Info

Ahmet Kaan Demirci
21050111031
