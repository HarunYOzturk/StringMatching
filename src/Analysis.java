import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        // If pattern is empty, it matches at every position
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }

        // Build LPS (longest proper prefix which is also suffix) array
        int[] lps = computeLPS(pattern);

        int i = 0; // index in text
        int j = 0; // index in pattern

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

    private static final int PRIME = 101; // Prime number used for hashing

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        // If pattern is empty, it matches at every position
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }

        if (m > n) {
            return "";
        }

        int d = 256; // Size of the input alphabet
        long patternHash = 0;
        long textHash = 0;
        long h = 1;

        // Compute h = d^(m-1) % PRIME
        for (int i = 0; i < m - 1; i++) {
            h = (h * d) % PRIME;
        }

        // Compute hash of pattern and first window of text
        for (int i = 0; i < m; i++) {
            patternHash = (d * patternHash + pattern.charAt(i)) % PRIME;
            textHash = (d * textHash + text.charAt(i)) % PRIME;
        }

        // Slide pattern over text one position at a time
        for (int i = 0; i <= n - m; i++) {
            // If hashes match, check characters
            if (patternHash == textHash) {
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

            // Update hash for next window
            if (i < n - m) {
                textHash = (d * (textHash - text.charAt(i) * h) + text.charAt(i + m)) % PRIME;

                // Make sure hash is non-negative
                if (textHash < 0) {
                    textHash = textHash + PRIME;
                }
            }
        }

        return indicesToString(indices);
    }
}

