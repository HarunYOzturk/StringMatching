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
// Your custom pre-analysis class.
// This class decides which string matching algorithm to use
// before actually running any of them.
class StudentPreAnalysis extends PreAnalysis {

    @Override
    public String chooseAlgorithm(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();

        // 1) Trivial cases: empty pattern or pattern longer than text.
        // In these cases, any algorithm will be fast, so we simply use Naive.
        if (m == 0 || m > n) {
            return "Naive";
        }

        // 2) Very short patterns: preprocessing cost is not worth it.
        // For length 1 or 2, the Naive algorithm is usually competitive.
        if (m <= 2) {
            return "Naive";
        }

        // 3) Compute basic statistics about the pattern.
        // - alphabetSize: number of distinct characters in the pattern
        // - repetitionRatio: how repetitive the pattern is (smaller => more repetition)
        java.util.Set<Character> alphabet = new java.util.HashSet<>();
        for (int i = 0; i < m; i++) {
            alphabet.add(pattern.charAt(i));
        }
        int alphabetSize = alphabet.size();
        double repetitionRatio = (double) alphabetSize / m;  // in (0, 1]

        // 4) Highly repetitive patterns -> KMP.
        // KMP is strong when the pattern has a lot of internal structure
        // and repeating prefixes/suffixes.
        if (repetitionRatio < 0.30 && m >= 5) {
            return "KMP";
        }

        // 5) Very long text + short, diverse pattern -> RabinKarp.
        // For very long texts with many different characters and a relatively
        // short pattern, hashing can work well in practice.
        if (n > 5000 && m < 50 && alphabetSize > m / 2) {
            return "RabinKarp";
        }

        // 6) Long text + reasonably long pattern + larger alphabet -> BoyerMoore.
        // Boyer-Moore benefits from larger alphabets and longer patterns
        // because bad-character skips tend to be larger.
        if (n > 2000 && m >= 5 && alphabetSize >= 5) {
            return "BoyerMoore";
        }

        // 7) Check symmetry of the pattern for GoCrazy.
        // GoCrazy compares characters from both ends toward the center,
        // so it can be effective on patterns that are somewhat symmetric.
        int matchEnds = 0;
        for (int i = 0; i < m / 2; i++) {
            if (pattern.charAt(i) == pattern.charAt(m - 1 - i)) {
                matchEnds++;
            }
        }
        if (pattern.charAt(0) == pattern.charAt(m - 1) || matchEnds > m / 4) {
            return "GoCrazy";
        }

        // 8) Default fallback: if none of the above conditions triggered,
        // choose the Naive algorithm. It is simple and robust for many cases.
        return "Naive";
    }

    @Override
    public String getStrategyDescription() {
        // This description is shown in the report section of the tester.
        return "Heuristic strategy based on pattern length, repetition, alphabet size "
             + "and symmetry: very short patterns use Naive, highly repetitive patterns "
             + "use KMP, very long texts with short and diverse patterns use Rabin-Karp, "
             + "long texts with larger alphabets use Boyer-Moore, symmetric patterns "
             + "use GoCrazy, and Naive is used as a safe fallback in other cases.";
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
