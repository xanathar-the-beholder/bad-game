import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

import state.State;

public class M2 extends KeyAdapter {

	JFrame gameFrame;
	State state = new State();
	int x, y, n, d, t; 

	private M2() throws Exception {
		//
		gameFrame = new JFrame();
		gameFrame.setResizable(false);
		gameFrame.setSize(512, 512);
		gameFrame.setVisible(true);
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.createBufferStrategy(2);
		gameFrame.addKeyListener(this);
		//
	}

	private void run() throws InterruptedException {
		do {
			state.gu = new boolean[256]; // In use
			state.gt = new int[256]; // Type
			state.gv = new int[256]; // Visual Type
			state.ga = new int[256]; // animation frame
			state.gp = new int[256]; // general purpose counter.
			state.gblk = new boolean[256][8][8];
			state.gbnd = new double[256]; // Bounding Sphere
			state.gx = new double[256]; // X coordinates
			state.gy = new double[256]; // Y coordinates
			state.gdx = new double[256]; // dX
			state.gdy = new double[256]; // dY
			state.todo = new int[20 * 20];
			state.maze = new int[20][20];
			state.invincible = 100;
			state.lives = 3;
			state.scientists = 0;
			state.binh = 0;
			state.restart = false;
			state.toggle = false;
			//
			//
			//
			// Build the state.maze
			//
			//
			state.gt[0] = 1; // player
			state.gv[0] = 1; // player
			state.gu[0] = true;
			state.gx[0] = 15;
			state.gy[0] = 15;
			state.gbnd[0] = 1;
			for (t = 1; t < 5; t++) {
				state.gt[t] = 2; // player bullet
				state.gv[t] = 2;
				state.gbnd[t] = 0.2;
				state.gt[t + 5] = 4; // Explosion
				state.gv[t + 5] = 4;
			}
			for (t = 10; t < 20; t++) {
				state.gt[t] = 11; // enemy bullet
				state.gv[t] = 2;
				state.gbnd[t] = 0.1;
				state.gt[t + 10] = 12; // enemy bullet Explosion
				state.gv[t + 10] = 4;
			}
			//
			// Generate state.maze
			//
			t = 0;
			for (x = 0; x < 20; ++x) {
				for (y = 0; y < 20; ++y) {
					state.maze[x][y] = (x == 0 || x == 19 || y == 0 || y == 19) ? 32 : 63;
				}
			}
			x = 1 + (int) (Math.random() * 18);
			y = 1 + (int) (Math.random() * 18);
			state.maze[x][y] &= ~48;
			for (d = 0; d < 4; ++d) {
				if ((state.maze[x + state.mazex[d]][y + state.mazey[d]] & 16) != 0) {
					state.todo[t++] = ((x + state.mazex[d]) << 16) | (y + state.mazey[d]);
					state.maze[x + state.mazex[d]][y + state.mazey[d]] &= ~16;
				}
			}
			while (t > 0) {
				n = (int) (Math.random() * t);
				x = state.todo[n] >> 16; /* the top 2 bytes of the data */
				y = state.todo[n] & 65535; /* the bottom 2 bytes of the data */
				state.todo[n] = state.todo[--t];
				do {
					d = (int) (Math.random() * 4);
				} while ((state.maze[x + state.mazex[d]][y + state.mazey[d]] & 32) != 0);
				state.maze[x][y] &= ~((1 << d) | 32);
				state.maze[x + state.mazex[d]][y + state.mazey[d]] &= ~(1 << (d ^ 1));
				for (d = 0; d < 4; ++d) {
					if ((state.maze[x + state.mazex[d]][y + state.mazey[d]] & 16) != 0) {
						state.todo[t++] = ((x + state.mazex[d]) << 16) | (y + state.mazey[d]);
						state.maze[x + state.mazex[d]][y + state.mazey[d]] &= ~16;
					}
				}
			}
			//
			// End of state.maze generation
			//
			int citem = 30;
			for (t = 0; t < 4; t++) {
				while (true) {
					x = (int) (Math.random() * 19);
					y = 10 + (int) (Math.random() * 9);
					if ((state.maze[x][y] & 3) == 3) {
						state.gt[citem] = 10; // person
						state.gv[citem] = 10;
						state.gu[citem] = true;
						state.gx[citem] = x * 10 + 5;
						state.gy[citem] = y * 10 + 8.5;
						state.gbnd[citem] = 1;
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
						if ((state.maze[x][y] & 3) == 2) { // open from above, closed bottom.
							state.gt[citem] = state.gv[citem] = 5; // block 8*8
							state.gu[citem] = true;
							state.gx[citem] = x * 10 + 5;
							state.gy[citem] = y * 10 + 5;
							state.gbnd[citem] = 8;
							citem++;
						} else if ((state.maze[x][y] & 3) == 3) {
							for (t = 0; t < (y < 10 ? 1 : 2); t++) {
								state.gt[citem] = state.gv[citem] = 8; // vertical laser
								state.gu[citem] = true;
								state.gx[citem] = x * 10 + 2 + t * 5;
								state.gp[citem] = (int) (50 * Math.random());
								state.gy[citem] = y * 10;
								citem++;
							}
						} else if ((state.maze[x][y] & 12) == 12) {
							for (t = 0; t < (y < 10 ? 1 : 2); t++) {
								state.gt[citem] = state.gv[citem] = 9; // horizontal laser
								state.gu[citem] = true;
								state.gx[citem] = x * 10;
								state.gy[citem] = y * 10 + 2 + t * 5;
								state.gp[citem] = (int) (50 * Math.random());
								citem++;
							}
						} else if (y > 5) {
							state.gt[citem] = state.gv[citem] = 6; // Turret
							state.gu[citem] = true;
							state.gx[citem] = x * 10 + 5;
							state.gy[citem] = y * 10 + 5;
							state.gbnd[citem] = 1;
							citem++;
						}
					}
				}
			}
			//
			//
			while (!state.restart) {
				//
				Graphics2D g = (Graphics2D) gameFrame.getBufferStrategy().getDrawGraphics();
				//
				//
				state.toggle = !state.toggle;
				//
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, 512, 512);
				g.setColor(Color.LIGHT_GRAY);
				g.translate(256, 256);
				g.scale(20, 20);
				g.translate(-state.gx[0], -state.gy[0]);
				//
				for (x = 1; x < 19; ++x) {
					for (y = 1; y < 19; ++y) {
						if ((state.maze[x][y] & 1) != 0) /* This cell has a top wall */
							g.drawLine(x * 10, y * 10, x * 10 + 10, y * 10);
						if ((state.maze[x][y] & 2) != 0) /* This cell has a bottom wall */
							g.drawLine(x * 10, y * 10 + 10, x * 10 + 10, y * 10 + 10);
						if ((state.maze[x][y] & 4) != 0) /* This cell has a left wall */
							g.drawLine(x * 10, y * 10, x * 10, y * 10 + 10);
						if ((state.maze[x][y] & 8) != 0) /* This cell has a right wall */
							g.drawLine(x * 10 + 10, y * 10, x * 10 + 10, y * 10 + 10);
					}
				}
				// rebirth
				if (state.invincible > 0)
					state.invincible--;
				if ((!state.gu[0]) && (state.invincible == 100)) {
					if (state.lives-- > 0) {
						state.gu[0] = true;
					}
				}
				for (int gobj = 0; gobj < 255; gobj++) {
					if (!state.gu[gobj])
						continue;
					if ((Math.abs(state.gx[0] - state.gx[gobj]) > 40) && (state.gt[gobj] != 2))
						continue;
					if ((Math.abs(state.gy[0] - state.gy[gobj]) > 40) && (state.gt[gobj] != 2))
						continue;
					//
					// Next Position, if no collision.
					//
					double nx = state.gx[gobj] + state.gdx[gobj];
					double ny = state.gy[gobj] + state.gdy[gobj];
					//
					double dx = state.gx[0] - state.gx[gobj];
					double dy = state.gy[0] - state.gy[gobj];
					double a = Math.atan2(dy, dx);
					//
					boolean bullet = false;
					switch (state.gt[gobj]) {
					case 1:
						bullet = k[KeyEvent.VK_SPACE] && (state.binh == 0);
						if (state.binh > 0)
							state.binh--;
						//
						if ((k[KeyEvent.VK_W]) && (state.gdy[0] > -0.2))
							state.gdy[0] += -0.05;
						if ((k[KeyEvent.VK_S]) && (state.gdy[0] < 0.2))
							state.gdy[0] += 0.05;
						if ((k[KeyEvent.VK_A]) && (state.gdx[0] > -0.3))
							state.gdx[0] += -0.05;
						if ((k[KeyEvent.VK_D]) && (state.gdx[0] < 0.3))
							state.gdx[0] += 0.05;
						//
						if (state.gdy[0] > 0.02) {
							state.gdy[0] += -0.01;
						} else if (state.gdy[0] < 0) {
							state.gdy[0] += 0.01;
						} else {
							state.gdy[0] = 0.01;
						}
						//
						break;
					case 6:
						//
						state.ga[gobj] = (int) Math.toDegrees(a);
						//
						if (state.gp[gobj] <= 0) {
							if (Math.abs(dx * dx + dy * dy) < 200) {
								state.gp[gobj] = 64;
								for (t = 0; t < 255; t++) {
									if ((state.gt[t] == 11) && (!state.gu[t])) {
										state.gu[t] = true;
										state.gx[t] = state.gx[gobj];
										state.gy[t] = state.gy[gobj];
										state.gdx[t] = 0.2 * Math.cos(a);
										state.gdy[t] = 0.2 * Math.sin(a);
										state.gv[t] = state.gy[gobj] > 150 ? 7 : 2;
										break;
									}
								}
							}
						} else {
							state.gp[gobj]--;
						}
						break;
					case 11:
						if (state.gv[gobj] == 7) {
							state.gdx[gobj] += (state.gdx[gobj] < 0.15 * Math.cos(a) ? 0.005 : -0.005);
							state.gdy[gobj] += (state.gdy[gobj] < 0.15 * Math.sin(a) ? 0.005 : -0.005);
						}
						break;
					case 8:
					case 9:
						state.gp[gobj]++;
						break;
					}
					//
					// Collision detection (with state.maze).
					//
					d = ((int) nx) % 10;
					t = ((int) ny) % 10;
					x = ((int) nx) / 10;
					y = ((int) ny) / 10;
					//
					if (((t < 1) && ((state.maze[x][y] & 1) != 0)) || ((t >= 9) && ((state.maze[x][y] & 2) != 0))
							|| ((d < 1) && ((state.maze[x][y] & 4) != 0)) || ((d >= 9) && ((state.maze[x][y] & 8) != 0))) {
						state.gdx[gobj] = 0;
						state.gdy[gobj] = 0;
						if (state.gt[gobj] == 2) {
							state.gu[gobj] = false;
							state.gu[gobj + 5] = true;
							state.gx[gobj + 5] = state.gx[gobj];
							state.gy[gobj + 5] = state.gy[gobj];
							state.ga[gobj + 5] = 1;
						}
						if (state.gt[gobj] == 11) {
							state.gu[gobj] = false;
							state.gu[gobj + 10] = true;
							state.gx[gobj + 10] = state.gx[gobj];
							state.gy[gobj + 10] = state.gy[gobj];
							state.ga[gobj + 10] = 1;
						}
					} else {
						state.gx[gobj] = nx;
						state.gy[gobj] = ny;
					}
					//
					// Collision Detection with other.
					//
					for (int gcol = 0; gcol < 255; gcol++) {
						if (!state.gu[gcol])
							continue;
						if (gcol == gobj)
							continue;
						boolean hit = false;
						if ((state.gt[gcol] == 8) || (state.gt[gcol] == 9)) {
							if (state.gp[gcol] % 100 > 50) {
								if (state.gt[gcol] == 8) {
									if ((state.gy[gobj] - state.gy[gcol] < 9) && (state.gy[gobj] - state.gy[gcol] > 0)) {
										hit = (Math.abs(state.gx[gcol] - state.gx[gobj]) < 1);
									}
								} else {
									if ((state.gx[gobj] - state.gx[gcol] < 9) && (state.gx[gobj] - state.gx[gcol] > 1)) {
										hit = (Math.abs(state.gy[gcol] - state.gy[gobj]) < 1);
									}
								}
							}
						}
						if (!hit) {
							if (state.gbnd[gcol] == 0)
								continue;
							if (state.gbnd[gobj] == 0)
								continue;
							//
							dx = state.gx[gcol] - state.gx[gobj];
							dy = state.gy[gcol] - state.gy[gobj];
							if (dx * dx + dy * dy < (state.gbnd[gcol] + state.gbnd[gobj]) * (state.gbnd[gcol] + state.gbnd[gobj])) {
								//
								// HIT!
								//
								if (state.gt[gcol] == 5) {
									int rx = (int) Math.floor(0.88 * (state.gx[gobj] - state.gx[gcol]) + 4);
									int ry = (int) Math.floor(0.88 * (state.gy[gobj] - state.gy[gcol]) + 4);
									if ((rx < 0) || (ry < 0))
										continue;
									if ((rx > 7) || (ry > 7))
										continue;
									if (!state.gblk[gcol][ry][rx]) {
										state.gblk[gcol][ry][rx] = true;
										switch (state.gt[gobj]) {
										case 1:
											hit = true;
											break;
										case 2:
											// plr bullet
											state.gu[gobj + 5] = true;
											state.gx[gobj + 5] = state.gx[gobj];// +rx*1.125-4;
											state.gy[gobj + 5] = state.gy[gobj];// +ry*1.125-4;
											state.ga[gobj + 5] = 4;
										case 11:
											// enemy bullet
											state.gu[gobj] = false;
											break;
										}
									}
								} else {
									hit = true;
								}
							}
						}
						if (hit) {
							if (state.gt[gobj] == 1) {
								if (state.gt[gcol] == 2)
									continue;
								if (state.gt[gcol] == 10) {
									state.gu[gcol] = false;
									state.scientists++;
									state.lives++;
									continue;
								}
								if (state.invincible > 0) {
									if (state.gt[gcol] != 5)
										state.gu[gcol] = false;
									continue;
								}
								state.gu[gobj] = false;
								state.gu[6] = true;
								state.gx[6] = state.gx[gobj];
								state.gy[6] = state.gy[gobj];
								state.ga[6] = 16;
								state.invincible = 200;
							} else if ((state.gt[gobj] == 2) && ((state.gt[gcol] == 3) || (state.gt[gcol] == 6) || (state.gt[gcol] == 11))) {
								state.gu[gcol] = false;
								state.gu[gobj] = false;
								state.gu[gobj + 5] = true;
								state.gx[gobj + 5] = state.gx[gcol];
								state.gy[gobj + 5] = state.gy[gcol];
								state.ga[gobj + 5] = 12;
							}
						}
					}
					//
					if ((state.gt[gobj] == 1) && (bullet)) {
						for (t = 0; t < 255; t++) {
							if ((state.gt[t] == 2) && (!state.gu[t])) {
								state.gu[t] = true;
								state.gx[t] = state.gx[0];
								state.gy[t] = state.gy[0];
								state.binh = 10;
								if (state.gdx[0] == 0) {
									state.gdx[t] = 0;
									state.gdy[t] = 0.3;
								} else {
									state.gdx[t] = state.gdx[0] > 0 ? 0.4 : -0.4;
									state.gdy[t] = state.gdy[0] + Math.abs(state.gdx[0] / 8);
								}
								break;
							}
						}
					}
					//
					g.translate(state.gx[gobj], state.gy[gobj]);
					g.scale(0.1, 0.1);
					g.setColor(Color.WHITE);
					switch (state.gv[gobj]) {
					case 1:
						g.rotate(state.gdx[gobj] * 0.4);
						//
						// Draw Heli
						//
						if (state.invincible > 0) {
							g.setColor(state.toggle ? Color.CYAN : Color.BLUE);
						}
						//
						g.drawOval(-5, -5, 10, 10);
						g.drawLine(0, -5, 0, -7);
						g.drawLine(state.toggle ? -10 : 10, -7, state.toggle ? 3 : -3, -7);
						if (state.gdx[0] >= 0.01) {
							state.gdx[0] += -0.01;
							g.drawLine(-5, 0, -12, 0);
							g.drawLine(-5, 7, 5, 7);
							g.drawLine(3, 7, 0, 5);
							g.drawLine(-11, state.toggle ? -1 : 1, -13, state.toggle ? 1 : -1);
						} else if (state.gdx[0] < -0.01) {
							state.gdx[0] += 0.01;
							g.drawLine(5, 0, 12, 0);
							g.drawLine(-5, 7, 5, 7);
							g.drawLine(-3, 7, 0, 5);
							g.drawLine(11, state.toggle ? -1 : 1, 13, state.toggle ? 1 : -1);
						} else {
							state.gdx[0] = 0;
							g.drawLine(0, 5, -5, 7);
							g.drawLine(0, 5, 5, 7);
						}
						//
						g.rotate(-state.gdx[gobj] * 0.4);
						break;
					case 2:
						// Bullet
						g.fillRect(-1, -1, 2, 2);
						//
						break;
					case 4:
						// Explosion
						switch (state.ga[gobj]) {
						case 0:
							g.setColor(Color.RED);
							g.fillOval(-3, -2, 6, 4);
							state.gu[gobj] = false;
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
								g.setColor(new Color(255, 128 + (int) (127 * Math.random()), 0,
										(int) (255 * Math.random())));
								g.fillOval((int) (-12 * Math.random()), (int) (-6 * Math.random()), 12, 6);
							}
							if (state.ga[gobj] > 8) {
								for (t = 0; t < 3; t++) {
									g.setColor(new Color(128 + (int) (127 * Math.random()),
											128 + (int) (127 * Math.random()), 255));
									g.drawLine(0, 0, -state.ga[gobj] / 2 + (int) (state.ga[gobj] * Math.random()),
											-state.ga[gobj] / 2 + (int) (state.ga[gobj] * Math.random()));
								}
							}
							break;
						}
						state.ga[gobj]--;
						break;
					case 5:
						// Blocks
						// g.setColor(Color.CYAN);
						for (y = 0; y < 8; y++) {
							for (x = 0; x < 8; x++) {
								if (!state.gblk[gobj][y][x]) {
									g.fillRect(-43 + x * 11, -43 + y * 11, 9, 9);
								}
							}
						}
						break;
					case 6:
						// Turrets
						a = Math.toRadians(state.ga[gobj]);
						g.setColor(Color.RED);
						g.drawOval(-5, -5, 10, 10);
						g.rotate(a);
						g.setColor(Color.YELLOW);
						g.drawLine(5, 0, 8, 0);
						g.rotate(-a);
						//
						break;
					case 7:
						// Missile
						a = Math.atan2(state.gdy[gobj], state.gdx[gobj]);
						g.rotate(a);
						g.drawLine(1, -1, 4, 0);
						g.drawLine(1, 1, 4, 0);
						g.drawLine(1, 1, 1, -1);
						g.setColor(state.toggle ? Color.RED : Color.ORANGE);
						g.drawLine(0, -1, -2, 0);
						g.drawLine(0, 1, -2, 0);
						// g.drawLine(0, 1, 0,-1);
						g.rotate(-a);
						//
						break;
					case 8:
						// vertical laser
						g.drawLine(0, 0, 0, 10);
						g.drawLine(0, 90, 0, 100);
						if (state.gp[gobj] % 100 > 50) {
							g.setColor(state.toggle ? Color.RED : Color.YELLOW);
							g.drawLine(0, 10, 0, 90);
						}
						break;
					case 9:
						// horizontal laser
						g.drawLine(0, 0, 10, 0);
						g.drawLine(90, 0, 100, 0);
						if (state.gp[gobj] % 100 > 50) {
							g.setColor(state.toggle ? Color.RED : Color.YELLOW);
							g.drawLine(10, 0, 90, 0);
						}
						break;
					case 10:
						// person 2 rescue
						g.setColor(Color.LIGHT_GRAY);
						g.drawOval(-2, -2, 4, 4);
						g.drawLine(0, 2, 0, 6);
						g.drawLine(-2, 4, 2, 4);
						g.drawLine(0, 6, -2, 8);
						g.drawLine(0, 6, 2, 8);
						//
						break;
					}
					g.scale(10, 10);
					g.translate(-state.gx[gobj], -state.gy[gobj]);
					//
				}
				//
				g.translate(state.gx[0], state.gy[0]);
				g.setColor(Color.WHITE);
				g.setFont(g.getFont().deriveFont(4f));
				if ((state.lives < 0) || (state.scientists == 4)) {
					g.drawString(state.lives < 0 ? "Game Over" : "Well Done", -10, 0);
					state.restart = k[KeyEvent.VK_SPACE];
					state.gx[0] += 0.1;
					if (state.gx[0] > 210) {
						state.gx[0] = -10;
					}
				}
				//
				g.translate(10, 10);
				g.scale(0.01, 0.01);
				g.setColor(new Color(0, 128, 0, 128));
				g.fillRect(-5, -5, 210, 210);
				for (t = 0; t < 255; t++) {
					if (state.gu[t]) {
						n = state.gv[t];
						if ((n == 10) || (n == 1)) {
							g.setColor(n == 1 ? Color.WHITE : Color.CYAN);
							g.fillRect((int) state.gx[t], (int) state.gy[t], 10, 10);
						}
					}
				}
				for (t = 0; t < 4; t++) {
					if (state.lives > t) {
						g.setColor(Color.ORANGE);
						g.fillRect(-4 + t * 20, 205, 15, 15);
					}
					if (state.scientists > t) {
						g.setColor(Color.WHITE);
						g.fillRect(186 - t * 20, 205, 15, 15);
					}
				}
				//
				gameFrame.getBufferStrategy().show();
				Thread.sleep(20);
				//
			}
		} while (true);
	}

	public void keyPressed(KeyEvent e) {
		k[e.getKeyCode()] = true;
	}

	public void keyReleased(KeyEvent e) {
		k[e.getKeyCode()] = false;
	}

	private boolean[] k = new boolean[65538];

	public static void main(String[] args) throws Exception {
		new M2().run();
	}
}
