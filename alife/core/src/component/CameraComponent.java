package component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class CameraComponent implements Component {
	public float width;
	public float height;
	public float zoom;
	public OrthographicCamera cam;
	public boolean canMove;
}
