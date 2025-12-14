import java.util.*;

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
//  */
// class StudentPreAnalysis extends PreAnalysis {
    
//     @Override
//     public String chooseAlgorithm(String text, String pattern) {
//          int textLen = text.length();
//         int patternLen = pattern.length();

//         // Simple heuristic example
//         if (patternLen <= 3) {
//             return "Naive"; // For very short patterns, naive is often fastest
//         } else if (hasRepeatingPrefix(pattern)) {
//             return "KMP"; // KMP is good for patterns with repeating prefixes
//         }else if (getUniqueAlphabets(text) <= 5 && textLen > 10){
//             return "BoyerMoore";
//         }
//          else if (patternLen > 10 && textLen > 1000) {
//             return "RabinKarp"; // RabinKarp can be good for long patterns in long texts
//         } else if (patternLen > 10 && textLen > 1000){
//             return "BoyerMoore"; // Default to naive for other cases
//         }else{
//             return "Naive";
//         }
        
//         // return null; // Return null to run all algorithms, or return algorithm name to use pre-analysis
//     }
    
//     @Override
//     public String getStrategyDescription() {
//         return "Strategy: Choice was made on the length of the text, the length of the pattern, whether the pattern has repeating characters and/or according to the number of unique alphabets in the text.";
//     }

//     private int getUniqueAlphabets(String text)
//     {
//         Set<Character> uniqueChars = new HashSet<>();
//         for(int i=0; i<text.length(); i++)
//         {
//             uniqueChars.add(text.charAt(i));
//         }

//         return uniqueChars.size();
//     }

//     private boolean hasRepeatingPrefix(String pattern) {
//         if (pattern.length() < 2) return false;

//         // Check if first character repeats
//         char first = pattern.charAt(0);
//         int count = 0;
//         for (int i = 0; i < Math.min(pattern.length(), 5); i++) {
//             if (pattern.charAt(i) == first) count++;
//         }
//         return count >= 3;
//     }


// }






class StudentPreAnalysis extends PreAnalysis {

    @Override
    public String chooseAlgorithm(String text, String pattern) {

        // get the lengths of the text and the pattern
        int n = text.length();
        int m = pattern.length();

        // get the number of unique characters
        int sigma = getUniqueAlphabets(text); // Alphabet size

        // === 1. Handle trivial & degenerate cases ===

        // if the pattern is empty, use naive
        if (m == 0)
        {
            return "Naive";
        }

        // if the pattern is longer than the text, use naive
        if (m > n)
        {
            return "Naive";
        }

        // if the pattern is smaller than or equal to two characters, use naive
        if (m <= 2)
        {
            return "Naive";
        }
            


        // If the pattern is repetitive, use KMP
        if (isHighlyPeriodic(pattern)) {
            return "KMP";
        }


        // If the alphabet size is large and the pattern is long, use BoyerMoore
        if (sigma >= 20 && m >= 5) {
            return "BoyerMoore";
        }

        // If the alpabet is small but the text is long, again BoyerMoore works well
        if (sigma <= 5 && n >= 50) {
            return "BoyerMoore";
        }


        // If botht the text and the patterns are long, RabinKarp is ideal
        if (n >= 2000 && m >= 20) {
            return "RabinKarp";
        }


        // General fallback
        return "Naive";
    }


    @Override
    public String getStrategyDescription() {
        return "This strategy considers alphabet size, pattern repeatedness, and text/pattern length. "
             + "It prefers KMP for repeated patterns, Boyer-Moore for large alphabets or small-alphabet long texts, "
             + "and Rabin-Karp for very long text-pattern combinations. "
             + "Short patterns default to Naive for efficiency.";
    }


    // SUPPORT FUNCTIONS 

    private int getUniqueAlphabets(String text) {
        // Create a set
        Set<Character> set = new HashSet<>();

        // Add each alphabet to the set
        for (char c : text.toCharArray())
        {
             set.add(c);
        }

        // return the length of the set
        return set.size();
    }

    // method use to each for repeatedness or periodicity
    private boolean isHighlyPeriodic(String p) {
        // get the length of the string
        int m = p.length();

        // compute prefix-function (like KMP)
        int[] pi = new int[m];
        for (int i = 1, j = 0; i < m; i++) {

            while (j > 0 && p.charAt(i) != p.charAt(j))
            {
                // update j
                j = pi[j - 1];
            }    
                
            // if the characters are the same
            if (p.charAt(i) == p.charAt(j)) 
            {
                // move to the next character
                 j++;
            }   
            // update the value for character at ith index
            pi[i] = j;
        }

        // get the value at the last index
        int longestPrefixSuffix = pi[m - 1];

        // get the repeating factor
        int period = m - longestPrefixSuffix;

        boolean result = (longestPrefixSuffix > 0 && m % period == 0);

        return result;
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
