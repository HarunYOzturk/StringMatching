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
 * I used geeksforgeeks.org to understand the logic of the Boyer Moore algorithm and
 * to implement it. I understand the logic before I take a look at the code. However,
 * when I tried to code the logic, I realised I learnt nothing. So, you can consider my code
 * is partially copied from there since I nearly did nothing different.
 * 
 * Except one thing. We always assumed all string matchings are done with ASCII characters.
 * However there were one test case that a character that is not on the ASCII table. So, I added
 * a loop to find the maximum character value in text and pattern to create the proper table.
 * 
 * Research Website: https://www.geeksforgeeks.org/dsa/boyer-moore-algorithm-for-pattern-searching/
 */
class BoyerMoore extends Solution {
    static {
        SUBCLASSES.add(BoyerMoore.class);
        System.out.println("BoyerMoore registered");
    }

    public BoyerMoore() {
    }

    // The preprocessing function for Boyer Moore's
    // bad character heuristic
    private void badCharHeuristic(String str, int size, int badchar[], int tableSize) {
        // Initialize all occurrences as -1
        for (int i = 0; i < tableSize; i++)
            badchar[i] = -1;

        /**
         * Fill the actual value of last occurrence of a character (indices of table
         * are char values and values are index of occurrence)
         */
        for (int i = 0; i < size; i++)
            badchar[(int) str.charAt(i)] = i;
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int m = pattern.length();
        int n = text.length();

        // Handle empty pattern
        if (m == 0) {
            for (int i = 0; i <= n; i++)
                indices.add(i);
            
            return indicesToString(indices);
        }

        // Handle pattern longer than text
        if (m > n)
            return "";

        // Find the maximum character value in text and pattern to create the proper table
        int maxChar = 0;
        for (int i = 0; i < n; i++) {
            int charVal = (int) text.charAt(i);
            if (charVal > maxChar)
                maxChar = charVal;
        }
        for (int i = 0; i < m; i++) {
            int charVal = (int) pattern.charAt(i);
            if (charVal > maxChar)
                maxChar = charVal;
        }
        
        // Table size is maxChar + 1 to include the max character itself
        int tableSize = maxChar + 1;
        int badchar[] = new int[tableSize];

        // Fill the bad character array
        badCharHeuristic(pattern, m, badchar, tableSize);

        // shift index
        int s = 0; 

        // n-m+1 potential alignments
        while (s <= (n - m)) {
            int j = m - 1;

            /**
             * Reduce index j of pattern while characters of pattern and text are
             * matching at this shift s
             */
            while (j >= 0 && pattern.charAt(j) == text.charAt(s + j))
                j--;

            /**
             * If the pattern is present at current shift, then index j will 
             * become -1 afterthe above loop
             */
            if (j < 0) {
                indices.add(s);

                /**
                 * Shift the pattern so that the next character in text aligns with the
                 *  last occurrence of it in pattern. The condition s+m < n is necessary
                 * for the case when pattern occurs at the end of text
                 */
                s += (s + m < n) ? m - badchar[text.charAt(s + m)]
                                 : 1;
            } else
                   /**
                    * Shift the pattern so that the bad character in text aligns with the last
                    * occurrence of it in pattern.
                    */
                s += Math.max(1, j - badchar[text.charAt(s + j)]);
        }

        return indicesToString(indices);
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


