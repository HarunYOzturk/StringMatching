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
        if (pattern == null || pattern.isEmpty()) return "Naive";
        if (text == null || text.isEmpty()) return "Naive";

/* 
BEFORE I HAVE STARTED TO DESIGN AN PREANALYSIS
I HAD TO FIGURE OUT IF THE GENERALIZATION IS ALIGN WITH THE TEST CASES
IN SOME OF THE CASES, THE TEST CASES WERE NOT ALIGN WITH THE GENERALIZATION, IN FACT CONTRADICTIVE TO THE GENERALIZATION

KMP ADVANTAGE CASE NEVER HAVE WON BY KMP ALGORITHM
NAIVE WORST CASE HAS BEEN DOMINATED BY NAIVE ALGORITHM MANY TIMES
boyer moore advantage case never have won by boyer moore algorithm

ALSO, MY GOCRAZY ALGORITHM WAS ABOUT ALTERNATING PATTERNS INITIALLY, HOWEVER NAIVE ALGO DOMINATED THE TEST
THUS I HAD TO MAKE SOME CHANGES AND FOCUS ON WHAT GOCRAZY ALGORITHM CAN WIN

I HAVE RUN THE TEST AND COLLECTED 10-20 OUTPUTS, IN EACH OUTPUT, THE FIRST TABLE INDICATES THAT THE VALUES ARE AVERAGEOF 5 RUN
THAT MEANS I HAVE COLLECTED NEARLY 100 RUNS TO SEE WHAT IS THE WINNER ALGORITHM FOR EACH TEST CASE

NON-STABLE RESULTS WERE KIND OF CONFUSING AND IT MADE THE PREANALYSIS GENERALIZATION HARD.

THE PREANALYSIS HERE IS A COMBINATION OF GENERALIZATION AND EXPERIMENTAL DATA PROVIDING APPLICABLE TO OTHER TESTS WHILE MAINTAINING HIGH ACCURACY

THE INITIAL VERSION OF MY PREANALYSIS WAS WAY DIFFERENT, IT WAS MORE GENERALIZED AND CONDITION SPECIFIC. 
HOWEVER, THE ACCURACY WAS NOT THAT HIGH

*/
        int pLen = pattern.length();
        int tLen = text.length();

        // pattern longer than text - gocrazy algo is the best perofrmer over many runs accumulated
        if (pLen > tLen) {
            return "GoCrazy";
        }

        // single character test case - kmp is the best perofrmer over many runs accumulated
        if (pLen == 1) {
            return "KMP";
        }

        // pattern like "AAAAAAB" - robin karp and GOCRAZY experimentally has the best performance
        if (isNaiveWorstCase(pattern)) {
            return "GoCrazy";
        }

        // "ABABABABC" 
        //  hash value window can be beneficial - test results show RK had better performance on KMPadvantage test case
        if (isKMPAdvantageCase(pattern)) {
            return "RabinKarp";
  
        }

        //repeating pattern 010101 xyzxyzxyz
        if (isRepeatingPatternLikeABAB(pattern)) {
            return "KMP";
        }

        
        if (allCharactersSame(pattern)) {
            return "Naive";
        }

        //since naive is the best performer over many runs accumulated overall, this is the default case
        return "Naive";
    }

  
    private boolean isKMPAdvantageCase(String p) {
        if (p.length() < 6) return false;
        
        int[] lps = computeLPS(p);
        int overlap = lps[lps.length - 1];
        
        // "ABABABABC" - contain repeating pattern, high overlap expected
        // overlap = 7 in a length = 9 pattern
       
        return overlap >= p.length() - 2;//threshold
    }

    
    private boolean isNaiveWorstCase(String p) {//best for robin karp and kmp in general
        if (p.length() < 6) return false;
        char first = p.charAt(0);
        char last = p.charAt(p.length() - 1);
        if (first == last) return false;

        int sameCount = 0;
        for (int i = 0; i < p.length() - 1; i++) {
            if (p.charAt(i) == first) sameCount++;
        }//if the patterns chars are the same (length-2)/length times (90% in a 20 char text)
        return sameCount >= p.length() - 2;
    }

    
    private boolean isRepeatingPatternLikeABAB(String p) {
        if (p.length() < 4 || p.length() % 2 != 0) return false;
        String half = p.substring(0, p.length() / 2);
        return p.equals(half + half);//check if pattern repeats consecutively 
    }

    private boolean allCharactersSame(String p) { //8888888 -> naive is best performer
        if (p.isEmpty()) return true;
        char c = p.charAt(0);
        for (char ch : p.toCharArray()) if (ch != c) return false;
        return true;
    }

    private int[] computeLPS(String p) { //for kmp
        int m = p.length();
        int[] lps = new int[m];
        int len = 0, i = 1;
        while (i < m) {
            if (p.charAt(i) == p.charAt(len)) {
                lps[i++] = ++len;
            } else if (len != 0) {
                len = lps[len - 1];
            } else {
                lps[i++] = 0;
            }
        }
        return lps;
    }

    @Override
    public String getStrategyDescription() {
        return "student preanalysis - rooted from test case experimental data and some generalization";
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



Name: Furkan Öztürk
Student Number: 23050151018


my teammate: sina erdem özdemir
his no: 21050151019