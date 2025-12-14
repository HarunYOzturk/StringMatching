# My Journey: String Matching Algorithms Project

## 1. Research & Development Strategy
To develop our custom "GoCrazy" algorithm and the Pre-Analysis strategy, we utilized a combination of academic resources and AI assistance.

* **Algorithm Research:** We researched the **Boyer-Moore-Horspool** algorithm on *GeeksForGeeks* and *Wikipedia* to understand how it simplifies the standard Boyer-Moore algorithm by removing the "Good Suffix" rule.
* **Design:** Our strategy evolved significantly after analyzing the initial test runs. We focused heavily on the trade-off between "Initialization Overhead" and "Search Speed".

## 2. AI Assistance & Transparency
We used **Google Gemini** as a pair programmer and debugger throughout the process. Below are the specific areas where AI assistance was utilized:

### The "Hybrid Table" Optimization
When our initial GoCrazy implementation crashed on Unicode characters, we consulted Gemini to understand why our `int[256]` approach was insufficient. The AI suggested using a `HashMap` for safety or checking for ASCII compatibility to use arrays for speed. This led to our **Hybrid Table Strategy**: using lightweight arrays for ASCII-only patterns and switching to HashMaps for Unicode.

### The "Naive Dominance" Analysis
We fed the execution time tables into Gemini to identify why the "Naive" algorithm was winning so frequently on small inputs. The AI pointed out the "Initialization Overhead" factor (object creation costs) which we hadn't initially considered. This insight was crucial for tuning our `StudentPreAnalysis` logic. We set a strictly "Naive-first" approach for texts under 300 characters, as the overhead of creating HashMaps (for BM/Horspool) or arrays (for KMP) often took longer than the actual search itself.

## Transparency Note: We did not simply copy-paste generated code. We actively iterated on the logic based on test results. For instance, our initial Pre-Analysis strategy was too complex, and after discussing results with the AI, we simplified it to prioritize Naive for small inputs.

## 3. Reflection: Theoretical Complexity vs. Practical Overhead
This assignment was an eye-opener regarding "Theoretical Complexity vs. Practical Overhead".

* **What We Learned:** We initially expected complex algorithms like KMP or Boyer-Moore to dominate in all cases. However, we learned that object creation and preprocessing have a tangible cost in Java.
* **The Outcome:** For the specific dataset provided (mostly small strings), the brute-force approach of the Naive algorithm was practically faster than smarter algorithms due to Java's JVM loop optimizations.
* **Challenges:** Optimizing the GoCrazy algorithm to beat Naive was difficult because Naive sets a very high bar on small inputs. We also gained significant experience dealing with Java's character handling regarding Unicode issues.


**Students:**
* Galip Yılmaz - 22050111013
* Abdullah Tülek - 22050111079