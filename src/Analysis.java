import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;


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

        // Empty pattern matches at every position
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }

        // If pattern longer than text then no matches
        if (m > n) {
            return "";
        }

        int[] badChar = buildBadCharacterTable(pattern);
        int[] goodSuffix = buildGoodSuffixTable(pattern);

        int s = 0;

        while (s <= n - m) {
            int j = m - 1;

            // Compare from right to left
            while (j >= 0 && pattern.charAt(j) == text.charAt(s + j)) {
                j--;
            }

            // Full match found
            if (j < 0) {
                indices.add(s);
                // Shift by good suffix for full match
                s += goodSuffix[0];
            } else {
                int c = text.charAt(s + j) & 0xFF;
                int badShift = j - badChar[c];
                if (badShift < 1) {
                    badShift = 1;
                }
                int goodShift = goodSuffix[j];
                s += Math.max(badShift, goodShift);
            }
        }

        return indicesToString(indices);
    }

    /**
     * Bad character table:
     * For each character c, stores the last index where c appears in pattern,
     * or -1 if it does not appear.
     */

    private int[] buildBadCharacterTable(String pattern) {

        final int ALPHABET_SIZE = 256; // same as RabinKarp
        int[] badChar = new int[ALPHABET_SIZE];
        Arrays.fill(badChar, -1);

        for (int i = 0; i < pattern.length(); i++) {
            badChar[pattern.charAt(i) & 0xFF] = i;
        }
        return badChar;
    }

    /**
     * Computes the suffix array used for the good suffix rule.
     * suff[i] = length of the longest suffix of pattern[0..i]
     * that is also a suffix of the whole pattern.
     */

    private int[] buildSuffixes(String pattern) {
        int m = pattern.length();
        int[] suff = new int[m];

        suff[m - 1] = m;
        int g = m - 1;
        int f = 0;

        for (int i = m - 2; i >= 0; i--) {
            if (i > g && suff[i + m - 1 - f] < i - g) {
                suff[i] = suff[i + m - 1 - f];
            } else {
                if (i < g) {
                    g = i;
                }
                f = i;
                while (g >= 0 && pattern.charAt(g) == pattern.charAt(g + m - 1 - f)) {
                    g--;
                }
                suff[i] = f - g;
            }
        }

        return suff;
    }

    /**
     * Good suffix table:
     * goodSuffix[j] = shift amount when mismatch occurs at position j.
     */
    private int[] buildGoodSuffixTable(String pattern) {

        int m = pattern.length();
        int[] goodSuffix = new int[m];
        int[] suff = buildSuffixes(pattern);

        Arrays.fill(goodSuffix, m);

        int j = 0;
        // 1st case: prefixes that are also suffixes
        for (int i = m - 1; i >= 0; i--) {
            if (suff[i] == i + 1) {
                for (; j < m - 1 - i; j++) {
                    if (goodSuffix[j] == m) {
                        goodSuffix[j] = m - 1 - i;
                    }
                }
            }
        }

        // 2nd case: other suffixes
        for (int i = 0; i <= m - 2; i++) {
            goodSuffix[m - 1 - suff[i]] = m - 1 - i;
        }

        return goodSuffix;
    }
}

/**
 * TODO: Implement your own creative string matching algorithm
 * This is a homework assignment for students
 * Be creative! Try to make it efficient for specific cases
 */

// My GoCrazy method is designed for cases where the pattern is longer than the text.
class GoCrazy extends Solution {
    static {
        SUBCLASSES.add(GoCrazy.class);
        System.out.println("GoCrazy registered");
    }

    public GoCrazy() {
    }

    @Override
    public String Solve(String text, String pattern) {
        // TODO: Students should implement their own creative algorithm here


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

        // Use fast bit-parallel search for short patterns
        if (m <= 64) {
            long[] masks = buildBitMasks(pattern);
            long state = 0L;
            long matchBit = 1L << (m - 1);

            for (int i = 0; i < n; i++) {
                state = ((state << 1) | 1L) & masks[text.charAt(i)];
                if ((state & matchBit) != 0L) {
                    indices.add(i - m + 1);
                }
            }

            return indicesToString(indices);
        }

        // Return to a Boyer Moore Horspool style scan for longer patterns
        int[] shift = buildBadCharacterShifts(pattern);
        int last = m - 1;
        char lastChar = pattern.charAt(last);
        int i = 0;

        while (i <= n - m) {
            char tail = text.charAt(i + last);
            if (tail == lastChar) {
                int j = last - 1;
                while (j >= 0 && text.charAt(i + j) == pattern.charAt(j)) {
                    j--;
                }
                if (j < 0) {
                    indices.add(i);
                    i += shift[lastChar];
                    continue;
                }
            }
            i += shift[tail];
        }

        return indicesToString(indices);
    }


    private int[] buildBadCharacterShifts(String pattern) {
        int m = pattern.length();
        int[] shift = new int[65536];
        Arrays.fill(shift, m);
        for (int i = 0; i < m - 1; i++) {
            shift[pattern.charAt(i)] = m - 1 - i;
        }
        shift[pattern.charAt(m - 1)] = 1;
        return shift;
    }
}

    private long[] buildBitMasks(String pattern) {
        long[] masks = new long[65536];
        for (int i = 0; i < pattern.length(); i++) {
            masks[pattern.charAt(i)] |= (1L << i);
        }
        return masks;
    }



