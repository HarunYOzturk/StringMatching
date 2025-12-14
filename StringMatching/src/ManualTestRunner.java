import java.util.*;

/**
 * Manual Test Runner - Allows running specific tests and viewing detailed time comparisons
 */
public class ManualTestRunner {
    
    /**
     * Result of running a single algorithm on a single test
     */
    static class AlgorithmTestResult {
        String algorithmName;
        String result;
        long executionTimeNanos;
        long[] allRunTimes; // Store all 5 run times
        boolean passed;
        boolean implemented;
        Exception error;

        public AlgorithmTestResult(String algorithmName) {
            this.algorithmName = algorithmName;
            this.implemented = true;
            this.allRunTimes = new long[5];
        }
    }
    
    /**
     * Result of running all algorithms on a single test
     */
    static class TestExecutionResult {
        TestCase testCase;
        Map<String, AlgorithmTestResult> algorithmResults;
        
        public TestExecutionResult(TestCase testCase) {
            this.testCase = testCase;
            this.algorithmResults = new LinkedHashMap<>();
        }
    }
    
    /**
     * Get all registered algorithm solutions
     */
    private static List<Solution> getAllSolutions() {
        List<Solution> solutions = new ArrayList<>();
        try {
            for (Class<? extends Solution> algorithmClass : Solution.SUBCLASSES) {
                Solution instance = algorithmClass.getDeclaredConstructor().newInstance();
                solutions.add(instance);
            }
        } catch (Exception e) {
            System.err.println("Error creating algorithm instances: " + e.getMessage());
        }
        return solutions;
    }

    /**
     * Run specific tests by their indices
     */
    public static List<TestExecutionResult> runTests(List<TestCase> allTests, List<Integer> testIndices) {
        List<TestExecutionResult> results = new ArrayList<>();
        List<Solution> algorithms = getAllSolutions();
        
        System.out.println("Running " + testIndices.size() + " test(s) with " + algorithms.size() + " algorithm(s)...\n");
        
        for (int testIndex : testIndices) {
            if (testIndex < 0 || testIndex >= allTests.size()) {
                System.out.println("⚠ Warning: Test index " + testIndex + " is out of range (0-" + (allTests.size()-1) + ")");
                continue;
            }
            
            TestCase test = allTests.get(testIndex);
            TestExecutionResult testResult = new TestExecutionResult(test);
            
            // Run each algorithm on this test
            for (Solution algorithm : algorithms) {
                AlgorithmTestResult algResult = new AlgorithmTestResult(algorithm.getName());

                try {
                    // Warm up
                    algorithm.Solve(test.getText(), test.getPattern());

                    // Run 5 times and take average
                    long totalTime = 0;
                    String result = null;
                    for (int run = 0; run < 5; run++) {
                        long startTime = System.nanoTime();
                        result = algorithm.Solve(test.getText(), test.getPattern());
                        long endTime = System.nanoTime();
                        algResult.allRunTimes[run] = endTime - startTime;
                        totalTime += algResult.allRunTimes[run];
                    }

                    algResult.result = result;
                    algResult.executionTimeNanos = totalTime / 5; // Average time
                    algResult.passed = result.equals(test.getExpectedResult());

                } catch (UnsupportedOperationException e) {
                    algResult.implemented = false;
                } catch (Exception e) {
                    algResult.error = e;
                    algResult.passed = false;
                }

                testResult.algorithmResults.put(algorithm.getName(), algResult);
            }
            
            results.add(testResult);
        }
        
        return results;
    }
    
    /**
     * Print detailed results table with execution times
     */
    public static void printDetailedResultsTable(List<TestExecutionResult> results) {
        if (results.isEmpty()) {
            System.out.println("No results to display.");
            return;
        }

        // ANSI color codes
        final String GREEN = "\u001B[32m";
        final String YELLOW = "\u001B[33m";
        final String CYAN = "\u001B[36m";
        final String BOLD = "\u001B[1m";
        final String RESET = "\u001B[0m";

        // Get algorithm names
        List<String> algorithmNames = new ArrayList<>(results.get(0).algorithmResults.keySet());

        // Calculate column widths
        int testNameWidth = 32;
        int timeWidth = 18;
        int winnerWidth = 18;

        // Print header
        printSeparatorWithWinner(testNameWidth, algorithmNames.size(), timeWidth, winnerWidth);
        System.out.println(BOLD + CYAN + "DETAILED TEST RESULTS - Execution Time Comparison (Average of 5 runs)" + RESET);
        printSeparatorWithWinner(testNameWidth, algorithmNames.size(), timeWidth, winnerWidth);

        // Print column headers
        System.out.print(BOLD + padRight("Test Case", testNameWidth));
        for (String algName : algorithmNames) {
            System.out.print(padRight(algName + " (μs)", timeWidth));
        }
        System.out.print(padRight("Winner", winnerWidth) + RESET);
        System.out.println();
        printSeparatorWithWinner(testNameWidth, algorithmNames.size(), timeWidth, winnerWidth);

        // Print each test result
        for (TestExecutionResult testResult : results) {
            String testName = truncate(testResult.testCase.getName(), testNameWidth - 1);
            System.out.print(padRight(testName, testNameWidth));

            // Find the fastest algorithm for this test
            String fastestAlg = null;
            long fastestTime = Long.MAX_VALUE;
            for (String algName : algorithmNames) {
                AlgorithmTestResult algResult = testResult.algorithmResults.get(algName);
                if (algResult.implemented && algResult.passed && algResult.executionTimeNanos < fastestTime) {
                    fastestTime = algResult.executionTimeNanos;
                    fastestAlg = algName;
                }
            }

            for (String algName : algorithmNames) {
                AlgorithmTestResult algResult = testResult.algorithmResults.get(algName);

                // Show time, or FAIL/ERROR/N/A if not passed
                String display;
                if (!algResult.implemented) {
                    display = "N/A";
                } else if (algResult.error != null) {
                    display = "✗ ERROR";
                } else if (!algResult.passed) {
                    display = "✗ FAIL";
                } else {
                    // Show time for passing tests, highlight if winner
                    String timeStr = String.format("%.3f", algResult.executionTimeNanos / 1000.0);
                    if (algName.equals(fastestAlg)) {
                        display = GREEN + timeStr + RESET;
                    } else {
                        display = timeStr;
                    }
                }
                System.out.print(padRight(display, timeWidth));
            }

            // Winner column
            String winner = fastestAlg != null ? GREEN + "🏆 " + fastestAlg + RESET : "None";
            System.out.print(padRight(winner, winnerWidth));
            System.out.println();
        }

        printSeparatorWithWinner(testNameWidth, algorithmNames.size(), timeWidth, winnerWidth);

        // Print summary statistics
        printSummaryStatistics(results, algorithmNames);
    }
    
