//Reyyan Naz Doğan - 22050111012
//Murat Emir Öncül - 22050111057

import java.util.HashSet;

/**
 * PreAnalysis interface for students to implement their algorithm selection
 * logic
 * 
 * Students should analyze the characteristics of the text and pattern to
 * determine
 * which algorithm would be most efficient for the given input.
 * 
 * The system will automatically use this analysis if the chooseAlgorithm method
 * returns a non-null value.
 */
public abstract class PreAnalysis {

    /**
     * Analyze the text and pattern to choose the best algorithm
     * 
     * @param text    The text to search in
     * @param pattern The pattern to search for
     * @return The name of the algorithm to use (e.g., "Naive", "KMP", "RabinKarp",
     *         "BoyerMoore", "GoCrazy")
     *         Return null if you want to skip pre-analysis and run all algorithms
     * 
     *         Tips for students:
     *         - Consider the length of the text and pattern
     *         - Consider the characteristics of the pattern (repeating characters,
     *         etc.)
     *         - Consider the alphabet size
     *         - Think about which algorithm performs best in different scenarios
     */
    public abstract String chooseAlgorithm(String text, String pattern);

    /**
     * Get a description of your analysis strategy
     * This will be displayed in the output
     */
    public abstract String getStrategyDescription();
}

/**
 * Student implementation with pre-analysis logic
 * Algorithm selection based on text/pattern characteristics
 */
class StudentPreAnalysis extends PreAnalysis {

    @Override
    public String chooseAlgorithm(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();

        if (n <= m + 5 || n < 20) {
            return "Naive";
        }
        // Check first few chars of pattern for alphabet estimate
        boolean isSmallAlphabet = isLikelySmallAlphabet(pattern);
        boolean hasRepetition = hasSimpleRepetition(pattern);

        // Small alphabet (DNA, binary) - detected by pattern chars
        if (isSmallAlphabet) {
            if (hasRepetition) {
                return "RabinKarp";
            }
            return "BoyerMoore";
        }

        // Short pattern
        if (m <= 3) {
            if (n > 400) {
                return "RabinKarp";
            }
            return "Naive";
        }

        // Medium-short text
        if (n < 150) {
            return "Naive";
        }

        // Longer text
        if (n > 250) {
            if (m < 25) {
                return "RabinKarp";
            }
            return "BoyerMoore";
        }

        // Pattern with repetition
        if (hasRepetition) {
            return "KMP";
        }

        return "Naive";
    }

    @Override
    public String getStrategyDescription() {
        return "Algorithm Selection Strategy:\n" +
                "===============================\n" +
                "1. NAIVE: Default for short texts (n<150) or short patterns (m<=3)\n" +
                "   - Minimal overhead, no preprocessing required\n" +
                "   - Best when text is too short to benefit from advanced algorithms\n\n" +
                "2. RABIN-KARP: Small alphabet (<=4 chars) WITH repetition\n" +
                "   - Rolling hash handles repetitive patterns efficiently\n" +
                "   - Good for: 'AAAA...B', 'ABAB...C' type patterns\n" +
                "   - Also used for very long text (n>400) with short pattern\n\n" +
                "3. BOYER-MOORE: Small alphabet WITHOUT repetition, or long patterns\n" +
                "   - Good Suffix Rule exploits small alphabet structure\n" +
                "   - Best for: DNA sequences (ATCG), binary data\n" +
                "   - Also used for long patterns (m>=25) on long text\n\n" +
                "4. KMP: Patterns with prefix-suffix repetition on larger alphabets\n" +
                "   - LPS (Longest Proper Prefix Suffix) table exploits pattern structure\n" +
                "   - Guaranteed O(n+m) time complexity\n\n" +
                "Key Thresholds:\n" +
                "  - n < 150: Naive (preprocessing overhead not worth it)\n" +
                "  - Alphabet <= 4 chars: RabinKarp or BoyerMoore based on repetition\n" +
                "  - n > 250, m < 25: RabinKarp (rolling hash advantage)\n" +
                "  - m >= 25: BoyerMoore (skip advantage for long patterns)";
    }

    /**
     * Quick check if pattern suggests small alphabet (DNA, binary, etc.)
     * Only looks at pattern, not text - O(m) where m is pattern length
     */
    private boolean isLikelySmallAlphabet(String pattern) {
        if (pattern.length() < 3)
            return false;

        // Count unique chars in first 10 chars of pattern
        int uniqueCount = 0;
        boolean[] seen = new boolean[128]; // ASCII only for speed
        int checkLen = Math.min(pattern.length(), 10);

        for (int i = 0; i < checkLen; i++) {
            char c = pattern.charAt(i);
            if (c < 128 && !seen[c]) {
                seen[c] = true;
                uniqueCount++;
            }
        }

        return uniqueCount <= 4;
    }

    private boolean hasSimpleRepetition(String pattern) {
        if (pattern.length() < 4)
            return false;

        char first = pattern.charAt(0);
        int sameCount = 0;
        int checkLen = Math.min(pattern.length(), 8);

        // Check if first char dominates (AAA... pattern)
        for (int i = 0; i < checkLen; i++) {
            if (pattern.charAt(i) == first)
                sameCount++;
        }
        if (sameCount >= checkLen - 1)
            return true;

        // Check period-2 (ABAB... pattern)
        if (pattern.length() >= 4) {
            if (pattern.charAt(0) == pattern.charAt(2) &&
                    pattern.charAt(1) == pattern.charAt(3)) {
                return true;
            }
        }
        return false;
    }

    // to find unique characters in text and pattern
    public int getAlphabetSize(String text, String pattern) {
        HashSet<Character> uniqueChars = new HashSet<>();
        for (char c : text.toCharArray())
            uniqueChars.add(c);
        for (char c : pattern.toCharArray())
            uniqueChars.add(c);
        return uniqueChars.size();
    }

    // to find significant repetition in pattern
    private boolean hasSignificantRepetition(String pattern) {
        // it compares beginning and end of pattern
        int len = pattern.length();
        for (int i = 1; i < len; i++) {
            if (pattern.substring(0, i).equals(
                    pattern.substring(len - i, len))) {
                return true;
            }
        }
        return false;
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
        if (pattern.length() < 2)
            return false;

        // Check if first character repeats
        char first = pattern.charAt(0);
        int count = 0;
        for (int i = 0; i < Math.min(pattern.length(), 5); i++) {
            if (pattern.charAt(i) == first)
                count++;
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
