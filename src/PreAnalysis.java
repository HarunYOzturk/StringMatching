
import java.util.HashSet;

/**
 * PreAnalysis interface for students to implement their algorithm selection
 * logic
 *
 * Students should analyze the characteristics of the text and pattern to
 * determine which algorithm would be most efficient for the given input.
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
     * @return The name of the algorithm to use (e.g., "Naive", "KMP",
     * "RabinKarp", "BoyerMoore", "GoCrazy") Return null if you want to skip
     * pre-analysis and run all algorithms
     *
     * Tips for students: - Consider the length of the text and pattern -
     * Consider the characteristics of the pattern (repeating characters, etc.)
     * - Consider the alphabet size - Think about which algorithm performs best
     * in different scenarios
     */
    public abstract String chooseAlgorithm(String text, String pattern);

    /**
     * Get a description of your analysis strategy This will be displayed in the
     * output
     */
    public abstract String getStrategyDescription();
}

/**
 * Default implementation that students should modify This is where students
 * write their pre-analysis logic
 */
class StudentPreAnalysis extends PreAnalysis {

    @Override
    public String chooseAlgorithm(String text, String pattern) {
        int textLen = text.length();
        int patternLen = pattern.length();

        // Edge case empty text or pattern
        if (patternLen == 0 || textLen == 0) {
            return "BoyerMoore";
        }

        // If pattern is longer than the text
        if (patternLen > textLen) {
            return "RabinKarp";
        }

        // Very short patterns or small texts  Naive is usually fastest
        if (patternLen == 1 || textLen < 50) {
            return "Naive";
        }

        // Very short patterns (2–3 chars)  Naive tends to perform well
        if (patternLen <= 3) {
            return "Naive";
        }

        int alphabetSize = guessAlphabetSize(text, pattern);

        // Long texts Boyer-Moore performs well
        if (textLen > 50) {
            return "BoyerMoore";
        }

        // Repetitive patterns KMP or Rabin-Karp
        if (hasRepeatingPrefix(pattern)) {
            // Highly repetitive patterns  Rabin-Karp
            if (isHighlyRepetitive(pattern)) {
                return "RabinKarp";
            }
            return "KMP";
        }

        // Long patterns (15+)  Naive often wins based on test results
        if (patternLen >= 15) {
            return "Naive";
        }

        //Small alphabet cases (ex DNA with 4 chars) KMP is efficient
        if (alphabetSize <= 8) {
            return "KMP";
        }

        // Medium-length patterns  KMP usually performs well
        if (patternLen >= 7 && patternLen < 15) {
            return "KMP";
        }

        // Default case Naive is often fastest for short patterns
        return "Naive";
    }

    private boolean isHighlyRepetitive(String pattern) {
        if (pattern.length() < 3) {
            return false;
        }

        HashSet<Character> uniqueChars = new HashSet<>();
        for (char c : pattern.toCharArray()) {
            uniqueChars.add(c);
        }

        //Highly repetitive unique chars one-third of pattern length
        return uniqueChars.size() < pattern.length() / 3;
    }

    private boolean hasRepeatingPrefix(String pattern) {
        if (pattern.length() < 2) {
            return false;
        }

        //Check if the first character repeats in the first few positions
        char first = pattern.charAt(0);
        int count = 0;
        for (int i = 0; i < Math.min(pattern.length(), 5); i++) {
            if (pattern.charAt(i) == first) {
                count++;
            }
        }
        return count >= 3;
    }

    private int guessAlphabetSize(String text, String pattern) {
        HashSet<Character> alphabet = new HashSet<>();

        // Collect characters from text (stop early)
        for (int i = 0; i < text.length(); i++) {
            alphabet.add(text.charAt(i));
            if (alphabet.size() > 8) {
                break;
            }
        }

        //Add characters from pattern
        for (int i = 0; i < pattern.length(); i++) {
            alphabet.add(pattern.charAt(i));
            if (alphabet.size() > 8) {
                break;
            }
        }

        return alphabet.size();
    }

    @Override
    public String getStrategyDescription() {
        return "The algorithm chooses the most suitable string-search method based on text length, "
                + "pattern length, repetition structure, and alphabet size. Short patterns prefer Naive search, "
                + "long texts favor Boyer-Moore, repetitive patterns are handled with KMP or Rabin-Karp, "
                + "and cases where the pattern exceeds the text use Rabin-Karp.";
    }
}

/**
 * Example implementation showing how pre-analysis could work This is for
 * demonstration purposes
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
        if (pattern.length() < 2) {
            return false;
        }

        // Check if first character repeats
        char first = pattern.charAt(0);
        int count = 0;
        for (int i = 0; i < Math.min(pattern.length(), 5); i++) {
            if (pattern.charAt(i) == first) {
                count++;
            }
        }
        return count >= 3;
    }

    @Override
    public String getStrategyDescription() {
        return "Example strategy: Choose based on pattern length and characteristics";
    }
}

/**
 * Instructor's pre-analysis implementation (for testing purposes only) Students
 * should NOT modify this class
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
