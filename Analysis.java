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

class BoyerMoore extends Solution {
    static {
        SUBCLASSES.add(BoyerMoore.class);
        System.out.println("BoyerMoore (Turbo) registered");
    }

    private static final int ALPHABET_SIZE = 256;

    public BoyerMoore() {
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        char[] txt = text.toCharArray();
        char[] pat = pattern.toCharArray();
        int n = txt.length;
        int m = pat.length;

        // Fast Fail: Empty or pattern longer than text
        if (m == 0) {
            for (int i = 0; i <= n; i++) indices.add(i);
            return indicesToString(indices);
        }
        if (m > n) return "";

        // --- PREPROCESSING (Bad Character Table) ---
        // We use a raw array for speed. -1 means "char not in pattern".
        int[] badChar = new int[ALPHABET_SIZE];
        for (int i = 0; i < ALPHABET_SIZE; i++) badChar[i] = -1;

        // Fill table for ASCII characters
        for (int i = 0; i < m; i++) {
            if (pat[i] < ALPHABET_SIZE) badChar[pat[i]] = i;
        }

        // --- SEARCHING (Inlined Logic) ---
        int s = 0;

        // Loop unrolling isn't needed here, JVM does it.
        // We focus on minimizing operations per step.
        while (s <= (n - m)) {
            int j = m - 1;

            // Scan backwards
            while (j >= 0 && pat[j] == txt[s + j]) {
                j--;
            }

            if (j < 0) {
                // MATCH FOUND
                indices.add(s);

                // Calculate skip for next step
                if (s + m < n) {
                    char nextC = txt[s + m];
                    int bcIndex = -1;
                    if (nextC < ALPHABET_SIZE) bcIndex = badChar[nextC];
                    else {
                        // Manual scan for Unicode (Safety Fallback)
                        for (int k = m - 1; k >= 0; k--) {
                            if (pat[k] == nextC) { bcIndex = k; break; }
                        }
                    }
                    s += (m - bcIndex);
                } else {
                    s += 1;
                }
            } else {
                // MISMATCH
                char badC = txt[s + j];
                int bcIndex = -1;

                // Optimized Lookup: Check Array if ASCII, Scan if Unicode
                if (badC < ALPHABET_SIZE) {
                    bcIndex = badChar[badC];
                } else {
                    for (int k = m - 1; k >= 0; k--) {
                        if (pat[k] == badC) { bcIndex = k; break; }
                    }
                }

                // Calculate jump
                int jump = j - bcIndex;
                if (jump < 1) jump = 1;
                s += jump;
            }
        }
        return indicesToString(indices);
    }
}


class GoCrazy extends Solution {
    static {
        SUBCLASSES.add(GoCrazy.class);
        System.out.println("GoCrazy (Turbo Raita) registered");
    }

    private static final int ALPHABET_SIZE = 256;

    public GoCrazy() {
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        char[] txt = text.toCharArray();
        char[] pat = pattern.toCharArray();
        int n = txt.length;
        int m = pat.length;

        if (m == 0) {
            for (int i = 0; i <= n; i++) indices.add(i);
            return indicesToString(indices);
        }
        if (m > n) return "";

        // --- PREPROCESSING (Horspool Shift) ---
        int[] shift = new int[ALPHABET_SIZE];
        // Default shift is full pattern length
        for (int i = 0; i < ALPHABET_SIZE; i++) shift[i] = m;

        // Fill shift table
        for (int i = 0; i < m - 1; i++) {
            if (pat[i] < ALPHABET_SIZE) shift[pat[i]] = m - 1 - i;
        }

        // --- PRE-COMPUTED INDICES (Raita's Optimization) ---
        // Checking boundaries first fails faster on mismatches
        int last = m - 1;
        int first = 0;
        int middle = m / 2;

        char pLast = pat[last];
        char pFirst = pat[first];
        char pMid = pat[middle];

        // --- SEARCHING ---
        int s = 0;
        while (s <= n - m) {
            char cLast = txt[s + last];

            // 1. GUARD CHECKS (Fastest way to find mismatch)
            if (cLast == pLast) {
                // If last char matches, check first
                if (txt[s + first] == pFirst) {
                    // If first matches, check middle
                    if (txt[s + middle] == pMid) {
                        // 2. FULL SCAN
                        // Only if all 3 guards pass, scan the rest
                        boolean match = true;
                        for (int i = 1; i < m - 1; i++) {
                            if (i == middle) continue; // Already checked
                            if (txt[s + i] != pat[i]) {
                                match = false;
                                break;
                            }
                        }
                        if (match) indices.add(s);
                    }
                }
            }

            // 3. SHIFT (Horspool Logic)
            // Look at the character aligned with the end of the pattern
            if (s + m < n) {
                char c = txt[s + m - 1]; // Character at end of window
                if (c < ALPHABET_SIZE) {
                    s += shift[c];
                } else {
                    // Unicode fallback: Safe scan
                    int step = m;
                    for (int k = m - 2; k >= 0; k--) {
                        if (pat[k] == c) { step = m - 1 - k; break; }
                    }
                    s += step;
                }
            } else {
                s++;
            }
        }
        return indicesToString(indices);
    }
}

