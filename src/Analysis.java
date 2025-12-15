import java.util.ArrayList;
import java.util.List;

class Naive extends Solution {
    static {
        SUBCLASSES.add(Naive.class);
        System.out.println("Naive registered");
    }

    public Naive() {
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        for (int i = 0; i <= n - m; i++) {
            int j;
            for (j = 0; j < m; j++) {
                if (text.charAt(i + j) != pattern.charAt(j)) {
                    break;
                }
            }
            if (j == m) {
                indices.add(i);
            }
        }

        return indicesToString(indices);
    }
}

class KMP extends Solution {
    static {
        SUBCLASSES.add(KMP.class);
        System.out.println("KMP registered");
    }

    public KMP() {
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        // Handle empty pattern - matches at every position
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }

        // Compute LPS (Longest Proper Prefix which is also Suffix) array
        int[] lps = computeLPS(pattern);

        int i = 0; // index for text
        int j = 0; // index for pattern

        while (i < n) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
            }

            if (j == m) {
                indices.add(i - j);
                j = lps[j - 1];
            } else if (i < n && text.charAt(i) != pattern.charAt(j)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        return indicesToString(indices);
    }

    private int[] computeLPS(String pattern) {
        int m = pattern.length();
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
}

class RabinKarp extends Solution {
    static {
        SUBCLASSES.add(RabinKarp.class);
        System.out.println("RabinKarp registered.");
    }

    public RabinKarp() {
    }

    private static final int PRIME = 101; // A prime number for hashing

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        // Handle empty pattern - matches at every position
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }

        if (m > n) {
            return "";
        }

        int d = 256; // Number of characters in the input alphabet
        long patternHash = 0;
        long textHash = 0;
        long h = 1;

        // Calculate h = d^(m-1) % PRIME
        for (int i = 0; i < m - 1; i++) {
            h = (h * d) % PRIME;
        }

        // Calculate hash value for pattern and first window of text
        for (int i = 0; i < m; i++) {
            patternHash = (d * patternHash + pattern.charAt(i)) % PRIME;
            textHash = (d * textHash + text.charAt(i)) % PRIME;
        }

        // Slide the pattern over text one by one
        for (int i = 0; i <= n - m; i++) {
            // Check if hash values match
            if (patternHash == textHash) {
                // Check characters one by one
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    indices.add(i);
                }
            }

            // Calculate hash value for next window
            if (i < n - m) {
                textHash = (d * (textHash - text.charAt(i) * h) + text.charAt(i + m)) % PRIME;

                // Convert negative hash to positive
                if (textHash < 0) {
                    textHash = textHash + PRIME;
                }
            }
        }

        return indicesToString(indices);
    }
}

/**
 * Boyer-Moore algorithm implementation.
 * Uses Bad Character and Good Suffix heuristics.
 */
class BoyerMoore extends Solution {
    static {
        SUBCLASSES.add(BoyerMoore.class);
        System.out.println("BoyerMoore registered");
    }

    public BoyerMoore() {
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = boyerMooreSearch(text, pattern);
        return formatIndices(indices);
    }

