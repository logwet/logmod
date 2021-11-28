package me.logwet.logmod.statistics.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.apache.commons.lang3.Range;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PiecewiseFunction<T, R> {
    private final Map<Range<T>, Function<T, R>> functions;

    public PiecewiseFunction(@NotNull Map<Range<T>, Function<T, R>> functions) {
        this.functions = functions;
    }

    public PiecewiseFunction() {
        this(new HashMap<>());
    }

    public void addPiece(@NotNull Range<T> range, @NotNull Function<T, R> function) {
        functions.put(range, function);
    }

    @Nullable
    public R apply(@NotNull T x) {
        R r = null;

        for (Map.Entry<Range<T>, Function<T, R>> entry : functions.entrySet()) {
            if (entry.getKey().contains(x)) {
                r = entry.getValue().apply(x);

                if (r instanceof Double) {
                    if (Double.isFinite((Double) r)) {
                        break;
                    }
                } else if (r instanceof Float) {
                    if (Float.isFinite((Float) r)) {
                        break;
                    }
                } else {
                    break;
                }
            }
        }

        return r;
    }

    public int size() {
        return functions.size();
    }
}
