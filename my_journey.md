# My Journey - String Matching Algorithms Homework



---
## How it all started

When I first saw this assignment, I honestly felt a bit overwhelmed. We had to implement multiple string matching algorithms, design our own custom algorithm, and create a pre-analysis system. I knew this was going to take time, so I broke it down into manageable chunks.

I ended up working on this across **three main sessions** over several days. The first session was all about understanding what the hell was going on with these algorithms. The second was actually writing the code. The third was the painful debugging and testing phase.

One night my classes ran really late and I only had about an hour to work on this before I literally fell asleep at my desk. That definitely slowed me down, but I picked it back up the next day.

**Total time spent:** Around 8-10 hours
- 2 hours understanding the framework and algorithms
- 2 hours on Boyer-Moore
- 2 hours designing and coding GoCrazy
- 1.5 hours on the pre-analysis strategy
- 1.5 hours debugging (mostly that damn Unicode bug)
- 1 hour testing everything
- 1 hour writing the report

---

## The research phase

I started by going back to our lecture slides to refresh my memory on how these algorithms work. The theory made sense in class, but actually implementing them? That's a different story.

**What I used:**
- **Lecture notes** - Good for the high-level concepts
- **CLRS textbook** (Chapter 32) - This was helpful for Boyer-Moore, but honestly the explanations are pretty dense
- **GeeksforGeeks** - I used their articles on KMP and Boyer-Moore to see actual implementations and understand the preprocessing steps better
- **Wikipedia** - Just to get a broader view of different string matching approaches

The hardest part to visualize was the **Good Suffix Rule** in Boyer-Moore. The textbook explanation was confusing as hell, so I had to look at multiple sources to actually understand what was happening.

---

## Implementation journey

### 1. Starting with the basics (Naive, KMP, RabinKarp)

I started with the simpler algorithms to get comfortable with the framework. Naive was straightforward - just check every position. KMP required building the LPS (failure function) array, which took me a bit to wrap my head around. I referenced GeeksforGeeks to make sure I was building it correctly.

RabinKarp was interesting with the rolling hash concept, but I had to be careful with the modulo arithmetic to avoid overflow issues.

### 2. Boyer-Moore - The main challenge

This is where things got tough. Boyer-Moore is supposed to be one of the most efficient algorithms, especially for long patterns, but implementing it correctly is tricky.

**My approach:**
- I focused mainly on the **Bad Character Rule** because it's more intuitive than the Good Suffix Rule
- Built a bad character table that stores the rightmost position of each character in the pattern
- The algorithm scans from right to left, which feels backwards at first but makes sense once you see how the skipping works

**The preprocessing:**
```
For each character in the pattern, I store its rightmost position.
When we hit a mismatch, we look at the mismatched character in the text.
If it's in our pattern, we shift to align with its rightmost occurrence.
If it's not in the pattern at all, we can skip the entire pattern length.
```

**The Unicode nightmare:**

Everything was working fine until I hit the Unicode test case. The problem? I was using a fixed-size array (256) for ASCII characters, but Unicode characters can have values way higher than that. This caused an `ArrayIndexOutOfBoundsException`.

My fix:
- For ASCII characters (< 256), use the fast array lookup
- For Unicode characters, do a linear search through the pattern
- It's not as fast for Unicode, but it works and doesn't crash

Even after fixing the crash, the Unicode test still shows "FAIL". There's some edge case in my shift calculation that I haven't figured out yet. This is honestly the part I'm least satisfied with.

**Result:** 29/30 test cases passed. That one Unicode failure still bugs me.

### 3. GoCrazy - My custom algorithm

This was actually the fun part. At first I tried to invent some completely new algorithm, but I quickly realized that was stupid. The real insight is that **different algorithms work better in different situations**.

So I designed an **adaptive hybrid approach** that picks a strategy based on the pattern length:

**The strategy:**
- **Single character?** Just scan through the text directly. No point in fancy preprocessing.
- **2-4 characters?** Use a "first-last filter" - check if the first and last characters match before checking the middle. This eliminates a lot of false starts.
- **5-10 characters?** Hybrid approach with intelligent skipping. When we find a mismatch, calculate how far we can safely jump based on where that character appears in the pattern.
- **11+ characters?** Modified Boyer-Moore with an extra trick - check the middle character first as an additional filter before doing the full right-to-left scan.

