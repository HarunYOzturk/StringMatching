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
 * Boyer-Moore string matching algorithm.
 *
 * Uses two heuristics:
 *  - Bad character rule
 *  - Good suffix rule
 *
 * This implementation:
 *  - Handles empty pattern (matches at every position, consistent
 *    with the other algorithms in this project)
 *  - Handles pattern longer than text (no matches)
 *  - Finds overlapping matches correctly
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
        List<Integer> indices = new ArrayList<>();

        int n = text.length();
        int m = pattern.length();

        // Empty pattern: by convention, match at every index
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }

        // Pattern longer than text: no matches
        if (m > n) {
            return "";
        }

        final int ALPHABET_SIZE = 256;

        // Bad character table: for each character c,
        // badChar[c] = rightmost index of c in pattern (or -1 if absent)
        int[] badChar = new int[ALPHABET_SIZE];
        preprocessBadChar(pattern, badChar);

        // Good suffix tables:
        //  suffix[k] = starting index of a substring in pattern that matches
        //              the suffix of length k (or -1 if none)
        //  prefix[k] = true if the suffix of length k is also a prefix
        int[] suffix = new int[m];
        boolean[] prefix = new boolean[m];
        preprocessGoodSuffix(pattern, suffix, prefix);

        int shift = 0; // alignment of pattern over text

        while (shift <= n - m) {
            int j;

            // Compare from right to left
            for (j = m - 1; j >= 0; j--) {
                if (pattern.charAt(j) != text.charAt(shift + j)) {
                    break; // mismatch at position j
                }
            }

            if (j < 0) {
                // Full match found at position "shift"
                indices.add(shift);

                // After a full match, use the longest prefix that is also
                // a suffix to decide the shift. If none exists, shift by m.
                int move = moveAfterFullMatch(m, prefix);
                shift += move;
            } else {
                // Mismatch at position j: combine both heuristics

                // 1) Bad character rule: align the last occurrence of text[shift+j]
                //    in the pattern with position j.
                char bad = text.charAt(shift + j);
                int lastOcc = badChar[bad & 0xFF]; // -1 if not present
                int bcShift = j - lastOcc;
                if (bcShift < 1) {
                    bcShift = 1;
                }

                // 2) Good suffix rule: use the matched suffix (length k)
                int gsShift = moveByGoodSuffix(j, m, suffix, prefix);

                // Take the maximum shift suggested by both rules
                shift += Math.max(bcShift, gsShift);
            }
        }

        return indicesToString(indices);
    }

    /**
     * Builds the bad character table.
     * For each possible character code c in [0, ALPHABET_SIZE),
     * badChar[c] is the rightmost index of c in the pattern, or -1 if absent.
     */
    private void preprocessBadChar(String pattern, int[] badChar) {
        final int ALPHABET_SIZE = badChar.length;

        // Default: character does not appear
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            badChar[i] = -1;
        }

        // Record rightmost occurrences
        for (int i = 0; i < pattern.length(); i++) {
            badChar[pattern.charAt(i) & 0xFF] = i;
        }
    }

    /**
     * Builds the good suffix auxiliary tables:
     *
     * suffix[k] = starting index of the substring that matches
     *             the suffix of length k (k > 0), or -1 if none.
     * prefix[k] = true if there is a suffix of length k which is also
     *             a prefix of the pattern.
     */
    private void preprocessGoodSuffix(String pattern, int[] suffix, boolean[] prefix) {
        int m = pattern.length();

        // Initialize
        for (int i = 0; i < m; i++) {
            suffix[i] = -1;
            prefix[i] = false;
        }

        // For each position i, consider pattern[0..i] and find common suffixes
        for (int i = 0; i < m - 1; i++) {
            int j = i;
            int k = 0; // length of current matching suffix

            // Compare backwards: pattern[j], pattern[m - 1 - k]
            while (j >= 0 && pattern.charAt(j) == pattern.charAt(m - 1 - k)) {
                j--;
                k++;
                // Found a substring (j+1 .. j+k) that matches suffix of length k
                suffix[k] = j + 1;
            }

            // If we matched all the way to the beginning, then the suffix
            // of length k is also a prefix.
            if (j == -1 && k > 0) {
                prefix[k] = true;
            }
        }
    }

    /**
     * Computes the good suffix shift for a mismatch at position j.
     *
     * Let k = length of the matched suffix = m - 1 - j.
     *
     * 1) If there exists another substring in the pattern equal to this
     *    suffix (suffix[k] != -1), shift so that this substring aligns
     *    with the current suffix.
     * 2) Otherwise, find the longest suffix of the matched suffix that
     *    is also a prefix of the pattern, and shift accordingly.
     * 3) If none of the above applies, shift by m (no useful suffix).
     */
    private int moveByGoodSuffix(int j, int m, int[] suffix, boolean[] prefix) {
        int k = m - 1 - j; // length of matched suffix

        if (k <= 0) {
            // No suffix matched, good suffix rule does not help
            return 0;
        }

        // Case 1: there is another occurrence of this suffix inside the pattern
        if (suffix[k] != -1) {
            return j - suffix[k] + 1;
        }

        // Case 2: find the longest suffix of the matched suffix which is also a prefix
        for (int r = j + 2; r <= m - 1; r++) {
            if (prefix[m - r]) {
                return r;
            }
        }

        // Case 3: no border, shift full pattern length
        return m;
    }

    /**
     * After a full match, shift pattern so that the longest
     * border (prefix that is also a suffix) aligns with the
     * suffix we just matched. If there is no such border,
     * shift by m.
     */
    private int moveAfterFullMatch(int m, boolean[] prefix) {
        // Try borders of length k from largest to smallest
        for (int k = m - 1; k >= 1; k--) {
            if (prefix[k]) {
                return m - k;
            }
        }
        return m;
    }
}


