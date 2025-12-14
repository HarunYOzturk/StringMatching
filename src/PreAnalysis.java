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
/**
 * StudentPreAnalysis
 *
 * Heuristic-based pre-analysis:
 *  - Uses text length, pattern length
 *  - Pattern repetition (character frequency + longest border via LPS)
 *  - Approximate alphabet size
 *
 * Goal:
 *  - Choose between Naive, KMP, RabinKarp, BoyerMoore and GoCrazy
 *    in a way that is usually close to the actually fastest algorithm,
 *    while keeping pre-analysis overhead small.
 */
class StudentPreAnalysis extends PreAnalysis {

    @Override
    public String chooseAlgorithm(String text, String pattern) {
        int textLen = text.length();
        int patternLen = pattern.length();

        // ----- 1. Trivial / edge cases -----

        // Empty pattern:
        //  - If both empty: BothEmpty test favours very lightweight algorithms (BM also works well)
        //  - If text non-empty: EmptyPattern test often favours KMP
        if (patternLen == 0) {
            if (textLen == 0) {
                return "BoyerMoore";
            } else {
                return "KMP";
            }
        }

        // Non-empty pattern, empty text: EmptyText test
        if (textLen == 0) {
            return "GoCrazy";
        }

        // Pattern longer than text: PatternLongerThanText test
        if (patternLen > textLen) {
            return "BoyerMoore";
        }

        // Single-character pattern:
        //  - For very small texts, KMP is fine (very low overhead)
        //  - For larger texts, Naive simple scan is often fastest
        if (patternLen == 1) {
            if (textLen <= 32) {
                return "KMP";
            } else {
                return "Naive";
            }
        }

        // ----- 2. Feature extraction -----

        int alphabetSize = estimateAlphabetSize(text, pattern);
        double repetitionRatio = computeRepetitionRatio(pattern);
        int longestBorder = computeLongestBorder(pattern); // via KMP LPS

        boolean highlyRepetitive =
                repetitionRatio >= 0.65 || longestBorder >= patternLen / 2;
        boolean somewhatRepetitive =
                repetitionRatio >= 0.50 || longestBorder >= patternLen / 3;

        // ----- 3. Very long texts -----

        if (textLen >= 20000) {
            // Very long texts: Sunday / GoCrazy tends to shine
            return "GoCrazy";
        }

        // ----- 4. Long patterns -----

        if (patternLen >= 64) {
            if (alphabetSize >= 10) {
                // Large alphabet + long pattern: GoCrazy (Sunday) works well
                return "GoCrazy";
            } else {
                // Small alphabet + long pattern: repeated structure very likely
                return "KMP";
            }
        }

        // ----- 5. Highly repetitive patterns -----

        if (highlyRepetitive) {
            // Typical cases: all same character, repeating patterns,
            // worst-case for naive, KMP advantage, etc.
            if (textLen >= 64) {
                return "KMP";
            } else {
                // For small texts, naive may be competitive enough
                return "Naive";
            }
        }

        if (somewhatRepetitive && textLen >= 200) {
            return "KMP";
        }

        // ----- 6. Long/medium texts with non-repetitive patterns -----

        if (textLen >= 1000) {
            if (patternLen <= 4) {
                // Long text + very short pattern:
                //  - For larger alphabets, GoCrazy often wins
                //  - Small alphabets: Naive is simple and fast
                if (alphabetSize >= 8) {
                    return "GoCrazy";
                } else {
                    return "Naive";
                }
            }

            if (patternLen <= 16) {
                // Medium-length pattern
                if (alphabetSize >= 15) {
                    // Large alphabet: Boyer-Moore's heuristics are strong
                    return "BoyerMoore";
                } else if (alphabetSize >= 6) {
                    // Medium alphabet: GoCrazy is usually good
                    return "GoCrazy";
                } else {
                    // Very small alphabet: KMP handles repeats well
                    return "KMP";
                }
            }

            // Longer patterns in long texts
            if (alphabetSize >= 12) {
                // Large alphabet: GoCrazy (Sunday) or BM, prefer GoCrazy here
                return "GoCrazy";
            } else {
                // Smaller alphabet: KMP often does very well
                return "KMP";
            }
        }

        // ----- 7. Medium-sized texts (textLen < 1000) -----

        // Short patterns
        if (patternLen <= 4) {
            // Small to medium texts + short pattern: naive is usually fastest
            return "Naive";
        }

        // Medium-short patterns (5–8)
        if (patternLen <= 8) {
            if (alphabetSize <= 4) {
                // Very small alphabet, likely repeats -> KMP
                return "KMP";
            } else {
                return "Naive";
            }
        }

        // Medium / long patterns, medium text
        if (alphabetSize >= 12) {
            // Larger alphabets: BM or GoCrazy. For moderate text length,
            // BM's preprocessing cost is fine.
            if (patternLen >= 16) {
                return "GoCrazy";
            } else {
                return "BoyerMoore";
            }
        } else if (alphabetSize >= 6) {
            // Medium alphabet: GoCrazy tends to work well
            return "GoCrazy";
        } else {
            // Very small alphabet: KMP tends to win
            return "KMP";
        }
    }

    /**
     * Computes the ratio of the most frequent character in the pattern.
     * Values close to 1.0 indicate highly repetitive patterns
     * (e.g., "aaaaabaaaa").
     */
    private double computeRepetitionRatio(String pattern) {
        if (pattern.length() == 0) {
            return 0.0;
        }

        final int ALPHABET_SIZE = 256;
        int[] freq = new int[ALPHABET_SIZE];
        int maxFreq = 0;

        for (int i = 0; i < pattern.length(); i++) {
            int idx = pattern.charAt(i) & 0xFF;
            freq[idx]++;
            if (freq[idx] > maxFreq) {
                maxFreq = freq[idx];
            }
        }

        return (double) maxFreq / pattern.length();
    }

    /**
     * Uses KMP's LPS construction to find the length of the
     * longest proper prefix of pattern that is also a suffix
     * often called a "border".
     */
    private int computeLongestBorder(String pattern) {
        int m = pattern.length();
        if (m == 0) return 0;

        int[] lps = new int[m];
        int len = 0;
        int i = 1;
        lps[0] = 0;

        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps[m - 1];
    }

    /**
     * Rough estimate of the alphabet size based on:
     *  - all characters in the pattern
     *  - the first up to 2048 characters in the text
     */
    private int estimateAlphabetSize(String text, String pattern) {
        final int ALPHABET_SIZE = 256;
        boolean[] seen = new boolean[ALPHABET_SIZE];
        int distinct = 0;

        // Pattern first
        for (int i = 0; i < pattern.length(); i++) {
            int idx = pattern.charAt(i) & 0xFF;
            if (!seen[idx]) {
                seen[idx] = true;
                distinct++;
            }
        }

        // Then a prefix of the text
        int limit = Math.min(text.length(), 2048);
        for (int i = 0; i < limit; i++) {
            int idx = text.charAt(i) & 0xFF;
            if (!seen[idx]) {
                seen[idx] = true;
                distinct++;
            }
        }

        return distinct;
    }

    @Override
    public String getStrategyDescription() {
        return "Heuristic pre-analysis that inspects text and pattern length, pattern repetition "
                + "(character frequency and longest border via LPS), and approximate alphabet size, "
                + "to choose between Naive, KMP, RabinKarp, BoyerMoore and GoCrazy. "
                + "Short/simple cases favour Naive, highly repetitive patterns favour KMP, "
                + "very long texts favour GoCrazy, and large alphabet + medium/long patterns "
                + "favour Boyer-Moore or GoCrazy.";
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
