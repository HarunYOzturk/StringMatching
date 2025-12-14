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
    private char[] pattern;
    private int m;
    private int[] badChar;
    private int[] goodSuffix;
    private final int ALPHABET_SIZE = 65536; //The alphabet contains all characters, including unicode characters.

    static {
        SUBCLASSES.add(BoyerMoore.class);
        System.out.println("BoyerMoore registered");
    }

    public BoyerMoore() {
        this.badChar = new int[ALPHABET_SIZE]; //Initializing badChar array here to keep it from initializing it every iteration
    }

    @Override
    public String Solve(String text, String pattern) {
        // TODO: Students should implement Boyer-Moore algorithm here
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        this.m = pattern.length();
        this.pattern = pattern.toCharArray();

        if(m == 0){ //Edge case: if pattern is empty it matches at every position
            for(int i = 0; i <= n; i++){
                indices.add(i);
            }return indicesToString(indices);
        }
        if(m > n){ //Edge case: if pattern is longer than text there's no match
            return "";
        }
        this.goodSuffix = new int[m+1];
        //Since all characters in the alphabet other than the ones appearing inside the pattern take the value m 
        java.util.Arrays.fill(badChar, m); //More efficient than a for loop
       
        preprocessingBadCharRule();
        preprocessingGoodSuffixRule();
        
        int i = 0;
        while(i <= n-m){
            int j = m-1;
            while(j >= 0 && this.pattern[j] == text.charAt(i+j)){
                j--;
            }
            if(j < 0){ // An exact match was found
                indices.add(i);
                //Good Suffix rule determines the shifting amount after the match
                //Since pattern is a "good suffix" as a whole, shifting amount is goodSuffix[0]
                i += goodSuffix[0];
            }else{ //Mismatch happens at index j
                char badCharacter = text.charAt(i+j);

                /*1. Shifting according to Bad Character Rule
                badChar[badCharacter]: Rightmost index of the mismatched character in the pattern*/
                int badCharShiftVal = badChar[badCharacter] - (m-1-j);
                //If bad character isn't in the pattern or it's to the right of the mismatched character, badCharShiftVal might take a negative value.
                //In that case we should perform at least one shift.
                if(badCharShiftVal < 1){ 
                    badCharShiftVal = 1;
                }
                /*2. Shifting According to Good Suffix Rule
                j+1 is the beginning of the good suffix to the right of mismatch, which has a length of m-1-j.*/
                int goodSuffixShiftVal = goodSuffix[j+1];
                //Shift by the amount that's larger.
                i+= Math.max(badCharShiftVal, goodSuffixShiftVal);
            }
        }
        return indicesToString(indices);
        
    }
    private void preprocessingBadCharRule(){
        for(int i = 0; i<m; i++){
            badChar[this.pattern[i]] = m-i-1; //All characters in the pattern take the value m-i-1 with i being the rightmost index they were spotted        
            }
    }
    private void preprocessingGoodSuffixRule(){
        int[] border = new int[m+1];
        int i = m;
        int j = m + 1;
        border[i] = j;
        while(i>0){
            while(j <= m && pattern[i-1] != pattern[j-1]){
                if(goodSuffix[j] == 0){
                    goodSuffix[j] = j-i;
                }
                j = border[j];
            }
            i--; j--;
            border[i] = j;
        }

        j = border[0];
        for(i = 0; i <= m; i++){
            if(goodSuffix[i] == 0){
                goodSuffix[i] = j;
            } if(i==j){
                j = border[j];
            }
        }
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


