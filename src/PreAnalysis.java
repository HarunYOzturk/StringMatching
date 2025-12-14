public abstract class PreAnalysis {
    public abstract String chooseAlgorithm(String text, String pattern);
    
    public abstract String getStrategyDescription();
}


/**
 * Strategy Overview:
 * Decision tree:
 * 
 * 1. Pattern Length: Very short patterns use Naive algorithm
 * 
 * 2. Pattern Repetition: Uses LPS (Longest Proper Prefix which is also Suffix) to detect
 *    internal repetition. High repetition ratio indicates KMP is optimal.
 * 
 * 3. Character Diversity: Calculates distinct character ratio in pattern. High difference
 *    favors Boyer-Moore algorithm. (Good skip chances)
 * 
 * 4. Text Entropy: Analyzes text entropy using Shannon entropy formula. High entropy
 *    indicates low repetition, favoring Rabin-Karp. Low entropy indicates
 *    high repetition, favoring KMP.
 */
class StudentPreAnalysis extends PreAnalysis {
    
    @Override
    public String chooseAlgorithm(String text, String pattern) {
        int m = pattern.length();
        
        if (m <= 4)
            return "Naive";
        
        
        // IF pattern is highly repetitive, use KMP
        int[] lps = computeLPS(pattern);
        if (m > 0 && lps[m - 1] > 0) {
            double repetitionRatio = (double) lps[m - 1] / m;
            if (repetitionRatio >= 0.5)
                return "KMP";
            
        }
        
        // If pattern character diversity is high, use BoyerMoore
        int distinct = countDistinctChars(pattern);
        double diversityRatio = (double) distinct / m;
        if (diversityRatio > 0.6) {
            return "BoyerMoore";
        }
        
        // If text entropy is high, use RabinKarp
        double textEntropy = calculateEntropy(text);
        if (textEntropy > 3.5)
            return "RabinKarp";
        else
            return "KMP";
    }
    
    // Compute LPS (Longest Proper Prefix) array
    private int[] computeLPS(String pattern) {
        int m = pattern.length();
        if (m == 0) return new int[0];
        
        int[] lps = new int[m];
        int len = 0;
        int i = 1;
        
        lps[0] = 0;
        
        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        
        return lps;
    }
    
    private int countDistinctChars(String pattern) {
        if (pattern.length() == 0) return 0;
        
        // Find max char value for array size
        int maxChar = 0;
        for (int i = 0; i < pattern.length(); i++) {
            int charVal = (int) pattern.charAt(i);
            if (charVal > maxChar) {
                maxChar = charVal;
            }
        }
        
        // Use boolean array to track seen characters
        boolean[] seen = new boolean[maxChar + 1];
        int distinct = 0;
        
        for (int i = 0; i < pattern.length(); i++) {
            int charVal = (int) pattern.charAt(i);
            if (!seen[charVal]) {
                seen[charVal] = true;
                distinct++;
            }
        }
        
        return distinct;
    }
    
    private double calculateEntropy(String text) {
        if (text.length() == 0) return 0.0;
        
        // Use sample for long texts to keep analysis fast
        String sample;
        int sampleSize = 1000;
        if (text.length() > sampleSize) {
            sample = text.substring(0, sampleSize);
        } else {
            sample = text;
        }
        
        // Count character frequencies
        int maxChar = 0;
        for (int i = 0; i < sample.length(); i++) {
            int charVal = (int) sample.charAt(i);
            if (charVal > maxChar) {
                maxChar = charVal;
            }
        }
        
        int[] freq = new int[maxChar + 1];
        for (int i = 0; i < sample.length(); i++) {
            freq[(int) sample.charAt(i)]++;
        }
        
        // Calculate entropy: H = -Σ(p(x) * log2(p(x)))
        double entropy = 0.0;
        int n = sample.length();
        
        for (int i = 0; i <= maxChar; i++) {
            if (freq[i] > 0) {
                double probability = (double) freq[i] / n;
                entropy -= probability * (Math.log(probability) / Math.log(2.0));
            }
        }
        
        return entropy;
    }
    
    @Override
    public String getStrategyDescription() {
        return "Student Algorithm Selector: Selects based on pattern length, repetition ratio, diversity, and text entropy";
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


