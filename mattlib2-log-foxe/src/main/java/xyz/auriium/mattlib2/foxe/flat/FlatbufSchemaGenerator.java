package xyz.auriium.mattlib2.foxe.flat;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.reflection.BaseType;
import com.google.flatbuffers.reflection.Field;
import com.google.flatbuffers.reflection.Object;
import com.google.flatbuffers.reflection.Schema;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import xyz.auriium.mattlib2.utils.BufferUtils;
import xyz.auriium.mattlib2.foxe.structure.NetworkDescriptionRecord;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FlatbufSchemaGenerator {

    static final String DOCUMENTATION_DESCRIPTOR_ARRAY_FIELD = "docsField";

    final Map<Class<?>, byte[]> cache = new HashMap<>();

    /**
     * Come up with a unique name to represent a class
     * @param useClass the class
     * @return a unique name for schema+ws
     */
    public String calculateName(Class<?> useClass) {
        return useClass.getSimpleName(); //make this better later
    }
    public byte[] generateSchemaFromDescription(Class<?> useClass, NetworkDescriptionRecord[] description) {
        return cache.computeIfAbsent(useClass, ignored -> {


            //build fields to the bytebuffer builder, add to storage vector
            FlatBufferBuilder bufferBuilder = new FlatBufferBuilder();
            int[] fieldsArray = new int[0];

            for (int i = 0; i < description.length; i++) {
                NetworkDescriptionRecord record = description[i];
                int newFieldName_index = bufferBuilder.createString(record.fieldName());
                Optional<Byte> possibleTypeOfField = computeBaseTypeIndex(record.fieldType());
                if (possibleTypeOfField.isEmpty()) {
                    throw xyz.auriium.mattlib2.Exceptions.BAD_LOG_TYPE(description[i].fieldName(), useClass.getSimpleName(),record.fieldType().getSimpleName());
                }
                byte actualType = possibleTypeOfField.get();

                Field.startField(bufferBuilder);
                Field.addName(bufferBuilder, newFieldName_index);
                Field.addType(bufferBuilder, actualType);
                Field.addId(bufferBuilder, record.id());
                int createdField_offset = Field.endField(bufferBuilder);

                fieldsArray = BufferUtils.add(fieldsArray, createdField_offset);
            }

            //TODO set up documentation as a constant 1shot field sent at the start
            //int docsFieldName_offset = bufferBuilder.createString(DOCUMENTATION_DESCRIPTOR_ARRAY_FIELD);
            //new fields vectors are done!
            int fieldsVector_offset = Object.createFieldsVector(bufferBuilder, fieldsArray);
            int schemaName_offset = bufferBuilder.createString( calculateName(useClass) ); //TODO something more sophisticated

            //we have a field vector, let's build the object
            Object.startObject(bufferBuilder);
            Object.addName(bufferBuilder, schemaName_offset);
            Object.addFields(bufferBuilder, fieldsVector_offset);
            int object_offset = Object.endObject(bufferBuilder);

            //we need an enums vector because FlatBuffers demands it, let's make an empty one
            int emptyEnumsVector_offset = Schema.createEnumsVector(bufferBuilder, new int[0]);
            int objectsVector_offset = Schema.createObjectsVector(bufferBuilder, new int[] { object_offset });

            //we have our object! let's start a Schema now
            Schema.startSchema(bufferBuilder);
            Schema.addEnums(bufferBuilder, emptyEnumsVector_offset);
            Schema.addObjects(bufferBuilder, objectsVector_offset);
            Schema.addRootTable(bufferBuilder, object_offset);
            int finalSchema_offset = Schema.endSchema(bufferBuilder);
            bufferBuilder.finish(finalSchema_offset); //i think this is ok?

            return bufferBuilder.sizedByteArray();
        });
    }


    //TODO replace this with a real type builder, use the nesting system
    public static Optional<Byte> computeBaseTypeIndex(Class<?> type) {


        if (type == int.class || type == Integer.class) {
            return Optional.of(BaseType.Int);
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
    }


}
