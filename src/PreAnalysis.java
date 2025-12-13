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

/*
 * Strategy: "Intelligent Routing" - Algorithm Selection Logic
 * This strategy analyzes the characteristics of the text and pattern to select
 * the most suitable algorithm through a hierarchical set of rules.
 * 
 * 1. Rule 1 (Micro Patterns):
 * - If the pattern length is 4 or less, 'GoCrazy' is chosen.
 * - Reason: For such small patterns, the preprocessing costs of complex
 * algorithms like KMP or Boyer-Moore might be higher than their
 * performance gains. 'GoCrazy' has a lower setup cost.
 * 
 * 2. Rule 2 (Specialized Content Scan):
 * - If the pattern has a highly repetitive structure (e.g., "ababab"),
 * 'KMP' is selected, as this is its most efficient scenario.
 * - If the pattern contains characters outside the standard ASCII table
 * (e.g., Unicode), 'RabinKarp' is selected. This is because the hash-based
 * algorithm is less affected by a large alphabet size.
 * 
 * 3. Rule 3 (Fine-Tuning for General-Purpose Cases):
 * - If the pattern is long ('>20') and has a high number of distinct
 * characters ('>15'), 'BoyerMoore' is chosen. This is the ideal
 * situation for Boyer-Moore to make large jumps.
 * - If the pattern is very long relative to the text ('> n/2'), 'GoCrazy' is
 * chosen again. In this case, there are few alignment windows, so the
 * complex setup cost of Boyer-Moore is not worthwhile.
 *
 * 4. Rule 4 (Default Choice):
 * - If none of the specific or semi-specific cases above are met, the most
 * basic and guaranteed-to-work algorithm, 'Naive', is chosen as the
 * safest default option.
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
 * Default implementation that students should modify
 * This is where students write their pre-analysis logic
 */
class StudentPreAnalysis extends PreAnalysis {

    // Constant for sampling size to keep analysis fast.
    private static final int SAMPLE_SIZE = 64;

    /**
     * Our Strategy: "Smart Routing"
     * This version introduces more nuanced heuristics for "normal" patterns by
     * analyzing
     * alphabet diversity and pattern-to-text ratio. The goal is to make a smarter
     * choice between Boyer-Moore and other general-purpose algorithms, while still
     * keeping all analysis costs fixed and minimal.
     */
    @Override
    public String chooseAlgorithm(String text, String pattern) {
        int m = pattern.length();
        int n = text.length();

        // --- RULE 0: Trivial Cases (O(1) checks) ---
        if (m == 0 || n == 0)
            return "Naive";
        if (m > n)
            return "RabinKarp";

        // --- RULE 1: Micro Patterns (O(1) check) ---
        if (m <= 4)
            return "GoCrazy";

        // --- RULE 2: Specialized Content Heuristics (O(k) checks) ---
        // These are strong signals for specific algorithms.
        if (isHighlyRepetitive(pattern))
            return "KMP";
        if (containsNonAscii(pattern))
            return "RabinKarp";

        // --- RULE 3: General-Purpose Heuristics (O(k) + O(1) checks) ---
        // For standard ASCII patterns that aren't highly repetitive.

        // Heuristic 3a: Large, Diverse Alphabet -> BoyerMoore
        // Boyer-Moore excels with large alphabets and longer patterns due to its
        // ability
        // to make large shifts on mismatches. We check for a reasonably long pattern
        // and a high number of distinct characters in our sample.
        int distinctChars = countDistinctChars(pattern);
        if (m > 20 && distinctChars > 15) {
            return "BoyerMoore";
        }

        // Heuristic 3b: High Pattern/Text Ratio -> GoCrazy
        // If the pattern is very long relative to the text, the number of possible
        // alignments is small. The setup cost of Boyer-Moore may not be worth it.
        // GoCrazy has lower setup cost and is a good choice here.
        if (n > 0 && m > n / 2) { // Check n > 0 to avoid division by zero
            return "GoCrazy";
        }

        // --- RULE 4: Default to the All-Rounder ---
        // If no other specific heuristic matches, Naive is the most reliable default.
        return "Naive";
    }

    private int countDistinctChars(String pattern) {
        int len = Math.min(pattern.length(), SAMPLE_SIZE);
        java.util.HashSet<Character> distinct = new java.util.HashSet<>();
        for (int i = 0; i < len; i++) {
            distinct.add(pattern.charAt(i));
        }
        return distinct.size();
    }

    private boolean containsNonAscii(String pattern) {
        int len = Math.min(pattern.length(), SAMPLE_SIZE);
        for (int i = 0; i < len; i++) {
            if (pattern.charAt(i) > 127) {
                return true;
            }
        }
        return false;
    }

    private boolean isHighlyRepetitive(String pattern) {
        int len = Math.min(pattern.length(), SAMPLE_SIZE);
        if (len <= 2)
            return false;

        char first = pattern.charAt(0);
        char second = first;
        for (int i = 1; i < len; i++) {
            if (pattern.charAt(i) != first) {
                second = pattern.charAt(i);
                break;
            }
        }
        if (second == first)
            return true;

        for (int i = 1; i < len; i++) {
            char c = pattern.charAt(i);
            if (c != first && c != second) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getStrategyDescription() {
        return "Our Strategy: 'Smart Routing'. Adds alphabet diversity (for BoyerMoore) and pattern/text ratio checks to make smarter choices for general-purpose patterns, all while maintaining minimal, fixed-cost analysis.";
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