    /**
     * Helper method to convert list of indices to comma-separated string.
     * @param indices List of indices
     * @return Comma-separated string
     */
    public static String formatIndices(List<Integer> indices) {
        if (indices.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indices.size(); i++) {
            sb.append(indices.get(i));
            if (i < indices.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    /**
     * Boyer-Moore search algorithm.
     * @param text The text to search in
     * @param pattern The pattern to search for
     * @return List of starting indices
     */
    public static List<Integer> boyerMooreSearch(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        // Handle empty pattern: match at every position
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                matches.add(i);
            }
            return matches;
        }

        // Handle pattern longer than text
        if (m > n) {
            return matches;
        }

        // Preprocessing
        // Bad Character Heuristic
        // Use int array for ASCII/Latin1 and Map for other Unicode characters
        int[] badCharTable = new int[256];
        java.util.Map<Integer, Integer> badCharMap = new java.util.HashMap<>();
        preprocessBadCharacter(pattern, badCharTable, badCharMap);

        // Good Suffix Heuristic
        int[] goodSuffixTable = preprocessGoodSuffix(pattern);

        // Main Search Loop
        int i = 0; // Alignment of pattern relative to text
        while (i <= n - m) {
            int j = m - 1;

            // Scan right-to-left
            while (j >= 0 && pattern.charAt(j) == text.charAt(i + j)) {
                j--;
            }

            if (j < 0) {
                // Match found
                matches.add(i);
                // Shift so next occurrence aligns with longest proper suffix that is a prefix
                // goodSuffixTable[0] holds the shift amount for a full match
                i += goodSuffixTable[0];
            } else {
                // Mismatch at index j
                char badChar = text.charAt(i + j);
                
                // Calculate Bad Character Shift
                int lastOccurrence;
                if (badChar < 256) {
                    lastOccurrence = badCharTable[badChar];
                } else {
                    lastOccurrence = badCharMap.getOrDefault((int)badChar, -1);
                }
                // We want to align badChar in text with its last occurrence in pattern.
                // Shift = j - lastOccurrence.
                int badCharShift = j - lastOccurrence;

                // Calculate Good Suffix Shift
                int goodSuffixShift = goodSuffixTable[j + 1];

                // Take the maximum of the two shifts
                i += Math.max(badCharShift, goodSuffixShift);
            }
        }

        return matches;
    }

    /**
     * Preprocesses the pattern for the Bad Character rule.
     */
    private static void preprocessBadCharacter(String pattern, int[] table, java.util.Map<Integer, Integer> map) {
        int m = pattern.length();
        // Initialize table with -1
        java.util.Arrays.fill(table, -1);
        
        for (int i = 0; i < m; i++) {
            char c = pattern.charAt(i);
            if (c < 256) {
                table[c] = i;
            } else {
                map.put((int)c, i);
            }
        }
    }

    /**
     * Preprocesses the pattern for the Good Suffix rule.
     * Computes the shift array in linear time.
     */
    private static int[] preprocessGoodSuffix(String pattern) {
        int m = pattern.length();
        int[] bpos = new int[m + 1];
        int[] shift = new int[m + 1];

        // Initialize shift array
        java.util.Arrays.fill(shift, 0);

        // Step 1: Compute bpos (border position) array
        int i = m;
        int j = m + 1;
        bpos[i] = j;

        while (i > 0) {
            while (j <= m && pattern.charAt(i - 1) != pattern.charAt(j - 1)) {
                if (shift[j] == 0) {
                    shift[j] = j - i;
                }
                j = bpos[j];
            }
            i--;
            j--;
            bpos[i] = j;
        }

        // Step 2: Handle case where suffix matches a prefix
        j = bpos[0];
        for (int k = 0; k <= m; k++) {
            if (shift[k] == 0) {
                shift[k] = j;
            }
            if (k == j) {
                j = bpos[j];
            }
        }

        return shift;
    }
}

/**
 * GoCrazy: A hybrid meta-algorithm that analyzes input characteristics
 * to select the optimal string matching strategy.
 */
class GoCrazy extends Solution {
    static {
        SUBCLASSES.add(GoCrazy.class);
        System.out.println("GoCrazy registered");
    }

    public GoCrazy() {
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = goCrazySearch(text, pattern);
        return indicesToString(indices);
    }

    /**
     * Deterministic hybrid search that selects the best algorithm based on input features.
     * 
     * Decision Logic:
     * 1. Empty/Short Pattern -> Naive (Lowest overhead)
     * 2. Binary Data -> Rabin-Karp (Robust hashing)
     * 3. High Repetition/Overlap -> Rabin-Karp (Proven winner in stress tests)
     * 4. Low Match Density & Long Pattern -> Boyer-Moore (Best skip performance)
     * 5. Periodic/Torture Cases -> KMP (Linear guarantee)
     * 6. Default -> Boyer-Moore (General purpose winner)
     */
    public static List<Integer> goCrazySearch(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();

        // 1. Edge Case: Empty Pattern
        if (m == 0) {
            List<Integer> all = new ArrayList<>();
            for (int i = 0; i <= n; i++) all.add(i);
            return all;
        }

        // 2. Heuristic: Single Character -> Naive
        // KMP is good but Naive is competitive and has less overhead
        if (m == 1) {
            return runNaive(text, pattern);
        }

        // 3. Heuristic: Very Long Text -> Boyer-Moore
        // For large texts, the skip benefit of BM outweighs setup costs even for short patterns
        if (n > 500 && m > 2) {
            return BoyerMoore.boyerMooreSearch(text, pattern);
        }

        // 4. Heuristic: Short/Medium Patterns (m <= 10)
        // Naive is usually fastest due to low overhead, UNLESS there is high repetition
        // which triggers worst-case O(nm) behavior (e.g. "AAAAAAB" in "AAAA...")
        if (m <= 10) {
            // Check for potential repetition (e.g. "AAA...")
            boolean potentialRepetition = false;
            if (m > 2 && pattern.charAt(0) == pattern.charAt(1) && pattern.charAt(1) == pattern.charAt(2)) {
                potentialRepetition = true;
            }

            if (potentialRepetition) {
                double repeatScore = calculatePatternRepeatScore(pattern);
                if (repeatScore > 0.5) {
                    // High repetition detected
                    int uniqueChars = countUniqueChars(pattern);
                    if (uniqueChars == 1) {
                        // "All Same Character" case -> KMP is most robust
                        return runKMP(text, pattern);
                    } else {
                        // "Worst Case for Naive" case (e.g. "AAAAAAB") -> Boyer-Moore handles this well
                        return BoyerMoore.boyerMooreSearch(text, pattern);
                    }
                }
            }
            // No high repetition -> Naive is fastest
            return runNaive(text, pattern);
        }

        // Feature Extraction for remaining cases
        boolean isBinary = isBinaryData(text, 1000);
        
        // 5. Heuristic: Binary Data -> Rabin-Karp
        if (isBinary) {
            return runRabinKarp(text, pattern);
        }

        // 6. Default -> Boyer-Moore
        // For m > 10, BM is generally the winner
        return BoyerMoore.boyerMooreSearch(text, pattern);
    }

