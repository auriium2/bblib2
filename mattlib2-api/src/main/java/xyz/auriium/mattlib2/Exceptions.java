package xyz.auriium.mattlib2;


import xyz.auriium.yuukonstants.GenericPath;

import static java.lang.String.format;

public class Exceptions {


    public static final Mattlib2Exception CONF_NOT_LOADED() {
        throw new Mattlib2Exception(
                "configNotLoaded",
                format("your code tried to access a component, but it was loaded after preInit was called and therefore wasn't loaded!"),
                "make sure you declare your component using MattLog before pre-init is called, usually in the static initializer block or as a static inline declaration. Note that due to how the classloader works you probably want to declare your components in the same class as the MattLog preinit call"
        );
    }

    public static final Mattlib2Exception MATTLIB_FILE_EXCEPTION(String fileType) {
        throw new Mattlib2Exception(
                "noConfigFile",
                format("for some reason the [%s] file for mattlib is not present!", fileType),
                "contact matt"
        );
    }

    public static final Mattlib2Exception DUPLICATE_IDS(GenericPath path) {
        throw new Mattlib2Exception(
                "duplicateIds",
                format("multiple config nodes/mattlib components share the name [%s].", path.tablePath()),
                "remove the duplicates"
        );
    }

    public static final Mattlib2Exception NODE_NOT_MAP(GenericPath path) {
        throw new Mattlib2Exception(
                "nodeNotMap",
                format("the node at position [%s] is expected to be a mapping-like, but it is not.", path.tablePath()),
                "contact matt"
        );
    }

    public static final Mattlib2Exception ALREADY_INITIALIZED() {
        return new Mattlib2Exception(
                "logAlreadyInitialized",
                "mattlib2 was already initialized, but you called loadWaiting or loadFuture on it after initialization!",
                "make sure all calls to loadWaiting or loadFuture happen before initialization. Just make your components static and nothing else!"
        );
    }

    public static Mattlib2Exception NOT_INITIALIZED_ON_TIME() {
        return new Mattlib2Exception(
                "guaranteeInit/notInitialized",
                "you made a variable into a guarantee init, but then tried to use it before filling that variable with data.",
                "make sure that the variable found at the line in the below stack trace is completed before it is used by others"
        );
    }

    public static Mattlib2Exception TOO_MANY_ANNOTATIONS(String methodName, String className) {
        return new Mattlib2Exception(
                "tooManyAnnotations",
                format("too many mattlib2 annotations on method %s in class %s ", methodName, className),
                "please use only one of @log, @tune, or @conf on it"
        );
    }

    public static Mattlib2Exception NO_ANNOTATIONS_ON_METHOD(String methodName, String className) {
        return new Mattlib2Exception(
                "noAnnotationOnMethod",
                format("no mattlib2 annotations on method %s in class %s ", methodName, className),
                "please use one of @log, @tune, or @conf on it"
        );
    }

    public static Mattlib2Exception BAD_CONF_OR_TUNE(String methodName, String className) {
        return new Mattlib2Exception(
                "badConfOrTune",
                format("the method %s in class %s cannot have parameters", methodName, className),
                "please remove parameters or do not declare it as @conf or @tune"
        );
    }

    public static Mattlib2Exception BAD_LOG(String methodName, String className) {
        return new Mattlib2Exception(
                "badLog",
                format("method %s in class %s must have parameters", methodName, className),
                "please add parameters or do not declare @log"
        );
    }

    public static Mattlib2Exception BAD_RETURN_TYPE(String methodName, String className) {
        return new Mattlib2Exception(
                "badReturnType",
                format("return type of method %s in class %s is not void, but is log", methodName, className),
                "please set the return type to void"
        );
    }

    public static Mattlib2Exception NO_TOML(GenericPath path) {
        return new Mattlib2Exception(
                "noTomlException",
                format("there is no valid toml for the related path [%s] in config.toml", path.tablePath()),
                "please add some toml"
        );
    }

    public static Mattlib2Exception MULTIPLE_SELF_PATH(GenericPath path) {
        return new Mattlib2Exception(
                "multipleSelfPath",
                format("the component at path [%s] already has the @SelfPath annotated function on it. Do not add an extra one", path.tablePath()),
                "Remove the extra @selfpath you added"
        );
    }

    public static Mattlib2Exception NO_SELF_PATH(GenericPath path) {
        return new Mattlib2Exception(
                "noSelfpath",
                format("the component at path [%s] somehow has no @SelfPath annotated function on it.", path.tablePath()),
                "Contact matt"
        );
    }



    public static Mattlib2Exception BAD_LOG_TYPE(String methodName, String className, String unsupportedTypeName) {
        return new Mattlib2Exception(
                "badLogType",
                format("method %s on class %s logs type %s, but mattlog2 does not support that type!", methodName, className, unsupportedTypeName),
                "contact matt to add it or modify LogComponentManipulator.java or MutantComponentManipulator.java"
        );
    }



}
