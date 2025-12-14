//Rümeysa Kara 22050111053
//Aybüke Karaçavuş 22050111005

import java.util.ArrayList;
import java.util.List;
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
 * TODO: Implement Boyer-Moore algorithm This is a homework assignment for
 * students
 */

/* In implementing this algorithm, we utilized Boyer-Moore's fundamental principle: 
we begin character comparisons from the right of the pattern. 
When a match breaks, we use a "bad character" shift to skip unnecessary comparisons. */
class BoyerMoore extends Solution {

    static {
        SUBCLASSES.add(BoyerMoore.class);
        System.out.println("BoyerMoore registered");
    }

    public BoyerMoore() {
    }

    @Override
    public String Solve(String text, String pattern) {
        // TODO: Students should implement Boyer-Moore algorithm here
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        
        //If pattern is empty, We accept matches for all positions.
        if (m == 0) {
            for (int i = 0; i <= n; i++) indices.add(i);
            return indicesToString(indices);
        }

        /* We are creating a bad character table.
        The purpose here is to calculate how much to shift the pattern if an incorrect character is detected during comparison.  
        */
        int[] badChar = new int[65536];
        /*We start all characters with -1 (meaning they are not in the pattern). */ 
        for (int i = 0; i < 65536; i++) badChar[i] = -1;
        /*We are processing the last seen index within the pattern. */
        for (int i = 0; i < m; i++) badChar[pattern.charAt(i)] = i;

        int s = 0;     /*// 's' is the position where the pattern is shifted on the text. */
        while (s <= (n - m)) {
            int j = m - 1;
            // We move backward as long as the characters remain the same.
            while (j >= 0 && pattern.charAt(j) == text.charAt(s + j)) j--;

            /* If j < 0, it means all characters match → we found the solution. */
            if (j < 0) {
                indices.add(s);
                /*We're shifting the pattern accordingly. 
                If there's still space in the text, we shift based on the bad-character. */ 
                s += (s + m < n) ? m - badChar[text.charAt(s + m)] : 1;
            } else {
                /*The match is broken. We're applying a bad character shift. 
                But we shift at least one character, otherwise it will be an infinite loop. */
                s += Math.max(1, j - badChar[text.charAt(s + j)]);
            }
        }

        return indicesToString(indices);
    }
}

/*
 TODO: Implement your own creative string matching algorithm This is a
homework assignment for students Be creative! Try to make it efficient for specific cases
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
        // In this algorithm, our goal is not to create a single universal string-search method.
        // Instead, we design an adaptive approach that chooses the most suitable algorithm
        // (Naive, KMP, or Rabin-Karp) depending on the structure of the pattern.
        //
        // In other words, GoCrazy is not a standalone matching algorithm,
        // but a strategy that dynamically selects the optimal method for the given case.
        
        List<Integer> indices = new ArrayList<>();

        if (pattern.length() == 0 || text.length() < pattern.length())
            return indicesToString(indices);
        
        // We select the appropriate algorithm based on pattern properties.
        String algorithm = chooseAlgorithm(text, pattern);

        // We execute the chosen method.
        switch (algorithm) {
            case "KMP":
                kmpSearch(text, pattern, indices);
                break;
            case "RabinKarp":
                rabinKarpSearch(text, pattern, indices);
                break;
                
            // If no special situation is detected, we fall back to the Naive algorithm.    
            default:
                naiveSearch(text, pattern, indices);
        }

        return indicesToString(indices);
    }
    private static String chooseAlgorithm(String text, String pattern) {
        int m = pattern.length();
        
        // If the pattern is very short, the Naive method generally outperforms others,
        // because more advanced preprocessing is unnecessary overhead.
        if (m <= 4)
            return "Naive";

        // If the pattern contains repeated structures (e.g., “ababab”, “aaaaabcaaaaa”),
        // KMP becomes significantly more efficient.
        if (hasRepetitiveStructure(pattern))
            return "KMP";

        // If the pattern is long and contains a large variety of characters,
        // Rabin-Karp's hashing approach can perform well.
        if (m > 10 && alphabetSize(pattern) > m / 2)
            return "RabinKarp";
        
        // In all other cases, the Naive algorithm is sufficient.
        return "Naive";
    }

    // naive algorithm
    private static void naiveSearch(String text, String pattern, List<Integer> indices) {
        for (int i = 0; i <= text.length() - pattern.length(); i++) {
            int j = 0;
            while (j < pattern.length() &&
                    text.charAt(i + j) == pattern.charAt(j)) {
                j++;
            }
            if (j == pattern.length())
                indices.add(i);
        }
    }

    // kmp algorithm
    private static void kmpSearch(String text, String pattern, List<Integer> indices) {
        int[] lps = buildLPS(pattern);
        int i = 0, j = 0;

        while (i < text.length()) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
            }

            if (j == pattern.length()) {
                indices.add(i - j);
                j = lps[j - 1];
            } else if (i < text.length() &&
                    text.charAt(i) != pattern.charAt(j)) {
                if (j != 0)
                    j = lps[j - 1];
                else
                    i++;
            }
        }
    }

    private static int[] buildLPS(String pattern) {
        int[] lps = new int[pattern.length()];
        int len = 0, i = 1;

        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
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

    // rabin karp
    private static void rabinKarpSearch(String text, String pattern, List<Integer> indices) {
        int m = pattern.length();
        int n = text.length();
        int base = 256;
        int mod = 101;

        int pHash = 0, tHash = 0, h = 1;

        for (int i = 0; i < m - 1; i++)
            h = (h * base) % mod;

        for (int i = 0; i < m; i++) {
            pHash = (base * pHash + pattern.charAt(i)) % mod;
            tHash = (base * tHash + text.charAt(i)) % mod;
        }

        for (int i = 0; i <= n - m; i++) {
            if (pHash == tHash) {
                int j = 0;
                while (j < m &&
                        text.charAt(i + j) == pattern.charAt(j)) {
                    j++;
                }
                if (j == m)
                    indices.add(i);
            }

            if (i < n - m) {
                tHash = (base * (tHash - text.charAt(i) * h)
                        + text.charAt(i + m)) % mod;
                if (tHash < 0)
                    tHash += mod;
            }
        }
    }

    private static boolean hasRepetitiveStructure(String pattern) {
        int[] lps = buildLPS(pattern);
        int len = lps[pattern.length() - 1];
        return len > pattern.length() / 3;
    }

    private static int alphabetSize(String pattern) {
        return (int) pattern.chars().distinct().count();
    
        //throw new UnsupportedOperationException("GoCrazy algorithm not yet implemented - this is your homework!");
    }
}
