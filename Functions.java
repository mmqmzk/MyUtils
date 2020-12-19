package zk.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.*;
import java.util.regex.Pattern;
import java.util.stream.*;

/**
 * Created by 周锟 on 2016/1/29 9:53.
 */
public enum Functions {
    ;

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

    public static <T, U, V, W, X> Predicate<T> joinP(@NonNull Function<T, U> func1,
            @NonNull Function<U, V> func2,
            @NonNull Function<V, W> func3,
            @NonNull Function<W, X> func4,
            @NonNull Predicate<X> predicate) {
        return t -> predicate.test(func4.apply(func3.apply(func2.apply(func1.apply(t)))));
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

    public static <T, R> Function<T, R> joinCS(@NonNull Supplier<R> supplier,
            @NonNull Collection<Consumer<T>> consumers) {
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

    public static <T, R> Function<T, R> joinCF(@NonNull Function<T, R> func,
            @NonNull Collection<Consumer<T>> consumers) {
        return t -> {
            joinCC(consumers).accept(t);
            return func.apply(t);
        };
    }

    public static <T> Function<T, Boolean> p2F(@NonNull Predicate<T> predicate) {
        return predicate::test;
    }

    public static <T> Predicate<T> f2P(@NonNull Function<T, Boolean> func) {
        return t -> Optional.ofNullable(func.apply(t)).orElse(Boolean.FALSE);
    }

    public static <T> Consumer<T> ifThen(@NonNull Predicate<T> predicate, @NonNull Consumer<T> thenFunc) {
        return t -> {
            if (predicate.test(t)) {
                thenFunc.accept(t);
            }
        };
    }

    public static <T> Consumer<T> ifThenElse(@NonNull Predicate<T> predicate,
            @NonNull Consumer<T> thenFunc, @NonNull Consumer<T> elseFunc) {
        return t -> {
            if (predicate.test(t)) {
                thenFunc.accept(t);
            } else {
                elseFunc.accept(t);
            }
        };
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

    public static <T> UnaryOperator<T> identity() {
        return t -> t;
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
        return Stream.concat(stream, Arrays.stream(values));
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
        return Stream.concat(stream, Arrays.stream(values));
    }

    public static <T> Predicate<T> negate(@NonNull Predicate<T> predicate) {
        return predicate.negate();
    }

    @SafeVarargs
    public static <T> Predicate<T> or(@NonNull Predicate<? super T>... predicates) {
        if (predicates == null || predicates.length == 0) {
            return alwaysFalse();
        }
        return t ->
                Arrays.stream(predicates)
                        .filter(Objects::nonNull)
                        .anyMatch(pBindSecond(Predicate::test, t));
    }

    public static <T> Predicate<T> or(@Nullable Collection<Predicate<? super T>> predicates) {
        if (predicates == null || predicates.isEmpty()) {
            return alwaysFalse();
        }
        return t ->
                predicates.stream()
                        .filter(Objects::nonNull)
                        .anyMatch(pBindSecond(Predicate::test, t));
    }

    @SafeVarargs
    public static <T> Predicate<T> and(@NonNull Predicate<? super T>... predicates) {
        if (predicates == null || predicates.length == 0) {
            return alwaysTrue();
        }
        return t ->
                Arrays.stream(predicates)
                        .filter(Objects::nonNull)
                        .allMatch(pBindSecond(Predicate::test, t));
    }

    public static <T> Predicate<T> and(@Nullable Collection<Predicate<? super T>> predicates) {
        if (predicates == null || predicates.isEmpty()) {
            return alwaysTrue();
        }
        return t ->
                predicates.stream()
                        .filter(Objects::nonNull)
                        .allMatch(pBindSecond(Predicate::test, t));
    }

    @SafeVarargs
    public static <T> Predicate<T> xor(@NonNull Predicate<? super T>... predicates) {
        if (predicates == null || predicates.length == 0) {
            return alwaysFalse();
        }
        return t ->
                Arrays.stream(predicates)
                        .filter(Objects::nonNull)
                        .map(bindSecond(Predicate::test, t))
                        .reduce(Boolean.FALSE, Boolean::logicalXor);
    }

    public static <T> Predicate<T> xor(@Nullable Collection<Predicate<? super T>> predicates) {
        if (predicates == null || predicates.isEmpty()) {
            return alwaysFalse();
        }
        return t ->
                predicates.stream()
                        .filter(Objects::nonNull)
                        .map(bindSecond(Predicate::test, t))
                        .reduce(Boolean.FALSE, Boolean::logicalXor);
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

    public static <T> BinaryOperator<T> bIgnoreFirst(@NonNull UnaryOperator<T> func) {
        return (t, v) -> func.apply(v);
    }

    public static <T> BinaryOperator<T> bIgnoreSecond(@NonNull UnaryOperator<T> func) {
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
        return i -> i == null ? null : i + n;
    }

    public static IntUnaryOperator plusN(int n) {
        return i -> i + n;
    }

    public static UnaryOperator<Integer> negateBoxed() {
        return i -> i == null ? null : -i;
    }

    public static IntUnaryOperator negate() {
        return i -> -i;
    }

    public static UnaryOperator<Long> plusNBoxed(long n) {
        return l -> l == null ? null : l + n;
    }

    public static LongUnaryOperator plusN(long n) {
        return l -> l + n;
    }

    public static UnaryOperator<Long> negateLBoxed() {
        return l -> l == null ? null : -l;
    }

    public static LongUnaryOperator negateL() {
        return l -> -l;
    }

    public static UnaryOperator<Double> plusNBoxed(double n) {
        return d -> d == null ? null : d + n;
    }

    public static DoubleUnaryOperator plusN(double n) {
        return d -> d + n;
    }

    public static UnaryOperator<Double> negateDBoxed() {
        return d -> d == null ? null : -d;
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

    public static <T> Predicate<T> inCollection(@Nullable Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return alwaysFalse();
        }
        return pBindFirst(Collection::contains, collection);
    }

    public static <T> Predicate<T> inString(@Nullable String str) {
        if (str == null) {
            return alwaysFalse();
        }
        return and(Objects::nonNull, joinP(Object::toString, pBindFirst(String::contains, str)));
    }

    public static <T> Predicate<T> inStringIgnoreCase(@Nullable String str) {
        if (str == null) {
            return alwaysFalse();
        }
        return and(Objects::nonNull, joinP(Object::toString, pBindFirst(StringUtils::containsIgnoreCase, str)));
    }

    public static <T> ToIntFunction<T[]> indexOf(@Nullable T value) {
        return array -> ArrayUtils.indexOf(array, value);
    }

    public static <T> ToIntFunction<List<T>> listIndexOf(@Nullable T value) {
        return list -> list == null || list.isEmpty() ? -1 : list.indexOf(value);
    }

    public static Function<String, Integer> stringIndexOf(Object value) {
        if (value == null) {
            return always(-1);
        }
        return bindSecond(StringUtils::indexOf, String.valueOf(value));
    }

    public static ToIntFunction<int[]> indexOf(int value) {
        return array -> ArrayUtils.indexOf(array, value);
    }

    public static ToIntFunction<long[]> indexOf(long value) {
        return array -> ArrayUtils.indexOf(array, value);
    }

    public static ToIntFunction<double[]> indexOf(double value) {
        return array -> ArrayUtils.indexOf(array, value);
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

    public static <T> Predicate<Collection<T>> collectionContains(@Nullable T value) {
        return and(Objects::nonNull, negate(Collection::isEmpty), pBindSecond(Collection::contains, value));
    }

    public static Predicate<String> stringContains(@Nullable Object value) {
        if (value == null) {
            return alwaysFalse();
        }
        return pBindSecond(StringUtils::contains, String.valueOf(value));
    }

    public static Predicate<Collection<String>> containsIgnoreCase(@Nullable String value) {
        if (value == null) {
            return alwaysFalse();
        }
        return and(Objects::nonNull, negate(Collection::isEmpty),
                joinP(Collection::stream, pBindSecond(Stream::anyMatch, pBindFirst(String::equalsIgnoreCase, value))));
    }

    public static BinaryOperator<String> stringJoinBy(@Nullable String separator) {
        String sep = separator == null ? StringUtils.EMPTY : separator;
        return (t, s) -> t + sep + s;
    }

    public static Function<String, String[]> splitBy(@Nullable String separator) {
        if (separator == null) {
            separator = StringUtils.EMPTY;
        }
        Pattern pattern = Pattern.compile(separator);
        return pattern::split;
    }

    public static BiFunction<String, Integer, String[]> splitWithLimit(@Nullable String separator) {
        if (separator == null) {
            separator = StringUtils.EMPTY;
        }
        Pattern pattern = Pattern.compile(separator);
        return pattern::split;
    }

    public static Function<String, Stream<String>> splitToStream(@Nullable String separator) {
        if (separator == null) {
            separator = StringUtils.EMPTY;
        }
        Pattern pattern = Pattern.compile(separator);
        return join(pattern::split, Arrays::stream);
    }

    public static Function<CharSequence, List<String>> splitToList(@Nullable String separator) {
        if (separator == null) {
            separator = StringUtils.EMPTY;
        }
        Pattern pattern = Pattern.compile(separator);
        return input -> {
            if (input == null) {
                return Collections.emptyList();
            }
            return pattern.splitAsStream(input).collect(Collectors.toList());
        };
    }

    public static Function<String, Map<String, String>> splitToMap(
            @Nullable String keyValueSeparator,
            @Nullable String entrySeparator) {
        if (keyValueSeparator == null) {
            keyValueSeparator = StringUtils.EMPTY;
        }
        Pattern kvPattern = Pattern.compile(keyValueSeparator);
        if (entrySeparator == null) {
            entrySeparator = StringUtils.EMPTY;
        }
        Pattern entryPattern = Pattern.compile(entrySeparator);
        Function<Stream<String>, String> keyFunc = bindSecond(Functions::first, StringUtils.EMPTY);
        Function<Stream<String>, String> valueFunc = bindSecond(Functions::second, StringUtils.EMPTY);
        return input -> {
            if (input == null || input.isEmpty()) {
                return Collections.emptyMap();
            }
            return entryPattern.splitAsStream(input)
                    .map(kvPattern::splitAsStream)
                    .collect(Collectors.toMap(keyFunc, valueFunc, bSecondArg()));
        };
    }

    public static Function<Object[], String> arrayJoinBy(@Nullable String separator) {
        return bindSecond(StringUtils::join, separator);
    }

    public static <T> Function<Iterable<T>, String> iterableJoinBy(@Nullable String separator) {
        return bindSecond(StringUtils::join, separator);
    }

    public static <T, V> Function<Map<T, V>, String> mapJoinBy(
            @NonNull String keyValueSeparator,
            @NonNull String entrySeparator) {
        return map -> {
            if (map == null || map.isEmpty()) {
                return StringUtils.EMPTY;
            }
            return map.entrySet().stream()
                    .map(entry -> entry.getKey() + keyValueSeparator + entry.getValue())
                    .collect(Collectors.joining(entrySeparator));
        };
    }

    public static UnaryOperator<String> append(String suffix) {
        return bBindSecond(String::concat, suffix);
    }

    public static UnaryOperator<String> prepend(String prefix) {
        return bBindFirst(String::concat, prefix);
    }

    public static <T> Stream<T> reverse(@Nullable Stream<T> stream) {
        if (stream == null) {
            return Stream.empty();
        }
        Deque<T> deque = new ArrayDeque<>();
        stream.sequential().forEachOrdered(deque::addFirst);
        return deque.stream().sequential();
    }

    public static IntStream reverse(@Nullable IntStream stream) {
        return reverse(stream.boxed()).mapToInt(Integer::intValue);
    }

    public static LongStream reverse(@Nullable LongStream stream) {
        return reverse(stream.boxed()).mapToLong(Long::longValue);
    }

    public static DoubleStream reverse(@Nullable DoubleStream stream) {
        return reverse(stream.boxed()).mapToDouble(Double::doubleValue);
    }

    public static <T> Stream<T> reverseStream(@Nullable Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return Stream.empty();
        }
        Deque<T> deque = new ArrayDeque<>(collection.size());
        collection.stream().sequential().forEach(deque::addFirst);
        return deque.stream().sequential();
    }

    @SafeVarargs
    public static <T> Stream<T> reverseStream(T... array) {
        if (array == null || array.length == 0) {
            return Stream.empty();
        }
        ArrayUtils.reverse(Arrays.copyOf(array, array.length));
        return Arrays.stream(array).sequential();
    }

    public static IntStream reverseStream(int... array) {
        if (array == null || array.length == 0) {
            return IntStream.empty();
        }
        ArrayUtils.reverse(Arrays.copyOf(array, array.length));
        return Arrays.stream(array).sequential();
    }

    public static LongStream reverseStream(long... array) {
        if (array == null || array.length == 0) {
            return LongStream.empty();
        }
        ArrayUtils.reverse(Arrays.copyOf(array, array.length));
        return Arrays.stream(array).sequential();
    }

    public static DoubleStream reverseStream(double... array) {
        if (array == null || array.length == 0) {
            return DoubleStream.empty();
        }
        ArrayUtils.reverse(Arrays.copyOf(array, array.length));
        return Arrays.stream(array).sequential();
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

    public static <T, R> Optional<R> foldSequential(Stream<T> stream, BiFunction<R, T, R> accumulator) {
        return Optional.ofNullable(foldSequential(stream, null, accumulator));
    }

    public static <T, R> R foldSequential(Stream<T> stream, R identity, BiFunction<R, T, R> accumulator) {
        if (stream == null) {
            return identity;
        }
        return fold(stream.sequential(), identity, accumulator, bFirstArg());
    }

    public static <T, R> Optional<R> fold(Stream<T> stream,
            BiFunction<R, T, R> accumulator, BinaryOperator<R> selector) {
        return Optional.ofNullable(fold(stream, null, accumulator, selector));
    }

    public static <T, R> R fold(Stream<T> stream, R identity,
            BiFunction<R, T, R> accumulator, BinaryOperator<R> selector) {
        if (stream == null) {
            return identity;
        }
        Holder<R> holder = new Holder<>(identity);
        BiFunction<Holder<R>, T, Holder<R>> wrapAccumulator = (h, t) ->
                new Holder<>(accumulator.apply(h.getValue(), t));
        BinaryOperator<Holder<R>> combiner = (h1, h2) ->
                h1.setValue(selector.apply(h1.getValue(), h2.getValue()));
        return stream.reduce(holder, wrapAccumulator, combiner).getValue();
    }

    public static <T, V> BiFunction<T, V, T> firstArg() {
        return (t, v) -> t;
    }

    public static <T> BinaryOperator<T> bFirstArg() {
        return (t, v) -> t;
    }

    public static <T, V> BiFunction<T, V, V> secondArg() {
        return (t, v) -> v;
    }

    public static <T> BinaryOperator<T> bSecondArg() {
        return (v, t) -> t;
    }

    public static <T> Optional<T> first(Stream<T> stream) {
        if (stream == null) {
            return Optional.empty();
        }
        return stream.findFirst();
    }

    public static <T> Optional<T> second(Stream<T> stream) {
        if (stream == null) {
            return Optional.empty();
        }
        return stream.skip(1).findFirst();
    }

    public static <T> Optional<T> third(Stream<T> stream) {
        if (stream == null) {
            return Optional.empty();
        }
        return stream.skip(2).findFirst();
    }

    public static <T> Optional<T> nth(Stream<T> stream, int n) {
        if (stream == null) {
            return Optional.empty();
        }
        return stream.skip(n - 1).findFirst();
    }

    public static <T> T first(Stream<T> stream, T defaultValue) {
        return first(stream).orElse(defaultValue);
    }

    public static <T> T second(Stream<T> stream, T defaultValue) {
        return second(stream).orElse(defaultValue);
    }

    public static <T> T third(Stream<T> stream, T defaultValue) {
        return third(stream).orElse(defaultValue);
    }

    public static <T> T nth(Stream<T> stream, int n, T defaultValue) {
        return nth(stream, n).orElse(defaultValue);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static final class Holder<T> implements
                                         UnaryOperator<T>, Consumer<T>,
                                         Supplier<T>, Predicate<Object> {
        private T value;

        public Holder<T> setValue(T value) {
            this.value = value;
            return this;
        }

        @Override
        public T apply(T t) {
            T oldValue = value;
            value = t;
            return oldValue;
        }

        @Override
        public void accept(T t) {
            setValue(t);
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public boolean test(Object o) {
            return value != null;
        }
    }
}
