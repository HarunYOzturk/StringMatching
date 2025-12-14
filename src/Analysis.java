import java.util.ArrayList;
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

class BoyerMoore extends Solution {

    static {
        SUBCLASSES.add(BoyerMoore.class);
        System.out.println("BoyerMoore registered");
    }

    @Override
    public String Solve(String text, String pattern) {

        int n = text.length();
        int m = pattern.length();

        if (m == 0)
            return generateResultAllPositions(n); // if pattern is empty then i returned all positions.
        if (m > n) // there is no match if pattern length is greater than text
            return "";

        List<Integer> result = new ArrayList<>();

        Map<Character, Integer> badCharTable = badCharacterTable(pattern); // for bad character table

        int[] goodSuffixTable = goodSuffixTable(pattern); // for good suffix table

        int shift = 0; // shift value for shifting pattern on the text

        while (shift <= (n - m)) {
            int j = m - 1;

            while (j >= 0 && pattern.charAt(j) == text.charAt(shift + j)) {
                j--; // backtracking control from right to left pattern and text matching
            }

            if (j < 0) {
                result.add(shift);
                shift += goodSuffixTable[0];
            } else {

                int move = j - badCharTable.getOrDefault(text.charAt(shift + j), -1);
                shift += Math.max(1, Math.max(move, goodSuffixTable[j]));
            }
        }

        return indicesToString(result);
    }

    private Map<Character, Integer> badCharacterTable(String pattern) {
        Map<Character, Integer> table = new HashMap<>();
        for (int i = 0; i < pattern.length(); i++) {
            table.put(pattern.charAt(i), i); // we should store the last position of each character in the pattern
        }
        return table;
    }

    private int[] goodSuffixTable(String pattern) {
        int m = pattern.length();

        int[] goodSuffixTable = new int[m]; // it stores the shifting value for every index

        int[] border = new int[m + 1]; // border for suffix and prefix relations

        int i = m;
        int j = m + 1;
        border[i] = j;

        while (i > 0) { // I create the border table

            while (j <= m && pattern.charAt(i - 1) != pattern.charAt(j - 1)) {
                if (goodSuffixTable[j - 1] == 0) {
                    goodSuffixTable[j - 1] = j - i;
                }
                j = border[j];
            }
            i--;
            j--;
            border[i] = j;
        }

        j = border[0];
        for (i = 0; i < m; i++) {
            if (goodSuffixTable[i] == 0) {
                goodSuffixTable[i] = j;
            }
            if (i == j) {
                j = border[j];
            }
        }

        return goodSuffixTable;
    }

    private String generateResultAllPositions(int n) { // if the pattern is empty then we should return all the position
                                                       // indexes
                                                       // This method work for this purpose.
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i <= n; i++)
            list.add(i);
        return indicesToString(list);
    }
}

class GoCrazyFusion extends Solution {

    static {
        SUBCLASSES.add(GoCrazyFusion.class);
        System.out.println("GoCrazyFusion registered.");
    }

    public GoCrazyFusion() {
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();

        if (pattern == null || text == null)
            return "";
        int n = text.length();
        int m = pattern.length();

        // these are for edge cases empty pattern or pattern longer than text
        if (m == 0) {
            for (int i = 0; i <= n; i++)
                indices.add(i);
            return indicesToString(indices);
        }
        if (m > n)
            return "";

        // I choose the rarest character in the pattern as an anchor
        int anchorIndex = findRarestAnchor(pattern);
        char anchor = pattern.charAt(anchorIndex);

        int i = 0;

        while (i <= n - m) {

            // I check the anchor character in the text
            if (text.charAt(i + anchorIndex) != anchor) {
                // If it does not match, I skip ahead using my adaptive skip logic
                i += adaptiveSkip(text.charAt(i + anchorIndex), pattern, anchorIndex);
                continue;
            }

            // Quick filter using first and last character match
            if (!quickFilter(text, pattern, i)) {
                i++;
                // If it fails, I move to the next position
                continue;
            }

            // Full validation
            if (fullMatch(text, pattern, i)) {
                indices.add(i); // If full match succeeds , I record the position
                // I skip ahead if the pattern repeats
                i += Math.max(1, m - anchorIndex);
                continue;
            }

            // Smart shift when no match is found
            i += smartShift(text, pattern, i, anchorIndex);
        }

        return indicesToString(indices);
    }

    // I find the rarest character in the pattern to maximize early mismatches
    private int findRarestAnchor(String pattern) {
        int[] freq = new int[256];
        for (char c : pattern.toCharArray())
            freq[c]++;

        int minIndex = 0;
        int minFreq = Integer.MAX_VALUE;

        for (int i = 0; i < pattern.length(); i++) {
            if (freq[pattern.charAt(i)] < minFreq) {
                minFreq = freq[pattern.charAt(i)];
                minIndex = i;
            }
        }
        return minIndex;
    }

    // I quickly check prefix and suffix for a fast rejection of impossible matches
    private boolean quickFilter(String text, String pattern, int pos) {
        return text.charAt(pos) == pattern.charAt(0)
                && text.charAt(pos + pattern.length() - 1) == pattern.charAt(pattern.length() - 1);
    }

    // I validate the full pattern match
    private boolean fullMatch(String text, String pattern, int pos) {
        for (int j = 0; j < pattern.length(); j++) {
            if (text.charAt(pos + j) != pattern.charAt(j))
                return false;
        }
        return true;
    }

    private int adaptiveSkip(char c, String pattern, int anchorIndex) {
        /*
         * I adaptively skip ahead based on the
         * character mismatch and anchor position
         */

        for (int k = 0; k < pattern.length(); k++) {
            if (pattern.charAt(k) == c)
                return Math.max(1, Math.abs(anchorIndex - k));
        }
        return pattern.length();
    }

    private int smartShift(String text, String pattern, int pos, int anchorIndex) {
        // I compute a smart shift to avoid unnecessary comparisons while checking
        // repeated patterns.
        if (pos + anchorIndex + 1 < text.length()
                && text.charAt(pos + anchorIndex + 1) == pattern.charAt(anchorIndex)) {
            return 1; // this is for the minimal shift if next char matches anchor
        }
        return adaptiveSkip(text.charAt(pos + anchorIndex), pattern, anchorIndex);
    }
}
