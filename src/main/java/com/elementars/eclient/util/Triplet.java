package com.elementars.eclient.util;

/**
 * @author Elementars
 */
public class Triplet<T, S, U> {
    T first;
    S second;
    U third;

    public Triplet(T first, S second, U third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public T getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    public U getThird() {
        return third;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public void setSecond(S second) {
        this.second = second;
    }

    public void setThird(U third) {
        this.third = third;
    }
}
