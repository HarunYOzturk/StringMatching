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
 * This is where students write their pre-analysis logic
 */
class StudentPreAnalysis extends PreAnalysis {

    @Override
    public String chooseAlgorithm(String text, String pattern) {

        int n = text.length();
        int m = pattern.length();

        // 1) Empty pattern: trivial case, any algorithm works fast.
        if (m == 0) {
            return "Naive";
        }

        // 2) Very short patterns: naive matching is usually the fastest.
        if (m <= 3) {
            return "Naive";
        }

        // 3) If pattern is longer than the text, naive fails immediately with minimal overhead.
        if (m > n) {
            return "Naive";
        }

        // 4) Check if pattern has repeating prefix structure → KMP benefits significantly.
        if (hasRepeatingPrefix(pattern)) {
            return "KMP";
        }

        // 5) Large alphabet text: Boyer-Moore tends to skip more and performs better.
        int uniqueChars = countUnique(text);
        if (uniqueChars > 200) {
            return "BoyerMoore";
        }

        // 6) Very large text combined with a relatively long pattern → Rabin-Karp performs well.
        if (n > 5000 && m > 20) {
            return "RabinKarp";
        }

        // 7) Medium-sized text and medium/long pattern: RK often runs efficiently.
        if (m > 10 && n > 1000) {
            return "RabinKarp";
        }

        // 8) Potential best-case scenario for Boyer-Moore:
        // If pattern's last character rarely appears in the text, BM jumps further.
        if (isLikelyBMCase(pattern, text)) {
            return "BoyerMoore";
        }

        // 9) For small inputs, naive matching is typically the simplest and fastest.
        if (n < 1000 && m < 20) {
            return "Naive";
        }

        // 10) Default fallback: KMP provides stable linear-time performance.
        return "KMP";
    }

    // Detect whether the pattern contains a repeating prefix (KMP-friendly structure).
    private boolean hasRepeatingPrefix(String p) {
        int m = p.length();
        int j = 0;
        int[] lps = new int[m];

        for (int i = 1; i < m; ) {
            if (p.charAt(i) == p.charAt(j)) {
                lps[i++] = ++j;
            } else {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    lps[i++] = 0;
                }
            }
        }

        return lps[m - 1] > 0;
    }

    // Count how many distinct characters appear in the given string.
    private int countUnique(String s) {
        boolean[] seen = new boolean[65536];
        int count = 0;

        for (char c : s.toCharArray()) {
            if (!seen[c]) {
                seen[c] = true;
                count++;
            }
        }
        return count;
    }

    // Check whether Boyer-Moore is likely to perform well based on the last character frequency.
    private boolean isLikelyBMCase(String pattern, String text) {
        char last = pattern.charAt(pattern.length() - 1);
        int freq = 0;

        for (char c : text.toCharArray()) {
            if (c == last) {
                freq++;
            }
            if (freq > 5) {
                break;
            }
        }

        return freq <= 2;  // Rare last character → favorable for Boyer-Moore
    }

    @Override
    public String getStrategyDescription() {
        return "Uses heuristic-based pre-analysis to choose a suitable string matching algorithm.";
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
