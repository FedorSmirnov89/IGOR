package entity;

import static org.junit.Assert.*;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.mockito.Mockito.mock;

import static org.mockito.Mockito.when;

public class VariableImportanceTest {

	@Test
	public void test() {
		Object var1 = mock(Object.class);
		when(var1.toString()).thenReturn("B");
		double importance1 = 1.;
		VariableImportance varImp1 = new VariableImportance(var1, importance1);
		
		Object var2 = mock(Object.class);
		when(var2.toString()).thenReturn("B");
		double importance2 = 2.;
		VariableImportance varImp2 = new VariableImportance(var2, importance2);
		
		Object var3 = mock(Object.class);
		when(var3.toString()).thenReturn("A");
		double importance3 = 1.;
		VariableImportance varImp3 = new VariableImportance(var3, importance3);
		assertEquals(var1, varImp1.getVariable());
		assertEquals(importance1, varImp1.getImportance(), .000001);
		assertTrue(varImp1.compareTo(varImp2) < 0 );
		assertTrue(varImp1.compareTo(varImp3) > 0 );
		assertTrue(varImp1.compareTo(varImp1) == 0 );
	}
	
	@Test
	public void testEquals() {
		EqualsVerifier.forClass(VariableImportance.class).verify();
	}

}
