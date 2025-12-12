/**
 * PreAnalysis interface for students to implement their algorithm selection logic
 * 
 * Students should analyze the characteristics of the text and pattern to determine
 * which algorithm would be most efficient for the given input.
 * 
 * The system will automatically use this analysis if the chooseAlgorithm method
 * returns a non-null value.
 */
public abstract class PreAnalysis {
    
    /**
     * Analyze the text and pattern to choose the best algorithm
     * 
     * @param text The text to search in
     * @param pattern The pattern to search for
     * @return The name of the algorithm to use (e.g., "Naive", "KMP", "RabinKarp", "BoyerMoore", "GoCrazy")
     *         Return null if you want to skip pre-analysis and run all algorithms
     * 
     * Tips for students:
     * - Consider the length of the text and pattern
     * - Consider the characteristics of the pattern (repeating characters, etc.)
     * - Consider the alphabet size
     * - Think about which algorithm performs best in different scenarios
     */
    public abstract String chooseAlgorithm(String text, String pattern);
    
    /**
     * Get a description of your analysis strategy
     * This will be displayed in the output
     */
    public abstract String getStrategyDescription();
}


/**
 * Default implementation that students should modify
 * This is where students write their pre-analysis logic
 */
class StudentPreAnalysis extends PreAnalysis {

