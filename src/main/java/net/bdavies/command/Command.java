package net.bdavies.command;

import lombok.Getter;
import net.bdavies.Application;
import net.bdavies.Strip;

import java.lang.reflect.ParameterizedType;

public abstract class Command<T> {

    @Getter
    protected final Class<T> dataClass;

    public Command() {
        //noinspection unchecked
        this.dataClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    public abstract boolean run(Application application, Strip strip, T data);
}
