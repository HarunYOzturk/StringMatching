/**
 * PreAnalysis interface for students to implement their algorithm selection logic
 * * Students should analyze the characteristics of the text and pattern to determine
 * which algorithm would be most efficient for the given input.
 * * The system will automatically use this analysis if the chooseAlgorithm method
 * returns a non-null value.
 */
public abstract class PreAnalysis {

    /**
     * Analyze the text and pattern to choose the best algorithm
     * * @param text The text to search in
     * @param pattern The pattern to search for
     * @return The name of the algorithm to use (e.g., "Naive", "KMP", "RabinKarp", "BoyerMoore", "GoCrazy")
     * Return null if you want to skip pre-analysis and run all algorithms
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

        // 1. Short texts -> Naive (No setup overhead)
        if (n < 20) {
            return "Naive";
        }

        // 2. Repetitive patterns -> KMP (Handles re-scanning best)
        if (isRepetitive(pattern)) {
            return "KMP";
        }

        // 3. Complex/Mixed patterns -> RabinKarp (Robust hashing for symbols/nums)
        if (isComplex(pattern)) {
            return "RabinKarp";
        }

        // 4. Default -> BoyerMoore (Fastest for standard text)
        return "BoyerMoore";
    }

    // Helper: Detect repetitive prefixes (e.g. "AAA")
    private boolean isRepetitive(String p) {
        if (p.length() < 4) return false;
        // Fast check: first 3 chars are same
        return p.charAt(0) == p.charAt(1) && p.charAt(1) == p.charAt(2);
    }

    // Helper: Detect complex patterns (numbers, symbols, spaces)
    private boolean isComplex(String p) {
        for (char c : p.toCharArray()) {
            // If char is not a simple letter, treat as complex
            if (!Character.isLetter(c)) return true;
        }
        return false;
    }

    @Override
    public String getStrategyDescription() {
        return "Strategy: Naive for short texts, KMP for repetitive, RabinKarp for complex, BoyerMoore otherwise.";
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