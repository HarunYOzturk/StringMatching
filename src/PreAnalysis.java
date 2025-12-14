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
    // TODO: Students should implement their analysis logic here
    // 
    // Example considerations:
    // - If pattern is very short, Naive might be fastest
    // - If pattern has repeating prefixes, KMP is good
    // - If pattern is long and text is very long, RabinKarp might be good
    // - If alphabet is small, Boyer-Moore can be very efficient
    //
    // For now, this returns null which means "run all algorithms"
    // Students should replace this with their logic
    
    
    @Override
    public String chooseAlgorithm(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();

        // 1. If both pattern and text is empty or pattern length is bigger than text length
        if (m == 0 || n == 0) return "Naive";
        if (m > n) return "Naive"; 

        // 2. UNICODE control
        // If there is a Turkish character 
        if (hasUnicode(pattern)) {
            return "Naive";
        }

        // 3. Repetition control (KMP dedector)
        // Even if the pattern is short but contains repeat naive is slow
        // Because of that checking it before "m < 20"
        if (isRepetitive(pattern)) {
            return "KMP";
        }

        // 4. long or average sized length
        // If the length is long the GoCrazy works better
        if (n > 500) {
            return "GoCrazy"; 
        }

        // 5. short pattern use naive
        // Because its not efficient to create a table
        if (m < 20) {
            return "Naive";
        }

        // 6. DEFAULT
        // Using Gocrazy becasue it works more stable than BoyerMoore because of guard mechanism
        return "GoCrazy"; 
    }
    
    // Unicode control
    private boolean hasUnicode(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) > 255) return true;
        }
        return false;
    }

    // Repetition control
    // Looks for the first 5 character
    private boolean isRepetitive(String p) {
        int checkLen = Math.min(p.length(), 5);
        if (checkLen < 2) return false;
        
        char first = p.charAt(0);
        // Checks if the first letter repeats in the next indexes
        for (int i = 1; i < checkLen; i++) {
            if (p.charAt(i) == first) return true;
        }
        return false;
    }

    @Override
    public String getStrategyDescription() {
        return "Smart Hybrid: Prioritizes GoCrazy (Sunday) for most cases, detects repetitive patterns for KMP.";
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
