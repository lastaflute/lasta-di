/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.lastaflute.di.util;

import java.io.Serializable;

/**
 * @author modified by jflute (originated in Seasar)
 */
public class SStringBuilder implements Serializable, CharSequence {

    private static final long serialVersionUID = 1L;

    protected char value[];

    protected int count;

    public SStringBuilder() {
        this(16);
    }

    public SStringBuilder(final int capacity) {
        value = new char[capacity];
    }

    /**
     * @param str
     * @see java.lang.StringBuilder#StringBuilder(String)
     */
    public SStringBuilder(final String str) {
        this(str.length() + 16);
        append(str);
    }

    /**
     * @param seq
     *            character sequence
     * @see java.lang.StringBuilder#StringBuilder(CharSequence)
     */
    public SStringBuilder(final CharSequence seq) {
        this(seq.length() + 16);
        append(seq);
    }

    /**
     * @param b
     *            boolean
     * @see java.lang.StringBuilder#StringBuilder(boolean)
     */
    public SStringBuilder append(final boolean b) {
        if (b) {
            final int newCount = count + 4;
            if (newCount > value.length) {
                expandCapacity(newCount);
            }
            value[count++] = 't';
            value[count++] = 'r';
            value[count++] = 'u';
            value[count++] = 'e';
        } else {
            final int newCount = count + 5;
            if (newCount > value.length) {
                expandCapacity(newCount);
            }
            value[count++] = 'f';
            value[count++] = 'a';
            value[count++] = 'l';
            value[count++] = 's';
            value[count++] = 'e';
        }
        return this;
    }

    /**
     * @param c
     *            char
     * @see java.lang.StringBuilder#append(char)
     */
    public SStringBuilder append(final char c) {
        final int newCount = count + 1;
        if (newCount > value.length) {
            expandCapacity(newCount);
        }
        value[count++] = c;
        return this;
    }

    /**
     * @param str
     * @return 
     * @see java.lang.StringBuilder#append(char[])
     */
    public SStringBuilder append(final char[] str) {
        final int newCount = count + str.length;
        if (newCount > value.length) {
            expandCapacity(newCount);
        }
        System.arraycopy(str, 0, value, count, str.length);
        count = newCount;
        return this;
    }

    /**
     * @param str
     * @param offset
     * @param len
     * @return 
     * @see java.lang.StringBuilder#append(char[], int, int)
     */
    public SStringBuilder append(final char[] str, final int offset, final int len) {
        final int newCount = count + len;
        if (newCount > value.length) {
            expandCapacity(newCount);
        }
        System.arraycopy(str, offset, value, count, len);
        count = newCount;
        return this;
    }

    /**
     * @param d
     * @return 
     * @see java.lang.StringBuilder#append(double)
     */
    public SStringBuilder append(final double d) {
        append(String.valueOf(d));
        return this;
    }

    /**
     * @param f
     *            float
     * @return 
     * @see java.lang.StringBuilder#append(float)
     */
    public SStringBuilder append(final float f) {
        append(String.valueOf(f));
        return this;
    }

    /**
     * @param i
     *            int
     * @return 
     * @see java.lang.StringBuilder#append(int)
     */
    public SStringBuilder append(final int i) {
        append(String.valueOf(i));
        return this;
    }

    /**
     * @param s
     *            char Sequence
     * @return 
     * @see java.lang.StringBuilder#append(CharSequence)
     */
    public SStringBuilder append(CharSequence s) {
        if (s == null) {
            s = "null";
        }
        if (s instanceof String) {
            return this.append((String) s);
        }
        if (s instanceof StringBuffer) {
            return this.append((StringBuffer) s);
        }
        if (s instanceof SStringBuilder) {
            return this.append((SStringBuilder) s);
        }
        return append(s, 0, s.length());
    }

