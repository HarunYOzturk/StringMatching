import java.util.*;

/**
 * String Matching Algorithm Test System
 *
 * This program runs string matching algorithms on test cases loaded from JSON files
 * and displays detailed performance comparisons.
 *
 * Usage examples:
 *   java ManualTest                    - Run all tests with full comparison tables
 *   java ManualTest 0 1 2              - Run specific tests (0, 1, and 2)
 *   java ManualTest 0-5                - Run range of tests (0 through 5)
 *   java ManualTest list               - List all available tests
 *   java ManualTest share              - Run only shared tests (for students)
 *   java ManualTest hidden             - Run only hidden tests (for grading)
 *   java ManualTest preanalysis        - Run with pre-analysis comparison
 */
public class ManualTest {
    
    public static void main(String[] args) {
        printHeader();

        // Register algorithms by loading classes
        try {
            Class.forName("Naive");
            Class.forName("KMP");
            Class.forName("RabinKarp");
            Class.forName("BoyerMoore");
            Class.forName("GoCrazy");
        } catch (ClassNotFoundException e) {
            System.err.println("Error loading algorithm classes: " + e.getMessage());
        }

        // Load test cases from files
        List<TestCase> sharedTestCases = TestCaseLoader.loadSharedTestCases();
        List<TestCase> hiddenTestCases = TestCaseLoader.loadHiddenTestCases();
        List<TestCase> allTests = new ArrayList<>();
        allTests.addAll(sharedTestCases);
        allTests.addAll(hiddenTestCases);

        // Create index lists for shared and hidden tests
        List<Integer> sharedTests = new ArrayList<>();
        for (int i = 0; i < sharedTestCases.size(); i++) {
            sharedTests.add(i);
        }
        List<Integer> hiddenTests = new ArrayList<>();
        for (int i = sharedTestCases.size(); i < allTests.size(); i++) {
            hiddenTests.add(i);
        }
        
        // Parse command line arguments
        if (args.length == 0) {
            // Run all tests with full comparison
            System.out.println("Running ALL tests...\n");
            runWithFullComparison(allTests, getAllIndices(allTests.size()));
        } else if (args[0].equalsIgnoreCase("list")) {
            // List all tests
            listAllTests(allTests, sharedTests, hiddenTests);
        } else if (args[0].equalsIgnoreCase("share") || args[0].equalsIgnoreCase("shared")) {
            // Run only shared tests
            System.out.println("Running SHARED tests (for students)...\n");
            runWithFullComparison(allTests, sharedTests);
        } else if (args[0].equalsIgnoreCase("hidden") || args[0].equalsIgnoreCase("grading")) {
            // Run only hidden tests
            System.out.println("Running HIDDEN tests (for grading)...\n");
            runWithFullComparison(allTests, hiddenTests);
        } else if (args[0].equalsIgnoreCase("preanalysis") || args[0].equalsIgnoreCase("pre")) {
            // Run with pre-analysis comparison
            System.out.println("Running with PRE-ANALYSIS comparison...\n");
            runWithPreAnalysis(allTests);
        } else {
            // Parse test indices
            List<Integer> testIndices = parseTestIndices(args, allTests.size());
            if (testIndices.isEmpty()) {
                System.out.println("No valid test indices provided. Use 'java ManualTest list' to see available tests.");
                return;
            }
            runWithFullComparison(allTests, testIndices);
        }
    }
    
    /**
     * Run tests with full comparison tables (time comparison + algorithm comparison)
     */
    private static void runWithFullComparison(List<TestCase> allTests, List<Integer> testIndices) {
        // Get the test cases to run
        List<TestCase> testsToRun = new ArrayList<>();
        for (int idx : testIndices) {
            testsToRun.add(allTests.get(idx));
        }

        // Run the tests with ManualTestRunner (detailed time comparison)
        List<ManualTestRunner.TestExecutionResult> results = ManualTestRunner.runTests(allTests, testIndices);

        // Display detailed time comparison
        ManualTestRunner.printDetailedResultsTable(results);

        // Add PreAnalysis comparison using YOUR implementation
        System.out.println("\n" + "=".repeat(120));
        System.out.println("Running PreAnalysis comparison (using YOUR StudentPreAnalysis)...");
        System.out.println("=".repeat(120));

        PreAnalysis preAnalysis = new StudentPreAnalysis();
        List<PreAnalysisComparison.ComparisonResult> preAnalysisResults =
                PreAnalysisComparison.runComparison(preAnalysis, testsToRun);

        // Print detailed algorithm comparison
        PreAnalysisComparison.printDetailedAlgorithmComparison(preAnalysisResults);

        System.out.println("\n✓ Testing complete!");
    }

