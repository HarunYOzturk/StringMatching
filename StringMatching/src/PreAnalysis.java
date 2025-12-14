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
/**
 * Default implementation that students should modify
 * This is where students write their pre-analysis logic
 */
class StudentPreAnalysis extends PreAnalysis {
    
    @Override
    public String chooseAlgorithm(String text, String pattern) {
        // 1. SAFETY CHECKS
        if (text == null || pattern == null) return "Naive";
        
        int n = text.length();
        int m = pattern.length();
        
        // 2. TRIVIAL CASES -> NAIVE
        // If the pattern is longer than text, no match is possible (or handle as per spec).
        // If pattern length is 0, logic varies, but Naive handles it simply.
        if (m > n || m == 0) return "Naive";

        // 3. TINY INPUTS -> NAIVE
        // REASON: "GoCrazy" (and other complex algos) requires allocating memory 
        // for maps or arrays (Shift Table / Bitmasks). 
        // For very short texts or 1-char patterns, the allocation time 
        // exceeds the time to just scan the string linearly.
        // Rule of thumb: If searching a single char or text is very small, keep it simple.
        if (m == 1 || n < 50) {
            return "Naive";
        }

        // 4. THE POWERHOUSE -> GOCRAZY
        // REASON: Our "GoCrazy" class is now a Hybrid Algorithm.
        // - For M <= 63: It uses Bitap (Shift-Or). This is O(N) with extremely low constant factor
        //   because it uses bitwise CPU instructions. It is faster than standard Boyer-Moore here.
        // - For M > 63: It switches to Sunday's Algorithm. Sunday performs larger jumps 
        //   than standard Boyer-Moore-Horspool because it checks the character *after* the window.
        
        // Since GoCrazy internally handles the switch between Bitap and Sunday,
        // it is the optimal choice for almost all medium-to-large search scenarios.
        return "GoCrazy";
    }
    
    @Override
    public String getStrategyDescription() {
        return "Smart Strategy: Uses 'Naive' for tiny inputs (N<50 or M=1) to avoid initialization overhead. For everything else, delegates to 'GoCrazy', which internally selects the best high-performance method (Bitap for M<=63, Sunday for M>63).";
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