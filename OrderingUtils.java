package zk.util;

import com.google.common.base.Optional;
import com.google.common.collect.Ordering;

import java.util.*;
import java.util.Map.Entry;

/**
 * Created on 2017/9/20 16:51.
 *
 * @author 周锟
 */
public class OrderingUtils {
    public static <K, V extends Comparable<V>> Ordering<Entry<K, V>> entryValueOrdering() {
        return new Ordering<Entry<K, V>>() {
            @Override
            public int compare(Entry<K, V> left, Entry<K, V> right) {
                return left.getValue().compareTo(right.getValue());
            }
        };
    }

    public static <K, V> Ordering<Entry<K, V>> entryValueOrdering(final Comparator<V> comparator) {
        return new Ordering<Entry<K, V>>() {
            @Override
            public int compare(Entry<K, V> left, Entry<K, V> right) {
                return comparator.compare(left.getValue(), right.getValue());
            }
        };
    }

    public static <K, V extends Comparable<V>> Optional<Entry<K, V>> minValueEntry(Collection<Entry<K, V>> entries) {
        return minValueEntry(entries, OrderingUtils.<K, V>entryValueOrdering());
    }

    public static <K, V> Optional<Entry<K, V>> minValueEntry(Collection<Entry<K, V>> entries,
                                                             Ordering<Entry<K, V>> ordering) {
        if (entries == null || entries.isEmpty() || ordering == null) {
            return Optional.absent();
        }
        return Optional.of(ordering.min(entries));
    }

    public static <K, V extends Comparable<V>> Optional<Entry<K, V>> maxValueEntry(Collection<Entry<K, V>> entries) {
        return maxValueEntry(entries, OrderingUtils.<K, V>entryValueOrdering());
    }

    public static <K, V> Optional<Entry<K, V>> maxValueEntry(Collection<Entry<K, V>> entries,
                                                             Ordering<Entry<K, V>> ordering) {
        if (entries == null || entries.isEmpty() || ordering == null) {
            return Optional.absent();
        }
        return Optional.of(ordering.max(entries));
    }

    public static <K, V extends Comparable<V>> List<Entry<K, V>> sortedByValue(Collection<Entry<K, V>> entries) {
        return sortedByValue(entries, OrderingUtils.<K, V>entryValueOrdering());
    }

    public static <K, V> List<Entry<K, V>> sortedByValue(Collection<Entry<K, V>> entries,
                                                         Ordering<Entry<K, V>> ordering) {
        if (entries == null || entries.isEmpty()) {
            return Collections.emptyList();
        }
        if (ordering == null) {
            return new ArrayList<>(entries);
        }
        return ordering.sortedCopy(entries);
    }
}