    @Override
    public String chooseAlgorithm(String text, String pattern) {
        // Basic metrics
        int textLen = text.length();
        int patternLen = pattern.length();

        // ============ PRELIMINARY CHECKS - CLEAR CASES ============

        // Single character pattern - simplest case
        if (patternLen == 1) return "Naive";

        // Pattern longer than text - impossible case
        if (patternLen > textLen) return "Naive";

        // Very small datasets - preprocessing cost unnecessary
        if (textLen < 50 && patternLen <= 3) return "Naive";

        // Very long pattern (>40) - Boyer-Moore can reach sub-linear speed
        if (patternLen > 40) return "BoyerMoore";

        // ============ DETAILED ANALYSES ============

        // Character diversity and distribution analyses
        int uniqueCharsPattern = countUniqueChars(pattern);
        int uniqueCharsText = countUniqueChars(text.substring(0, Math.min(1000, textLen)));
        double uniqueRatioPattern = (double) uniqueCharsPattern / patternLen;
        int alphabetSize = Math.max(uniqueCharsPattern, uniqueCharsText);

        // Entropy analyses (information theory)
        double patternEntropy = calculateEntropy(pattern);
        double textEntropy = calculateEntropy(text.substring(0, Math.min(1000, textLen)));

        // Structural analyses
        boolean hasRepeating = hasRepeatingPattern(pattern);
        int maxConsecutive = maxConsecutiveRepeats(pattern);
        int periodLength = findPeriod(pattern);

        // Special case analyses
        boolean allSameChar = (uniqueCharsPattern == 1);
        boolean isDNALike = isdnaLike(pattern, text);
        boolean hasHighRepetition = (maxConsecutive >= 3 || hasRepeating);
        boolean isNaturalLanguage = isNaturalLanguageLike(pattern, alphabetSize);

        // First character rarity analysis (important for Naive)
        double firstCharRarity = calculateCharRarity(pattern.charAt(0), text);

        // Mismatch position estimation
        double mismatchPosition = estimateMismatchPosition(pattern, text);

        // Hash collision risk estimation for RabinKarp
        double collisionRisk = estimateHashCollisionRisk(pattern, text, patternEntropy);

        // ============ SCORING SYSTEM ============
        int naiveScore = 0;
        int kmpScore = 0;
        int rabinKarpScore = 0;
        int boyerMooreScore = 0;

        // ---------- NAIVE SCORING ----------
        // Small datasets
        if (patternLen <= 2) naiveScore += 100;
        if (patternLen == 3 && textLen < 100) naiveScore += 80;
        if (textLen < 100) naiveScore += 40;

        // First character is rare (early mismatch)
        if (firstCharRarity > 0.9 && patternLen <= 5) naiveScore += 70;
        if (firstCharRarity > 0.95) naiveScore += 50;

        // Short pattern + short text combination
        if (patternLen <= 4 && textLen < 500) naiveScore += 50;

        // ---------- KMP SCORING ----------
        // KMP's strongest cases

        // 1. Repeating patterns and periodic structures
        if (hasRepeating) kmpScore += 100;
        if (periodLength > 0 && periodLength < patternLen / 2) kmpScore += 90;
        if (allSameChar && patternLen > 3) {
            kmpScore += 120;  // "AAAAA" - KMP's paradise
        }

        // 2. Consecutive repeats (AAA, BBB)
        if (maxConsecutive >= 3) kmpScore += 80;
        if (maxConsecutive >= 5) kmpScore += 40;

        // 3. Low entropy (repetitive structure)
        if (patternEntropy < 2.0) kmpScore += 70;
        if (patternEntropy < 1.5) kmpScore += 50;

        // 4. Ideal for streaming data (no backtracking)
        if (textLen > 50000) kmpScore += 40;

        // 5. Pattern or its subparts may frequently appear in text
        if (uniqueRatioPattern < 0.4) kmpScore += 60;

        // 6. Safe for medium length patterns
        if (patternLen >= 4 && patternLen <= 20) kmpScore += 30;

        // 7. Guaranteed linear performance - reasonable in all cases (safety bonus)
        kmpScore += 25;

        // Advantageous in small alphabets like DNA or binary
        if (isDNALike && patternLen > 5) kmpScore += 60;

        // ---------- RABIN-KARP SCORING ----------
        // RabinKarp's golden scenarios

        // 1. Very long texts - hash-based approach shines
        if (textLen > 5000) rabinKarpScore += 50;
        if (textLen > 20000) rabinKarpScore += 60;
        if (textLen > 50000) rabinKarpScore += 50;
        if (textLen > 100000) rabinKarpScore += 40;

        // 2. Medium to long patterns - good hash distribution
        if (patternLen >= 5 && patternLen <= 30) rabinKarpScore += 60;
        if (patternLen >= 10 && patternLen <= 25) rabinKarpScore += 40;

        // 3. High entropy - low hash collision risk
        if (patternEntropy > 3.0) rabinKarpScore += 70;
        if (patternEntropy > 3.5) rabinKarpScore += 50;
        if (patternEntropy > 4.0) rabinKarpScore += 30;

        // 4. Similar text and pattern entropy (characteristic match)
        double entropyDiff = Math.abs(patternEntropy - textEntropy);
        if (entropyDiff < 0.5) rabinKarpScore += 50;
        if (entropyDiff < 0.3) rabinKarpScore += 30;

        // 5. Wide alphabet (hash diversity)
        if (alphabetSize > 20) rabinKarpScore += 40;
        if (alphabetSize > 40) rabinKarpScore += 30;

        // 6. Low collision risk - RabinKarp's requirement
        if (collisionRisk < 0.3) rabinKarpScore += 60;
        if (collisionRisk < 0.2) rabinKarpScore += 40;

        // 7. Good for uniform character distribution
        if (uniqueRatioPattern > 0.5 && uniqueRatioPattern < 0.9) rabinKarpScore += 40;

        // 8. Excellent when BM and KMP are not ideal
        if (!isNaturalLanguage && !hasHighRepetition && textLen > 5000) {
            rabinKarpScore += 50;
        }

        // 9. Works well with moderate pattern lengths in large texts
        if (patternLen >= 8 && patternLen <= 20 && textLen > 10000) {
            rabinKarpScore += 60;
        }

        // PENALTY: High collision risk
        if (collisionRisk > 0.6) rabinKarpScore -= 70;
        if (collisionRisk > 0.8) rabinKarpScore -= 50;

        // PENALTY: Very short patterns (hash overhead not worth it)
        if (patternLen < 5) rabinKarpScore -= 40;

        // ---------- BOYER-MOORE SCORING ----------
        // Boyer-Moore's shining moments

        // 1. Long patterns (can reach sub-linear O(n/m) speed)
        if (patternLen > 8) boyerMooreScore += 60;
        if (patternLen > 15) boyerMooreScore += 70;
        if (patternLen > 25) boyerMooreScore += 60;
        if (patternLen > 35) boyerMooreScore += 50;

        // 2. Wide alphabet (Bad Character Heuristic very effective)
        if (alphabetSize > 15) boyerMooreScore += 70;
        if (alphabetSize > 30) boyerMooreScore += 60;
        if (alphabetSize > 50) boyerMooreScore += 40;

        // 3. High unique character ratio
        if (uniqueRatioPattern > 0.7) boyerMooreScore += 90;
        if (uniqueRatioPattern > 0.8) boyerMooreScore += 50;

        // 4. Natural language (English, Turkish, etc.) - wide alphabet + long words
        if (isNaturalLanguage && patternLen > 5) boyerMooreScore += 80;

        // 5. High entropy (good character distribution)
        if (patternEntropy > 3.5) boyerMooreScore += 70;
        if (patternEntropy > 4.0) boyerMooreScore += 40;

        // 6. Long text (big jumps more valuable)
        if (textLen > 5000) boyerMooreScore += 50;
        if (textLen > 20000) boyerMooreScore += 40;
        if (textLen > 100000) boyerMooreScore += 30;

        // 7. Mismatch not at the end (not BM's weak point)
        if (mismatchPosition < 0.7) boyerMooreScore += 40;

        // PENALTY: Small alphabet and repetitive structure (BM's weakness)
        if (isDNALike || alphabetSize < 8) {
            boyerMooreScore -= 80;  // Boyer-Moore useless in DNA/Binary
        }

        if (patternEntropy < 2.0) boyerMooreScore -= 50;  // Repetitive structure

        // End-to-start mismatch scenario
        if (mismatchPosition > 0.8 && hasHighRepetition) {
            boyerMooreScore -= 60;  // Patterns like "AAAAB"
        }

        // ============ SPECIAL CASE CHECKS ============

        // DNA/Binary-like small alphabet - Boyer-Moore very bad
        if (isDNALike && alphabetSize <= 4) {
            if (hasRepeating || maxConsecutive >= 3) {
                return "KMP";  // KMP is king in small alphabet with repetition
            }
            // RabinKarp good for DNA when pattern is medium-long
            if (patternLen > 7 && textLen > 1000) {
                return "RabinKarp";
            }
            return "KMP";
        }

        // Text "AAAAA", Pattern "BAAAA" - BM's nightmare
        if (allSameChar && textLen > 100) {
            return "KMP";  // KMP finishes in single pass
        }

        // Natural language + long word - BM's golden scenario
        if (isNaturalLanguage && patternLen > 10 && alphabetSize > 20) {
            return "BoyerMoore";  // Ctrl+F scenario
        }

        // Very long text + medium pattern + high entropy - RabinKarp excels
        if (textLen > 50000 && patternLen >= 8 && patternLen <= 25 && patternEntropy > 3.0) {
            if (collisionRisk < 0.4 && !hasHighRepetition) {
                return "RabinKarp";  // Hash-based perfect for large texts
            }
        }

        // Large text + medium pattern + not natural language - RabinKarp territory
        if (textLen > 20000 && patternLen >= 10 && patternLen <= 20 && !isNaturalLanguage) {
            if (collisionRisk < 0.5) {
                return "RabinKarp";
            }
        }

        // Streaming data (very long text, no backtracking)
        if (textLen > 100000 && hasHighRepetition) {
            return "KMP";  // Works without backtracking in streaming
        }

        // Moderate entropy, large text, medium pattern - RabinKarp sweet spot
        if (patternEntropy >= 2.5 && patternEntropy <= 3.5 &&
                textLen > 15000 && patternLen >= 8 && patternLen <= 22) {
            if (collisionRisk < 0.5) {
                return "RabinKarp";
            }
        }

        // Log files, uniform data patterns - RabinKarp efficient
        if (textLen > 10000 && !hasHighRepetition && !isNaturalLanguage &&
                patternLen >= 6 && patternLen <= 18) {
            return "RabinKarp";
        }

        // ============ SELECT HIGHEST SCORE ============
        int maxScore = Math.max(Math.max(naiveScore, kmpScore),
                Math.max(rabinKarpScore, boyerMooreScore));

        // Minimum threshold check - avoid choosing with very low scores
        if (maxScore < 20) return "KMP";  // Default safe choice

        if (maxScore == boyerMooreScore) {
            return "BoyerMoore";
        } else if (maxScore == kmpScore) {
            return "KMP";
        } else if (maxScore == rabinKarpScore) {
            return "RabinKarp";
        } else {
            return "Naive";
        }
    }

