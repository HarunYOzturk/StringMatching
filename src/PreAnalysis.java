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

    public abstract String chooseAlgorithm(String text, String pattern);

    public abstract String getStrategyDescription();
}


/**
 * Default implementation that students should modify
 * This is where students write their pre-analysis logic
 */
class StudentPreAnalysis extends PreAnalysis {

    @Override
    public String chooseAlgorithm(String text, String pattern) {

        int n = text.length();
        int m = pattern.length();

        // Empty pattern: in our implementations it matches everywhere; choose the cheapest.
        if (m == 0) return "Naive";

        // Very small input: overhead is not worth it
        if (n <= 32 || m <= 2) {
            return "Naive";
        }

        int alphaText = alphabetSize(text);
        int alphaPattern = alphabetSize(pattern);

        // Pattern periodicity via LPS (KMP preprocessing)
        int[] lps = computeLPS(pattern);
        int borderLen = lps[m - 1];
        double borderRatio = borderLen / (double) m;

        int distinctP = alphaPattern;
        double diversityP = distinctP / (double) m;

        double runRatio = runRatio(text);
        double lastCharFreq = lastCharFrequency(text, pattern.charAt(m - 1));

        // A) Highly periodic / low diversity pattern -> KMP usually wins
        if (borderRatio >= 0.5 || diversityP < 0.4) {
            return "KMP";
        }

        // B) Large text + large alphabet + diverse pattern -> BoyerMoore is strong
        if (n > 2000 && alphaText > 30 && diversityP > 0.7) {
            return "BoyerMoore";
        }

        // C) If the last pattern char is rare in text -> BM can skip a lot
        if (m >= 5 && lastCharFreq < 0.05 && alphaText > 20) {
            return "BoyerMoore";
        }

        // D) Very long + very repetitive text -> rolling hash / RK can be OK
        if (n > 5000 && runRatio < 0.3) {
            return "RabinKarp";
        }

        // E) For long-ish texts, use the hybrid strategy
        if (n > 3000) {
            return "GoCrazy";
        }

        // F) Medium case: prefer BM when alphabet is not tiny
        if (m >= 4 && alphaText > 15) {
            return "BoyerMoore";
        }

        // Default
        return "GoCrazy";
    }

    @Override
    public String getStrategyDescription() {
        return "Heuristic selection based on pattern periodicity (LPS), alphabet size, diversity, run ratio, and last-character frequency.";
    }

    // ------------------- HELPER FUNCTIONS -------------------

    // Unicode-safe alphabet size count (small array for ASCII, upgrade if needed)
    private int alphabetSize(String s) {
        boolean[] seen = new boolean[256];
        int cnt = 0;
        boolean upgraded = false;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (!upgraded && c >= 256) {
                boolean[] big = new boolean[Character.MAX_VALUE + 1]; // 65536
                System.arraycopy(seen, 0, big, 0, seen.length);
                seen = big;
                upgraded = true;
            }

            if (!seen[c]) {
                seen[c] = true;
                cnt++;
            }
        }
        return cnt;
    }

    private int[] computeLPS(String p) {
        int m = p.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;

        while (i < m) {
            if (p.charAt(i) == p.charAt(len)) {
                lps[i++] = ++len;
            } else {
                if (len != 0) len = lps[len - 1];
                else lps[i++] = 0;
            }
        }
        return lps;
    }

    private double runRatio(String text) {
        if (text.length() <= 1) return 1.0;
        int runs = 1;
        for (int i = 1; i < text.length(); i++) {
            if (text.charAt(i) != text.charAt(i - 1)) {
                runs++;
            }
        }
        return runs / (double) text.length();
    }

    private double lastCharFrequency(String text, char ch) {
        if (text.isEmpty()) return 0.0;
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == ch) count++;
        }
        return count / (double) text.length();
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

        if (patternLen <= 3) {
            return "Naive";
        } else if (hasRepeatingPrefix(pattern)) {
            return "KMP";
        } else if (patternLen > 10 && textLen > 1000) {
            return "RabinKarp";
        } else {
            return "Naive";
        }
    }

    private boolean hasRepeatingPrefix(String pattern) {
        if (pattern.length() < 2) return false;

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
 */
class InstructorPreAnalysis extends PreAnalysis {

    @Override
    public String chooseAlgorithm(String text, String pattern) {
        return null;
    }

    @Override
    public String getStrategyDescription() {
        return "Instructor's testing implementation";
    }
}