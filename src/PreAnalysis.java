
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

    public abstract String getStrategyDescription();
}

class StudentPreAnalysis extends PreAnalysis {

    @Override
    public String getStrategyDescription() {
        return "Optimized Rule-Based Selector: Chooses algorithm based on text length, pattern length, repetition, alternating characters, and Unicode/special characters. Aims to pick the fastest algorithm per shared test cases.";
    }

    @Override
    public String chooseAlgorithm(String text, String pattern) {
        int textLen = text.length();
        int patternLen = pattern.length();

        // Both text and pattern are empty → we should choose KMP
        if (textLen == 0 || patternLen == 0)
            return "KMP";

        // Only pattern is empty → choose KMP (handles empty pattern efficiently)
        if (patternLen == 0)
            return "KMP";

        if (textLen > 500)
            return "BoyerMoore";

        boolean unicodeFound = false;

        for (int i = 0; i < pattern.length(); i++) {
            if (pattern.charAt(i) > 127) {
                unicodeFound = true;
                break;
            }
        }

        if (!unicodeFound) {
            for (int i = 0; i < text.length(); i++) {
                if (text.charAt(i) > 127) {
                    unicodeFound = true;
                    break;
                }
            }
        }

        if (unicodeFound) {
            if (patternLen == 1)
                return "Naive";

            return "GoCrazy";
        }

        // If the pattern longer than text → impossible match and using BoyerMoore
        if (patternLen > textLen)
            return "BoyerMoore";

        // Single character pattern → Naive is fastest for this situation.
        if (patternLen == 1)
            return "Naive";

        // short pattern we should use naive algorithm for simple strings
        if (textLen <= 10 && patternLen <= 5)
            return "Naive";

        // if the pattern consist repidations we should use KMP
        if (hasRepeatingPattern(pattern))
            return "KMP";

        // for default behaviour i choose Naive
        return "Naive";
    }

    boolean hasRepeatingPattern(String pattern) {
        /*
         * With this method I checked if a pattern is made of repeated substrings for
         * using KMP
         */
        int len = pattern.length();
        for (int i = 1; i <= len / 2; i++) {
            String sub = pattern.substring(0, i);
            if (pattern.startsWith(sub.repeat(len / i)))
                return true;
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
