package kotlin.collections;

import java.util.Arrays;
import java.util.List;
class ArraysUtilJVM {
    public static <T> List<T> asList(T[] tArr) {
        return Arrays.asList(tArr);
    }
}