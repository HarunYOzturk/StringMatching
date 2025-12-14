import java.lang.reflect.Constructor;
import java.util.Set;

public class SolutionFactory {
     static void x(String[] args) {
        try {
            Set<Class<? extends Solution>> sb = Solution.SUBCLASSES;
            for (Class<? extends Solution> S : sb) {
                // Using getDeclaredConstructor() with no parameters
                Constructor<? extends Solution> constructor = S.getDeclaredConstructor();
                // Create a new instance of the class
                Solution solution = constructor.newInstance();
                System.out.println("Created instance of: " + solution.getClass().getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}