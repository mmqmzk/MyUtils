package zk.util;

import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.*;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Created by 周锟 on 2016/1/29 9:53.
 */
public class Functions {

    public static <T, V, R> Function<T, R> join(@NonNull Function<T, V> before, @NonNull Function<V, R> after) {
        return before.andThen(after);
    }

    public static <T, V> Predicate<T> joinP(@NonNull Function<T, V> function, @NonNull Predicate<V> predicate) {
        return t -> predicate.test(function.apply(t));
    }

    public static <T, R> Consumer<T> joinC(@NonNull Function<T, R> function, @NonNull Consumer<R> consumer) {
        return t -> consumer.accept(function.apply(t));
    }

    public static <T, R> Function<T, R> joinCS(@NonNull Consumer<T> consumer, @NonNull Supplier<R> supplier) {
        return t -> {
            consumer.accept(t);
            return supplier.get();
        };
    }

    public static <T> Consumer<T> joinCC(@NonNull Consumer<T> before, @NonNull Consumer<T> after) {
        return before.andThen(after);
    }

    public static <T> Supplier<T> constant(T value) {
        return () -> value;
    }

    public static IntSupplier constantInt(int value) {
        return () -> value;
    }

    public static LongSupplier constantLong(long value) {
        return () -> value;
    }

    public static DoubleSupplier constantDouble(double value) {
        return () -> value;
    }

    public static <T> Predicate<T> always(boolean value) {
        return t -> value;
    }

    public static <T, R> Function<T, R> always(R value) {
        return t -> value;
    }

    public static <T, R> Function<T, R> forSupplier(@NonNull Supplier<R> supplier) {
        return t -> supplier.get();
    }

    public static <T> ToIntFunction<T> forISupplier(@NonNull IntSupplier supplier) {
        return t -> supplier.getAsInt();
    }

    public static <T> ToLongFunction<T> forLSupplier(@NonNull LongSupplier supplier) {
        return t -> supplier.getAsLong();
    }

    public static <T> ToDoubleFunction<T> forDSupplier(@NonNull DoubleSupplier supplier) {
        return t -> supplier.getAsDouble();
    }

    public static <T, R> Function<T, R> forMap(@NonNull Map<T, R> map) {
        return forMapDefault(map, null);
    }

    public static <T, R> Function<T, Optional<R>> forMapOpt(@NonNull Map<T, R> map) {
        return key -> Optional.ofNullable(map.get(key));
    }

    public static <T, R> Function<T, R> forMapDefault(@NonNull Map<T, R> map, R defaultValue) {
        return key -> map.getOrDefault(key, defaultValue);
    }

    public static <T, U, R> Function<U, R> bindFirst(@NonNull BiFunction<T, U, R> func, T first) {
        return u -> func.apply(first, u);
    }

    public static <T, U, R> Function<T, R> bindSecond(@NonNull BiFunction<T, U, R> func, U second) {
        return t -> func.apply(t, second);
    }

    public static <T> UnaryOperator<T> bBindFirst(@NonNull BinaryOperator<T> func, T first) {
        return t -> func.apply(first, t);
    }

    public static <T> UnaryOperator<T> bBindSecond(@NonNull BinaryOperator<T> func, T second) {
        return t -> func.apply(t, second);
    }

    public static <T> Supplier<T> bindFirst(@NonNull UnaryOperator<T> func, T first) {
        return () -> func.apply(first);
    }

    public static IntSupplier iuBindFirst(@NonNull IntUnaryOperator func, int first) {
        return () -> func.applyAsInt(first);
    }

    public static LongSupplier luBindFirst(@NonNull LongUnaryOperator func, long first) {
        return () -> func.applyAsLong(first);
    }

    public static DoubleSupplier duBindFirst(@NonNull DoubleUnaryOperator func, double first) {
        return () -> func.applyAsDouble(first);
    }

    public static IntUnaryOperator ibBindFirst(@NonNull IntBinaryOperator func, int first) {
        return i -> func.applyAsInt(first, i);
    }

    public static LongUnaryOperator lbBindFirst(@NonNull LongBinaryOperator func, long first) {
        return l -> func.applyAsLong(first, l);
    }

    public static DoubleUnaryOperator dbBindFirst(@NonNull DoubleBinaryOperator func, double first) {
        return d -> func.applyAsDouble(first, d);
    }

    public static IntUnaryOperator ibBindSecond(@NonNull IntBinaryOperator func, int second) {
        return i -> func.applyAsInt(i, second);
    }

    public static LongUnaryOperator lbBindSecond(@NonNull LongBinaryOperator func, long second) {
        return i -> func.applyAsLong(i, second);
    }

    public static DoubleUnaryOperator dbBindSecond(@NonNull DoubleBinaryOperator func, double second) {
        return d -> func.applyAsDouble(d, second);
    }

    public static <T, U> Consumer<U> cBindFirst(@NonNull BiConsumer<T, U> func, T first) {
        return u -> func.accept(first, u);
    }

    public static <T, U> Consumer<T> cBindSecond(@NonNull BiConsumer<T, U> func, U second) {
        return t -> func.accept(t, second);
    }

    public static <T, U> Predicate<U> pBindFirst(@NonNull BiPredicate<T, U> func, T first) {
        return u -> func.test(first, u);
    }

    public static <T, U> Predicate<T> pBindSecond(@NonNull BiPredicate<T, U> func, U second) {
        return t -> func.test(t, second);
    }

    public static <T> BooleanSupplier pBindFirst(@NonNull Predicate<T> func, T first) {
        return () -> func.test(first);
    }

    public static <T, R> Supplier<R> bindFirst(@NonNull Function<T, R> func, T first) {
        return () -> func.apply(first);
    }

    public static <T> Supplier<T> uBindFirst(@NonNull UnaryOperator<T> func, T first) {
        return () -> func.apply(first);
    }

    public static <T> Stream<T> copyStream(@Nullable Collection<T> col) {
        if (col == null || col.isEmpty()) {
            return Stream.empty();
        }
        return new ArrayList<>(col).stream();
    }

    public static <T> Stream<T> copyStream(@Nullable T[] array) {
        if (array == null || array.length == 0) {
            return Stream.empty();
        }
        return Arrays.stream(Arrays.copyOf(array, array.length));
    }

    public static IntStream copyStream(@Nullable int[] array) {
        if (array == null || array.length == 0) {
            return IntStream.empty();
        }
        return Arrays.stream(Arrays.copyOf(array, array.length));
    }

    public static LongStream copyStream(@Nullable long[] array) {
        if (array == null || array.length == 0) {
            return LongStream.empty();
        }
        return Arrays.stream(Arrays.copyOf(array, array.length));
    }

    public static DoubleStream copyStream(@Nullable double[] array) {
        if (array == null || array.length == 0) {
            return DoubleStream.empty();
        }
        return Arrays.stream(Arrays.copyOf(array, array.length));
    }

    public static <T> boolean noneMatch(@Nullable Stream<T> stream, @NonNull Predicate<T> predicate) {
        return stream == null || stream.allMatch(predicate.negate());
    }
}
