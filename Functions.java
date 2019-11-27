package zk.util;

import com.google.common.collect.Streams;
import lombok.NonNull;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

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

    public static <T> Consumer<T> empty() {
        return t -> {
        };
    }

    public static <T, V> BiConsumer<T, V> bEmpty() {
        return (t, v) -> {
        };
    }

    public static <T, V, R> Function<T, R> join(@NonNull Function<T, V> before, @NonNull Function<V, R> after) {
        return before.andThen(after);
    }

    public static <T, U, V, R> Function<T, R> join(@NonNull Function<T, U> func1,
                                                   @NonNull Function<U, V> func2,
                                                   @NonNull Function<V, R> func3) {
        return func1.andThen(func2).andThen(func3);
    }

    public static <T, U, V, W, R> Function<T, R> join(@NonNull Function<T, U> func1,
                                                      @NonNull Function<U, V> func2,
                                                      @NonNull Function<V, W> func3,
                                                      @NonNull Function<W, R> func4) {
        return func1.andThen(func2).andThen(func3).andThen(func4);
    }

    public static <T, V> Predicate<T> joinP(@NonNull Function<T, V> function, @NonNull Predicate<V> predicate) {
        return t -> predicate.test(function.apply(t));
    }

    public static <T, U, V> Predicate<T> joinP(@NonNull Function<T, U> func1,
                                               @NonNull Function<U, V> func2, @NonNull Predicate<V> predicate) {
        return t -> predicate.test(func2.apply(func1.apply(t)));
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

    public static <T> Predicate<T> alwaysTrue() {
        return always(true);
    }

    public static <T> Predicate<T> alwaysFalse() {
        return always(false);
    }

    public static <T, R> Function<T, R> always(R value) {
        return t -> value;
    }

    public static <T> Predicate<T> notEqual(T t) {
        return Predicate.<T>isEqual(t).negate();
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

    public static <T, V, R> Function<V, R> bindFirst(@NonNull BiFunction<T, V, R> func, T first) {
        return v -> func.apply(first, v);
    }

    public static <T, V, R> Function<T, R> bindSecond(@NonNull BiFunction<T, V, R> func, V second) {
        return t -> func.apply(t, second);
    }

    public static <T> UnaryOperator<T> bBindFirst(@NonNull BinaryOperator<T> func, T first) {
        return (UnaryOperator<T>) bindFirst(func, first);
    }

    public static <T> UnaryOperator<T> bBindSecond(@NonNull BinaryOperator<T> func, T second) {
        return (UnaryOperator<T>) bindSecond(func, second);
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

    public static <T, V> Consumer<V> cBindFirst(@NonNull BiConsumer<T, V> func, T first) {
        return v -> func.accept(first, v);
    }

    public static <T, V> Consumer<T> cBindSecond(@NonNull BiConsumer<T, V> func, V second) {
        return t -> func.accept(t, second);
    }

    public static <T, V> Predicate<V> pBindFirst(@NonNull BiPredicate<T, V> func, T first) {
        return v -> func.test(first, v);
    }

    public static <T, V> Predicate<T> pBindSecond(@NonNull BiPredicate<T, V> func, V second) {
        return t -> func.test(t, second);
    }

    public static <T, R> Supplier<R> sBindFirst(@NonNull Function<T, R> func, T first) {
        return () -> func.apply(first);
    }

    public static <T> BooleanSupplier bsBindFirst(@NonNull Predicate<T> predicate, T first) {
        return () -> predicate.test(first);
    }

    public static <T> Runnable rBindFirst(@NonNull Consumer<T> consumer, T first) {
        return () -> consumer.accept(first);
    }

    public static <T> Stream<T> copyStream(@Nullable Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return Stream.empty();
        }
        return new ArrayList<>(collection).stream();
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

    @SafeVarargs
    public static <T> Stream<T> joinStream(Collection<? extends T>... collections) {
        return Arrays.stream(collections).filter(Objects::nonNull).flatMap(Collection::stream);
    }

    @SafeVarargs
    public static <T, V extends T> Stream<T> joinStream(V[]... arrays) {
        return Arrays.stream(arrays).filter(Objects::nonNull).flatMap(Arrays::stream);
    }

    @SafeVarargs
    public static <T, V extends T, S extends T> Stream<T> addStream(@Nullable Collection<V> collection, S... values) {
        Stream<V> stream;
        if (collection == null || collection.isEmpty()) {
            if (values.length == 0) {
                return Stream.empty();
            }
            stream = Stream.empty();
        } else {
            stream = collection.stream();
        }
        return Streams.concat(stream, Arrays.stream(values));
    }

    @SafeVarargs
    public static <T, V extends T, S extends T> Stream<T> addStream(@Nullable V[] array, S... values) {
        Stream<V> stream;
        if (array == null || array.length == 0) {
            if (values.length == 0) {
                return Stream.empty();
            }
            stream = Stream.empty();
        } else {
            stream = Arrays.stream(array);
        }
        return Streams.concat(stream, Arrays.stream(values));
    }

    public static <T> Predicate<T> negate(@NonNull Predicate<T> predicate) {
        return predicate.negate();
    }

    @SafeVarargs
    public static <T> Predicate<T> or(@NonNull Predicate<? super T>... predicates) {
        return t -> Arrays.stream(predicates).anyMatch(pBindSecond(Predicate::test, t));
    }

    @SafeVarargs
    public static <T> Predicate<T> and(@NonNull Predicate<? super T>... predicates) {
        return t -> Arrays.stream(predicates).map(Predicate::negate).noneMatch(pBindSecond(Predicate::test, t));
    }

    @SafeVarargs
    public static <T> Predicate<T> xor(@NonNull Predicate<? super T>... predicates) {
        return t -> Arrays.stream(predicates).map(p -> p.test(t)).reduce(Boolean.FALSE, Boolean::logicalOr);
    }

    public static <T, R> Function<T, R> ignoreFirst(@NonNull Supplier<R> supplier) {
        return t -> supplier.get();
    }

    public static <T, V, R> BiFunction<T, V, R> ignoreFirst(@NonNull Function<V, R> func) {
        return (t, v) -> func.apply(v);
    }

    public static <T, V, R> BiFunction<T, V, R> ignoreSecond(@NonNull Function<T, R> func) {
        return (t, v) -> func.apply(t);
    }

    public static <T> Predicate<T> pIgnoreFirst(@NonNull BooleanSupplier supplier) {
        return t -> supplier.getAsBoolean();
    }

    public static <T, V> BiPredicate<T, V> pIgnoreFirst(@NonNull Predicate<V> func) {
        return (t, v) -> func.test(v);
    }

    public static <T, V> BiPredicate<T, V> pIgnoreSecond(@NonNull Predicate<T> func) {
        return (t, v) -> func.test(t);
    }

    public static <T> Consumer<T> cIgnoreFirst(@NonNull Runnable runnable) {
        return t -> runnable.run();
    }

    public static <T, V> BiConsumer<T, V> cIgnoreFirst(@NonNull Consumer<V> consumer) {
        return (t, v) -> consumer.accept(v);
    }

    public static <T, V> BiConsumer<T, V> cIgnoreSecond(@NonNull Consumer<T> consumer) {
        return (t, v) -> consumer.accept(t);
    }

    public static IntUnaryOperator plusN(int n) {
        return ibBindFirst(Integer::sum, n);
    }

    public static LongUnaryOperator plusN(long n) {
        return lbBindFirst(Long::sum, n);
    }

    public static DoubleUnaryOperator plusN(double n) {
        return dbBindFirst(Double::sum, n);
    }

    public static <T> Predicate<T> inArray(T[] array) {
        return pBindFirst(ArrayUtils::contains, array);
    }

    public static <T> Predicate<T> inCollection(@NonNull Collection<T> collection) {
        return pBindFirst(Collection::contains, collection);
    }

    public static <T> Predicate<T> inString(@NonNull String string) {
        return and(Objects::nonNull, joinP(Object::toString, pBindFirst(String::contains, string)));
    }

    public static <T> Function<T[], Integer> arrayIndexOf(T value) {
        return bindSecond(ArrayUtils::indexOf, value);
    }

    public static <T> Function<List<T>, Integer> listIndexOf(T value) {
        Predicate<List<T>> negate = negate(List::isEmpty);
        Function<List<T>, Integer> indexOf = bindSecond(List::indexOf, value);
        return join(Optional::ofNullable,
                bindSecond(Optional::filter, negate),
                bindSecond(Optional::map, indexOf),
                bindSecond(Optional::orElse, -1));
    }

    public static <T> Function<String, Integer> stringIndexOf(T value) {
        return bindSecond(StringUtils::indexOf, String.valueOf(value));
    }

    public static <T> Function<int[], Integer> intsIndexOf(int value) {
        return bindSecond(ArrayUtils::indexOf, value);
    }

    public static <T> Predicate<T[]> arrayContains(T value) {
        return pBindSecond(ArrayUtils::contains, value);
    }

    public static Predicate<int[]> intsContains(int value) {
        return pBindSecond(ArrayUtils::contains, value);
    }

    public static <T> Predicate<Collection<T>> collectionContains(T value) {
        return and(Objects::nonNull, negate(Collection::isEmpty), pBindSecond(Collection::contains, value));
    }

    public static <T> Predicate<String> stringContains(T value) {
        return pBindSecond(StringUtils::contains, String.valueOf(value));
    }

    public static Predicate<Collection<String>> containsIgnoreCase(String value) {
        return and(Objects::nonNull, negate(Collection::isEmpty),
                joinP(Collection::stream, pBindSecond(Stream::anyMatch, pBindFirst(String::equalsIgnoreCase, value))));
    }

    public static Function<String, String[]> splitBy(String separator) {
        return bindSecond(String::split, separator);
    }

    public static Function<String, Stream<String>> splitToStream(String separator) {
        return join(splitBy(separator), Arrays::stream);
    }

    public static Function<Object[], String> arrayJoinBy(String separator) {
        return bindSecond(StringUtils::join, separator);
    }

    public static <T> Function<Iterable<T>, String> iterableJoinBy(String separator) {
        return bindSecond(StringUtils::join, separator);
    }

    public static <T, V> Function<Map<T, V>, String> mapJoinBy(String keyValueSeparator, String entrySeparator) {
        return map ->
                map.entrySet().stream()
                        .map(entry ->
                                entry.getKey() + keyValueSeparator + entry.getValue())
                        .reduce(StringUtils.EMPTY, (t, c) ->
                                t.isEmpty() ? c : t + entrySeparator + c);
    }

    public static UnaryOperator<String> append(String suffix) {
        return bBindSecond(String::concat, suffix);
    }

    public static UnaryOperator<String> prepend(String prefix) {
        return bBindFirst(String::concat, prefix);
    }
}
