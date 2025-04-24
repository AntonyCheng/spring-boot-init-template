package top.sharehome.springbootinittemplate.model.common;

import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

import java.io.Serial;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 多元组
 *
 * @author AntonyCheng
 */
public class TupleN<T> extends AbstractList<T> implements Serializable, Cloneable {

    private final T[] elements;

    @SafeVarargs
    public TupleN(final T... elements) {
        this.elements = Objects.requireNonNull(elements, "Tuple elements is null");
    }

    public TupleN(TupleN<T> tupleN) {
        this.elements = tupleN.elements;
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

    public List<T> toList() {
        return Arrays.asList(elements);
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
        return new TupleN<>(newElements);
    }

    public TupleN<T> subTuple(int fromIndex, int toIndex) {
        return subTuple(fromIndex, toIndex);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TupleN that)) {
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
        return Objects.hash((Object) elements);
    }

    @Override
    public String toString() {
        return Arrays.toString(toArray());
    }

    @Override
    public TupleN<T> clone() {
        return new TupleN<>(this);
    }

    @Serial
    private static final long serialVersionUID = 5675163051060227215L;

}
