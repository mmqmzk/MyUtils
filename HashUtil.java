package zk.util;

import com.google.common.base.Charsets;
import com.google.common.collect.Ordering;
import com.sh.commons.util.Symbol;
import com.sh.game.util.Functions;
import com.sh.game.util.Utils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created on 2019/06/26 14:37.
 *
 * @author 周锟
 */
public enum HashUtil {

    MD5 {
        @Override
        public String hash(byte[] str) {
            return DigestUtils.md5Hex(str);
        }
    },

    SHA1 {
        @Override
        public String hash(byte[] str) {
            return DigestUtils.sha1Hex(str);
        }
    },

    SHA256 {
        @Override
        public String hash(byte[] str) {
            return DigestUtils.sha256Hex(str);
        }
    },
    ;

    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String urlDecode(String str) {
        try {
            return URLDecoder.decode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Function<Entry<String, Object>, String> entryToString(String kvSeparator, boolean urlEncode) {
        return entry -> entry.getKey() + kvSeparator
                + (urlEncode ? urlEncode(String.valueOf(entry.getValue())) : entry.getValue());
    }

    public static Function<Entry<String, Object>, String> entryValueToString() {
        return Functions.join(Entry::getValue, String::valueOf);
    }

    public static Predicate<Entry<String, Object>> filterByNotContains(Collection<String> keys) {
        return Functions.joinP(Entry<String, Object>::getKey, keys::contains).negate();
    }

    public static BinaryOperator<String> stringJoiner(String separator) {
        return (s, c) -> StringUtils.isEmpty(s) ? c : s + separator + c;
    }

    public String hash(String str) {
        return hash(str.getBytes(Charsets.UTF_8));
    }

    public abstract String hash(byte[] data);

    public String sign(String prefix, String separator, String suffix, Object... args) {
        return signCommon(Arrays.stream(args), Functions.alwaysTrue(),
                null, String::valueOf, stringJoiner(separator), prefix, suffix);
    }

    public String signMap(Map<String, Object> map,
            Set<String> excludeKeys, String prefix, String suffix) {
        return signMap(map, excludeKeys, Symbol.DENGHAO, prefix, Symbol.AND, suffix, false);
    }

    public String signMap(Map<String, Object> map,
            Set<String> excludeKeys, String kvSeparator,
            String prefix, String separator, String suffix, boolean urlEncode) {
        return signMap(map, excludeKeys, entryToString(kvSeparator, urlEncode), prefix, separator, suffix);
    }

    public String signMap(Map<String, Object> map, Set<String> excludeKeys,
            Function<Entry<String, Object>, String> toString, String prefix,
            String separator, String suffix) {
        Comparator<Entry<String, Object>> comparator = Entry.comparingByKey();
        return signCommon(map.entrySet().stream(),
                filterByNotContains(excludeKeys), comparator, toString, stringJoiner(separator), prefix, suffix);
    }

    public String signExplicit(Map<String, Object> map, List<String> keys,
            Function<Entry<String, Object>, String> toString, String prefix,
            String separator, String suffix) {
        Ordering<Entry<String, Object>> comparator = Ordering.explicit(keys).onResultOf(Entry::getKey);
        return signCommon(map.entrySet().stream(),
                Functions.joinP(Entry::getKey, Functions.inCollection(keys)),
                comparator, toString, stringJoiner(separator), prefix, suffix);
    }

    public <T> String signCommon(Collection<T> elements,
            Predicate<T> predicate, Comparator<T> comparator,
            Function<T, String> toString, BinaryOperator<String> joiner, String suffix) {
        return signCommon(elements.stream(), predicate, comparator, toString, joiner, "", suffix);
    }

    public <T> String signCommon(Stream<T> elements, Predicate<T> predicate,
            Comparator<T> comparator, Function<T, String> toString,
            BinaryOperator<String> joiner, String prefix, String suffix) {
        Stream<T> stream = elements.filter(predicate);
        if (comparator != null) {
            stream = stream.sorted(comparator);
        }
        return stream.map(toString)
                .reduce(joiner)
                .map(Functions.prepend(prefix))
                .map(Functions.append(suffix))
                .map(this::hash)
                .orElse(StringUtils.EMPTY);
    }
}
