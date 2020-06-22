package util;

import java.util.HashMap;

public class BinaryHashFunction<T> {
    final HashMap<T, Boolean> hashMap = new HashMap<>();

    public void set(final T object, final boolean value) {
        hashMap.put(object, value);
    }

    public boolean get(final T object) {
        final Boolean value = hashMap.get(object);

        return value == null ? false : value;
    }

    public void clear() {
        hashMap.clear();
    }
}
