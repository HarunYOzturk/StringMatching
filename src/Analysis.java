//Mehmet Demir 22050111024
//Ayşe Zülal Şimşek 22050111047

import java.util.ArrayList;
import java.util.Arrays;
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

        if (m == 0) { //empty pattern
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }

        if (m > n) {//if the pattern is longer than the text,there is no match
            return "";
        }

        int[] badChar = buildBadCharTable(pattern);

        int s = 0; //offset of the paattern sliiding on the text
        while (s <= n - m) {
            int j = m - 1;
            //compare from right to left
            while (j >= 0 && pattern.charAt(j) == text.charAt(s + j)) {
                j--;
            }

            if (j < 0) {
                // all characters are matched, an occurrence was found
                indices.add(s);

                //scroll to next possible position
                if (s + m < n) {
                    char nextChar = text.charAt(s + m);
                    int lastOcc = badChar[nextChar & 0xFF];
                    int shift = m - lastOcc;
                    if (shift <= 0) shift = 1;
                    s += shift;
                } else {
                    s += 1;
                }

            } else {
                //bad character scrolling for mismatched characters
                char bad = text.charAt(s + j);
                int lastOcc = badChar[bad & 0xFF];
                int shift = j - lastOcc;
                if (shift < 1) shift = 1;
                s += shift;
            }
        }

        return indicesToString(indices);
    }

    private int[] buildBadCharTable(String pattern) {
        int ALPHABET_SIZE = 256;
        int[] table = new int[ALPHABET_SIZE];
        Arrays.fill(table, -1);

        for (int i = 0; i < pattern.length(); i++) {
            table[pattern.charAt(i) & 0xFF] = i;
        }

        return table;
    }

}