    /**
     * Run with pre-analysis comparison
     * This tests YOUR PreAnalysis implementation (StudentPreAnalysis)
     */
    private static void runWithPreAnalysis(List<TestCase> allTests) {
        System.out.println("Running pre-analysis comparison on all test cases...\n");
        System.out.println("NOTE: This uses YOUR StudentPreAnalysis implementation.");
        System.out.println("      Make sure to implement the chooseAlgorithm() method!\n");

        // Use StudentPreAnalysis - this is YOUR implementation
        PreAnalysis preAnalysis = new StudentPreAnalysis();

        List<PreAnalysisComparison.ComparisonResult> preAnalysisResults =
                PreAnalysisComparison.runComparison(preAnalysis, allTests);

        // Print pre-analysis comparison table
        PreAnalysisComparison.printComparisonTable(preAnalysisResults, preAnalysis);

        // Print detailed algorithm comparison
        PreAnalysisComparison.printDetailedAlgorithmComparison(preAnalysisResults);

        System.out.println("\n✓ Pre-analysis testing complete!");
    }
    
    /**
     * List all available tests
     */
    private static void listAllTests(List<TestCase> allTests, List<Integer> sharedTests, List<Integer> hiddenTests) {
        System.out.println("AVAILABLE TEST CASES:");
        System.out.println("=".repeat(100));
        System.out.println();
        
        System.out.println("📚 SHARED TESTS (for students):");
        System.out.println("-".repeat(100));
        for (int i : sharedTests) {
            TestCase test = allTests.get(i);
            System.out.printf("[%2d] %-30s | Text length: %4d | Pattern length: %2d\n",
                i, test.getName(), test.getText().length(), test.getPattern().length());
            System.out.printf("     Text: \"%s\"\n", truncate(test.getText(), 70));
            System.out.printf("     Pattern: \"%s\"\n", test.getPattern());
            System.out.printf("     Expected: %s\n", test.getExpectedResult().isEmpty() ? "(no match)" : test.getExpectedResult());
            System.out.println();
        }
        
        System.out.println("\n🔒 HIDDEN TESTS (for instructor grading only):");
        System.out.println("-".repeat(100));
        for (int i : hiddenTests) {
            TestCase test = allTests.get(i);
            System.out.printf("[%2d] %-30s | Text length: %4d | Pattern length: %2d\n",
                i, test.getName(), test.getText().length(), test.getPattern().length());
            System.out.printf("     Text: \"%s\"\n", truncate(test.getText(), 70));
            System.out.printf("     Pattern: \"%s\"\n", test.getPattern());
            System.out.printf("     Expected: %s\n", test.getExpectedResult().isEmpty() ? "(no match)" : test.getExpectedResult());
            System.out.println();
        }
        
        System.out.println("=".repeat(100));
        System.out.println("\nUSAGE EXAMPLES:");
        System.out.println("  java ManualTest              - Run all tests with full comparison tables");
        System.out.println("  java ManualTest 0 1 2        - Run specific tests (0, 1, and 2)");
        System.out.println("  java ManualTest 0-5          - Run tests 0 through 5");
        System.out.println("  java ManualTest share        - Run only shared tests (for students)");
        System.out.println("  java ManualTest hidden       - Run only hidden tests (for grading)");
        System.out.println("  java ManualTest preanalysis  - Run with pre-analysis comparison");
        System.out.println("  java ManualTest list         - Show this list");
    }
    
    /**
     * Parse test indices from command line arguments
     */
    private static List<Integer> parseTestIndices(String[] args, int maxIndex) {
        List<Integer> indices = new ArrayList<>();
        
        for (String arg : args) {
            try {
                // Check for range (e.g., "0-5")
                if (arg.contains("-")) {
                    String[] parts = arg.split("-");
                    if (parts.length == 2) {
                        int start = Integer.parseInt(parts[0].trim());
                        int end = Integer.parseInt(parts[1].trim());
                        for (int i = start; i <= end; i++) {
                            if (i >= 0 && i < maxIndex && !indices.contains(i)) {
                                indices.add(i);
                            }
                        }
                    }
                } else {
                    // Single index
                    int index = Integer.parseInt(arg.trim());
                    if (index >= 0 && index < maxIndex && !indices.contains(index)) {
                        indices.add(index);
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("⚠ Warning: Invalid test index '" + arg + "' (ignored)");
            }
        }
        
        Collections.sort(indices);
        return indices;
    }
    
    /**
     * Get all indices from 0 to size-1
     */
    private static List<Integer> getAllIndices(int size) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            indices.add(i);
        }
        return indices;
    }
    
    /**
     * Truncate string for display
     */
    private static String truncate(String s, int maxLength) {
        if (s.length() <= maxLength) {
            return s;
        }
        return s.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Print header
     */
    private static void printHeader() {
        System.out.println("╔" + "═".repeat(98) + "╗");
        System.out.println("║" + center("MANUAL TEST RUNNER - String Matching Algorithms", 98) + "║");
        System.out.println("╚" + "═".repeat(98) + "╝");
        System.out.println();
    }
    
    private static String center(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(padding) + text + " ".repeat(width - text.length() - padding);
    }
}
