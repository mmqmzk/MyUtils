package zk.util;

import lombok.Getter;
import lombok.NonNull;
import java.util.*;

/**
 * 并查集
 * Created on 2017/7/22 16:40.
 *
 * @author 周锟
 */
public class UnionFindSet<T> {

    private final Map<T, T> parents;

    private final Map<T, Integer> treeSize;

    @Getter
    private int count;

    public UnionFindSet() {
        this(10);
    }

    public UnionFindSet(int sz) {
        parents = new HashMap<>(sz);
        treeSize = new HashMap<>(sz);
        count = 0;
    }

    public UnionFindSet(@NonNull Collection<T> elements) {
        this(elements.size());
        addAll(elements);
    }

    public UnionFindSet(@NonNull T[] elements) {
        this(elements.length);
        addAll(elements);
    }

    public int size() {
        return parents.size();
    }

    public T find(T element) {
        if (!parents.containsKey(element)) {
            add(element);
        }
        T tmp;
        while (!isEqual((tmp = parents.get(element)), element)) {
            parents.put(element, tmp = parents.get(tmp));
            element = tmp;
        }
        return element;
    }

    public boolean add(T element) {
        if (parents.containsKey(element)) {
            return false;
        }
        parents.put(element, element);
        treeSize.put(element, 1);
        count += 1;
        return true;
    }

    public int addAll(Collection<T> elements) {
        if (elements == null || elements.isEmpty()) {
            return 0;
        }
        return StrictMath.toIntExact(elements.stream().filter(this::add).count());
    }

    public int addAll(T[] elements) {
        if (elements == null || elements.length == 0) {
            return 0;
        }
        return StrictMath.toIntExact(Arrays.stream(elements).filter(this::add).count());
    }

    public int union(T element1, T element2) {
        T parent1 = find(element1);
        T parent2 = find(element2);
        if (isEqual(parent1, parent2)) {
            return count;
        }
        int count1 = treeSize.getOrDefault(parent1, 0);
        int count2 = treeSize.getOrDefault(parent2, 0);
        if (count1 <= count2) {
            parents.put(parent1, parent2);
            treeSize.merge(parent1, count2, Integer::sum);
        } else {
            parents.put(parent2, parent1);
            treeSize.merge(parent2, count1, Integer::sum);
        }
        count -= 1;
        return count;
    }

    public int unionAll(Map<T, T> relations) {
        relations.forEach(this::union);
        return count;
    }

    protected boolean isEqual(T a, T b) {
        return Objects.equals(a, b);
    }
}