/**
 * TODO: Implement Boyer-Moore string matching.
 * This is part of the homework.
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

        // If pattern is empty, match at every position (same as other algorithms)
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }

        if (m > n) {
            return indicesToString(indices);
        }

        // Preprocessing: build bad character and good suffix tables
        Object[] prep = preprocess(pattern);
        @SuppressWarnings("unchecked")
        Map<Character, Integer> badChar =
                (Map<Character, Integer>) prep[0];
        int[] suffix = (int[]) prep[1];
        boolean[] prefix = (boolean[]) prep[2];

        int i = 0;

        while (i <= n - m) {
            int j = m - 1;

            // Compare from right to left
            while (j >= 0 && pattern.charAt(j) == text.charAt(i + j)) {
                j--;
            }

            if (j < 0) {
                // Found a full match
                indices.add(i);

                // Shift after a full match (based on good suffix / prefix)
                int shift = fullMatchShift(m, prefix);
                if (shift < 1) {
                    shift = 1;
                }
                i += shift;
            } else {
                // On mismatch: combine bad character and good suffix shifts
                int bcShift = badCharShift(text.charAt(i + j), j, badChar);
                int gsShift = goodSuffixShift(j, m, suffix, prefix);

                int shift = bcShift;
                if (gsShift > shift) {
                    shift = gsShift;
                }
                if (shift < 1) {
                    shift = 1;
                }

                i += shift;
            }
        }

        return indicesToString(indices);
    }

    // ================== PREPROCESSING ==================

    private Object[] preprocess(String pattern) {
        Map<Character, Integer> badChar = buildBadCharMap(pattern);
        Object[] gs = buildGoodSuffixTables(pattern);
        int[] suffix = (int[]) gs[0];
        boolean[] prefix = (boolean[]) gs[1];

        return new Object[]{badChar, suffix, prefix};
    }

    // Bad character table with Unicode support (HashMap)
    public static Map<Character, Integer> buildBadCharMap(String pattern) {
        Map<Character, Integer> badChar =
                new HashMap<Character, Integer>();

        int m = pattern.length();
        for (int i = 0; i < m; i++) {
            char c = pattern.charAt(i);
            // Last position of this character in the pattern
            badChar.put(c, i);
        }

        return badChar;
    }

    // Build suffix[] and prefix[] tables for the good suffix rule
    private Object[] buildGoodSuffixTables(String pattern) {
        int m = pattern.length();
        int[] suffix = new int[m];
        boolean[] prefix = new boolean[m];

        Arrays.fill(suffix, -1);
        Arrays.fill(prefix, false);

        // Take pattern[0..i] and compare it with suffixes that end at the last character
        for (int i = 0; i < m - 1; i++) {
            int j = i;
            int k = 0;

            while (j >= 0 && pattern.charAt(j) == pattern.charAt(m - 1 - k)) {
                j--;
                k++;
                // Starting index of the suffix of length k
                suffix[k] = j + 1;
            }

            // If it matches all the way to the start, this suffix is also a prefix
            if (j == -1 && k > 0) {
                prefix[k] = true;
            }
        }

        return new Object[]{suffix, prefix};
    }

    // ================== SHIFT HELPERS ==================

    // Shift based on the bad character rule
    private int badCharShift(char mismatchedChar, int j,
                             Map<Character, Integer> badChar) {
        Integer idx = badChar.get(mismatchedChar);
        int bcIndex = (idx == null) ? -1 : idx.intValue();
        return j - bcIndex;
    }

    // Shift based on the good suffix rule
    private int goodSuffixShift(int j, int m, int[] suffix, boolean[] prefix) {
        int k = m - 1 - j; // length of the matched suffix

        if (k <= 0) {
            // No suffix matched, so good suffix rule does not apply
            return 0;
        }

        // 1) Check if there is another substring that has the same suffix
        if (suffix[k] != -1) {
            return j - suffix[k] + 1;
        }

        // 2) Otherwise, look for a shorter suffix that is also a prefix
        for (int r = k - 1; r > 0; r--) {
            if (prefix[r]) {
                return m - r;
            }
        }

        // 3) If none of the above, shift the whole pattern
        return m;
    }

    // Shift after a full match
    private int fullMatchShift(int m, boolean[] prefix) {
        // Use the longest prefix that is also a suffix
        for (int r = m - 1; r > 0; r--) {
            if (prefix[r]) {
                return m - r;
            }
        }
        // If there is none, shift by the full pattern length
        return m;
    }
}





//Hybrid string matcher:
//Uses a Boyer–Moore–Horspool style scan for fast average-case performance.
//If the algorithm keeps shifting by 1 (a clear sign of BM's worst case), it
//automatically switches to KMP for guaranteed linear-time behavior.
//This way we keep BM's speed on normal text while avoiding its bad-case slowdown.

class GoCrazy extends Solution {
    static {
        SUBCLASSES.add(GoCrazy.class);
        System.out.println("GoCrazy registered");
    }

    public GoCrazy() {
    }

    @Override
    public String Solve(String text, String pattern) {
        java.util.List<Integer> indices = new java.util.ArrayList<>();

        int n = text.length();
        int m = pattern.length();

        // If pattern is empty, match at every position
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }

        if (n < m) {
            return indicesToString(indices);
        }

        // Bad character table: reuse BoyerMoore version
        Map<Character, Integer> badChar = BoyerMoore.buildBadCharMap(pattern);

        // LPS table for KMP (used in fallback phase)
        int[] lps = buildLps(pattern);

        int i = 0;                  // index in text (BM phase)
        int smallShiftCount = 0;    // how many times in a row shift was 1
        int threshold = m;          // if we shift by 1 more than m times, BM is doing badly

        // Phase 1: Boyer–Moore–Horspool-like scan
        while (i <= n - m) {
            int j = m - 1;

            // Compare from right to left
            while (j >= 0 && text.charAt(i + j) == pattern.charAt(j)) {
                j--;
            }

            if (j < 0) {
                // We found a match
                indices.add(i);

                // Move by 1 to also catch overlapping matches
                i += 1;
                smallShiftCount = 0;
            } else {
                // Horspool style: shift based on the last character in the window
                char c = text.charAt(i + m - 1);
                Integer lastOcc = badChar.get(c);

                int shift;
                if (lastOcc == null) {
                    // If character is not in the pattern, shift by full pattern length
                    shift = m;
                } else {
                    // Shift based on the last occurrence index
                    shift = m - 1 - lastOcc;
                    if (shift <= 0) {
                        shift = 1;
                    }
                }

                if (shift == 1) {
                    smallShiftCount++;
                    if (smallShiftCount > threshold) {
                        // If BM keeps shifting by 1, switch to KMP (bad case)
                        break;
                    }
                } else {
                    smallShiftCount = 0;
                }

                i += shift;
            }
        }

        // Phase 2: use KMP on the remaining part of the text
        if (i <= n - m) {
            kmpSearchFrom(text, pattern, lps, i, indices);
        }

        return indicesToString(indices);
    }

    // ====== KMP helpers (independent from BoyerMoore) ======

    private int[] buildLps(String pattern) {
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

    private void kmpSearchFrom(String text,
                               String pattern,
                               int[] lps,
                               int start,
                               List<Integer> indices) {

        int n = text.length();
        int m = pattern.length();

        int i = start;
        int j = 0;

        while (i < n) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == m) {
                    indices.add(i - m);
                    j = lps[j - 1];
                }
            } else {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
    }
}
