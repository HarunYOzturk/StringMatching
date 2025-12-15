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


/**
 * Default implementation that students should modify
 * This is where students write their pre-analysis logic.
 * 
 * Logic Explanation:
 * 1. Short patterns (<= 3 chars) use Naive due to low overhead.
 * 2. Patterns longer than text use RabinKarp (fastest failure).
 * 3. Binary data uses RabinKarp for robustness.
 * 4. Highly repetitive patterns use KMP (single unique char) or RabinKarp (high overlap) to avoid worst-case behavior.
 * 5. Long patterns (> 20 chars) use BoyerMoore for efficient skipping.
 * 6. All other cases default to GoCrazy (hybrid) which handles mixed scenarios well.
 */
class StudentPreAnalysis extends PreAnalysis {
    
    @Override
    public String chooseAlgorithm(String text, String pattern) {
        if (pattern == null || text == null) return "Naive";
        int m = pattern.length();
        int n = text.length();

        // 1. Very short patterns: Naive is fastest (no overhead)
        if (m <= 2) {
            return "Naive";
        }

        // 2. Sanity check
        if (m > n) {
            return "RabinKarp";
        }

        // 3. Binary data: RabinKarp is robust
        if (isBinaryData(text, 1000)) {
            return "RabinKarp";
        }

        // 4. Single unique character: KMP is O(n)
        int uniqueChars = countUniqueChars(pattern);
        if (uniqueChars == 1) {
            return "KMP";
        }
        
        // 5. Long text: BoyerMoore skips are valuable
        if (n > 5000) {
            return "BoyerMoore";
        }

        // 6. Repetitive patterns: KMP avoids worst-case O(nm)
        double repeatScore = calculatePatternRepeatScore(pattern);
        if (repeatScore > 0.3) {
             return "KMP";
        }

        // 7. Long patterns: BoyerMoore is best
        if (m > 10) {
            return "BoyerMoore";
        }

        // 8. Default to hybrid
        return "GoCrazy";
    }
    
    @Override
    public String getStrategyDescription() {
        return "Adaptive strategy based on empirical test results: Naive for short, KMP/RK for repetitive, BM for long patterns, GoCrazy for mixed cases.";
    }

    // --- Feature Extraction Helpers ---

    private boolean isBinaryData(String text, int limit) {
        int checkLen = Math.min(text.length(), limit);
        int nonPrintable = 0;
        for (int i = 0; i < checkLen; i++) {
            char c = text.charAt(i);
            if (c < 32 && c != '\t' && c != '\n' && c != '\r') {
                nonPrintable++;
            }
        }
        return (double) nonPrintable / checkLen > 0.1;
    }

    private int countUniqueChars(String pattern) {
        boolean[] seen = new boolean[256]; 
        int count = 0;
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (c < 256 && !seen[c]) {
                seen[c] = true;
                count++;
            }
        }
        return count == 0 ? 1 : count;
    }

    private double calculatePatternRepeatScore(String pattern) {
        if (pattern.isEmpty()) return 0.0;
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;
        lps[0] = 0;
        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) len = lps[len - 1];
                else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        int maxLps = 0;
        for (int val : lps) maxLps = Math.max(maxLps, val);
        return (double) maxLps / m;
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