    /**
     * Print summary statistics for all tests
     */
    private static void printSummaryStatistics(List<TestExecutionResult> results, List<String> algorithmNames) {
        System.out.println("\nSUMMARY STATISTICS:");
        printSeparator(100, 0, 0, 0);
        
        for (String algName : algorithmNames) {
            int passed = 0;
            int failed = 0;
            int notImplemented = 0;
            long totalTime = 0;
            int implementedCount = 0;
            long minTime = Long.MAX_VALUE;
            long maxTime = Long.MIN_VALUE;
            
            for (TestExecutionResult testResult : results) {
                AlgorithmTestResult algResult = testResult.algorithmResults.get(algName);
                
                if (!algResult.implemented) {
                    notImplemented++;
                } else if (algResult.error != null) {
                    failed++;
                } else if (algResult.passed) {
                    passed++;
                    totalTime += algResult.executionTimeNanos;
                    implementedCount++;
                    minTime = Math.min(minTime, algResult.executionTimeNanos);
                    maxTime = Math.max(maxTime, algResult.executionTimeNanos);
                } else {
                    failed++;
                }
            }
            
            System.out.printf("%-15s: ", algName);
            System.out.printf("%d passed, %d failed", passed, failed);
            
            if (notImplemented > 0) {
                System.out.printf(", %d not implemented", notImplemented);
            }
            
            if (implementedCount > 0) {
                double avgTime = totalTime / (double) implementedCount / 1000.0;
                double minTimeUs = minTime / 1000.0;
                double maxTimeUs = maxTime / 1000.0;
                System.out.printf(" | Avg: %.3f μs, Min: %.3f μs, Max: %.3f μs", avgTime, minTimeUs, maxTimeUs);
            }
            
            System.out.println();
        }
        
        printSeparator(100, 0, 0, 0);
    }
    
    /**
     * Print a comparison showing which algorithm was fastest for each test
     */
    public static void printFastestAlgorithmComparison(List<TestExecutionResult> results) {
        System.out.println("\n");
        printSeparator(80, 0, 0, 0);
        System.out.println("FASTEST ALGORITHM COMPARISON");
        printSeparator(80, 0, 0, 0);
        
        System.out.printf("%-30s %-20s %-15s\n", "Test Case", "Fastest Algorithm", "Time (μs)");
        printSeparator(80, 0, 0, 0);
        
        for (TestExecutionResult testResult : results) {
            String fastestAlg = null;
            long fastestTime = Long.MAX_VALUE;
            
            for (Map.Entry<String, AlgorithmTestResult> entry : testResult.algorithmResults.entrySet()) {
                AlgorithmTestResult algResult = entry.getValue();
                if (algResult.implemented && algResult.passed && algResult.executionTimeNanos < fastestTime) {
                    fastestTime = algResult.executionTimeNanos;
                    fastestAlg = entry.getKey();
                }
            }
            
            String testName = truncate(testResult.testCase.getName(), 29);
            if (fastestAlg != null) {
                System.out.printf("%-30s %-20s %.3f\n", testName, fastestAlg, fastestTime / 1000.0);
            } else {
                System.out.printf("%-30s %-20s %s\n", testName, "None", "-");
            }
        }
        
        printSeparator(80, 0, 0, 0);
    }
    
    // Helper methods
    private static void printSeparator(int testNameWidth, int algorithmCount, int statusWidth, int timeWidth) {
        int totalWidth = testNameWidth + algorithmCount * (statusWidth + timeWidth);
        for (int i = 0; i < totalWidth; i++) {
            System.out.print("=");
        }
        System.out.println();
    }

    private static void printSeparatorWithWinner(int testNameWidth, int algorithmCount, int timeWidth, int winnerWidth) {
        int totalWidth = testNameWidth + algorithmCount * timeWidth + winnerWidth;
        for (int i = 0; i < totalWidth; i++) {
            System.out.print("=");
        }
        System.out.println();
    }

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

    private static String truncate(String s, int maxLength) {
        if (s.length() <= maxLength) {
            return s;
        }
        return s.substring(0, maxLength - 2) + "..";
    }
}

