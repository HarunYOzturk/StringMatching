/**
 * PreAnalysis interface for students to implement their algorithm selection
 * logic
 * 
 * Students should analyze the characteristics of the text and pattern to
 * determine
 * which algorithm would be most efficient for the given input.
 * 
 * The system will automatically use this analysis if the chooseAlgorithm method
 * returns a non-null value.
 */
public abstract class PreAnalysis {

    /**
     * Analyze the text and pattern to choose the best algorithm
     * 
     * @param text    The text to search in
     * @param pattern The pattern to search for
     * @return The name of the algorithm to use (e.g., "Naive", "KMP", "RabinKarp",
     *         "BoyerMoore", "GoCrazy")
     *         Return null if you want to skip pre-analysis and run all algorithms
     * 
     *         Tips for students:
     *         - Consider the length of the text and pattern
     *         - Consider the characteristics of the pattern (repeating characters,
     *         etc.)
     *         - Consider the alphabet size
     *         - Think about which algorithm performs best in different scenarios
     */
    public abstract String chooseAlgorithm(String text, String pattern);

    /**
     * Get a description of your analysis strategy
     * This will be displayed in the output
     */
    public abstract String getStrategyDescription();
}

/**
 * Default implementation that students should modify
 * This is where students write their pre-analysis logic
 */
class StudentPreAnalysis extends PreAnalysis {

    @Override
    public String chooseAlgorithm(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();

        // edge cases
        if (m == 0 || n == 0 || m > n) {
            return "Naive";
        }

        // tiny inputs -> naive
        if (m <= 3 || n <= 128) {
            return "Naive";
        }

        // repetition-heavy patterns -> KMP
        if (looksRepetitive(pattern) || hasStrongBorder(pattern)) {
            return "KMP";
        }

        // very large text -> avoid naive
        if (n >= 2000) {
            return "RabinKarp";
        }

        // long-long -> Rabin-Karp
        if (m >= 32 && n >= 5000) {
            return "RabinKarp";
        }

        // default
        return "BoyerMoore";
    }

    private boolean hasStrongBorder(String pattern) {
        int m = pattern.length();
        if (m == 0)
            return false;

        int[] lps = new int[m];
        int len = 0;

        for (int i = 1; i < m;) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                lps[i++] = ++len;
            } else if (len != 0) {
                len = lps[len - 1];
            } else {
                lps[i++] = 0;
            }
        }

        int border = lps[m - 1];
        return border >= Math.max(2, m / 4);
    }

    private boolean looksRepetitive(String p) {
        int m = p.length();
        if (m < 6)
            return false;

        // Unicode-safe
        int distinct = 0;
        boolean[] seen = new boolean[Character.MAX_VALUE + 1];

        for (int i = 0; i < m; i++) {
            char c = p.charAt(i);
            if (!seen[c]) {
                seen[c] = true;
                distinct++;
                // early exit: if too diverse, not repetitive
                if (distinct > Math.max(8, m / 3)) {
                    return false;
                }
            }
        }

        return distinct <= Math.max(2, m / 4);
    }

    @Override
    public String getStrategyDescription() {
        return "Naive for tiny inputs; KMP for repetitive/border-heavy patterns; RabinKarp for large texts; BoyerMoore otherwise";
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
        if (pattern.length() < 2)
            return false;

        // Check if first character repeats
        char first = pattern.charAt(0);
        int count = 0;
        for (int i = 0; i < Math.min(pattern.length(), 5); i++) {
            if (pattern.charAt(i) == first)
                count++;
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
