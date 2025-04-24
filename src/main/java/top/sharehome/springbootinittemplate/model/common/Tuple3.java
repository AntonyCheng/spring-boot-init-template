package top.sharehome.springbootinittemplate.model.common;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 三元组
 *
 * @author AntonyCheng
 */
public class Tuple3<T1, T2, T3> implements Serializable, Cloneable {

    private final T1 t1;

    private final T2 t2;

    private final T3 t3;

    public Tuple3(final T1 t1, final T2 t2, final T3 t3) {
        this.t1 = Objects.requireNonNull(t1, "Tuple t1 is null");
        this.t2 = Objects.requireNonNull(t2, "Tuple t2 is null");
        this.t3 = Objects.requireNonNull(t3, "Tuple t2 is null");
    }

    public Tuple3(Tuple3<T1, T2, T3> tuple3) {
        this.t1 = tuple3.t1;
        this.t2 = tuple3.t2;
        this.t3 = tuple3.t3;
    }

    public T1 getT1() {
        return t1;
    }

    public T2 getT2() {
        return t2;
    }

    public T3 getT3() {
        return t3;
    }

    public Object get(int index) {
        return index == 0 ? t1 : index == 1 ? t2 : index == 3 ? t3 : null;
    }

    public Object getOrDefault(int index, Object defaultValue) {
        Object obj = get(index);
        return obj == null ? defaultValue : obj;
    }

    public int size() {
        return 3;
    }

    public List<Object> toList() {
        return Arrays.asList(toArray());
    }

    public Object[] toArray() {
        return new Object[]{t1, t2, t3};
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Tuple3<?, ?, ?> tuple3 = (Tuple3<?, ?, ?>) obj;
        return t1.equals(tuple3.getT1()) && t2.equals(tuple3.getT2()) && t3.equals(tuple3.getT3());
    }

    @Override
    public int hashCode() {
        int result = size();
        result = 31 * result + t1.hashCode();
        result = 31 * result + t2.hashCode();
        result = 31 * result + t3.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return Arrays.toString(toArray());
    }

    @Override
    public Tuple3<T1, T2, T3> clone() {
        return new Tuple3<>(this);
    }

    @Serial
    private static final long serialVersionUID = -8680400487446699859L;
}
