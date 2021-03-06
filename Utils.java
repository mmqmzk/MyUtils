package zk.util;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import com.sh.commons.tuple.Tuple;
import com.sh.commons.tuple.TwoTuple;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author 周锟
 */
@Slf4j
public enum Utils {
    ;

    public static String getStackTrace() {
        return getStackTrace(1, 10);
    }

    public static String getStackTrace(int start, int stop) {
        if (start > stop) {
            throw new IllegalArgumentException("start > stop");
        }
        StringBuilder builder = new StringBuilder((stop - start + 1) * 50);
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int length = 2;
        if (stackTrace.length < start + length) {
            return builder.toString();
        }
        appendElement(builder, stackTrace[start + 1]);
        for (int i = start + length; i < stop + length + 1 && i < stackTrace.length; i++) {
            builder.append("<=");
            appendElement(builder, stackTrace[i]);
        }
        return builder.toString();
    }

    private static void appendElement(StringBuilder builder, StackTraceElement element) {
        String className = element.getClassName();
        String methodName = element.getMethodName();
        int index = className.lastIndexOf('.');
        builder.append(className.substring(index + 1))
                .append('.')
                .append(methodName)
                .append(':')
                .append(element.getLineNumber());
    }

    @SafeVarargs
    public static <T> T firstNonNull(T first, T second, T... rest) {
        if (first != null) {
            return first;
        } else if (second != null) {
            return second;
        } else {
            for (T t : rest) {
                if (t != null) {
                    return t;
                }
            }
            throw new NullPointerException();
        }
    }

    public static int firstNonZero(int first, int second, int... rest) {
        if (first != 0) {
            return first;
        } else if (second != 0) {
            return second;
        } else {
            for (int i : rest) {
                if (i != 0) {
                    return i;
                }
            }
            return 0;
        }
    }

    public static long firstNonZero(long first, long second, long... rest) {
        if (first != 0L) {
            return first;
        } else if (second != 0L) {
            return second;
        } else {
            for (long i : rest) {
                if (i != 0L) {
                    return i;
                }
            }
            return 0L;
        }
    }

    public static int nextInt(int i) {
        return ThreadLocalRandom.current().nextInt(i);
    }

    /**
     * 结果包含最小最大数
     *
     * @param min
     * @param max
     * @return
     */
    public static int random(int min, int max) {
        return nextInt(max - min + 1) + min;
    }

    public static boolean isLuck(int rate) {
        return isLuck(rate, 100);
    }

    public static Predicate<Integer> isLuckP(int base) {
        return rate -> isLuck(rate, base);
    }

    public static boolean isLuck(int rate, int base) {
        return nextInt(base) < rate;
    }

    public static OptionalInt randomChoose(@Nullable int[] array) {
        int length;
        if (array == null || (length = array.length) <= 0) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(get(array, nextInt(length)));
    }

    public static OptionalLong randomChoose(@Nullable long[] array) {
        int length;
        if (array == null || (length = array.length) <= 0) {
            return OptionalLong.empty();
        }
        return OptionalLong.of(get(array, nextInt(length)));
    }

    public static OptionalDouble randomChoose(@Nullable double[] array) {
        int length;
        if (array == null || (length = array.length) <= 0) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(get(array, nextInt(length)));
    }

    public static <T> Optional<T> randomChoose(@Nullable T[] array) {
        return randomChoose(array, null);
    }

    public static <T> Optional<T> randomChoose(@Nullable T[] array, @Nullable ToIntFunction<T> weigher) {
        if (array == null || array.length == 0) {
            return Optional.empty();
        }
        int total = calculateTotalWeight(array, weigher);
        return randomChoose(Arrays.stream(array), total, weigher);
    }

    public static <T> Optional<T> randomChoose(@Nullable Collection<T> collection) {
        return randomChoose(collection, null);
    }

    public static <T> Optional<T> randomChoose(@Nullable Collection<T> collection, @Nullable ToIntFunction<T> weigher) {
        if (collection == null) {
            return Optional.empty();
        }
        int total = calculateTotalWeight(collection, weigher);
        return randomChoose(collection.stream(), total, weigher);
    }

