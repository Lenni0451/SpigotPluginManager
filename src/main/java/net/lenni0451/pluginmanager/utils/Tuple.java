package net.lenni0451.pluginmanager.utils;

public class Tuple<A, B> {

    private final A a;
    private final B b;

    public Tuple(final A a, final B b) {
        this.a = a;
        this.b = b;
    }

    public A getA() {
        return this.a;
    }

    public B getB() {
        return this.b;
    }

}