**Why this works:**

The key insight is that for short patterns, the overhead of preprocessing (building tables, etc.) often takes longer than just doing a simple scan. But for longer patterns, that preprocessing pays off because you can skip large sections of text.

**Results:**
- Won 13 out of 30 test cases (43% win rate)
- **Best average time: 3.446 µs** (fastest overall!)
- 100% correctness (30/30 passed)
- Particularly strong on: "No Match", "Long Text Multiple Matches", "Very Long Text"

I'm actually really proud of this. It shows that being smart about when to use which technique can beat always using the "theoretically best" algorithm.

### 4. Pre-Analysis - Picking the right tool

The pre-analysis system is supposed to look at the input and decide which algorithm to use. I wanted something smarter than just checking pattern length.

**My metric: Character Diversity**

```
Diversity = (Number of unique characters) / (Pattern length)
```

Examples:
- "aaaa" → 1/4 = 0.25 (low diversity, lots of repetition)
- "abcd" → 4/4 = 1.0 (high diversity, all unique)
- "hello" → 4/5 = 0.8 (high diversity)

**The decision logic:**
1. Empty or impossible patterns → Naive (handles edge cases)
2. Very short (≤ 2 chars) → Naive (overhead not worth it)
3. Low diversity (< 0.5) → KMP (good with repeating patterns)
4. Long + high diversity (≥ 7 chars, diversity > 0.7) → Boyer-Moore
5. Medium patterns in large text → RabinKarp
6. Short-medium (≤ 5 chars) → Naive
7. Default → Boyer-Moore

**Why diversity matters:**

If a pattern has lots of repeating characters (low diversity), KMP handles it well because of how the failure function works. If it's highly diverse, Boyer-Moore can skip more effectively because mismatches are more informative.

**Results:**
- Chose Naive 20 times (67%)
- Chose KMP 5 times (17%)
- Chose Boyer-Moore 5 times (17%)

The conservative approach (favoring Naive) actually worked pretty well since Naive won 50% of the test cases. But there's definitely room for improvement in identifying when the more complex algorithms would help.

---

## The challenges I faced

### Challenge 1: Boyer-Moore Unicode bug

This was the most frustrating part of the entire assignment. I spent hours trying to figure out why my implementation kept failing on Unicode characters.

The problem: Unicode characters can have values greater than 255, which broke my fixed-size array.

My solution: Check if the character is ASCII (< 256) and use the array, otherwise search the pattern directly.

Current status: It doesn't crash anymore, but there's still a logic error somewhere in the shift calculation for Unicode. The test shows "FAIL" which means wrong output, not a crash. I tried multiple approaches but couldn't nail down the exact issue. This is something I'd want to revisit if I had more time.

### Challenge 2: Understanding Good Suffix Rule

The Good Suffix Rule in Boyer-Moore is conceptually harder than the Bad Character Rule. The textbook explanation was really dense and hard to follow. I ended up using ChatGPT to explain it step-by-step with examples, which helped a lot. Even then, I decided to focus mainly on the Bad Character Rule for my implementation because I could understand and implement it correctly.

### Challenge 3: Pre-analysis strategy design

Figuring out what metrics to use for algorithm selection was harder than I expected. My first attempt was just based on pattern length, which was too simple. Then I tried pattern length + text length, which was better but still not great. Finally I came up with the diversity metric, which feels more sophisticated and actually makes sense theoretically.

The tricky part is keeping the analysis cheap. If the pre-analysis takes too long, it defeats the purpose. My diversity calculation is O(m) where m is pattern length, which is acceptable.

### Challenge 4: GoCrazy design

I initially wanted to invent some completely new algorithm with a cool name. But after thinking about it, I realized that's not realistic. The breakthrough came when I shifted my thinking to "adaptive selection" - using the right tool for the job rather than inventing a new tool.

---

## What I actually learned

### Technical stuff

