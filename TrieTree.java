package zk.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 字典树, 非线程安全
 * Created on 2019/03/13 13:00.
 *
 * @author 周锟
 */
@RequiredArgsConstructor
public class TrieTree<T> {
    public static final char ZERO = '\0';
    public static final String STAR = "*";
    public static final int DATA_PER_NODE = 4;

    @RequiredArgsConstructor
    private class Node {
        final List<T> words = Lists.newArrayListWithCapacity(DATA_PER_NODE);
        final Map<Character, Node> nodeMap = Maps.newHashMap();
        @NonNull
        final String value;
        int prefixes = 0;

        boolean isEnd() {
            return !words.isEmpty();
        }

        Node addNode(@NonNull String value) {
            if (value.isEmpty() || value.equals(this.value)) {
                return this;
            }
            prefixes++;
            char ch = transChar(value.charAt(value.length() - 1));
            return nodeMap.computeIfAbsent(ch, k -> new Node(value));
        }

        void putEnd(@NonNull T t) {
            words.add(t);
        }

        @Nullable
        Node getNode(char value) {
            return nodeMap.get(transChar(value));
        }

        List<Entry<String, T>> getEntries() {
            return words.stream().map(Pair.bindFirst(value)).collect(Collectors.toList());
        }

        @Nullable
        Entry<String, T> getEnd() {
            return getEnd(picker);
        }

        @Nullable
        Entry<String, T> getEnd(@NonNull Function<List<Entry<String, T>>, Entry<String, T>> picker) {
            return picker.apply(getEntries());
        }
    }

    @Data
    @AllArgsConstructor
    private static class Pair<K, V> implements Entry<K, V> {
        private K key;

        private V value;

        @Override
        public V setValue(V val) {
            V v = value;
            value = val;
            return v;
        }

        static <K, V> Function<V, Entry<K, V>> bindFirst(K key) {
            return Functions.bindFirst(Pair::new, key);
        }
    }

    @Getter
    @Setter
    @NonNull
    private Function<List<Entry<String, T>>, Entry<String, T>> picker;

    private final Node root = new Node(StringUtils.EMPTY);

    public TrieTree() {
        this(Functions.bindSecond(Utils::get, 0));
    }

    public void addWord(@NonNull String word, @NonNull T data) {
        if (word.isEmpty()) {
            return;
        }
        addWord(word, data, root, 0);
    }

    private void addWord(String word, T data, Node node, int index) {
        if (index >= word.length()) {
            node.putEnd(data);
        } else {
            node = node.addNode(word.substring(0, index + 1));
            addWord(word, data, node, index + 1);
        }
    }

    public int wordCount() {
        return root.prefixes;
    }

    public Optional<Entry<String, T>> pickWordEndFast(@Nullable String word) {
        return pickWordEndFast(word, picker, 0);
    }

    public Optional<Entry<String, T>> pickWordEndFast(@Nullable String word,
                                                      @NonNull Function<List<Entry<String, T>>, Entry<String, T>> picker) {
        return pickWordEndFast(word, picker, 0);
    }

    private Optional<Entry<String, T>> pickWordEndFast(String word,
                                                       Function<List<Entry<String, T>>, Entry<String, T>> picker,
                                                       int start) {
        if (word == null || start >= word.length()) {
            return Optional.empty();
        }
        Node node = root;
        for (int i = start; i < word.length(); i++) {
            Node next = node.getNode(word.charAt(i));
            if (next == null) {
                break;
            } else if (next.isEnd()) {
                return Optional.ofNullable(next.getEnd(picker));
            }
        }
        return pickWordEndFast(word, picker, start + 1);
    }

