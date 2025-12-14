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

    public BoyerMoore() {}

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

  
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }

        if (m > n) { //pattern longer than text, return empty
            return "";
        }

        // Build preprocessing tables
        //bad char -> the righmost position of each character in the pattern
        //good suffix -> unmatched suffix tell us how much we can shift the pattern
        java.util.HashMap<Character, Integer> badChar = buildBadCharTable(pattern);
        int[] goodSuffix = buildGoodSuffixTable(pattern);

        int s = 0; // shift of the pattern with respect to text
        while (s <= n - m) {
            int j = m - 1; // compare from the rightmost character

            //matching from right to left
            while (j >= 0 && pattern.charAt(j) == text.charAt(s + j)) {
                j--;
            }

            if (j < 0) {
                // full pattern found at position s
                indices.add(s);
                // Shift pattern - use good suffix table to designate how much to shift
                s += goodSuffix[0];
            } else {
                // Mismatch
  
                char mismatchedChar = text.charAt(s + j);
                int badCharShift = j - getBadCharShift(badChar, mismatchedChar);// calculate shift using bad char rule
                
                // calculate shift using good suffix rule
                int goodSuffixShift = goodSuffix[j + 1];
                
                //maximum of both shifts to shift as much as possible
                s += Math.max(Math.max(1, badCharShift), goodSuffixShift);
            }
        }

        return indicesToString(indices);
    }

    
    //Uses HashMap for Unicode support (sparse storage for efficiency)
    //Time: O(m)
    private java.util.HashMap<Character, Integer> buildBadCharTable(String pattern) {
        java.util.HashMap<Character, Integer> badChar = new java.util.HashMap<>();
        int m = pattern.length();
        
        // Store the rightmost occurrence of each character
        for (int i = 0; i < m; i++) {
            badChar.put(pattern.charAt(i), i);
        }
        
        return badChar;
    }

    //Good Suffix Table: Preprocessing for good suffix heuristic
    //Time: O(m)
    private int[] buildGoodSuffixTable(String pattern) {
        int m = pattern.length();
        int[] shift = new int[m + 1];
        int[] bpos = new int[m + 1]; // Border position
        
        // Initialize all shifts to pattern length
        for (int i = 0; i <= m; i++) {
            shift[i] = m;
        }
        
        //find strong suffixes
        int i = m;
        int j = m + 1;
        bpos[i] = j;
        
        while (i > 0) {
          
            while (j <= m && pattern.charAt(i - 1) != pattern.charAt(j - 1)) {
                if (shift[j] == m) {
                    shift[j] = j - i;
                }
                j = bpos[j];
            }
            i--;
            j--;
            bpos[i] = j;
        }
        
        //suffix not in the pattern - desginate shift value
        j = bpos[0];
        for (i = 0; i <= m; i++) {
            // If shift is still default, use border position
            if (shift[i] == m) {
                shift[i] = j;
            }
            //update j
            if (i == j) {
                j = bpos[j];
            }
        }
        
        return shift;
    }

   //if char not in the pattern, return -1
   //shift = j+1
    private int getBadCharShift(java.util.HashMap<Character, Integer> badChar, char c) {
        return badChar.getOrDefault(c, -1);
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

    //this algorithm is changed and updated after the preanalysis results. the mos recent version engineered to handle:
    //long text + possibly many match
    //small alphabet + long pattern
    //pattern with spaces
    //near match
    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        //empty pattern
        if (m == 0) {
            for (int i = 0; i <= n; i++) indices.add(i);
            return indicesToString(indices);
        }
        if (m > n) return "";

        
        if (!isGoCrazyStrongCase(pattern, text)) {
            return new Naive().Solve(text, pattern);
        }//this safety fallback is to successfully handle all test cases, and when the gocrazy is not suitable, it will fallback to naive

        // look for prefix 
        String prefix = pattern.substring(0, Math.min(8, m)); // fast indexOf
        int i = 0;

        while (i <= n - m) {
            int next = text.indexOf(prefix, i);
            if (next == -1 || next > n - m) break;

            i = next;

            // prefix found -check if full match
            if (text.startsWith(pattern, i)) {
                indices.add(i);
                // if overlap - skip small
                i += calculateOverlapSkip(pattern);
            } else {
                i += 1;
            }
        }

        return indicesToString(indices);
    }

   
    private boolean isGoCrazyStrongCase(String pattern, String text) {
        int pLen = pattern.length();
        int tLen = text.length();
//here are some of the baseline strong cases for goCrazy.
    if (pLen > tLen) {
        return true;
    }

    if (tLen > 10000) {
        return true;
    }

    if (tLen > 5000 && pLen >= 10) {
        return true;
    }

  
    if (pLen >= 12 && getAlphabetSize(pattern) <= 8) {
        return true;
    }

    if (pattern.indexOf(' ') != -1) {
        return true;
    }

 
    if (pLen >= 15 && tLen > pLen * 15) {
        return true;
    }

    return false;
}

    // calc alphabet size (small is good)
    private int getAlphabetSize(String s) {
        boolean[] seen = new boolean[256];
        int count = 0;
        for (char c : s.toCharArray()) {
            if (!seen[c & 0xFF]) {
                seen[c & 0xFF] = true;
                if (++count > 10) return count; // erken çıkış
            }
        }
        return count;
    }

    // kmp style, find overlap to calculate skip
    private int calculateOverlapSkip(String pattern) {
        int m = pattern.length();
        int[] lps = computeLPS(pattern);
        int overlap = lps[m - 1];
        return overlap > 0 ? overlap : m; // if contains overlap, skip that much, else skip the whole pattern
    }
//lps calculation to find overlap
    private int[] computeLPS(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;
        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                lps[i++] = ++len;
            } else if (len != 0) {
                len = lps[len - 1];
            } else {
                lps[i++] = 0;
            }
        }
        return lps;
    }
}


Name: Furkan Öztürk
Student Number: 23050151018


my teammate: sina erdem özdemir
his no: 21050151019