    /**
     * @param s
     * @param start
     * @param end
     * @return 
     * @see java.lang.StringBuilder#append(CharSequence, int, int)
     */
    public SStringBuilder append(CharSequence s, final int start, final int end) {
        if (s == null) {
            s = "null";
        }
        if ((start < 0) || (end < 0) || (start > end) || (end > s.length())) {
            throw new IndexOutOfBoundsException("start " + start + ", end " + end + ", s.length() " + s.length());
        }
        final int len = end - start;
        if (len == 0) {
            return this;
        }
        final int newCount = count + len;
        if (newCount > value.length) {
            expandCapacity(newCount);
        }
        for (int i = start; i < end; i++) {
            value[count++] = s.charAt(i);
        }
        count = newCount;
        return this;
    }

    /**
     * @param obj
     * @return 
     * @see java.lang.StringBuilder#append(Object)
     */
    public SStringBuilder append(final Object obj) {
        return append(String.valueOf(obj));
    }

    /**
     * @param str
     * @return 
     * @see java.lang.StringBuilder#append(String)
     */
    public SStringBuilder append(String str) {
        if (str == null) {
            str = "null";
        }
        final int len = str.length();
        if (len == 0) {
            return this;
        }
        final int newCount = count + len;
        if (newCount > value.length) {
            expandCapacity(newCount);
        }
        str.getChars(0, len, value, count);
        count = newCount;
        return this;
    }

    /**
     * @param sb
     *            {@link StringBuffer}
     * @return 
     * @see java.lang.StringBuilder#append(StringBuffer)
     */
    public SStringBuilder append(final StringBuffer sb) {
        if (sb == null) {
            return append("null");
        }
        final int len = sb.length();
        final int newCount = count + len;
        if (newCount > value.length) {
            expandCapacity(newCount);
        }
        sb.getChars(0, len, value, count);
        count = newCount;
        return this;
    }

    /**
     * @param l
     *            long
     * @return 
     * @see java.lang.StringBuilder#append(long)
     */
    public SStringBuilder append(final long l) {
        append(String.valueOf(l));
        return this;
    }

    /**
     * @return 
     * @see java.lang.StringBuilder#capacity()
     */
    public int capacity() {
        return value.length;
    }

