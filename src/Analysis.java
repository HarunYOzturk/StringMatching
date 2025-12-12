import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
        if (m == 0) {
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i <= n; i++) indices.add(i);
            return indicesToString(indices);
        }
        if (n == 0 || m > n) {
            return "";
        }

        Map<Character, Integer> badCharTable = new HashMap<>();

        for (int i = 0; i < m; i++) {
            badCharTable.put(pattern.charAt(i), i);
        }
        int[] goodSuffixTable = preprocessGoodSuffix(pattern);

        List<Integer> match = new ArrayList<>();
        int s = 0;

        while (s <= (n - m)) {
            int j = m - 1;
            while (j >= 0 && pattern.charAt(j) == text.charAt(s + j)) {
                j--;
            }

            if (j < 0) {
                match.add(s);
                s += goodSuffixTable[0];
            } else {
                char badChar = text.charAt(s + j);
                int lastSeenPos = badCharTable.getOrDefault(badChar, -1);
                int bcShift = j - lastSeenPos;
                int gsShift = goodSuffixTable[j + 1];
                s += Math.max(1, Math.max(bcShift, gsShift));
            }
        }

        return indicesToString(match);
    }


    private int[] preprocessGoodSuffix(String pattern) {
        int m = pattern.length();
        int[] table = new int[m + 1];
        int[] borderPos = new int[m + 1];
        int i = m;
        int j = m + 1;
        borderPos[i] = j;
        while (i > 0) {
            while (j <= m && pattern.charAt(i - 1) != pattern.charAt(j - 1)) {
                if (table[j] == 0) table[j] = j - i;
                j = borderPos[j];
            }
            i--;
            j--;
            borderPos[i] = j;
        }

        j = borderPos[0];
        for (i = 0; i <= m; i++) {
            if (table[i] == 0) table[i] = j;
            if (i == j) j = borderPos[j];
        }

        return table;
    }


    public String indicesToString(List<Integer> indices) {
        if (indices.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indices.size(); i++) {
            sb.append(indices.get(i));
            if (i < indices.size() - 1) sb.append(",");
        }
        return sb.toString();
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

        // Handle empty pattern
        if (m == 0) {
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i <= n; i++) {
                indices.add(i);
            }
            return indicesToString(indices);
        }

        // Handle empty text or pattern longer than text
        if (n == 0 || m > n) {
            return "";
        }

        Map<Character, Integer> rightMostIndex = new HashMap<>();

        for (int i = 0; i < m; i++) {
            rightMostIndex.put(pattern.charAt(i), i);
        }

        List<Integer> matches = new ArrayList<>();
        int i = 0;
        int center = m / 2;

        while (i <= n - m) {
            boolean match = true;

            if (text.charAt(i + center) != pattern.charAt(center)) {
                match = false;
            } else if (text.charAt(i + m - 1) != pattern.charAt(m - 1)) {
                match = false;
            } else if (text.charAt(i) != pattern.charAt(0)) {
                match = false;
            } else {
                for (int j = 1; j < m - 1; j++) {
                    if (j == center) continue;
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }
            }

            if (match) {
                matches.add(i);
            }


            if (i + m < n) {
                char nextChar = text.charAt(i + m);

                int posInPattern = rightMostIndex.getOrDefault(nextChar, -1);
                int shift = m - posInPattern;

                i += shift;
            } else {
                break; // End of text
            }
        }

        return indicesToString(matches);
    }
}
