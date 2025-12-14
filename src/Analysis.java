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
 * Boyer-Moore string matching algorithm implementation
 * Uses bad character rule and good suffix rule for efficient pattern matching
 */
class BoyerMoore extends Solution {
    static {
        SUBCLASSES.add(BoyerMoore.class);
        System.out.println("BoyerMoore registered (Cache-Optimized)");
    }

    @Override
    public String Solve(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
        
        if (m == 0) {
            List<Integer> indices = new ArrayList<>(n + 1);
            for (int i = 0; i <= n; i++) indices.add(i);
            return indicesToString(indices);
        }
        if (m > n) return "";
        
        List<Integer> indices = new ArrayList<>();
        
        int[] badChar = preprocessBadChar(pattern);
        int[] goodSuffix = preprocessGoodSuffix(pattern);
        
        int s = 0;
        while (s <= n - m) {
            int j = m - 1;
            
            // Match from right to left
            while (j >= 0 && pattern.charAt(j) == text.charAt(s + j)) {
                j--;
            }
            
            if (j < 0) {
                // Full match
                indices.add(s);
                s += goodSuffix[0];
            } else {
                // Mismatch - compute shifts
                char mismatchChar = text.charAt(s + j);
                int badCharPos = badChar[mismatchChar & 0xFF];
                int badCharShift = (badCharPos < 0) ? j + 1 : Math.max(1, j - badCharPos);
                int goodSuffixShift = goodSuffix[j + 1];
                
                s += Math.max(badCharShift, goodSuffixShift);
            }
        }
        
        return indicesToString(indices);
    }

    private int[] preprocessBadChar(String pattern) {
        int[] badChar = new int[256];
        Arrays.fill(badChar, -1);
        
        for (int i = 0; i < pattern.length(); i++) {
            badChar[pattern.charAt(i) & 0xFF] = i;
        }
        
        return badChar;
    }

    private int[] preprocessGoodSuffix(String pattern) {
        int m = pattern.length();
        int[] goodSuffix = new int[m + 1];
        int[] border = new int[m + 1];
        
        Arrays.fill(goodSuffix, m);
        
        int i = m, j = m + 1;
        border[i] = j;
        
        while (i > 0) {
            while (j <= m && pattern.charAt(i - 1) != pattern.charAt(j - 1)) {
                if (goodSuffix[j] == m) {
                    goodSuffix[j] = j - i;
                }
                j = border[j];
            }
            i--;
            j--;
            border[i] = j;
        }
        
        j = border[0];
        for (i = 0; i <= m; i++) {
            if (goodSuffix[i] == m) {
                goodSuffix[i] = j;
            }
            if (i == j) {
                j = border[j];
            }
        }
        
        // Ensure minimum shift of 1
        for (i = 0; i <= m; i++) {
            if (goodSuffix[i] == 0) goodSuffix[i] = 1;
        }
        
        return goodSuffix;
    }
}

class GoCrazy extends Solution {
    static {
        SUBCLASSES.add(GoCrazy.class);
        System.out.println("GoCrazy registered (Adaptive Horspool++)");
    }

    @Override
    public String Solve(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
        
        if (m == 0) {
            List<Integer> indices = new ArrayList<>(n + 1);
            for (int i = 0; i <= n; i++) indices.add(i);
            return indicesToString(indices);
        }
        if (m > n) return "";
        
        List<Integer> indices = new ArrayList<>();
        
        // Adaptive strategy based on pattern length
        if (m == 1) {
            // Special case: single character (ultra-fast)
            char c = pattern.charAt(0);
            for (int i = 0; i < n; i++) {
                if (text.charAt(i) == c) indices.add(i);
            }
            return indicesToString(indices);
        }
        
        // Build skip table (Horspool)
        int[] skip = new int[256];
        Arrays.fill(skip, m);
        
        for (int i = 0; i < m - 1; i++) {
            skip[pattern.charAt(i) & 0xFF] = m - 1 - i;
        }
        
        // Cache last character for quick rejection
        char lastPatternChar = pattern.charAt(m - 1);
        
        // Main search loop
        int i = 0;
        while (i <= n - m) {
            // Quick last-character check
            char lastTextChar = text.charAt(i + m - 1);
            
            if (lastTextChar != lastPatternChar) {
                // Fast skip - no match possible
                i += skip[lastTextChar & 0xFF];
                continue;
            }
            
            // Last character matches - check rest from right to left
            int j = m - 2;
            while (j >= 0 && text.charAt(i + j) == pattern.charAt(j)) {
                j--;
            }
            
            if (j < 0) {
                // Full match found
                indices.add(i);
                // Skip intelligently: use pattern's self-overlap
                i += (m > 1) ? skip[pattern.charAt(m - 2) & 0xFF] : 1;
            } else {
                // Mismatch - use Horspool skip
                i += skip[lastTextChar & 0xFF];
            }
        }
        
        return indicesToString(indices);
    }
}