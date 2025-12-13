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

        // Strategy 1: Repeating Prefixes -> KMP
        // Check this FIRST. Even for short patterns, if they are repetitive (e.g. "AAAA")
        // and the text is long, Naive degrades to O(n*m). KMP stays O(n).
        if (hasRepeatingPrefix(pattern)) {
            return "KMP";
        }

        // Strategy 2: Short to Medium Patterns -> Naive
        // Naive is surprisingly fast in Java due to JIT optimizations and low overhead.
        // For patterns up to 20 characters (that aren't repetitive), the overhead of 
        // building tables (HashMap/Arrays) in BM/KMP/GoCrazy often outweighs the search speedup.
        if (m <= 20) {
            return "Naive";
        }

        // Strategy 3: Long Patterns -> GoCrazy (Horspool)
        // For long patterns, the skip mechanism of Horspool becomes very effective.
        // It's generally lighter than standard Boyer-Moore.
        if (m > 20) {
            return "GoCrazy";
        }

        // Default Fallback
        return "Naive";
    }

    private boolean hasRepeatingPrefix(String pattern) {
        if (pattern.length() < 2) return false;
        // Check if the first few characters are the same
        char first = pattern.charAt(0);
        int limit = Math.min(pattern.length(), 5);
        int count = 0;
        for (int i = 0; i < limit; i++) {
            if (pattern.charAt(i) == first) count++;
        }
        return count >= 3;
    }
    
    @Override
    public String getStrategyDescription() {
        return "Optimized Strategy: Naive for patterns <= 20 chars (low overhead), KMP for repeating prefixes, GoCrazy (Horspool) for long patterns.";
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
