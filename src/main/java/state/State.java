package state;

public class State {

	public int invincible, scientists, lives;
	public int maze[][];
	public int todo[];
	public int[] gt, gv, ga, gp;
	public boolean restart, toggle;
	public boolean[] gu;
	public boolean[][][] gblk;
	public double[] gbnd, gx, gy, gdx, gdy;
	public int mazex[] = { 0, 0, -1, 1 };
	public int mazey[] = { -1, 1, 0, 0 };
	public int binh;
	
}
