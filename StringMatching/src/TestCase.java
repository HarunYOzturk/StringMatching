/**
 * Represents a test case for string matching algorithms.
 * Test cases are now loaded from JSON files in the testcases/ directory.
 * See TestCaseLoader.java for loading test cases from files.
 */
public class TestCase {
    private String name;
    private String text;
    private String pattern;
    private String expectedResult;
    
    public TestCase(String name, String text, String pattern, String expectedResult) {
        this.name = name;
        this.text = text;
        this.pattern = pattern;
        this.expectedResult = expectedResult;
    }
    
    public String getName() {
        return name;
    }
    
    public String getText() {
        return text;
    }
    
    public String getPattern() {
        return pattern;
    }
    
    public String getExpectedResult() {
        return expectedResult;
    }
    

    
    @Override
    public String toString() {
        return String.format("TestCase{name='%s', textLen=%d, patternLen=%d}", 
                           name, text.length(), pattern.length());
    }
}

