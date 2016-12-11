package component;

import com.badlogic.ashley.core.Component;

import creature.CreatureBody;

public class Bot implements Component {
	public CreatureBody body;
	public boolean renderState;
}
