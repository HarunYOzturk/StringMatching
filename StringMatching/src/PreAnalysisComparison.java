import java.util.*;

/**
 * Compares the performance of using pre-analysis vs running all algorithms
 */
public class PreAnalysisComparison {
    
    /**
     * Result of comparing pre-analysis performance
     */
    public static class ComparisonResult {
        String testCaseName;
        String chosenAlgorithm;
        long preAnalysisTimeNanos;
        long chosenAlgorithmTimeNanos;
        long totalTimeWithPreAnalysis;
        Map<String, Long> allAlgorithmTimes;
        long fastestAlgorithmTime;
        String fastestAlgorithm;
        long timeSavedOrLost; // Positive means saved, negative means lost
        boolean preAnalysisChoseFastest;
        List<String> allAlgorithmNames; // Ordered list of all algorithms

        public ComparisonResult(String testCaseName) {
            this.testCaseName = testCaseName;
            this.allAlgorithmTimes = new HashMap<>();
            this.allAlgorithmNames = new ArrayList<>();
        }
    }
    
    /**
     * Run comparison for all test cases
     */
    public static List<ComparisonResult> runComparison(PreAnalysis preAnalysis, List<TestCase> testCases) {
        List<ComparisonResult> results = new ArrayList<>();
        
        for (TestCase testCase : testCases) {
            ComparisonResult result = compareForTestCase(preAnalysis, testCase);
            if (result != null) {
                results.add(result);
            }
        }
        
        return results;
    }
    
    /**
     * Compare pre-analysis performance for a single test case
     * Runs each algorithm 5 times and takes average
     */
    private static ComparisonResult compareForTestCase(PreAnalysis preAnalysis, TestCase testCase) {
        ComparisonResult result = new ComparisonResult(testCase.getName());

        // Run pre-analysis 5 times and take average
        long totalAnalysisTime = 0;
        String chosenAlgorithm = null;
        for (int i = 0; i < 5; i++) {
            long analysisStart = System.nanoTime();
            chosenAlgorithm = preAnalysis.chooseAlgorithm(testCase.getText(), testCase.getPattern());
            long analysisEnd = System.nanoTime();
            totalAnalysisTime += (analysisEnd - analysisStart);
        }

        result.preAnalysisTimeNanos = totalAnalysisTime / 5;
        result.chosenAlgorithm = chosenAlgorithm;

        // If pre-analysis returns null, skip this test case
        if (chosenAlgorithm == null) {
            return null;
        }

        // Run the chosen algorithm 5 times and take average
        try {
            Solution chosenSolution = createSolutionByName(chosenAlgorithm);
            // Warm up
            chosenSolution.Solve(testCase.getText(), testCase.getPattern());

            long totalChosenTime = 0;
            for (int i = 0; i < 5; i++) {
                long chosenStart = System.nanoTime();
                chosenSolution.Solve(testCase.getText(), testCase.getPattern());
                long chosenEnd = System.nanoTime();
                totalChosenTime += (chosenEnd - chosenStart);
            }

            result.chosenAlgorithmTimeNanos = totalChosenTime / 5;
            result.totalTimeWithPreAnalysis = result.preAnalysisTimeNanos + result.chosenAlgorithmTimeNanos;

        } catch (Exception e) {
            return null; // Skip if chosen algorithm fails
        }

        // Run all algorithms 5 times for comparison
        long minTime = result.chosenAlgorithmTimeNanos;
        String fastestAlg = chosenAlgorithm;

        result.allAlgorithmTimes.put(chosenAlgorithm, result.chosenAlgorithmTimeNanos);
        result.allAlgorithmNames.add(chosenAlgorithm);

        for (Class<? extends Solution> algorithmClass : Solution.SUBCLASSES) {
            String algName = algorithmClass.getSimpleName();

            if (!algName.equals(chosenAlgorithm)) {
                try {
                    Solution solution = algorithmClass.getDeclaredConstructor().newInstance();
                    // Warm up
                    solution.Solve(testCase.getText(), testCase.getPattern());

                    long totalTime = 0;
                    for (int i = 0; i < 5; i++) {
                        long start = System.nanoTime();
                        solution.Solve(testCase.getText(), testCase.getPattern());
                        long end = System.nanoTime();
                        totalTime += (end - start);
                    }

                    long avgTime = totalTime / 5;
                    result.allAlgorithmTimes.put(algName, avgTime);
                    result.allAlgorithmNames.add(algName);

                    if (avgTime < minTime) {
                        minTime = avgTime;
                        fastestAlg = algName;
                    }

                } catch (Exception e) {
                    // Skip algorithms that aren't implemented
                }
            }
        }

        result.fastestAlgorithmTime = minTime;
        result.fastestAlgorithm = fastestAlg;
        result.preAnalysisChoseFastest = fastestAlg.equals(chosenAlgorithm);

        // Calculate time saved or lost
        // Time saved = (fastest algorithm time) - (pre-analysis time + chosen algorithm time)
        // Positive means we saved time, negative means we lost time
        result.timeSavedOrLost = minTime - result.totalTimeWithPreAnalysis;

        return result;
    }
    
