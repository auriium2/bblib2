package xyz.auriium.mattlib2.yuukonfig;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import xyz.auriium.mattlib2.log.ProcessPath;
import xyz.auriium.yuukonstants.GenericPath;
import yuukonfig.core.node.Mapping;
import yuukonfig.core.node.Node;
import yuukonfig.toml.TomlNodeFactory;

import static org.junit.jupiter.api.Assertions.*;

class TypeMapManipulatorTest {

    @Test
    void testDrillToNodeShouldWorkNominally() {
        var factory = new TomlNodeFactory();

        var chaddedBuilder = factory.makeMappingBuilder(GenericPath.of("based/breadpilled/chadded"));
        chaddedBuilder.add("yeast", factory.scalarOf(GenericPath.of("based/breadpilled/chadded/yeast"),"flour"));
        Node chadded = chaddedBuilder.build();
        var breadpilledBuilder = factory.makeMappingBuilder(GenericPath.of("based/breadpilled"));
        breadpilledBuilder.add("chadded", chadded);
        Node breadpilled = breadpilledBuilder.build();
        var basedBuilder = factory.makeMappingBuilder(GenericPath.of("based"));
        basedBuilder.add("breadpilled", breadpilled);
        Mapping basedRoot = basedBuilder.build();

        var query1 = TypeMapManipulator.drillToNode(factory, basedRoot, ProcessPath.of("based"));
        Assertions.assertEquals(Node.Type.NOT_PRESENT, query1.type()); //no "based" bc its root
        var query2 = TypeMapManipulator.drillToNode(factory, basedRoot, ProcessPath.of("breadpilled/chadded"));
        Assertions.assertEquals("flour",query2.asMapping().valueGuaranteed("yeast").asScalar().value());


    }

    @Test
    void testRecursiveSerializeShouldWork() {
        var factory = new TomlNodeFactory();

        ProcessPath simPath = ProcessPath.of("a/b/c");
        Mapping generatedFatFuckingMess = TypeMapManipulator.recursivelySerialize(
                factory,
                simPath,
                0,
                factory.makeMappingBuilder(new GenericPath()).build() //root starts empty
        );

        Assertions.assertEquals(Node.Type.MAPPING, generatedFatFuckingMess.valueGuaranteed("a").type());
        Assertions.assertEquals(Node.Type.MAPPING, generatedFatFuckingMess.valueGuaranteed("a").asMapping().valueGuaranteed("b").type());
        Assertions.assertEquals(Node.Type.MAPPING, generatedFatFuckingMess.valueGuaranteed("a").asMapping().valueGuaranteed("b").asMapping().valueGuaranteed("c").type());
        Assertions.assertEquals(Node.Type.NOT_PRESENT, generatedFatFuckingMess.valuePossiblyMissing("g").type());
        Assertions.assertEquals(Node.Type.NOT_PRESENT, generatedFatFuckingMess.valuePossiblyMissing("a").asMapping().valuePossiblyMissing("g").type());


        ProcessPath simPath2 = ProcessPath.of("a");
        Mapping generatedFatFuckingMess2 = TypeMapManipulator.recursivelySerialize(
                factory,
                simPath2,
                0,
                factory.makeMappingBuilder(new GenericPath()).build() //root starts empty
        );

        Assertions.assertEquals(Node.Type.MAPPING, generatedFatFuckingMess2.valueGuaranteed("a").type());

    }

    @Test
    void testRecursiveSerializeShouldWorkWithStupid() {
        var factory = new TomlNodeFactory();

        ProcessPath simPath = ProcessPath.of("");
        Mapping generatedFatFuckingMess = TypeMapManipulator.recursivelySerialize(
                factory,
                simPath,
                0,
                factory.makeMappingBuilder(new GenericPath()).build() //root starts empty
        );

        Assertions.assertEquals(Node.Type.NOT_PRESENT, generatedFatFuckingMess.valuePossiblyMissing("a").type());

    }
}