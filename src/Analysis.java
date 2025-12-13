import java.util.ArrayList;
import java.util.List;

// Name : Ömer Faruk Başaran
// ID : 21050111041

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
 * Boyer-Moore String Matching Algorithm
 *
 * This algorithm searches from right to left in the pattern, which allows
 * us to skip sections of the text when we find mismatches.
 *
 * Key concept: Bad Character Rule
 * When we find a mismatch, we look at the mismatched character in the text.
 * If this character appears in our pattern, we shift the pattern to align with it.
 * If it doesn't appear, we can skip the entire pattern length.
 *
 * Example: Pattern "EXAMPLE" in text "HERE IS A SIMPLE EXAMPLE"
 * We start comparing from the right side of the pattern.
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
        int textLength = text.length();
        int patternLength = pattern.length();

        // Empty pattern matches at every position
        if (patternLength == 0) {
            for (int i = 0; i <= textLength; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }

        // If pattern is longer than text, no match is possible
        if (patternLength > textLength) {
            return "";
        }

        // Preprocessing: build the bad character table using HashMap for Unicode support
        int[] badCharShift = new int[256];
        for (int i = 0; i < 256; i++) {
            badCharShift[i] = -1;
        }

        // Fill bad character table for ASCII characters
        for (int i = 0; i < patternLength; i++) {
            char c = pattern.charAt(i);
            if (c < 256) {
                badCharShift[c] = i;
            }
        }

        // Start searching
        int currentPosition = 0;

        while (currentPosition <= textLength - patternLength) {
            int patternIndex = patternLength - 1;

            // Compare pattern with text from right to left
            while (patternIndex >= 0 &&
                    pattern.charAt(patternIndex) == text.charAt(currentPosition + patternIndex)) {
                patternIndex--;
            }

            // If we went through entire pattern, we found a match
            if (patternIndex < 0) {
                indices.add(currentPosition);

                // Shift pattern to align with next possible match
                if (currentPosition + patternLength < textLength) {
                    char nextChar = text.charAt(currentPosition + patternLength);
                    int shift = getBadCharShift(badCharShift, nextChar, patternLength, pattern);
                    currentPosition += patternLength - shift;
                } else {
                    currentPosition++;
                }
            } else {
                // Mismatch occurred
                char mismatchedChar = text.charAt(currentPosition + patternIndex);
                int charPosition = getBadCharShift(badCharShift, mismatchedChar, patternLength, pattern);
                int shift = patternIndex - charPosition;

                // Always shift at least 1 position forward
                currentPosition += Math.max(1, shift);
            }
        }

        return indicesToString(indices);
    }

    /**
     * Get the bad character shift value for a given character.
     * For ASCII characters, use the precomputed table.
     * For Unicode characters, search the pattern directly.
     *
     * @param badCharShift Precomputed bad character table for ASCII
     * @param c The character to look up
     * @param patternLength Length of the pattern
     * @param pattern The pattern string
     * @return The rightmost position of the character in pattern, or -1 if not found
     */
    private int getBadCharShift(int[] badCharShift, char c, int patternLength, String pattern) {
        // For ASCII characters, use the precomputed table
        if (c < 256) {
            return badCharShift[c];
        }

        // For Unicode characters, search the pattern
        for (int i = patternLength - 1; i >= 0; i--) {
            if (pattern.charAt(i) == c) {
                return i;
            }
        }

        return -1;
    }
}

