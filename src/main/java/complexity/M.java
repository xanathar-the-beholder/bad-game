package complexity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

public class M extends KeyAdapter {

	int x, y, n, d, t, invincible, scientists, lives;
	int maze[][];
	int todo[];
	int[] gt, gv, ga, gp;
	boolean restart, toggle;
	boolean[] gu;
	boolean[][][] gblk;
	double[] gbnd, gx, gy, gdx, gdy;
	int mazex[] = { 0, 0, -1, 1 };
	int mazey[] = { -1, 1, 0, 0 };
	int binh;
	double nx;
	double ny;
	//
	double dx;
	double dy;
	double a;
	//
	boolean bullet;

	private M() throws Exception {
		//
		JFrame gameFrame = new JFrame();
		gameFrame.setResizable(false);
		gameFrame.setSize(512, 512);
		gameFrame.setVisible(true);
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.createBufferStrategy(2);
		gameFrame.addKeyListener(this);
		//
		do {

			gu = new boolean[256]; // In use
			gt = new int[256]; // Type
			gv = new int[256]; // Visual Type
			ga = new int[256]; // animation frame
			gp = new int[256]; // general purpose counter.
			gblk = new boolean[256][8][8];
			gbnd = new double[256]; // Bounding Sphere
			gx = new double[256]; // X coordinates
			gy = new double[256]; // Y coordinates
			gdx = new double[256]; // dX
			gdy = new double[256]; // dY
			todo = new int[20 * 20];
			maze = new int[20][20];
			invincible = 100;
			lives = 3;
			scientists = 0;
			binh = 0;
			restart = false;
			toggle = false;
			//
			//
			//
			// Build the Maze
			//
			//
			gt[0] = 1; // player
			gv[0] = 1; // player
			gu[0] = true;
			gx[0] = 15;
			gy[0] = 15;
			gbnd[0] = 1;
			for (t = 1; t < 5; t++) {
				gt[t] = 2; // player bullet
				gv[t] = 2;
				gbnd[t] = 0.2;
				gt[t + 5] = 4; // Explosion
				gv[t + 5] = 4;
			}
			for (t = 10; t < 20; t++) {
				gt[t] = 11; // enemy bullet
				gv[t] = 2;
				gbnd[t] = 0.1;
				gt[t + 10] = 12; // enemy bullet Explosion
				gv[t + 10] = 4;
			}
			//
			// Generate maze
			//
			t = 0;
			for (x = 0; x < 20; ++x) {
				for (y = 0; y < 20; ++y) {
					maze[x][y] = (x == 0 || x == 19 || y == 0 || y == 19) ? 32 : 63;
				}
			}
			x = 1 + (int) (Math.random() * 18);
			y = 1 + (int) (Math.random() * 18);
			maze[x][y] &= ~48;
			for (d = 0; d < 4; ++d) {
				if ((maze[x + mazex[d]][y + mazey[d]] & 16) != 0) {
					todo[t++] = ((x + mazex[d]) << 16) | (y + mazey[d]);
					maze[x + mazex[d]][y + mazey[d]] &= ~16;
				}
			}
			while (t > 0) {
				n = (int) (Math.random() * t);
				x = todo[n] >> 16; /* the top 2 bytes of the data */
				y = todo[n] & 65535; /* the bottom 2 bytes of the data */
				todo[n] = todo[--t];
				do {
					d = (int) (Math.random() * 4);
				} while ((maze[x + mazex[d]][y + mazey[d]] & 32) != 0);
				maze[x][y] &= ~((1 << d) | 32);
				maze[x + mazex[d]][y + mazey[d]] &= ~(1 << (d ^ 1));
				for (d = 0; d < 4; ++d) {
					if ((maze[x + mazex[d]][y + mazey[d]] & 16) != 0) {
						todo[t++] = ((x + mazex[d]) << 16) | (y + mazey[d]);
						maze[x + mazex[d]][y + mazey[d]] &= ~16;
					}
				}
			}
			//
			// End of maze generation
			//
			int citem = 30;
			for (t = 0; t < 4; t++) {
				while (true) {
					x = (int) (Math.random() * 19);
					y = 10 + (int) (Math.random() * 9);
					if ((maze[x][y] & 3) == 3) {
						gt[citem] = 10; // person
						gv[citem] = 10;
						gu[citem] = true;
						gx[citem] = x * 10 + 5;
						gy[citem] = y * 10 + 8.5;
						gbnd[citem] = 1;
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
						if ((maze[x][y] & 3) == 2) { // open from above, closed bottom.
							gt[citem] = gv[citem] = 5; // block 8*8
							gu[citem] = true;
							gx[citem] = x * 10 + 5;
							gy[citem] = y * 10 + 5;
							gbnd[citem] = 8;
							citem++;
						} else if ((maze[x][y] & 3) == 3) {
							for (t = 0; t < (y < 10 ? 1 : 2); t++) {
								gt[citem] = gv[citem] = 8; // vertical laser
								gu[citem] = true;
								gx[citem] = x * 10 + 2 + t * 5;
								gp[citem] = (int) (50 * Math.random());
								gy[citem] = y * 10;
								citem++;
							}
						} else if ((maze[x][y] & 12) == 12) {
							for (t = 0; t < (y < 10 ? 1 : 2); t++) {
								gt[citem] = gv[citem] = 9; // horizontal laser
								gu[citem] = true;
								gx[citem] = x * 10;
								gy[citem] = y * 10 + 2 + t * 5;
								gp[citem] = (int) (50 * Math.random());
								citem++;
							}
						} else if (y > 5) {
							gt[citem] = gv[citem] = 6; // Turret
							gu[citem] = true;
							gx[citem] = x * 10 + 5;
							gy[citem] = y * 10 + 5;
							gbnd[citem] = 1;
							citem++;
						}
					}
				}
			}
			//
			//
			while (!restart) {
				//
				Graphics2D g = (Graphics2D) gameFrame.getBufferStrategy().getDrawGraphics();
				//
				//
				toggle = !toggle;
				//
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, 512, 512);
				g.setColor(Color.LIGHT_GRAY);
				g.translate(256, 256);
				g.scale(20, 20);
				g.translate(-gx[0], -gy[0]);
				//
				for (x = 1; x < 19; ++x) {
					for (y = 1; y < 19; ++y) {
						if ((maze[x][y] & 1) != 0) /* This cell has a top wall */
							g.drawLine(x * 10, y * 10, x * 10 + 10, y * 10);
						if ((maze[x][y] & 2) != 0) /* This cell has a bottom wall */
							g.drawLine(x * 10, y * 10 + 10, x * 10 + 10, y * 10 + 10);
						if ((maze[x][y] & 4) != 0) /* This cell has a left wall */
							g.drawLine(x * 10, y * 10, x * 10, y * 10 + 10);
						if ((maze[x][y] & 8) != 0) /* This cell has a right wall */
							g.drawLine(x * 10 + 10, y * 10, x * 10 + 10, y * 10 + 10);
					}
				}
				// rebirth
				if (invincible > 0)
					invincible--;
				if ((!gu[0]) && (invincible == 100)) {
					if (lives-- > 0) {
						gu[0] = true;
					}
				}
				for (int gobj = 0; gobj < 255; gobj++) {
					if (!gu[gobj])
						continue;
					if ((Math.abs(gx[0] - gx[gobj]) > 40) && (gt[gobj] != 2))
						continue;
					if ((Math.abs(gy[0] - gy[gobj]) > 40) && (gt[gobj] != 2))
						continue;
					//
					// Next Position, if no collision.
					//
					nx = gx[gobj] + gdx[gobj];
					ny = gy[gobj] + gdy[gobj];
					//
					dx = gx[0] - gx[gobj];
					dy = gy[0] - gy[gobj];
					a = Math.atan2(dy, dx);
					//
					bullet = false;
					movement(gobj);
					//
					// Collision detection (with maze).
					//
					mazeCollisions(gobj);
					//
					// Collision Detection with other.
					//
					collisionsWithOther(gobj);
					//
					stuff(gobj);
					//
					drawThings(g, gobj);
				}
				//
				gameOver(g);
				//
				moreStuff(g);
				//
				gameFrame.getBufferStrategy().show();
				Thread.sleep(20);
				//
			}
		} while (true);
	}

	private void moreStuff(Graphics2D g) {
		g.translate(10, 10);
		g.scale(0.01, 0.01);
		g.setColor(new Color(0, 128, 0, 128));
		g.fillRect(-5, -5, 210, 210);
		for (t = 0; t < 255; t++) {
			if (gu[t]) {
				n = gv[t];
				if ((n == 10) || (n == 1)) {
					g.setColor(n == 1 ? Color.WHITE : Color.CYAN);
					g.fillRect((int) gx[t], (int) gy[t], 10, 10);
				}
			}
		}
		for (t = 0; t < 4; t++) {
			if (lives > t) {
				g.setColor(Color.ORANGE);
				g.fillRect(-4 + t * 20, 205, 15, 15);
			}
			if (scientists > t) {
				g.setColor(Color.WHITE);
				g.fillRect(186 - t * 20, 205, 15, 15);
			}
		}
	}

	private void gameOver(Graphics2D g) {
		g.translate(gx[0], gy[0]);
		g.setColor(Color.WHITE);
		g.setFont(g.getFont().deriveFont(4f));
		if ((lives < 0) || (scientists == 4)) {
			g.drawString(lives < 0 ? "Game Over" : "Well Done", -10, 0);
			restart = k[KeyEvent.VK_SPACE];
			gx[0] += 0.1;
			if (gx[0] > 210) {
				gx[0] = -10;
			}
		}
	}

	private void drawThings(Graphics2D g, int gobj) {
		g.translate(gx[gobj], gy[gobj]);
		g.scale(0.1, 0.1);
		g.setColor(Color.WHITE);
		switch (gv[gobj]) {
		case 1:
			drawHeli(g, gobj);
			break;
		case 2:
			drawBullet(g);
			break;
		case 4:
			drawExplosion(g, gobj);
			break;
		case 5:
			drawBlocks(g, gobj);
			break;
		case 6:
			drawTurret(g, gobj);
			break;
		case 7:
			drawMissile(g, gobj);
			break;
		case 8:
			drawVerticalLaser(g, gobj);
			break;
		case 9:
			drawHorizontalLazer(g, gobj);
			break;
		case 10:
			drawScientist(g);
			break;
		}
		g.scale(10, 10);
		g.translate(-gx[gobj], -gy[gobj]);
		//
	}

	private void drawScientist(Graphics2D g) {
		// person 2 rescue
		g.setColor(Color.LIGHT_GRAY);
		g.drawOval(-2, -2, 4, 4);
		g.drawLine(0, 2, 0, 6);
		g.drawLine(-2, 4, 2, 4);
		g.drawLine(0, 6, -2, 8);
		g.drawLine(0, 6, 2, 8);
		//
	}

	private void drawHorizontalLazer(Graphics2D g, int gobj) {
		// horizontal laser
		g.drawLine(0, 0, 10, 0);
		g.drawLine(90, 0, 100, 0);
		if (gp[gobj] % 100 > 50) {
			g.setColor(toggle ? Color.RED : Color.YELLOW);
			g.drawLine(10, 0, 90, 0);
		}
	}

	private void drawVerticalLaser(Graphics2D g, int gobj) {
		// vertical laser
		g.drawLine(0, 0, 0, 10);
		g.drawLine(0, 90, 0, 100);
		if (gp[gobj] % 100 > 50) {
			g.setColor(toggle ? Color.RED : Color.YELLOW);
			g.drawLine(0, 10, 0, 90);
		}
	}

	private void drawMissile(Graphics2D g, int gobj) {
		// Missile
		a = Math.atan2(gdy[gobj], gdx[gobj]);
		g.rotate(a);
		g.drawLine(1, -1, 4, 0);
		g.drawLine(1, 1, 4, 0);
		g.drawLine(1, 1, 1, -1);
		g.setColor(toggle ? Color.RED : Color.ORANGE);
		g.drawLine(0, -1, -2, 0);
		g.drawLine(0, 1, -2, 0);
		// g.drawLine(0, 1, 0,-1);
		g.rotate(-a);
		//
	}

	private void drawTurret(Graphics2D g, int gobj) {
		// Turrets
		a = Math.toRadians(ga[gobj]);
		g.setColor(Color.RED);
		g.drawOval(-5, -5, 10, 10);
		g.rotate(a);
		g.setColor(Color.YELLOW);
		g.drawLine(5, 0, 8, 0);
		g.rotate(-a);
		//
	}

	private void drawBlocks(Graphics2D g, int gobj) {
		// Blocks
		// g.setColor(Color.CYAN);
		for (y = 0; y < 8; y++) {
			for (x = 0; x < 8; x++) {
				if (!gblk[gobj][y][x]) {
					g.fillRect(-43 + x * 11, -43 + y * 11, 9, 9);
				}
			}
		}
	}

	private void drawExplosion(Graphics2D g, int gobj) {
		// Explosion
		switch (ga[gobj]) {
		case 0:
			g.setColor(Color.RED);
			g.fillOval(-3, -2, 6, 4);
			gu[gobj] = false;
			break;
		case 1:
			g.setColor(Color.ORANGE);
			g.fillOval(-6, -3, 12, 6);
			break;
		case 2:
			g.setColor(Color.YELLOW);
			g.fillOval(-8, -3, 16, 6);
			break;
		default:
			for (t = 0; t < 8; t++) {
				g.setColor(new Color(255, 128 + (int) (127 * Math.random()), 0, (int) (255 * Math.random())));
				g.fillOval((int) (-12 * Math.random()), (int) (-6 * Math.random()), 12, 6);
			}
			if (ga[gobj] > 8) {
				for (t = 0; t < 3; t++) {
					g.setColor(new Color(128 + (int) (127 * Math.random()), 128 + (int) (127 * Math.random()), 255));
					g.drawLine(0, 0, -ga[gobj] / 2 + (int) (ga[gobj] * Math.random()),
							-ga[gobj] / 2 + (int) (ga[gobj] * Math.random()));
				}
			}
			break;
		}
		ga[gobj]--;
	}

	private void drawBullet(Graphics2D g) {
		// Bullet
		g.fillRect(-1, -1, 2, 2);
		//
	}

	private void drawHeli(Graphics2D g, int gobj) {
		g.rotate(gdx[gobj] * 0.4);
		//
		// Draw Heli
		//
		if (invincible > 0) {
			g.setColor(toggle ? Color.CYAN : Color.BLUE);
		}
		//
		g.drawOval(-5, -5, 10, 10);
		g.drawLine(0, -5, 0, -7);
		g.drawLine(toggle ? -10 : 10, -7, toggle ? 3 : -3, -7);
		if (gdx[0] >= 0.01) {
			gdx[0] += -0.01;
			g.drawLine(-5, 0, -12, 0);
			g.drawLine(-5, 7, 5, 7);
			g.drawLine(3, 7, 0, 5);
			g.drawLine(-11, toggle ? -1 : 1, -13, toggle ? 1 : -1);
		} else if (gdx[0] < -0.01) {
			gdx[0] += 0.01;
			g.drawLine(5, 0, 12, 0);
			g.drawLine(-5, 7, 5, 7);
			g.drawLine(-3, 7, 0, 5);
			g.drawLine(11, toggle ? -1 : 1, 13, toggle ? 1 : -1);
		} else {
			gdx[0] = 0;
			g.drawLine(0, 5, -5, 7);
			g.drawLine(0, 5, 5, 7);
		}
		//
		g.rotate(-gdx[gobj] * 0.4);
	}

	private void stuff(int gobj) {
		if ((gt[gobj] == 1) && (bullet)) {
			for (t = 0; t < 255; t++) {
				if ((gt[t] == 2) && (!gu[t])) {
					gu[t] = true;
					gx[t] = gx[0];
					gy[t] = gy[0];
					binh = 10;
					if (gdx[0] == 0) {
						gdx[t] = 0;
						gdy[t] = 0.3;
					} else {
						gdx[t] = gdx[0] > 0 ? 0.4 : -0.4;
						gdy[t] = gdy[0] + Math.abs(gdx[0] / 8);
					}
					break;
				}
			}
		}
	}

	private void collisionsWithOther(int gobj) {
		for (int gcol = 0; gcol < 255; gcol++) {
			if (!gu[gcol])
				continue;
			if (gcol == gobj)
				continue;
			boolean hit = false;
			hit = hitSomething(gobj, gcol, hit);
			if (!hit) {
				if (gbnd[gcol] == 0)
					continue;
				if (gbnd[gobj] == 0)
					continue;
				//
				dx = gx[gcol] - gx[gobj];
				dy = gy[gcol] - gy[gobj];
				if (dx * dx + dy * dy < (gbnd[gcol] + gbnd[gobj]) * (gbnd[gcol] + gbnd[gobj])) {
					//
					// HIT!
					//
					if (gt[gcol] == 5) {
						int rx = (int) Math.floor(0.88 * (gx[gobj] - gx[gcol]) + 4);
						int ry = (int) Math.floor(0.88 * (gy[gobj] - gy[gcol]) + 4);
						if ((rx < 0) || (ry < 0))
							continue;
						if ((rx > 7) || (ry > 7))
							continue;
						hit = somethingHit(gobj, gcol, hit, rx, ry);
					} else {
						hit = true;
					}
				}
			}
			if (hit) {
				if (gt[gobj] == 1) {
					if (gt[gcol] == 2)
						continue;
					if (gt[gcol] == 10) {
						gu[gcol] = false;
						scientists++;
						lives++;
						continue;
					}
					if (invincible > 0) {
						if (gt[gcol] != 5)
							gu[gcol] = false;
						continue;
					}
					andAnotherThingHit(gobj);
				} else if ((gt[gobj] == 2) && ((gt[gcol] == 3) || (gt[gcol] == 6) || (gt[gcol] == 11))) {
					anotherThingHit(gobj, gcol);
				}
			}
		}
	}

	private void andAnotherThingHit(int gobj) {
		gu[gobj] = false;
		gu[6] = true;
		gx[6] = gx[gobj];
		gy[6] = gy[gobj];
		ga[6] = 16;
		invincible = 200;
	}

	private void anotherThingHit(int gobj, int gcol) {
		gu[gcol] = false;
		gu[gobj] = false;
		gu[gobj + 5] = true;
		gx[gobj + 5] = gx[gcol];
		gy[gobj + 5] = gy[gcol];
		ga[gobj + 5] = 12;
	}

	private boolean hitSomething(int gobj, int gcol, boolean hit) {
		if ((gt[gcol] == 8) || (gt[gcol] == 9)) {
			if (gp[gcol] % 100 > 50) {
				if (gt[gcol] == 8) {
					if ((gy[gobj] - gy[gcol] < 9) && (gy[gobj] - gy[gcol] > 0)) {
						hit = (Math.abs(gx[gcol] - gx[gobj]) < 1);
					}
				} else {
					if ((gx[gobj] - gx[gcol] < 9) && (gx[gobj] - gx[gcol] > 1)) {
						hit = (Math.abs(gy[gcol] - gy[gobj]) < 1);
					}
				}
			}
		}
		return hit;
	}

	private boolean somethingHit(int gobj, int gcol, boolean hit, int rx, int ry) {
		if (!gblk[gcol][ry][rx]) {
			gblk[gcol][ry][rx] = true;
			switch (gt[gobj]) {
			case 1:
				hit = true;
				break;
			case 2:
				// plr bullet
				gu[gobj + 5] = true;
				gx[gobj + 5] = gx[gobj];// +rx*1.125-4;
				gy[gobj + 5] = gy[gobj];// +ry*1.125-4;
				ga[gobj + 5] = 4;
			case 11:
				// enemy bullet
				gu[gobj] = false;
				break;
			}
		}
		return hit;
	}

	private void mazeCollisions(int gobj) {
		d = ((int) nx) % 10;
		t = ((int) ny) % 10;
		x = ((int) nx) / 10;
		y = ((int) ny) / 10;
		//
		if (((t < 1) && ((maze[x][y] & 1) != 0)) || ((t >= 9) && ((maze[x][y] & 2) != 0))
				|| ((d < 1) && ((maze[x][y] & 4) != 0)) || ((d >= 9) && ((maze[x][y] & 8) != 0))) {
			gdx[gobj] = 0;
			gdy[gobj] = 0;
			if (gt[gobj] == 2) {
				gu[gobj] = false;
				gu[gobj + 5] = true;
				gx[gobj + 5] = gx[gobj];
				gy[gobj + 5] = gy[gobj];
				ga[gobj + 5] = 1;
			}
			if (gt[gobj] == 11) {
				gu[gobj] = false;
				gu[gobj + 10] = true;
				gx[gobj + 10] = gx[gobj];
				gy[gobj + 10] = gy[gobj];
				ga[gobj + 10] = 1;
			}
		} else {
			gx[gobj] = nx;
			gy[gobj] = ny;
		}
	}

	private void movement(int gobj) {
		switch (gt[gobj]) {
		case 1:
			movementOne();
			break;
		case 6:
			movementSix(gobj);
			break;
		case 11:
			movementEleven(gobj);
			break;
		case 8:
		case 9:
			gp[gobj]++;
			break;
		}
	}

	private void movementEleven(int gobj) {
		if (gv[gobj] == 7) {
			gdx[gobj] += (gdx[gobj] < 0.15 * Math.cos(a) ? 0.005 : -0.005);
			gdy[gobj] += (gdy[gobj] < 0.15 * Math.sin(a) ? 0.005 : -0.005);
		}
	}

	private void movementSix(int gobj) {
		//
		ga[gobj] = (int) Math.toDegrees(a);
		//
		if (gp[gobj] <= 0) {
			if (Math.abs(dx * dx + dy * dy) < 200) {
				gp[gobj] = 64;
				for (t = 0; t < 255; t++) {
					if ((gt[t] == 11) && (!gu[t])) {
						gu[t] = true;
						gx[t] = gx[gobj];
						gy[t] = gy[gobj];
						gdx[t] = 0.2 * Math.cos(a);
						gdy[t] = 0.2 * Math.sin(a);
						gv[t] = gy[gobj] > 150 ? 7 : 2;
						break;
					}
				}
			}
		} else {
			gp[gobj]--;
		}
	}

	private void movementOne() {
		bullet = k[KeyEvent.VK_SPACE] && (binh == 0);
		if (binh > 0)
			binh--;
		//
		if ((k[KeyEvent.VK_W]) && (gdy[0] > -0.2))
			gdy[0] += -0.05;
		if ((k[KeyEvent.VK_S]) && (gdy[0] < 0.2))
			gdy[0] += 0.05;
		if ((k[KeyEvent.VK_A]) && (gdx[0] > -0.3))
			gdx[0] += -0.05;
		if ((k[KeyEvent.VK_D]) && (gdx[0] < 0.3))
			gdx[0] += 0.05;
		//
		if (gdy[0] > 0.02) {
			gdy[0] += -0.01;
		} else if (gdy[0] < 0) {
			gdy[0] += 0.01;
		} else {
			gdy[0] = 0.01;
		}
		//
	}

	public void keyPressed(KeyEvent e) {
		k[e.getKeyCode()] = true;
	}

	public void keyReleased(KeyEvent e) {
		k[e.getKeyCode()] = false;
	}

	private boolean[] k = new boolean[65538];

	public static void main(String[] args) throws Exception {
		new M();
	}
}
