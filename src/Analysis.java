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

        // KMP/RabinKarp ile uyumlu: empty pattern her pozisyonda eşleşir
        if (m == 0) {
            for (int i = 0; i <= n; i++) indices.add(i);
            return indicesToString(indices);
        }

        if (m > n) {
            return "";
        }

        // 1) Bad character table (last occurrence)
        int[] bc = buildBadCharTable(pattern);

        // 2) Good suffix preprocessing
        int[] suffix = new int[m];      // suffix[k] = pattern içinde suffix uzunluğu k olan eşleşmenin başlangıcı
        boolean[] prefix = new boolean[m]; // prefix[k] = suffix uzunluğu k aynı zamanda prefix mi?
        generateGoodSuffix(pattern, suffix, prefix);

        int i = 0; // text üzerinde kayma
        while (i <= n - m) {
            int j;

            // Sağdan sola karşılaştır
            for (j = m - 1; j >= 0; j--) {
                if (text.charAt(i + j) != pattern.charAt(j)) break;
            }

            if (j < 0) {
                // Tam eşleşme
                indices.add(i);

                // Tam eşleşmeden sonra kaydırma: en uzun border (prefix==suffix)
                int shift = shiftAfterFullMatch(m, prefix);
                i += shift;
            } else {
                // Bad character shift
                char bad = text.charAt(i + j);
                int bcShift = j - bc[bad & 0xFFFF];
                if (bcShift < 1) bcShift = 1;

                // Good suffix shift
                int gsShift = moveByGoodSuffix(j, m, suffix, prefix);

                i += Math.max(bcShift, gsShift);
            }
        }

        return indicesToString(indices);
    }

    // ----------------- Bad Character -----------------
    private int[] buildBadCharTable(String pattern) {
        int[] bc = new int[65536]; // Java char aralığı
        for (int i = 0; i < bc.length; i++) bc[i] = -1;

        for (int i = 0; i < pattern.length(); i++) {
            bc[pattern.charAt(i) & 0xFFFF] = i;
        }
        return bc;
    }

    // ----------------- Good Suffix Preprocess -----------------
    private void generateGoodSuffix(String pattern, int[] suffix, boolean[] prefix) {
        int m = pattern.length();
        for (int i = 0; i < m; i++) {
            suffix[i] = -1;
            prefix[i] = false;
        }

        // i: pattern içinde "suffix matching" başlatacağımız yer
        for (int i = 0; i < m - 1; i++) {
            int j = i;
            int k = 0; // eşleşen suffix uzunluğu

            // pattern[j] ile pattern[m-1-k] eşleştiği sürece geri git
            while (j >= 0 && pattern.charAt(j) == pattern.charAt(m - 1 - k)) {
                j--;
                k++;
                suffix[k] = j + 1; // uzunluk k olan suffix'in pattern içindeki başlangıcı
            }

            // Eğer j == -1 ise: pattern’in prefix’i aynı zamanda suffix oldu
            if (j == -1) {
                prefix[k] = true;
            }
        }
    }

    // mismatch index j iken (0..m-1), good suffix kaydırması
    private int moveByGoodSuffix(int j, int m, int[] suffix, boolean[] prefix) {
        int k = m - 1 - j; // good suffix uzunluğu
        if (k <= 0) return 1;

        // Case 1: pattern içinde aynı suffix var
        if (suffix[k] != -1) {
            return j - suffix[k] + 1;
        }

        // Case 2: suffix'in bir parçası pattern'in prefix'i olabilir mi?
        for (int r = j + 2; r <= m - 1; r++) {
            int len = m - r; // prefix uzunluğu
            if (prefix[len]) {
                return r;
            }
        }

        // Case 3: hiçbir şey yoksa komple kaydır
        return m;
    }

    // Tam eşleşmeden sonra kaydırma (border kullan)
    private int shiftAfterFullMatch(int m, boolean[] prefix) {
        // m-1'den 1'e kadar en uzun prefix==suffix uzunluğunu bul
        for (int k = m - 1; k >= 1; k--) {
            if (prefix[k]) {
                return m - k;
            }
        }
        return m;
    }
}

/**
 * TODO: Implement your own creative string matching algorithm
 * This is a homework assignment for students
 * Be creative! Try to make it efficient for specific cases
 */
/**
 * Task 2: Implement Your Own Algorithm
 * Algorithm: Hybrid Sunday-QuickSearch
 * * Strategy:
 * 1. Small Pattern Heuristic: If pattern length <= 5, overhead of pre-processing
 * arrays is too high. Use a simplified Naive approach.
 * 2. Sunday's Algorithm: For larger patterns, look at the character *after* * the current window to decide how far to jump. This often allows larger
 * shifts than KMP or standard Boyer-Moore.
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
        int n = text.length();
        int m = pattern.length();
        List<Integer> indices = new ArrayList<>();

        // 1. Edge Case: Empty pattern matches everywhere
        if (m == 0) {
            for (int i = 0; i <= n; i++) indices.add(i);
            return indicesToString(indices);
        }

        // 2. Edge Case: Pattern longer than text
        if (m > n) {
            return "";
        }

        // 3. HYBRID STRATEGY
        // If the pattern is very short, the overhead of creating arrays
        // slows us down. Use a "Fast Naive" approach instead.
        if (m <= 5) {
            return solveFastNaive(text, pattern, n, m, indices);
        }

        // 4. MAIN ALGORITHM: Sunday's Algorithm (Quick Search)
        // Pre-processing for Sunday's Shift
        // We use an array for ASCII (size 256).
        int[] shift = new int[256];

        // Default shift is m + 1 (jump past the whole pattern + 1)
        for (int k = 0; k < 256; k++) {
            shift[k] = m + 1;
        }

        // Populate shift table based on pattern
        // shift[c] = distance from character c to the END of the pattern
        for (int k = 0; k < m; k++) {
            char c = pattern.charAt(k);
            if (c < 256) {
                shift[c] = m - k;
            }
        }

        int i = 0;
        while (i <= n - m) {
            // Check for match at current position i
            int j = 0;
            while (j < m && text.charAt(i + j) == pattern.charAt(j)) {
                j++;
            }

            // If we found a full match
            if (j == m) {
                indices.add(i);
            }

            // SHIFT LOGIC (Sunday's Trick)
            // Instead of looking at the mismatch inside the pattern (like BM),
            // look at the character in the text right AFTER the pattern.
            if (i + m < n) {
                char nextChar = text.charAt(i + m);
                if (nextChar < 256) {
                    i += shift[nextChar];
                } else {
                    i += 1; // Fallback for non-ASCII characters
                }
            } else {
                break; // End of text
            }
        }

        return indicesToString(indices);
    }

    /**
     * Optimized Naive for short strings.
     * Avoids array allocation overhead.
     */
    private String solveFastNaive(String text, String pattern, int n, int m, List<Integer> indices) {
        char first = pattern.charAt(0);
        // Loop through text
        for (int i = 0; i <= n - m; i++) {
            // Quick check: only proceed if first char matches
            if (text.charAt(i) != first) {
                continue;
            }

            // Standard check
            int j = 1;
            while (j < m) {
                if (text.charAt(i + j) != pattern.charAt(j)) {
                    break;
                }
                j++;
            }

            if (j == m) {
                indices.add(i);
            }
        }
        return indicesToString(indices);
    }
}
