We began by implementing the Boyer-Moore algorithm's bad character rule using [GfG's](https://www.geeksforgeeks.org/dsa/boyer-moore-algorithm-for-pattern-searching/) to learn and understand it. The bad character rule implementation was easy, but when we moved to the good suffix table, we really struggled. Understanding what we had to do was easy, but understanding how to implement it efficiently was really hard.

We initially used [GfG's good suffix page](https://www.geeksforgeeks.org/dsa/boyer-moore-algorithm-good-suffix-heuristic/), but we couldn't continue with that approach. We then studied the rule from this [Medium page](https://medium.com/@neethamadhu.ma/good-suffix-rule-in-boyer-moore-algorithm-explained-simply-9d9b6d20a773), which really helped us understand and grasp the algorithm's logic. After that, we continued from GfG's good suffix page again.

Once our study was done, we implemented the algorithm on our own and tested it with the given test cases. The first 2-3 trials were problematic, but we debugged them. Then we sent our implementation to ChatGPT to get help testing edge cases.

Next, we started thinking about preprocessing. We identified some easy-to-calculate parameters, listed them with our thoughts about preprocessing, and had ChatGPT evaluate them. We got criticized and received suggestions. Then we gave it our full parameter list and asked it to create a decision tree (just if-else structures) based on those tweakable parameters, tweaked them according to our preferences like higher unique character counts favoring Boyer-Moore, and more repetitive patterns favoring KMP.

This led us to think why don't we train a model if we're going to tweak these parameters according to test cases and our intuition? We searched for string matching datasets containing text-pattern-expected output pairs, but we couldn't find suitable datasets. Creating one would take too much time and would probably have high bias. We think training a lightweight model would be good for this purpose, but it needs huge amounts of data containing and lots of different string matching use cases. Creating a linear model would make interpretation easy and could work fast as preprocessing.

When we searched for related work (with ChatGPT), we found some experiments for these purposes and they were relatively new research. We didn't use these approaches in our work, but they show that people have already thought this way and conducted experiments to find methods:

- [Entropy-Based Approach in Selection Exact String-Matching Algorithms](https://www.mdpi.com/1099-4300/23/1/31)
    - This paper is about preprocessing patterns and calculating entropy, then selecting an algorithm according to it.
- [Boosting exact pattern matching with extreme gradient boosting (and more)](https://www.researchgate.net/publication/390306340_Boosting_exact_pattern_matching_with_extreme_gradient_boosting_and_more) 
    - This is also a new paper. They use lots of features from distinct string matching algorithms that we don't even understand most of it, and they used tree-based models and gained 11% speed.

**Our comments:**

 - We got really familiar with string matching algorithms and their heuristics. The Boyer-Moore good suffix table implementation was really hard for us and took a long time to grasp. We got the decision tree structure from an LLM because we didn't want to get lost in writing huge if-else structures, and we also wanted to avoid method call or object creation overheads. However, this makes our code really hard to read, and we're not sure if it's worth it or not.

 - We don't think tweaking parameter thresholds like this is meaningful. 
  - We can't evaluate the patterns that deeply because it costs too much, but without it we also can't predict the best algorithm. 
  - Our test cases mostly favor naive because of our pattern sizes. Even when it looks like results absolutely favor another algorithm, longer test cases would be better to evaluate our preprocessing.
  - When we re-run tests, the winner algorithm differ too much. 
  - It looks like the best preprocessing is just predicting naive for shared test cases, and most probably we made it worse than only predicting naive.
  - There's a weird situation with the Unicode character case. If we choose Rabin-Karp over Boyer-Moore, it differs by 8 μs, and if we choose Boyer-Moore, then it differs by 8 μs to Rabin-Karp.

We write our reasoning for preprocessing in the StudentPreAnalysis description section.

[Here is our chat history](https://chatgpt.com/share/69399973-e708-8002-a413-4291b60a7927)

---

Muhammed Enes Uğraş  23050151030  
Nurten Çiftçioğlu    21050111027