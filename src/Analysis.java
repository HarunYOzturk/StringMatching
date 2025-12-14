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
 * TODO: Implement Boyer-Moore algorithm
 * This is a homework assignment for students
 */
class BoyerMoore extends Solution {
    static {
        SUBCLASSES.add(BoyerMoore.class);
        System.out.println("BoyerMoore registered.");
    }

    public BoyerMoore() {
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int m = pattern.length();
        int n = text.length();

        // If there is no pattern
        if (m == 0) {
            for (int i = 0; i <= n; i++) indices.add(i);
            return indicesToString(indices);
        }

        if (m > n) return "";

        // Bad Character Heuristic (Hybrid: Array for ASCII + Map for Unicode)
        int[] badCharTable = new int[256];
        java.util.Map<Character, Integer> badCharMap = null;

        // Initialize array with -1
        java.util.Arrays.fill(badCharTable, -1);

        boolean hasUnicode = false;
        for (int i = 0; i < m; i++) {
            char c = pattern.charAt(i);
            if (c < 256) {
                badCharTable[c] = i;
            } else {
                if (badCharMap == null) badCharMap = new java.util.HashMap<>();
                badCharMap.put(c, i);
                hasUnicode = true;
            }
        }

        // Good Suffix Heuristic
        int[] goodSuffixTable = preprocessGoodSuffix(pattern);

        int shift = 0;

        while (shift <= (n - m)) {
            int j = m - 1;

            // Keep reducing index j of pattern while characters of pattern and text are matching
            while (j >= 0 && pattern.charAt(j) == text.charAt(shift + j))  j--;

            if (j < 0) {
                // Match found
                indices.add(shift);

                // Shifting the pattern so that the next character in text aligns
                if (shift + m < n)
                    shift += goodSuffixTable[0];
                 else
                    shift += 1;

            } else {
                // Mismatch at index j
                char charInText = text.charAt(shift + j);

                // Get Bad Character shift
                int badCharValue;
                if (charInText < 256) {
                    badCharValue = badCharTable[charInText];
                } else {
                    badCharValue = (badCharMap != null) ? badCharMap.getOrDefault(charInText, -1) : -1;
                }

                int badCharShift = j - badCharValue;
                int goodSuffixShift = goodSuffixTable[j];

                // Using the maximum of the two heuristics
                shift += Math.max(1, Math.max(badCharShift, goodSuffixShift));
            }
        }

        return indicesToString(indices);
    }

    // Good Suffix Heuristic Preprocessing
    private int[] preprocessGoodSuffix(String pattern) {
        int m = pattern.length();
        int[] table = new int[m];
        int[] suffixes = computeSuffixes(pattern);

        // Case 1: The matching suffix occurs elsewhere in the pattern
        java.util.Arrays.fill(table, m);

        int j = 0;
        for (int i = m - 1; i >= -1; --i) {
            if (i == -1 || suffixes[i] == i + 1) {
                for (; j < m - 1 - i; ++j) {
                    if (table[j] == m) table[j] = m - 1 - i;
                }
            }
        }

        // Case 2: A prefix of the pattern matches a suffix of the matching suffix
        for (int i = 0; i <= m - 2; ++i) {
            table[m - 1 - suffixes[i]] = m - 1 - i;
        }

        return table;
    }

    // To compute suffixes array for Good Suffix
    private int[] computeSuffixes(String pattern) {
        int m = pattern.length();
        int[] suffixes = new int[m];
        suffixes[m - 1] = m;
        int g = m - 1;
        int f = m - 1;

        for (int i = m - 2; i >= 0; --i) {
            if (i > g && suffixes[i + m - 1 - f] < i - g) {
                suffixes[i] = suffixes[i + m - 1 - f];
            } else {
                if (i < g) g = i;
                f = i;
                while (g >= 0 && pattern.charAt(g) == pattern.charAt(g + m - 1 - f)) {
                    g--;
                }
                suffixes[i] = f - g;
            }
        }
        return suffixes;
    }
}

/**
 * TODO: Implement your own creative string matching algorithm
 * This is a homework assignment for students
 */
class GoCrazy extends Solution {

    static {
        SUBCLASSES.add(GoCrazy.class);
        System.out.println("GoCrazy registered.");
    }

    public GoCrazy() {
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        // Edge Cases
        if (m == 0) {
            for (int i = 0; i <= n; i++) indices.add(i);
            return indicesToString(indices);
        }
        if (m > n) return "";

        // We map each character to its rightmost position in the pattern using Sunday algorithm logic.
        // If a character is not in the pattern, we can shift by m + 1.
        int[] delta = new int[256]; // Using 256 for ASCII, map for full Unicode fallback
        java.util.Map<Character, Integer> deltaMap = new java.util.HashMap<>();

        // Initialize with default shift m + 1
        for (int i = 0; i < 256; i++) delta[i] = m + 1;

        for (int i = 0; i < m; i++) {
            char c = pattern.charAt(i);
            if (c < 256) {
                delta[c] = m - i;
            }
            deltaMap.put(c, m - i);
        }

        // KMP Next Table for verification
        int[] KMPNextTable = new int[m + 1];
        int i = 0;
        int j = -1;
        KMPNextTable[0] = -1;

        while (i < m) {
            while (j > -1 && pattern.charAt(i) != pattern.charAt(j)) {
                j = KMPNextTable[j];
            }
            i++;
            j++;
            if (i < m && pattern.charAt(i) == pattern.charAt(j)) {
                KMPNextTable[i] = KMPNextTable[j];
            } else {
                KMPNextTable[i] = j;
            }
        }

        // Search Phase (FJS Logic)
        int pos = 0;
        while (pos <= n - m) {
            // Check the rightmost character of the window first (Sunday's heuristic)
            // If this doesn't match, we can skip immediately without checking the rest!
            if (pattern.charAt(m - 1) != text.charAt(pos + m - 1)) {

                if (pos + m >= n) break; // End of text

                char nextChar = text.charAt(pos + m);
                int shift;
                if (nextChar < 256) {
                    shift = delta[nextChar];
                } else {
                    shift = deltaMap.getOrDefault(nextChar, m + 1);
                }
                pos += shift;
            } else {
                // Rightmost character matches. Now verifying the rest using KMP logic.
                int k = 0;
                while (k < m - 1 && pattern.charAt(k) == text.charAt(pos + k))  k++;

                if (k == m - 1) {
                    // Full match found
                    indices.add(pos);
                    // Shift using KMP's KMPNextTable table
                    pos += (j - KMPNextTable[m]);
                    // Standard KMP shift would be m - KMPNextTable[m], but FJS is slightly different
                    // For multiple matches, we can just shift by 1 or use KMP logic.
                    pos += (m - KMPNextTable[m]); // Correct KMP shift
                } else {
                    // Mismatch at index k
                    // Shift using KMP rule relative to the mismatch
                    pos += (k - KMPNextTable[k]);
                }
            }
        }

        return indicesToString(indices);
    }
}
