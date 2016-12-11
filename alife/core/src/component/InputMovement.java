package component;

import com.badlogic.ashley.core.Component;

public class InputMovement implements Component {
	public boolean up;
	public boolean down;
	public boolean left;
	public boolean right;
	public boolean action1;
	public boolean action2;
	
	@Override
	public String toString() {
		return up + " " + down + " " + left + " " + right;
	}
}
