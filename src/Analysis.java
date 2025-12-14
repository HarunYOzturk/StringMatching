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

    static void preprocess_strong_suffix(int []shift, int []bpos,
                                      char []pat, int m)
{
    // m is the length of pattern 
    int i = m, j = m + 1;
    bpos[i] = j;

    while(i > 0)
    {
        while(j <= m && pat[i - 1] != pat[j - 1])
        {
            if (shift[j] == 0)
                shift[j] = j - i;

            j = bpos[j];
        }
        i--; j--;
        bpos[i] = j; 
    }
}

static void preprocess_case2(int []shift, int []bpos,
                              char []pat, int m)
{
    int i, j;
    j = bpos[0];
    for(i = 0; i <= m; i++)
    {
        if(shift[i] == 0)
            shift[i] = j;

        if (i == j)
            j = bpos[j];
    }
}

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();
        char []pat = pattern.toCharArray();
        int d = 256; 
        int badChar[] = new int[d];
        int []bpos = new int[m + 1];
        int []shift = new int[m + 1];


        for (int i = 0; i < d; i++)
            badChar[i] = -1;

        for (int i = 0; i < m; i++)
            badChar[(int)pattern.charAt(i)] = i;

        for(int i = 0; i < m + 1; i++) 
            shift[i] = 0;

        int s = 0;

        preprocess_strong_suffix(shift, bpos, pat, m);
        preprocess_case2(shift, bpos, pat, m);

        while (s <= (n - m)) {
            int j = m - 1;

            while (j >= 0 && pattern.charAt(j) == text.charAt(s + j))
                j--;

            if (j < 0) {
                indices.add(s);

                s += shift[0];
            }

            else{
                int bc = j - badChar[text.charAt(s + j)];
                int gs = shift[j + 1];
                s += Math.max(1, Math.max(bc, gs));
            }
                 
        }
        return indicesToString(indices);
        // TODO: Students should implement Boyer-Moore algorithm here
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


