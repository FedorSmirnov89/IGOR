package blueprint;

import static org.junit.Assert.*;

import org.junit.Test;

import static org.mockito.Mockito.mock;

import java.util.HashSet;
import java.util.Set;

public class BluePrintProviderBasicTest {

	@Test
	public void testGetSetInit() {
		Object first = new Object();
		Object second = new Object();
		Set<Object> encodedVariables = new HashSet<>();
		encodedVariables.add(first);
		encodedVariables.add(second);
		BluePrintSat bluePrintMock = mock(BluePrintSat.class);
		BluePrintProviderBasic provider = new BluePrintProviderBasic();
		assertFalse(provider.isInit());
		provider.setCurrentBlueprint(bluePrintMock, encodedVariables);
		assertTrue(provider.isInit());
		assertEquals(bluePrintMock, provider.getCurrentBlueprint());
		assertEquals(encodedVariables, provider.getCurrentlyEncodedVariables());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoInit1() {
		BluePrintProviderBasic provider = new BluePrintProviderBasic();
		provider.getCurrentBlueprint();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoInit2() {
		BluePrintProviderBasic provider = new BluePrintProviderBasic();
		provider.getCurrentlyEncodedVariables();
	}

}
