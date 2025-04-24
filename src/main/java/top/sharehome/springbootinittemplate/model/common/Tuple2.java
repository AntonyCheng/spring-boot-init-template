package top.sharehome.springbootinittemplate.model.common;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 二元组
 *
 * @author AntonyCheng
 */
public class Tuple2<T1, T2> implements Serializable, Cloneable {

    private final T1 t1;

    private final T2 t2;

    public Tuple2(final T1 t1, final T2 t2) {
        this.t1 = Objects.requireNonNull(t1, "Tuple t1 is null");
        this.t2 = Objects.requireNonNull(t2, "Tuple t2 is null");
    }

    public Tuple2(Tuple2<T1, T2> tuple2) {
        this.t1 = tuple2.t1;
        this.t2 = tuple2.t2;
    }

    public T1 getT1() {
        return t1;
    }

    public T2 getT2() {
        return t2;
    }

    public Object get(int index) {
        return index == 0 ? t1 : index == 1 ? t2 : null;
    }

    public Object getOrDefault(int index, Object defaultValue) {
        Object obj = get(index);
        return obj == null ? defaultValue : obj;
    }

    public int size() {
        return 2;
    }

    public List<Object> toList() {
        return Arrays.asList(toArray());
    }

    public Object[] toArray() {
        return new Object[]{t1, t2};
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Tuple2<?, ?> tuple2 = (Tuple2<?, ?>) obj;
        return t1.equals(tuple2.getT1()) && t2.equals(tuple2.getT2());
    }

    @Override
    public int hashCode() {
        int result = size();
        result = 31 * result + t1.hashCode();
        result = 31 * result + t2.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return Arrays.toString(toArray());
    }

    @Override
    public Tuple2<T1, T2> clone() {
        return new Tuple2<>(this);
    }

    @Serial
    private static final long serialVersionUID = 3994369565888807992L;
}
