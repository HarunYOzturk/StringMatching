/*
Muhammet Enes Varol - 22050111041
Mehmet Emin Kaya - 22050111034
 */
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

    // --- Preprocessing: Bad Character Heuristic ---
    // Stores the last position of each character in the pattern
    private Map<Character, Integer> buildBadCharTable(String pattern) {
        int m = pattern.length();
        Map<Character, Integer> badCharTable = new HashMap<>();
        for (int i = 0; i < m; i++) {
            badCharTable.put(pattern.charAt(i), i);
        }
        return badCharTable;
    }

    // --- Helper for Good Suffix Heuristic ---
    // Computes border array for suffix analysis
    private int[] computeSuffixBorder(String pattern) {
        int m = pattern.length();
        int[] border = new int[m + 1];
        int i = m;
        int j = m + 1;
        border[i] = j;

        while (i > 0) {
            while (j <= m && pattern.charAt(i - 1) != pattern.charAt(j - 1)) {
                j = border[j];
            }
            i--;
            j--;
            border[i] = j;
        }
        return border;
    }

    // --- Preprocessing: Good Suffix Heuristic ---
    // Calculates shift amounts based on matching suffixes
    private int[] computeGoodSuffixShift(String pattern, int[] border) {
        int m = pattern.length();
        int[] gsShift = new int[m + 1];
        
        // Case 1: Suffix matches a prefix of pattern
        int j = border[0]; 
        for (int i = 0; i <= m; i++) {
            gsShift[i] = m - j; 
            if (i == j) {
                j = border[j];
            }
        }

        // Case 2: Suffix matches elsewhere in pattern
        for (int i = 0; i <= m; i++) {
            if (border[i] < m) {
                gsShift[border[i]] = Math.min(gsShift[border[i]], m - border[i]);
            }
        }
        return gsShift;
    }

    @Override
    public String Solve(String text, String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            if (text == null) return "";
            StringBuilder sb = new StringBuilder();
            for (int k = 0; k <= text.length(); k++) {
                sb.append(k);
                if (k < text.length()) sb.append(",");
            }
            return sb.toString();
        }
        
        // If text shorter than pattern
        if (text == null || text.length() < pattern.length()) return "";

        int n = text.length();
        int m = pattern.length();
        List<Integer> matches = new ArrayList<>();

        Map<Character, Integer> badCharTable = buildBadCharTable(pattern);
        int[] border = computeSuffixBorder(pattern);
        int[] gsShift = computeGoodSuffixShift(pattern, border);

        int shift = 0;

        while (shift <= (n - m)) {
            int j = m - 1;

            while (j >= 0 && pattern.charAt(j) == text.charAt(shift + j)) {
                j--;
            }

            if (j < 0) {
                matches.add(shift);
                
                shift += 1;
                
            } else {
                
                // Calculate Bad Character Rule Shift
                char badChar = text.charAt(shift + j);
                int lastIndexInPattern = badCharTable.getOrDefault(badChar, -1); 
                int badCharShift = j - lastIndexInPattern; 
                
                // Calculate Good Suffix Rule Shift
                int goodSuffixShift = gsShift[m - 1 - j];

                // Take the maximum shift (Math.max(1, ...) prevents infinite loops)
                shift += Math.max(1, Math.max(badCharShift, goodSuffixShift));
            }
        }
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < matches.size(); i++) {
            result.append(matches.get(i));
            if (i < matches.size() - 1) {
                result.append(",");
            }
        }

        return result.toString();
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
        // Edge Case: If pattern is empty, it matches at every index (0,1,2...n)
        // This mimics the behavior of the Naive algorithm for consistency
        if (pattern == null || pattern.isEmpty()) {
            if (text == null) return "";
            StringBuilder sb = new StringBuilder();
            for (int k = 0; k <= text.length(); k++) {
                sb.append(k);
                if (k < text.length()) sb.append(",");
            }
            return sb.toString();
        }
        
        List<Integer> matches;

        // --- Hybrid Strategy ---
        // Analyze the pattern to decide the best algorithm
        if (isRepetitive(pattern)) {
            // Use KMP for repetitive patterns (e.g., "AAAA") to avoid O(n*m) worst case
            matches = kmpSearch(text, pattern);
        } else {
            // Use Optimized Naive for normal/random patterns because it has low overhead
            matches = naiveFast(text, pattern);
        }

        return formatMatches(matches);
    }
    
    // --- Heuristic Analysis ---
    // Checks if the pattern has many repeating characters (like "AAAA" or "ABAB")
    private boolean isRepetitive(String p) {
        int m = p.length();
        int sameCharCount = 0;
        
        // Count how many times a character is the same as the previous one
        for (int i = 1; i < m; i++) {
            if (p.charAt(i) == p.charAt(i - 1)) {
                sameCharCount++;
            }
        }

        // If more than 50% of the pattern is repetitive, return true
        return sameCharCount > m / 2;
    }

    // --- Algorithm 1: Optimized Naive ---
    // Fast for random text, zero preprocessing cost
    private List<Integer> naiveFast(String text, String pat) {
        int n = text.length(), m = pat.length();
        List<Integer> matches = new ArrayList<>();

        for (int i = 0; i <= n - m; i++) {
            // Optimization: Skip immediately if the first character doesn't match
            if (text.charAt(i) != pat.charAt(0)) continue;

            boolean match = true;
            for (int j = 1; j < m; j++) {
                if (text.charAt(i + j) != pat.charAt(j)) {
                    match = false;
                    break;
                }
            }
            if (match) matches.add(i);
        }
        return matches;
    }

    // --- Algorithm 2: KMP (Knuth-Morris-Pratt) ---
    // Efficient for periodic patterns, guarantees linear time O(n)
    private List<Integer> kmpSearch(String txt, String pat) {
        int n = txt.length();
        int m = pat.length();
        List<Integer> matches = new ArrayList<>();

        // Build the failure function (LPS array)
        int[] lps = computeLPS(pat);

        int i = 0, j = 0; 
        while (i < n) {
            if (txt.charAt(i) == pat.charAt(j)) {
                i++; j++;
                if (j == m) {
                    matches.add(i - j);
                    // Use LPS to shift pattern for next match
                    j = lps[j - 1]; 
                }
            } else {
                // Mismatch handling using LPS to skip comparisons
                if (j != 0)
                    j = lps[j - 1];
                else
                    i++;
            }
        }
        return matches;
    }

    // --- Helper for KMP ---
    // Computes Longest Proper Prefix which is also Suffix (LPS)
    private int[] computeLPS(String pat) {
        int m = pat.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;

        while (i < m) {
            if (pat.charAt(i) == pat.charAt(len)) {
                lps[i++] = ++len;
            } else {
                if (len != 0)
                    len = lps[len - 1];
                else
                    lps[i++] = 0;
            }
        }
        return lps;
    }

    private String formatMatches(List<Integer> matches) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < matches.size(); i++) {
            result.append(matches.get(i));
            if (i < matches.size() - 1) {
                result.append(",");
            }
        }
        return result.toString();
    }
}