    /**
     * Create a solution instance by algorithm name
     */
    private static Solution createSolutionByName(String name) throws Exception {
        for (Class<? extends Solution> algorithmClass : Solution.SUBCLASSES) {
            if (algorithmClass.getSimpleName().equals(name)) {
                return algorithmClass.getDeclaredConstructor().newInstance();
            }
        }
        throw new IllegalArgumentException("Algorithm not found: " + name);
    }
    
    /**
     * Print the pre-analysis comparison table
     */
    public static void printComparisonTable(List<ComparisonResult> results, PreAnalysis preAnalysis) {
        if (results.isEmpty()) {
            System.out.println("\n" + "=".repeat(100));
            System.out.println("PRE-ANALYSIS COMPARISON TABLE");
            System.out.println("=".repeat(100));
            System.out.println("Pre-analysis returned null for all test cases (no algorithm selection made).");
            System.out.println("This means all algorithms were run without pre-analysis optimization.");
            System.out.println("=".repeat(100));
            return;
        }
        
        System.out.println("\n" + "=".repeat(120));
        System.out.println("PRE-ANALYSIS PERFORMANCE COMPARISON");
        System.out.println("Strategy: " + preAnalysis.getStrategyDescription());
        System.out.println("=".repeat(120));
        
        // Print header
        System.out.printf("%-25s %-12s %-12s %-12s %-12s %-15s %-15s%n",
                "Test Case", "Chosen Alg", "Analysis(μs)", "Exec(μs)", "Total(μs)", "Fastest Alg", "Time Diff(μs)");
        System.out.println("-".repeat(120));
        
        // Print results for each test case
        long totalTimeSaved = 0;
        int correctChoices = 0;
        
        for (ComparisonResult result : results) {
            double analysisTimeUs = result.preAnalysisTimeNanos / 1000.0;
            double execTimeUs = result.chosenAlgorithmTimeNanos / 1000.0;
            double totalTimeUs = result.totalTimeWithPreAnalysis / 1000.0;
            double timeDiffUs = -result.timeSavedOrLost / 1000.0; // Negative because we want to show saved as positive
            
            String timeDiffStr;
            if (result.preAnalysisChoseFastest) {
                timeDiffStr = String.format("✓ %.2f", timeDiffUs);
                correctChoices++;
            } else {
                timeDiffStr = String.format("✗ %.2f", timeDiffUs);
            }
            
            System.out.printf("%-25s %-12s %12.2f %12.2f %12.2f %-15s %15s%n",
                    truncate(result.testCaseName, 23),
                    truncate(result.chosenAlgorithm, 10),
                    analysisTimeUs,
                    execTimeUs,
                    totalTimeUs,
                    truncate(result.fastestAlgorithm, 13),
                    timeDiffStr);
            
            totalTimeSaved += result.timeSavedOrLost;
        }
        
        System.out.println("=".repeat(120));
        
        // Print summary
        printSummary(results, totalTimeSaved, correctChoices);
    }
    
    /**
     * Print summary statistics
     */
    private static void printSummary(List<ComparisonResult> results, long totalTimeSaved, int correctChoices) {
        System.out.println("\nPRE-ANALYSIS SUMMARY:");
        System.out.println("-".repeat(120));
        
        double totalTimeSavedMs = totalTimeSaved / 1_000_000.0;
        double avgTimeSavedMs = totalTimeSavedMs / results.size();
        double accuracyPercent = (correctChoices * 100.0) / results.size();
        
        System.out.printf("Total test cases analyzed: %d%n", results.size());
        System.out.printf("Correct algorithm choices: %d / %d (%.1f%%)%n", correctChoices, results.size(), accuracyPercent);
        System.out.println();
        
        if (totalTimeSavedMs > 0) {
            System.out.printf("✓ Pre-analysis SAVED %.4f ms total (avg %.4f ms per test)%n", 
                    Math.abs(totalTimeSavedMs), Math.abs(avgTimeSavedMs));
            System.out.println("  Pre-analysis overhead was worth it!");
        } else if (totalTimeSavedMs < 0) {
            System.out.printf("✗ Pre-analysis COST %.4f ms total (avg %.4f ms per test)%n", 
                    Math.abs(totalTimeSavedMs), Math.abs(avgTimeSavedMs));
            System.out.println("  Pre-analysis overhead was NOT worth it for these test cases.");
        } else {
            System.out.println("Pre-analysis broke even (no time saved or lost).");
        }
        
        System.out.println();
        System.out.println("INTERPRETATION:");
        System.out.println("- 'Analysis(μs)': Time spent in pre-analysis choosing algorithm");
        System.out.println("- 'Exec(μs)': Time spent executing the chosen algorithm");
        System.out.println("- 'Total(μs)': Analysis + Execution time");
        System.out.println("- 'Fastest Alg': The actually fastest algorithm for this test case");
        System.out.println("- 'Time Diff(μs)': Positive = saved time, Negative = lost time");
        System.out.println("- '✓' = Pre-analysis chose the fastest algorithm");
        System.out.println("- '✗' = Pre-analysis did NOT choose the fastest algorithm");
        
        System.out.println("=".repeat(120));
    }
    
