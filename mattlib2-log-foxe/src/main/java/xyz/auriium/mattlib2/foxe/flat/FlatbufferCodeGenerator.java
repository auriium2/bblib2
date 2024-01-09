package xyz.auriium.mattlib2.foxe.flat;

import com.google.flatbuffers.FlatBufferBuilder;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.EqualsMethod;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.matcher.ElementMatchers;
import xyz.auriium.mattlib2.foxe.NetworkDescriptionRecord;
import xyz.auriium.mattlib2.foxe.ServerChannel;

import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.function.Supplier;
/*

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



    public Object generateFinalObject(NetworkDescriptionRecord[] records, Class<?> useClass) throws NoSuchMethodException {



        var builder = BUDDY
                .subclass(useClass, ConstructorStrategy.Default.DEFAULT_CONSTRUCTOR)
                .name(useClass.getPackageName() + "." + useClass.getSimpleName())
                .suffix(FlatbufferConstants.GENERATED + Integer.toHexString(hashCode()))
                .method(ElementMatchers.isEquals())
                .intercept(EqualsMethod.isolated())
                .defineField(BUFFER_FIELD, FlatBufferBuilder.class, Modifier.PUBLIC) //Add a buffer to this object!
                .defineField(CHANNEL_FIELD, ServerChannel.class, Modifier.PUBLIC); //send my data to me!!!

        //let's add the log fields

        Implementation[] perFieldInsertImplementations = new Implementation[records.length];

        for (int i = 0; i < records.length; i++) {
            NetworkDescriptionRecord rx = records[i];

            builder.defineField(rx.fieldName(), rx.fieldType(), Modifier.PUBLIC); //I MustBeUpdated should only be called from the main thread, so no need for volatile
            perFieldInsertImplementations[i] = getImplementation(rx);
            */
/*

            builder.define()
            perFieldInsertImplementations[i] =*//*

        }

        MethodCall createTableWithCorrectLength = implementations[IDX_UNF_START_TABLE].with(records.length);

        Implementation.Composable implementation = implementations[IDX_CLEAR_BUFFER]
                .andThen(createTableWithCorrectLength);



        implementation = implementation
                .andThen(implementations[IDX_FINISH_BUFFER])
                .andThen(implementations[IDX_SEND_BYTES_TO_HELL]);

*/
/*

        new FlatBufferBuilder().startTable();
*//*


        return null;
    }


    public static Implementation getImplementation(NetworkDescriptionRecord record) throws NoSuchMethodException {

        Class<?> returnType = record.fieldType();
        String specificFieldName = record.fieldName();
        int fieldID = record.id();


        FlatBufferBuilder bb = new FlatBufferBuilder();
        bb.addDouble();
        if (returnType == Double.class || returnType == double.class) { //handle doubles
            return MethodCall
                    .invoke(FlatBufferBuilder.class.getMethod("putDouble", double.class))
                    .onField(CHANNEL_FIELD)
                    .withField(specificFieldName);
        }

        if (returnType == Long.class || returnType == long.class) { //handle longs
            return MethodCall
                    .invoke(FlatBufferBuilder.class.getMethod("putLong", long.class))
                    .onField(CHANNEL_FIELD)
                    .withField(specificFieldName);
        }

        if (returnType == Integer.class || returnType == int.class) {
            return MethodCall
                    .invoke(FlatBufferBuilder.class.getMethod("putInt", int.class))
                    .onField(CHANNEL_FIELD)
                    .withField(specificFieldName);
        }

        if (returnType == String.class) {
            MethodCall createStringReturnOffset = MethodCall
                    .invoke(FlatBufferBuilder.class.getMethod("createString", ByteBuffer.class))
                    .onField(CHANNEL_FIELD)
                    .withField(specificFieldName);

            return MethodCall
                    .invoke(FlatBufferBuilder.class.getMethod("addOffset", int.class))
                    .onField(CHANNEL_FIELD)
                    .withMethodCall(createStringReturnOffset);
        }

        if (returnType == Boolean.class || returnType == boolean.class) {
            return MethodCall
                    .invoke(FlatBufferBuilder.class.getMethod("putBoolean", boolean.class))
                    .onField(CHANNEL_FIELD)
                    .withField(specificFieldName);
        }

        if (returnType == long[].class || returnType == Long[].class) {

            FlatBufferBuilder builder = new FlatBufferBuilder();
            builder.addOffset();

            MethodCall lengthReturnsLength = MethodCall
                    .invoke(long[].class.getMethod("size"))
                    .onField(specificFieldName);

            MethodCall startVector = MethodCall
                    .invoke(FlatBufferBuilder.class.getMethod("startVector", int.class, int.class, int.class))
                    .onField(BUFFER_FIELD)
                    .with(Long.BYTES)
                    .withMethodCall(lengthReturnsLength)
                    .with(Long.BYTES);

            Implementation.Composable working = startVector;

            //we are in the vector
            for (int i = 0; i < record.arraySize(); i++) {

                MethodCall valueAtIndexReturnsValue = MethodCall
                        .invoke(FlatbufferCodeGenerator.class.getMethod("getFromArray", Object[].class, int.class))
                        .withField(specificFieldName)
                        .with(i);

                working = working.andThen(
                        MethodCall.invoke(FlatBufferBuilder.class.getMethod("addLong", long.class))
                                .onField(BUFFER_FIELD)
                                .withMethodCall(valueAtIndexReturnsValue)
                );
            }

            MethodCall endVectorReturnOffset = MethodCall.invoke(FlatBufferBuilder.class.getMethod("endVector"))
                    .onField(BUFFER_FIELD);

            MethodCall insertVectorOffsetIntoVtableOffset = MethodCall.invoke(FlatBufferBuilder.class.getMethod("addOffset", int.class, int.class, int.class))
                    .with()

            return working.andThen()
        }


    }

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


            return new FlatbufferCodeGenerator(implementationArray);



        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }


    }


    */
/**
     * PLEASE GET RID OF THIS THIS IS FUCKIGN RIDICULOUS
     * @param array
     * @param index
     * @return
     * @param <T>
     *//*

    public static <T> T getFromArray(T[] array, int index) {
        return array[index];
    }

}
*/