/**
 * GoCrazy Algorithm 
 *
 * We studied string matching algorithms for this homework. We noticed that
 * Boyer-Moore is fast but it is very complex to write perfectly.
 * Also, even when it skips, it checks many characters.
 *
 * So we thought that can we make a faster check before full comparison
 * We decided to check only 3 characters first such as a quick filter
 *
 * Our strategy:
 * 1. We pick 3 important positions in pattern (start, middle, end)
 * 2. We check these 3 points first
 * 3. If they match, we check the rest of the pattern
 * 4. If they dont match, we use skip table to jump forward
 *
 * Why we chose 3 positions:
 * In normal text, usually these positions are different.
 * So we can reject wrong positions very fast.
 *
 * We check the last character first because in English/Turkish words,
 * endings change a lot (like -ing, -lar, -de). Then we check first and middle.
 *
 * For the skipping part, we studied the Horspool algorithm.
 * We learned that it has a safe skip table that never misses matches.
 * So we used that part from Horspool.
 *
 * This combination works very good for normal text.
 * The 3-character check is our idea to filter fast.
 * The skip table makes sure we are safe.
 *
 * Performance:
 * Best case: Very fast (O(n/m)) because we skip a lot.
 * Normal case: Fast (O(n)), we check few chars per position.
 * Worst case: O(n*m) - but we didn't see this much in real tests.
 *
 * It works good for:
 * - Normal languages (English, Turkish sentences)
 * - Medium/Long patterns
 * - Big alphabet
 *
 * Not very good for:
 * - Repeating text like "aaaaaaa" (skip is small)
 * - DNA (only 4 letters, so fingerprints match often)
 * - Very short patterns 
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
        // checking inputs to be safe
        if (text == null || pattern == null) {
            return "";
        }
        
        // empty pattern matches everywhere (standard rule)
        if (pattern.isEmpty()) {
            return matchEmptyPattern(text.length());
        }
        
        int textLen = text.length();
        int patternLen = pattern.length();
        
        // pattern cannot be longer than text
        if (patternLen > textLen) {
            return "";
        }
        
        // for very short patterns our method is too heavy
        // so we just use simple check
        if (patternLen <= 2) {
            return searchShortPattern(text, pattern, textLen, patternLen);
        }
        
        // using our hybrid method for normal patterns
        return hybridFingerprintSearch(text, pattern, textLen, patternLen);
    }
    
    // logic for empty pattern - matches every index
    private String matchEmptyPattern(int textLen) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i <= textLen; i++) {
            if (result.length() > 0) {
                result.append(",");
            }
            result.append(i);
        }
        return result.toString();
    }
    
    // simple scanning for short patterns (1-2 chars)
    private String searchShortPattern(String text, String pattern, int textLen, int patternLen) {
        StringBuilder matches = new StringBuilder();
        
        for (int pos = 0; pos <= textLen - patternLen; pos++) {
            boolean match = true;
            
            for (int j = 0; j < patternLen; j++) {
                if (text.charAt(pos + j) != pattern.charAt(j)) {
                    match = false;
                    break;
                }
            }
            
            if (match) {
                if (matches.length() > 0) {
                    matches.append(",");
                }
                matches.append(pos);
            }
        }
        
        return matches.toString();
    }
    
    /**
     * Our main search method.
     *
     * We combined two ideas here:
     * 1. Quick fingerprint check (our idea)
     * 2. Safe skip table (from Horspool)
     *
     * How it works:
     * - We pick 3 positions from pattern.
     * - We build the skip table.
     * - In the loop:
     * First check 3 fingerprint points.
     * If they match check the whole pattern.
     * If they dont match use table to skip forward.
     *
     * Most positions fail at fingerprint, so we save time.
     * Skip table guarantees we dont miss anything.
     */
    private String hybridFingerprintSearch(String text, String pattern, int textLen, int patternLen) {
        StringBuilder matches = new StringBuilder();
        
        // choosing 3 positions for fingerprint
        int[] fpPoints = selectFingerprintPoints(pattern, patternLen);
        int fp1 = fpPoints[0]; // first
        int fp2 = fpPoints[1]; // middle
        int fp3 = fpPoints[2]; // last
        
        // saving characters to variables for fast access
        char finger1 = pattern.charAt(fp1);
        char finger2 = pattern.charAt(fp2);
        char finger3 = pattern.charAt(fp3);
        
        // building skip table for safe jumping
        int[] skipTable = buildSkipTable(pattern, patternLen);
        
        int currentPos = 0;
        
        while (currentPos <= textLen - patternLen) {
            
            // Checking fingerprint points now
            // We check LAST one first because it changes most in words
            if (text.charAt(currentPos + fp3) == finger3) {
                
                // last matched, checking first
                if (text.charAt(currentPos + fp1) == finger1) {
                    
                    // first matched too, checking middle
                    if (text.charAt(currentPos + fp2) == finger2) {
                        
                        // all 3 points matched
                        // now we must check the rest of the pattern
                        boolean fullMatch = true;
                        
                        // checking characters we didn't check yet
                        for (int i = 0; i < patternLen; i++) {
                            // skipping points we already checked
                            if (i == fp1 || i == fp2 || i == fp3) {
                                continue;
                            }
                            
                            if (text.charAt(currentPos + i) != pattern.charAt(i)) {
                                fullMatch = false;
                                break;
                            }
                        }
                        
                        if (fullMatch) {
                            // we found a match
                            if (matches.length() > 0) {
                                matches.append(",");
                            }
                            matches.append(currentPos);
                            
                            // moving by 1 to find overlapping matches
                            // for example finding "AAA" in "AAAA" needs position 0 and 1
                            currentPos++;
                            continue;
                        }
                    }
                }
            }
            
            // Fingerprint didn't match
            // We use skip table to jump forward safely
            char skipChar = text.charAt(currentPos + patternLen - 1);
            int skipDist = getSkipValue(skipTable, skipChar, patternLen);
            currentPos += skipDist;
        }
        
        return matches.toString();
    }
    
    /**
     * Selecting fingerprint positions
     *
     * We always use first and last position
     * For middle, we just use the center
     *
     * We also check if points are same (for short patterns 3-4 chars)
     * and fix them so we check different chars
     */
    private int[] selectFingerprintPoints(String pattern, int patternLen) {
        int[] points = new int[3];
        
        points[0] = 0;
        points[2] = patternLen - 1;
        points[1] = patternLen / 2;
        
        //avoiding duplicate points
        if (points[1] == points[0]) {
            points[1] = Math.min(1, patternLen - 1);
        }
        if (points[1] == points[2]) {
            points[1] = Math.max(patternLen - 2, 0);
        }
        
        return points;
    }
    
    /**
     * Building skip table using Horspool method
     *
     * Ideally, for each character we decide how far to skip
     * If char is not in pattern skip whole pattern
     * If char is in pattern skip to align it
     *
     * This way is proven safe.We learned this from reading about Horspool
     *
     */
    private int[] buildSkipTable(String pattern, int patternLen) {
        int[] table = new int[256];
        
        // default skip is full pattern length
        for (int i = 0; i < 256; i++) {
            table[i] = patternLen;
        }
        
        // setting skip values for characters in pattern
        // we dont include last position as reference point
        for (int i = 0; i < patternLen - 1; i++) {
            char c = pattern.charAt(i);
            // only for ascii
            if (c < 256) {
                table[c] = patternLen - 1 - i;
            }
        }
        
        return table;
    }
    
    /**
     * Getting skip value safely
     *
     * Our table has 256 entries for ASCII
     * But Java char can be Unicode (bigger numbers)
     * So we handle both cases here to prevent errors
     */
    private int getSkipValue(int[] skipTable, char c, int patternLen) {
        if (c < 256) {
            return skipTable[c];
        }
        // for unicode character, we just skip full length
        return patternLen;
    }
}