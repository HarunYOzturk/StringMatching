//Reyyan Naz Doğan - 22050111012
//Murat Emir Öncül - 22050111057 

import java.util.ArrayList;
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

        // An empty pattern matches at every position
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }

        // if pattern is longer than text, no match
        if (m > n) {
            return "";
        }

        // Bad Character Rule: for each character in pattern, store the last position
        Map<Character, Integer> badCharTable = preprocessBadCharacter(pattern, m);

        // Good Suffix Rule: for each suffix in pattern, store the best shift
        int[] goodSuffixTable = preprocessGoodSuffix(pattern, m);

        // Search loop - slide pattern over text
        int shift = 0;
        while (shift <= n - m) {
            int j = m - 1;

            // compare from right to left
            while (j >= 0 && pattern.charAt(j) == text.charAt(shift + j)) {
                j--;
            }

            if (j < 0) {
                // found a match
                indices.add(shift);
                // shift to the next possible match
                shift += goodSuffixTable[0];
            } else {
                // no match - select the larger shift from Bad Character and Good Suffix rules
                char mismatchChar = text.charAt(shift + j);
                int badCharShift = j - badCharTable.getOrDefault(mismatchChar, -1);
                int goodSuffixShift = goodSuffixTable[j + 1];

                shift += Math.max(badCharShift, goodSuffixShift);
            }
        }

        return indicesToString(indices);
    }

    /**
     * Bad Character Rule Preprocessing
     * 
     * For each character in pattern, store the last position.
     * If character is not in pattern, returns -1 (not found in HashMap).
     * 
     * Example: pattern = "ABCAB"
     * A -> 3, B -> 4, C -> 2
     */
    private Map<Character, Integer> preprocessBadCharacter(String pattern, int m) {
        Map<Character, Integer> badCharTable = new HashMap<>();
        for (int i = 0; i < m; i++) {
            badCharTable.put(pattern.charAt(i), i);
        }
        return badCharTable;
    }

    private int[] preprocessGoodSuffix(String pattern, int m) {
        int[] goodSuffixTable = new int[m + 1];
        int[] borderPos = new int[m + 1];
        int[] shift = new int[m + 1];

        // Case 1: Strong Good Suffix Rule
        // Calculate border positions from right to left
        int i = m;
        int j = m + 1;
        borderPos[i] = j;

        while (i > 0) {
            // while characters do not match, go to the next border
            while (j <= m && pattern.charAt(i - 1) != pattern.charAt(j - 1)) {
                if (shift[j] == 0) {
                    shift[j] = j - i;
                }
                j = borderPos[j];
            }
            i--;
            j--;
            borderPos[i] = j;
        }

        // Case 2: If a part of the suffix matches a prefix of the pattern
        j = borderPos[0];
        for (i = 0; i <= m; i++) {
            if (shift[i] == 0) {
                shift[i] = j;
            }
            if (i == j) {
                j = borderPos[j];
            }
        }

        // Copy result to goodSuffixTable
        for (int k = 0; k <= m; k++) {
            goodSuffixTable[k] = shift[k];
        }

        return goodSuffixTable;
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
        // TODO: Students should implement their own creative algorithm here
        throw new UnsupportedOperationException("GoCrazy algorithm not yet implemented - this is your homework!");
    }
}