    private static <T> Optional<T> randomChoose(Stream<T> stream, int total, ToIntFunction<T> weigher) {
        if (stream == null || total <= 0) {
            return Optional.empty();
        }
        int random = nextInt(total);
        if (weigher == null) {
            return stream.skip(random).findFirst();
        }
        TwoTuple<Integer, T> tuple = Tuple.tuple(random, null);
        BiFunction<TwoTuple<Integer, T>, T, TwoTuple<Integer, T>> accumulator = (t, obj) ->
                t.getFirst() < 0 ? t : Tuple.tuple(t.getFirst() - getWeight(weigher, obj), obj);
        tuple = Functions.foldSequential(stream, tuple, accumulator);
        return Optional.ofNullable(tuple.getSecond());
    }

    private static <T> int calculateTotalWeight(T[] array, ToIntFunction<T> weigher) {
        if (array == null) {
            return 0;
        }
        if (weigher == null) {
            return array.length;
        }
        return calculateTotalWeight(Arrays.stream(array), weigher);
    }

    private static <T> int calculateTotalWeight(Collection<T> collection, ToIntFunction<T> weigher) {
        if (collection == null) {
            return 0;
        }
        if (weigher == null) {
            return collection.size();
        }
        return calculateTotalWeight(collection.stream(), weigher);
    }

    private static <T> int calculateTotalWeight(Stream<T> stream, ToIntFunction<T> weigher) {
        if (stream == null) {
            return 0;
        }
        if (weigher == null) {
            return Math.toIntExact(stream.count());
        }
        return stream.mapToInt(weigher).sum();
    }

    public static int randomIndex(@Nullable int[] array) {
        int length;
        if (array == null || (length = array.length) == 0) {
            return -1;
        }
        int total = Arrays.stream(array).sum();
        if (total <= 0) {
            return -1;
        }
        int random = nextInt(total);
        for (int i = 0; i < length; i++) {
            random -= array[i];
            if (random < 0) {
                return i;
            }
        }
        return -1;
    }

    public static int randomIndex(@Nullable long[] array) {
        int length;
        if (array == null || (length = array.length) == 0) {
            return -1;
        }
        long total = Arrays.stream(array).sum();
        if (total <= 0) {
            return -1;
        }
        long random = ThreadLocalRandom.current().nextLong(total);
        for (int i = 0; i < length; i++) {
            random -= array[i];
            if (random < 0) {
                return i;
            }
        }
        return -1;
    }

    public static <T> int randomIndex(@Nullable T[] array, @NonNull ToIntFunction<T> weigher) {
        int length;
        if (array == null || (length = array.length) == 0) {
            return -1;
        }
        int total = calculateTotalWeight(array, weigher);
        if (total <= 0) {
            return -1;
        }
        int random = nextInt(total);
        for (int i = 0; i < length; i++) {
            random -= getWeight(weigher, array[i]);
            if (random < 0) {
                return i;
            }
        }
        return -1;
    }

    public static int randomIndex(@Nullable Integer[] array) {
        return randomIndex(array, Integer::intValue);
    }

    public static <T> int randomIndex(@Nullable List<T> list, @NonNull ToIntFunction<T> weigher) {
        int size;
        if (list == null || (size = list.size()) == 0) {
            return -1;
        }
        int total = calculateTotalWeight(list, weigher);
        if (total <= 0) {
            return -1;
        }
        int random = nextInt(total);
        for (int i = 0; i < size; i++) {
            random -= getWeight(weigher, list.get(i));
            if (random < 0) {
                return i;
            }
        }
        return -1;
    }

    public static int randomIndex(@Nullable List<Integer> list) {
        return randomIndex(list, Integer::intValue);
    }

    public static <T> List<T> randomChooseN(@Nullable T[] array, int n) {
        return randomChooseN(array, n, null);
    }

    public static <T> List<T> randomChooseN(@Nullable T[] array, int n, @Nullable ToIntFunction<T> weigher) {
        if (array == null || array.length == 0 || n <= 0) {
            return Collections.emptyList();
        }
        int total = calculateTotalWeight(array, weigher);
        return randomChooseN(Arrays.stream(array), n, total, weigher);
    }

    public static <T> List<T> randomChooseN(@Nullable Collection<T> collection, int n) {
        return randomChooseN(collection, n, null);
    }

