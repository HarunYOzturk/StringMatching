# Student Project Documentation: Journey, Research, and References

## 1. Our Journey: Experience and Challenges

This project presented both intellectual challenge and significant learning opportunities. While implementing the core algorithms provided a deep understanding of string matching mechanics, the most demanding aspects were algorithm selection and optimization.

### Key Challenges Faced:
* **Boyer-Moore Complexity (Good Suffix Rule):** Implementing the Good Suffix Rule proved challenging and overly complex. Consequently, we focused solely on the Bad Character Rule for the final implementation, as it offered a more straightforward approach while still providing a strong level of efficiency.
* **Pre-Analysis Algorithm Conflict:** The greatest difficulty lay in designing the StudentPreAnalysis to accurately choose between the most efficient algorithms (KMP, Boyer-Moore, GoCrazy). Initially, KMP and GoCrazy often claimed scenarios optimally solved by Boyer-Moore, leading to Boyer-Moore being rarely selected. This conflict caused numerous incorrect (red-flagged) time results until the strategy was refined.
* **Strategy Refinement:** Through iterative testing and the aid of external tools (AI assistance), we developed specific, non-overlapping criteria based on pattern characteristics (e.g., last-character rarity for Boyer-Moore) to significantly improve accuracy and correct the initial incorrect selections.

## 2. Research Process and References

The project relied on external resources and tools to ensure optimal performance, particularly in the most complex phases of analysis.

### Research Methodology and Tool Use
* **Boyer-Moore Implementation:** The Bad Character Rule was implemented based on classical algorithm documentation. The more complex Good Suffix Rule was intentionally omitted, as research indicated its high implementation overhead.
* **Pre-Analysis Strategy (StudentPreAnalysis):** To achieve high accuracy and resolve algorithmic conflicts, **AI assistance (Large Language Model - LLM)** was utilized. The LLM helped to define specialized, non-overlapping heuristics, such as analyzing the **rarity of the pattern's last character** to trigger the efficient Boyer-Moore and focusing on **prefix repetition density** to confirm KMP selection.
* **Rabin-Karp:** Due to the lack of low-overhead heuristics that reliably favored Rabin-Karp in the given test set, it remained the least-chosen algorithm in the final strategy.

### References
1.  **Classical Algorithm Documentation:** Used for theoretical validation of time complexities and core algorithm mechanics (KMP, Boyer-Moore).
2.  **External Learning Resources:** Consulted for implementation details and edge case handling of the Bad Character Rule.
3.  **Large Language Model (LLM) Assistance:** Utilized for developing specialized and differentiating heuristics within the StudentPreAnalysis logic.


***
### Student Information 
* **Student:** Muzaffer Batmaz - 2205011021
* **Student:** Mikail Kaçmaz - 22050111080