    /**
     * Print detailed algorithm comparison table showing PreAnalysis + Chosen vs Other Algorithms
     */
    public static void printDetailedAlgorithmComparison(List<ComparisonResult> results) {
        if (results.isEmpty()) {
            return;
        }

        // ANSI color codes
        final String GREEN = "\u001B[32m";
        final String RED = "\u001B[31m";
        final String RESET = "\u001B[0m";

        System.out.println("\n" + "=".repeat(140));
        System.out.println("PREANALYSIS PERFORMANCE COMPARISON");
        System.out.println("Shows: (PreAnalysis + Chosen Algorithm) vs Each Algorithm");
        System.out.println("Green = PreAnalysis was faster | Red = PreAnalysis was slower");
        System.out.println("=".repeat(140));

        // Get all unique algorithm names
        Set<String> allAlgSet = new LinkedHashSet<>();
        for (ComparisonResult result : results) {
            allAlgSet.addAll(result.allAlgorithmNames);
        }
        List<String> allAlgorithms = new ArrayList<>(allAlgSet);

        // Print header
        System.out.printf("%-32s %-15s %-20s", "Test Case", "Choice", "PreA+Choice (μs)");
        for (String alg : allAlgorithms) {
            System.out.printf(" %-22s", "vs " + alg);
        }
        System.out.println();
        System.out.println("-".repeat(140));

        // Print each test case
        for (ComparisonResult result : results) {
            String testName = truncate(result.testCaseName, 30);
            String chosenAlg = truncate(result.chosenAlgorithm, 13);
            double totalWithPreAnalysis = result.totalTimeWithPreAnalysis / 1000.0;

            System.out.printf("%-32s %-15s %20.2f", testName, chosenAlg, totalWithPreAnalysis);

            // For each algorithm, show the difference
            for (String alg : allAlgorithms) {
                if (alg.equals(result.chosenAlgorithm)) {
                    // For chosen algorithm, show N/A (since it's already included in PreA+Chosen)
                    System.out.print(padRight(" N/A", 23));
                } else {
                    Long algTime = result.allAlgorithmTimes.get(alg);
                    if (algTime != null) {
                        double algTimeUs = algTime / 1000.0;
                        // Calculate difference: (PreA + Chosen) - Algorithm
                        // Negative = PreAnalysis was faster (good) = GREEN
                        // Positive = PreAnalysis was slower (bad) = RED
                        double diff = totalWithPreAnalysis - algTimeUs;
                        String diffStr;
                        if (diff < 0) {
                            // PreAnalysis was faster (saved time) - GREEN
                            diffStr = String.format(" %s%.2f μs%s", GREEN, diff, RESET);
                        } else {
                            // PreAnalysis was slower (wasted time) - RED
                            diffStr = String.format(" %s+%.2f μs%s", RED, diff, RESET);
                        }
                        System.out.print(padRight(diffStr, 23));
                    } else {
                        System.out.print(padRight(" N/A", 23));
                    }
                }
            }
            System.out.println();
        }

        System.out.println("=".repeat(140));

        // Print interpretation
        System.out.println("\nINTERPRETATION:");
        System.out.println("- 'PreA+Choice (μs)': Total time = PreAnalysis time + Chosen algorithm execution time");
        System.out.println("- 'vs Algorithm': Difference = (PreA+Choice) - Algorithm");
        System.out.println("  - " + GREEN + "Negative (Green)" + RESET + " = PreAnalysis was FASTER (saved time)");
        System.out.println("  - " + RED + "Positive (Red)" + RESET + " = PreAnalysis was SLOWER (wasted time)");
        System.out.println("- 'N/A' = This is the chosen algorithm (already included in PreA+Choice)");
        System.out.println("=".repeat(140));
    }

    /**
     * Truncate string to specified length
     */
    private static String truncate(String str, int maxLength) {
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 2) + "..";
    }

    /**
     * Pad string to the right, accounting for ANSI color codes
     */
    private static String padRight(String s, int n) {
        // Count visible characters (excluding ANSI codes)
        String withoutAnsi = s.replaceAll("\u001B\\[[;\\d]*m", "");
        int visibleLength = withoutAnsi.length();

        if (visibleLength >= n) {
            return s.substring(0, Math.min(s.length(), n));
        }

        // Add padding for the difference
        int padding = n - visibleLength;
        return s + " ".repeat(padding);
    }
}

