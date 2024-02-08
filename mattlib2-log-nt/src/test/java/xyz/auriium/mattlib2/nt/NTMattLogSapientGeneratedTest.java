package xyz.auriium.mattlib2.nt;

import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import net.bytebuddy.description.ByteCodeElement;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.matcher.ElementMatcher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.doReturn;
import static org.hamcrest.Matchers.is;

@Timeout(value = 5, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
class NTMattLogSapientGeneratedTest {

    //Sapient generated method id: ${d339fb5a-3c6f-3842-810b-f0d02fb32c7b}
    @Disabled()
    @Test()
    void recursivelyGenerateMatcherWhenSuperclassGetInterfacesIsNotEmpty() {
        /* Branches:
         * (for-each(superclass.getInterfaces())) : true
         *
         * TODO: Help needed! Please adjust the input/test parameter values manually to satisfy the requirements of the given test scenario.
         *  The test code, including the assertion statements, has been successfully generated.
         */
        //Arrange Statement(s)
        ElementMatcher.Junction<ByteCodeElement> matcherMock = mock(ElementMatcher.Junction.class);
        ElementMatcher.Junction<ByteCodeElement> elementMatcherJunctionMock = mock(ElementMatcher.Junction.class);
        ElementMatcher.Junction elementMatcherJunctionMock2 = mock(ElementMatcher.Junction.class);
        try (MockedStatic<ElementMatchers> elementMatchers = mockStatic(ElementMatchers.class)) {
            doReturn(elementMatcherJunctionMock).when(matcherMock).or(elementMatcherJunctionMock2);
            elementMatchers.when(() -> ElementMatchers.isDeclaredBy(Object.class)).thenReturn(elementMatcherJunctionMock2);
            NTMattLog target = new NTMattLog();
            //Act Statement(s)
            ElementMatcher.Junction<ByteCodeElement> result = target.recursivelyGenerateMatcher(Object.class, matcherMock);
            //Assert statement(s)
            assertAll("result", () -> {
                assertThat(result, is(notNullValue()));
                verify(matcherMock).or(elementMatcherJunctionMock2);
                elementMatchers.verify(() -> ElementMatchers.isDeclaredBy(Object.class), atLeast(1));
            });
        }
    }
}