    public static <T> List<T> randomChooseN(@Nullable Collection<T> collection, int n,
                                            @Nullable ToIntFunction<T> weigher) {
        if (collection == null || collection.size() == 0 || n <= 0) {
            return Collections.emptyList();
        }
        int total = calculateTotalWeight(collection, weigher);
        return randomChooseN(collection.stream(), n, total, weigher);
    }

    private static <T> List<T> randomChooseN(Stream<T> stream, int n, int total, ToIntFunction<T> weigher) {
        if (stream == null || total <= 0 || n <= 0) {
            return Collections.emptyList();
        }
        Counter count = new Counter(n);
        Counter totalWeight = new Counter(total);
        return stream.filter(obj -> {
            if (count.gt0() && totalWeight.gt0()) {
                int weight = getWeight(weigher, obj);
                int random = nextInt(totalWeight.getAndDec(weight));
                if (random < weight * count.get()) {
                    count.decAndGet();
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    private static <T> int getWeight(ToIntFunction<T> weigher, T obj) {
        if (obj == null) {
            return 0;
        }
        return weigher == null ? 1 : weigher.applyAsInt(obj);
    }

    public static <T> Optional<T> randomRemove(@Nullable List<T> list) {
        if (list == null || list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(list.remove(nextInt(list.size())));
    }

    public static <T> Optional<T> randomRemove(@Nullable Collection<T> collection) {
        return randomRemove(collection, null);
    }

    public static <T> Optional<T> randomRemove(@Nullable Collection<T> collection, @Nullable ToIntFunction<T> weigher) {
        if (collection == null || collection.isEmpty()) {
            return Optional.empty();
        }
        List<T> list = randomRemoveN(collection, 1, weigher);
        return Optional.ofNullable(get(list, 0));
    }

    public static <T> List<T> randomRemoveN(@Nullable Collection<T> collection, int n) {
        return randomRemoveN(collection, n, null);
    }

    public static <T> List<T> randomRemoveN(@Nullable Collection<T> collection, int n,
                                            @Nullable ToIntFunction<T> weigher) {
        if (collection == null || collection.size() == 0 || n <= 0) {
            return Collections.emptyList();
        }
        List<T> result = Lists.newArrayListWithExpectedSize(n);
        if (n >= collection.size()) {
            result.addAll(collection);
            collection.clear();
            return result;
        }
        int total = calculateTotalWeight(collection, weigher);
        if (total <= 0) {
            return Collections.emptyList();
        }
        for (; n > 0 && total > 0; n--) {
            int random = nextInt(total);
            for (Iterator<T> iterator = collection.iterator(); iterator.hasNext(); ) {
                T t = iterator.next();
                int weight = getWeight(weigher, t);
                random -= weight;
                if (random < 0) {
                    result.add(t);
                    iterator.remove();
                    total -= weight;
                    break;
                }
            }
        }
        return result;
    }

    public static int get0(@Nullable int[] array) {
        return get(array, 0);
    }

    public static int get(@Nullable int[] array, int index) {
        return get(array, index, 0);
    }

    public static int get(@Nullable int[] array, int index, int defaultValue) {
        int length;
        if (array == null || (length = array.length) == 0) {
            return defaultValue;
        }
        if (index < 0) {
            index += length;
        }
        if (index < 0 || index >= length) {
            return defaultValue;
        }
        return array[index];
    }

    public static long get0(@Nullable long[] array) {
        return get(array, 0);
    }

    public static long get(@Nullable long[] array, int index) {
        return get(array, index, 0L);
    }

    public static long get(@Nullable long[] array, int index, long defaultValue) {
        int length;
        if (array == null || (length = array.length) == 0) {
            return defaultValue;
        }
        if (index < 0) {
            index += length;
        }
        if (index < 0 || index >= length) {
            return defaultValue;
        }
        return array[index];
    }

    public static double get0(@Nullable double[] array) {
        return get(array, 0);
    }

    public static double get(@Nullable double[] array, int index) {
        return get(array, index, 0.0);
    }

    public static double get(@Nullable double[] array, int index, double defaultValue) {
        int length;
        if (array == null || (length = array.length) == 0) {
            return defaultValue;
        }
        if (index < 0) {
            index += length;
        }
        if (index < 0 || index >= length) {
            return defaultValue;
        }
        return array[index];
    }

    public static <T> T get0(@Nullable T[] array) {
        return get(array, 0);
    }

    public static <T> T get(@Nullable T[] array, int index) {
        return get(array, index, null);
    }

    public static <T> T get(@Nullable T[] array, int index, T defaultValue) {
        int length;
        if (array == null || (length = array.length) == 0) {
            return defaultValue;
        }
        if (index < 0) {
            index += length;
        }
        if (index < 0 || index >= length) {
            return defaultValue;
        }
        T t = array[index];
        return t == null ? defaultValue : t;
    }

    public static <T> T get0(@Nullable Collection<T> collection) {
        return get(collection, 0);
    }

    public static <T> T get(@Nullable Collection<T> collection, int index) {
        return get(collection, index, null);
    }

    public static <T> T get(@Nullable Collection<T> collection, int index, T defaultValue) {
        int size;
        if (collection == null || (size = collection.size()) == 0) {
            return defaultValue;
        }
        if (index < 0) {
            index += size;
        }
        if (index < 0 || index >= size) {
            return defaultValue;
        }
        T t = Iterables.get(collection, index);
        return t == null ? defaultValue : t;
    }

    public static <T> T get0(Stream<T> stream) {
        return get0(stream, null);
    }

    public static <T> T get0(Stream<T> stream, T defaultValue) {
        if (stream == null) {
            return defaultValue;
        }
        return stream.findFirst().orElse(defaultValue);
    }

    public static <T> T get(Stream<T> stream, int i) {
        return get(stream, i, null);
    }

    public static <T> T get(Stream<T> stream, int i, T defaultValue) {
        if (stream == null) {
            return defaultValue;
        }
        return stream.skip(i).findFirst().orElse(defaultValue);
    }

    public static <T> Function<T[], T> arrayGetI(int i) {
        return Functions.bindSecond(Utils::get, i);
    }

    public static <T> Function<Collection<T>, T> collectionGetI(int i) {
        return Functions.bindSecond(Utils::get, i);
    }

    public static Function<int[], Integer> intsGetI(int i) {
        return Functions.bindSecond(Utils::get, i);
    }

    public static <T, R> Ordering<T> orderingFromToIntFunction(@NonNull ToIntFunction<T> function) {
        return orderingFromToIntFunction(function, true);
    }

    public static <T, R> Ordering<T> orderingFromToIntFunction(@NonNull ToIntFunction<T> function, boolean nullsFirst) {
        Ordering<T> ordering = Ordering.from((a, b) ->
                a == b ? 0 : Integer.compare(function.applyAsInt(a), function.applyAsInt(b)));
        return nullsFirst ? ordering.nullsFirst() : ordering.nullsLast();
    }

    public static <T, R> Ordering<T> orderingFromFunction(@NonNull Function<T, Comparable<R>> function) {
        return orderingFromFunction(function, Ordering.natural(), true);
    }

    public static <T, R> Ordering<T> orderingFromFunction(@NonNull Function<T, Comparable<R>> function,
                                                          boolean nullsFirst) {
        return orderingFromFunction(function, Ordering.natural(), nullsFirst);
    }

    public static <T, R> Ordering<T> orderingFromFunction(@NonNull Function<T, R> function, Comparator<R> comparator) {
        return orderingFromFunction(function, comparator, true);
    }

    public static <T, R> Ordering<T> orderingFromFunction(@NonNull Function<T, R> function,
                                                          @NonNull Comparator<R> comparator, boolean nullsFirst) {
        Ordering<T> ordering = Ordering.from((a, b) ->
                a == b ? 0 : comparator.compare(function.apply(a), function.apply(b)));
        return nullsFirst ? ordering.nullsFirst() : ordering.nullsLast();
    }

    /**
     * 返回list中第一个小于target的数的索引
     *
     * @param list
     * @param target
     * @return
     */
    public static int indexLessThan(@Nullable List<Integer> list, @Nullable Integer target) {
        return index(list, target, true, false);
    }

    /**
     * 返回list中第一个小于等于target的数的索引
     *
     * @param list
     * @param target
     * @return
     */
    public static int indexLessThanOrEqualTo(@Nullable List<Integer> list, @Nullable Integer target) {
        return index(list, target, true, true);
    }

    /**
     * 返回list中第一个小于target的数的索引
     *
     * @param list
     * @param target
     * @return
     */
    public static int indexLessThan(@Nullable int[] list, int target) {
        return index(list, target, true, false);
    }

    /**
     * 返回list中第一个小于等于target的数的索引
     *
     * @param list
     * @param target
     * @return
     */
    public static int indexLessThanOrEqualTo(@Nullable int[] list, int target) {
        return index(list, target, true, true);
    }

    /**
     * 返回list中第一个大于target的数的索引
     *
     * @param list
     * @param target
     * @return
     */
    public static int indexMoreThan(@Nullable List<Integer> list, @Nullable Integer target) {
        return index(list, target, false, false);
    }

    /**
     * 返回list中第一个大于等于target的数的索引
     *
     * @param list
     * @param target
     * @return
     */
    public static int indexMoreThanOrEqualTo(@Nullable List<Integer> list, @Nullable Integer target) {
        return index(list, target, false, true);
    }

    /**
     * 返回list中第一个大于target的数的索引
     *
     * @param list
     * @param target
     * @return
     */
    public static int indexMoreThan(@Nullable int[] list, int target) {
        return index(list, target, false, false);
    }

    /**
     * 返回list中第一个大于等于target的数的索引
     *
     * @param list
     * @param target
     * @return
     */
    public static int indexMoreThanOrEqualTo(@Nullable int[] list, int target) {
        return index(list, target, false, true);
    }

    private static int index(List<Integer> list, Integer target, boolean less, boolean equal) {
        if (list == null) {
            return -1;
        }
        int size = list.size();
        for (int i = 0; i < size; i++) {
            Integer n = list.get(i);
            if (n == null) {
                if (equal && target == null) {
                    return i;
                }
            } else if ((less && target < n) || (!less && target > n) || (equal && n.equals(target))) {
                return i;
            }
        }
        return -1;
    }

    private static int index(int[] list, int target, boolean less, boolean equal) {
        if (list == null) {
            return -1;
        }
        int size = list.length;
        for (int i = 0; i < size; i++) {
            int n = list[i];
            if ((less && target < n) || (!less && target > n) || (equal && target == n)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 将字符串数组转换为int数组
     *
     * @param strings
     * @return
     */
    public static int[] parseToInts(@Nullable String[] strings) {
        if (strings == null) {
            return new int[0];
        }
        int[] ints = new int[strings.length];
        for (int i = 0; i < strings.length; i++) {
            ints[i] = parseToInt(strings[i]);
        }
        return ints;
    }

    public static int[] parseToInts(@Nullable String data, @NonNull String separator) {
        if (data == null || separator == null) {
            return new int[0];
        }
        return parseToInts(data.trim().split(separator));
    }

    private static Number toNumber(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Number) {
            return (Number) object;
        } else if (object instanceof Boolean) {
            Boolean aBoolean = (Boolean) object;
            return aBoolean ? 1 : 0;
        } else if (object instanceof BigInteger) {
            return (Number) object;
        } else if (object instanceof BigDecimal) {
            return (Number) object;
        }
        return null;
    }

    public static int parseToInt(@Nullable Object object) {
        return Ints.saturatedCast(parseToLong(object));
    }

    public static int parseToInt(@Nullable String data) {
        return Ints.saturatedCast(parseToLong(data));
    }

    public static long parseToLong(@Nullable Object object) {
        if (object == null) {
            return 0L;
        }
        Number number = toNumber(object);
        if (number != null) {
            return number.longValue();
        }
        return parseToLong(object.toString());
    }

    public static long parseToLong(@Nullable String data) {
        if (data == null) {
            return 0L;
        }
        data = data.trim();
        int length = data.length();
        if (length == 0) {
            return 0L;
        }
        int radix = 10;
        if (data.charAt(0) == '0' && length > 1) {
            char c = data.charAt(1);
            switch (c) {
                case 'x':
                case 'X':
                    if (length > 2) {
                        data = data.substring(2);
                    } else {
                        return 0L;
                    }
                    radix = 16;
                    break;
                case 'b':
                case 'B':
                    if (length > 2) {
                        data = data.substring(2);
                    } else {
                        return 0L;
                    }
                    radix = 2;
                    break;
                default:
                    data = data.substring(1);
                    radix = 8;
                    break;
            }
            if (data.isEmpty()) {
                return 0L;
            }
        }
        Long aLong = null;
        try {
            aLong = Long.parseLong(data, radix);
        } catch (Exception ignore) {
        }
        return aLong == null ? 0L : aLong;
    }

    public static double parseToDouble(@Nullable Object object) {
        if (object == null) {
            return 0.0;
        }
        Number number = toNumber(object);
        if (number != null) {
            return number.doubleValue();
        }
        return parseToDouble(object.toString());
    }

    public static double parseToDouble(@Nullable String data) {
        if (data == null) {
            return 0.0;
        }
        data = data.trim();
        if (string.isEmpty()) {
            return 0.0;
        }
        Double aDouble = null;
        try {
            aDouble = Double.parseDouble(data);
        } catch (Exception ignore) {
        }
        return aDouble == null ? 0.0 : aDouble;
    }

    public static boolean parseToBoolean(@Nullable Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof Boolean) {
            return (Boolean) object;
        } else {
            Number number = toNumber(object);
            if (number != null) {
                return number.doubleValue() != 0;
            }
        }
        return parseToBoolean(object.toString());
    }

    /**
     * true, yes, on(无视大小写),非0数返回true,其他情况返回false
     *
     * @param data
     * @return
     */
    public static boolean parseToBoolean(@Nullable String data) {
        if (data == null) {
            return false;
        }
        data = data.trim();
        if (data.isEmpty()) {
            return false;
        }
        if (data.equalsIgnoreCase("true") || data.equalsIgnoreCase("yes") ||
                data.equalsIgnoreCase("on")) {
            return true;
        }
        return parseToDouble(data) != 0;
    }

    @Data
    public static class Counter implements IntSupplier, IntConsumer, IntUnaryOperator, IntPredicate {
        public static final int STEP = 1;
        public static final int ZERO = 0;
        int count;

        public Counter() {
            this(ZERO);
        }

        public Counter(int count) {
            set(count);
        }

        public int get() {
            return count;
        }

        public void set(int count) {
            this.count = count;
        }

        public int getAndInc() {
            return getAndInc(STEP);
        }

        public int getAndInc(int i) {
            int count = this.count;
            this.count += i;
            return count;
        }

        public int getAndDec() {
            return getAndDec(STEP);
        }

        public int getAndDec(int i) {
            int count = this.count;
            this.count -= i;
            return count;
        }

        public int incAndGet() {
            return incAndGet(STEP);
        }

        public int incAndGet(int i) {
            return count += i;
        }

        public int decAndGet() {
            return decAndGet(STEP);
        }

        public int decAndGet(int i) {
            return count -= i;
        }

        public boolean eq(int i) {
            return count == i;
        }

        public boolean eq0() {
            return eq(ZERO);
        }

        public boolean gt(int i) {
            return count > i;
        }

        public boolean ge(int i) {
            return count >= i;
        }

        public boolean lt(int i) {
            return count < i;
        }

        public boolean le(int i) {
            return count <= i;
        }

        public boolean gt0() {
            return gt(ZERO);
        }

        public boolean ge0() {
            return ge(ZERO);
        }

        public boolean lt0() {
            return lt(ZERO);
        }

        public boolean le0() {
            return le(ZERO);
        }

        @Override
        public void accept(int value) {
            set(value);
        }

        @Override
        public int getAsInt() {
            return get();
        }

        @Override
        public int applyAsInt(int operand) {
            return incAndGet(operand);
        }

        @Override
        public boolean test(int value) {
            return eq(value);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Holder<T> implements Consumer<T>, Supplier<T>, Predicate<T> {
        T data;

        @Override
        public void accept(T t) {
            data = t;
        }

        @Override
        public T get() {
            return data;
        }

        @Override
        public boolean test(T t) {
            return equals(t);
        }
    }
}
