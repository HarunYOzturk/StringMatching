
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
 * Student implementation of pre-analysis logic
 * Theory-driven selection optimized for our 5 algorithms:
 * Naive, KMP, RabinKarp, BoyerMoore, GoCrazy (Adaptive Horspool++)
 */
class StudentPreAnalysis extends PreAnalysis {
    
    // Pattern length thresholds
    private static final int SINGLE_CHAR = 1;
    private static final int VERY_SHORT = 3;
    private static final int SHORT = 8;
    private static final int MEDIUM = 20;
    private static final int LONG = 50;
    
    // Text length thresholds
    private static final int TINY_TEXT = 100;
    private static final int SMALL_TEXT = 1000;
    private static final int MEDIUM_TEXT = 10000;
    
    // Pattern characteristic thresholds
    private static final double HIGH_REPETITION = 0.4;
    private static final int MIN_FOR_REPETITION_CHECK = 4;
    
    @Override
    public String chooseAlgorithm(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
        
        // Edge cases
        if (m == 0 || m > n) {
            return "Naive";
        }

        
        // RULE 1: Very short patterns (2-3 chars) → Context matters

        if (m <= VERY_SHORT) {
            // For tiny texts, Naive has no overhead
            if (m == 1 || n <= TINY_TEXT) {
                return "Naive";
            }
            // GoCrazy's Horspool skip table is lightweight and effective
            // Better than BoyerMoore's heavier preprocessing for short patterns
            return "GoCrazy";
        }
        
        
        // RULE 2: Detect highly repetitive patterns → KMP WINS
       
        // KMP's LPS table excels when pattern has self-similarity
        // Examples: "AAAA", "ABABAB", "ABCABCABC"
        if (m >= MIN_FOR_REPETITION_CHECK && hasHighRepetition(pattern)) {
            return "KMP";
        }
        
        
        // RULE 3: Short-medium patterns (4-8 chars) → Horspool territory
        
        if (m <= SHORT) {
            // Very small texts still favor Naive (no preprocessing)
            if (n < TINY_TEXT) {
                return "Naive";
            }
            // GoCrazy's Horspool with last-char caching is optimal here
            // Lighter than BoyerMoore, faster skips than Naive/KMP/RabinKarp
            return "GoCrazy";
        }
        
        
        // RULE 4: Medium patterns (9-20 chars) → BoyerMoore vs GoCrazy
       
        if (m <= MEDIUM) {
            // For large texts, BoyerMoore's good suffix rule adds value
            // Its preprocessing overhead is justified by skip efficiency
            if (n >= SMALL_TEXT) {
                return "BoyerMoore";
            }
            // For smaller texts, GoCrazy is still competitive
            // Lighter preprocessing, decent skips
            return "GoCrazy";
        }
        
        
        // RULE 5: Long patterns (21-50 chars) → RabinKarp starts winning
      
        if (m <= LONG) {
            // For very large texts, RabinKarp's O(n+m) rolling hash shines
            // Constant-time hash comparison regardless of pattern length
            if (n >= MEDIUM_TEXT) {
                return "RabinKarp";
            }
            // For medium texts, BoyerMoore can still skip effectively
            if (n >= SMALL_TEXT) {
                return "BoyerMoore";
            }
            // For smaller texts, simpler algorithms suffice
            return "GoCrazy";
        }
        
        
        // RULE 6: Very long patterns (>50 chars) → RabinKarp DOMINATES
        
        // RabinKarp's O(m) preprocessing is manageable even for long patterns
        // BoyerMoore's preprocessing cost grows significantly with pattern length
        // Rolling hash comparison is O(1) regardless of pattern length
        return "RabinKarp";
    }
    
    /**
     * Detects patterns with high character repetition or repeating substrings
     * These patterns benefit greatly from KMP's LPS (Longest Proper Prefix 
     * which is also Suffix) table
     * 
     * Detection methods:
     * 1. Character frequency: If any char appears in 40%+ of pattern
     * 2. Substring repetition: If pattern contains repeating substrings
     * 
     * Time: O(m²) worst case for substring check, but early exits common
     * Space: O(256) for character frequency array
     */
    private boolean hasHighRepetition(String pattern) {
        int m = pattern.length();
        
        // Method 1: Character frequency analysis
        int[] freq = new int[256];
        int maxFreq = 0;
        
        for (int i = 0; i < m; i++) {
            char c = pattern.charAt(i);
            if (c < 256) {
                freq[c]++;
                maxFreq = Math.max(maxFreq, freq[c]);
            }
        }
        
        // If any character appears in 40%+ of pattern → highly repetitive
        double ratio = (double) maxFreq / m;
        if (ratio >= HIGH_REPETITION) {
            return true;
        }
        
        // Method 2: Repeating substring detection
        // Check if pattern is composed of repeating substrings
        // E.g., "ABCABC" = "ABC" repeated, "XYXYXY" = "XY" repeated
        if (m >= 6) {
            // Try different substring lengths (2 to m/2)
            for (int subLen = 2; subLen <= m / 2; subLen++) {
                // Only check if pattern length is divisible by substring length
                // or nearly divisible (for partial repeats at the end)
                String substring = pattern.substring(0, subLen);
                boolean isRepeating = true;
                
                // Check if this substring repeats throughout pattern
                for (int i = subLen; i < m; i += subLen) {
                    int endIdx = Math.min(i + subLen, m);
                    String currentSection = pattern.substring(i, endIdx);
                    String expectedSection = substring.substring(0, endIdx - i);
                    
                    if (!currentSection.equals(expectedSection)) {
                        isRepeating = false;
                        break;
                    }
                }
                
                if (isRepeating) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    @Override
    public String getStrategyDescription() {
        return "Theory-driven algorithm selection optimized for our specific implementations:\n" +
               "\n" +
               "GoCrazy (Adaptive Horspool++):\n" +
               "  - Single char: Ultra-fast linear scan (O(n))\n" +
               "  - Short patterns (2-8): Lightweight Horspool skip table + last-char caching\n" +
               "  - Best for: m ≤ 8, especially when n < 1000\n" +
               "\n" +
               "KMP (Knuth-Morris-Pratt):\n" +
               "  - O(n+m) with LPS table\n" +
               "  - Best for: Repetitive patterns (40%+ char repetition or repeating substrings)\n" +
               "  - Examples: 'AAAA', 'ABABAB', 'ABCABCABC'\n" +
               "\n" +
               "BoyerMoore:\n" +
               "  - O(n/m) best case with bad char + good suffix rules\n" +
               "  - Best for: Medium patterns (9-20) with large texts (n ≥ 1000)\n" +
               "  - Heavier preprocessing justified by large skip distances\n" +
               "\n" +
               "RabinKarp:\n" +
               "  - O(n+m) average with rolling hash\n" +
               "  - Best for: Long patterns (m > 20) with very large texts (n ≥ 10000)\n" +
               "  - O(1) hash comparison regardless of pattern length\n" +
               "\n" +
               "Naive:\n" +
               "  - O(n*m) brute force\n" +
               "  - Best for: Edge cases (m=0, m>n) or tiny inputs (n < 100, m ≤ 3)\n" +
               "  - Zero preprocessing overhead\n" +
               "\n" +
               "Decision factors:\n" +
               "  - Pattern length (m): Primary factor for algorithm selection\n" +
               "  - Text length (n): Determines if preprocessing overhead is justified\n" +
               "  - Pattern characteristics: Repetition detection for KMP\n" +
               "  - Preprocessing vs. search trade-off: Shorter patterns favor lighter algorithms";
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