package zk.util;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import com.sh.commons.tuple.Tuple;
import com.sh.commons.tuple.TwoTuple;
import lombok.Data;

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
public class Utils {

    public static String getStackTrace() {
        return getStackTrace(1, 10);
    }

    public static String getStackTrace(int start, int stop) {
        if (start > stop) {
            throw new IllegalArgumentException("start > stop");
        }
        StringBuilder builder = new StringBuilder((stop - start + 1) * 50);
        StackTraceElement[] stackTrace = new Exception().getStackTrace();
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

    public static int nextInt(int i) {
        return ThreadLocalRandom.current().nextInt(i);
    }

    public static int random(int min, int max) {
        return nextInt(max - min + 1) + min;
    }

    public static boolean isLuck(int rate) {
        return isLuck(rate, 100);
    }

    public static boolean isLuck(int rate, int base) {
        return nextInt(base) < rate;
    }

    public static <T> Optional<T> randomChoose(T[] array) {
        return randomChoose(array, null);
    }

    public static <T> Optional<T> randomChoose(T[] array, ToIntFunction<T> weigher) {
        if (array == null) {
            return Optional.empty();
        }
        int total = weigher == null ? array.length : calculateTotalWeight(Arrays.stream(array), weigher);
        return randomChoose(Arrays.stream(array), total, weigher);
    }

    public static <T> Optional<T> randomChoose(Collection<T> collection) {
        return randomChoose(collection, null);
    }

    public static <T> Optional<T> randomChoose(Collection<T> collection, ToIntFunction<T> weigher) {
        if (collection == null) {
            return Optional.empty();
        }
        int total = weigher == null ? collection.size() : calculateTotalWeight(collection.stream(), weigher);
        return randomChoose(collection.stream(), total, weigher);
    }

    private static <T> Optional<T> randomChoose(Stream<T> stream, int total, ToIntFunction<T> weigher) {
        if (stream == null || total <= 0) {
            return Optional.empty();
        }
        int random = nextInt(total);
        if (weigher == null) {
            return stream.filter(Objects::nonNull).skip(random).findFirst();
        }
        TwoTuple<Integer, T> tuple = Tuple.tuple(random, null);
        tuple = stream.filter(Objects::nonNull).reduce(tuple, (t, obj) -> {
            int weight;
            int current = t.getFirst();
            return current < 0 || (weight = weigher.applyAsInt(obj)) <= 0 ? t : Tuple.tuple(current - weight, obj);
        }, BinaryOperator.minBy(orderingFromToIntFunction(TwoTuple::getFirst)));
        return Optional.ofNullable(tuple.getSecond());
    }

    private static <T> int calculateTotalWeight(Stream<T> stream, ToIntFunction<T> weigher) {
        if (stream == null) {
            return 0;
        }
        if (weigher == null) {
            return Math.toIntExact(stream.filter(Objects::nonNull).count());
        }
        return stream.filter(Objects::nonNull).mapToInt(weigher).filter(w -> w > 0).sum();
    }

    public static int randomIndex(int[] list) {
        int length;
        if (list == null || (length = list.length) == 0) {
            return -1;
        }
        int total = 0;
        for (int i = 0; i < length; i++) {
            if (list[i] <= 0) {
                continue;
            }
            total += list[i];
        }
        int random = nextInt(total);
        for (int i = 0; i < length; i++) {
            if (list[i] <= 0) {
                continue;
            }
            random -= list[i];
            if (random < 0) {
                return i;
            }
        }
        return -1;
    }

    public static int randomIndex(long[] list) {
        int length;
        if (list == null || (length = list.length) == 0) {
            return -1;
        }
        long total = 0;
        for (int i = 0; i < length; i++) {
            if (list[i] <= 0) {
                continue;
            }
            total += list[i];
        }
        long random = ThreadLocalRandom.current().nextLong(total);
        for (int i = 0; i < length; i++) {
            if (list[i] <= 0) {
                continue;
            }
            random -= list[i];
            if (random < 0) {
                return i;
            }
        }
        return -1;
    }

    public static <T> int randomIndex(T[] list, ToIntFunction<T> weigher) {
        int length;
        if (list == null || weigher == null || (length = list.length) == 0) {
            return -1;
        }
        int total = 0;
        for (int i = 0; i < length; i++) {
            int weight;
            if (list[i] == null || (weight = weigher.applyAsInt(list[i])) <= 0) {
                continue;
            }
            total += weight;
        }
        if (total <= 0) {
            return -1;
        }
        int random = nextInt(total);
        for (int i = 0; i < length; i++) {
            int weight;
            if (list[i] == null || (weight = weigher.applyAsInt(list[i])) <= 0) {
                continue;
            }
            random -= weight;
            if (random < 0) {
                return i;
            }
        }
        return -1;
    }

    public static int randomIndex(Integer[] list) {
        return randomIndex(list, Integer::intValue);
    }

    public static <T> int randomIndex(List<T> list, ToIntFunction<T> weigher) {
        int size;
        if (list == null || weigher == null || (size = list.size()) == 0) {
            return -1;
        }
        int total = calculateTotalWeight(list.stream(), weigher);
        if (total <= 0) {
            return -1;
        }
        int random = nextInt(total);
        for (int i = 0; i < size; i++) {
            T value = list.get(i);
            if (value == null) {
                continue;
            }
            int weight = weigher.applyAsInt(value);
            if (weight <= 0) {
                continue;
            }
            random -= weight;
            if (random < 0) {
                return i;
            }
        }
        return -1;
    }

    public static int randomIndex(List<Integer> list) {
        return randomIndex(list, Integer::intValue);
    }

    public static <T> List<T> randomChooseN(T[] array, int n) {
        return randomChooseN(array, n, null);
    }

    public static <T> List<T> randomChooseN(T[] array, int n, ToIntFunction<T> weigher) {
        int length;
        if (array == null || (length = array.length) == 0 || n <= 0) {
            return Collections.emptyList();
        }
        int total = weigher == null ? length : calculateTotalWeight(Arrays.stream(array), weigher);
        return randomChooseN(Arrays.stream(array), n, total, weigher);
    }

    public static <T> List<T> randomChooseN(Collection<T> collection, int n) {
        return randomChooseN(collection, n, null);
    }

    public static <T> List<T> randomChooseN(Collection<T> collection, int n, ToIntFunction<T> weigher) {
        int size;
        if (collection == null || (size = collection.size()) == 0|| n <= 0) {
            return Collections.emptyList();
        }
        int total = weigher == null ? size : calculateTotalWeight(collection.stream(), weigher);
        return randomChooseN(collection.stream(), n, total, weigher);
    }

    private static <T> List<T> randomChooseN(Stream<T> stream, int n, int total, ToIntFunction<T> weigher) {
        if (stream == null || total <= 0 || n <= 0) {
            return Collections.emptyList();
        }
        Counter count = new Counter(n);
        Counter totalWeight = new Counter(total);
        return stream.filter(Objects::nonNull).filter(obj -> {
            int weight = weigher == null ? 1 : weigher.applyAsInt(obj);
            if (weight > 0 && count.gt0() && totalWeight.gt0()) {
                int random = nextInt(totalWeight.getAndDec(weight));
                if (random < weight * count.get()) {
                    count.decAndGet();
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    public static <T> Optional<T> randomRemove(List<T> list) {
        if (list == null || list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(list.remove(nextInt(list.size())));
    }

    public static <T> Optional<T> randomRemove(Collection<T> collection) {
        return randomRemove(collection, null);
    }

    public static <T> Optional<T> randomRemove(Collection<T> collection, ToIntFunction<T> weigher) {
        if (collection == null || collection.isEmpty()) {
            return Optional.empty();
        }
        List<T> list = randomRemoveN(collection, 1, weigher);
        return Optional.ofNullable(get(list, 0));
    }

    public static <T> List<T> randomRemoveN(Collection<T> collection, int n) {
        return randomRemoveN(collection, n, null);
    }

    public static <T> List<T> randomRemoveN(Collection<T> collection, int n, ToIntFunction<T> weigher) {
        int size;
        if (collection == null || (size = collection.size()) == 0 || n <= 0) {
            return Collections.emptyList();
        }
        int total = weigher == null ? size : calculateTotalWeight(collection.stream(), weigher);
        if (total <= 0) {
            return Collections.emptyList();
        }
        List<T> result = Lists.newArrayListWithCapacity(n);
        for (; n > 0 && total > 0; n--) {
            int random = nextInt(total);
            for (Iterator<T> iterator = collection.iterator(); iterator.hasNext(); ) {
                T t = iterator.next();
                if (t == null) {
                    continue;
                }
                int weight = weigher == null ? 1 : weigher.applyAsInt(t);
                if (weight <= 0) {
                    continue;
                }
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

    public static int get(int[] array, int index) {
        return get(array, index, 0);
    }

    public static int get(int[] array, int index, int defaultValue) {
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

    public static long get(long[] array, int index) {
        return get(array, index, 0L);
    }

    public static long get(long[] array, int index, long defaultValue) {
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

    public static <T> T get(T[] array, int index) {
        return get(array, index, null);
    }

    public static <T> T get(T[] array, int index, T defaultValue) {
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

    public static <T> T get(Collection<T> collection, int index) {
        return get(collection, index, null);
    }

    public static <T> T get(Collection<T> collection, int index, T defaultValue) {
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

    public static <T, R> Ordering<T> orderingFromToIntFunction(ToIntFunction<T> function) {
        return orderingFromToIntFunction(function, true);
    }

    public static <T, R> Ordering<T> orderingFromToIntFunction(ToIntFunction<T> function, boolean nullsFirst) {
        Objects.requireNonNull(function);
        Ordering<T> ordering = Ordering.from((a, b) ->
                a == b ? 0 : Integer.compare(function.applyAsInt(a), function.applyAsInt(b)));
        return nullsFirst ? ordering.nullsFirst() : ordering.nullsLast();
    }

    public static <T, R> Ordering<T> orderingFromFunction(Function<T, Comparable<R>> function) {
        return orderingFromFunction(function, Ordering.natural(), true);
    }

    public static <T, R> Ordering<T> orderingFromFunction(Function<T, Comparable<R>> function, boolean nullsFirst) {
        return orderingFromFunction(function, Ordering.natural(), nullsFirst);
    }

    public static <T, R> Ordering<T> orderingFromFunction(Function<T, R> function, Comparator<R> comparator) {
        return orderingFromFunction(function, comparator, true);
    }

    public static <T, R> Ordering<T> orderingFromFunction(Function<T, R> function, Comparator<R> comparator, boolean nullsFirst) {
        Objects.requireNonNull(function);
        Objects.requireNonNull(comparator);
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
    public static int indexLessThan(List<Integer> list, Integer target) {
        return index(list, target, true, false);
    }

    /**
     * 返回list中第一个小于等于target的数的索引
     *
     * @param list
     * @param target
     * @return
     */
    public static int indexLessThanOrEqualTo(List<Integer> list, Integer target) {
        return index(list, target, true, true);
    }

    /**
     * 返回list中第一个小于target的数的索引
     *
     * @param list
     * @param target
     * @return
     */
    public static int indexLessThan(int[] list, int target) {
        return index(list, target, true, false);
    }

    /**
     * 返回list中第一个小于等于target的数的索引
     *
     * @param list
     * @param target
     * @return
     */
    public static int indexLessThanOrEqualTo(int[] list, int target) {
        return index(list, target, true, true);
    }

    /**
     * 返回list中第一个大于target的数的索引
     *
     * @param list
     * @param target
     * @return
     */
    public static int indexMoreThan(List<Integer> list, Integer target) {
        return index(list, target, false, false);
    }

    /**
     * 返回list中第一个大于等于target的数的索引
     *
     * @param list
     * @param target
     * @return
     */
    public static int indexMoreThanOrEqualTo(List<Integer> list, Integer target) {
        return index(list, target, false, true);
    }

    /**
     * 返回list中第一个大于target的数的索引
     *
     * @param list
     * @param target
     * @return
     */
    public static int indexMoreThan(int[] list, int target) {
        return index(list, target, false, false);
    }

    /**
     * 返回list中第一个大于等于target的数的索引
     *
     * @param list
     * @param target
     * @return
     */
    public static int indexMoreThanOrEqualTo(int[] list, int target) {
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
    public static int[] parseToInts(String[] strings) {
        if (strings == null) {
            return new int[0];
        }
        int[] ints = new int[strings.length];
        for (int i = 0; i < strings.length; i++) {
            ints[i] = parseToInt(strings[i]);
        }
        return ints;
    }

    public static int[] parseToInts(String string, String separator) {
        if (string == null || separator == null) {
            return new int[0];
        }
        return parseToInts(string.trim().split(separator));
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
            return (BigInteger) object;
        } else if (object instanceof BigDecimal) {
            return (BigDecimal) object;
        }
        return null;
    }

    public static int parseToInt(Object object) {
        return Ints.saturatedCast(parseToLong(object));
    }

    public static int parseToInt(String string) {
        return Ints.saturatedCast(parseToLong(string));
    }

    public static long parseToLong(Object object) {
        if (object == null) {
            return 0L;
        }
        Number number = toNumber(object);
        if (number != null) {
            return number.longValue();
        }
        return parseToLong(object.toString());
    }

    public static long parseToLong(String string) {
        if (string == null) {
            return 0L;
        }
        string = string.trim();
        int length = string.length();
        if (length == 0) {
            return 0L;
        }
        int radix = 10;
        if (string.charAt(0) == '0' && length > 1) {
            char c = string.charAt(1);
            switch (c) {
                case 'x':
                case 'X':
                    if (length > 2) {
                        string = string.substring(2);
                    } else {
                        return 0L;
                    }
                    radix = 16;
                    break;
                case 'b':
                case 'B':
                    if (length > 2) {
                        string = string.substring(2);
                    } else {
                        return 0L;
                    }
                    radix = 2;
                    break;
                default:
                    string = string.substring(1);
                    radix = 8;
                    break;
            }
            if (string.isEmpty()) {
                return 0L;
            }
        }
        Long aLong = null;
        try {
            aLong = Long.parseLong(string, radix);
        } catch (Exception ignore) {
        }
        return aLong == null ? 0L : aLong;
    }

    public static double parseToDouble(Object object) {
        if (object == null) {
            return 0.0;
        }
        Number number = toNumber(object);
        if (number != null) {
            return number.doubleValue();
        }
        return parseToDouble(object.toString());
    }

    public static double parseToDouble(String string) {
        if (string == null) {
            return 0.0;
        }
        string = string.trim();
        if (string.isEmpty()) {
            return 0.0;
        }
        Double aDouble = null;
        try {
            aDouble = Double.parseDouble(string);
        } catch (Exception ignore) {
        }
        return aDouble == null ? 0.0 : aDouble;
    }

    public static boolean parseToBoolean(Object object) {
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
     * @param string
     * @return
     */
    public static boolean parseToBoolean(String string) {
        if (string == null) {
            return false;
        }
        string = string.trim();
        if (string.isEmpty()) {
            return false;
        }
        if (string.equalsIgnoreCase("true") || string.equalsIgnoreCase("yes") ||
                string.equalsIgnoreCase("on")) {
            return true;
        }
        return parseToDouble(string) != 0;
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
    public static class Holder<T> implements Consumer<T>, Supplier<T>, Predicate<T> {
        T data;

        @Override
        public void accept(T t) {
            this.data = t;
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