**Algorithm trade-offs are real:**
In theory, Boyer-Moore should be the fastest. In practice, on these test cases, Naive won 50% of the time! Why? Because for short patterns and short texts, the preprocessing overhead of Boyer-Moore (building the bad character table, etc.) takes longer than just doing a simple scan. This was a huge eye-opener for me.

**Preprocessing costs matter:**
Complex algorithms like Boyer-Moore and KMP spend time building tables before they even start searching. For small inputs, this preprocessing can dominate the total runtime. You have to consider the total cost, not just the search phase.

**Unicode is a pain:**
I learned the hard way that assuming ASCII is not enough. Real-world text has all kinds of characters, and you need to handle them properly. This is one of those things that seems simple until you actually implement it.

**Adaptive strategies work:**
My GoCrazy algorithm proved that being smart about when to use which technique can beat always using the "theoretically optimal" algorithm. Context matters.

### Practical skills

**Debugging systematically:**
When I hit the Unicode bug, I had to learn to debug systematically - add print statements, test with simple cases, isolate the problem. It's not glamorous but it's essential.

**Reading documentation:**
I spent a lot of time reading Java documentation for HashMap, understanding how to handle Unicode characters, etc. Being able to read and understand documentation is a crucial skill.

**Testing thoroughly:**
The test cases in this assignment were really helpful. They covered edge cases I wouldn't have thought of (empty pattern, pattern longer than text, Unicode, etc.). I learned to think about edge cases upfront.

**Writing clear code:**
I tried to use descriptive variable names and write comments that explain the "why" not just the "what". Looking back at my code, I can actually understand what I was thinking, which is important.

---

## Using AI tools (being honest)

I used ChatGPT, Gemini, and Claude throughout this assignment. I want to be completely transparent about how I used them:

**What I used AI for:**
1. **Understanding concepts:** When the textbook explanation of the Good Suffix Rule was confusing, I asked ChatGPT to explain it step-by-step with examples. This was like having a tutor.
2. **Debugging:** When I got `IndexOutOfBoundsException` errors, I pasted my code and the error, and asked for help figuring out what was wrong. The AI helped me see that I wasn't handling Unicode properly.
3. **Code structure:** I asked for feedback on my variable naming and whether my code was readable.
4. **Brainstorming:** For the GoCrazy algorithm, I discussed different approaches with the AI and got suggestions for the adaptive strategy.

**What I did myself:**
1. **All design decisions:** I chose the diversity metric for pre-analysis. I designed the adaptive strategy for GoCrazy. These were my ideas.
2. **Understanding:** I didn't just copy code. I made sure I understood what every line does and why.
3. **Testing and analysis:** I ran all the tests, analyzed the results, and drew my own conclusions.
4. **Writing:** This report and all the comments in my code are my own words.

**My take on using AI:**
I think using AI as a learning tool is fine as long as you're actually learning. If you just copy-paste code without understanding it, you're screwing yourself over. But if you use it to understand concepts better and debug your own logic, it's like having a really patient tutor available 24/7.

The key is: **I used AI to help me implement MY ideas, not to generate ideas for me.**

---

## Results and what they mean

### Overall performance

| Algorithm | Tests Passed | Avg Time (µs) | Wins | Win Rate |
|-----------|--------------|---------------|------|----------|
| Naive | 30/30 | 3.942 | 15 | 50% |
| GoCrazy | 30/30 | 3.446 | 13 | 43% |
| RabinKarp | 30/30 | 5.561 | 0 | 0% |
| KMP | 30/30 | 4.867 | 3 | 10% |
| BoyerMoore | 29/30 | 5.346 | 1 | 3% |

### What surprised me

**Naive won 50% of test cases.** This was shocking at first, but it makes sense. For small inputs, simple is better. The overhead of fancy algorithms isn't worth it.

**GoCrazy had the best average time.** My adaptive approach worked! By choosing the right strategy for each situation, it beat the specialized algorithms on average.

**Boyer-Moore only won 1 test.** This surprised me because Boyer-Moore is supposed to be really efficient. But the test patterns were relatively short, and Boyer-Moore really shines with long patterns. Also, my implementation might not be fully optimized.