    /**
     * @return 
     * @see java.lang.StringBuilder#charAt(int)
     */
    public char charAt(final int index) {
        if ((index < 0) || (index >= count)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return value[index];
    }

    /**
     * @param start
     * @param end
     * @return 
     * @see java.lang.StringBuilder#delete(int, int)
     */
    public SStringBuilder delete(final int start, int end) {
        if (start < 0) {
            throw new StringIndexOutOfBoundsException(start);
        }
        if (end > count) {
            end = count;
        }
        if (start > end) {
            throw new StringIndexOutOfBoundsException();
        }
        final int len = end - start;
        if (len > 0) {
            System.arraycopy(value, start + len, value, start, count - end);
            count -= len;
        }
        return this;
    }

    /**
     * @param index
     * @return 
     * @see java.lang.StringBuilder#deleteCharAt(int)
     */
    public SStringBuilder deleteCharAt(final int index) {
        if ((index < 0) || (index >= count)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        System.arraycopy(value, index + 1, value, index, count - index - 1);
        count--;
        return this;
    }

    /**
     * @param minimumCapacity
     * @see java.lang.StringBuilder#ensureCapacity(int)
     */
    public void ensureCapacity(final int minimumCapacity) {
        if (minimumCapacity > value.length) {
            expandCapacity(minimumCapacity);
        }
    }

    /**
     * @param srcBegin
     * @param srcEnd
     * @param dst
     * @param dstBegin
     * @see java.lang.StringBuilder#getChars(int, int, char[], int)
     */
    public void getChars(final int srcBegin, final int srcEnd, final char[] dst, final int dstBegin) {
        if (srcBegin < 0) {
            throw new StringIndexOutOfBoundsException(srcBegin);
        }
        if ((srcEnd < 0) || (srcEnd > count)) {
            throw new StringIndexOutOfBoundsException(srcEnd);
        }
        if (srcBegin > srcEnd) {
            throw new StringIndexOutOfBoundsException("srcBegin > srcEnd");
        }
        System.arraycopy(value, srcBegin, dst, dstBegin, srcEnd - srcBegin);
    }

    /**
     * @param str
     * @return 
     * @see java.lang.StringBuilder#indexOf(String)
     */
    public int indexOf(final String str) {
        return indexOf(str, 0);
    }

    /**
     * @param str
     * @param fromIndex
     * @return 
     * @see java.lang.StringBuilder#indexOf(String, int)
     */
    public int indexOf(final String str, final int fromIndex) {
        return indexOf(value, 0, count, str.toCharArray(), 0, str.length(), fromIndex);
    }

    /**
     * @param offset
     * @param b
     *            boolean
     * @return 
     * @see java.lang.StringBuilder#insert(int, boolean)
     */
    public SStringBuilder insert(final int offset, final boolean b) {
        return insert(offset, String.valueOf(b));
    }

    /**
     * @param offset
     * @param c
     * @return 
     * @see java.lang.StringBuilder#insert(int, char)
     */
    public SStringBuilder insert(final int offset, final char c) {
        final int newCount = count + 1;
        if (newCount > value.length) {
            expandCapacity(newCount);
        }
        System.arraycopy(value, offset, value, offset + 1, count - offset);
        value[offset] = c;
        count = newCount;
        return this;
    }

    /**
     * @param offset
     * @param str
     * @return 
     * @see java.lang.StringBuilder#insert(int, char[])
     */
    public SStringBuilder insert(final int offset, final char[] str) {
        if ((offset < 0) || (offset > length())) {
            throw new StringIndexOutOfBoundsException(offset);
        }
        final int len = str.length;
        final int newCount = count + len;
        if (newCount > value.length) {
            expandCapacity(newCount);
        }
        System.arraycopy(value, offset, value, offset + len, count - offset);
        System.arraycopy(str, 0, value, offset, len);
        count = newCount;
        return this;
    }

    /**
     * @param index
     * @param str
     * @param offset
     * @param len
     * @return 
     * @see java.lang.StringBuilder#insert(int, char[], int, int)
     */
    public SStringBuilder insert(final int index, final char[] str, final int offset, final int len) {
        if ((index < 0) || (index > length())) {
            throw new StringIndexOutOfBoundsException(index);
        }
        if ((offset < 0) || (len < 0) || (offset > str.length - len)) {
            throw new StringIndexOutOfBoundsException("offset " + offset + ", len " + len + ", str.length " + str.length);
        }
        final int newCount = count + len;
        if (newCount > value.length) {
            expandCapacity(newCount);
        }
        System.arraycopy(value, index, value, index + len, count - index);
        System.arraycopy(str, offset, value, index, len);
        count = newCount;
        return this;
    }

    /**
     * @param offset
     * @param d
     * @return 
     * @see java.lang.StringBuilder#insert(int, double)
     */
    public SStringBuilder insert(final int offset, final double d) {
        return insert(offset, String.valueOf(d));
    }

    /**
     * @param offset
     * @param f
     *            float
     * @return 
     * @see java.lang.StringBuilder#insert(int, float)
     */
    public SStringBuilder insert(final int offset, final float f) {
        return insert(offset, String.valueOf(f));
    }

    /**
     * @param offset
     * @param i
     *            int
     * @return 
     * @see java.lang.StringBuilder#insert(int, int)
     */
    public SStringBuilder insert(final int offset, final int i) {
        return insert(offset, String.valueOf(i));
    }

    /**
     * @param dstOffset
     * @param s
     *            character sequence
     * @return 
     * @see java.lang.StringBuilder#insert(int, CharSequence)
     */
    public SStringBuilder insert(final int dstOffset, CharSequence s) {
        if (s == null) {
            s = "null";
        }
        if (s instanceof String) {
            return this.insert(dstOffset, (String) s);
        }
        return this.insert(dstOffset, s, 0, s.length());
    }

    /**
     * @param dstOffset
     * @param s
     *            character sequence
     * @param start
     * @param end
     * @return 
     * @see java.lang.StringBuilder#insert(int, CharSequence, int, int)
     */
    public SStringBuilder insert(int dstOffset, CharSequence s, final int start, final int end) {
        if (s == null) {
            s = "null";
        }
        if ((dstOffset < 0) || (dstOffset > length())) {
            throw new IndexOutOfBoundsException("dstOffset " + dstOffset);
        }
        if ((start < 0) || (end < 0) || (start > end) || (end > s.length())) {
            throw new IndexOutOfBoundsException("start " + start + ", end " + end + ", s.length() " + s.length());
        }
        final int len = end - start;
        if (len == 0) {
            return this;
        }
        final int newCount = count + len;
        if (newCount > value.length) {
            expandCapacity(newCount);
        }
        System.arraycopy(value, dstOffset, value, dstOffset + len, count - dstOffset);
        for (int i = start; i < end; i++) {
            value[dstOffset++] = s.charAt(i);
        }
        count = newCount;
        return this;
    }

    /**
     * @param offset
     * @param obj
     * @return 
     * @see java.lang.StringBuilder#insert(int, Object)
     */
    public SStringBuilder insert(final int offset, final Object obj) {
        return insert(offset, String.valueOf(obj));
    }

    /**
     * @param offset
     * @param str
     * @return 
     * @see java.lang.StringBuilder#insert(int, String)
     */
    public SStringBuilder insert(final int offset, String str) {
        if ((offset < 0) || (offset > length())) {
            throw new StringIndexOutOfBoundsException(offset);
        }
        if (str == null) {
            str = "null";
        }
        final int len = str.length();
        final int newCount = count + len;
        if (newCount > value.length) {
            expandCapacity(newCount);
        }
        System.arraycopy(value, offset, value, offset + len, count - offset);
        str.getChars(0, len, value, offset);
        count = newCount;
        return this;
    }

    /**
     * @param offset
     * @param l
     * @return 
     * @see java.lang.StringBuilder#insert(int, long)
     */
    public SStringBuilder insert(final int offset, final long l) {
        return insert(offset, String.valueOf(l));
    }

    /**
     * @param str
     * @return 
     * @see java.lang.StringBuilder#lastIndexOf(String)
     */
    public int lastIndexOf(final String str) {
        return lastIndexOf(str, count);
    }

    /**
     * @param str
     * @param fromIndex
     * @return 
     * @see java.lang.StringBuilder#lastIndexOf(String, int)
     */
    public int lastIndexOf(final String str, int fromIndex) {
        final char[] target = str.toCharArray();
        final int targetOffset = 0;
        final int targetCount = str.length();
        final int rightIndex = count - targetCount;
        if (fromIndex < 0) {
            return -1;
        }
        if (fromIndex > rightIndex) {
            fromIndex = rightIndex;
        }
        /* Empty string always matches. */
        if (targetCount == 0) {
            return fromIndex;
        }

        final int strLastIndex = targetOffset + targetCount - 1;
        final char strLastChar = target[strLastIndex];
        final int min = targetCount - 1;
        int i = min + fromIndex;

        startSearchForLastChar: while (true) {
            while (i >= min && value[i] != strLastChar) {
                i--;
            }
            if (i < min) {
                return -1;
            }
            int j = i - 1;
            final int start = j - (targetCount - 1);
            int k = strLastIndex - 1;

            while (j > start) {
                if (value[j--] != target[k--]) {
                    i--;
                    continue startSearchForLastChar;
                }
            }
            return start + 1;
        }
    }

    /**
     * @see java.lang.StringBuilder#length()
     */
    public int length() {
        return count;
    }

    /**
     * @param start
     * @param end
     * @param str
     * @return 
     * @see java.lang.StringBuilder#replace(int, int, String)
     */
    public SStringBuilder replace(final int start, int end, final String str) {
        if (start < 0) {
            throw new StringIndexOutOfBoundsException(start);
        }
        if (start > count) {
            throw new StringIndexOutOfBoundsException("start > length()");
        }
        if (start > end) {
            throw new StringIndexOutOfBoundsException("start > end");
        }
        if (end > count) {
            end = count;
        }

        if (end > count) {
            end = count;
        }
        final int len = str.length();
        final int newCount = count + len - (end - start);
        if (newCount > value.length) {
            expandCapacity(newCount);
        }

        System.arraycopy(value, end, value, start + len, count - end);
        str.getChars(0, str.length(), value, start);
        count = newCount;
        return this;
    }

    /**
     * @return 
     * 
     * @see java.lang.StringBuilder#reverse()
     */
    public SStringBuilder reverse() {
        int j = count - 1;
        for (int i = 0; i < j; ++i, --j) {
            final char temp = value[i];
            value[i] = value[j];
            value[j] = temp;
        }
        return this;
    }

    /**
     * @param index
     * @param ch
     *            char
     * @see java.lang.StringBuilder#setCharAt(int, char)
     */
    public void setCharAt(final int index, final char ch) {
        if ((index < 0) || (index >= count)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        value[index] = ch;
    }

    /**
     * @param newLength
     * @see java.lang.StringBuilder#setLength(int)
     */
    public void setLength(final int newLength) {
        if (newLength < 0) {
            throw new StringIndexOutOfBoundsException(newLength);
        }
        if (newLength > value.length) {
            expandCapacity(newLength);
        }

        if (count < newLength) {
            for (; count < newLength; count++) {
                value[count] = '\0';
            }
        } else {
            count = newLength;
        }
    }

    /**
     * @see java.lang.StringBuilder#subSequence(int, int)
     */
    public CharSequence subSequence(final int start, final int end) {
        return substring(start, end);
    }

    /**
     * @param start
     * @return 
     * @see java.lang.StringBuilder#substring(int)
     */
    public String substring(final int start) {
        return substring(start, count);
    }

    /**
     * @param start
     * @param end
     * @return 
     * @see java.lang.StringBuilder#substring(int, int)
     */
    public String substring(final int start, final int end) {
        if (start < 0) {
            throw new StringIndexOutOfBoundsException(start);
        }
        if (end > count) {
            throw new StringIndexOutOfBoundsException(end);
        }
        if (start > end) {
            throw new StringIndexOutOfBoundsException(end - start);
        }
        return new String(value, start, end - start);
    }

    /**
     * @see java.lang.StringBuilder#toString()
     */
    public String toString() {
        return new String(value, 0, count);
    }

    /**
     * @see java.lang.StringBuilder#trimToSize()
     */
    public void trimToSize() {
        if (count < value.length) {
            final char[] newValue = new char[count];
            System.arraycopy(value, 0, newValue, 0, count);
            value = newValue;
        }
    }

    void expandCapacity(final int minimumCapacity) {
        int newCapacity = (value.length + 1) * 2;
        if (newCapacity < 0) {
            newCapacity = Integer.MAX_VALUE;
        } else if (minimumCapacity > newCapacity) {
            newCapacity = minimumCapacity;
        }
        final char newValue[] = new char[newCapacity];
        System.arraycopy(value, 0, newValue, 0, count);
        value = newValue;
    }

    final char[] getValue() {
        return value;
    }

    static int indexOf(final char[] source, final int sourceOffset, final int sourceCount, final char[] target, final int targetOffset,
            final int targetCount, int fromIndex) {
        if (fromIndex >= sourceCount) {
            return (targetCount == 0 ? sourceCount : -1);
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (targetCount == 0) {
            return fromIndex;
        }

        final char first = target[targetOffset];
        final int max = sourceOffset + (sourceCount - targetCount);

        for (int i = sourceOffset + fromIndex; i <= max; i++) {
            /* Look for first character. */
            if (source[i] != first) {
                while (++i <= max && source[i] != first) {}
            }

            /* Found first character, now look at the rest of v2 */
            if (i <= max) {
                int j = i + 1;
                final int end = j + targetCount - 1;
                for (int k = targetOffset + 1; j < end && source[j] == target[k]; j++, k++) {}

                if (j == end) {
                    /* Found whole string. */
                    return i - sourceOffset;
                }
            }
        }
        return -1;
    }

    private SStringBuilder append(final SStringBuilder sb) {
        if (sb == null) {
            return append("null");
        }
        final int len = sb.length();
        final int newcount = count + len;
        if (newcount > value.length) {
            expandCapacity(newcount);
        }
        sb.getChars(0, len, value, count);
        count = newcount;
        return this;
    }

    private void writeObject(final java.io.ObjectOutputStream s) throws java.io.IOException {
        s.defaultWriteObject();
        s.writeInt(count);
        s.writeObject(value);
    }

    private void readObject(final java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
        s.defaultReadObject();
        count = s.readInt();
        value = (char[]) s.readObject();
    }

}
