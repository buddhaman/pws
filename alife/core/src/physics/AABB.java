package physics;

public class AABB {
	public float width;
	public float height;
	public float x;
	public float y;
	
	public AABB(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public AABB() {
		
	}
	
	public float getCenterX() {
		return x + width/2f;
	}
	
	public float getCenterY() {
		return y + height/2f;
	}
	
	public static boolean overlaps(AABB box1, AABB box2) {
		return (Math.abs(box1.getCenterX()-box2.getCenterX())*2 < (box1.width+box2.width) && 
				(Math.abs(box1.getCenterY()-box2.getCenterY())*2 < (box1.height+box2.height)));
	}

	public void extendTo(float xMin, float yMin, float xMax, float yMax) {
		float xDif = x-xMin;
		if(xDif > 0)
			x-=xDif;
		xDif = xMax-(x+width);
		if(xDif > 0)
			width+=xDif;
		
		float yDif = y-yMin;
		if(yDif > 0)
			y-=yDif;
		yDif = yMax-(y+height);
		if(yDif > 0)
			height+=yDif;
	}
}
