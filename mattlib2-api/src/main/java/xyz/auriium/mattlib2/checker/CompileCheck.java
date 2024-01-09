package xyz.auriium.mattlib2.checker;

import yuukonstants.exception.ExplainedException;

import java.util.Optional;

public interface CompileCheck {

    Optional<ExplainedException[]> listDetectedExceptions();

}