    /**
     * Shannon entropy - measure of randomness/diversity in pattern
     * 0-2: Very repetitive, 2-3: Moderate, 3+: High diversity
     */
    private double calculateEntropy(String str) {
        if (str.length() == 0) return 0.0;

        int[] freq = new int[256];
        for (char c : str.toCharArray()) {
            freq[c]++;
        }

        double entropy = 0.0;
        int len = str.length();

        for (int f : freq) {
            if (f > 0) {
                double prob = (double) f / len;
                entropy -= prob * (Math.log(prob) / Math.log(2));
            }
        }

        return entropy;
    }

    /**
     * Count unique characters
     */
    private int countUniqueChars(String str) {
        boolean[] seen = new boolean[256];
        int count = 0;
        for (char c : str.toCharArray()) {
            if (!seen[c]) {
                seen[c] = true;
                count++;
            }
        }
        return count;
    }

    /**
     * Check for repeating periodic structure (ABABAB, ABCABC)
     */
    private boolean hasRepeatingPattern(String pattern) {
        if (pattern.length() < 2) return false;

        for (int len = 1; len <= pattern.length() / 2; len++) {
            if (pattern.length() % len == 0) {
                String period = pattern.substring(0, len);
                boolean matches = true;

                for (int i = len; i < pattern.length(); i += len) {
                    if (!pattern.substring(i, i + len).equals(period)) {
                        matches = false;
                        break;
                    }
                }

                if (matches) return true;
            }
        }
        return false;
    }

