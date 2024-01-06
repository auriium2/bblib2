package xyz.auriium.mattlib2.foxe.flat;

import com.google.flatbuffers.FlatBufferBuilder;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.EqualsMethod;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.matcher.ElementMatchers;
import xyz.auriium.mattlib2.foxe.LogDescriptionRecord;
import xyz.auriium.mattlib2.foxe.ServerChannel;

import java.lang.reflect.Modifier;

public class FlatbufferCodeGenerator {
    static final ByteBuddy BUDDY = new ByteBuddy();

    static final String BUFFER_FIELD = "buffer";
    static final String CHANNEL_FIELD = "channel";



    //this method will clear the local buffer
    static final byte IDX_CLEAR_BUFFER = 0;

    //this method will mark the buffer as finished
    static final byte IDX_FINISH_BUFFER = 1;

    //this method sends the bytes to the server
    static final byte IDX_SEND_BYTES_TO_HELL = 2;

    //This is "start table". Unf means unfinished; it needs to be supplied the field quantity.
    static final byte IDX_UNF_START_TABLE = 3;

    static final byte IDX_UNF_PUT_BOOLEAN = 4;
    static final byte IDX_UNF_PUT_INT = 5;
    static final byte IDX_UNF_PUT_FLOAT = 6;
    static final byte IDX_UNF_PUT_LONG = 7;
    static final byte IDX_UNF_PUT_DOUBLE = 8;
    static final byte IDX_UNF_PUT_STRING = 9;
    static final byte IDX_UNF_PUT_BYTE = 10;
    static final byte IDX_UNF_PUT_ARRAY = 11;
    static final byte IDX_UNF_PUT_POSE2 = 12;
    static final byte IDX_UNF_PUT_POSE3 = 13;







    final MethodCall[] implementations;

    FlatbufferCodeGenerator(MethodCall[] implementations) {
        this.implementations = implementations;
    }

    public Object generateFinalObject(LogDescriptionRecord[] records, Class<?> useClass) {



        var builder = BUDDY.subclass(useClass, ConstructorStrategy.Default.DEFAULT_CONSTRUCTOR)
                .name(useClass.getPackageName() + "." + useClass.getSimpleName())
                .suffix(FlatbufferConstants.GENERATED + Integer.toHexString(hashCode()));

        builder
                .method(ElementMatchers.isEquals())
                .intercept(EqualsMethod.isolated());

        //class is set up, let's set up the fields, etc

        builder.defineField(BUFFER_FIELD, FlatBufferBuilder.class, Modifier.PUBLIC); //Add a buffer to this object!
        builder.defineField(CHANNEL_FIELD, ServerChannel.class, Modifier.PUBLIC); //send my data to me!!!

        //let's add the log fields

        Implementation[] perFieldInsertImplementations = new Implementation[records.length];

        for (int i = 0; i < records.length; i++) {
            LogDescriptionRecord rx = records[i];
            builder.defineField(rx.fieldName(), rx.fieldType(), Modifier.PUBLIC); //I MustBeUpdated should only be called from the main thread, so no need for volatile
/*
            builder.define()
            perFieldInsertImplementations[i] =*/
        }

        MethodCall createTableWithCorrectLength = implementations[IDX_UNF_START_TABLE].with(records.length);

        Implementation.Composable implementation = implementations[IDX_CLEAR_BUFFER]
                .andThen(createTableWithCorrectLength);



        implementation = implementation
                .andThen(implementations[IDX_FINISH_BUFFER])
                .andThen(implementations[IDX_SEND_BYTES_TO_HELL]);

/*

        new FlatBufferBuilder().startTable();
*/

        return null;
    }
/*
    Optional<MethodCall> selectMethodCall(Class<?> type, String fieldName) {

        if (type == int.class || type == Integer.class) {
            return Optional.of(

            );
        }

        if (type == float.class || type == Float.class) {
            return Optional.of(BaseType.Float);
        }

        if (type == double.class || type == Double.class) {
            return Optional.of(BaseType.Double);
        }

        if (type == long.class || type == Long.class) {
            return Optional.of(BaseType.Long);
        }

        if (type == boolean.class || type == Boolean.class) {
            return Optional.of(BaseType.Bool);
        }

        if (type == Byte.class || type == byte.class) {
            return Optional.of(BaseType.Byte);
        }

        if (type == String.class) {
            return Optional.of(BaseType.String);
        }

        if (type.isArray() || type == Pose2d.class || type == Pose3d.class) {
            return Optional.of(BaseType.Vector);
        }


        return Optional.empty();
    }*/

    public static FlatbufferCodeGenerator makeSafely() {

        try {
            MethodCall[] implementationArray = new MethodCall[20];

            implementationArray[IDX_CLEAR_BUFFER] = MethodCall
                    .invoke(FlatBufferBuilder.class.getMethod("clear"))
                    .onField(BUFFER_FIELD);

            MethodCall finishTable = MethodCall
                    .invoke(FlatBufferBuilder.class.getMethod("endTable"))
                    .onField(BUFFER_FIELD);

            implementationArray[IDX_FINISH_BUFFER] = MethodCall
                    .invoke(FlatBufferBuilder.class.getMethod("finish", int.class))
                    .onField(BUFFER_FIELD)
                    .withMethodCall(finishTable);

            MethodCall bufferToBytes = MethodCall
                    .invoke(FlatBufferBuilder.class.getMethod("sizedByteArray"))
                    .onField(BUFFER_FIELD);

            implementationArray[IDX_SEND_BYTES_TO_HELL] = MethodCall
                    .invoke(ServerChannel.class.getMethod("push", byte[].class))
                    .onField(CHANNEL_FIELD)
                    .withMethodCall(bufferToBytes);

            implementationArray[IDX_UNF_START_TABLE] = MethodCall
                    .invoke(FlatBufferBuilder.class.getMethod("startTable", int.class))
                    .onField(CHANNEL_FIELD);

            implementationArray[IDX_UNF_PUT_BOOLEAN] = MethodCall
                    .invoke(FlatBufferBuilder.class.getMethod("putBoolean", boolean.class))
                    .onField(CHANNEL_FIELD);

            implementationArray[IDX_UNF_PUT_DOUBLE] = MethodCall
                    .invoke(FlatBufferBuilder.class.getMethod("putDouble", double.class))
                    .onField(CHANNEL_FIELD);


            return new FlatbufferCodeGenerator(implementationArray);



        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }


    }

}
