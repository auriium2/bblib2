package xyz.auriium.mattlib2;

import edu.wpi.first.wpilibj.AnalogInput;
import xyz.auriium.mattlib2.log.INetworkedComponent;

import java.lang.reflect.Array;
import java.util.function.BiFunction;

public interface IMattLog {



    <T extends INetworkedComponent> T load(Class<T> type, String path);

    default <T extends INetworkedComponent> T[] loadRange(Class<T> type, String originalPath, int range, BiFunction<String, Integer, String> subNamingFunction) {
        T[] array = (T[]) Array.newInstance(type, range);
        for (int i = 0; i < range; i++) {
            array[i] = load(type, subNamingFunction.apply(originalPath, i));
        }
        return array;
    }

    default <T extends INetworkedComponent> T[] loadRange(Class<T> type, String originalPath, int range) {
        return loadRange(type, originalPath, range, (s,i) -> s+"/"+i);
    }

}