    /**
     * Maximum consecutive repeats (AAA=3, AAAA=4)
     */
    private int maxConsecutiveRepeats(String pattern) {
        if (pattern.length() == 0) return 0;

        int maxCount = 1;
        int currentCount = 1;

        for (int i = 1; i < pattern.length(); i++) {
            if (pattern.charAt(i) == pattern.charAt(i - 1)) {
                currentCount++;
                maxCount = Math.max(maxCount, currentCount);
            } else {
                currentCount = 1;
            }
        }
        return maxCount;
    }

    /**
     * Find period length (ABABAB->2, ABCABC->3)
     */
    private int findPeriod(String pattern) {
        for (int len = 1; len <= pattern.length() / 2; len++) {
            if (pattern.length() % len == 0) {
                String period = pattern.substring(0, len);
                boolean isPeriodic = true;

                for (int i = len; i < pattern.length(); i += len) {
                    if (!pattern.substring(i, i + len).equals(period)) {
                        isPeriodic = false;
                        break;
                    }
                }
                if (isPeriodic) return len;
            }
        }
        return -1;
    }

    /**
     * Is it DNA or Binary-like small alphabet?
     * If alphabet <= 4 chars (A,T,G,C or 0,1) it's DNA/Binary-like
     */
    private boolean isdnaLike(String pattern, String text) {
        int uniquePattern = countUniqueChars(pattern);
        int uniqueText = countUniqueChars(text.substring(0, Math.min(500, text.length())));

        return Math.max(uniquePattern, uniqueText) <= 4;
    }

