import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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

class BoyerMoore extends Solution {
    /*
     * Boyer-Moore Algorithm: How it Works
     * Unlike other algorithms, Boyer-Moore starts its comparison from the right
     * side of the pattern and moves left. During this process, it uses
     * "Bad Character" and "Good Suffix" tables to enable advanced, jump-based
     * searching. (In some cases, it can jump 3-4 characters at a time, which
     * means it runs very efficiently.)
     *
     * Our Implementation Map:
     * - Edge Cases: If the pattern length 'm' is zero, all positions 0..n are
     *   added to the results. If m > n, it returns an empty result immediately.
     *
     * - The Bad Character Rule: Shifts the pattern based on the character that
     *   caused the mismatch.
     *
     * - The Good Suffix Rule: If we find a matching suffix before a mismatch,
     *   this rule determines where to shift the pattern. It uses pre-calculated
     *   suffix and prefix arrays.
     *
     * - Matching and Shifting: The process starts at index 'i' in the text and
     *   checks the pattern from right to left. If there's no mismatch (j < 0),
     *   the index is recorded and a shift is made. If there is a mismatch, the
     *   shifts from the bad character and good suffix rules are calculated, and
     *   the maximum of the two is applied.
     *
     * - Combining the Two Rules: The Biggest Jump Wins!
     *   The real Boyer-Moore algorithm calculates how far it can shift according
     *   to both rules and chooses the one that provides the longest jump.
     *   shift = max(BadCharacterRule(), GoodSuffixRule())
     */
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
        char[] t = text.toCharArray();
        char[] p = pattern.toCharArray();

        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }
        if (m > n) {
            return "";
        }
        if (m == 1) {
            char target = p[0];
            for (int i = 0; i < n; i++) {
                if (t[i] == target) {
                    indices.add(i);
                }
            }
            return indicesToString(indices);
        }

        int[] goodSuffix = buildGoodSuffixShift(p);
        Map<Character, Integer> badChar = buildBadCharacterMap(p);

        int i = 0;
        while (i <= n - m) {
            int j;
            // Scan the pattern from right to left.
            for (j = m - 1; j >= 0; j--) {
                if (p[j] != t[i + j]) {
                    break; // Mismatch found.
                }
            }

            if (j < 0) {
                // Full match found! Record it.
                indices.add(i);
                i += goodSuffix[0];
            } else {
                // Mismatch: Calculate the biggest possible jump.
                int badCharShift = j - badChar.getOrDefault(t[i + j], -1);
                int goodSuffixShift = goodSuffix[j + 1];
                int shift = Math.max(badCharShift, goodSuffixShift);
                i += (shift > 0) ? shift : 1;
            }
        }

        return indicesToString(indices);
    }

    private Map<Character, Integer> buildBadCharacterMap(char[] pattern) {
        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < pattern.length; i++) {
            map.put(pattern[i], i);
        }
        return map;
    }

    private int[] buildGoodSuffixShift(char[] pattern) {
        int m = pattern.length;
        int[] shift = new int[m + 1];
        int[] border = new int[m + 1];

        int i = m;
        int j = m + 1;
        border[i] = j;

        while (i > 0) {
            while (j <= m && pattern[i - 1] != pattern[j - 1]) {
                if (shift[j] == 0) {
                    shift[j] = j - i;
                }
                j = border[j];
            }
            i--;
            j--;
            border[i] = j;
        }

        j = border[0];
        for (i = 0; i <= m; i++) {
            if (shift[i] == 0) {
                shift[i] = j;
            }
            if (i == j) {
                j = border[j];
            }
        }
        return shift;
    }
}

/**
 * TODO: Implement your own creative string matching algorithm
 * This is a homework assignment for students
 * Be creative! Try to make it efficient for specific cases
 */
/*
 * GoCrazy Algorithm: A "Gated" Bidirectional Search
 *
 * This is a creative, heuristic-based algorithm. The core idea is to perform a
 * very fast check on the endpoints of the pattern before committing to a full
 * comparison of the inner part. It acts like a "gate": if the first and last
 * characters don't match, the gate is closed, and we can immediately discard
 * the current window and move on.
 *
 * How it Works:
 * 1. The Two Gates: For each window in the text, it first compares the first
 *    character of the pattern with the first character of the window, AND the
 *    last character of the pattern with the last character of the window.
 * 2. Inner Check: Only if both "gates" pass (i.e., both endpoints match) does
 *    the algorithm proceed to check the rest of the characters in between.
 * 3. Shift: In this implementation, the window is shifted by one position after
 *    each check. This is simple but could be optimized further by using
 *    information from mismatches to make larger jumps.
 *
 * This approach can be very efficient for quickly rejecting non-matching
 * positions, especially with a large character set where mismatches on the
 * endpoints are common.
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
        // This is a creative algorithm: Bidirectional Search (İki Yönlü Arama).
        // It first checks the outermost characters (first and last) of the pattern.
        // If they match, it then proceeds to check the inner part of the pattern.
        // This can be very efficient for large alphabets as it quickly discards
        // mismatches.
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }
        if (m > n) {
            return "";
        }

        char firstChar = pattern.charAt(0);
        char lastChar = pattern.charAt(m - 1);
        int i = 0;

        while (i <= n - m) {
            // 1. Check last and first characters first (the two "gates")
            if (text.charAt(i + m - 1) == lastChar && text.charAt(i) == firstChar) {

                // 2. If gates pass, check the rest of the pattern from inside
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
            i++; // In this simple version, we shift by one. Can be optimized further.
        }

        return indicesToString(indices);
    }
}