    // --- Wrappers for existing algorithms to return List<Integer> ---
    
    private static List<Integer> runNaive(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();
        for (int i = 0; i <= n - m; i++) {
            int j;
            for (j = 0; j < m; j++) {
                if (text.charAt(i + j) != pattern.charAt(j)) break;
            }
            if (j == m) indices.add(i);
        }
        return indices;
    }

    private static List<Integer> runKMP(String text, String pattern) {
        // Re-implementing core KMP logic to return List<Integer> directly
        // as the KMP class only exposes String Solve().
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();
        if (m == 0) return indices; // Handled in main
        
        int[] lps = computeLPS(pattern);
        int i = 0, j = 0;
        while (i < n) {
            if (pattern.charAt(j) == text.charAt(i)) {
                j++; i++;
            }
            if (j == m) {
                indices.add(i - j);
                j = lps[j - 1];
            } else if (i < n && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0) j = lps[j - 1];
                else i++;
            }
        }
        return indices;
    }

    private static List<Integer> runRabinKarp(String text, String pattern) {
        // Re-implementing core RK logic to return List<Integer> directly
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();
        int prime = 101;
        int d = 256;
        long pHash = 0;
        long tHash = 0;
        long h = 1;

        for (int i = 0; i < m - 1; i++) h = (h * d) % prime;

        for (int i = 0; i < m; i++) {
            pHash = (d * pHash + pattern.charAt(i)) % prime;
            tHash = (d * tHash + text.charAt(i)) % prime;
        }

        for (int i = 0; i <= n - m; i++) {
            if (pHash == tHash) {
                // Verification step
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) indices.add(i);
            }
            if (i < n - m) {
                tHash = (d * (tHash - text.charAt(i) * h) + text.charAt(i + m)) % prime;
                if (tHash < 0) tHash = (tHash + prime);
            }
        }
        return indices;
    }

    // --- Feature Extraction Helpers ---

    private static boolean isBinaryData(String text, int limit) {
        int checkLen = Math.min(text.length(), limit);
        int nonPrintable = 0;
        for (int i = 0; i < checkLen; i++) {
            char c = text.charAt(i);
            // Check for control chars excluding common whitespace
            if (c < 32 && c != '\t' && c != '\n' && c != '\r') {
                nonPrintable++;
            }
        }
        // Threshold: > 10% non-printable characters
        return (double) nonPrintable / checkLen > 0.1;
    }

    private static int[] computeLPS(String pattern) {
        int m = pattern.length();
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
                if (len != 0) len = lps[len - 1];
                else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    private static double calculatePatternRepeatScore(String pattern) {
        if (pattern.isEmpty()) return 0.0;
        int[] lps = computeLPS(pattern);
        int maxLps = 0;
        for (int val : lps) maxLps = Math.max(maxLps, val);
        // Score is ratio of longest proper prefix/suffix to length
        return (double) maxLps / pattern.length();
    }

    private static int countUniqueChars(String pattern) {
        boolean[] seen = new boolean[256]; // Simple ASCII assumption for speed
        int count = 0;
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (c < 256 && !seen[c]) {
                seen[c] = true;
                count++;
            }
        }
        return count == 0 ? 1 : count; // Fallback for all non-ascii
    }
}

