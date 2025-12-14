/**
 * PreAnalysis interface for students to implement their algorithm selection logic
 * Students should analyze the characteristics of the text and pattern to determine
 * which algorithm would be most efficient for the given input.
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

        // 1.RABIN-KARP for unicode characters
        if(patternLen > 0 && pattern.charAt(0) > 123){
            return "RabinKarp";
        }

        // 2.Don't think in very short texts, just attack Naive directly.
        if (textLen < 30) {
            return "Naive";

        }

        // 3. BOYER-MOORE Zone (Big Fish)
        // In long text or medium-sized + long patterns, BM characters skip and fly away.
        if (textLen > 250 || (textLen > 100 && patternLen > 20)) {
            return "BoyerMoore";
        }

        // 4. "EMERGENCY" BRAKE FOR NAIVE (KMP Region) - Repeating Patterns
        // KMP works efficiently in these areas.
        if (patternLen >= 4){
            if(pattern.charAt(0) == pattern.charAt(1) ||
                    pattern.charAt(0) == pattern.charAt(2) ||
                    pattern.charAt(0) == pattern.charAt(3)
            ) {
                return "KMP";
            }
        }

        // 5. RABIN-CARP REGION (Mid-Segment - Future Investment)
        // This range represents 90% of real-world "Word Search" scenarios.
        if (textLen > 64 && patternLen > 1 && patternLen < 30) {
            return "RabinKarp";
        }

        // 6. DEFAULT (NAIVE)
        // Short texts, spaces, DNA, Unicode...
        // Naive handles all of these in the 1-2µs range
        // No need to overthink the analysis; let the JVM loops handle it.
        return "Naive";

    }
    @Override
    public String getStrategyDescription() {
        return "Hybrid Strategy: Uses JVM intrinsics (GoCrazy) for general speed, " +
                "switches to KMP for repetitive patterns, and Boyer-Moore/Rabin-Karp for large scale texts.";
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