    /**
     * Is it natural language-like? (English, Turkish, etc.)
     * Wide alphabet (>15) and pattern contains space or punctuation
     */
    private boolean isNaturalLanguageLike(String pattern, int alphabetSize) {
        if (alphabetSize < 15) return false;

        // Contains space or common punctuation?
        for (char c : pattern.toCharArray()) {
            if (c == ' ' || c == '.' || c == ',' || c == '!' || c == '?') {
                return true;
            }
        }

        // Mixed upper/lower case (natural language indicator)
        boolean hasUpper = false;
        boolean hasLower = false;
        for (char c : pattern.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isLowerCase(c)) hasLower = true;
        }

        return hasUpper && hasLower;
    }

    /**
     * Calculate how rare a character is in text
     * 1.0 = never appears, 0.0 = appears everywhere
     */
    private double calculateCharRarity(char c, String text) {
        int count = 0;
        int sampleSize = Math.min(1000, text.length());

        for (int i = 0; i < sampleSize; i++) {
            if (text.charAt(i) == c) count++;
        }

        return 1.0 - ((double) count / sampleSize);
    }

    /**
     * Estimate mismatch position
     * 0.0 = mismatch at start, 1.0 = mismatch at end
     * End mismatch is bad for Boyer-Moore
     */
    private double estimateMismatchPosition(String pattern, String text) {
        if (pattern.length() <= 1) return 0.5;

        // Check how frequent the last character is in text
        char lastChar = pattern.charAt(pattern.length() - 1);
        int sampleSize = Math.min(1000, text.length());
        int count = 0;

        for (int i = 0; i < sampleSize; i++) {
            if (text.charAt(i) == lastChar) count++;
        }

        double lastCharFreq = (double) count / sampleSize;

        // If last char is frequent, mismatch probably at end
        if (lastCharFreq > 0.2) return 0.8;

        // If repetitive structure and last char is different ("AAAAB")
        if (maxConsecutiveRepeats(pattern) >= 3) {
            if (pattern.charAt(pattern.length() - 1) != pattern.charAt(0)) {
                return 0.9;  // Definitely mismatch at end
            }
        }

        return 0.4;  // Usually at middle/start
    }

    /**
     * Estimate hash collision risk for RabinKarp
     * 0.0 = very low risk, 1.0 = very high risk
     * High collision risk makes RabinKarp inefficient
     */
    private double estimateHashCollisionRisk(String pattern, String text, double entropy) {
        // Low entropy = more collisions (repetitive patterns)
        double entropyRisk = Math.max(0, (3.5 - entropy) / 3.5);

        // Short patterns = higher collision probability
        double lengthRisk = 0.0;
        if (pattern.length() < 5) lengthRisk = 0.8;
        else if (pattern.length() < 8) lengthRisk = 0.4;
        else if (pattern.length() < 12) lengthRisk = 0.2;

        // Small alphabet = more collisions
        int uniqueChars = countUniqueChars(pattern);
        double alphabetRisk = 0.0;
        if (uniqueChars < 4) alphabetRisk = 0.7;
        else if (uniqueChars < 8) alphabetRisk = 0.4;
        else if (uniqueChars < 12) alphabetRisk = 0.2;

        // All same character = maximum collision risk
        if (uniqueChars == 1) return 1.0;

        // Combine risks (weighted average)
        return (entropyRisk * 0.4 + lengthRisk * 0.3 + alphabetRisk * 0.3);
    }

    @Override
    public String getStrategyDescription() {
        return "=== ADVANCED MULTI-FACTOR INTELLIGENT ANALYSIS ===\n\n" +
                " ANALYZED FACTORS:\n" +
                "1. Data Dimensions (text/pattern lengths)\n" +
                "2. Shannon Entropy (information theory - repetition measure)\n" +
                "3. Alphabet Size (character diversity)\n" +
                "4. Unique Character Ratio\n" +
                "5. Periodic/Repeating Pattern Detection\n" +
                "6. Consecutive Repeat Analysis\n" +
                "7. DNA/Binary-like Small Alphabet Detection\n" +
                "8. Natural Language Detection (English, Turkish, etc.)\n" +
                "9. First Character Rarity Analysis\n" +
                "10. Mismatch Position Estimation\n" +
                "11. Hash Collision Risk (for RabinKarp)\n\n" +

                " ALGORITHM SELECTION RULES:\n\n" +

                "NAIVE:\n" +
                "Small datasets (<50 chars)\n" +
                "First char is rare (early mismatch)\n" +
                "Preprocessing cost unnecessary\n" +
                "Repetitive long texts\n\n" +

                "KMP:\n" +
                "Repeating patterns (ABABAB, ADNANA)\n" +
                "All same chars (AAAAA)\n" +
                "Streaming data (no backtracking)\n" +
                "DNA/Binary small alphabet\n" +
                "Guaranteed O(n+m) linear performance\n" +
                "Very short patterns (overhead)\n\n" +

                "BOYER-MOORE:\n" +
                "Natural language (English) - Ctrl+F scenario\n" +
                "Long patterns (>10 chars)\n" +
                "Wide alphabet (>15 unique chars)\n" +
                "Can reach sub-linear O(n/m) speed\n" +
                "DNA/Binary (small alphabet)\n" +
                "End-to-start mismatch (AAAAB)\n\n" +

                "RABIN-KARP:\n" +
                "Very long texts (>5K chars)\n" +
                "Medium patterns (5-30 chars)\n" +
                "High entropy (low collision risk)\n" +
                "Uniform data (logs, structured files)\n" +
                "Good when BM/KMP aren't ideal\n" +
                "High hash collision risk\n" +
                "Very short patterns (<5)\n\n" +

                " SCORING SYSTEM:\n" +
                "Each algorithm scored based on factors\n" +
                "Highest score wins\n" +
                "Special cases have priority";
    }
}


