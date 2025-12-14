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

class StudentPreAnalysis extends PreAnalysis {

  @Override
  public String chooseAlgorithm(String text, String pattern) {
    int n = text.length();
    int m = pattern.length();

    // 1. Trivial and degenerate cases -> Naive
    if (m == 0)
      return "Naive"; // empty pattern
    if (n == 0)
      return "Naive"; // empty text
    if (m > n)
      return "Naive"; // pattern longer than text

    // 2. Very short patterns -> Naive (as in ExamplePreAnalysis)
    if (n <= 10 || m <= 4) {
      return "Naive";
    }

    // 3. Check for KMP-advantage patterns (cheap check, similar to Example's
    // hasRepeatingPrefix)
    // We'll make this a bit more robust than just prefix, but still very cheap.
    if (isKMPAdvantagePattern(pattern)) {
      return "KMP";
    }

    if (n >= 512 && m >= 8) {
      return "Boyer-Moore";
    }

    // 4. Default to Naive for practical simplification
    return "Naive";
  }

  /**
   * A very cheap check for patterns where KMP might have an advantage.
   */
  private boolean isKMPAdvantagePattern(String pattern) {
    int m = pattern.length();
    if (m < 4)
      return false; // Too short for meaningful repetition analysis

    // Heuristic 1: Check for repeating prefix (similar to Example)
    char first = pattern.charAt(0);
    int prefixRepeatCount = 0;
    for (int i = 0; i < Math.min(m, 5); i++) { // Check first few chars
      if (pattern.charAt(i) == first) {
        prefixRepeatCount++;
      }
    }
    if (prefixRepeatCount >= 3)
      return true; // e.g., "aaaa...", "ababa..."

    // Heuristic 2: Check for very low character variety in the first few characters
    // (a proxy for overall low variety without using a HashSet)
    // If the first 5 chars have only 1 or 2 unique characters, it's likely
    // repetitive.
    if (m >= 5) {
      char c1 = pattern.charAt(0);
      char c2 = 0; // Placeholder for second unique char
      int uniqueCount = 1;
      for (int i = 1; i < Math.min(m, 5); i++) {
        char current = pattern.charAt(i);
        if (current != c1 && (c2 == 0 || current != c2)) {
          if (c2 == 0) {
            c2 = current;
            uniqueCount++;
          } else {
            uniqueCount++; // Found a third unique char
            break;
          }
        }
      }
      if (uniqueCount <= 2)
        return true; // e.g., "aaaaa", "ababab", "ababa"
    }

    // Heuristic 3: Check for a very long run of the same character at the beginning
    // (e.g., "aaaaaaB")
    int maxRun = 1;
    int currentRun = 1;
    for (int i = 1; i < Math.min(m, 8); i++) { // Check first few chars for runs
      if (pattern.charAt(i) == pattern.charAt(i - 1)) {
        currentRun++;
      } else {
        currentRun = 1;
      }
      if (currentRun > maxRun) {
        maxRun = currentRun;
      }
    }
    if (maxRun >= Math.min(m, 5))
      return true; // If a significant portion of the start is a run

    return false;
  }

  @Override
  public String getStrategyDescription() {
    return "Naive for trivial or very short patterns/small texts, "
        + "KMP for patterns with cheap-to-detect repetitive characteristics, "
        + "BoyerMoore for sufficiently long texts and patterns, "
        + "and Naive as the general default for other cases.";
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