/**
 * GoCrazy
 **/
class GoCrazy extends Solution {
    // Register this class in the global list of algorithms (used by the framework)
    static {
        SUBCLASSES.add(GoCrazy.class);
        System.out.println("GoCrazy registered");
    }

    public GoCrazy() {
    }

    @Override
    public String Solve(String text, String pattern) {
        // List to store starting indices of all matches
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        // Edge case: empty pattern matches at every position (including after the last character)
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }

        // If pattern is longer than text, no match is possible
        if (m > n) {
            return "";
        }

        // We will use a “shift” table similar to the Sunday algorithm:
        // It tells us how far we can jump when the current alignment fails.
        final int ALPHABET_SIZE = 256;
        int[] shift = new int[ALPHABET_SIZE];

        // Default shift: if the character does not appear in the pattern,
        // skip the whole pattern length + 1 (look at the next window after the current alignment).
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            shift[i] = m + 1;
        }

        // For characters that appear in the pattern, set the shift based on their position.
        // Characters closer to the end of the pattern cause smaller shifts.
        for (int i = 0; i < m; i++) {
            shift[pattern.charAt(i) & 0xFF] = m - i;
        }

        // Current alignment position in the text
        int pos = 0;

        // Slide the pattern over the text while there is enough space left
        while (pos <= n - m) {
            int j = 0;

            // Compare pattern and text from left to right at this alignment
            while (j < m && text.charAt(pos + j) == pattern.charAt(j)) {
                j++;
            }

            // If we compared all characters successfully, we found a match
            if (j == m) {
                indices.add(pos);
            }

            // If there is no next character to look at, we are done
            if (pos + m >= n) {
                break;
            }

            // Look at the character right after the current window and decide how far to shift
            char nextChar = text.charAt(pos + m);
            pos += shift[nextChar & 0xFF];
        }

        // Convert list of indices to the expected comma-separated format
        return indicesToString(indices);
    }
}