    public boolean isDirtyWord(@Nullable String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }
        return pickWordEndFast(word, picker, 0).isPresent();
    }

    public void forEachDirtyWord(@Nullable String word, @NonNull Consumer<List<Entry<String, T>>> consumer) {
        if (word == null) {
            return;
        }
        forEachDirtyWord(word, Functions.joinC(Node::getEntries, consumer), 0);
    }

    public void forEachDirtyWord(@Nullable String word, @NonNull BiConsumer<String, List<T>> consumer) {
        if (word == null) {
            return;
        }
        forEachDirtyWord(word, node -> consumer.accept(node.value, node.words), 0);
    }

    private void forEachDirtyWord(String word, Consumer<Node> consumer, int start) {
        if (start >= word.length()) {
            return;
        }
        Node node = root;
        for (int i = start; i < word.length(); i++) {
            Node next = node.getNode(word.charAt(i));
            if (next == null) {
                break;
            } else if (next.isEnd()) {
                consumer.accept(next);
            }
        }
        forEachDirtyWord(word, consumer, start + 1);
    }

    public Optional<Entry<String, T>> pickWordEndHighest(@Nullable String word,
                                                         @NonNull Function<List<Entry<String, T>>, Entry<String, T>> picker) {
        if (word == null || word.isEmpty()) {
            return Optional.empty();
        }
        List<Entry<String, T>> list = Lists.newArrayListWithCapacity(word.length() * DATA_PER_NODE);
        forEachDirtyWord(word, Functions.joinC(Node::getEntries, list::addAll), 0);
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(picker.apply(list));
    }

    public void forEach(@NonNull Consumer<List<Entry<String, T>>> consumer) {
        forEach(Functions.joinC(Node::getEntries, consumer), root);
    }

    public void forEach(@NonNull BiConsumer<String, List<T>> consumer) {
        forEach(node -> consumer.accept(node.value, node.words), root);
    }

    private void forEach(Consumer<Node> consumer, Node node) {
        if (node.isEnd()) {
            consumer.accept(node);
        }
        node.nodeMap.values().forEach(Functions.cBindFirst(this::forEach, consumer));
    }

    public String replaceFast(@Nullable String string) {
        return replaceFast(string, -1);
    }

    public String replaceFast(@Nullable String string, int multi) {
        return replaceFast(string, STAR, multi);
    }

    public String replaceFast(@Nullable String string, @NonNull String star, int multi) {
        if (string == null || string.isEmpty()) {
            return string;
        }
        StringBuilder builder = new StringBuilder(string.length());
        appendReplace(string, star, builder, multi, 0, false);
        return builder.toString();
    }

    public String replaceLongest(@Nullable String string) {
        return replaceLongest(string, -1);
    }

    public String replaceLongest(@Nullable String string, int multi) {
        return replaceLongest(string, STAR, multi);
    }

    public String replaceLongest(@Nullable String string, @NonNull String star, int multi) {
        if (string == null || string.isEmpty()) {
            return string;
        }
        StringBuilder builder = new StringBuilder(string.length());
        appendReplace(string, star, builder, multi, 0, true);
        return builder.toString();
    }

    private void appendReplace(String string, String star,
                               StringBuilder builder, int multi, int start, boolean longest) {
        int length = string.length();
        if (start >= length) {
            return;
        }
        Node node = root;
        int end = 0;
        for (int i = start; i < length; i++) {
            Node next = node.getNode(string.charAt(i));
            if (next == null) {
                break;
            } else if (next.isEnd()) {
                end = i + 1;
                if (!longest) {
                    break;
                }
            }
        }
        if (end <= start) {
            builder.append(string.charAt(start));
            end = start + 1;
        } else {
            appendStar(string, star, builder, multi, start, end);
        }
        appendReplace(string, star, builder, multi, end, longest);
    }

    private void appendStar(String string, String star, StringBuilder builder, int multi, int start, int index) {
        for (int i = 0, len = multi < 0 ? index - start : multi; i < len; i++) {
            builder.append(star);
        }
    }

    protected char transChar(char ch) {
        return Character.toLowerCase(ch);
    }
}
