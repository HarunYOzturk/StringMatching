/**
 * PreAnalysis interface for students to implement their algorithm selection logic
 * 
 * Students should analyze the characteristics of the text and pattern to determine
 * which algorithm would be most efficient for the given input.
 * 
 * The system will automatically use this analysis if the chooseAlgorithm method
 * returns a non-null value.
 */

// Name : Ömer Faruk Başaran
// ID : 21050111041
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
        int textLen = text.length();
        int patternLen = pattern.length();

        // Empty pattern - use naive
        if (patternLen == 0) {
            return "Naive";
        }

        // Pattern longer than text - no match possible
        if (patternLen > textLen) {
            return "Naive";
        }

        // Very short patterns (1-2 chars) - naive is fastest
        if (patternLen <= 2) {
            return "Naive";
        }

        // Check pattern diversity (how many unique characters)
        int uniqueChars = countUniqueChars(pattern);
        double diversity = (double) uniqueChars / patternLen;

        // Low diversity (many repeating chars) - KMP handles this well
        if (diversity < 0.5) {
            return "KMP";
        }

        // Long patterns with high diversity - Boyer-Moore excels
        if (patternLen >= 7 && diversity > 0.7) {
            return "BoyerMoore";
        }

        // Medium patterns in large text - RabinKarp is good
        if (patternLen >= 4 && patternLen <= 8 && textLen > 500) {
            return "RabinKarp";
        }

        // Short to medium patterns - naive is simple and fast
        if (patternLen <= 5) {
            return "Naive";
        }

        // Default to Boyer-Moore
        return "BoyerMoore";
    }

    @Override
    public String getStrategyDescription() {
        return "Diversity-based selection: Analyzes unique character ratio in pattern. " +
                "Naive for short patterns, KMP for low diversity (repeating chars), " +
                "Boyer-Moore for long diverse patterns, RabinKarp for medium patterns in large text.";
    }

    /**
     * Count the number of unique characters in the pattern.
     * Used to determine pattern diversity.
     */
    private int countUniqueChars(String pattern) {
        boolean[] seen = new boolean[256]; // ASCII characters
        int count = 0;

        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (!seen[c]) {
                seen[c] = true;
                count++;
            }
        }

        return count;
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
