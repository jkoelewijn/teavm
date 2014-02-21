/*
 *  Copyright 2014 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.util;

import java.util.Arrays;
import org.teavm.classlib.java.io.TSerializable;
import org.teavm.classlib.java.lang.*;

/**
 *
 * @author Alexey Andreev <konsoletyper@gmail.com>
 */
public class TArrayList<E> extends TAbstractList<E> implements TCloneable, TSerializable {
    private E[] array;
    private int size;

    public TArrayList() {
        this(10);
    }

    @SuppressWarnings("unchecked")
    public TArrayList(int initialCapacity) {
        array = (E[])new Object[initialCapacity];
    }

    public TArrayList(TCollection<? extends E> c) {
        this(c.size());
        TIterator<? extends E> iter = c.iterator();
        for (int i = 0; i < array.length; ++i) {
            array[i] = iter.next();
        }
    }

    public void trimToSize() {
        array = Arrays.copyOf(array, size);
    }

    public void ensureCapacity(int minCapacity) {
        if (array.length < minCapacity) {
            array = TArrays.copyOf(array, array.length + TMath.min(5, array.length / 2));
        }
    }

    @Override
    public E get(int index) {
        checkIndex(index);
        return array[index];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public TObject clone() {
        return new TArrayList<>(this);
    }

    @Override
    public E set(int index, E element) {
        checkIndex(index);
        E old = array[index];
        array[index] = element;
        return old;
    }

    @Override
    public void add(int index, E element) {
        checkIndexForAdd(index);
        ensureCapacity(size + 1);
        for (int i = size; i > index; --i) {
            array[i] = array[i - 1];
        }
        array[index] = element;
        ++size;
        ++modCount;
    }

    @Override
    public E remove(int index) {
        checkIndex(index);
        E old = array[index];
        --size;
        for (int i = index; i < size; ++i) {
            array[i] = array[i + 1];
        }
        array[size] = null;
        ++modCount;
        return old;
    }

    @Override
    public boolean remove(Object o) {
        int index = indexOf(o);
        if (index >= 0) {
            remove(index);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void clear() {
        Arrays.fill(array, 0, size, null);
        size = 0;
    }

    @Override
    public boolean addAll(int index, TCollection<? extends E> c) {
        checkIndexForAdd(index);
        if (c.isEmpty()) {
            return false;
        }
        ensureCapacity(size + c.size());
        int gap = c.size();
        size += gap;
        for (int i = gap - 1; i > index; --i) {
            array[i] = array[i - gap];
        }
        TIterator<? extends E> iter = c.iterator();
        for (int i = 0; i < gap; ++i) {
            array[index++] = iter.next();
        }
        ++modCount;
        return true;
    }

    @Override
    protected void removeRange(int start, int end) {
        if (start > end) {
            throw new TIllegalArgumentException();
        }
        if (start < 0 || end > size) {
            throw new TIndexOutOfBoundsException();
        }
        if (start == end) {
            return;
        }
        for (int i = end; i < size; ++i) {
            array[start++] = array[end++];
        }
        Arrays.fill(array, start, end, null);
        size -= end - start;
        ++modCount;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new TIndexOutOfBoundsException();
        }
    }

    private void checkIndexForAdd(int index) {
        if (index < 0 || index > size) {
            throw new TIndexOutOfBoundsException();
        }
    }
}
