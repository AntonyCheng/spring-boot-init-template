package top.sharehome.springbootinittemplate.model.common;

import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

import java.io.Serial;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

/**
 * 多元组
 *
 * @author AntonyCheng
 */
public class Tuple<T> extends AbstractList<T> implements Serializable, Cloneable, Comparable<Tuple<T>> {

    private final T[] elements;

    @SafeVarargs
    public Tuple(T... elements) {
        if (elements == null) {
            throw new NullPointerException("Tuple elements is null");
        }
        this.elements = elements;
    }

    public Tuple(Tuple<T> tuple) {
        this.elements = tuple.elements;
    }

    @Override
    public T get(int index) {
        return elements[index];
    }

    public T getOrDefault(int index, T defaultValue) {
        return index >= elements.length ? defaultValue : elements[index];
    }

    @Override
    public int size() {
        return elements.length;
    }

    @Override
    public T[] toArray() {
        return elements;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> subList(int fromIndex, int toIndex) {
        int size = toIndex - fromIndex;
        T[] newElements = (T[]) new Object[size];
        System.arraycopy(elements, fromIndex, newElements, 0, size);
        return new Tuple<>(newElements);
    }

    public Tuple<T> subTuple(int fromIndex, int toIndex) {
        return subTuple(fromIndex, toIndex);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tuple that)) {
            return false;
        }
        int size = size();
        if (size != that.size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!DefaultTypeTransformation.compareEqual(get(i), that.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements);
    }

    @Override
    public int compareTo(Tuple<T> o) {
        int thisSize = this.size();
        int otherSize = o.size();

        for (int i = 0, n = Math.min(thisSize, otherSize); i < n; i++) {
            int result = compare(this.get(i), o.get(i));
            if (result != 0) {
                return result;
            }
        }
        return Integer.compare(thisSize, otherSize);
    }

    @SuppressWarnings("unchecked")
    private static <T> int compare(T tuple1, T tuple2) {
        return tuple1 == null && tuple2 == null ?
                0 : tuple1 == null ?
                1 : tuple1 instanceof Comparable ?
                ((Comparable<T>) tuple1).compareTo(tuple2) : DefaultTypeTransformation.compareEqual(tuple1, tuple2) ?
                0 : 1;
    }

    @Serial
    private static final long serialVersionUID = 5675163051060227215L;

    @Override
    public Tuple<T> clone() {
        return new Tuple<>(this);
    }

}
