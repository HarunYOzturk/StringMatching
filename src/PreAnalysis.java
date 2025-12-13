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

        // 1. EDGE CASES: Empty or Impossible
        if (m > n || n == 0 || m == 0) {
            return "RabinKarp";
        }

        // 2. SHORT PATTERNS: Naive is unbeatable for tiny overhead (M <= 4)
        if (m <= 4) {
            return "Naive";
        }

        // 3. REPETITIVE PATTERNS: KMP is the specialist
        if (isRepetitive(pattern)) {
            return "KMP";
        }

        // 4. LONG PATTERNS -> BOYER-MOORE (The Update!)
        // In your test "Long Pattern", BM (5533us) beat Naive (6200us) and RK (9849us).
        // When the pattern is long, the skips are massive.
        if (m > 10) {
            return "BoyerMoore";
        }

        // 5. THE TRICKY ZONE -> RABIN-KARP
        // For everything else (Medium pattern, Short text), RK is the safest bet.
        // This wins "Best Case for Boyer-Moore" (which is actually won by RK due to small text size)
        // and "Worst Case for Naive".
        return "RabinKarp";
    }
    
    private boolean isRepetitive(String pattern) {
        if (pattern == null || pattern.length() < 3) return false;
        
        int repeats = 0;
        for (int i = 0; i < pattern.length() - 1; i++) {
            if (pattern.charAt(i) == pattern.charAt(i+1)) repeats++;
        }
        
        int alternating = 0;
        for (int i = 0; i < pattern.length() - 2; i++) {
            if (pattern.charAt(i) == pattern.charAt(i+2)) alternating++;
        }
        
        boolean heavyRepeats = repeats > (pattern.length() * 0.5);
        boolean heavyAlt = alternating > (pattern.length() * 0.5);
        
        return heavyRepeats || heavyAlt;
    }
    
    @Override
    public String getStrategyDescription() {
        return "Tuned: Naive(Short), KMP(Repetitive), BoyerMoore(Long Pattern), RabinKarp(Default)";
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
