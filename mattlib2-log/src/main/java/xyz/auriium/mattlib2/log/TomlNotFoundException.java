package xyz.auriium.mattlib2.log;

import yuukonfig.core.err.BadValueException;
import yuukonstants.GenericPath;

public class TomlNotFoundException extends BadValueException {
    public TomlNotFoundException(String config, GenericPath path) {
        super("a value is expected at this position, but none could be found!", "add some data under this value!", config, path);
    }
}
