package xyz.auriium.mattlib.ctre;

import com.ctre.phoenix6.StatusCode;
import xyz.auriium.mattlib2.Mattlib2Exception;
import xyz.auriium.mattlib2.log.ProcessPath;
import xyz.auriium.yuukonstants.GenericPath;
import xyz.auriium.yuukonstants.exception.ExplainedException;

import static java.lang.String.format;

public class Exceptions {



    public static ExplainedException CTRE_ERROR(StatusCode code, GenericPath path) {
        throw new Mattlib2Exception(
                "ctreError",
                format("The ctre library threw an error with status [%s] and status code [%s] when configuring motor [%s]", code.getName(), code.value, path.tablePath()),
                format("The ctre library says that error is described as: [%s]. Perhaps a solution can be found there?", code.getDescription())
        );
    }
}
