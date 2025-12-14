// Selin Sargın  21005011024
// Zeynep Merve Karataş  23050131003

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
        System.out.println("BoyerMoore registered");
    }

    public BoyerMoore() {
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();

        int n = text.length();
        int m = pattern.length();

        // If the pattern is empty, return all positions
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }

        // If the pattern is longer than the text, no matches are possible
        if (m > n) {
            return "";
        }

        // Build the bad character table
        int[] badChar = new int[65536];
        for (int i = 0; i < badChar.length; i++) {
            badChar[i] = -1;
        }
        for (int i = 0; i < m; i++) {
            badChar[pattern.charAt(i)] = i;
        }

        // Preprocess pattern for good suffix rule

        int[] suffix = computeSuffixes(pattern);
        int[] L = new int[m];
        int[] l = new int[m];

        // Initialize L and l tables
        for (int i = 0; i < m; i++) {
            L[i] = 0;
            l[i] = 0;
        }

        // Build L-table (big-L) based on suffix array
        for (int i = 0; i < m - 1; i++) {
            int len = suffix[i];
            if (len > 0) {
                L[m - len] = i + 1;
            }
        }

        // Build small-l table based on the longest prefix
        int longestPrefix = 0;
        for (int i = m - 1; i >= 0; i--) {
            if (suffix[i] == i + 1) {
                longestPrefix = i + 1;
            }
            l[m - 1 - i] = longestPrefix;
        }

        // Find all occurrences of pattern in text
        int s = 0; // shift

        while (s <= n - m) {
            int j = m - 1;

            // Compare pattern from right to left
            while (j >= 0 && pattern.charAt(j) == text.charAt(s + j)) {
                j--;
            }

            // Match found
            if (j < 0) {
                indices.add(s);

                int shift = (m > 1) ? (m - l[1]) : 1;
                if (shift <= 0)
                    shift = 1;
                s += shift;
            } else {
                char bad = text.charAt(s + j);

                // Bad character rule
                int bcIndex = badChar[bad];
                int bcShift = j - bcIndex;
                if (bcShift < 1)
                    bcShift = 1;

                // Good suffix rule
                int gsShift;
                if (j < m - 1) {
                    gsShift = goodSuffixShift(j, m, L, l);
                } else {
                    gsShift = 1;
                }

                s += Math.max(bcShift, gsShift);
            }
        }

        return indicesToString(indices);
    }

    // Helper method to compute suffixes for good suffix rule
    private int[] computeSuffixes(String pattern) {
        int m = pattern.length();
        int[] suf = new int[m];

        suf[m - 1] = m;
        int g = m - 1;
        int f = 0;

        for (int i = m - 2; i >= 0; i--) {
            if (i > g && suf[i + m - 1 - f] < i - g) {
                suf[i] = suf[i + m - 1 - f];
            } else {
                if (i < g)
                    g = i;
                f = i;
                while (g >= 0 && pattern.charAt(g) == pattern.charAt(g + m - 1 - f)) {
                    g--;
                }
                suf[i] = f - g;
            }
        }

        return suf;
    }

    // Helper method to calculate good suffix shift
    private int goodSuffixShift(int j, int m, int[] L, int[] l) {
        int length = m - 1 - j;
        if (length == 0)
            return 1;

        if (L[j + 1] > 0) {
            return m - L[j + 1];
        } else {
            return m - l[j + 1];
        }
    }
}

/**
 * TODO: Implement your own creative string matching algorithm
 * This is a homework assignment for students
 * Be creative! Try to make it efficient for specific cases
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
        List<Integer> list = new ArrayList<>();

        int n = text.length();
        int m = pattern.length();

        // Handle empty pattern
        if (m == 0) {
            for (int i = 0; i <= n; i++)
                list.add(i);
            return indicesToString(list);
        }
        if (m > n)
            return "";

        // Pivot: the last character of the pattern
        char pivot = pattern.charAt(m - 1);

        // Check before pivot if the pattern is longer than 1 character
        boolean useBefore = false;
        char beforePivot = 0;
        if (m >= 2) {
            beforePivot = pattern.charAt(m - 2);
            useBefore = true;
        }

        // Skip rule for fast jumping
        int skip = Math.max(1, m / 2);

        // Start with the pivot aligned
        int i = m - 1; 

        while (i < n) {

            // Pivot check
            if (text.charAt(i) == pivot) {

                // Early rejection for before pivot character
                if (useBefore) {

                    if (i - 1 < 0) {
                        i += skip;
                        continue;
                    }
                    if (text.charAt(i - 1) != beforePivot) {
                        i += skip;
                        continue;
                    }
                }

                // Full pattern match check from right to left
                boolean ok = true;
                for (int j = m - 1, k = i; j >= 0; j--, k--) {
                    if (text.charAt(k) != pattern.charAt(j)) {
                        ok = false;
                        break;
                    }
                }

                if (ok) {
                    list.add(i - (m - 1));
                    i += skip;
                    continue;
                }

                i += skip;
            } else {
                i += skip;
            }
        }

        return indicesToString(list);
    }
}
