package xyz.auriium.mattlib2.log;

import xyz.auriium.mattlib2.Mattlib2Exception;

public class Exceptions {

    public static Mattlib2Exception TOO_MANY_ANNOTATIONS(String methodName, String className) {
        return new Mattlib2Exception(
                "tooManyAnnotations",
                String.format("too many mattlib2 annotations on method %s in class %s ", methodName, className),
                "please use only one of @log, @tune, or @conf on it"
        );
    }

    public static Mattlib2Exception NO_ANNOTATIONS_ON_METHOD(String methodName, String className) {
        return new Mattlib2Exception(
                "noAnnotationOnMethod",
                String.format("no mattlib2 annotations on method %s in class %s ", methodName, className),
                "please use one of @log, @tune, or @conf on it"
        );
    }

    public static Mattlib2Exception BAD_CONF_OR_TUNE(String methodName, String className) {
        return new Mattlib2Exception(
                "badConfOrTune",
                String.format("the method %s in class %s cannot have parameters", methodName, className),
                "please remove parameters or do not declare it as @conf or @tune"
        );
    }

    public static Mattlib2Exception BAD_LOG(String methodName, String className) {
        return new Mattlib2Exception(
                "badLog",
                String.format("method %s in class %s must have parameters", methodName, className),
                "please add parameters or do not declare @log"
        );
    }

    public static Mattlib2Exception BAD_RETURN_TYPE(String methodName, String className) {
        return new Mattlib2Exception(
                "badReturnType",
                String.format("return type of method %s in class %s is not void, but is log", methodName, className),
                "please set the return type to void"
        );
    }

    public static Mattlib2Exception BAD_LOG_TYPE(String methodName, String className, String unsupportedTypeName) {
        return new Mattlib2Exception(
                "badLogType",
                String.format("method %s on class %s logs type %s, but mattlog2 does not support that type!", methodName, className, unsupportedTypeName),
                "contact matt to add it or modify LogComponentManipulator.java or MutantComponentManipulator.java"
        );
    }

}
