package dev.xulu.settings;

/**
 * @author Elementars
 * @since 7/23/2020 - 6:17 PM
 */
public class OnChangedValue<T> {

    private final T _old;
    private final T _new;

    public OnChangedValue(T _old, T _new) {
        this._old = _old;
        this._new = _new;
    }

    public T getOld() {
        return _old;
    }

    public T getNew() {
        return _new;
    }
}
