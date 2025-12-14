/**
 * PreAnalysis interface for students to implement their algorithm selection
 * logic
 * 
 * Students should analyze the characteristics of the text and pattern to
 * determine
 * which algorithm would be most efficient for the given input.
 * 
 * The system will automatically use this analysis if the chooseAlgorithm method
 * returns a non-null value.
 */
public abstract class PreAnalysis {

    /**
     * Analyze the text and pattern to choose the best algorithm
     * 
     * @param text    The text to search in
     * @param pattern The pattern to search for
     * @return The name of the algorithm to use (e.g., "Naive", "KMP", "RabinKarp",
     *         "BoyerMoore", "GoCrazy")
     *         Return null if you want to skip pre-analysis and run all algorithms
     * 
     *         Tips for students:
     *         - Consider the length of the text and pattern
     *         - Consider the characteristics of the pattern (repeating characters,
     *         etc.)
     *         - Consider the alphabet size
     *         - Think about which algorithm performs best in different scenarios
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

    // -------------------------------
    // Tunable parameters
    // -------------------------------

    // Early exits for tiny inputs
    private static final int P_THRESHOLD = 4;
    private static final int T_THRESHOLD = 64;

    // Sample up to 8 chars to keep it constant-time
    private static final int SAMPLE_CAP = 8;

    // For sample <= 8, unique buffer only needs 8 slots
    private static final int MAX_UNIQUE = SAMPLE_CAP;

    // Using fractions as NUM/DEN (Numerator/Denominator)
    // borderStrong = 1/2, borderWeak = 1/4
    private static final int BORDER_STRONG_NUM = 1;
    private static final int BORDER_STRONG_DEN = 2;
    private static final int BORDER_WEAK_NUM = 1;
    private static final int BORDER_WEAK_DEN = 4;

    // distinctLow = 3/10 - (0.3), distinctHigh = 8/10 - (0.8)
    private static final int DISTINCT_LOW_NUM = 3;
    private static final int DISTINCT_LOW_DEN = 10;
    private static final int DISTINCT_HIGH_NUM = 4;
    private static final int DISTINCT_HIGH_DEN = 5;

    // runHigh = 1/2 - (0.5), runLow = 1/5 (0.2)
    private static final int RUN_HIGH_NUM = 1;
    private static final int RUN_HIGH_DEN = 2;
    private static final int RUN_LOW_NUM = 1;
    private static final int RUN_LOW_DEN = 5;

    // Minimum text sizes to consider BoyerMoore/RabinKarp
    private static final int BM_TEXT_MIN = 256;
    private static final int RK_TEXT_MIN = 512;
    private static final int RK_PATTERN_MAX = 64;

    // Unicode detection (anything above 255)
    private static final int BYTE_MAX_CHAR = 255;

    // -------------------------------
    // Reused buffers to avoid allocations
    // -------------------------------
    private static final int[] LPS_BUF = new int[SAMPLE_CAP];
    private static final char[] UP_BUF = new char[MAX_UNIQUE];
    private static final char[] UT_BUF = new char[MAX_UNIQUE];

    @Override
    public String chooseAlgorithm(String text, String pattern) {
        final int n = text.length();
        final int m = pattern.length();

        // ------------------------
        // Constant-time decisions
        // ------------------------
        if (m == 0)
            return "Naive";
        if (n == 0)
            return "Naive";
        if (m > n)
            return "Naive";
        if (m == 1)
            return "Naive";
        if (m == n)
            return "Naive";

        // Quick Unicode check on first few chars
        final int scan = Math.min(P_THRESHOLD, Math.max(m, n));
        for (int i = 0; i < scan; i++) {
            if (i < m && pattern.charAt(i) > BYTE_MAX_CHAR)
                return "RabinKarp";
            if (i < n && text.charAt(i) > BYTE_MAX_CHAR)
                return "RabinKarp";
        }

        // If both are tiny, preprocessing overhead isn't worth it
        if (m < P_THRESHOLD && n < T_THRESHOLD)
            return "Naive";

        // -----------------------------------------
        // Phase 1: Sample pattern only (up to SAMPLE_CAP chars)
        // -----------------------------------------
        final int patSample = (m < SAMPLE_CAP) ? m : SAMPLE_CAP;

        int uniquePat = 0;
        int maxRunPat = 1, curRunPat = 0;
        char prevPat = 0;

        int lenBorder = 0;
        int maxBorder = 0;

        for (int i = 0; i < patSample; i++) {
            char cp = pattern.charAt(i);
            if (cp > BYTE_MAX_CHAR)
                return "BoyerMoore"; // Unicode check

            // Count unique chars
            int k = 0;
            while (k < uniquePat && UP_BUF[k] != cp)
                k++;
            if (k == uniquePat && uniquePat < MAX_UNIQUE)
                UP_BUF[uniquePat++] = cp;

            // Track max run length
            if (i == 0) {
                prevPat = cp;
                curRunPat = 1;
                maxRunPat = 1;
            } else {
                if (cp == prevPat)
                    curRunPat++;
                else {
                    prevPat = cp;
                    curRunPat = 1;
                }
                if (curRunPat > maxRunPat)
                    maxRunPat = curRunPat;
            }

            // Build LPS and track max border
            if (i == 0) {
                LPS_BUF[0] = 0;
                lenBorder = 0;
            } else {
                while (lenBorder > 0 && cp != pattern.charAt(lenBorder)) {
                    lenBorder = LPS_BUF[lenBorder - 1];
                }
                if (cp == pattern.charAt(lenBorder))
                    lenBorder++;
                LPS_BUF[i] = lenBorder;
                if (lenBorder > maxBorder)
                    maxBorder = lenBorder;
            }
        }

        // Calculate boolean flags from ratios
        final boolean borderStrong = maxBorder * BORDER_STRONG_DEN >= patSample * BORDER_STRONG_NUM;
        final boolean borderWeak = maxBorder * BORDER_WEAK_DEN <= patSample * BORDER_WEAK_NUM;

        final boolean distinctLow = uniquePat * DISTINCT_LOW_DEN <= patSample * DISTINCT_LOW_NUM;
        final boolean distinctHigh = uniquePat * DISTINCT_HIGH_DEN >= patSample * DISTINCT_HIGH_NUM;

        final boolean runHigh = maxRunPat * RUN_HIGH_DEN >= patSample * RUN_HIGH_NUM;
        final boolean runLow = (patSample < 5) ? (maxRunPat <= 1)
                : (maxRunPat * RUN_LOW_DEN <= patSample * RUN_LOW_NUM);

        // -----------------------------------------
        // Phase 2: Choose KMP based on pattern structure
        // -----------------------------------------
        // KMP wins when pattern has strong borders or is repetitive
        if (borderStrong || (distinctLow && runHigh)) {
            return "KMP";
        }

        // -----------------------------------------
        // Phase 3: Only now consider BoyerMoore/RabinKarp
        // -----------------------------------------

        // If text isn’t huge, don’t waste more analysis — Naive wins a lot on your
        // platform.
        // We already handled KMP-favorable patterns above.
        if (n < BM_TEXT_MIN && n < RK_TEXT_MIN) {
            return "Naive";
        }

        // Sample text only when deciding between BoyerMoore
        final int textSample = (n < SAMPLE_CAP) ? n : SAMPLE_CAP;

        int uniqueTxt = 0;
        int maxRunTxt = 1, curRunTxt = 0;
        char prevTxt = 0;

        for (int i = 0; i < textSample; i++) {
            char ct = text.charAt(i);
            if (ct > BYTE_MAX_CHAR)
                return "BoyerMoore"; // Unicode check

            int k = 0;
            while (k < uniqueTxt && UT_BUF[k] != ct)
                k++;
            if (k == uniqueTxt && uniqueTxt < MAX_UNIQUE)
                UT_BUF[uniqueTxt++] = ct;

            if (i == 0) {
                prevTxt = ct;
                curRunTxt = 1;
                maxRunTxt = 1;
            } else {
                if (ct == prevTxt)
                    curRunTxt++;
                else {
                    prevTxt = ct;
                    curRunTxt = 1;
                }
                if (curRunTxt > maxRunTxt)
                    maxRunTxt = curRunTxt;
            }
        }

        final boolean txtDistinctHigh = uniqueTxt * 2 >= textSample; // >= 0.5
        final boolean txtRunLow = maxRunTxt * RUN_LOW_DEN <= textSample * RUN_LOW_NUM;

        // Boyer-Moore: large text with distinct chars and weak borders
        if (n >= BM_TEXT_MIN && distinctHigh && runLow && borderWeak && txtDistinctHigh && txtRunLow) {
            return "BoyerMoore";
        }

        // Rabin-Karp: very large text with medium pattern size
        if (n >= RK_TEXT_MIN && m <= RK_PATTERN_MAX && distinctHigh && borderWeak) {
            return "RabinKarp";
        }

        // Default
        return "KMP";
    }

    @Override
    public String getStrategyDescription() {
        return "Our preprocessing approach uses a tiered decision tree structure:\n\n" +
                "1) We start with constant-time checks for degenerate cases: empty strings, single character patterns, "
                +
                "or pattern length equals/exceeds text length.\n\n" +
                "2) If we cannot select an algorithm yet, we preprocess the pattern by sampling up to a specified number "
                +
                "of characters to calculate key metrics:\n" +
                "   - maxBorder: detects repetitive patterns like \"ABABABAB\"\n" +
                "   - unique character count: measures character diversity in the pattern\n" +
                "   - max run length: finds repeated characters like \"aaaaa\"\n\n" +
                "3) KMP is chosen when pattern structure shows strong borders or low diversity with high repetition.\n\n"
                +
                "4) If we still cannot select an algorithm, we preprocess the text by sampling up to a specified number \n"
                +
                "of characters to help decide between Boyer-Moore and Rabin-Karp:\n" +
                "   - Boyer-Moore: chosen for large texts with unique characters and weak borders\n" +
                "   - Rabin-Karp: chosen for very large texts not favored by KMP or Boyer-Moore, or when Unicode is detected\n\n"
                +
                "5) We default to Naive for small inputs where preprocessing overhead isn't worth it, and KMP otherwise.\n\n";
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
        if (pattern.length() < 2)
            return false;

        // Check if first character repeats
        char first = pattern.charAt(0);
        int count = 0;
        for (int i = 0; i < Math.min(pattern.length(), 5); i++) {
            if (pattern.charAt(i) == first)
                count++;
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
