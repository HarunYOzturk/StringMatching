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


class BoyerMoore extends Solution {
  static {
    SUBCLASSES.add(BoyerMoore.class);
    System.out.println("BoyerMoore registered");
  }

  // Stamping trick to avoid O(65536) initialization each call
  private static final int ALPHABET = 65536;
  private static final int[] lastPos = new int[ALPHABET];
  private static final int[] stamp = new int[ALPHABET];
  private static int currentStamp = 1;

  public BoyerMoore() {
  }

  @Override
  public String Solve(String text, String pattern) {
    List<Integer> indices = new ArrayList<>();
    int n = text.length();
    int m = pattern.length();

    // Empty pattern matches at every position (consistent with your other
    // algorithms)
    if (m == 0) {
      for (int i = 0; i <= n; i++)
        indices.add(i);
      return indicesToString(indices);
    }
    if (m > n)
      return "";

    // Preprocess: record last occurrence in pattern for chars that appear
    int myStamp = nextStamp();
    for (int i = 0; i < m; i++) {
      char c = pattern.charAt(i);
      stamp[c] = myStamp;
      lastPos[c] = i;
    }

    int shift = 0;
    while (shift <= n - m) {
      int j = m - 1;

      while (j >= 0 && pattern.charAt(j) == text.charAt(shift + j)) {
        j--;
      }

      if (j < 0) {
        indices.add(shift);

        // Standard BM bad-character shift after a match
        if (shift + m < n) {
          char next = text.charAt(shift + m);
          int last = (stamp[next] == myStamp) ? lastPos[next] : -1;
          shift += m - last;
        } else {
          shift += 1;
        }
      } else {
        char bad = text.charAt(shift + j);
        int last = (stamp[bad] == myStamp) ? lastPos[bad] : -1;
        shift += Math.max(1, j - last);
      }
    }

    return indicesToString(indices);
  }

  private static int nextStamp() {
    // Avoid stamp overflow issues (extremely unlikely here, but safe)
    if (currentStamp == Integer.MAX_VALUE) {
      // reset stamps to 0
      for (int i = 0; i < ALPHABET; i++)
        stamp[i] = 0;
      currentStamp = 1;
    }
    return currentStamp++;
  }
}

class GoCrazy extends Solution {
  static {
    SUBCLASSES.add(GoCrazy.class);
    System.out.println("GoCrazy registered");
  }

  public GoCrazy() {
  }

  @Override
  public String Solve(String text, String pattern) {
    List<Integer> indices = new ArrayList<>();
    int n = text.length();
    int m = pattern.length();

    if (m == 0) {
      for (int i = 0; i <= n; i++)
        indices.add(i);
      return indicesToString(indices);
    }
    if (m > n)
      return "";

    // -------------------------------------------------------
    // "Sparse Bad Character" Heuristic
    // -------------------------------------------------------
    // We only track the LAST position of the LAST 4 unique characters.
    // This gives us skips for the most likely mismatch scenarios
    // without the overhead of a full array or HashMap.

    char c1 = 0, c2 = 0, c3 = 0, c4 = 0;
    int p1 = -1, p2 = -1, p3 = -1, p4 = -1;
    int trackedCount = 0;

    // Scan BACKWARDS to find the rightmost unique characters
    for (int i = m - 1; i >= 0; i--) {
      char c = pattern.charAt(i);
      // Check if we already tracked this char
      if (trackedCount > 0 && c == c1)
        continue;
      if (trackedCount > 1 && c == c2)
        continue;
      if (trackedCount > 2 && c == c3)
        continue;
      if (trackedCount > 3 && c == c4)
        continue;

      // Add new unique char
      if (trackedCount == 0) {
        c1 = c;
        p1 = i;
      } else if (trackedCount == 1) {
        c2 = c;
        p2 = i;
      } else if (trackedCount == 2) {
        c3 = c;
        p3 = i;
      } else if (trackedCount == 3) {
        c4 = c;
        p4 = i;
      }

      trackedCount++;
      if (trackedCount >= 4)
        break;
    }

    int shift = 0;
    while (shift <= n - m) {
      int j = m - 1;

      // Standard right-to-left scan
      while (j >= 0 && pattern.charAt(j) == text.charAt(shift + j)) {
        j--;
      }

      if (j < 0) {
        indices.add(shift);
        shift++; // Safe shift after match
      } else {
        char badChar = text.charAt(shift + j);
        int lastPos = -1;

        // Check our sparse table
        if (trackedCount > 0 && badChar == c1)
          lastPos = p1;
        else if (trackedCount > 1 && badChar == c2)
          lastPos = p2;
        else if (trackedCount > 2 && badChar == c3)
          lastPos = p3;
        else if (trackedCount > 3 && badChar == c4)
          lastPos = p4;

        if (lastPos != -1) {
          // Known character: use Boyer-Moore skip
          shift += Math.max(1, j - lastPos);
        } else {
          // Unknown character: MUST shift by 1 to be safe
          // (It might be in the pattern, just not tracked)
          shift++;
        }
      }
    }

    return indicesToString(indices);
  }
}
