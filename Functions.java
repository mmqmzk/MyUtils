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
    public static <L, R, V> List<V> transform(Collection<L> lhs, Collection<R> rhs, BiFunction<L, R, V> func) {
        Objects.requireNonNull(func);
        if (lhs == null || rhs == null) {
            return Collections.emptyList();
        }
        List<V> result = new ArrayList<>(Math.min(lhs.size(), rhs.size()));
        Iterator<L> lIterator = lhs.iterator();
        Iterator<R> rIterator = rhs.iterator();
        while (lIterator.hasNext() && rIterator.hasNext()) {
            result.add(func.apply(lIterator.next(), rIterator.next()));
        }
        return result;
    }

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

    public static IntSupplier constantI(int value) {
        return () -> value;
    }

    public static LongSupplier constantL(long value) {
        return () -> value;
    }

    public static DoubleSupplier constantD(double value) {
        return () -> value;
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

    public static IntSupplier iUBindFirst(IntUnaryOperator func, int first) {
        Objects.requireNonNull(func);
        return () -> func.applyAsInt(first);
    }

    public static LongSupplier lUBindFirst(LongUnaryOperator func, long first) {
        Objects.requireNonNull(func);
        return () -> func.applyAsLong(first);
    }

    public static DoubleSupplier dUBindFirst(DoubleUnaryOperator func, double first) {
        Objects.requireNonNull(func);
        return () -> func.applyAsDouble(first);
    }

    //==========================================================

    public static IntUnaryOperator iBBindFirst(IntBinaryOperator func, int first) {
        Objects.requireNonNull(func);
        return i -> func.applyAsInt(first, i);
    }

    public static LongUnaryOperator lBBindFirst(LongBinaryOperator func, long first) {
        Objects.requireNonNull(func);
        return l -> func.applyAsLong(first, l);
    }

    public static DoubleUnaryOperator dBBindFirst(DoubleBinaryOperator func, double first) {
        Objects.requireNonNull(func);
        return d -> func.applyAsDouble(first, d);
    }

    //==========================================================

    public static IntUnaryOperator iBBindSecond(IntBinaryOperator func, int second) {
        Objects.requireNonNull(func);
        return i -> func.applyAsInt(i, second);
    }

    public static LongUnaryOperator lBBindSecond(LongBinaryOperator func, long second) {
        Objects.requireNonNull(func);
        return i -> func.applyAsLong(i, second);
    }

    public static DoubleUnaryOperator dBBindSecond(DoubleBinaryOperator func, double second) {
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

    public static <T, R> Supplier<R> sBindFirst(Function<T, R> func, T first) {
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
