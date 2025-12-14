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
 * Optimized Boyer-Moore using HashMap for Bad Character Heuristic.
 * This avoids the overhead of initializing a size-65536 array for every search,
 * providing both Unicode support and high performance.
 */
class BoyerMoore extends Solution {
    
    static {
        SUBCLASSES.add(BoyerMoore.class);
        System.out.println("BoyerMoore registered.");
    }

    public BoyerMoore() {
    }

    // HashMap based preprocessing
    private void badCharHeuristic(String str, int size, java.util.Map<Character, Integer> badchar) {
        for (int i = 0; i < size; i++)
            badchar.put(str.charAt(i), i);
    }

    @Override
    public String Solve(String text, String pattern) {
        StringBuilder result = new StringBuilder();
        int m = pattern.length();
        int n = text.length();

        // Empty Pattern Logic
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                if (i > 0) result.append(",");
                result.append(i);
            }
            return result.toString();
        }

        if (m > n) return "";

        // Use HashMap instead of int[65536]
        java.util.Map<Character, Integer> badchar = new java.util.HashMap<>();
        badCharHeuristic(pattern, m, badchar);

        int s = 0; 
        while (s <= (n - m)) {
            int j = m - 1;

            while (j >= 0 && pattern.charAt(j) == text.charAt(s + j))
                j--;

            if (j < 0) {
                if (result.length() > 0) result.append(",");
                result.append(s);

                // Calculate shift: look at text[s+m]
                // If text[s+m] exists in map, align it. Else shift m+1.
                // getOrDefault(char, -1) simulates the array initialized to -1.
                int nextCharIndex = (s + m < n) ? badchar.getOrDefault(text.charAt(s + m), -1) : -1;
                s += (s + m < n) ? m - nextCharIndex : 1;

            } else {
                // Mismatch shift
                // getOrDefault returns -1 if char not in pattern
                int charIndex = badchar.getOrDefault(text.charAt(s + j), -1);
                s += Math.max(1, j - charIndex);
            }
        }

        return result.toString();
    }
}

/**
 * Implementation of Boyer-Moore-Horspool.
 * OPTIMIZATION: Hybrid Table Strategy
 * - If pattern is ASCII only: Uses int[256] array (Ultra fast, low overhead).
 * - If pattern has Unicode: Uses HashMap (Safe, prevents crashes).
 * This eliminates the overhead of HashMap for most of test cases.
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
        StringBuilder result = new StringBuilder();
        int m = pattern.length();
        int n = text.length();

        // Edge Cases
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                if (i > 0) result.append(",");
                result.append(i);
            }
            return result.toString();
        }
        if (m > n) return "";

        // CHECK IF PATTERN IS ASCII ONLY
        boolean isAscii = true;
        for (int i = 0; i < m; i++) {
            if (pattern.charAt(i) > 255) {
                isAscii = false;
                break;
            }
        }

        // PATH 1: ASCII ONLY (FAST PATH)
        // Uses int[] array. Extremely low overhead.
        if (isAscii) {
            int[] shiftTable = new int[256];
            // Arrays.fill is native and fast
            java.util.Arrays.fill(shiftTable, m);

            for (int i = 0; i < m - 1; i++) {
                shiftTable[pattern.charAt(i)] = m - 1 - i;
            }

            int i = m - 1;
            while (i < n) {
                int k = 0;
                while (k < m && pattern.charAt(m - 1 - k) == text.charAt(i - k)) {
                    k++;
                }
                if (k == m) {
                    if (result.length() > 0) result.append(",");
                    result.append(i - m + 1);
                }
                
                // Fast array access
                char c = text.charAt(i);
                // If text char is Unicode but pattern is ASCII, it's a mismatch -> shift m
                i += (c <= 255) ? shiftTable[c] : m;
            }
        } 
        // PATH 2: UNICODE (SAFE PATH)
        // Uses HashMap. Handles emojis, special chars etc.
        else {
            java.util.Map<Character, Integer> shiftTable = new java.util.HashMap<>();
            for (int i = 0; i < m - 1; i++) {
                shiftTable.put(pattern.charAt(i), m - 1 - i);
            }

            int i = m - 1;
            while (i < n) {
                int k = 0;
                while (k < m && pattern.charAt(m - 1 - k) == text.charAt(i - k)) {
                    k++;
                }
                if (k == m) {
                    if (result.length() > 0) result.append(",");
                    result.append(i - m + 1);
                }
                i += shiftTable.getOrDefault(text.charAt(i), m);
            }
        }

        return result.toString();
    }
}
