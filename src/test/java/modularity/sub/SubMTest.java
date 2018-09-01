package modularity.sub;

import java.awt.Graphics2D;

import org.junit.Test;

public class SubMTest {

	private boolean hit;
	private int gcol;
	private int gobj;
	private double[] gy;
	private double[] gx;
	private int[] gp;
	private int[] gt;
	private double a;
	private Graphics2D g;
	private double[] gdy;
	private double[] gdx;
	private boolean[][][] gblk;
	private boolean[] gu;
	private boolean toggle;
	private int[] gv;
	private int[] ga;
	private int invincible;
	private int scientists;
	private int lives;
	private double ny;
	private double nx;
	private int[][] maze;
	private double[] gbnd;
	private boolean bullet;
	private double dy;
	private double dx;
	private int binh;

	@Test
	public void should() {
		SubM s = new SubM(null);
		s.collisionDetection(gt, gp, gx, gy, gobj, gcol, hit);
		s.drawGame(invincible, gv, ga, gp, toggle, gu, gblk, gx, gy, gdx, gdy, g, gobj, a);
		s.drawGameOver(scientists, lives, gx, gy, g);
		s.drawLives(scientists, lives, g);
		s.drawRadar(gv, gu, gx, gy, g);
		s.dunno(ga, gu, gx, gy, gobj, gcol);
		s.handleBullet(gu, gx, gy, gdx, gdy);
		s.handleMazeCollisions(maze, gt, ga, gu, gx, gy, gdx, gdy, gobj, nx, ny);
		s.placeItems(maze, gt, gv, gp, gu, gbnd, gx, gy);
		s.respawn(ga, gu, gx, gy, gobj);
		s.saveScientist(scientists, gu, gcol);
		s.updateGameObject(gt, gv, ga, gp, gu, gx, gy, gdx, gdy, binh, gobj, dx, dy, a, bullet);
		
	}
	
}
