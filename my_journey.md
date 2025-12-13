This assignment showed me that even strong algorithms do not always give the best performance in every case. The runtime highly depends on the input text and pattern, which are not under my control. Because of this, it is not realistic to expect one algorithm to always be the fastest.

From the test results, I observed that simple algorithms like Naive can outperform advanced ones on short patterns, while KMP or GoCrazy works better on repetitive or longer inputs. This helped me understand that algorithm selection depends more on input characteristics than only theoretical complexity.

I also learned the importance of workflow. This was my first time actively using GitHub. At the beginning, I misunderstood the instructions and worked outside the repository, then later realized I should have forked and cloned it first. This mistake showed me how attention and correct workflow can directly affect the quality of an assignment.

In the pre-analysis part, my goal was not to always choose the perfect algorithm, but to use simple heuristics with very low overhead. Although my pre-analysis selected the fastest algorithm in 16 out of 30 cases (53.3%), the results showed that predicting the best algorithm is difficult without actually running them.

This is mainly because performance differences depend on many factors such as pattern structure, repetition, and runtime environment. In some cases, the cost of pre-analysis itself reduced the overall benefit. Therefore, I consider my pre-analysis as a reasonable heuristic-based attempt rather than a guaranteed optimizer.

I used online resources and an LLM to refresh my understanding of string matching algorithms, especially Boyer-Moore and heuristic-based algorithm selection. These resources were mainly used for conceptual guidance, such as understanding bad character logic and general optimization ideas.

All implementations were written and adapted by me, and I tested them using the provided test framework. I did not directly copy any full implementation from external sources. The LLM was used as a learning and support tool, not as a replacement for my own work.


Name:Pakize Sumeyye Soylemez
Student Number:255101402