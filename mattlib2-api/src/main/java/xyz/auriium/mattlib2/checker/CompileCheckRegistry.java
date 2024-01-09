package xyz.auriium.mattlib2.checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompileCheckRegistry {

    final List<CompileCheck> selfMap = new ArrayList<>();

    public CompileCheckRegistry register(CompileCheck check) {
        selfMap.add(check);

        return this;
    }

    public CompileCheck[] gimme() {
        return selfMap.toArray(CompileCheck[]::new);
    }

}
