import java.util.*;

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
        int textLen = text.length();
        int patLen = pattern.length();

        //Empty pattern mean matches at every position
        if (patLen == 0) {
            for (int i = 0; i <= textLen; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }

        // Pattern longer than text means no match
        if (patLen > textLen) {
            return "";
        }

        Map<Character, Integer> badChar = badCharTable(pattern);
        int[] goodSuffix = goodSuffixTable(pattern);

        int shift = 0;

        while (shift <= textLen - patLen) {
            int j = patLen - 1;

            // Compare characters from right to left
            while (j >= 0 && pattern.charAt(j) == text.charAt(shift + j)) {
                j--;
            }

            if (j < 0) {
                // Match found
                indices.add(shift);

                // shift using full match good-suffix rule
                shift += goodSuffix[0];
            } else {
                char mismatchChar = text.charAt(shift + j);

                // Shift based bad character rule
                int badCharIndex = badChar.getOrDefault(mismatchChar, -1);
                int badCharShift = j - badCharIndex;
                if (badCharShift < 1) {
                    badCharShift = 1;
                }

                // Shift based good suffix rule
                int goodSuffixShift = goodSuffix[j];

                // Choose the larger shift
                shift += Math.max(badCharShift, goodSuffixShift);
            }
        }

        return indicesToString(indices);
    }

    private Map<Character, Integer> badCharTable(String pattern) {
        Map<Character, Integer> badChar = new HashMap<>();
        
        //Hold the rightmost index of each character
        for (int i = 0; i < pattern.length(); i++) {
            badChar.put(pattern.charAt(i), i);
        }

        return badChar;
    }

    private int[] goodSuffixTable(String pattern) {
        int patLen = pattern.length();
        int[] goodSuffix = new int[patLen];
        int[] border = new int[patLen + 1];

        int i = patLen;
        int j = patLen + 1;
        border[i] = j;

        // Build good suffix table
        while (i > 0) {
            while (j <= patLen && pattern.charAt(i - 1) != pattern.charAt(j - 1)) {
                if (goodSuffix[j - 1] == 0) {
                    goodSuffix[j - 1] = j - i;
                }
                j = border[j];
            }
            i--;
            j--;
            border[i] = j;
        }

        // Build the remaining values
        j = border[0];
        for (i = 0; i < patLen; i++) {
            if (goodSuffix[i] == 0) {
                goodSuffix[i] = j;
            }
            if (i == j) {
                j = border[j];
            }
        }

        return goodSuffix;
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


