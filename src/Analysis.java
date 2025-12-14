import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
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

    public BoyerMoore() {}

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();

        int n = text.length();
        int m = pattern.length();

        // Empty pattern: matches at every position (same behavior as Naive / KMP)
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }

        // If text is empty or pattern is longer than text, there is no match
        if (n == 0 || m > n) {
            return indicesToString(indices);
        }

        // 1) Build the bad character table (supports full Unicode)
        Map<Character, Integer> badChar = buildBadCharTable(pattern);

        // 2) Main search loop
        int s = 0; // s = current shift of the pattern over the text
        while (s <= n - m) {
            int j = m - 1;

            // Compare pattern and text from right to left
            while (j >= 0 && pattern.charAt(j) == text.charAt(s + j)) {
                j--;
            }

            if (j < 0) {
                // Full match found at shift s
                indices.add(s);

                // Shift the pattern for the next possible match
                if (s + m < n) {
                    char nextChar = text.charAt(s + m);
                    int lastOcc = getLastOccurrence(badChar, nextChar);
                    int shift = m - lastOcc;
                    if (shift < 1) {
                        shift = 1;
                    }
                    s += shift;
                } else {
                    s += 1;
                }

            } else {
                // Mismatch occurred at index j
                char mismatch = text.charAt(s + j);
                int lastOcc = getLastOccurrence(badChar, mismatch); // last occurrence in pattern, or -1

                // Compute shift amount using the bad character rule
                int shift = j - lastOcc;
                if (shift < 1) {
                    shift = 1; // Always shift by at least 1
                }

                s += shift;
            }
        }

        return indicesToString(indices);
    }

    /**
     * Builds the bad character table using a HashMap.
     * For each character in the pattern, stores the last index where it appears.
     */
    private Map<Character, Integer> buildBadCharTable(String pattern) {
        Map<Character, Integer> badChar = new HashMap<>();

        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            badChar.put(c, i);  // last occurrence will overwrite previous ones
        }

        return badChar;
    }

    /**
     * Safely gets the last occurrence of character c from the badChar table.
     * If c does not appear in the pattern, returns -1.
     */
    private int getLastOccurrence(Map<Character, Integer> badChar, char c) {
        Integer val = badChar.get(c);
        return (val == null) ? -1 : val;
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

    public GoCrazy() {}

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();

        int n = text.length();
        int m = pattern.length();

        // Empty pattern: matches at every position (same behavior as others)
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }

        // If text is empty or pattern is longer than text, there is no match
        if (n == 0 || m > n) {
            return indicesToString(indices);
        }

        // Special case: single character pattern
        if (m == 1) {
            char p = pattern.charAt(0);
            for (int i = 0; i < n; i++) {
                if (text.charAt(i) == p) {
                    indices.add(i);
                }
            }
            return indicesToString(indices);
        }

        // Main loop: try all possible shifts
        for (int s = 0; s <= n - m; s++) {
            boolean match = true;

            int left = 0;
            int right = m - 1;

            // Compare from both ends towards the center
            while (left <= right) {
                if (text.charAt(s + left) != pattern.charAt(left)) {
                    match = false;
                    break;
                }

                if (left != right) { // avoid double-checking the middle character
                    if (text.charAt(s + right) != pattern.charAt(right)) {
                        match = false;
                        break;
                    }
                }

                left++;
                right--;
            }

            if (match) {
                indices.add(s);
            }
        }

        return indicesToString(indices);
    }
}



