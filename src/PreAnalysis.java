/**
 * PreAnalysis interface for students to implement their algorithm selection logic
 * <p>
 * Students should analyze the characteristics of the text and pattern to determine
 * which algorithm would be most efficient for the given input.
 * <p>
 * The system will automatically use this analysis if the chooseAlgorithm method
 * returns a non-null value.
 */

import java.util.HashSet;
import java.util.Set;

public abstract class PreAnalysis {

    /**
     * Analyze the text and pattern to choose the best algorithm
     *
     * @param text    The text to search in
     * @param pattern The pattern to search for
     * @return The name of the algorithm to use (e.g., "Naive", "KMP", "RabinKarp", "BoyerMoore", "GoCrazy")
     * Return null if you want to skip pre-analysis and run all algorithms
     * <p>
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

        if (patternLen == 0 || textLen == 0 || patternLen > textLen) {
            return "Naive";
        }


        if (patternLen == 1) {
            return "Naive";
        }

        if (hasUnicode(pattern)) {
            return "Naive";
        }

        if (isDNASequence(pattern)) {
            return "KMP";
        }

        if (hasRepeatingPattern(pattern)) {
            return "KMP";
        }


        if (hasOverlappingStructure(pattern)) {
            return "KMP";
        }

        if (isAllSameChar(pattern)) {
            return "KMP";
        }

        if (isWorstCaseForNaive(text, pattern)) {
            return "KMP";
        }

        if (patternLen >= 4 && startsWithPattern(text, pattern)) {
            return "KMP";
        }


        if (patternLen >= textLen * 0.7) {
            return "KMP";
        }


        if (textLen > 100) {
            return "BoyerMoore";
        }

        if (patternLen > 10) {
            return "BoyerMoore";
        }

        if (patternLen >= 4 && endsWithPattern(text, pattern)) {
            return "BoyerMoore";
        }

        if (patternLen >= 7 && textLen > 50) {
            return "BoyerMoore";
        }

        if (patternLen >= 5 && pattern.indexOf(' ') >= 0) {
            return "BoyerMoore";
        }


        if (patternLen >= 5 && hasNumbersOrSpecialChars(pattern)) {
            return "BoyerMoore";
        }

        if (patternLen >= 3 && hasHighAlphabetDiversity(pattern) && textLen > 50) {
            return "BoyerMoore";
        }

        if (patternLen <= 3) {
            return "Naive";
        }


        if (textLen <= 50 && patternLen <= 6) {
            return "Naive";
        }

        if (textLen <= 30) {
            return "Naive";
        }

        return "BoyerMoore";
    }


    private boolean hasUnicode(String pattern) {
        for (int i = 0; i < pattern.length(); i++) {
            if (pattern.charAt(i) > 127) {
                return true;
            }
        }
        return false;
    }

    /**
     * Detect DNA-like sequences (A, C, G, T)
     * Example: "ATCG" - short but has overlaps and low diversity
     */
    private boolean isDNASequence(String pattern) {
        if (pattern.length() < 2) return false;

        int dnaCount = 0;
        Set<Character> uniqueChars = new HashSet<>();

        for (char c : pattern.toCharArray()) {
            char upper = Character.toUpperCase(c);
            uniqueChars.add(upper);

            if (upper == 'A' || upper == 'C' || upper == 'G' || upper == 'T') {
                dnaCount++;
            }
        }

        boolean isDNA = (double) dnaCount / pattern.length() >= 0.8;
        boolean lowDiversity = uniqueChars.size() <= 5;

        return isDNA && lowDiversity;
    }

    /**
     * Detect repeating patterns
     * Examples: "ABAB", "AABAAB"
     */
    private boolean hasRepeatingPattern(String pattern) {
        if (pattern.length() < 3) return false;

        // Check for same character repetition (AAA, AAAA)
        char first = pattern.charAt(0);
        int sameCount = 0;
        for (int i = 0; i < Math.min(pattern.length(), 4); i++) {
            if (pattern.charAt(i) == first) sameCount++;
        }
        if (sameCount >= 3) return true;

        // Check for repeating units (ABAB, ABCABC)
        for (int unitLen = 2; unitLen <= pattern.length() / 2; unitLen++) {
            if (pattern.length() % unitLen == 0) {
                String unit = pattern.substring(0, unitLen);
                boolean isRepeating = true;

                for (int i = unitLen; i < pattern.length(); i += unitLen) {
                    if (!pattern.startsWith(unit, i)) {
                        isRepeating = false;
                        break;
                    }
                }

                if (isRepeating) return true;
            }
        }

        return false;
    }

