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
        int m = pattern.length();
        int n = text.length();
        if (m == 0 || n == 0 || m > n){ // This is an edge case, if pattern or the text (or both) is/are empty
            return "";
        }
        int R = 65536; // unicode character set. 
        int[] badCharacter = new int[R];
        for (int i = 0; i < R; i++) {
            badCharacter[i] = m;
        }
        // (m - 1 - i)
        for (int i = 0; i < m - 1; i++){
            badCharacter[pattern.charAt(i)] = m - 1 - i;
        }
        
        List<Integer> match = new ArrayList<>();
        
        int i = 0;
        
        while (i <= (n - m)){
            int j = m - 1;
            
            // Go backward until we find a match
            while (j >= 0 && pattern.charAt(j) == text.charAt(i + j)){
                j--;
            }
            
            if (j < 0) { // Match found.
                match.add(i);
                
                if (i + m < n) {
                    i += badCharacter[text.charAt(i + m)];
                }
                else{
                    i++;
                }
            }
            else{ // Match NOT found.
                int badChar = text.charAt(i + j);
                int badCharacterShiftAmount = badCharacter[badChar];
                
                int shift = Math.max(1, badCharacterShiftAmount - (m - 1 - j));
                i += shift;
            }
            
            
        }
        
        // Return matches in wanted format
        return match.stream()
                  .map(String::valueOf)
                  .collect(java.util.stream.Collectors.joining(","));
        
        // throw new UnsupportedOperationException("Boyer-Moore algorithm not yet implemented - this is your homework!");
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
        int n = text.length();
        int m = pattern.length();
        
        // Edge cases where pattern or text or both are empty.
        if (m == 0) return (n == 0) ? "" : ""; 
        if (n == 0 || m > n) return "";
        
        int R = 65536; // unicode character set.
        
        int[] rightMostIndex = new int[R];
        
        for (int i = 0; i < R; i++) {
            rightMostIndex[i] = -1;
        }
        
        for (int i = 0; i < m; i++) {
            if (pattern.charAt(i) < R) {
                rightMostIndex[pattern.charAt(i)] = i;
            }
        }
        
        List<Integer> matches = new ArrayList<>();
        int i = 0;
        
        int center = m / 2;
        
        while (i <= n - m) {            
            boolean match = true;
            
            if (text.charAt(i + center) != pattern.charAt(center)) {
                match = false;
            }
            else{ // If the character at the middle matched
                int left = center - 1;
                int right = center + 1;
                while (left >= 0 || right < m) {                    
                    if (left >= 0){
                        if (text.charAt(i + left) != pattern.charAt(left)) {
                            match = false;
                            break;
                        }
                        left--;
                    }
                    
                    if (right < m) {
                        if (text.charAt(i + right) != pattern.charAt(right)) {
                            match  = false;
                            break;
                        }
                        right++;
                    }
                }
            }
            if (match) {
                matches.add(i);
            }
            
            if (i + m < n) {
                char nextChar = text.charAt(i + m);
                
                int shift;
                
                if (nextChar < R) {
                    int posInPattern = rightMostIndex[nextChar];
                    // pattern lenght - char position in the pattern
                    shift = m - posInPattern;
                }else{
                    shift = 1; // fallback
                }
                i += shift;
            }else{
                break; // end of text
            }
            
        }
        return matches.stream()
                .map(String::valueOf)
                .collect(java.util.stream.Collectors.joining(","));
        // throw new UnsupportedOperationException("GoCrazy algorithm not yet implemented - this is your homework!");
    }
}


