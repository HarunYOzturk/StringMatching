# My Journey: String Matching Algorithms

## 1. Learning Process & Resources
At the beginning of the homework, I used the lecture videos on Aybuzem to understand the logic behind the Boyer-Moore algorithm. To visualize how the shifting mechanism works, I also used this helpful website:
[Boyer-Moore Visualization Tool](https://cmps-people.ok.ubc.ca/ylucet/DS/BoyerMoore.html)

## 2. Challenges with Boyer-Moore
For every part of this assignment, my goal was to write the code on my own first. However, implementing the Boyer-Moore algorithm was challenging for me. I used AI assistance and classical "Bad Character Rule" examples from the internet to understand the logic correctly and fix index errors.

## 3. My "GoCrazy" Algorithm Journey
For the custom algorithm task, I was inspired by Question 3 from our Midterm Exam, which focused on bitwise operations. I wanted to combine the **Rolling Hash** technique with **Bitwise Operations** to understand both concepts better.

I designed a Bitwise Rolling Hash algorithm with two key optimizations:
* **Bitwise Shift instead of Multiplication:** I learned that bitwise operations (like `<< 5`) are faster for the CPU than standard multiplication.
* **Integer Overflow instead of Modulo:** I realized that we don't strictly need a modulo (`%`) operator. Using Java's native "Integer Overflow" mechanism acts as a natural modulo, allowing me to get rid of an expensive operation.

This approach helped me create a fast **O(N)** algorithm while applying the bit-manipulation logic I learned from the exam.

## 4. Pre-Analysis Strategy and Why I Chose It
I designed a heuristic decision tree to select the most efficient algorithm based on the text and pattern characteristics. My main goal was to minimize the "overhead" (setup cost) of the algorithms.

Here is my selection logic:
* **Micro-Patterns (Length <= 6):** I chose the **Naive** algorithm. For very short patterns, building complex tables or calculating hashes takes more time than just scanning the text directly.
* **Repetitive Patterns:** I implemented a helper check for patterns like "AAAAA". In these cases, Boyer-Moore performs poorly because it cannot skip many characters. Therefore, I chose **KMP**, which guarantees linear time safety for repetitive inputs.
* **Long Patterns (Length > 17):** I chose **Boyer-Moore**. As the pattern gets longer, Boyer-Moore's "Bad Character Rule" allows it to skip larger sections of the text, making it the fastest option.
* **Medium Patterns:** For the remaining cases (7 to 17 characters), I selected my custom **GoCrazy (Bitwise Rolling Hash)**. It offers a stable **O(N)** performance without the complex preprocessing of Boyer-Moore.

## 5. Analysis of Results
After implementing the fixes for edge cases (such as empty patterns and Unicode characters), all algorithms successfully passed **30/30** test cases.

* **Performance:** My "GoCrazy" algorithm performed exceptionally well, often beating Naive and KMP in medium-sized inputs due to the efficiency of bitwise operations.
* **Optimization:** The Pre-Analysis results showed that choosing the right algorithm makes a measurable difference. For example, selecting Boyer-Moore for "Very Long Text" saved significant time compared to Naive, while selecting Naive for "Pattern at Beginning" avoided unnecessary initialization costs.
* **Conclusion:** This assignment demonstrated that there is no single "best" algorithm; the best choice depends entirely on the data characteristics.

---
**Name:** Mustafa Bulut
**Student ID:** 22050111015
