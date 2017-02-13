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

	public static IntSupplier constant(int value) {
		return () -> value;
	}

	public static LongSupplier constant(long value) {
		return () -> value;
	}

	public static DoubleSupplier constant(double value) {
		return () -> value;
	}

	public static <T, R> Function<T, R> forSupplier(Supplier<R> supplier) {
		Objects.requireNonNull(supplier);
		return t -> supplier.get();
	}

	public static <T> ToIntFunction<T> forSupplier(IntSupplier supplier) {
		Objects.requireNonNull(supplier);
		return t -> supplier.getAsInt();
	}

	public static <T> ToLongFunction<T> forSupplier(LongSupplier supplier) {
		Objects.requireNonNull(supplier);
		return t -> supplier.getAsLong();
	}

	public static <T> ToDoubleFunction<T> forSupplier(DoubleSupplier supplier) {
		Objects.requireNonNull(supplier);
		return t -> supplier.getAsDouble();
	}

	public static <T, R> Function<T, R> forMap(Map<T, R> map) {
		return forMap(map, null);
	}

	public static <T, R> Function<T, R> forMap(Map<T, R> map, R defaultValue) {
		Objects.requireNonNull(map);
		return  key -> map.containsKey(key) ? map.get(key) : defaultValue;
	}

	public static <R> IntFunction<R> forIntKeyMap(Map<Integer, R> map) {
		return  forIntKeyMap(map, null);
	}

	public static <R> IntFunction<R> forIntKeyMap(Map<Integer, R> map, R defaultValue) {
		Objects.requireNonNull(map);
		return  key -> map.containsKey(key) ? map.get(key) : defaultValue;
	}


	public static <R> LongFunction<R> forLongKeyMap(Map<Long, R> map) {
		return  forLongKeyMap(map, null);
	}

	public static <R> LongFunction<R> forLongKeyMap(Map<Long, R> map, R defaultValue) {
		Objects.requireNonNull(map);
		return  key -> map.containsKey(key) ? map.get(key) : defaultValue;
	}


	public static <R> DoubleFunction<R> forDoubleKeyMap(Map<Double, R> map) {
		return  forDoubleKeyMap(map, null);
	}

	public static <R> DoubleFunction<R> forDoubleKeyMap(Map<Double, R> map, R defaultValue) {
		Objects.requireNonNull(map);
		return  key -> map.containsKey(key) ? map.get(key) : defaultValue;
	}

	public static <T> ToIntFunction<T> forIntValueMap(Map<T, Integer> map) {
		Objects.requireNonNull(map);
		return key -> map.containsKey(key) ? map.get(key) : 0;
	}

	public static <T> ToLongFunction<T> forLongValueMap(Map<T, Long> map) {
		Objects.requireNonNull(map);
		return key -> map.containsKey(key) ? map.get(key) : 0L;
	}

	public static <T> ToDoubleFunction<T> forDoubleValueMap(Map<T, Double> map) {
		Objects.requireNonNull(map);
		return key -> map.containsKey(key) ? map.get(key) : 0.0;
	}

	//==========================================================

	public static <T, U, R> Function<U, R> bindFirst(BiFunction<T, U, R> func, T first) {
		Objects.requireNonNull(func);
		return u -> func.apply(first, u);
	}

	public static <T, U> ToIntFunction<U> bindFirst(ToIntBiFunction<T, U> func, T first) {
		Objects.requireNonNull(func);
		return u -> func.applyAsInt(first, u);
	}

	public static <T, U> ToLongFunction<U> bindFirst(ToLongBiFunction<T, U> func, T first) {
		Objects.requireNonNull(func);
		return u -> func.applyAsLong(first, u);
	}

	public static <T, U> ToDoubleFunction<U> bindFirst(ToDoubleBiFunction<T, U> func, T first) {
		Objects.requireNonNull(func);
		return u -> func.applyAsDouble(first, u);
	}


	//==========================================================

	public static <T, U, R> Function<T, R> bindSecond(BiFunction<T, U, R> func, U second) {
		Objects.requireNonNull(func);
		return t -> func.apply(t, second);
	}

	public static <T, U> ToIntFunction<T> bindSecond(ToIntBiFunction<T, U> func, U second) {
		Objects.requireNonNull(func);
		return t -> func.applyAsInt(t, second);
	}

	public static <T, U> ToLongFunction<T> bindSecond(ToLongBiFunction<T, U> func, U second) {
		Objects.requireNonNull(func);
		return t -> func.applyAsLong(t, second);
	}

	public static <T, U> ToDoubleFunction<T> bindSecond(ToDoubleBiFunction<T, U> func, U second) {
		Objects.requireNonNull(func);
		return t -> func.applyAsDouble(t, second);
	}

	//==========================================================

	public static <T> UnaryOperator<T> bindFirst(BinaryOperator<T> func, T first) {
		Objects.requireNonNull(func);
		return t -> func.apply(first, t);
	}

	public static IntUnaryOperator bindFirst(IntBinaryOperator func, int first) {
		Objects.requireNonNull(func);
		return i -> func.applyAsInt(first, i);
	}

	public static LongUnaryOperator bindFirst(LongBinaryOperator func, long first) {
		Objects.requireNonNull(func);
		return l -> func.applyAsLong(first, l);
	}

	public static DoubleUnaryOperator bindFirst(DoubleBinaryOperator func, double first) {
		Objects.requireNonNull(func);
		return d -> func.applyAsDouble(first, d);
	}

	//==========================================================

	public static <T> UnaryOperator<T> bindSecond(BinaryOperator<T> func, T second) {
		Objects.requireNonNull(func);
		return t -> func.apply(t, second);
	}

	public static IntUnaryOperator bindSecond(IntBinaryOperator func, int second) {
		Objects.requireNonNull(func);
		return i -> func.applyAsInt(i, second);
	}

	public static LongUnaryOperator bindSecond(LongBinaryOperator func, long second) {
		Objects.requireNonNull(func);
		return i -> func.applyAsLong(i, second);
	}

	public static DoubleUnaryOperator bindSecond(DoubleBinaryOperator func, double second) {
		Objects.requireNonNull(func);
		return d -> func.applyAsDouble(d, second);
	}

	//==========================================================

	public static <T, U> Consumer<U> bindFirst(BiConsumer<T, U> func, T first) {
		Objects.requireNonNull(func);
		return u -> func.accept(first, u);
	}

	public static <T> IntConsumer bindFirst(ObjIntConsumer<T> func, T first) {
		Objects.requireNonNull(func);
		return i -> func.accept(first, i);
	}

	public static <T> LongConsumer bindFirst(ObjLongConsumer<T> func, T first) {
		Objects.requireNonNull(func);
		return l -> func.accept(first, l);
	}

	public static <T> DoubleConsumer bindFirst(ObjDoubleConsumer<T> func, T first) {
		Objects.requireNonNull(func);
		return d -> func.accept(first, d);
	}

	//==========================================================

	public static <T, U> Consumer<T> bindSecond(BiConsumer<T, U> func, U second) {
		Objects.requireNonNull(func);
		return t -> func.accept(t, second);
	}

	public static <T> Consumer<T> bindSecond(ObjIntConsumer<T> func, int second) {
		Objects.requireNonNull(func);
		return t -> func.accept(t, second);
	}

	public static <T> Consumer<T> bindSecond(ObjLongConsumer<T> func, long second) {
		Objects.requireNonNull(func);
		return t -> func.accept(t, second);
	}

	public static <T> Consumer<T> bindSecond(ObjDoubleConsumer<T> func, double second) {
		Objects.requireNonNull(func);
		return t -> func.accept(t, second);
	}

	//==========================================================

	public static <T, U> Predicate<U> bindFirst(BiPredicate<T, U> func, T first) {
		Objects.requireNonNull(func);
		return u -> func.test(first, u);
	}

	public static <T, U> Predicate<T> bindSecond(BiPredicate<T, U> func, U second) {
		Objects.requireNonNull(func);
		return t -> func.test(t, second);
	}

	public static <T> BooleanSupplier bindFirst(Predicate<T> func, T first) {
		Objects.requireNonNull(func);
		return () -> func.test(first);
	}

	//==========================================================

	public static <T, R> Supplier<R> bindFirst(Function<T, R> func, T first) {
		Objects.requireNonNull(func);
		return () -> func.apply(first);
	}

	public static <T> IntSupplier bindFirst(ToIntFunction<T> func, T first) {
		Objects.requireNonNull(func);
		return () -> func.applyAsInt(first);
	}

	public static IntSupplier bindFirst(LongToIntFunction func, long first) {
		Objects.requireNonNull(func);
		return () -> func.applyAsInt(first);
	}

	public static IntSupplier bindFirst(DoubleToIntFunction func, double first) {
		Objects.requireNonNull(func);
		return () -> func.applyAsInt(first);
	}

	public static <T> LongSupplier bindFirst(ToLongFunction<T> func, T first) {
		Objects.requireNonNull(func);
		return () -> func.applyAsLong(first);
	}

	public static LongSupplier bindFirst(IntToLongFunction func, int first) {
		Objects.requireNonNull(func);
		return () -> func.applyAsLong(first);
	}

	public static LongSupplier bindFirst(DoubleToLongFunction func, double first) {
		Objects.requireNonNull(func);
		return () -> func.applyAsLong(first);
	}

	public static <T> DoubleSupplier bindFirst(ToDoubleFunction<T> func, T first) {
		Objects.requireNonNull(func);
		return () -> func.applyAsDouble(first);
	}

	public static DoubleSupplier bindFirst(IntToDoubleFunction func, int first) {
		Objects.requireNonNull(func);
		return () -> func.applyAsDouble(first);
	}

	public static DoubleSupplier bindFirst(LongToDoubleFunction func, long first) {
		Objects.requireNonNull(func);
		return () -> func.applyAsDouble(first);
	}

	//==========================================================
}
