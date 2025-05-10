package org.example.advertisingagency.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class BatchLoaderUtils {

    private static final int DEFAULT_BATCH_SIZE = 1000;

    public static <T, R> List<R> loadInBatches(List<T> items, Function<List<T>, List<R>> batchLoader) {
        List<R> result = new ArrayList<>();
        for (int i = 0; i < items.size(); i += DEFAULT_BATCH_SIZE) {
            List<T> batch = items.subList(i, Math.min(i + DEFAULT_BATCH_SIZE, items.size()));
            result.addAll(batchLoader.apply(batch));
        }
        return result;
    }
}
