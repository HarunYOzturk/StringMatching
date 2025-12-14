/*
Muhammet Enes Varol - 22050111041
Mehmet Emin Kaya - 22050111034
*/
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
        if (pattern == null || pattern.isEmpty()) {
            return "Naive";
        }

        int textLen = text.length();
        int patternLen = pattern.length();
        
        /* 
        Strategy 1: Short Patterns (m <= 4)
        For very short patterns, the overhead of building tables (for BM or KMP) is costlier than just searching directly. Naive is fastest here.
        */
        if (patternLen <= 4) {
            return "Naive"; 
        }

        /*
        Strategy 2: Periodic Patterns
        If pattern repeats itself (e.g., "ABABAB"), KMP is mathematically guaranteed to be linear O(n), avoiding Naive's worst case.
         */
        if (isPatternPeriodic(pattern)) {
            return "KMP";
        }
        
        /*
        Strategy 3: Long Patterns (m > 25)
        Boyer-Moore shines with long patterns because it can make larger jumps (skips) when a mismatch occurs.
        */
        if (patternLen > 25) { 
            return "BoyerMoore";
        }
        
        /*
        Strategy 4: Very Long Texts
        Rabin-Karp's rolling hash is generally efficient for scanning large texts.
        */
        if (textLen > 10000) {
             return "RabinKarp";
        }

        /*
        Strategy 5: Default / Hybrid
        For everything else, use our custom adaptive algorithm.
        */
        return "GoCrazy"; 
    }
    
    @Override
    public String getStrategyDescription() {
        return "Hybrid Decision Tree: Naive for very short patterns (m<=4), KMP for periodic patterns, BoyerMoore for long non-periodic patterns (m>25), RabinKarp for long texts, and GoCrazy as the smart default for all remaining scenarios.";
    }
    
    // --- Helper: Periodicity Check ---
    // Uses the KMP failure function (LPS) to check if pattern consists of repeating units
    private boolean isPatternPeriodic(String p) {
        int m = p.length();
        if (m <= 1) return false;

        int[] lps = computeLPS(p);
        int longestBorder = lps[m - 1]; 

        // A pattern is periodic if the longest border length can divide the total length
        int period = m - longestBorder;
        
        return longestBorder > 0 && (m % period == 0);
    }
    
    // --- Helper: Compute LPS Array ---
    // Standard KMP preprocessing table (Longest Prefix Suffix)
    private int[] computeLPS(String pat) {
        int m = pat.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;

        while (i < m) {
            if (pat.charAt(i) == pat.charAt(len)) {
                lps[i++] = ++len;
            } else {
                if (len != 0)
                    len = lps[len - 1];
                else
                    lps[i++] = 0;
            }
        }
        return lps;
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
