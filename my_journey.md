**Havva Nisa Altınbaş 21050111014**

**Emircan Kasap 21050111060**

**Our Research & Documentation**

**Process:** We started by studying the lecture slides, but implementing
the **Boyer-Moore** algorithm (specifically the *Good Suffix Rule*) was
challenging to visualize. We used online resources to understand how to
build the preprocessing tables correctly. For our **GoCrazy** algorithm,
we researched \"hybrid string matching\" and learned that many
real-world implementations switch algorithms based on performance. We
decided to combine Boyer-Moore's speed with KMP's safety.

**Tools:** We want to share our tools we used:

- **LLMs (AI):** We used ChatGPT/Gemini to explain the logic of the
  \"Good Suffix\" rule step-by-step, as the textbook definition was hard
  to grasp. We also used AI to help debug \"IndexOutOfBounds\" errors in
  our loops. We did not use AI to write the whole code for us, but
  rather as a tutor to fix our logic.

- **Internet:** We referenced GeeksForGeeks to check the standard
  implementation of the KMP failure function (LPS array) to make sure
  ours was efficient.

**Implementation & Analysis Details**

**1. Boyer-Moore Implementation Approach**

- **Dual Heuristics:** We implemented both the Bad Character Rule and
  the Good Suffix Rule to maximize skip distances.

- **Data Structures:** Used a HashMap\<Character, Integer\> for the Bad
  Character table to support full Unicode characters rather than a
  fixed-size ASCII array.

- **Shift Logic:** On a mismatch, the algorithm calculates the shift
  amount suggested by both heuristics and selects the maximum value to
  ensure the most efficient jump while maintaining correctness.

**2. GoCrazy Algorithm Design:** GoCrazy is designed to combine the
average-case speed of Boyer-Moore-Horspool with the worst-case safety of
KMP.

- **Adaptive Switching:** The algorithm starts with a fast
  Horspool-style scan. It monitors efficiency by tracking \"small
  shifts\" (shifts of 1).

- **Safety Mechanism:** If the shift amount is consistently 1 (exceeding
  a threshold of m), it detects a potential worst-case scenario O(MN)
  and immediately hot-swaps to KMP to guarantee linear time performance
  for the remainder of the text.

**3.PreAnalysis Strategy**

Our strategy minimizes overhead by selecting the algorithm based on
input size and pattern characteristics:

- **Ultra-Light Early Exit:** For texts shorter than 512 characters, we
  immediately return Naive to avoid any pre-processing cost, as standard
  java overhead outweighs algorithmic gains on small data.

- **Scale Optimization:** For large texts (\>10,000 chars), we select
  Boyer-Moore to take advantage of its \"bad character\" skipping
  mechanism, which is most effective on larger datasets.

- **Pattern Structure Analysis:** For long patterns (\>100 chars), we
  perform a lightweight O(1) sampling of the first 20 characters:

- **Repetitive:** If \>70% repetition is detected (e.g., \"AAAA\"), we
  choose KMP for stability.

- **Non-Repetitive:** Otherwise, we switch to our hybrid GoCrazy
  algorithm.

- **Default:** For all other medium-sized random cases, we default to
  Naive.

**4. Analysis of Results**

- **Small Data:** Our tests confirmed that Naive is indeed the fastest
  solution for short inputs due to zero setup time.

- **Large Data:** Boyer-Moore demonstrated superior performance on large
  texts with large alphabets by skipping large sections of text.

- **Robustness:** The GoCrazy hybrid approach successfully prevented
  performance degradation on repetitive patterns where standard Horspool
  typically fails, validating the utility of our adaptive switching
  mechanism.

**Our Journey:**

This homework was highly instructional and strongly encouraged
independent research. We found the process of implementing complex
algorithms like Boyer-Moore challenging, particularly visualizing the
logic behind the \"Good Suffix Rule.\" However, overcoming these
challenges and researching hybrid methods for our \"GoCrazy\" algorithm
gave us a deeper understanding of real-world string matching
optimizations. It was a rewarding experience that effectively bridged
the gap between theoretical lecture notes and practical implementation.

**Resources Used:**

1\. Lecture Notes

2\. GeeksForGeeks - KMP & Boyer-Moore articles
https://www.geeksforgeeks.org/kmp-algorithm-for-pattern-searching/
https://www.geeksforgeeks.org/boyer-moore-algorithm-for-pattern-searching/

3\. ChatGPT and Gemini- For debugging and concept explanation

4\. Java Documentation - For HashMap usage
<https://docs.oracle.com/javase/8/docs/api/java/util/HashMap.html>
