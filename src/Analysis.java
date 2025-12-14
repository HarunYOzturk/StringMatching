import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * String Matching Algorithms (Analysis.java)
 *
 * Implemented Solution subclasses:
 *  - Naive
 *  - KMP
 *  - RabinKarp
 *  - BoyerMoore (bad-character heuristic)
 *  - GoCrazy (hybrid strategy)
 *
 * Consistency rules:
 *  - All algorithms return via Solution.indicesToString(List<Integer>)
 *  - Empty pattern matches at every position 0..n (inclusive)
 */

/* =========================
 * NAIVE
 * ========================= */
class Naive extends Solution {
    static {
        SUBCLASSES.add(Naive.class);
        System.out.println("Naive registered");
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        if (m == 0) {
            for (int i = 0; i <= n; i++) indices.add(i);
            return indicesToString(indices);
        }

        if (m > n) return indicesToString(indices);

        for (int i = 0; i <= n - m; i++) {
            int j = 0;
            while (j < m && text.charAt(i + j) == pattern.charAt(j)) j++;
            if (j == m) indices.add(i);
        }

        return indicesToString(indices);
    }
}

/* =========================
 * KMP
 * ========================= */
class KMP extends Solution {
    static {
        SUBCLASSES.add(KMP.class);
        System.out.println("KMP registered");
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        if (m == 0) {
            for (int i = 0; i <= n; i++) indices.add(i);
            return indicesToString(indices);
        }

        if (m > n) return indicesToString(indices);

        int[] lps = computeLPS(pattern);
        int i = 0, j = 0;

        while (i < n) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++; j++;
            }

            if (j == m) {
                indices.add(i - j);
                j = lps[j - 1];
            } else if (i < n && text.charAt(i) != pattern.charAt(j)) {
                if (j != 0) j = lps[j - 1];
                else i++;
            }
        }

        return indicesToString(indices);
    }

    private int[] computeLPS(String p) {
        int m = p.length();
        int[] lps = new int[m];
        int len = 0, i = 1;

        while (i < m) {
            if (p.charAt(i) == p.charAt(len)) {
                lps[i++] = ++len;
            } else {
                if (len != 0) len = lps[len - 1];
                else lps[i++] = 0;
            }
        }
        return lps;
    }
}

/* =========================
 * RABIN–KARP
 * ========================= */
class RabinKarp extends Solution {
    static {
        SUBCLASSES.add(RabinKarp.class);
        System.out.println("RabinKarp registered");
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        if (m == 0) {
            for (int i = 0; i <= n; i++) indices.add(i);
            return indicesToString(indices);
        }

        if (m > n) return indicesToString(indices);

        final int base = 256;
        final int mod = 101;

        int pHash = 0, tHash = 0, h = 1;

        for (int i = 0; i < m - 1; i++) h = (h * base) % mod;

        for (int i = 0; i < m; i++) {
            pHash = (base * pHash + pattern.charAt(i)) % mod;
            tHash = (base * tHash + text.charAt(i)) % mod;
        }

        for (int s = 0; s <= n - m; s++) {
            if (pHash == tHash) {
                boolean ok = true;
                for (int j = 0; j < m; j++) {
                    if (text.charAt(s + j) != pattern.charAt(j)) {
                        ok = false;
                        break;
                    }
                }
                if (ok) indices.add(s);
            }

            if (s < n - m) {
                tHash = (tHash - text.charAt(s) * h) % mod;
                if (tHash < 0) tHash += mod;
                tHash = (tHash * base + text.charAt(s + m)) % mod;
            }
        }

        return indicesToString(indices);
    }
}

/* =========================
 * BOYER–MOORE (Unicode-safe)
 * ========================= */
class BoyerMoore extends Solution {
    static {
        SUBCLASSES.add(BoyerMoore.class);
        System.out.println("BoyerMoore registered");
    }

    @Override
    public String Solve(String text, String pattern) {
        List<Integer> indices = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        if (m == 0) {
            for (int i = 0; i <= n; i++) indices.add(i);
            return indicesToString(indices);
        }

        if (m > n) return indicesToString(indices);

        int[] last = buildLastTable(pattern);
        int s = 0;

        while (s <= n - m) {
            int j = m - 1;

            while (j >= 0 && pattern.charAt(j) == text.charAt(s + j)) j--;

            if (j < 0) {
                indices.add(s);
                if (s + m < n) {
                    char next = text.charAt(s + m);
                    int li = lastIndex(last, next);
                    s += Math.max(1, m - li);
                } else s++;
            } else {
                char bad = text.charAt(s + j);
                int li = lastIndex(last, bad);
                s += Math.max(1, j - li);
            }
        }

        return indicesToString(indices);
    }

    private int[] buildLastTable(String pattern) {
        int[] last = new int[Character.MAX_VALUE + 1];
        Arrays.fill(last, -1);
        for (int i = 0; i < pattern.length(); i++) {
            last[pattern.charAt(i)] = i;
        }
        return last;
    }

    private int lastIndex(int[] last, char c) {
        return last[c];
    }
}

/* =========================
 * GOCRAZY (Hybrid Strategy)
 * ========================= */
class GoCrazy extends Solution {
    static {
        SUBCLASSES.add(GoCrazy.class);
        System.out.println("GoCrazy registered");
    }

    @Override
    public String Solve(String text, String pattern) {

        int n = text.length();
        int m = pattern.length();

        if (m == 0) {
            List<Integer> res = new ArrayList<>();
            for (int i = 0; i <= n; i++) res.add(i);
            return indicesToString(res);
        }

        if (m <= 3 || n <= 32)
            return new Naive().Solve(text, pattern);

        if (isPeriodic(pattern))
            return new KMP().Solve(text, pattern);

        if (n > 2000 && alphabetSize(text) > 20)
            return new BoyerMoore().Solve(text, pattern);

        return new RabinKarp().Solve(text, pattern);
    }

    private boolean isPeriodic(String p) {
        int[] lps = computeLPS(p);
        int m = p.length();
        return lps[m - 1] >= m / 2;
    }

    private int[] computeLPS(String p) {
        int[] lps = new int[p.length()];
        int len = 0, i = 1;
        while (i < p.length()) {
            if (p.charAt(i) == p.charAt(len)) lps[i++] = ++len;
            else if (len != 0) len = lps[len - 1];
            else lps[i++] = 0;
        }
        return lps;
    }

    private int alphabetSize(String s) {
        boolean[] seen = new boolean[Character.MAX_VALUE + 1];
        int cnt = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!seen[c]) {
                seen[c] = true;
                cnt++;
            }
        }
        return cnt;
    }
}