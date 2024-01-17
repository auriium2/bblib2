package xyz.auriium.mattlib2.checker;


import xyz.auriium.yuukonstants.exception.ExplainedException;

import java.util.Optional;

public interface CompileCheck {

    Optional<ExplainedException[]> listDetectedExceptions();

}