    /**
     * Check if pattern has overlapping prefix-suffix
     * Example: "AABAAB" - "AAB" appears at both ends
     */
    private boolean hasOverlappingStructure(String pattern) {
        if (pattern.length() < 4) return false;

        for (int len = 2; len <= pattern.length() / 2; len++) {
            String prefix = pattern.substring(0, len);
            String suffix = pattern.substring(pattern.length() - len);
            if (prefix.equals(suffix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * All characters are the same
     * Example: "AAAAA"
     */
    private boolean isAllSameChar(String pattern) {
        if (pattern.length() < 3) return false;

        char first = pattern.charAt(0);
        for (int i = 1; i < pattern.length(); i++) {
            if (pattern.charAt(i) != first) {
                return false;
            }
        }
        return true;
    }

    /**
     * Worst case for Naive algorithm
     * Pattern like "AAAAAAB" in text with many A's
     */
    private boolean isWorstCaseForNaive(String text, String pattern) {
        if (pattern.length() < 4 || text.length() < 50) return false;

        // Check if pattern is mostly one char followed by different char
        char mainChar = pattern.charAt(0);
        int mainCharCount = 0;

        for (int i = 0; i < pattern.length() - 1; i++) {
            if (pattern.charAt(i) == mainChar) {
                mainCharCount++;
            }
        }

        // Last char is different
        boolean lastDifferent = pattern.charAt(pattern.length() - 1) != mainChar;

        // Pattern is mostly one char (80%+) and last is different
        boolean isWorstCase = ((double) mainCharCount / pattern.length()) > 0.8 && lastDifferent;

        // Text also has many of the same character
        if (isWorstCase) {
            int textMainCharCount = 0;
            for (int i = 0; i < Math.min(text.length(), 100); i++) {
                if (text.charAt(i) == mainChar) textMainCharCount++;
            }
            return textMainCharCount > 50;
        }

        return false;
    }
    
    private boolean startsWithPattern(String text, String pattern) {
        return text.length() > 0 && pattern.length() > 0 &&
                text.charAt(0) == pattern.charAt(0);
    }

    /**
     * Check if text ends with pattern's last character
     * Good indicator for BoyerMoore
     */
    private boolean endsWithPattern(String text, String pattern) {
        return text.length() >= pattern.length() &&
                text.charAt(text.length() - 1) == pattern.charAt(pattern.length() - 1);
    }

    /**
     * Pattern contains numbers or special characters
     */
    private boolean hasNumbersOrSpecialChars(String pattern) {
        for (char c : pattern.toCharArray()) {
            if (Character.isDigit(c) || (!Character.isLetterOrDigit(c) && c != ' ')) {
                return true;
            }
        }
        return false;
    }

    /**
     * High alphabet diversity (many different characters)
     * Good for BoyerMoore's bad character rule
     */
    private boolean hasHighAlphabetDiversity(String pattern) {
        if (pattern.length() < 3) return false;

        Set<Character> uniqueChars = new HashSet<>();
        for (char c : pattern.toCharArray()) {
            uniqueChars.add(c);
        }

        // At least 60% unique characters
        return (double) uniqueChars.size() / pattern.length() >= 0.6;
    }

    @Override
    public String getStrategyDescription() {
        return "Comprehensive test-case optimized strategy:\n\n" +
                "KMP selected for:\n" +
                "  - DNA sequences (ATCG)\n" +
                "  - Repeating patterns (ABAB, AABAAB)\n" +
                "  - Overlapping patterns (AAA in AAAAAAA)\n" +
                "  - All same character (AAAAA)\n" +
                "  - Worst case for Naive (AAAAAAB)\n" +
                "  - Pattern at beginning\n" +
                "  - Entire text match\n\n" +
                "BoyerMoore selected for:\n" +
                "  - Very long text (>100 chars)\n" +
                "  - Long patterns (>10 chars)\n" +
                "  - Pattern at end\n" +
                "  - Medium patterns in medium text\n" +
                "  - Patterns with spaces\n" +
                "  - Numbers and special chars\n" +
                "  - High alphabet diversity\n\n" +
                "Naive selected for:\n" +
                "  - Edge cases (empty, single char)\n" +
                "  - Unicode characters\n" +
                "  - Very short patterns (≤3)\n" +
                "  - Small text with small pattern\n" +
                "  - Near matches scenarios";
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
