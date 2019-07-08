package zk.util;

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

    public static <T, V, R> Function<T, R> join(Function<T, V> before, Function<V, R> after) {
        return before.andThen(after);
    }

    public static <T, V> Predicate<T> joinP(Function<T, V> function, Predicate<V> predicate) {
        return t -> predicate.test(function.apply(t));
    }

    public static <T, R> Consumer<T> joinC(Function<T, R> function, Consumer<R> consumer) {
        return t -> consumer.accept(function.apply(t));
    }

    public static <T, R> Function<T, R> joinCS(Consumer<T> consumer, Supplier<R> supplier) {
        return t -> {
            consumer.accept(t);
            return supplier.get();
        };
    }

    public static <T> Consumer<T> joinCC(Consumer<T> before, Consumer<T> after) {
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

    public static <T, R> Function<T, R> forSupplier(Supplier<R> supplier) {
        Objects.requireNonNull(supplier);
        return t -> supplier.get();
    }

    public static <T> ToIntFunction<T> forISupplier(IntSupplier supplier) {
        Objects.requireNonNull(supplier);
        return t -> supplier.getAsInt();
    }

    public static <T> ToLongFunction<T> forLSupplier(LongSupplier supplier) {
        Objects.requireNonNull(supplier);
        return t -> supplier.getAsLong();
    }

    public static <T> ToDoubleFunction<T> forDSupplier(DoubleSupplier supplier) {
        Objects.requireNonNull(supplier);
        return t -> supplier.getAsDouble();
    }

    public static <T, R> Function<T, R> forMap(Map<T, R> map) {
        return forMapDefault(map, null);
    }

    public static <T, R> Function<T, Optional<R>> forMapOpt(Map<T, R> map) {
        Objects.requireNonNull(map);
        return key -> Optional.ofNullable(map.get(key));
    }

    public static <T, R> Function<T, R> forMapDefault(Map<T, R> map, R defaultValue) {
        Objects.requireNonNull(map);
        return key -> map.getOrDefault(key, defaultValue);
    }

    //==========================================================

    public static <T, U, R> Function<U, R> bindFirst(BiFunction<T, U, R> func, T first) {
        Objects.requireNonNull(func);
        return u -> func.apply(first, u);
    }

    public static <T, U, R> Function<T, R> bindSecond(BiFunction<T, U, R> func, U second) {
        Objects.requireNonNull(func);
        return t -> func.apply(t, second);
    }

    //==========================================================

    public static <T> Supplier<T> bindFirst(UnaryOperator<T> func, T first) {
        Objects.requireNonNull(func);
        return () -> func.apply(first);
    }

    public static IntSupplier iuBindFirst(IntUnaryOperator func, int first) {
        Objects.requireNonNull(func);
        return () -> func.applyAsInt(first);
    }

    public static LongSupplier luBindFirst(LongUnaryOperator func, long first) {
        Objects.requireNonNull(func);
        return () -> func.applyAsLong(first);
    }

    public static DoubleSupplier duBindFirst(DoubleUnaryOperator func, double first) {
        Objects.requireNonNull(func);
        return () -> func.applyAsDouble(first);
    }

    //==========================================================

    public static IntUnaryOperator ibBindFirst(IntBinaryOperator func, int first) {
        Objects.requireNonNull(func);
        return i -> func.applyAsInt(first, i);
    }

    public static LongUnaryOperator lbBindFirst(LongBinaryOperator func, long first) {
        Objects.requireNonNull(func);
        return l -> func.applyAsLong(first, l);
    }

    public static DoubleUnaryOperator dbBindFirst(DoubleBinaryOperator func, double first) {
        Objects.requireNonNull(func);
        return d -> func.applyAsDouble(first, d);
    }

    //==========================================================

    public static IntUnaryOperator ibBindSecond(IntBinaryOperator func, int second) {
        Objects.requireNonNull(func);
        return i -> func.applyAsInt(i, second);
    }

    public static LongUnaryOperator lbBindSecond(LongBinaryOperator func, long second) {
        Objects.requireNonNull(func);
        return i -> func.applyAsLong(i, second);
    }

    public static DoubleUnaryOperator dbBindSecond(DoubleBinaryOperator func, double second) {
        Objects.requireNonNull(func);
        return d -> func.applyAsDouble(d, second);
    }

    //==========================================================

    public static <T, U> Consumer<U> cBindFirst(BiConsumer<T, U> func, T first) {
        Objects.requireNonNull(func);
        return u -> func.accept(first, u);
    }

    public static <T, U> Consumer<T> cBindSecond(BiConsumer<T, U> func, U second) {
        Objects.requireNonNull(func);
        return t -> func.accept(t, second);
    }

    //==========================================================

    public static <T, U> Predicate<U> pBindFirst(BiPredicate<T, U> func, T first) {
        Objects.requireNonNull(func);
        return u -> func.test(first, u);
    }

    public static <T, U> Predicate<T> pBindSecond(BiPredicate<T, U> func, U second) {
        Objects.requireNonNull(func);
        return t -> func.test(t, second);
    }

    //==========================================================

    public static <T, R> Supplier<R> bindFirst(Function<T, R> func, T first) {
        Objects.requireNonNull(func);
        return () -> func.apply(first);
    }

    //==========================================================

    public static <T> Stream<T> copyStream(Collection<T> col) {
        Objects.requireNonNull(col);
        return new ArrayList<>(col).stream();
    }

    public static <T> Stream<T> copyStream(T[] array) {
        Objects.requireNonNull(array);
        return Arrays.stream(Arrays.copyOf(array, array.length));
    }

    public static IntStream copyStream(int[] array) {
        Objects.requireNonNull(array);
        return Arrays.stream(Arrays.copyOf(array, array.length));
    }

    public static LongStream copyStream(long[] array) {
        Objects.requireNonNull(array);
        return Arrays.stream(Arrays.copyOf(array, array.length));
    }

    public static DoubleStream copyStream(double[] array) {
        Objects.requireNonNull(array);
        return Arrays.stream(Arrays.copyOf(array, array.length));
    }
}
