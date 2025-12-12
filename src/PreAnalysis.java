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
    
     // Example considerations:
        // - If pattern is very short, Naive might be fastest
        // - If pattern has repeating prefixes, KMP is good
        // - If pattern is long and text is very long, RabinKarp might be good
        // - If alphabet is small, Boyer-Moore can be very efficient
        //
        // For now, this returns null which means "run all algorithms"
        // Students should replace this with their logic
    @Override
    public String chooseAlgorithm(String text, String pattern) {
        // TODO: Students should implement their analysis logic here


        int n = text.length();
        int m = pattern.length();

        // Special trivial cases
        if (m == 0) return "KMP";            // KMP consistently fastest for empty pattern
        if (m > n) return "GoCrazy";         // GoCrazy was the winner in this scenario

        int alphabet = estimateAlphabet(text);
        int lps = longestPrefixSuffix(pattern);
        boolean highlyRepeated = (lps >= m / 2);

        // Very short patterns: naive consistently dominates
        if (m <= 3) return "Naive";

        // Highly repetitive patterns: KMP excels
        if (highlyRepeated) return "KMP";

        // Large text handling
        if (n >= 5000) {
            if (alphabet > 16) return "BoyerMoore";  // BM best for large/random/alphabet-rich texts
            if (m >= 20) return "RabinKarp";         // RK wins some long-pattern cases
            
            return "BoyerMoore";                     // default BM in large texts
        }

        // Medium-length patterns with large-ish alphabet → BM tends to win
        if (m >= 10 && alphabet > 12) return "BoyerMoore";

        // Moderate pattern repetition but not extreme → KMP is solid
        if (lps > 0 && m < 20) return "KMP";

        // Default fallback: Naive surprisingly wins most common small-medium tests
        return "Naive";
    }
        
    
    @Override
    public String getStrategyDescription() {
    return "This strategy chooses algorithms by examining the length of the text and pattern, "
         + "how repetitive the pattern is, and how many different characters appear in the text. "
         + "Based on these characteristics, it selects the algorithm that tends to perform the fastest "
         + "in situations similar to the ones observed during testing.";
}


private int estimateAlphabet(String text) {
        boolean[] seen = new boolean[256];
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            int c = text.charAt(i) & 0xFF;
            if (!seen[c]) {
                seen[c] = true;
                count++;
            }
        }
        return count;
    }

    private int longestPrefixSuffix(String pattern) {
        int m = pattern.length();
        if (m == 0) return 0;

        int[] lps = new int[m];
        int len = 0;
        int i = 1;

        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                lps[i] = ++len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps[m - 1];
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