/**
 * Example implementation showing how pre-analysis could work
 * This is for demonstration purposes
 */
class ExamplePreAnalysis extends PreAnalysis {

    @Override
    public String chooseAlgorithm(String text, String pattern) {
        int textLen = text.length();
        int patternLen = pattern.length();

        // Simple heuristic example
        if (patternLen <= 3) {
            return "Naive"; // For very short patterns, naive is often fastest
        } else if (hasRepeatingPrefix(pattern)) {
            return "KMP"; // KMP is good for patterns with repeating prefixes
        } else if (patternLen > 10 && textLen > 1000) {
            return "RabinKarp"; // RabinKarp can be good for long patterns in long texts
        } else {
            return "Naive"; // Default to naive for other cases
        }
    }

    private boolean hasRepeatingPrefix(String pattern) {
        if (pattern.length() < 2) return false;

        // Check if first character repeats
        char first = pattern.charAt(0);
        int count = 0;
        for (int i = 0; i < Math.min(pattern.length(), 5); i++) {
            if (pattern.charAt(i) == first) count++;
        }
        return count >= 3;
    }

    @Override
    public String getStrategyDescription() {
        return "Example strategy: Choose based on pattern length and characteristics";
    }
}

/**
 * Instructor's pre-analysis implementation (for testing purposes only)
 * Students should NOT modify this class
 */
class InstructorPreAnalysis extends PreAnalysis {

    @Override
    public String chooseAlgorithm(String text, String pattern) {
        // This is a placeholder for instructor testing
        // Students should focus on implementing StudentPreAnalysis
        return null;
    }

    @Override
    public String getStrategyDescription() {
        return "Instructor's testing implementation";
    }
}
