import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

public abstract class Solution {
    static final Set<Class<? extends Solution>> SUBCLASSES = new HashSet<>();

    public Solution(){
        // Constructor
    }

    /**
     * Main method to solve the string matching problem
     * @param text The text to search in
     * @param pattern The pattern to search for
     * @return Comma-separated string of indices where pattern is found, or empty string if not found
     */
    public abstract String Solve(String text, String pattern);

    /**
     * Helper method to convert list of indices to comma-separated string
     * @param indices List of indices where pattern was found
     * @return Comma-separated string of indices
     */
    protected String indicesToString(List<Integer> indices) {
        if (indices.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indices.size(); i++) {
            sb.append(indices.get(i));
            if (i < indices.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    /**
     * Helper method to check if pattern matches text at given position
     * @param text The text to search in
     * @param pattern The pattern to search for
     * @param pos The position in text to check
     * @return true if pattern matches at position pos
     */
    protected boolean matchesAt(String text, String pattern, int pos) {
        if (pos + pattern.length() > text.length()) {
            return false;
        }
        for (int i = 0; i < pattern.length(); i++) {
            if (text.charAt(pos + i) != pattern.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get the name of this algorithm
     * @return The class name (algorithm name)
     */
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
