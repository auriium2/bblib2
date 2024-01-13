package xyz.auriium.mattlib2;

import edu.wpi.first.wpilibj.Filesystem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import xyz.auriium.mattlib2.log.INetworkedComponent;
import xyz.auriium.mattlib2.log.ProcessMap;
import xyz.auriium.mattlib2.log.ProcessPath;
import xyz.auriium.mattlib2.log.TypeMap;
import xyz.auriium.mattlib2.log.annote.Conf;
import xyz.auriium.mattlib2.nt.LogComponentManipulator;
import xyz.auriium.mattlib2.nt.NetworkMattLogger;
import xyz.auriium.mattlib2.yuukonfig.TypeMapManipulator;
import yuukonfig.core.YuuKonfig;
import yuukonfig.core.manipulation.Contextual;
import yuukonfig.core.manipulation.Manipulation;
import yuukonfig.core.node.Node;
import yuukonfig.toml.TomlNodeFactory;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

//shitty little test that i should move to the origin
class TypeMapManipulatorTest {

    public interface MyConfig extends INetworkedComponent {
        default @Conf("i") int i() {
            return 1;
        }
    }

    public interface MyConfig2 extends INetworkedComponent {
        default @Conf("j") int j() {
            return 2;
        }
    }

    @Test
    void doOtherThing() {
        var node = YuuKonfig.instance()
                .register(
                        (manipulation,clazz,c,factory) -> new LogComponentManipulator(
                                clazz,
                                manipulation,
                                () -> false,
                                factory,
                                Mockito.mock(NetworkMattLogger.class)
                        )
                )
                .register(
                        (manipulation, useClass, useType, factory) -> new TypeMapManipulator(manipulation, useClass, useType, factory,
                                new ProcessMap()
                                        .with(ProcessPath.parse("house/cat"), MyConfig.class)
                                        .with(ProcessPath.parse("house"), MyConfig2.class)
                        )
                )
                .test()
                .serializeTest(TypeMap.class);

        Assertions.assertFalse(node.type() == Node.Type.NOT_PRESENT);
        Assertions.assertSame(node.asMapping().value("house").type(), Node.Type.MAPPING);

        Node cat = node.asMapping()
                .value("house")
                .asMapping()
                .value("cat");

        Assertions.assertSame(Node.Type.MAPPING,cat.type());

        Assertions.assertEquals("1",

                        cat.asMapping()
                        .value("i")
                        .asScalar()
                        .value()
        );
        Assertions.assertEquals("2",node.asMapping().value("house").asMapping().value("j").asScalar().value());

    }

    @Test
    void doThing() {
    }
}