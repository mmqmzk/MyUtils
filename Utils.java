package com.sh.game.util;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by 周锟 on 2016/1/25 10:12.
 */
public class Utils {

	/**
	 * 返回0~max之间的一个随机数,不包括max
	 *
	 * @param max
	 * @return
	 */
	public static int nextInt(int max) {
		if (max <= 0) {
			throw new IllegalArgumentException("max <= 0");
		}
		return ThreadLocalRandom.current().nextInt(max);
	}

	/**
	 * rate%的概率返回true
	 *
	 * @param rate
	 * @return
	 */
	public static boolean isLuck(int rate) {
		return isLuck(rate, 100);
	}

	/**
	 * rate/base的概率返回true
	 *
	 * @param rate
	 * @param base
	 * @return
	 */
	public static boolean isLuck(int rate, int base) {
		if (base <= 0) {
			throw new IllegalArgumentException("base < 0");
		}
		return nextInt(base) < rate;
	}

	/**
	 * 返回from~to之间的随机数,包括form和to
	 *
	 * @param from
	 * @param to
	 * @return
	 */
	public static int random(int from, int to) {
		if (from > to) {
			throw new IllegalArgumentException("from > to");
		}
		int n = to - from + 1;
		return nextInt(n) + from;
	}

	/**
	 * 等概率随机选一个
	 *
	 * @param list
	 * @param <T>
	 * @return
	 */
	public static <T> T randomChoose(List<T> list) {
		if (list == null || list.isEmpty()) {
			return null;
		}
		int index = nextInt(list.size());
		return list.get(index);
	}

	/**
	 * 等概率随机选一个
	 *
	 * @param array
	 * @param <T>
	 * @return
	 */
	public static <T> T randomChoose(T[] array) {
		if (array == null || array.length == 0) {
			return null;
		}
		int index = nextInt(array.length);
		return array[index];
	}

	/**
	 * 等概率随机选一个
	 *
	 * @param ints
	 * @return
	 */
	public static int randomChoose(int[] ints) {
		if (ints == null || ints.length == 0) {
			throw new IllegalArgumentException();
		}
		int index = nextInt(ints.length);
		return ints[index];
	}

	/**
	 * 等概率随机选择n个
	 * @param list
	 * @param n
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> randomChooseN(List<T> list, int n) {
		if (list == null || n <= 0) {
			return Collections.emptyList();
		}
		if (list.isEmpty() || list.size() <= n) {
			return list;
		}
		List<T> result = new ArrayList<>(n);
		for (int i = list.size(); i > 0 && n > 0; i--) {
			if (nextInt(i) < n) {
				result.add(list.get(i - 1));
				n--;
			}
		}
		return result;
	}

	/**
	 * 等概率随机选择n个
	 * @param array
	 * @param n
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> randomChooseN(T[] array, int n) {
		if (array == null || array.length == 0 || n <= 0) {
			return Collections.emptyList();
		}
		if (array.length <= n) {
			return Arrays.asList(array);
		}
		List<T> result = new ArrayList<>(n);
		for (int i = array.length; i > 0 && n > 0; i--) {
			if (nextInt(i) < n) {
				result.add(array[i - 1]);
				n--;
			}
		}
		return result;
	}

	/**
	 * 等概率随机选择n个
	 * @param ints
	 * @param n
	 * @return
	 */
	public static List<Integer> randomChooseN(int[] ints, int n) {
		if (ints == null || ints.length == 0 || n <= 0) {
			return Collections.emptyList();
		}
		if (ints.length <= n) {
			return Ints.asList(ints);
		}
		List<Integer> result = new ArrayList<>(n);
		for (int i = ints.length; i > 0 && n > 0; i--) {
			if (nextInt(i) < n) {
				result.add(ints[i - 1]);
				n--;
			}
		}
		return result;
	}

	/**
	 * 等概率随机删除一个并返回
	 *
	 * @param list
	 * @param <T>
	 * @return
	 */
	public static <T> T randomRemove(List<T> list) {
		if (list == null || list.isEmpty()) {
			return null;
		}
		int index = nextInt(list.size());
		return list.remove(index);
	}

	/**
	 * 以对应索引上的概率选择物品
	 *
	 * @param items
	 * @param rates
	 * @param <T>
	 * @return
	 */
	public static <T> T randomChooseByRate(List<T> items, List<Integer> rates) {
		if (items == null || rates == null) {
			return null;
		}
		int index = randomIndex(rates);
		if (index >= 0 && index < items.size()) {
			return items.get(index);
		}
		return null;
	}

