package physics;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class World {
	public float width;
	public float height;
	
	//width and height of the world in tiles
	public int tWidth;
	public int tHeight;
	
	public float tileSize;
	public int tDiagonal;
	
	public Array<Constraint> constraintList = new Array<Constraint>();
	public Array<Circle> circleList = new Array<Circle>();
	
	public Array<Group> groupList = new Array<Group>();
	
	public Tile[] tiles;
	
	private final Pool<CollisionInfo> collisionPool = new Pool<CollisionInfo>() {
		@Override
		protected CollisionInfo newObject() {
			return new CollisionInfo();
		}
	};
	private final List<CollisionInfo> toResolve = new ArrayList<CollisionInfo>();
	
	private final Pool<TileCollisionInfo> tileCollisionPool = new Pool<TileCollisionInfo> () {
		@Override
		protected TileCollisionInfo newObject() {
			return new TileCollisionInfo();
		}
	};
	private Array<TileCollisionInfo> tileToResolve = new Array<TileCollisionInfo>();
	
	public World(int tWidth, int tHeight, float tileSize) {
		this.tWidth = tWidth;
		this.tHeight = tHeight;
		width = tWidth*tileSize;
		height = tHeight*tileSize;
		
		this.tileSize = tileSize;
		tDiagonal = (int)Math.sqrt(tWidth*tWidth+tHeight*tHeight);
		
		tiles = new Tile[tWidth*tHeight];
		int energyPerTile = (int)(Tile.ENERGY_DENSITY*tileSize*tileSize);
		for(int i = 0; i < tWidth; i++) {
			for(int j = 0; j < tHeight; j++) {
				int type = (i==0 || i==tWidth-1 || j == 0 || j==tHeight-1) ? Tile.TYPE_STONE : Tile.TYPE_EMPTY;
				tiles[i+j*tWidth] = new Tile(type, energyPerTile);
			}
		}
	}
	
	public void updateTiles() {
		for(int i = 0; i < tWidth; i++) {
			for(int j = 0; j < tHeight; j++) {
				tiles[i+j*tWidth].circleList.clear();
			}
		}
		
		//selecteer random tiles die energie naar elkaar vloeien
		int n = tWidth*2;
		int flow = 2;
		for(int i = 0; i < n; i++) {
			int x = MathUtils.random(tWidth-1);
			int y = MathUtils.random(tHeight-1);
			int nx = x+MathUtils.randomSign();
			int ny = y+MathUtils.randomSign();
			if(nx<0)
				nx+=tWidth;
			if(ny<0)
				ny+=tHeight;
			if(nx>tWidth-1)
				nx-=tWidth;
			if(ny>tHeight-1)
				ny-=tHeight;
			Tile t1 = tiles[x+y*tWidth];
			Tile t2 = tiles[nx+ny*tWidth];
			if(t1.energy<t2.energy) {
				t1.energy+=flow;
				t2.energy-=flow;
			} else {
				t1.energy-=flow;
				t2.energy+=flow;
			}
			
		}
		for(int i = 0; i < circleList.size; i++) {
			Circle circle = circleList.get(i);
			
			float minX = circle.getMinX();
			float minY = circle.getMinY();
			float maxX = circle.getMaxX();
			float maxY = circle.getMaxY();
			
			int startX = getXTile(minX);
			int startY = getYTile(minY);
			int endX = getXTile(maxX);
			int endY = getYTile(maxY);
			
			//TODO: do the tile - circle collision here
			for(int x = startX; x <= endX; x++) {
				for(int y = startY; y <= endY; y++) {
					Tile tile = tiles[x+y*tWidth];
					tile.circleList.add(circle);
				}
			}
		}
	}
	
	public int getXTile(float x) {
		int xTile = (int)(x/tileSize);
		if(xTile < 0) xTile = 0;
		if(xTile > tWidth-1) xTile = tWidth-1;
		return xTile;
	}
	
	public int getYTile(float y) {
		int yTile = (int)(y/tileSize);
		if(yTile < 0) yTile = 0;
		if(yTile > tHeight-1) yTile = tHeight-1;
		return yTile;
	}
	
	public void checkWorldEdgeCollisions() {
		for(Circle circle: circleList) {
			Vector2 c = circle.particle.pos;
			if(c.x > width-tileSize/2)
				c.x = width-tileSize/2;
			if(c.y>height-tileSize/2)
				c.y = height-tileSize/2;
			if(c.x < tileSize/2)
				c.x=tileSize/2;
			if(c.y<tileSize/2)
				c.y = tileSize/2;
		}
	}

	public void checkCollisions() {
		
		//list to reuse
		List<Circle> toCheck = new ArrayList<Circle>();
		
		for(int i = 0; i < circleList.size; i++) {
			Circle circle = circleList.get(i);
			getCirclesInNeighbourhood(circle, toCheck);
			for(int j = 0; j < toCheck.size(); j++) {
				Circle other = toCheck.get(j);
				if(circleList.indexOf(other, true) <= i) continue;
				narrowPhase(circle, other);
			}
			toCheck.clear();
		}
	}
	
	public void narrowPhase(Circle c1, Circle c2) {
		float dx = c2.particle.pos.x-c1.particle.pos.x;
		float dy = c2.particle.pos.y-c1.particle.pos.y;
		
		float l2 = dx*dx+dy*dy;
		float radSum = c1.r+c2.r;
		if(l2 < radSum*radSum) {
			CollisionInfo info1 = collisionPool.obtain();
			CollisionInfo info2 = collisionPool.obtain();
			info1.c1 = c1;
			info1.c2 = c2;
			info2.c1 = c2;
			info2.c2 = c1;
			if(c1.hasPhysics && c2.hasPhysics) {
				float l = (float)Math.sqrt(l2);
				info1.normal.set(dx, dy);
				info1.normal.scl(1f/l);
				info1.depth = radSum-l;
				toResolve.add(info1);
			}
			c1.group.addCollisionInfo(info1);
			c2.group.addCollisionInfo(info2);
		}
	}
	
	public void resolveCollisions() {
		
		for(int i = 0; i < toResolve.size(); i++) {
			CollisionInfo info = toResolve.get(i);
			Circle c1 = info.c1;
			Circle c2 = info.c2;
			
			float relXVel = c2.particle.vel.x-c1.particle.vel.x;
			float relYVel = c2.particle.vel.y-c1.particle.vel.y;
			
			float dp = info.normal.x*relXVel+info.normal.y*relYVel;
			float xImpulse = info.normal.x*dp;
			float yImpulse = info.normal.y*dp;
			
			c1.particle.vel.add(xImpulse, yImpulse);
			c2.particle.vel.sub(xImpulse, yImpulse);
			
			c1.particle.pos.sub(info.depth*info.normal.x*.5f, info.depth*info.normal.y*.5f);
			c2.particle.pos.add(info.depth*info.normal.x*.5f, info.depth*info.normal.y*.5f);
		}
		toResolve.clear();
		
		//resolve tile collisions
		for(int i = 0; i < tileToResolve.size; i++) {
			TileCollisionInfo info = tileToResolve.get(i);
			
			Vector2 vel = info.circle.particle.vel;
			
			float dp = info.normal.x*vel.x+info.normal.y*vel.y;
			float xImpulse = dp*info.normal.x;
			float yImpulse = dp*info.normal.y;
			
			info.circle.particle.pos.add(info.normal.x*info.depth, info.normal.y*info.depth);
			info.circle.particle.addImpulse(-xImpulse, -yImpulse);
		}
		tileToResolve.clear();
	}
	
	/**
	 * @param c circle in requested neigbourhood
	 * @param toCheck
	 * @return list of circles including c itself, excluding duplicates and circles in the same group as c;
	 */
	public List<Circle> getCirclesInNeighbourhood(Circle c, List<Circle> toCheck) {
		int startX = getXTile(c.getMinX());
		int startY = getYTile(c.getMinY());
		int endX = getXTile(c.getMaxX());
		int endY = getYTile(c.getMaxY());
			
		for(int x = startX; x <= endX; x++) {
			for(int y = startY; y <= endY; y++) {
				Tile tile = tiles[x+y*tWidth];
				
				//first, check tile collisions
				if(!tile.isEmpty()) {
					//circle center
					float cx = c.particle.pos.x;
					float cy = c.particle.pos.y;
					
					//tile center
					float hSize = tileSize/2; 		//half size
					float tcx = x*tileSize+hSize;
					float tcy = y*tileSize+hSize;
					
					float dx = cx-tcx;
					float dy = cy-tcy;
					
					TileCollisionInfo info = this.tileCollisionPool.obtain();
					float pointDistance = Float.MAX_VALUE;
					if(Math.abs(dx) < hSize && Math.abs(dy) < hSize) {
						//circle center inside square
						
					} else {
						//center outside of square, determine distance to edges and point
						float px = dx-Math.signum(dx)*hSize;
						float py = dy-Math.signum(dy)*hSize;
						pointDistance = (float)(Math.sqrt(px*px+py*py));
						info.normal.set(px, py);
					}
					
					float xEdgeDis = Math.abs(dx)-hSize;
					float yEdgeDis = Math.abs(dy)-hSize;
					
					if(xEdgeDis > 0 && yEdgeDis > 0) {
						//point - circle
						//normalise normal vector
						info.normal.scl(1f/pointDistance);
						
						info.depth = c.r-pointDistance;
					} else if(xEdgeDis > yEdgeDis) {
						//vertical edge - circle
						info.normal.set(Math.signum(dx), 0);
						info.depth = c.r-xEdgeDis;
					} else {
						//horizontal edge - circle
						info.normal.set(0, Math.signum(dy));
						info.depth = c.r-yEdgeDis;
					}
					
					//if distance to square < radius. no intersection. go away!!!
					if(info.depth > 0) {
						info.circle = c;
						c.group.addTileCollisionInfo(info);
						if(c.hasPhysics) {
							tileToResolve.add(info);
						}
					}
				}
				
				
				Array<Circle> cList = tile.circleList;
				for(Circle circle : cList) {
					if(circle.sameGroup(c)) continue;
					if(toCheck.contains(circle)) continue;
					toCheck.add(circle);
				}
			}
		}
		return toCheck;
	}
	
	public void update() {
		for(int i = constraintList.size-1; i>=0; i--) {
			Constraint c = constraintList.get(i);
			if(c.removed) 
				constraintList.removeIndex(i);
			else
				c.solve();
		}
		for(int i = groupList.size-1; i >= 0; i--) {
			Group group = groupList.get(i);
			group.update();
		}
		updateTiles();
		checkCollisions();
		resolveCollisions();
		checkWorldEdgeCollisions();
	}
	
	public Material castRayMaterial(Group exclude, float x, float y, float rayX, float rayY, float distance) {
		//traverse tile by tile. if intersection with an object that has a material is found (nonempty tile or circle) return material
		
		int tx = (int)(x/tileSize);
		int ty = (int)(y/tileSize);
		
		int xDir = rayX < 0 ? -1 : 1;
		int yDir = rayY < 0 ? -1 : 1;
		
		float atX = x;
		float atY = y;
		
		int maxTileSearch = tDiagonal;
		
		float length = 0;
		for(int i = 0; i < maxTileSearch; i++) {
			if(!inTileBounds(tx, ty)) return null;
			if(length > distance) return null;
			
			Tile t = tiles[ty*tWidth+tx];
			if(!t.isEmpty()) {
				return t.getMaterial();
			}
			
			Array<Circle> circles = t.circleList;
			int numCircles = circles.size;
			float minDis = Float.MAX_VALUE;
			Circle cMin = null;
			for(int j = 0; j < numCircles; j++) {
				Circle c = circles.get(j);
				if(c.group!=exclude) {
					//project midpoint of circle on  ray
					float cx = c.particle.pos.x-x;
					float cy = c.particle.pos.y-y;
					float dp = cx*rayX+cy*rayY;
					
					float xOnRay = dp*rayX;
					float yOnRay = dp*rayY;
					
					float dx = xOnRay-cx;
					float dy = yOnRay-cy;
					float len2 = dx*dx+dy*dy;
					if(len2 < c.r*c.r) {
						float appDist = xOnRay*xOnRay+yOnRay*yOnRay;
						if(appDist < minDis) {
							minDis = appDist;
							cMin = c;
						}
					}
				}
			}
			if(cMin!=null) {
				return cMin.group.material;
			}
			
			//calculate next, tx , ty, atX, atY
			float dx = (tx+xDir)*tileSize-atX;
			float xDist;
			if(rayX==0) 
				xDist = Float.MAX_VALUE;
			else
				xDist = dx/rayX;
			float dy = (ty+yDir)*tileSize-atY;
			float yDist;
			if(rayY==0)
				yDist = Float.MAX_VALUE;
			else
				yDist = dy/rayY;
		
			if(xDist < yDist) {
				//go to tile left or right
				tx+=xDir;
				atX+=rayX*xDist;
				atY+=rayY*xDist;
				length+=xDist;
			} else {
				ty+=yDir;
				atX+=rayX*yDist;
				atY+=rayY*yDist;
				length+=yDist;
			}
		}
		
		return null;
	}
	
	public boolean inTileBounds(int tx, int ty) {
		if(tx < 0) return false;
		if(ty < 0) return false;
		if(tx >= tWidth) return false;
		if(ty >= tHeight) return false;
		return true;
	}
	
	public boolean isFree(float x, float y) {
		int tx = (int)(x/tileSize);
		int ty = (int)(y/tileSize);
		if(!inTileBounds(tx, ty))
			return false;
		else {
			return tiles[tx+ty*tWidth].isEmpty();
		}
	}
	
	/**
	 * @param x
	 * @param y
	 * @return circle at (x, y). Note that this is the first circle that contains this point. It might be more.
	 */
	public Circle getCircleAt(float x, float y) {
		int tx = getXTile(x);
		int ty = getYTile(y);
		Tile t = tiles[tx+ty*tWidth];
		for(Circle circle : t.circleList) {
			if(circle.containsPoint(x, y))
				return circle;
		}
		return null;
	}
	
	public void FreeAll(Array<CollisionInfo> infoList) {
		collisionPool.freeAll(infoList);
	}
	
	public void FreeAllTileCollisions(Array<TileCollisionInfo> infoList) {
		tileCollisionPool.freeAll(infoList);
	}
	
	public Constraint makeConstraint(Circle c1, Circle c2) {
		Constraint constraint = new Constraint(c1.particle, c2.particle);
		c1.group.addConstraint(constraint);
		c2.group.addConstraint(constraint);
		constraintList.add(constraint);
		return constraint;
	}
	
	public void addGroup(Group group) {
		groupList.add(group);
		for(Constraint constraint : group.constraintList) {
			if(!constraintList.contains(constraint, true))
				constraintList.add(constraint);
		}
		circleList.addAll(group.circleList);
		group.setWorld(this);
	}
	
	public void removeGroup(Group group) {
		groupList.removeValue(group, true);
		for(Constraint c : group.constraintList) {
			c.removed = true;
		}
		circleList.removeAll(group.circleList, true);
	}
	
	public void addConstraint(Constraint c) {
		constraintList.add(c);
	}

	public Tile getTileAt(float x, float y) {
		int tx = getXTile(x);
		int ty = getYTile(y);
		return tiles[tx+ty*tWidth];
	}
}
