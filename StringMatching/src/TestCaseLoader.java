import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Loads test cases from JSON files in the testcases directory
 */
public class TestCaseLoader {
    
    private static final String TESTCASES_DIR = "testcases";
    private static final String SHARED_DIR = "shared";
    private static final String HIDDEN_DIR = "hidden";
    
    /**
     * Load all test cases from both shared and hidden directories
     */
    public static List<TestCase> loadAllTestCases() {
        List<TestCase> allTests = new ArrayList<>();
        allTests.addAll(loadTestCasesFromDirectory(SHARED_DIR));
        allTests.addAll(loadTestCasesFromDirectory(HIDDEN_DIR));
        return allTests;
    }
    
    /**
     * Load only shared test cases (for students)
     */
    public static List<TestCase> loadSharedTestCases() {
        return loadTestCasesFromDirectory(SHARED_DIR);
    }
    
    /**
     * Load only hidden test cases (for grading)
     */
    public static List<TestCase> loadHiddenTestCases() {
        return loadTestCasesFromDirectory(HIDDEN_DIR);
    }
    
    /**
     * Load test cases from a specific directory
     */
    public static List<TestCase> loadTestCasesFromDirectory(String subDir) {
        List<TestCase> testCases = new ArrayList<>();

        // Try multiple possible paths (for running from different directories)
        Path dirPath = Paths.get(TESTCASES_DIR, subDir);
        if (!Files.exists(dirPath)) {
            dirPath = Paths.get("..", TESTCASES_DIR, subDir);
        }

        if (!Files.exists(dirPath)) {
            System.err.println("Warning: Directory does not exist: " + TESTCASES_DIR + "/" + subDir);
            System.err.println("         Tried: " + Paths.get(TESTCASES_DIR, subDir).toAbsolutePath());
            System.err.println("         And: " + Paths.get("..", TESTCASES_DIR, subDir).toAbsolutePath());
            return testCases;
        }
        
        try (Stream<Path> paths = Files.walk(dirPath, 1)) {
            List<Path> jsonFiles = paths
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".json"))
                .sorted()
                .collect(Collectors.toList());
            
            for (Path jsonFile : jsonFiles) {
                try {
                    TestCase testCase = loadTestCaseFromFile(jsonFile);
                    testCases.add(testCase);
                } catch (Exception e) {
                    System.err.println("Error loading test case from " + jsonFile + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading directory " + dirPath + ": " + e.getMessage());
        }
        
        return testCases;
    }
    
    /**
     * Load a single test case from a JSON file
     */
    public static TestCase loadTestCaseFromFile(Path filePath) throws IOException {
        String content = Files.readString(filePath);
        return parseJsonTestCase(content, filePath.getFileName().toString());
    }
    
    /**
     * Parse JSON content into a TestCase object
     * Simple JSON parser without external dependencies
     */
    private static TestCase parseJsonTestCase(String json, String filename) {
        // Remove whitespace and braces
        json = json.trim();
        if (json.startsWith("{")) {
            json = json.substring(1);
        }
        if (json.endsWith("}")) {
            json = json.substring(0, json.length() - 1);
        }
        
        String name = null;
        String text = null;
        String pattern = null;
        String expected = null;
        
        // Split by lines and parse each field
        String[] lines = json.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.equals(",")) {
                continue;
            }
            
            // Remove trailing comma
            if (line.endsWith(",")) {
                line = line.substring(0, line.length() - 1);
            }
            
            if (line.contains(":")) {
                String[] parts = line.split(":", 2);
                String key = parts[0].trim().replace("\"", "");
                String value = parts[1].trim();
                
                // Remove quotes from value
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                
                switch (key) {
                    case "name":
                        name = value;
                        break;
                    case "text":
                        text = value;
                        break;
                    case "pattern":
                        pattern = value;
                        break;
                    case "expected":
                        expected = value;
                        break;
                }
            }
        }
        
        if (name == null || text == null || pattern == null || expected == null) {
            throw new IllegalArgumentException("Invalid JSON format in " + filename + 
                ". Required fields: name, text, pattern, expected");
        }
        
        return new TestCase(name, text, pattern, expected);
    }
    
    /**
     * Get the number of shared test cases
     */
    public static int getSharedTestCount() {
        return loadSharedTestCases().size();
    }
    
    /**
     * Get the number of hidden test cases
     */
    public static int getHiddenTestCount() {
        return loadHiddenTestCases().size();
    }
    
    /**
     * Print information about available test cases
     */
    public static void printTestCaseInfo() {
        int sharedCount = getSharedTestCount();
        int hiddenCount = getHiddenTestCount();
        int totalCount = sharedCount + hiddenCount;
        
        System.out.println("Test Cases Available:");
        System.out.println("  📚 Shared tests (for students): " + sharedCount);
        System.out.println("  🔒 Hidden tests (for grading): " + hiddenCount);
        System.out.println("  📊 Total tests: " + totalCount);
        System.out.println();
    }
}

