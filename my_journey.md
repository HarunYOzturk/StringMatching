# My Journey – String Matching Homework

## My Research Process

While working on this homework, I used both online resources and an LLM (ChatGPT) as supporting tools.
I did not use these tools to directly generate full implementations. Instead, I used them mainly to
review concepts, validate edge cases, and reason about algorithm behavior.

From the internet, I reviewed multiple explanations of the Boyer–Moore algorithm, especially the
bad-character and good-suffix heuristics, since the good-suffix rule is easy to misunderstand during
implementation. I also revisited KMP and LPS/border concepts to justify using border length as a
repetition signal in the pre-analysis stage. Resources such as Wikipedia, GeeksforGeeks, Baeldung,
TopCoder articles, and Thierry Lecroq’s string searching notes were helpful for cross-checking
definitions and standard practices.

I used ChatGPT mainly as a reviewer and discussion partner. For example, I asked it to walk through
small examples of good-suffix preprocessing, to confirm that shifts are always positive, and to
discuss which pattern features could be useful for a low-overhead pre-analysis strategy. All final
design decisions and code were written and tested by me.

## Challenges I Faced

The most challenging part of the homework was implementing the good-suffix heuristic correctly.
Although the idea is well known theoretically, small indexing mistakes can cause incorrect shifts or
miss overlapping matches. Debugging these cases required careful reasoning and constructing specific
test strings rather than relying only on intuition.

Another challenge was designing the pre-analysis strategy. Choosing thresholds for pattern length,
alphabet size, and repetition was not straightforward. In many cases, multiple algorithms perform
very similarly, which makes perfect selection impossible. This taught me that practical algorithm
selection is often about making reasonable decisions with minimal overhead rather than finding a
perfect rule.

## What I Learned

This homework helped me understand that real performance depends not only on asymptotic complexity,
but also on constant factors, preprocessing cost, and input characteristics. Implementing multiple
string matching algorithms and comparing them in practice improved my understanding of how theory
and real-world behavior differ.

I also learned the importance of disciplined handling of edge cases and overlaps, and how border
information plays a key role in both correctness and efficiency.

## Overall Opinion

Overall, I found this homework challenging but very educational. It encouraged me to think beyond
“correct code” and focus on reasoning, evaluation, and transparency. A possible improvement could
be providing a small standardized guideline for reporting result summaries, but the assignment
itself was well designed and meaningful.

---

Cemil Alp  
Student No: 21050111005
