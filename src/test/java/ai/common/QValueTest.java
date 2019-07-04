package ai.common;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import model.Position;
import model.Team;
import model.Unit;

@RunWith(MockitoJUnitRunner.class)
public class QValueTest {
	private double epsilon = 0.0001;
	@Mock
	private State state, newState;
	@Mock
	private Unit u1, u2;
	@Mock
	private List<Unit> units;
	@Mock
	private Team t;

	@Test
	public void initialValueisNegativeTwo() {
		QValue qValue = new QValue(state);
		assertEquals(-2.0, qValue.getValue(), epsilon);
	}

	@Test
	public void updateQValueCooperative() {
		when(u1.getTeam()).thenReturn(t);
		when(t.getLivingUnits()).thenReturn(units);
		when(u1.calculateState()).thenReturn(newState);
		when(units.get(anyInt())).thenReturn(u2);
		when(units.size()).thenReturn(1);
		QValue qValue = new QValue(state);

		qValue.updateQValue(u1, 3);
	}

	@Test
	public void updateQValueOracle() {
		when(u1.getTeam()).thenReturn(t);
		when(t.getLivingUnits()).thenReturn(units);
		when(units.get(anyInt())).thenReturn(u2);
		when(u1.calculateState()).thenReturn(newState);
		when(units.size()).thenReturn(1);
		QValue qValue = new QValue(state);

		qValue.updateQValue(u1, 1);
	}

}
