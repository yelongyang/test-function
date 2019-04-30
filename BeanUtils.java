package venus.platform.core.tools;


import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;


public class BeanUtils {

    public final static <T, K, V> Map<K, V> map(T t, Function<? super T, ? extends K> kf, Function<? super T, ? extends V> vf) {
        if(Utils.anyIsEmpty(t, kf, vf)) {
            return Collections.emptyMap();
        }

        Map<K, V> map = new HashMap<>(1);
        map.put(get(kf, t), get(vf, t));
        return map;
    }

    private final static <T, K, V> Map<K, V> map(Collection<T> c, Function<? super T, ? extends K> kf, Function<? super T, ? extends V> vf) {
        if (Utils.anyIsEmpty(c, kf, vf)) {
            return Collections.emptyMap();
        }

        Map<K, V> map = new HashMap<>(c.size());

        c.forEach(t -> {
            K k = get(kf, t);
            V v = get(vf, t);
            map.put(k, v);
        });

        return map;
    }

    public final static <T, R> List<R> distinct(Collection<T> c, Function<? super T, ? extends R> f) {
        if (Utils.anyIsEmpty(c, f)) {
            return Collections.emptyList();
        }

        return c.stream().map(f).filter(Utils::isNotEmpty).distinct().collect(Collectors.toList());
    }

    public final static <S, T, F, C> void set(S s, Function<S, F> sf, Function<S, C> sc, T t, Function<T, F> tf, BiConsumer<T, C> tc) {
        if (Utils.anyIsEmpty(s, sf, sc, t, tf, tc)) {
            return;
        }

        Map<F, C> map = map(s, sf, sc);
        set(t, map, tf, tc);
    }

    public final static <S, T, F, C> void set(Collection<S> sl, Function<S, F> sf, Function<S, C> sc, Collection<T> tl, Function<T, F> tf, BiConsumer<T, C> tc) {
        if (Utils.isEmpty(sl) || Utils.isEmpty(tl) || Utils.anyIsEmpty(sf, sc, tf, tc)) {
            return;
        }

        Map<F, C> map = map(sl, sf, sc);
        if (Utils.isEmpty(map)) {
            return;
        }

        tl.forEach(t -> set(t, map, tf, tc));
    }


    public final static <T, F> F get(Function<T, F> tf, T t) {
        if (Utils.anyIsEmpty(t, tf)) {
            return null;
        }

        return tf.apply(t);
    }

    private final static <T, F, C> void set(T t, Map<F, C> map, Function<T, F> tf, BiConsumer<T, C> tc) {
        if(Utils.isEmpty(map) || Utils.anyIsEmpty(t, tf, tc)) {
            return;
        }

        F f = tf.apply(t);
        if(Utils.isNotEmpty(f)) {
            C c = map.get(f);
            if(Utils.isNotEmpty(c)) {
                tc.accept(t, c);
            }
        }
    }

    private static final class Utils {
        public static boolean anyIsEmpty(Object... args) {
            for(Object arg : args) {
                if(isEmpty(arg)) {
                    return true;
                }
            }

            return false;
        }

        public static boolean isEmpty(Object e) {
            if(e == null || e.equals("")) {
                return true;
            }
            return false;
        }

        public static boolean isNotEmpty(Object e) {
            return !isEmpty(e);
        }

        public static boolean isEmpty(Map e) {
            if(e == null || e.size() == 0) {
                return true;
            }
            return false;
        }

        public static boolean isEmpty(Collection e) {
            if(e == null || e.size() == 0) {
                return true;
            }
            return false;
        }
    }
}


