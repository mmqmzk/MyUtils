package zk.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 字典树, 非线程安全
 * Created on 2019/03/13 13:00.
 *
 * @author 周锟
 */
public class TrieTree<T> {
    public static final char ZERO = '\0';
    public static final String STAR = "*";

    private class Node {
        List<T> words = Lists.newArrayListWithCapacity(4);
        Map<Character, Node> nodeMap = Maps.newHashMap();
        char value;
        int prefixes = 0;

        public Node(char value) {
            this.value = value;
        }

        boolean isEnd() {
            return !words.isEmpty();
        }

        Node addNode(char value) {
           value = transChar(value);
            Node node = nodeMap.get(value);
            if (node == null) {
                node = new Node(value);
                nodeMap.put(value, node);
            }
            prefixes++;
            return node;
        }

        void putEnd(T t) {
            words.add(t);
        }

        Node getNode(char value) {
            return nodeMap.get(transChar(value));
        }

        T getEnd() {
            return picker.apply(words);
        }
    }

    private Function<List<T>, T> picker;

    private Node root = new Node(ZERO);

    public TrieTree() {
        this(Functions.biBindSecond(Utils::get, 0));
    }

    public TrieTree(Function<List<T>, T> picker) {
        this.picker = Objects.requireNonNull(picker);
    }

    public void addWord(String word, T data) {
        if (word == null || word.isEmpty()) {
            return;
        }
        addWord(word, data, root, 0);
    }

    private void addWord(String word, T data, Node node, int index) {
        if (index >= word.length()) {
            node.putEnd(data);
        } else {
            char value = word.charAt(index);
            if (value != ZERO) {
                node = node.addNode(value);
            }
            addWord(word, data, node, index + 1);
        }
    }

    public Optional<T> isDirtyWord(String word) {
        if (word == null || word.isEmpty()) {
            return Optional.empty();
        }
        return isDirtyWord(word, 0);
    }

    private Optional<T> isDirtyWord(String word, int start) {
        if (start >= word.length()) {
            return Optional.empty();
        }
        Node node = root;
        for (int i = start; i < word.length(); i++) {
            Node next = node.getNode(word.charAt(i));
            if (next == null) {
                break;
            } else if (next.isEnd()) {
                return Optional.ofNullable(next.getEnd());
            }
        }
        return isDirtyWord(word, start + 1);
    }

    public void forEach(BiConsumer<String, List<T>> consumer) {
        Objects.requireNonNull(consumer);
        StringBuilder builder = new StringBuilder();
        forEach(consumer, builder, root);
    }

    private void forEach(BiConsumer<String, List<T>> consumer, StringBuilder builder, Node node) {
        if (node.value != ZERO) {
            builder.append(node.value);
        }
        if (node.isEnd()) {
            consumer.accept(builder.toString(), node.words);
        }
        for (Node next : node.nodeMap.values()) {
            forEach(consumer, builder, next);
        }
        if (node.value != ZERO) {
            builder.deleteCharAt(builder.length() - 1);
        }
    }

    public String replaceAll(String string) {
        return replaceAll(string, true);
    }

    public String replaceAll(String string, boolean multi) {
        return replaceAll(string, STAR, multi);
    }

    public String replaceAll(String string, String star, boolean multi) {
        if (string == null || string.isEmpty()) {
            return string;
        }
        if (star == null) {
            star = STAR;
        }
        StringBuilder builder = new StringBuilder(string.length());
        appendReplace(string, star, builder, multi, 0);
        return builder.toString();
    }

    private void appendReplace(String string, String star, StringBuilder builder, boolean multi, int start) {
        int length = string.length();
        if (start >= length) {
            return;
        }
        Node node = root;
        for (int i = start; i < length; i++) {
            Node next = node.getNode(string.charAt(i));
            if (next == null) {
                break;
            } else if (next.isEnd()) {
                appendStar(string, star, builder, multi, start, i + 1);
                return;
            }
        }
        builder.append(string.charAt(start));
        appendReplace(string, star, builder, multi, start + 1);
    }

    private void appendStar(String string, String star, StringBuilder builder, boolean multi, int start, int index) {
        for (int i = 0, len = multi ? index - start : 1; i < len; i++) {
            builder.append(star);
        }
        appendReplace(string, star, builder, multi, index);
    }

    protected char transChar(char ch) {
        return Character.toLowerCase(ch);
    }
}
