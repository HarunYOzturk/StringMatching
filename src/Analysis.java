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
    private int[] badChar = new int[65536];

    public BoyerMoore() {
    }


    @Override
    public String Solve(String text, String pattern) {
        // TODO: Students should implement Boyer-Moore algorithm here
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        // first case if the pattern is empty
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }
        // Second case prepare the Bad character table
        Arrays.fill(badChar, -1);
        for (int i = 0; i < m; i++) {
            char c = pattern.charAt(i);
            // only take the ones that fit into the UNICODE table
            if (c < 65536) {
                badChar[c] = i;
            }
        }
        // amount of shift
        int s = 0;

        while (s <= (n - m)) {
            int j = m - 1;
            while (j >= 0 && pattern.charAt(j) == text.charAt(s + j)) {
                j--;
            }
            if (j < 0) {
                // match found
                indices.add(s);
                // determine the next shift amount
                if (s + m < n) {
                    char nextChar = text.charAt(s + m);
                    int lastOccur = (nextChar < 65536) ? badChar[nextChar] : -1;
                    s += m - lastOccur;
                } else {
                    s += 1;
                }
            } else {
                // missmatch
                // Shift the bad character in the text to its last position in the pattern
                char missMatchedChar = text.charAt(s + j);
                int lastOccur = (missMatchedChar < 65536) ? badChar[missMatchedChar] : -1;

                // for going back
                s += Math.max(1, j - lastOccur);
            }
        }

        return indicesToString(indices);
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
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        // If the pattern is empty every character is a match
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }
        // If the pattern length is longer than text length that means no match
        if (m > n) {
            return "";
        }

        // Prepare the shifting table for jumping
        // Primitive int is faster than HashMap
        int[] shift = new int[256];

        // default jump emount: pattern + 1
        for (int i = 0; i < 256; i++) {
            shift[i] = m + 1;
        }

        // Adjust the jump margins according to the characters within the pattern
        for (int i = 0; i < m; i++) {
            char c = pattern.charAt(i);
            if (c < 256) {
                shift[c] = m - i;
            }
        }

        // 3. End points control
        char firstP = pattern.charAt(0);
        char lastP = pattern.charAt(m - 1);

        // 4. Search Loop
        int s = 0;
        while (s <= n - m) {

            // --- Hybrit part ---
            // Checking the end points before middle part (O(1))
            // This works as Light Hash
            char firstT = text.charAt(s);
            char lastT = text.charAt(s + m - 1);

            boolean potentialMatch = (firstT == firstP) && (lastT == lastP);

            if (potentialMatch) {
                // End points matched chechk the middle (Expensive Check)
                int j = 1;
                while (j < m - 1 && text.charAt(s + j) == pattern.charAt(j)) {
                    j++;
                }

                // ıf the loop is completed or the pattern is shorter than 2
                if (j >= m - 1) {
                    indices.add(s);
                }
            }

            // --- Jumping Mechanism ---
            if (s + m < n) {
                char nextChar = text.charAt(s + m); // The character out of window

                // Adjust the jumping amount
                if (nextChar < 256) {
                    s += shift[nextChar];
                } else {
                    // The default jump if Unicode encountered
                    s += 1;
                }
            } else {
                break;
            }
        }

        return indicesToString(indices);
    }
}


