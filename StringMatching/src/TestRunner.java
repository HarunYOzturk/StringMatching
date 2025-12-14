import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Runs test cases and generates comparison tables
 */
public class TestRunner {
    
    /**
     * Result of running a single algorithm on a single test case
     */
    public static class TestResult {
        String algorithmName;
        String testCaseName;
        boolean success;
        String result;
        long executionTimeNanos;
        String errorMessage;
        
        public TestResult(String algorithmName, String testCaseName) {
            this.algorithmName = algorithmName;
            this.testCaseName = testCaseName;
        }
    }
    
    /**
     * Result of pre-analysis
     */
    public static class PreAnalysisResult {
        String chosenAlgorithm;
        long analysisTimeNanos;
        long algorithmExecutionTimeNanos;
        boolean success;
        String result;
        
        // For comparison: what if we had used a different algorithm?
        Map<String, Long> alternativeExecutionTimes = new HashMap<>();
    }
    
    /**
     * Run all algorithms on all test cases
     */
    public static List<TestResult> runAllTests(List<TestCase> testCases) {
        List<TestResult> results = new ArrayList<>();
        Set<Class<? extends Solution>> algorithms = Solution.SUBCLASSES;
        
        for (TestCase testCase : testCases) {
            for (Class<? extends Solution> algorithmClass : algorithms) {
                TestResult result = runSingleTest(algorithmClass, testCase);
                results.add(result);
            }
        }
        
        return results;
    }
    
    /**
     * Run a single algorithm on a single test case
     */
    private static TestResult runSingleTest(Class<? extends Solution> algorithmClass, TestCase testCase) {
        TestResult result = new TestResult(algorithmClass.getSimpleName(), testCase.getName());
        
        try {
            Constructor<? extends Solution> constructor = algorithmClass.getDeclaredConstructor();
            Solution solution = constructor.newInstance();
            
            long startTime = System.nanoTime();
            String output = solution.Solve(testCase.getText(), testCase.getPattern());
            long endTime = System.nanoTime();
            
            result.executionTimeNanos = endTime - startTime;
            result.result = output;
            result.success = output.equals(testCase.getExpectedResult());
            
        } catch (UnsupportedOperationException e) {
            result.success = false;
            result.errorMessage = "Not implemented";
        } catch (Exception e) {
            result.success = false;
            result.errorMessage = e.getMessage();
        }
        
        return result;
    }
    
    /**
     * Run pre-analysis and execute the chosen algorithm
     */
    public static PreAnalysisResult runWithPreAnalysis(PreAnalysis preAnalysis, TestCase testCase) {
        PreAnalysisResult result = new PreAnalysisResult();
        
        // Run pre-analysis
        long analysisStart = System.nanoTime();
        String chosenAlgorithm = preAnalysis.chooseAlgorithm(testCase.getText(), testCase.getPattern());
        long analysisEnd = System.nanoTime();
        
        result.analysisTimeNanos = analysisEnd - analysisStart;
        result.chosenAlgorithm = chosenAlgorithm;
        
        if (chosenAlgorithm == null) {
            result.success = false;
            return result;
        }
        
        // Execute the chosen algorithm
        try {
            Solution solution = createSolutionByName(chosenAlgorithm);
            long execStart = System.nanoTime();
            String output = solution.Solve(testCase.getText(), testCase.getPattern());
            long execEnd = System.nanoTime();
            
            result.algorithmExecutionTimeNanos = execEnd - execStart;
            result.result = output;
            result.success = output.equals(testCase.getExpectedResult());
            
            // Also run other algorithms for comparison
            for (Class<? extends Solution> algorithmClass : Solution.SUBCLASSES) {
                String algName = algorithmClass.getSimpleName();
                if (!algName.equals(chosenAlgorithm)) {
                    try {
                        Solution altSolution = algorithmClass.getDeclaredConstructor().newInstance();
                        long altStart = System.nanoTime();
                        altSolution.Solve(testCase.getText(), testCase.getPattern());
                        long altEnd = System.nanoTime();
                        result.alternativeExecutionTimes.put(algName, altEnd - altStart);
                    } catch (Exception e) {
                        // Skip algorithms that aren't implemented
                    }
                }
            }
            
        } catch (Exception e) {
            result.success = false;
        }
        
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
     * Generate a comparison table showing which algorithms solved which test cases
     */
    public static void printComparisonTable(List<TestResult> results) {
        // Get unique algorithm names and test case names
        Set<String> algorithmNames = new LinkedHashSet<>();
        Set<String> testCaseNames = new LinkedHashSet<>();
        
        for (TestResult result : results) {
            algorithmNames.add(result.algorithmName);
            testCaseNames.add(result.testCaseName);
        }
        
        System.out.println("\n" + "=".repeat(100));
        System.out.println("ALGORITHM COMPARISON TABLE - Which algorithms solved which test cases");
        System.out.println("=".repeat(100));
        
        // Print header
        System.out.printf("%-30s", "Test Case");
        for (String algName : algorithmNames) {
            System.out.printf("%-15s", algName);
        }
        System.out.println();
        System.out.println("-".repeat(100));
        
        // Print results for each test case
        for (String testCaseName : testCaseNames) {
            System.out.printf("%-30s", truncate(testCaseName, 28));
            
            for (String algName : algorithmNames) {
                TestResult result = findResult(results, algName, testCaseName);
                if (result != null) {
                    if (result.success) {
                        System.out.printf("%-15s", "✓ PASS");
                    } else if (result.errorMessage != null && result.errorMessage.equals("Not implemented")) {
                        System.out.printf("%-15s", "- N/A");
                    } else {
                        System.out.printf("%-15s", "✗ FAIL");
                    }
                } else {
                    System.out.printf("%-15s", "- N/A");
                }
            }
            System.out.println();
        }
        
        System.out.println("=".repeat(100));
        
        // Print summary statistics
        printSummaryStatistics(results, algorithmNames);
    }
    
    /**
     * Print summary statistics for each algorithm
     */
    private static void printSummaryStatistics(List<TestResult> results, Set<String> algorithmNames) {
        System.out.println("\nSUMMARY STATISTICS:");
        System.out.println("-".repeat(100));
        
        for (String algName : algorithmNames) {
            int passed = 0;
            int failed = 0;
            int notImplemented = 0;
            long totalTime = 0;
            int timedTests = 0;
            
            for (TestResult result : results) {
                if (result.algorithmName.equals(algName)) {
                    if (result.errorMessage != null && result.errorMessage.equals("Not implemented")) {
                        notImplemented++;
                    } else if (result.success) {
                        passed++;
                        totalTime += result.executionTimeNanos;
                        timedTests++;
                    } else {
                        failed++;
                    }
                }
            }
            
            double avgTimeMs = timedTests > 0 ? (totalTime / timedTests) / 1_000_000.0 : 0;
            
            System.out.printf("%-15s: %d passed, %d failed", algName, passed, failed);
            if (notImplemented > 0) {
                System.out.printf(", %d not implemented", notImplemented);
            }
            if (timedTests > 0) {
                System.out.printf(" | Avg time: %.4f ms", avgTimeMs);
            }
            System.out.println();
        }
        
        System.out.println("=".repeat(100));
    }
    
    /**
     * Find a specific test result
     */
    private static TestResult findResult(List<TestResult> results, String algName, String testCaseName) {
        for (TestResult result : results) {
            if (result.algorithmName.equals(algName) && result.testCaseName.equals(testCaseName)) {
                return result;
            }
        }
        return null;
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
}

