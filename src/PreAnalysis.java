/**
 * PreAnalysis interface for students to implement their algorithm selection logic
 * 
 * Students should analyze the characteristics of the text and pattern to determine
 * which algorithm would be most efficient for the given input.
 * 
 * The system will automatically use this analysis if the chooseAlgorithm method
 * returns a non-null value.
 */
public abstract class PreAnalysis {
    
    /**
     * Analyze the text and pattern to choose the best algorithm
     * 
     * @param text The text to search in
     * @param pattern The pattern to search for
     * @return The name of the algorithm to use (e.g., "Naive", "KMP", "RabinKarp", "BoyerMoore", "GoCrazy")
     *         Return null if you want to skip pre-analysis and run all algorithms
     * 
     * Tips for students:
     * - Consider the length of the text and pattern
     * - Consider the characteristics of the pattern (repeating characters, etc.)
     * - Consider the alphabet size
     * - Think about which algorithm performs best in different scenarios
     */
    public abstract String chooseAlgorithm(String text, String pattern);
    
    /**
     * Get a description of your analysis strategy
     * This will be displayed in the output
     */
    public abstract String getStrategyDescription();
}


/*
Pattern length ≤ 3 → Naive
Reason: These patterns lead to very small m.
For small m, Naive often outperforms everything

Patterns with repeating prefixes → KMP
Reason: KMP is built for periodic or repetitive patterns,
Because its LPS table eliminates redundant comparisons.

Long pattern (m > 10) and long text (n > 1000) → Rabin–Karp
Reason: Hashing becomes beneficial when both text and pattern are long.

Everything else → Boyer–Moore
Reason: This is the general-purpose best-case algorithm.
It performs very well when,
pattern is moderately large,
text is mixed/randomized,
and the alphabet is not extremely small.

There are some other checks for certain edge cases.
They are explained at the comments within the method.

*/
class StudentPreAnalysis extends PreAnalysis {

    @Override
    public String chooseAlgorithm(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();

        // 1. Edge Cases
        if (m == 0) return "BoyerMoore";   // Your benchmark says BM is fastest for empty pattern
        if (n == 0) return "RabinKarp";    // RK was fastest in "Empty Text"

        // 2. Very short patterns (strong Naive performance)
        if (m <= 3) return "Naive";

        // 3. Pattern longer than text
        if (m > n) return "BoyerMoore";    // BM won this case in your tests

        // 4. Detect repeating or periodic patterns → KMP
        if (hasRepeatingPrefix(pattern)) {
            return "KMP";
        }

        // 5. Alphabet characteristics (sample-based)
        int distinct = countDistinct(pattern);

        // Very small alphabet (e.g., DNA or "aaaa...") → KMP tends to win
        if (distinct <= 4) {
            return "KMP";
        }

        // 6. Large pattern or large text cases
        boolean largePattern = m > 25;
        boolean largeText = n > 2000;

        if (largeText) {
            if (largePattern) {
                // BM was best for Very Long Text
                return "BoyerMoore";
            } else {
                // RK often wins medium patterns on long texts
                return "RabinKarp";
            }
        }

        // 7. Medium/long patterns on moderate text → RK best
        if (m > 10) {
            return "RabinKarp";
        }

        // 8. Default: Naive wins most generic cases
        return "Naive";
    }


    // Helper method for KMP-style periodicity detection
    private boolean hasRepeatingPrefix(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];

        int len = 0;
        for (int i = 1; i < m; ) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                lps[i++] = ++len;
            } else if (len > 0) {
                len = lps[len - 1];
            } else {
                lps[i++] = 0;
            }
        }

        int longestPrefix = lps[m - 1];
        return (longestPrefix > 0 && m % (m - longestPrefix) == 0);
    }

    // Helper method for determining distinct character count
    private int countDistinct(String s) {
        boolean[] seen = new boolean[256]; // ASCII-safe
        int count = 0;
        for (char c : s.toCharArray()) {
            if (!seen[c]) {
                seen[c] = true;
                count++;
            }
        }
        return count;
    }

    @Override
    public String getStrategyDescription() {
        return "Example strategy: Choose based on pattern length and characteristics";
    }
}


/**
 * Example implementation showing how pre-analysis could work
 * This is for demonstration purposes
 */
class ExamplePreAnalysis extends PreAnalysis {

    @Override
    public String chooseAlgorithm(String text, String pattern) {
        int textLen = text.length();
        int patternLen = pattern.length();

        // Simple heuristic example
        if (patternLen <= 3) {
            return "Naive"; // For very short patterns, naive is often fastest
        } else if (hasRepeatingPrefix(pattern)) {
            return "KMP"; // KMP is good for patterns with repeating prefixes
        } else if (patternLen > 10 && textLen > 1000) {
            return "RabinKarp"; // RabinKarp can be good for long patterns in long texts
        } else {
            return "Naive"; // Default to naive for other cases
        }
    }

    private boolean hasRepeatingPrefix(String pattern) {
        if (pattern.length() < 2) return false;

        // Check if first character repeats
        char first = pattern.charAt(0);
        int count = 0;
        for (int i = 0; i < Math.min(pattern.length(), 5); i++) {
            if (pattern.charAt(i) == first) count++;
        }
        return count >= 3;
    }

    @Override
    public String getStrategyDescription() {
        return "Example strategy: Choose based on pattern length and characteristics";
    }
}

/**
 * Instructor's pre-analysis implementation (for testing purposes only)
 * Students should NOT modify this class
 */
class InstructorPreAnalysis extends PreAnalysis {

    @Override
    public String chooseAlgorithm(String text, String pattern) {
        // This is a placeholder for instructor testing
        // Students should focus on implementing StudentPreAnalysis
        return null;
    }

    @Override
    public String getStrategyDescription() {
        return "Instructor's testing implementation";
    }
}
