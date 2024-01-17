package xyz.auriium.mattlib2;

import xyz.auriium.mattlib2.checker.CompileCheck;
import xyz.auriium.yuukonstants.exception.ExceptionUtil;
import xyz.auriium.yuukonstants.exception.ExplainedException;

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
