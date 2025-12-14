// Selin Sargın  21005011024
// Zeynep Merve Karataş  23050131003


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

    @Override
    public String chooseAlgorithm(String text, String pattern) {

        // Handle potential edge cases where text or pattern might be null or empty
        if (text == null || pattern == null || pattern.length() == 0) {
            return "Naive";
        }

        int n = text.length();
        int m = pattern.length();

        int alphabet = alphabetSize(text);
        double entropy = patternEntropy(pattern);

        // If the pattern is very short (1–3 characters)
        if (m <= 3) {
            return "Naive";
        }

         // If the pattern is a palindrome
        if (isPalindrome(pattern) && m > 5) {
            return "GoCrazy";  
        }

         // If the pattern has high repetition
        if (hasRepeatingPrefix(pattern) || entropy < 1.3) {
            return "KMP";
        }

         // If the text is very large and the pattern is of medium length, RabinKarp may perform well
        if (n > 50000 && m > 10 && alphabet < 40) {
            return "RabinKarp";
        }

          // If the alphabet is very large
        if (alphabet >= 40 && m > 8) {
            return "BoyerMoore";
        }

         // If the pattern is long and the alphabet size is moderate
        if (m > 12 && alphabet >= 15) {
            return "BoyerMoore";
        }

        // Default: Boyer-Moore is a good practical choice in most cases
        return "BoyerMoore";
    }


    // helper method to check for repeating prefixes
    private boolean hasRepeatingPrefix(String pattern) {
        int n = pattern.length();
        if (n < 4) return false;

        for (int len = 1; len <= n / 2; len++) {
            String prefix = pattern.substring(0, len);
            if (pattern.startsWith(prefix, len)) return true;
        }
        return false;
    }

    private int alphabetSize(String s) {

        // Calculate the number of unique characters in the string
        java.util.HashSet<Character> set = new java.util.HashSet<>();
        for (char c : s.toCharArray()) set.add(c);
        return set.size();
    }

    // Calculate the entropy (measure of character diversity) in the pattern
    private double patternEntropy(String pattern) {
        java.util.HashMap<Character, Integer> freq = new java.util.HashMap<>();
        for (char c : pattern.toCharArray()) {
            freq.put(c, freq.getOrDefault(c, 0) + 1);
        }
        double entropy = 0.0;
        int len = pattern.length();
        for (int count : freq.values()) {
            double p = (double) count / len;
            entropy += -p * Math.log(p);
        }
        return entropy;
    }

    private boolean isPalindrome(String s) {
        int i = 0, j = s.length() - 1;
        
        // Check if the pattern is a palindrome
        while (i < j) {
            if (s.charAt(i) != s.charAt(j)) return false;
            i++; j--;
        }
        return true;
    }

     @Override
    public String getStrategyDescription() {
        return "Default strategy - no pre-analysis implemented yet (students should implement this)";
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