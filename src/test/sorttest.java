package test;

import java.util.Comparator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class sorttest {

    private final Integer testArray[] = { 9, 7, 3 };

    public static final Comparator<Integer> ByIntValue = new ByIntValue();

    private static class ByIntValue implements Comparator<Integer> {
        public int compare(Integer v, Integer w) {
            if (v < w)
                return -1;
            else if (v > w)
                return 1;
            else
                return 0;
        }
    }

    @Test
    @DisplayName("Determine if array is sorted, should be false")
    public void sort() {
        // assertEquals(false, Sort.isSorted(ByIntValue, testArray));
    }

}