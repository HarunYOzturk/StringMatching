//Rümeysa Kara 22050111053
//Aybüke Karaçavuş 22050111005

import java.util.*;

/**
 * We examine the structural characteristics of both the text and the pattern,
 * and we determine which string-matching algorithm should provide the most
 * efficient execution. If we return a non-null algorithm name, the system will 
 * rely on our decision instead of executing all algorithms.
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
    
    
      /**
     * We analyze the given text and pattern to select the most appropriate algorithm.
     *
     * @param text The text in which we perform the search operation.
     * @param pattern The pattern we attempt to locate.
     * @return The algorithm name we decide to use (e.g., "Naive", "KMP", 
     *         "RabinKarp", "BoyerMoore", "GoCrazy"), or null if we prefer to let the
     *         system run all algorithms for performance comparison.
     *
     * We evaluate:
     * - How long the text and pattern are.
     * - Whether the pattern exhibits structural regularities such as repeated prefixes.
     * - Whether the alphabet size is small or large.
     * - Which algorithm performs better under various input characteristics.
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
 * Student implementation containing our custom analysis logic.
 *
 * Here we define the rules, heuristics, and structural evaluations we use
 * to decide which algorithm will likely execute most efficiently.
 */
class StudentPreAnalysis extends PreAnalysis {
    
    @Override
    public String chooseAlgorithm(String text, String pattern) {
        // TODO: Students should implement their analysis logic here
        
        /* We basically check a few simple things to decide which algorithm to use.
        We look at how long the text and pattern are, because very short patterns usually work best with the Naive algorithm. 
        If the pattern has a lot of repeating characters, we pick KMP since it handles that well. 
        If the pattern is long or the alphabet is large, Rabin-Karp can be faster because hashing helps.
        When the text is very long, Boyer-Moore can skip more characters and speed things up, but it works best when the alphabet is not too small. 
        We also avoid heavy preprocessing for tiny patterns because it wastes time.
        If none of these checks clearly point to one algorithm, we simply choose Naive as the default.
        */
        
        
        int textLen = text.length();
        int patternLen = pattern.length();

        // If the pattern is empty, we immediately select a simple approach.
        if (patternLen == 0) return "Naive";

        // We choose the Naive algorithm when patterns are extremely short
        // or when the input is too small for advanced preprocessing to pay off.
        if (patternLen <= 3  || textLen < 10) 
            return "Naive";
        
        // If we detect a repeating prefix pattern, we choose KMP because
        // its prefix-function preprocessing reduces redundant comparisons.
        if (hasRepeatingPrefix(pattern)) 
            return "KMP";
        
        // When the alphabet is large or the pattern is long, we select Rabin-Karp
        // because hashing can provide substantial performance benefits.
        if (isLargeAlphabet(pattern) || patternLen >= 10)
            return "RabinKarp";
        // If the text is very long, we select Boyer-Moore because
        // its skip-based heuristics outperform other algorithms on large inputs.
        if (textLen > 100) 
            return "BoyerMoore";
        // If no conditions match, we fall back to GoCrazy as a custom alternative.
        return "GoCrazy";
    }
    
    private boolean isLargeAlphabet(String pattern) {
    int alphabet = (int)pattern.chars().distinct().count();
    int m = pattern.length();

    // if alphabet is bigger than pattern length condsider as large alphabet
    return alphabet > m / 2;
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
        return "Choose algorithm based on: pattern lenght, text length and alphabet size.";
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