/**
 * GoCrazy - Hybrid String Matching Algorithm
 *
 * Strategy: This algorithm combines multiple techniques based on pattern analysis.
 * It uses a "first-last character filter" approach where we first check if the
 * first and last characters of the pattern match before checking the middle.
 * This reduces unnecessary comparisons.
 *
 * For patterns with unique characteristics, it switches between different methods:
 * - Single char: Direct scan
 * - Short patterns (2-4): First-last filter with naive
 * - Medium patterns (5-10): Hybrid approach with skip optimization
 * - Long patterns (>10): Modified Boyer-Moore with additional filtering
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
        int n = text.length();
        int m = pattern.length();

        // Empty pattern matches everywhere
        if (m == 0) {
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }

        // Pattern longer than text
        if (m > n) {
            return "";
        }

        // Choose strategy based on pattern length
        if (m == 1) {
            return singleCharSearch(text, pattern.charAt(0));
        } else if (m <= 4) {
            return firstLastFilter(text, pattern);
        } else if (m <= 10) {
            return hybridSearch(text, pattern);
        } else {
            return smartBoyerMoore(text, pattern);
        }
    }

    /**
     * Single character search - just scan through text
     */
    private String singleCharSearch(String text, char target) {
        List<Integer> indices = new ArrayList<>();

        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == target) {
                indices.add(i);
            }
        }

        return indicesToString(indices);
    }

    /**
     * First-Last Filter approach for short patterns.
     *
     * Key idea: Before checking the entire pattern, first verify that both
     * the first and last characters match. This eliminates many false starts.
     *
     * Example: Pattern "xyz" in text "abcxyzdef"
     * - At position 0: 'a' != 'x', skip
     * - At position 3: 'x' == 'x' AND 'z' == 'z', now check middle
     */
    private String firstLastFilter(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        char firstChar = pattern.charAt(0);
        char lastChar = pattern.charAt(m - 1);

        for (int i = 0; i <= n - m; i++) {
            // Quick filter: check first and last characters
            if (text.charAt(i) == firstChar && text.charAt(i + m - 1) == lastChar) {
                // Now check the middle characters
                boolean match = true;
                for (int j = 1; j < m - 1; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    indices.add(i);
                }
            }
        }

        return indicesToString(indices);
    }

    /**
     * Hybrid search for medium-length patterns.
     *
     * This combines the first-last filter with smart skipping.
     * When we find a mismatch, we calculate how far we can safely skip ahead.
     *
     * Example: Pattern "hello" in text "abchello"
     * - If we mismatch at position 0, we can skip ahead based on where
     *   the first character 'h' appears in the mismatched section.
     */
    private String hybridSearch(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        char firstChar = pattern.charAt(0);
        char lastChar = pattern.charAt(m - 1);

        int i = 0;
        while (i <= n - m) {
            // Check first and last
            if (text.charAt(i) == firstChar && text.charAt(i + m - 1) == lastChar) {
                // Check middle
                int j = 1;
                while (j < m - 1 && text.charAt(i + j) == pattern.charAt(j)) {
                    j++;
                }

                if (j == m - 1) {
                    indices.add(i);
                    i++;
                } else {
                    // Mismatch found, try to skip intelligently
                    i += calculateSkip(text, pattern, i, j);
                }
            } else {
                i++;
            }
        }

        return indicesToString(indices);
    }

    /**
     * Calculate how many positions we can skip after a mismatch.
     *
     * Logic: If we mismatched at position j in the pattern, we look at
     * the character in text that caused the mismatch and see if it appears
     * earlier in the pattern. If it does, we align to that position.
     * If it doesn't, we can skip past the entire pattern.
     */
    private int calculateSkip(String text, String pattern, int textPos, int mismatchPos) {
        if (textPos + mismatchPos >= text.length()) {
            return 1;
        }

        char mismatchChar = text.charAt(textPos + mismatchPos);

        // Look for this character in the pattern before mismatch position
        for (int k = mismatchPos - 1; k >= 0; k--) {
            if (pattern.charAt(k) == mismatchChar) {
                return mismatchPos - k;
            }
        }

        // Character not found, skip by mismatch position + 1
        return mismatchPos + 1;
    }

    /**
     * Smart Boyer-Moore for long patterns.
     *
     * This is Boyer-Moore but with an additional optimization:
     * I have also checked the middle character of the pattern first as an extra filter.
     *
     * Why did i do that? For long patterns, checking the middle character along with the
     * last character gives us better filtering before we do the full comparison.
     */
    private String smartBoyerMoore(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        // Build bad character table
        int[] badChar = new int[256];
        for (int i = 0; i < 256; i++) {
            badChar[i] = -1;
        }
        for (int i = 0; i < m; i++) {
            badChar[pattern.charAt(i)] = i;
        }

        char middleChar = pattern.charAt(m / 2);

        int shift = 0;
        while (shift <= n - m) {
            // Extra filter: check middle character first
            if (text.charAt(shift + m / 2) != middleChar) {
                shift++;
                continue;
            }

            // Now do Boyer-Moore from right to left
            int j = m - 1;
            while (j >= 0 && pattern.charAt(j) == text.charAt(shift + j)) {
                j--;
            }

            if (j < 0) {
                // Match found
                indices.add(shift);
                shift += (shift + m < n) ? m - badChar[text.charAt(shift + m)] : 1;
            } else {
                // Mismatch, use bad character rule
                shift += Math.max(1, j - badChar[text.charAt(shift + j)]);
            }
        }

        return indicesToString(indices);
    }
}



