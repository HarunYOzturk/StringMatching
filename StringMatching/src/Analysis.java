//import java.util.ArrayList;
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
        System.out.println("BoyerMoore registered");
    }

    public BoyerMoore() {
    }

    // ASCII
    private static final int ALPHABET_SIZE = 256;

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        // Empty pattern
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }
        
        // Shouldnt be longer than text
        if (m > n) {
            return ""; 
        }

        // --- BAD CHARACTER RULE ---
        
        int[] badChar = new int[ALPHABET_SIZE];
        
        // Default -1
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            badChar[i] = -1;
        }

        // Their last index in pattern
        for (int i = 0; i < m; i++) {
            // text.charAt(i) indexed for ASCII value
            if (pattern.charAt(i) < ALPHABET_SIZE) {
                badChar[pattern.charAt(i)] = i;
            }
        }

        int s = 0; // Shift
        
        // Pattern is shifting on the text
        while (s <= (n - m)) {
            int j = m - 1;

            // Check the pattern from right to left
            // If matches reduce j
            while (j >= 0 && pattern.charAt(j) == text.charAt(s + j)) {
                j--;
            }

            // If j < 0, pattern is matched
            if (j < 0) {
                indices.add(s);
                
                // Shifting after the match:
                // We cant shift by pattern's size(overlap)
                // Shift by bad char rule
                if (s + m < n && text.charAt(s + m) < ALPHABET_SIZE) {
                    s += m - badChar[text.charAt(s + m)];
                } else {
                    s += 1;
                }
            } else {
                // Mismatch:
                // We shift by looking at (text.charAt(s+j))
                char mismatchChar = text.charAt(s + j);
                int lastOccurrence = -1;
                
                if (mismatchChar < ALPHABET_SIZE) {
                    lastOccurrence = badChar[mismatchChar];
                }
                
                s += Math.max(1, j - lastOccurrence);
            }
        }

        return indicesToString(indices);
    }
}
import java.util.*;

class GoCrazy extends Solution {
    
    // Register the class automatically
    static {
        SUBCLASSES.add(GoCrazy.class);
        System.out.println("GoCrazy (Hybrid: Sunday + Bitap) registered");
    }

    public GoCrazy() {
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        // Edge Case: Empty pattern matches everywhere (technically logic varies, but standard is usually this)
        if (m == 0) {
            return ""; // Or return all indices depending on requirements
        }
        
        // Edge Case: Pattern longer than text
        if (m > n) {
            return indicesToString(indices);
        }

        // HYBRID STRATEGY:
        // If the pattern is short (<= 63 characters), use Bitap (Shift-Or).
        // It uses bitwise operations which are extremely fast on modern CPUs.
        // Long is 64 bits in Java, so we can handle patterns up to length 63 safely.
        if (m <= 63) {
            solveWithBitap(text, pattern, n, m, indices);
        } else {
            // For longer patterns, use Sunday's Algorithm.
            // Sunday is an improvement over Boyer-Moore-Horspool because it looks
            // at the character *right after* the current window to decide the jump.
            solveWithSunday(text, pattern, n, m, indices);
        }

        return indicesToString(indices);
    }

    /**
     * Bitap (Shift-Or) Algorithm.
     * Fast for short patterns using bitwise parallelism.
     * Complexity: O(N) strict.
     */
    private void solveWithBitap(String text, String pattern, int n, int m, List<Integer> indices) {
        // Since we are using Shift-Or, 0 indicates a match, 1 indicates a mismatch.
        // We initialize the state with all 1s (~0L is -1, all bits set to 1).
        long state = ~0L; 
        
        // Masks for each character. 
        // We use a Map to support full Unicode (inc. Turkish chars) without a huge array.
        // For standard ASCII, an array long[256] would be slightly faster.
        Map<Character, Long> charMasks = new HashMap<>();

        // Preprocessing: Create bitmasks
        for (int i = 0; i < m; i++) {
            char c = pattern.charAt(i);
            // If char is not in map, put ~0L (all 1s)
            // Then clear the bit at position i (set to 0) to indicate match position
            long mask = charMasks.getOrDefault(c, ~0L);
            mask &= ~(1L << i);
            charMasks.put(c, mask);
        }

        // Searching
        for (int i = 0; i < n; i++) {
            // Get mask for current text char. If not in pattern, default is all 1s (mismatch everywhere)
            long mask = charMasks.getOrDefault(text.charAt(i), ~0L);
            
            // Update state:
            // 1. Shift state left by 1
            // 2. OR with the mask of the current character
            state = (state << 1) | mask;

            // Check if the bit corresponding to the end of the pattern (m-1) is 0.
            // If the (m-1)th bit is 0, it means we have matched the entire pattern ending at i.
            if ((state & (1L << (m - 1))) == 0) {
                indices.add(i - m + 1);
            }
        }
    }

    /**
     * Sunday's Algorithm.
     * Often faster than Horspool because it jumps based on the character
     * OUTSIDE the current window (text[i+m]).
     */
    private void solveWithSunday(String text, String pattern, int n, int m, List<Integer> indices) {
        // Shift table: stores the LAST occurrence index of a character in the pattern.
        // Using int[256] for speed on ASCII. 
        // For full Unicode safety, we check bounds.
        int[] shiftTable = new int[256];
        
        // Initialize with -1
        Arrays.fill(shiftTable, -1);

        // Fill table with right-most occurrence
        for (int i = 0; i < m; i++) {
            char c = pattern.charAt(i);
            if (c < 256) {
                shiftTable[c] = i;
            }
        }

        int i = 0;
        while (i <= n - m) {
            // Check for match at current position
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

            // Calculate shift
            if (i + m < n) {
                char nextChar = text.charAt(i + m);
                
                int lastOccurInPattern;
                if (nextChar < 256) {
                    lastOccurInPattern = shiftTable[nextChar];
                } else {
                    // For non-ASCII chars not in our small table, 
                    // we scan the pattern manually or default to -1 (safe but slower for those chars).
                    // To keep "GoCrazy" fast for ASCII but safe for others:
                    lastOccurInPattern = -1; 
                    // Manual scan fallback (optional, for correctness on Unicode heavy texts):
                    for(int k = m-1; k >=0; k--) {
                        if(pattern.charAt(k) == nextChar) {
                            lastOccurInPattern = k;
                            break;
                        }
                    }
                }

                // Sunday's formula: Shift amount = m - last_occurrence_index
                // If char is not in pattern (lastOccurInPattern == -1), we jump m + 1 steps.
                i += (m - lastOccurInPattern);
            } else {
                break;
            }
        }
    }
}
