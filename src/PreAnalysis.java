//Mehmet Demir 22050111024
//Ayşe Zülal Şimşek 22050111047

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
        // first we need check if text and pattern is not null
        if (text == null || pattern == null) return "GoCrazy";
        
        final int textLen = text.length();
        final int patLen = pattern.length();
        
        // when pattern is empty we use gocrazy
        if (patLen == 0) return "GoCrazy";
        
        // if text empty then rabinkarp is fast
        if (textLen == 0) return "RabinKarp";
        
        // pattern cannot be bigger than text obviously
        if (patLen > textLen) return "BoyerMoore";
        
        // for single character gocrazy works best
        if (patLen == 1) return "GoCrazy";
        
        // rabinkarp is good when we have long pattern and long text
        if (patLen >= 10 && textLen >= 10000) {
            return "RabinKarp";
        }
        
        // medium patterns also good for rabinkarp
        if (patLen >= 8 && patLen <= 15 && textLen >= 3000) {
            return "RabinKarp";
        }
        
        // now we check if pattern has repeating structure
        int[] prefixTable = computeLPS(pattern);
        int lastValue = prefixTable[patLen - 1];
        int repeatLength = patLen - lastValue;
        
        // checking if pattern repeat itself
        boolean hasRepeat = (lastValue >= (int)(0.7 * patLen)) || 
                           (repeatLength > 0 && patLen % repeatLength == 0 && repeatLength <= patLen / 2);
        
        boolean isAltPattern = checkAlternating(pattern);
        boolean sameChars = checkAllSame(pattern);
        boolean isDNA = isDNALike(pattern);
        
        // kmp algorithm is very good for patterns that repeat
        if (sameChars) return "KMP";
        
        // alternating patterns like ababab also good for kmp
        if (isAltPattern) return "KMP";
        
        // dna sequences usually have repeats so kmp works
        if (isDNA && (hasRepeat || patLen >= 8)) {
            return "KMP";
        }
        
        if (hasRepeat && patLen >= 5) {
            return "KMP";
        }
        
        // when text is really long and pattern repeats
        if (hasRepeat && textLen >= 100) {
            return "KMP";
        }
        
        // gocrazy algorithm handles very big texts efficiently
        if (textLen >= 50000) {
            return "GoCrazy";
        }
        
        SmallAlphaStats stats = smallAlphabetStats(text, pattern);
        
        // boyermoore is better when we have many different characters
        if (!stats.isSmallAlphabet && patLen >= 15 && !hasRepeat) {
            return "BoyerMoore";
        }
        
        // naive algorithm is simple and works good for short patterns
        if (patLen <= 3 && !hasRepeat && !sameChars) {
            return "Naive";
        }
        
        // for small texts and short patterns naive is enough
        if (patLen <= 7 && textLen < 5000 && !hasRepeat && !isDNA) {
            return "Naive";
        }
        
        if (patLen <= 10 && textLen < 2000 && !hasRepeat) {
            return "Naive";
        }
        
        // very small cases we just use naive
        if (textLen < 500 && patLen < 20) {
            return "Naive";
        }
        
        // large texts with medium pattern size
        if (textLen >= 10000 && patLen >= 5 && patLen <= 20) {
            return "GoCrazy";
        }
        
        // longer patterns better with boyermoore
        if (patLen >= 12) {
            return "BoyerMoore";
        }
        
        // big texts better with rabinkarp
        if (textLen >= 5000) {
            return "RabinKarp";
        }
        
        // if nothing else works we use naive as default
        return "Naive";
    }
    
    @Override
    public String getStrategyDescription() {
        return "Optimized Strategy v3.0: " +
               "m=1→GoCrazy | " +
               "m≥10&n≥10k→RK | " +
               "periodic/same/alt→KMP | " +
               "DNA→KMP | " +
               "n≥50k→GoCrazy | " +
               "m≤10&n<5k→Naive | " +
               "else→smart-fallback";
    }
    
    // this function builds the prefix table for kmp algorithm
    private int[] computeLPS(String pattern) {
        int len = pattern.length();
        int[] lps = new int[len];
        int matchLen = 0;
        int i = 1;
        
        while (i < len) {
            if (pattern.charAt(i) == pattern.charAt(matchLen)) {
                lps[i++] = ++matchLen;
            } else if (matchLen != 0) {
                matchLen = lps[matchLen - 1];
            } else {
                lps[i++] = 0;
            }
        }
        return lps;
    }
    
    // checks if pattern is alternating like abab or abcabc
    private boolean checkAlternating(String pattern) {
        int len = pattern.length();
        if (len < 4) return false;
        
        // checking for two character alternation
        if (len >= 4) {
            boolean isTwoChar = true;
            for (int i = 2; i < len; i++) {
                if (pattern.charAt(i) != pattern.charAt(i % 2)) {
                    isTwoChar = false;
                    break;
                }
            }
            if (isTwoChar) return true;
        }
        
        // checking for three character alternation
        if (len >= 6) {
            boolean isThreeChar = true;
            for (int i = 3; i < len; i++) {
                if (pattern.charAt(i) != pattern.charAt(i % 3)) {
                    isThreeChar = false;
                    break;
                }
            }
            if (isThreeChar) return true;
        }
        
        return false;
    }
    
    // this checks if all characters in pattern are same
    private boolean checkAllSame(String pattern) {
        if (pattern.length() <= 1) return true;
        
        char firstChar = pattern.charAt(0);
        for (int i = 1; i < pattern.length(); i++) {
            if (pattern.charAt(i) != firstChar) return false;
        }
        return true;
    }
    
    // checking if pattern looks like dna sequence
    private boolean isDNALike(String pattern) {
        int len = pattern.length();
        if (len < 4) return false;
        
        int dnaChars = 0;
        // dna only has A C G T letters
        for (int i = 0; i < len; i++) {
            char c = Character.toUpperCase(pattern.charAt(i));
            if (c == 'A' || c == 'C' || c == 'G' || c == 'T' || c == 'N') {
                dnaChars++;
            }
        }
        // if 90% is dna letters then its probably dna
        return (dnaChars * 1.0 / len) >= 0.9;
    }
    
    // counts how many different characters we have
    private SmallAlphaStats smallAlphabetStats(String text, String pattern) {
        final int SAMPLE_SIZE = Math.min(300, text.length());
        boolean[] charSeen = new boolean[256];
        int uniqueChars = 0;
        
        // we check first 300 characters to save time
        int checkLimit = Math.min(text.length(), SAMPLE_SIZE);
        for (int i = 0; i < checkLimit; i++) {
            char c = text.charAt(i);
            if (c < 256 && !charSeen[c]) {
                charSeen[c] = true;
                uniqueChars++;
            }
        }
        
        // also check pattern characters
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (c < 256 && !charSeen[c]) {
                charSeen[c] = true;
                uniqueChars++;
            }
        }
        
        // if less than 8 different chars its small alphabet
        boolean isSmall = uniqueChars <= 8;
        return new SmallAlphaStats(isSmall);
    }
    
    private static class SmallAlphaStats {
        final boolean isSmallAlphabet;
        
        SmallAlphaStats(boolean s) {
            this.isSmallAlphabet = s;
        }
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