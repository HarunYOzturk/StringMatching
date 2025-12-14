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


class StudentPreAnalysis extends PreAnalysis {

    // Threshold constants
    private static final int MIN_TEXT_NAIVE = 512;
    private static final int LARGE_TEXT_THRESHOLD = 10000;
    private static final int MIN_PATTERN_BM = 15;
    private static final int LONG_PATTERN = 50;
    private static final int VERY_LONG_PATTERN = 100;

    @Override
    public String chooseAlgorithm(String text, String pattern) {
        if (text == null || pattern == null) return "Naive";

        int n = text.length();
        int m = pattern.length();

        // 1. If text is very short, just use Naive.
        if (n < MIN_TEXT_NAIVE) {
            return "Naive";
        }

        // 2. Empty or single char patterns -> Naive.
        if (m <= 1) {
            return "Naive";
        }

        // 3. For short patterns (2-5 chars), Naive is fast enough.
        if (m <= 5) {
            return "Naive";
        }

        // 4. Get pattern details (unique chars, repetition, etc.)
        PatternAnalysis analysis = analyzePattern(pattern);

        // 5. FOR VERY LARGE TEXTS
        if (n > LARGE_TEXT_THRESHOLD) {
            // If pattern is long and has many unique chars, BM is best.
            if (m >= MIN_PATTERN_BM && analysis.uniqueChars > 10) {
                return "BoyerMoore";
            }
            // If pattern repeats itself a lot (e.g. AAAAA), use KMP.
            if (analysis.isHighlyRepetitive) {
                return "KMP";
            }
            // Special algorithm for extremely long patterns.
            if (m > VERY_LONG_PATTERN) {
                return "GoCrazy";
            }
        }

        // 6. LONG PATTERNS (50-100 chars)
        if (m >= LONG_PATTERN && m < VERY_LONG_PATTERN) {
            if (analysis.isHighlyRepetitive) {
                return "KMP";
            }
            // More unique characters mean better skips for BM.
            if (analysis.uniqueChars > 15) {
                return "BoyerMoore";
            }
            // Use RabinKarp for standard long searches.
            if (n > 5000) {
                return "RabinKarp";
            }
        }

        // 7. EXTRA LONG PATTERNS (>100 chars)
        if (m >= VERY_LONG_PATTERN) {
            if (analysis.isHighlyRepetitive) {
                return "KMP";
            }
            return "GoCrazy";
        }

        // 8. MEDIUM PATTERNS (The most common case)
        if (m >= 6 && m < LONG_PATTERN) {
            if (analysis.repetitionRatio > 0.6) {
                return "KMP";
            }
            if (analysis.uniqueChars > 10 && n > 2000 && m >= MIN_PATTERN_BM) {
                return "BoyerMoore";
            }
            // Fallback to RabinKarp for medium inputs.
            if (n > 1000 && n <= 5000 && m > 8) {
                return "RabinKarp";
            }
        }

        // 9. Default to Naive for everything else.
        return "Naive";
    }

    /**
     * Helper to check unique chars and repetition.
     */
    private PatternAnalysis analyzePattern(String pattern) {
        PatternAnalysis result = new PatternAnalysis();
        int m = pattern.length();

        // Count different characters.
        boolean[] seen = new boolean[256];
        int uniqueCount = 0;

        for (int i = 0; i < m; i++) {
            char c = pattern.charAt(i);
            if (c < 256 && !seen[c]) {
                seen[c] = true;
                uniqueCount++;
            }
        }
        result.uniqueChars = uniqueCount;

        // Check if the pattern repeats itself (checking first 30 chars).
        int sampleSize = Math.min(m, 30);
        char firstChar = pattern.charAt(0);
        int matchCount = 0;

        for (int i = 0; i < sampleSize; i++) {
            if (pattern.charAt(i) == firstChar) {
                matchCount++;
            }
        }

        result.repetitionRatio = (double) matchCount / sampleSize;
        // If more than 65% matches, it is repetitive.
        result.isHighlyRepetitive = result.repetitionRatio > 0.65;

        return result;
    }

    private static class PatternAnalysis {
        int uniqueChars = 0;
        double repetitionRatio = 0.0;
        boolean isHighlyRepetitive = false;
    }

    @Override
    public String getStrategyDescription() {
        return "Simple Strategy: Naive for small inputs. Boyer-Moore for large texts with distinct patterns. KMP for repetitive patterns.";
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