	public static int randomChooseByRate(int[] ints, int[] rates) {
		if (ints == null || ints.length == 0 || rates == null || rates.length == 0 || ints.length < rates.length) {
			throw new IllegalArgumentException();
		}
		int index = randomIndex(rates);
		return ints[index];
	}

	public static <T> T randomChooseByRate(List<T> items, int[] rates) {
		if (items == null || rates == null) {
			return null;
		}
		int index = randomIndex(rates);
		if (index >= 0 && index < items.size()) {
			return items.get(index);
		}
		return null;
	}
	/**
	 * 以对应索引上的概率选择物品
	 *
	 * @param items
	 * @param rates
	 * @param <T>
	 * @return
	 */
	public static <T> T randomChooseByRate(T[] items, int[] rates) {
		if (items == null || rates == null) {
			return null;
		}
		int index = randomIndex(rates);
		if (index >= 0 && index < items.length) {
			return items[index];
		}
		return null;
	}

	/**
	 * 按概率返回索引
	 *
	 * @param rates
	 * @return
	 */
	public static int randomIndex(List<Integer> rates) {
		if (rates == null) {
			return -1;
		}
		int total = 0;
		for (Integer rate : rates) {
			if (rate != null) {
				total += rate;
			}
		}
		if (total == 0) {
			return -1;
		}
		int random = nextInt(total);
		for (int i = 0; i < rates.size(); i++) {
			Integer rate = rates.get(i);
			if (rate != null) {
				random -= rate;
				if (random < 0) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * 按概率返回索引
	 *
	 * @param rates
	 * @return
	 */
	public static int randomIndex(int[] rates) {
		if (rates == null) {
			return -1;
		}
		int total = 0;
		for (int i = 0; i < rates.length; i++) {
			total += rates[i];
		}
		if (total == 0) {
			return -1;
		}
		int random = nextInt(total);
		for (int i = 0; i < rates.length; i++) {
			random -= rates[i];
			if (random < 0) {
				return i;
			}
		}
		return -1;
	}

	//=============================================================================================

	/**
	 * 返回长度为length的随机字符串,仅包括字母
	 *
	 * @param length
	 * @return
	 */
	public static String randomString(int length) {
		if (length < 0) {
			throw new IllegalArgumentException("length < 0");
		}
		return randomString(length, false);
	}

	/**
	 * 返回长度为length的随机字符串
	 *
	 * @param length
	 * @param numbers 是否包含数字
	 * @return
	 */
	public static String randomString(int length, boolean numbers) {
		if (length < 0) {
			throw new IllegalArgumentException("length < 0");
		}
		return RandomStringUtils.random(length, true, numbers);
	}

	//=============================================================================================

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
			return aBoolean ? 0 : 1;
		} else if (object instanceof BigInteger) {
			BigInteger bigInteger = (BigInteger) object;
			return bigInteger.longValue();
		} else if (object instanceof BigDecimal) {
			BigDecimal decimal = (BigDecimal) object;
			return decimal.doubleValue();
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
		Long aLong = Longs.tryParse(string, radix);
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
		Double aDouble = Doubles.tryParse(string);
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
		if (parseToDouble(string) != 0) {
			return true;
		}
		return false;
	}

	//=============================================================================================

	public static String getStackTrace() {
		return getStackTrace(1, 10);
	}

	public static String getStackTrace(int start, int stop) {
		if (start > stop) {
			throw new IllegalArgumentException("start > stop");
		}
		StringBuilder builder = new StringBuilder((stop - start + 1) * 50);
		StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
		if (stackTrace.length < start + 2) {
			return builder.toString();
		}
		appendElement(builder, stackTrace[start + 1]);
		for (int i = start + 2; i < stop + 1 && i < stackTrace.length; i++) {
			builder.append("<=");
			appendElement(builder, stackTrace[i]);
		}
		return builder.toString();
	}

	private static void appendElement(StringBuilder builder, StackTraceElement element) {
		String className = element.getClassName();
		String methodName = element.getMethodName();
		int index = className.lastIndexOf('.');
		builder.append(className.substring(index + 1));
		builder.append('.').append(methodName);
		builder.append(':').append(element.getLineNumber());
	}

	//=============================================================================================

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

	//=============================================================================================

	public static String md5(String input) {
		if (input == null) {
			return "";
		}
		return Hashing.md5().hashString(input, Charsets.UTF_8).toString();
	}

	public static String sha1(String input) {
		if (input == null) {
			return "";
		}
		return Hashing.sha1().hashString(input, Charsets.UTF_8).toString();
	}

	public static String sha256(String input) {
		if (input == null) {
			return "";
		}
		return Hashing.sha256().hashString(input, Charsets.UTF_8).toString();
	}
}
