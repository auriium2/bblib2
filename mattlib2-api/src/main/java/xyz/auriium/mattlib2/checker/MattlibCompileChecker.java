package xyz.auriium.mattlib2.checker;

import yuukonstants.exception.ExceptionUtil;
import yuukonstants.exception.ExplainedException;

import java.util.*;

public class MattlibCompileChecker {

    final CompileCheck[] checks;

    public MattlibCompileChecker(CompileCheck[] checks) {
        this.checks = checks;
    }

    public Optional<ExplainedException[]> compileAllDetectedProblems() {

        List<ExplainedException> explainedExceptions = new ArrayList<>();

        for (CompileCheck check : checks) {
            check.listDetectedExceptions().ifPresent(exs -> {
                explainedExceptions.addAll(List.of(exs));
            });
        }

        if (explainedExceptions.size() == 0) return Optional.empty();
        return Optional.of(explainedExceptions.toArray(ExplainedException[]::new));
    }

    public void throwAllDetectedProblems() {
        compileAllDetectedProblems().ifPresent(exs -> {

            for (ExplainedException e : exs) {
                ExceptionUtil.wrapExceptionalRunnable(() -> {throw e;}).run();
            }

        });
    }


}
