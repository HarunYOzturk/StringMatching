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

        // If pattern is empty or longer than text, Naive handles it instantly.
        if (m == 0 || m > n) return "Naive";

        // For patterns with length <= 5, the overhead of building heuristic tables increases search time. Naive is faster.
        if (m <= 5) return "Naive";

        // If the pattern is highly repetitive, we use KMP here to guarantee O(N) linear time, even for short texts.
        if (hasHighRepetition(pattern)) {
            return "KMP";
        }

        // If the text is short (< 128 chars) and the pattern is safe (not repetitive),
        // simple loops (Naive) are extremely fast due to Java JIT optimizations and CPU cache locality.
        if (n < 128) return "Naive";

        // If the text is long (> 512) and has a small alphabet KMP is more stable and efficient here.
        // We used a sample size of 64 to keep this check O(1).
        if (n > 512 && isSmallAlphabet(text, 64)) {
            return "KMP";
        }

        // For long patterns (> 30 chars), Boyer-Moore's 'Good Suffix' heuristic allows for massive jumps.
        if (m > 30) {
            return "BoyerMoore";
        }

        // For standard English text and medium-length patterns GoCrazy Algorithm is generally the fastest due to its aggressive shift strategy.
        return "GoCrazy";
    }

    private boolean hasHighRepetition(String pattern) {
        int m = pattern.length();
        // Safety check for very short patterns
        if (m < 4) return false;

        // Analyze only the first 8 characters for speed
        int checkLen = Math.min(m, 8);
        char first = pattern.charAt(0);
        int sameCount = 0;

        // Count how many characters in the prefix match the first character
        for (int i = 0; i < checkLen; i++) {
            if (pattern.charAt(i) == first) sameCount++;
        }

        // If > 60% of the prefix consists of the same character, consider it repetitive.
        return sameCount >= (checkLen * 0.6);
    }

    private boolean isSmallAlphabet(String text, int sampleSize) {
        int limit = Math.min(text.length(), sampleSize);
        long mask = 0;
        int distinctCount = 0;

        for (int i = 0; i < limit; i++) {
            // Simple hash to map chars to 0-63 range
            int bitPos = text.charAt(i) % 64;
            long bit = 1L << bitPos;

            // If this bit hasn't been set yet, it's a new unique character
            if ((mask & bit) == 0) {
                mask |= bit;
                distinctCount++;
                // If we exceed 6 distinct chars, it's not a small alphabet
                if (distinctCount > 6) return false;
            }
        }

        // DNA has 4 chars. We allow a small noise margin up to 6.
        return distinctCount <= 6;
    }

    @Override
    public String getStrategyDescription() {
        return "Engineered Hybrid Strategy:\n" +
                "- Fail-Fast: Naive for ultra-short patterns (<=5 chars)\n" +
                "- Safety-First: KMP for repetitive patterns (avoids O(NM))\n" +
                "- Low-Overhead: Naive for short texts (<128 chars)\n" +
                "- Stability: KMP for small alphabets (DNA/Binary)\n" +
                "- Performance: Boyer-Moore for long patterns, GoCrazy for general case";
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
