package zk.util;

import java.util.*;
import java.util.function.*;

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

    public static <T, R> Function<T, R> forSupplier(Supplier<R> supplier) {
        Objects.requireNonNull(supplier);
        return t -> supplier.get();
    }

    public static <T> ToIntFunction<T> forIntSupplier(IntSupplier supplier) {
        Objects.requireNonNull(supplier);
        return t -> supplier.getAsInt();
    }

    public static <T> ToLongFunction<T> forLongSupplier(LongSupplier supplier) {
        Objects.requireNonNull(supplier);
        return t -> supplier.getAsLong();
    }

    public static <T> ToDoubleFunction<T> forDoubleSupplier(DoubleSupplier supplier) {
        Objects.requireNonNull(supplier);
        return t -> supplier.getAsDouble();
    }

    public static <T, R> Function<T, R> forMap(Map<T, R> map) {
        return forMapDefault(map, null);
    }

    public static <T, R> Function<T, Optional<R>> forMapOptional(Map<T, R> map) {
        Objects.requireNonNull(map);
        return  key -> Optional.ofNullable(map.get(key));
    }

    public static <T, R> Function<T, R> forMapDefault(Map<T, R> map, R defaultValue) {
        Objects.requireNonNull(map);
        return  key -> map.getOrDefault(key, defaultValue);
    }

    public static <R> IntFunction<R> forIntKeyMap(Map<Integer, R> map) {
        return  forIntKeyMapDefault(map, null);
    }

    public static <R> IntFunction<Optional<R>> forIntKeyMapOptional(Map<Integer, R> map) {
        Objects.requireNonNull(map);
        return  key -> Optional.ofNullable(map.get(key));
    }

    public static <R> IntFunction<R> forIntKeyMapDefault(Map<Integer, R> map, R defaultValue) {
        Objects.requireNonNull(map);
        return  key -> map.getOrDefault(key, defaultValue);
    }


    public static <R> LongFunction<R> forLongKeyMap(Map<Long, R> map) {
        return  forLongKeyMapDefault(map, null);
    }

    public static <R> LongFunction<Optional<R>> forLongKeyMapOptional(Map<Long, R> map) {
        Objects.requireNonNull(map);
        return  key -> Optional.ofNullable(map.get(key));
    }

    public static <R> LongFunction<R> forLongKeyMapDefault(Map<Long, R> map, R defaultValue) {
        Objects.requireNonNull(map);
        return  key -> map.getOrDefault(key, defaultValue);
    }


    public static <R> DoubleFunction<R> forDoubleKeyMap(Map<Double, R> map) {
        return  forDoubleKeyMapDefault(map, null);
    }

    public static <R> DoubleFunction<Optional<R>> forDoubleKeyMapOptional(Map<Double, R> map) {
        Objects.requireNonNull(map);
        return  key -> Optional.ofNullable(map.get(key));
    }

    public static <R> DoubleFunction<R> forDoubleKeyMapDefault(Map<Double, R> map, R defaultValue) {
        Objects.requireNonNull(map);
        return  key -> map.getOrDefault(key, defaultValue);
    }

    public static <T> ToIntFunction<T> forIntValueMap(Map<T, Integer> map) {
        return forIntValueMapDefault(map, 0);
    }

    public static <T> Function<T, OptionalInt> forIntValueMapOptional(Map<T, Integer> map) {
        Objects.requireNonNull(map);
        return key -> {
            Integer value = map.get(key);
            return value == null ? OptionalInt.empty() : OptionalInt.of(value);
        };
    }

    public static <T> ToIntFunction<T> forIntValueMapDefault(Map<T, Integer> map, int defaultValue) {
        Objects.requireNonNull(map);
        return key -> {
            Integer value = map.get(key);
            return value == null ? defaultValue : value;
        };
    }

    public static <T> ToLongFunction<T> forLongValueMap(Map<T, Long> map) {
        return forLongValueMapDefault(map, 0L);
    }

    public static <T> Function<T, OptionalLong> forLongValueMapOptional(Map<T, Long> map) {
        Objects.requireNonNull(map);
        return key -> {
            Long value = map.get(key);
            return value == null ? OptionalLong.empty() : OptionalLong.of(value);
        };
    }

    public static <T> ToLongFunction<T> forLongValueMapDefault(Map<T, Long> map, long defaultValue) {
        Objects.requireNonNull(map);
        return key -> {
            Long value = map.get(key);
            return value == null ? defaultValue : value;
        };
    }

    public static <T> ToDoubleFunction<T> forDoubleValueMap(Map<T, Double> map) {
        return forDoubleValueMapDefault(map, 0.0);
    }

    public static <T> Function<T, OptionalDouble> forDoubleValueMapOptional(Map<T, Double> map) {
        Objects.requireNonNull(map);
        return key -> {
            Double value = map.get(key);
            return value == null ? OptionalDouble.empty() : OptionalDouble.of(value);
        };
    }

    public static <T> ToDoubleFunction<T> forDoubleValueMapDefault(Map<T, Double> map, double defaultValue) {
        Objects.requireNonNull(map);
        return key -> {
            Double value = map.get(key);
            return value == null ? defaultValue : value;
        };
    }

    //==========================================================

    public static <T, U, R> Function<U, R> biBindFirst(BiFunction<T, U, R> func, T first) {
        Objects.requireNonNull(func);
        return u -> func.apply(first, u);
    }

    public static <T, U> ToIntFunction<U> biToIntBindFirst(ToIntBiFunction<T, U> func, T first) {
        Objects.requireNonNull(func);
        return u -> func.applyAsInt(first, u);
    }

    public static <T, U> ToLongFunction<U> biToLongBindFirst(ToLongBiFunction<T, U> func, T first) {
        Objects.requireNonNull(func);
        return u -> func.applyAsLong(first, u);
    }

    public static <T, U> ToDoubleFunction<U> biToDoubleBindFirst(ToDoubleBiFunction<T, U> func, T first) {
        Objects.requireNonNull(func);
        return u -> func.applyAsDouble(first, u);
    }


    //==========================================================

    public static <T, U, R> Function<T, R> biBindSecond(BiFunction<T, U, R> func, U second) {
        Objects.requireNonNull(func);
        return t -> func.apply(t, second);
    }

    public static <T, U> ToIntFunction<T> biToIntBindSecond(ToIntBiFunction<T, U> func, U second) {
        Objects.requireNonNull(func);
        return t -> func.applyAsInt(t, second);
    }

    public static <T, U> ToLongFunction<T> biToLongBindSecond(ToLongBiFunction<T, U> func, U second) {
        Objects.requireNonNull(func);
        return t -> func.applyAsLong(t, second);
    }

    public static <T, U> ToDoubleFunction<T> biToDoubleBindSecond(ToDoubleBiFunction<T, U> func, U second) {
        Objects.requireNonNull(func);
        return t -> func.applyAsDouble(t, second);
    }

    //==========================================================

    public static <T> Supplier<T> unaryBindFirst(UnaryOperator<T> func, T first) {
        Objects.requireNonNull(func);
        return () -> func.apply(first);
    }

    public static IntSupplier intUnaryBindFirst(IntUnaryOperator func, int first) {
        Objects.requireNonNull(func);
        return () -> func.applyAsInt(first);
    }

    public static LongSupplier longUnaryBindFirst(LongUnaryOperator func, long first) {
        Objects.requireNonNull(func);
        return () -> func.applyAsLong(first);
    }

    public static DoubleSupplier doubleUnaryBindFirst(DoubleUnaryOperator func, double first) {
        Objects.requireNonNull(func);
        return () -> func.applyAsDouble(first);
    }

    //==========================================================

    public static <T> UnaryOperator<T> binaryBindFirst(BinaryOperator<T> func, T first) {
        Objects.requireNonNull(func);
        return t -> func.apply(first, t);
    }

    public static IntUnaryOperator intBinaryBindFirst(IntBinaryOperator func, int first) {
        Objects.requireNonNull(func);
        return i -> func.applyAsInt(first, i);
    }

    public static LongUnaryOperator longBinaryBindFirst(LongBinaryOperator func, long first) {
        Objects.requireNonNull(func);
        return l -> func.applyAsLong(first, l);
    }

    public static DoubleUnaryOperator doubleBinaryBindFirst(DoubleBinaryOperator func, double first) {
        Objects.requireNonNull(func);
        return d -> func.applyAsDouble(first, d);
    }

    //==========================================================

    public static <T> UnaryOperator<T> binaryBindSecond(BinaryOperator<T> func, T second) {
        Objects.requireNonNull(func);
        return t -> func.apply(t, second);
    }

    public static IntUnaryOperator intBinaryBindSecond(IntBinaryOperator func, int second) {
        Objects.requireNonNull(func);
        return i -> func.applyAsInt(i, second);
    }

    public static LongUnaryOperator longBinaryBindSecond(LongBinaryOperator func, long second) {
        Objects.requireNonNull(func);
        return i -> func.applyAsLong(i, second);
    }

    public static DoubleUnaryOperator doubleBinaryBindSecond(DoubleBinaryOperator func, double second) {
        Objects.requireNonNull(func);
        return d -> func.applyAsDouble(d, second);
    }

    //==========================================================

    public static <T, U> Consumer<U> consumerBindFirst(BiConsumer<T, U> func, T first) {
        Objects.requireNonNull(func);
        return u -> func.accept(first, u);
    }

    public static <T> IntConsumer intConsumerBindFirst(ObjIntConsumer<T> func, T first) {
        Objects.requireNonNull(func);
        return i -> func.accept(first, i);
    }

    public static <T> LongConsumer longConsumerBindFirst(ObjLongConsumer<T> func, T first) {
        Objects.requireNonNull(func);
        return l -> func.accept(first, l);
    }

    public static <T> DoubleConsumer doubleConsumerBindFirst(ObjDoubleConsumer<T> func, T first) {
        Objects.requireNonNull(func);
        return d -> func.accept(first, d);
    }

    //==========================================================

    public static <T, U> Consumer<T> consumerBindSecond(BiConsumer<T, U> func, U second) {
        Objects.requireNonNull(func);
        return t -> func.accept(t, second);
    }

    public static <T> Consumer<T> intConsumerBindSecond(ObjIntConsumer<T> func, int second) {
        Objects.requireNonNull(func);
        return t -> func.accept(t, second);
    }

    public static <T> Consumer<T> longConsumerBindSecond(ObjLongConsumer<T> func, long second) {
        Objects.requireNonNull(func);
        return t -> func.accept(t, second);
    }

    public static <T> Consumer<T> doubleConsumerBindSecond(ObjDoubleConsumer<T> func, double second) {
        Objects.requireNonNull(func);
        return t -> func.accept(t, second);
    }

    //==========================================================

    public static <T, U> Predicate<U> biPredicateBindFirst(BiPredicate<T, U> func, T first) {
        Objects.requireNonNull(func);
        return u -> func.test(first, u);
    }

    public static <T, U> Predicate<T> biPredicateBindSecond(BiPredicate<T, U> func, U second) {
        Objects.requireNonNull(func);
        return t -> func.test(t, second);
    }

    public static <T> BooleanSupplier predicateBindFirst(Predicate<T> func, T first) {
        Objects.requireNonNull(func);
        return () -> func.test(first);
    }

    public static BooleanSupplier intPredicateBindFirst(IntPredicate func, int first) {
        Objects.requireNonNull(func);
        return () -> func.test(first);
    }

    public static BooleanSupplier longPredicateBindFirst(LongPredicate func, long first) {
        Objects.requireNonNull(func);
        return () -> func.test(first);
    }

    public static BooleanSupplier doublePredicateBindFirst(DoublePredicate func, double first) {
        Objects.requireNonNull(func);
        return () -> func.test(first);
    }

    //==========================================================

    public static <T, R> Supplier<R> bindFirst(Function<T, R> func, T first) {
        Objects.requireNonNull(func);
        return () -> func.apply(first);
    }

    public static <R> Supplier<R> intBindFirst(IntFunction<R> func, int first) {
        Objects.requireNonNull(func);
        return () -> func.apply(first);
    }

    public static <R> Supplier<R> longBindFirst(LongFunction<R> func, long first) {
        Objects.requireNonNull(func);
        return () -> func.apply(first);
    }

    public static <R> Supplier<R> doubleBindFirst(DoubleFunction<R> func, double first) {
        Objects.requireNonNull(func);
        return () -> func.apply(first);
    }

    public static <T> IntSupplier toIntBindFirst(ToIntFunction<T> func, T first) {
        Objects.requireNonNull(func);
        return () -> func.applyAsInt(first);
    }

    public static IntSupplier longToIntBindFirst(LongToIntFunction func, long first) {
        Objects.requireNonNull(func);
        return () -> func.applyAsInt(first);
    }

    public static IntSupplier doubleToIntBindFirst(DoubleToIntFunction func, double first) {
        Objects.requireNonNull(func);
        return () -> func.applyAsInt(first);
    }

    public static <T> LongSupplier toLongBindFirst(ToLongFunction<T> func, T first) {
        Objects.requireNonNull(func);
        return () -> func.applyAsLong(first);
    }

    public static LongSupplier intToLongBindFirst(IntToLongFunction func, int first) {
        Objects.requireNonNull(func);
        return () -> func.applyAsLong(first);
    }

    public static LongSupplier doubleToLongBindFirst(DoubleToLongFunction func, double first) {
        Objects.requireNonNull(func);
        return () -> func.applyAsLong(first);
    }

    public static <T> DoubleSupplier toDoubleBindFirst(ToDoubleFunction<T> func, T first) {
        Objects.requireNonNull(func);
        return () -> func.applyAsDouble(first);
    }

    public static DoubleSupplier intToDoubleBindFirst(IntToDoubleFunction func, int first) {
        Objects.requireNonNull(func);
        return () -> func.applyAsDouble(first);
    }

    public static DoubleSupplier longToDoubleBindFirst(LongToDoubleFunction func, long first) {
        Objects.requireNonNull(func);
        return () -> func.applyAsDouble(first);
    }

    //==========================================================
}
