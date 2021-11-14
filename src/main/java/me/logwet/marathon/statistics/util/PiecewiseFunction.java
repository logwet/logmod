package me.logwet.marathon.statistics.util;

import org.apache.commons.lang3.Range;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

public class PiecewiseFunction<T, R> {
    private final Map<Range<T>, Function<T, R>> functions;

    public PiecewiseFunction(@NotNull Map<Range<T>, Function<T, R>> functions) {
        this.functions = functions;
    }

    public PiecewiseFunction() {
        this(
                new TreeMap<>(
                        (o1, o2) -> o1.getComparator().compare(o1.getMinimum(), o2.getMinimum())));
    }

    public void addPiece(@NotNull Range<T> range, @NotNull Function<T, R> function) {
        functions.put(range, function);
    }

    @Nullable
    public R apply(@NotNull T x) {
        for (Map.Entry<Range<T>, Function<T, R>> entry : functions.entrySet()) {
            if (entry.getKey().contains(x)) {
                return entry.getValue().apply(x);
            }
        }

        return null;
    }

    public int size() {
        return functions.size();
    }
}
