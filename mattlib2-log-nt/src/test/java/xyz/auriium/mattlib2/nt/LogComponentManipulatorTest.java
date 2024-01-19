package xyz.auriium.mattlib2.nt;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import xyz.auriium.mattlib2.log.INetworkedComponent;
import xyz.auriium.yuukonstants.GenericPath;
import yuukonfig.core.impl.BaseManipulation;
import yuukonfig.core.node.NotPresentNode;
import yuukonfig.core.node.RawNodeFactory;

import static org.junit.jupiter.api.Assertions.*;

class LogComponentManipulatorTest {

    public interface LongCallChain extends INetworkedComponent
    {



    }
    @Test
    void deserialize() {
        var bm = Mockito.mock(BaseManipulation.class);
        var rnf = Mockito.mock(RawNodeFactory.class);
        var nnl = Mockito.mock(NetworkMattLogger.class);


        var lcm = new LogComponentManipulator(LongCallChain.class,bm, rnf, nnl);
        var asInterface = lcm.deserialize(new NotPresentNode(new GenericPath()));

        var interfaceIns = (LongCallChain) asInterface;

        System.out.println(interfaceIns.selfPath());

    }
}