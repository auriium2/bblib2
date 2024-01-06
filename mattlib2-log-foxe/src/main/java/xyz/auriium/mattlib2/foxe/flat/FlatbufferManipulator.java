package xyz.auriium.mattlib2.foxe.flat;

/*

public class FlatbufferManipulator implements Manipulator {


    static final String BUFFER_FIELD = "push_buffer";

    final Class<?> useClass;
    final Manipulation manipulation;
    final RawNodeFactory factory;
    final Supplier<Boolean> shouldUseTuning;

    final FlatbufSchemaGenerator flatbufSchemaGenerator;
    final FoxgloveStartupRegister register;

    public FlatbufferManipulator(Class<?> useClass, Manipulation manipulation, Supplier<Boolean> shouldUseTuning, RawNodeFactory factory, FlatbufSchemaGenerator flatbufSchemaGenerator, FoxgloveStartupRegister register) {
        this.useClass = useClass;
        this.manipulation = manipulation;
        this.shouldUseTuning = shouldUseTuning;
        this.factory = factory;
        this.flatbufSchemaGenerator = flatbufSchemaGenerator;
        this.register = register;
    }

    @Override
    public int handles() {
        if (ILogComponent.class.isAssignableFrom(useClass)) return Priority.PRIORITY_HANDLE;
        return Priority.DONT_HANDLE;
    }


    //TODO replace this later

    static Implementation CLEAR_LOCAL_BUFFER;
    static Implementation FINISH_LOCAL_BUFFER;
    static Implementation PUSH_TO_BUFFER;


    static {
        try {
            CLEAR_LOCAL_BUFFER =


            new FlatBufferBuilder().putBoolean();
            FINISH_LOCAL_BUFFER = MethodCall
                    .invoke(FlatBufferBuilder.class.getMethod("finish", int.class))
                    .onField(BUFFER_FIELD)
                    .withField("0");

            //TODO this needs to be finished
            FINISH_LOCAL_BUFFER = MethodCall
                    .invoke(FlatBufferBuilder.class.getMethod("finish", int.class))
                    .onField(BUFFER_FIELD)
                    .withField();


        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


    public static LogDescriptionRecord[] generateLogDescriptionFromClass(Class<?> useClass) {
        Method[] mds = useClass.getMethods();
        mds = Arrays.stream(mds).sorted().toArray(Method[]::new);

        //build list of log records with offsets
        List<LogDescriptionRecord> logMethods = new ArrayList<>();
        for (int i = 0; i < mds.length; i++) {
            Method method = mds[i];

            if (method.getDeclaringClass() == Object.class) continue;
            ReflectionUtil.check(method);
            if (!method.isAnnotationPresent(Log.class)) continue;
            Parameter[] parameters = method.getParameters();
            logMethods.add(new LogDescriptionRecord(parameters[0].getName(), parameters[0].getType(), i));
        }

        return logMethods.toArray(LogDescriptionRecord[]::new);
    }



    @Override
    public java.lang.Object deserialize(Node node, GenericPath exceptionalKey) throws BadValueException {

        LogDescriptionRecord[] records = generateLogDescriptionFromClass(useClass);
        byte[] bfbsSchema = flatbufSchemaGenerator.generateSchemaFromDescription(useClass, records);
        String schemaAsString = Base64.getEncoder().encodeToString(bfbsSchema);
        String schemaName = flatbufSchemaGenerator.calculateName(useClass);

        int channel_offset = register.registerChannel(
                new ChannelData(
                        exceptionalKey.getAsTablePath(),
                        FlatbufferConstants.MESSAGE_ENCODING,
                        schemaName,
                        schemaAsString,
                        Optional.of(FlatbufferConstants.SCHEMA_ENCODING)
                )
        );






        for (int i = 0; i < records.length; i++) {

        }


        try {
            var builder = BUDDY.subclass(useClass)
                    .name(useClass.getPackageName() + "." + useClass.getSimpleName())
                    .suffix("Generated_" + Integer.toHexString(hashCode()));

            builder
                    .method(ElementMatchers.isEquals())
                    .intercept(EqualsMethod.isolated());

            for (Map.Entry<Method, Supplier<Object>> values : configOrTuneMap.entrySet()) { //every method on the new implementation will only do one thing (return config value)
                Implementation supplierInvoke = MethodCall
                        .invoke(Supplier.class.getMethod("get"))
                        .on(values.getValue())
                        .withAssigner(Assigner.DEFAULT, Assigner.Typing.DYNAMIC);

                builder = builder
                        .method(ElementMatchers.is(values.getKey()))
                        .intercept(supplierInvoke);
            }
            for (Map.Entry<Method, Consumer<Object>> values : loggerMap.entrySet()) {
                Implementation consumerInvoke = MethodCall
                        .invoke(Consumer.class.getMethod("accept", Object.class))
                        .on(values.getValue())
                        .withArgument(0)
                        .withAssigner(Assigner.DEFAULT, Assigner.Typing.STATIC); //TODO this needs to be fixed

                builder = builder
                        .method(ElementMatchers.is(values.getKey()))
                        .intercept(consumerInvoke);
            }

            DynamicType.Unloaded<?> unloaded = builder.make();
            ClassLoader loaderToUse = useClass.getClassLoader();
            DynamicType.Loaded<?> loaded = unloaded.load(loaderToUse, ClassLoadingStrategy.ForBootstrapInjection.Default.INJECTION);

            return loaded
                    .getLoaded()
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new Mattlib2Exception("bytebuddy", "critical reflection error", "ask matt for help");
        }

    }















    @Override
    public Node serializeObject(java.lang.Object o, String[] strings) {
        return null;
    }


    @Override
    public Node serializeObject(Object object, String[] comment) {
        RawNodeFactory.MappingBuilder builder = factory.makeMappingBuilder();

        for (Method method : useClass.getMethods()) {
            if (method.getDeclaringClass() == Objects.class) continue;
            check(method);

            String key = getKey(method);
            Class<?> as = method.getReturnType();
            String[] comments = getComment(method);
            Object toSerialize = new CustomForwarder(method, object).invoke(); //get the return of the method


            Node serialized = manipulation.serialize(
                    toSerialize,
                    as,
                    comments,
                    Contextual.present(
                            method.getGenericReturnType()
                    )
            );

            builder.add(key, serialized);

        }


        return builder.build();
    }





    @Override
    public Node serializeDefault(String[] comment) {


        RawNodeFactory.MappingBuilder builder = factory.makeMappingBuilder();

        for (Method method : useClass.getMethods()) {
            if (method.getDeclaringClass() == Objects.class) continue;
            check(method);
            if (method.getAnnotation(Log.class) != null) continue; //No need to serialize for log

            Class<?> returnType = method.getReturnType();
            String key = getKey(method);
            String[] comments = getComment(method);
            Node serialized = manipulation.serializeDefault(
                    returnType,
                    comments,
                    Contextual.present(method.getGenericReturnType())
            );

            //System.out.println("finished: " + key + " : " + serialized);

            builder.add(key, serialized);
        }

        return builder.build(String.valueOf(Arrays.asList(comment)));
    }

    String[] getComment(Method method) {
        if (method.isAnnotationPresent(Comment.class)) {
            return method.getAnnotation(Comment.class).value();
        }

        return new String[]{};
    }


    String getKey(Method method) {
        if (method.isAnnotationPresent(Key.class)) {
            return method.getAnnotation(Key.class).value();
        }

        return method.getName();
    }



}

*/
