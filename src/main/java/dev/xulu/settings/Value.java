package dev.xulu.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.elementars.eclient.Xulu;
import com.elementars.eclient.command.Command;
import com.elementars.eclient.module.Module;

/**
 * @author Elementars
 */
public class Value<T> {
    private String name;
    private Module parent;
    private Mode mode;

    enum Mode {
        UNKNOWN,
        MODE,
        ENUM,
        TOGGLE,
        NUMBER,
        BIND,
        TEXT
    }

    private T value;
    private ArrayList<T> options;

    private T min;
    private T max;

    private Consumer<OnChangedValue<T>> changeTask = null;
    private Predicate<T> visibleCheck = null;
    private Predicate<T> filter = null;
    private String filterError = null;

    public Value(String name, Module parent, T value, ArrayList<T> options){
        this.name = name;
        this.parent = parent;
        this.value = value;
        this.options = options;
        this.mode = Mode.MODE;
    }

    public Value(String name, Module parent, T value, T[] options){
        this.name = name;
        this.parent = parent;
        this.value = value;
        this.options = new ArrayList<>(Arrays.asList(options));
        this.mode = Mode.MODE;
        if (value instanceof Enum) {
            this.mode = Mode.ENUM;
        }
    }

    public Value(String name, Module parent, T value){
        this.name = name;
        this.parent = parent;
        this.value = value;
        this.mode = Mode.UNKNOWN;
        if (value instanceof Boolean) {
            this.mode = Mode.TOGGLE;
        } else if (value instanceof Bind) {
            this.mode = Mode.BIND;
        } else if (value instanceof TextBox) {
            this.mode = Mode.TEXT;
        }
    }

    public Value(String name, Module parent, T value, T min, T max){
        this.name = name;
        this.parent = parent;
        this.value = value;
        this.min = min;
        this.max = max;
        this.mode = Mode.NUMBER;
    }

    public String getName(){
        return name;
    }

    public Module getParentMod(){
        return parent;
    }

    public ArrayList<T> getOptions(){
        return this.options;
    }

    public String getCorrectString(String stringIn) {
        if (this.value instanceof String) {
            for (String s : (ArrayList<String>) options) {
                if (s.equalsIgnoreCase(stringIn)) return s;
            }
            return null;
        }
        else if (mode == Mode.ENUM) {
            for (T s : options) {
                if (s.toString().equalsIgnoreCase(stringIn)) return Xulu.getTitle(s.toString());
            }
            return null;
        }
        return null;
    }

    public T getCorrectOption(String stringIn) {
        if (mode == Mode.ENUM) {
            for (T s : options) {
                if (s.toString().equalsIgnoreCase(stringIn)) return s;
            }
            return null;
        }
        return null;
    }
    
    public void setEnumValue(String value) {
        for (Enum e : ((Enum) this.value).getClass().getEnumConstants()) {
            if (e.name().equalsIgnoreCase(value)) {
                T old = this.value;
                this.value = (T) e;
                if (changeTask != null) {
                    changeTask.accept(new OnChangedValue<>(old, (T) e));
                }
            }
        }
    }

    public T getMax() {
        return max;
    }

    public T getMin() {
        return min;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        if (this.value != value) {
            if (filter != null && !filter.test(value)) {
                if (filterError != null) {
                    Command.sendChatMessage("&c" + filterError);
                }
                return;
            }
            T old = this.value;
            this.value = value;
            if (changeTask != null) {
                changeTask.accept(new OnChangedValue<>(old, value));
            }
        }
    }

    public boolean isMode(){
        return mode == Mode.MODE;
    }

    public boolean isToggle(){
        return mode == Mode.TOGGLE;
    }

    public boolean isNumber(){
        return mode == Mode.NUMBER;
    }

    public boolean isEnum(){
        return mode == Mode.ENUM;
    }

    public boolean isBind() {
        return mode == Mode.BIND;
    }

    public boolean isText() {
        return mode == Mode.TEXT;
    }

    public Value<T> onChanged(Consumer<OnChangedValue<T>> run) {
        this.changeTask = run;
        return this;
    }

    public Value<T> visibleWhen(Predicate<T> predicate) {
        this.visibleCheck = predicate;
        return this;
    }

    public Value<T> newValueFilter(Predicate<T> predicate) {
        this.filter = predicate;
        return this;
    }
    public Value<T> withFilterError(String s) {
        this.filterError = s;
        return this;
    }

    public boolean isVisible() {
        if (visibleCheck == null) return true;
        return visibleCheck.test(value);
    }
}
