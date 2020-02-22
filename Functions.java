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

    public static <T, V, R> Function<T, R> join(@NonNull Function<T, V> before,
                                                @NonNull Function<V, R> after) {
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

    public static <T, V> Predicate<T> joinP(@NonNull Function<T, V> function,
                                            @NonNull Predicate<V> predicate) {
        return t -> predicate.test(function.apply(t));
    }

    public static <T, U, V> Predicate<T> joinP(@NonNull Function<T, U> func1,
                                               @NonNull Function<U, V> func2,
                                               @NonNull Predicate<V> predicate) {
        return t -> predicate.test(func2.apply(func1.apply(t)));
    }

    public static <T, U, V, W> Predicate<T> joinP(@NonNull Function<T, U> func1,
                                                  @NonNull Function<U, V> func2,
                                                  @NonNull Function<V, W> func3,
                                                  @NonNull Predicate<W> predicate) {
        return t -> predicate.test(func3.apply(func2.apply(func1.apply(t))));
    }

    public static <T, R> Consumer<T> joinC(@NonNull Function<T, R> function,
                                           @NonNull Consumer<R> consumer) {
        return t -> consumer.accept(function.apply(t));
    }

    public static <T, U, V> Consumer<T> joinC(@NonNull Function<T, U> func1,
                                              @NonNull Function<U, V> func2,
                                              @NonNull Consumer<V> consumer) {
        return t -> consumer.accept(func2.apply(func1.apply(t)));
    }

    public static <T, U, V, W> Consumer<T> joinC(@NonNull Function<T, U> func1,
                                                 @NonNull Function<U, V> func2,
                                                 @NonNull Function<V, W> func3,
                                                 @NonNull Consumer<W> consumer) {
        return t -> consumer.accept(func3.apply(func2.apply(func1.apply(t))));
    }

    @SafeVarargs
    public static <T> Consumer<T> joinCC(@NonNull Consumer<T>... consumers) {
        return Arrays.stream(consumers)
                .filter(Objects::nonNull)
                .reduce(Consumer::andThen)
                .orElseGet(Functions::empty);
    }

    public static <T> Consumer<T> joinCC(@Nullable Collection<Consumer<T>> consumers) {
        if (consumers == null || consumers.isEmpty()) {
            return empty();
        }
        return consumers.stream()
                .filter(Objects::nonNull)
                .reduce(Consumer::andThen)
                .orElseGet(Functions::empty);
    }

    @SafeVarargs
    public static <T, R> Function<T, R> joinCS(@NonNull Supplier<R> supplier, @NonNull Consumer<T>... consumers) {
        return t -> {
            joinCC(consumers).accept(t);
            return supplier.get();
        };
    }

    @SafeVarargs
    public static <T> UnaryOperator<T> c2f(@NonNull Consumer<T>... consumers) {
        return t -> {
            joinCC(consumers).accept(t);
            return t;
        };
    }

    public static <T> UnaryOperator<T> c2f(@Nullable Collection<Consumer<T>> consumers) {
        return t -> {
            joinCC(consumers).accept(t);
            return t;
        };
    }

    @SafeVarargs
    public static <T, R> Function<T, R> joinCF(@NonNull Function<T, R> func, @NonNull Consumer<T>... consumers) {
        return t -> {
            joinCC(consumers).accept(t);
            return func.apply(t);
        };
    }

    public static <T> Function<T, Boolean> p2F(Predicate<T> predicate) {
        return predicate::test;
    }

    public static <T> Predicate<T> f2P(Function<T, Boolean> func) {
        return t -> Optional.ofNullable(func.apply(t)).orElse(Boolean.FALSE);
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

    public static <T> Predicate<T> equal(T t) {
        return Predicate.isEqual(t);
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
        return v -> func.apply(first, v);
    }

    public static <T> UnaryOperator<T> bBindSecond(@NonNull BinaryOperator<T> func, T second) {
        return t -> func.apply(t, second);
    }

    public static <T> Supplier<T> uBindFirst(@NonNull UnaryOperator<T> func, T first) {
        return () -> func.apply(first);
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

    public static <T> BooleanSupplier bsBindFirst(@NonNull Predicate<T> predicate, T first) {
        return () -> predicate.test(first);
    }

    public static <T, R> Supplier<R> sBindFirst(@NonNull Function<T, R> func, T first) {
        return () -> func.apply(first);
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

    @SafeVarargs
    public static <T> Stream<T> copyStream(T... array) {
        if (array == null || array.length == 0) {
            return Stream.empty();
        }
        return Arrays.stream(Arrays.copyOf(array, array.length));
    }

    public static IntStream copyIntStream(int... array) {
        if (array == null || array.length == 0) {
            return IntStream.empty();
        }
        return Arrays.stream(Arrays.copyOf(array, array.length));
    }

    public static LongStream copyLongStream(long... array) {
        if (array == null || array.length == 0) {
            return LongStream.empty();
        }
        return Arrays.stream(Arrays.copyOf(array, array.length));
    }

    public static DoubleStream copyDoubleStream(double... array) {
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
        return t -> Arrays.stream(predicates).allMatch(pBindSecond(Predicate::test, t));
    }

    @SafeVarargs
    public static <T> Predicate<T> xor(@NonNull Predicate<? super T>... predicates) {
        return t ->
                Arrays.stream(predicates)
                        .map(bindSecond(Predicate::test, t))
                        .reduce(Boolean.TRUE, Boolean::logicalXor);
    }

    public static <T, R> Function<T, R> sIgnoreFirst(@NonNull Supplier<R> supplier) {
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

    public static UnaryOperator<Integer> plusNBoxed(int n) {
        return box(plusN(n));
    }

    public static IntUnaryOperator plusN(int n) {
        return unBoxIU(bBindFirst(Integer::sum, n));
    }

    public static UnaryOperator<Integer> negateBoxed() {
        return box(negate());
    }

    public static IntUnaryOperator negate() {
        return i -> -i;
    }

    public static UnaryOperator<Long> plusNBoxed(long n) {
        return box(plusN(n));
    }

    public static LongUnaryOperator plusN(long n) {
        return unBoxLU(bBindFirst(Long::sum, n));
    }

    public static UnaryOperator<Long> negateLBoxed() {
        return box(negateL());
    }

    public static LongUnaryOperator negateL() {
        return l -> -l;
    }

    public static UnaryOperator<Double> plusNBoxed(double n) {
        return box(plusN(n));
    }

    public static DoubleUnaryOperator plusN(double n) {
        return unBoxDU(bBindFirst(Double::sum, n));
    }

    public static UnaryOperator<Double> negateDBoxed() {
        return box(negateD());
    }

    public static DoubleUnaryOperator negateD() {
        return d -> -d;
    }

    public static Predicate<Integer> greaterBoxed(int i) {
        return t -> t != null && t > i;
    }

    public static IntPredicate greater(int i) {
        return t -> t > i;
    }

    public static Predicate<Integer> greaterOrEqualBoxed(int i) {
        return t -> t != null && t >= i;
    }

    public static IntPredicate greaterOrEqual(int i) {
        return t -> t >= i;
    }

    public static Predicate<Integer> lessBoxed(int i) {
        return t -> t != null && t < i;
    }

    public static IntPredicate less(int i) {
        return t -> t < i;
    }

    public static Predicate<Integer> lessOrEqualBoxed(int i) {
        return t -> t != null && t <= i;
    }

    public static IntPredicate lessOrEqual(int i) {
        return t -> t <= i;
    }

    public static Predicate<Long> greaterBoxed(long l) {
        return t -> t != null && t > l;
    }

    public static LongPredicate greater(long l) {
        return t -> t > l;
    }

    public static Predicate<Long> greaterOrEqualBoxed(long l) {
        return t -> t != null && t >= l;
    }

    public static LongPredicate greaterOrEqual(long l) {
        return t -> t >= l;
    }

    public static Predicate<Long> lessBoxed(long l) {
        return t -> t != null && t < l;
    }

    public static LongPredicate less(long l) {
        return t -> t < l;
    }

    public static Predicate<Long> lessOrEqualBoxed(long l) {
        return t -> t != null && t <= l;
    }

    public static LongPredicate lessOrEqual(long l) {
        return t -> t <= l;
    }

    public static Predicate<Double> greaterBoxed(double d) {
        return t -> t != null && t > d;
    }

    public static DoublePredicate greater(double d) {
        return t -> t > d;
    }

    public static Predicate<Double> greaterOrEqualBoxed(double d) {
        return t -> t != null && t >= d;
    }

    public static DoublePredicate greaterOrEqual(double d) {
        return t -> t >= d;
    }

    public static Predicate<Double> lessBoxed(double d) {
        return t -> t != null && t < d;
    }

    public static DoublePredicate less(double d) {
        return t -> t < d;
    }

    public static Predicate<Double> lessOrEqualBoxed(double d) {
        return t -> t != null && t <= d;
    }

    public static DoublePredicate lessOrEqual(double d) {
        return t -> t <= d;
    }

    @SafeVarargs
    public static <T> Predicate<T> inArray(T... array) {
        return pBindFirst(ArrayUtils::contains, array);
    }

    public static IntPredicate inInts(int... array) {
        return unBoxIP(pBindFirst(ArrayUtils::contains, array));
    }

    public static LongPredicate inLongs(long... array) {
        return unBoxLP(pBindFirst(ArrayUtils::contains, array));
    }

    public static DoublePredicate inDoubles(double... array) {
        return unBoxDP(pBindFirst(ArrayUtils::contains, array));
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
        Predicate<List<T>> nonEmpty = negate(List::isEmpty);
        Function<List<T>, Integer> indexOf = bindSecond(List::indexOf, value);
        return join(Optional::ofNullable,
                bindSecond(Optional::filter, nonEmpty),
                bindSecond(Optional::map, indexOf),
                bindSecond(Optional::orElse, -1));
    }

    public static <T> Function<String, Integer> stringIndexOf(T value) {
        if (value == null) {
            return always(-1);
        }
        return bindSecond(StringUtils::indexOf, String.valueOf(value));
    }

    public static Function<int[], Integer> intsIndexOf(int value) {
        return bindSecond(ArrayUtils::indexOf, value);
    }

    public static Function<long[], Integer> longIndexOf(long value) {
        return bindSecond(ArrayUtils::indexOf, value);
    }

    public static Function<double[], Integer> doubleIndexOf(double value) {
        return bindSecond(ArrayUtils::indexOf, value);
    }

    public static <T> Predicate<T[]> arrayContains(T value) {
        return pBindSecond(ArrayUtils::contains, value);
    }

    public static Predicate<int[]> intsContains(int value) {
        return pBindSecond(ArrayUtils::contains, value);
    }

    public static Predicate<long[]> longsContains(long value) {
        return pBindSecond(ArrayUtils::contains, value);
    }

    public static Predicate<double[]> doublesContains(double value) {
        return pBindSecond(ArrayUtils::contains, value);
    }

    public static <T> Predicate<Collection<T>> collectionContains(T value) {
        return and(Objects::nonNull, negate(Collection::isEmpty), pBindSecond(Collection::contains, value));
    }

    public static <T> Predicate<String> stringContains(T value) {
        if (value == null) {
            return alwaysFalse();
        }
        return pBindSecond(StringUtils::contains, String.valueOf(value));
    }

    public static Predicate<Collection<String>> containsIgnoreCase(String value) {
        if (value == null) {
            return alwaysFalse();
        }
        return and(Objects::nonNull, negate(Collection::isEmpty),
                joinP(Collection::stream, pBindSecond(Stream::anyMatch, pBindFirst(String::equalsIgnoreCase, value))));
    }

    public static Function<String, String[]> splitBy(@NonNull String separator) {
        return bindSecond(String::split, separator);
    }

    public static Function<String, Stream<String>> splitToStream(@NonNull String separator) {
        return join(splitBy(separator), Arrays::stream);
    }

    public static Function<Object[], String> arrayJoinBy(@NonNull String separator) {
        return bindSecond(StringUtils::join, separator);
    }

    public static <T> Function<Iterable<T>, String> iterableJoinBy(@NonNull String separator) {
        return bindSecond(StringUtils::join, separator);
    }

    public static <T, V> Function<Map<T, V>, String> mapJoinBy(@NonNull String keyValueSeparator,
                                                               @NonNull String entrySeparator) {
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

    public static <T> Stream<T> reverse(@NonNull Stream<T> stream) {
        Deque<T> deque = new ArrayDeque<>();
        stream.forEachOrdered(deque::addFirst);
        return deque.stream();
    }

    public static IntStream reverse(@NonNull IntStream stream) {
        Deque<Integer> deque = new ArrayDeque<>();
        stream.boxed().forEachOrdered(deque::addFirst);
        return deque.stream().mapToInt(Integer::intValue);
    }

    public static LongStream reverse(@NonNull LongStream stream) {
        Deque<Long> deque = new ArrayDeque<>();
        stream.boxed().forEachOrdered(deque::addFirst);
        return deque.stream().mapToLong(Long::longValue);
    }

    public static DoubleStream reverse(@NonNull DoubleStream stream) {
        Deque<Double> deque = new LinkedList<>();
        stream.boxed().forEachOrdered(deque::addFirst);
        return deque.stream().mapToDouble(Double::doubleValue);
    }

    public static <T> Stream<T> reverseStream(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return Stream.empty();
        }
        Deque<T> deque = new ArrayDeque<>(collection.size());
        collection.forEach(deque::addFirst);
        return deque.stream();
    }

    @SafeVarargs
    public static <T> Stream<T> reverseStream(T... array) {
        if (array == null || array.length == 0) {
            return Stream.empty();
        }
        ArrayUtils.reverse(Arrays.copyOf(array, array.length));
        return Arrays.stream(array);
    }

    public static IntStream reverseStream(int... array) {
        if (array == null || array.length == 0) {
            return IntStream.empty();
        }
        ArrayUtils.reverse(Arrays.copyOf(array, array.length));
        return Arrays.stream(array);
    }

    public static LongStream reverseStream(long... array) {
        if (array == null || array.length == 0) {
            return LongStream.empty();
        }
        ArrayUtils.reverse(Arrays.copyOf(array, array.length));
        return Arrays.stream(array);
    }

    public static DoubleStream reverseStream(double... array) {
        if (array == null || array.length == 0) {
            return DoubleStream.empty();
        }
        ArrayUtils.reverse(Arrays.copyOf(array, array.length));
        return Arrays.stream(array);
    }

    public static <R> IntFunction<R> unBoxI(@NonNull Function<Integer, R> func) {
        return func::apply;
    }

    public static <R> LongFunction<R> unBoxL(@NonNull Function<Long, R> func) {
        return func::apply;
    }

    public static <R> DoubleFunction<R> unBoxD(@NonNull Function<Double, R> func) {
        return func::apply;
    }

    public static <T> ToIntFunction<T> unBoxTI(@NonNull Function<T, Integer> func) {
        return t -> Optional.ofNullable(func.apply(t)).orElse(0);
    }

    public static <T> ToLongFunction<T> unBoxTL(@NonNull Function<T, Long> func) {
        return t -> Optional.ofNullable(func.apply(t)).orElse(0L);
    }

    public static <T> ToDoubleFunction<T> unBoxTD(@NonNull Function<T, Double> func) {
        return t -> Optional.ofNullable(func.apply(t)).orElse(0.0);
    }

    public static IntUnaryOperator unBoxIU(@NonNull UnaryOperator<Integer> func) {
        return i -> Optional.ofNullable(func.apply(i)).orElse(0);
    }

    public static LongUnaryOperator unBoxLU(@NonNull UnaryOperator<Long> func) {
        return i -> Optional.ofNullable(func.apply(i)).orElse(0L);
    }

    public static DoubleUnaryOperator unBoxDU(@NonNull UnaryOperator<Double> func) {
        return i -> Optional.ofNullable(func.apply(i)).orElse(0.0);
    }

    public static IntPredicate unBoxIP(Predicate<Integer> predicate) {
        return predicate::test;
    }

    public static LongPredicate unBoxLP(Predicate<Long> predicate) {
        return predicate::test;
    }

    public static DoublePredicate unBoxDP(Predicate<Double> predicate) {
        return predicate::test;
    }

    public static <R> Function<Integer, R> box(@NonNull IntFunction<R> func) {
        return i -> func.apply(i == null ? 0 : i);
    }

    public static <R> Function<Long, R> box(@NonNull LongFunction<R> func) {
        return l -> func.apply(l == null ? 0L : l);
    }

    public static <R> Function<Double, R> box(@NonNull DoubleFunction<R> func) {
        return d -> func.apply(d == null ? 0.0 : d);
    }

    public static <T> Function<T, Integer> box(@NonNull ToIntFunction<T> func) {
        return func::applyAsInt;
    }

    public static <T> Function<T, Long> box(@NonNull ToLongFunction<T> func) {
        return func::applyAsLong;
    }

    public static <T> Function<T, Double> box(@NonNull ToDoubleFunction<T> func) {
        return func::applyAsDouble;
    }

    public static UnaryOperator<Integer> box(@NonNull IntUnaryOperator func) {
        return i -> func.applyAsInt(i == null ? 0 : i);
    }

    public static UnaryOperator<Long> box(@NonNull LongUnaryOperator func) {
        return l -> func.applyAsLong(l == null ? 0L : l);
    }

    public static UnaryOperator<Double> box(@NonNull DoubleUnaryOperator func) {
        return d -> func.applyAsDouble(d == null ? 0.0 : d);
    }

    public static Predicate<Integer> box(@NonNull IntPredicate predicate) {
        return and(Objects::nonNull, predicate::test);
    }

    public static Predicate<Long> box(@NonNull LongPredicate predicate) {
        return and(Objects::nonNull, predicate::test);
    }

    public static Predicate<Double> box(@NonNull DoublePredicate predicate) {
        return and(Objects::nonNull, predicate::test);
    }
}
