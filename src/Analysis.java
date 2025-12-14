import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
        int n = text.length();
        int m = pattern.length();
        
        //return empty for both empty text and pattern > text
        if (m > n)
            return "";
        
        List<Integer> indices = new ArrayList<>();

        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }
        

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

        
        int n = text.length();
        int m = pattern.length();
        
        
        List<Integer> indices = new ArrayList<>();
        
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }
        
        // Pattern can't be longer than text
        if (m > n)
            return "";

        // Build both heuristic tables
        HashMap<Character, Integer> badCharTable = createBadCharTable(pattern);
        int[] goodSuffixTable = createGoodSuffixTable(pattern);

        for (int i = m - 1; i < text.length();) {

            int iTemp = i;
            int badShift = 0;
            int goodShift = 0;
            // Compare pattern from right to left
            for (int j = m - 1; j >= 0; j--) {
                if (text.charAt(iTemp) != pattern.charAt(j)) {
                    // Mismatch calculate shifts from both tables
                    goodShift = goodSuffixTable[j + 1];

                    if (badCharTable.containsKey(text.charAt(iTemp))) {
                        badShift = Math.max(1, j - badCharTable.get(text.charAt(iTemp)));
                    } else {
                        badShift = j + 1;
                    }
                    // Shift by the maximum of both heuristics
                    i = i + Math.max(badShift, goodShift);
                    break;

                } else { // Character matched
                     
                    if (j == 0) { // pattern match found at iTemp
                        indices.add(iTemp);
                        i += goodSuffixTable[j];
                        break;
                    }
                    iTemp--;
                }
            }

        }
        return indicesToString(indices);

    }

    private int[] createGoodSuffixTable(String pattern) {
        int m = pattern.length();

        int[] borderPositions = new int[m + 1];
        int[] shiftTable = new int[m + 1];

        // Case 1: suffix appears at unmatched part of pattern with different preceding char
        fillTableCaseExactMatch(pattern, shiftTable, borderPositions);
        // Case 2: matched part of pattern's suffix matches patterns prefixes
        fillTableCasePrefixSuffix(shiftTable, borderPositions);

        return shiftTable;
    }

    // Fill shifts for case 1
    private void fillTableCaseExactMatch(String pattern, int[] shiftTable, int[] borderPositions) {
        
        int i = pattern.length();
        int j = i + 1;
        int n = i;
        
        borderPositions[i] = j;
        while (i > 0) {
            // Find border positions
            while (j <= n && (pattern.charAt(i - 1) != pattern.charAt(j - 1))) {
                
                if (shiftTable[j] == 0)
                    shiftTable[j] = j - i;
                
                j = borderPositions[j];

            }
            i--;
            j--;
            borderPositions[i] = j;
        }
        
    }
    
    // Fill shifts for case 2
    private void fillTableCasePrefixSuffix(int[] shiftTable, int[] borderPositions) {
        
        int j = borderPositions[0];

        for (int i = 0; i < shiftTable.length; i++) {
            if (shiftTable[i] == 0)
                shiftTable[i] = j;

            if (i == j)
                j = borderPositions[i];
        }
        
    }

    // Bad character table: rightmost occurrence of each character
    private HashMap<Character, Integer> createBadCharTable(String pattern) {
        
        HashMap<Character, Integer> map = new HashMap<>();
        
        for (int i = pattern.length() - 1; i >= 0; i--) {
            
            if (!map.containsKey(pattern.charAt(i))) {
                
                map.put(pattern.charAt(i), i);
            }
        }
        return map;
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
        // TODO: Students should implement their own creative algorithm here
        throw new UnsupportedOperationException("GoCrazy algorithm not yet implemented - this is your homework!");
    }
}
