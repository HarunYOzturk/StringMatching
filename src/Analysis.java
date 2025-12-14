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

    public BoyerMoore() {
    }

    

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int textLen = text.length();
        int patternLen = pattern.length();

        // Edge Case: If pattern is empty, it technically matches everywhere (0 to N).
        if (patternLen == 0) {
            for (int i = 0; i <= textLen; i++) indices.add(i);
            return indicesToString(indices);
        }
        // Edge Case: Pattern cannot be longer than text.
        if (patternLen > textLen) return "";

        // We use HashMap to support all Unicode characters (not just ASCII).
        Map<Character, Integer> shiftGuide = new HashMap<>();

        for (int i = 0; i < patternLen; i++) {
            shiftGuide.put(pattern.charAt(i), i);
        }
        
        // 'anchor' aligns the beginning of the pattern with the text.
        int anchor = 0;

        
        while (anchor <= textLen - patternLen) {
            int cursor = patternLen - 1; // Start scanning from right to left
            
            // Compare characters starting from the end of the pattern
            while (cursor >= 0 && pattern.charAt(cursor) == text.charAt(anchor + cursor)) {
                cursor--;
            }

            if (cursor < 0) {
                
                indices.add(anchor);

                // Check the character immediately after the pattern in the text.
                if (anchor + patternLen < textLen) {
                    char charAfterPattern = text.charAt(anchor + patternLen);
                    int charPos = shiftGuide.getOrDefault(charAfterPattern, -1);
                    anchor += patternLen - charPos;
                } else {
                    anchor += 1;// End of text reached, just shift by 1
                }
            } else {
                // Identify the bad character in the text.
                char badChar = text.charAt(anchor + cursor);
                int badCharIndex = shiftGuide.getOrDefault(badChar, -1);

                // Shift pattern to align bad character with its last occurrence in pattern.
                // Math.max ensures we never shift backwards.
                anchor += Math.max(1, cursor - badCharIndex);
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

        int textLen = text.length();
        int patternLen = pattern.length();
        List<Integer> indices = new ArrayList<>();

        // Edge Case: Empty pattern technically matches at every position (0 to N).
        if (patternLen == 0) {
            for (int i = 0; i <= textLen; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }

        // Edge Case
        if (patternLen > textLen) {
            return "";
        }

        int h = 1;
        for (int i = 0; i < patternLen - 1; i++) {
            //we use bitwise shift (<< 5) which is equivalent to (* 32).
            // This is much faster on the CPU.
            h = h << 5;

        }

        int patternHash = 0;
        int textHash = 0;

        for (int i = 0; i < patternLen; i++) {
            // We rely on Java's native "Integer Overflow" mechanism (implicit modulo 2^32).
            // This avoids expensive modulo (%) operations and negative number logic issues.
            patternHash = (patternHash << 5) + pattern.charAt(i);
            textHash = (textHash << 5) + text.charAt(i);
        }

        // Sliding Window (Rolling Hash)
        for (int anchor = 0; anchor <= textLen - patternLen; anchor++) {
            if (patternHash == textHash) {
                boolean match = true;
                // Collision Check: Verify characters one by one to avoid false positives.
                for (int j = 0; j < patternLen; j++) {
                    if (text.charAt(anchor + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }

                if (match) {
                    indices.add(anchor);
                }
            }

            // Rolling Step: Update hash for the next window in O(1) time
            if (anchor < textLen - patternLen) {
                textHash = textHash - (text.charAt(anchor) * h);
                textHash = textHash << 5;
                textHash = textHash + (text.charAt(anchor + patternLen));
            }
        }

        return indicesToString(indices);

    }
}