**RabinKarp won nothing.** The hash calculation overhead killed it on these test cases. RabinKarp is designed for multiple pattern matching, which wasn't tested here.

**KMP won exactly the cases it should.** It won on "Single Character", "All Same Character", and "Alternating Pattern" - all cases with repeating patterns. This validates that KMP is good for its niche.

### What this taught me

The "best" algorithm depends entirely on the input. There's no one-size-fits-all solution. Understanding the characteristics of your data and choosing the right tool is more important than memorizing which algorithm has the best Big-O notation.

---

## Honest reflection

### What went well
- My GoCrazy algorithm actually worked really well and I'm proud of it
- The code is clean and readable with good comments
- The pre-analysis strategy is reasonable and has minimal overhead
- I learned a ton about practical algorithm performance vs. theoretical performance
- I was honest about using AI tools and transparent about my process

### What could be better
- That damn Unicode bug in Boyer-Moore still haunts me. I wish I could have figured it out completely.
- My pre-analysis could be smarter. It's conservative (favors Naive) which works okay, but there's room for better heuristics.
- I could have tested more edge cases during development instead of finding them during final testing.
- My RabinKarp implementation might not be optimal - it didn't win any tests.
- I focused mainly on Bad Character Rule for Boyer-Moore. Implementing Good Suffix Rule properly would have been better.

### What I'd do differently next time
- Start testing earlier with edge cases
- Spend more time understanding the tricky parts (like Good Suffix Rule) before coding
- Maybe implement a more sophisticated pre-analysis that considers more factors
- Test with Unicode from the beginning instead of treating it as an afterthought

---

## Feedback on the assignment

### What I liked
- The testing framework is excellent. Clear output, helpful metrics, easy to see what's working and what's not.
- The pre-analysis comparison feature is really insightful. It shows you whether your analysis is actually helping or hurting.
- Multiple test cases with different characteristics (short/long text, repeating patterns, etc.) really test your understanding.
- The "Your Journey" section encourages honest reflection, which I appreciate.
- Freedom to design our own algorithm (GoCrazy) was creative and fun. It's not just implementing textbook algorithms.

### Suggestions for improvement
- More guidance on Unicode handling would be helpful. It's a real-world concern but wasn't covered much in lectures.
- A sample report structure would be nice. I wasn't sure how detailed to be.
- Some test cases with very long patterns (100+ characters) would show Boyer-Moore's strengths better.
- Test cases for multiple pattern matching would show where RabinKarp excels.
- More step-by-step walkthroughs of the tricky preprocessing parts (especially Good Suffix Rule) would help future students.

---

## Final thoughts

This assignment was tough but rewarding. I spent way more time on it than I initially expected, but I learned a lot.

The biggest lesson: **Theory and practice are different.** In class we learn that Boyer-Moore is O(n/m) in the best case, which sounds amazing. But in practice, on small inputs, Naive beats it because of preprocessing overhead. Understanding this gap between theory and practice is valuable.

I also learned that being honest about your process (including using AI tools) is important. I didn't just copy code from ChatGPT. I used it as a learning tool to understand concepts better and debug my logic. There's a big difference.

Looking back, I'm satisfied with what I accomplished. My GoCrazy algorithm performed really well, my code is clean and readable, and I learned a ton about string matching algorithms. Yes, there's that Unicode bug that still bothers me, and yes, there are things I could improve. But that's okay. That's part of learning.

If I had to do this again, I'd start earlier and test more thoroughly from the beginning. But overall, this was a valuable experience that taught me not just about algorithms, but about problem-solving, debugging, and the importance of choosing the right tool for the job.

---

## Resources I used

1. **Course lecture slides** - CENG303 Data Structures and Algorithms
2. **CLRS textbook** - Chapter 32 (String Matching)
3. **Wikipedia** - String searching algorithm (for broader context)
4. **AI Tools (ChatGPT, Gemini, Claude)** - For concept explanation, debugging help, and code feedback

---

**Name:** Ömer Faruk Başaran  
**Student Number:** 21050111041  

