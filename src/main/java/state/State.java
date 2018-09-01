package state;

import java.awt.Graphics2D;

public class State {

	private int t, x, y, d, n;
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

	public void initialize() {
		this.gu = new boolean[256]; // In use
		this.gt = new int[256]; // Type
		this.gv = new int[256]; // Visual Type
		this.ga = new int[256]; // animation frame
		this.gp = new int[256]; // general purpose counter.
		this.gblk = new boolean[256][8][8];
		this.gbnd = new double[256]; // Bounding Sphere
		this.gx = new double[256]; // X coordinates
		this.gy = new double[256]; // Y coordinates
		this.gdx = new double[256]; // dX
		this.gdy = new double[256]; // dY
		this.todo = new int[20 * 20];
		this.maze = new int[20][20];
		this.invincible = 100;
		this.lives = 3;
		this.scientists = 0;
		this.binh = 0;
		this.restart = false;
		this.toggle = false;
	}
	
	public void rebirth() {
		if (this.invincible > 0)
			this.invincible--;
		if ((!this.gu[0]) && (this.invincible == 100)) {
			if (this.lives-- > 0) {
				this.gu[0] = true;
			}
		}
	}
	
	public void drawMaze(Graphics2D g) {
		for (x = 1; x < 19; ++x) {
			for (y = 1; y < 19; ++y) {
				if ((this.maze[x][y] & 1) != 0) /* This cell has a top wall */
					g.drawLine(x * 10, y * 10, x * 10 + 10, y * 10);
				if ((this.maze[x][y] & 2) != 0) /* This cell has a bottom wall */
					g.drawLine(x * 10, y * 10 + 10, x * 10 + 10, y * 10 + 10);
				if ((this.maze[x][y] & 4) != 0) /* This cell has a left wall */
					g.drawLine(x * 10, y * 10, x * 10, y * 10 + 10);
				if ((this.maze[x][y] & 8) != 0) /* This cell has a right wall */
					g.drawLine(x * 10 + 10, y * 10, x * 10 + 10, y * 10 + 10);
			}
		}
	}

	public void initializeGame() {
		this.gt[0] = 1; // player
		this.gv[0] = 1; // player
		this.gu[0] = true;
		this.gx[0] = 15;
		this.gy[0] = 15;
		this.gbnd[0] = 1;
		for (t = 1; t < 5; t++) {
			this.gt[t] = 2; // player bullet
			this.gv[t] = 2;
			this.gbnd[t] = 0.2;
			this.gt[t + 5] = 4; // Explosion
			this.gv[t + 5] = 4;
		}
		for (t = 10; t < 20; t++) {
			this.gt[t] = 11; // enemy bullet
			this.gv[t] = 2;
			this.gbnd[t] = 0.1;
			this.gt[t + 10] = 12; // enemy bullet Explosion
			this.gv[t + 10] = 4;
		}
		//
		// Generate this.maze
		//
		t = 0;
		for (x = 0; x < 20; ++x) {
			for (y = 0; y < 20; ++y) {
				this.maze[x][y] = (x == 0 || x == 19 || y == 0 || y == 19) ? 32 : 63;
			}
		}
		x = 1 + (int) (Math.random() * 18);
		y = 1 + (int) (Math.random() * 18);
		this.maze[x][y] &= ~48;
		for (d = 0; d < 4; ++d) {
			if ((this.maze[x + this.mazex[d]][y + this.mazey[d]] & 16) != 0) {
				this.todo[t++] = ((x + this.mazex[d]) << 16) | (y + this.mazey[d]);
				this.maze[x + this.mazex[d]][y + this.mazey[d]] &= ~16;
			}
		}
		while (t > 0) {
			n = (int) (Math.random() * t);
			x = this.todo[n] >> 16; /* the top 2 bytes of the data */
			y = this.todo[n] & 65535; /* the bottom 2 bytes of the data */
			this.todo[n] = this.todo[--t];
			do {
				d = (int) (Math.random() * 4);
			} while ((this.maze[x + this.mazex[d]][y + this.mazey[d]] & 32) != 0);
			this.maze[x][y] &= ~((1 << d) | 32);
			this.maze[x + this.mazex[d]][y + this.mazey[d]] &= ~(1 << (d ^ 1));
			for (d = 0; d < 4; ++d) {
				if ((this.maze[x + this.mazex[d]][y + this.mazey[d]] & 16) != 0) {
					this.todo[t++] = ((x + this.mazex[d]) << 16) | (y + this.mazey[d]);
					this.maze[x + this.mazex[d]][y + this.mazey[d]] &= ~16;
				}
			}
		}
		//
		// End of this.maze generation
		//
		int citem = 30;
		for (t = 0; t < 4; t++) {
			while (true) {
				x = (int) (Math.random() * 19);
				y = 10 + (int) (Math.random() * 9);
				if ((this.maze[x][y] & 3) == 3) {
					this.gt[citem] = 10; // person
					this.gv[citem] = 10;
					this.gu[citem] = true;
					this.gx[citem] = x * 10 + 5;
					this.gy[citem] = y * 10 + 8.5;
					this.gbnd[citem] = 1;
					citem++;
					break;
				}
			}
		}
		for (x = 2; x < 19; ++x) {
			for (y = 2; y < 19; ++y) {
				if (citem >= 255)
					continue; // out of cells.
				if (Math.random() < 0.25 + y / 40.0) {
					if ((this.maze[x][y] & 3) == 2) { // open from above, closed bottom.
						this.gt[citem] = this.gv[citem] = 5; // block 8*8
						this.gu[citem] = true;
						this.gx[citem] = x * 10 + 5;
						this.gy[citem] = y * 10 + 5;
						this.gbnd[citem] = 8;
						citem++;
					} else if ((this.maze[x][y] & 3) == 3) {
						for (t = 0; t < (y < 10 ? 1 : 2); t++) {
							this.gt[citem] = this.gv[citem] = 8; // vertical laser
							this.gu[citem] = true;
							this.gx[citem] = x * 10 + 2 + t * 5;
							this.gp[citem] = (int) (50 * Math.random());
							this.gy[citem] = y * 10;
							citem++;
						}
					} else if ((this.maze[x][y] & 12) == 12) {
						for (t = 0; t < (y < 10 ? 1 : 2); t++) {
							this.gt[citem] = this.gv[citem] = 9; // horizontal laser
							this.gu[citem] = true;
							this.gx[citem] = x * 10;
							this.gy[citem] = y * 10 + 2 + t * 5;
							this.gp[citem] = (int) (50 * Math.random());
							citem++;
						}
					} else if (y > 5) {
						this.gt[citem] = this.gv[citem] = 6; // Turret
						this.gu[citem] = true;
						this.gx[citem] = x * 10 + 5;
						this.gy[citem] = y * 10 + 5;
						this.gbnd[citem] = 1;
						citem++;
					}
				}
			}
		}
	}
}
