package copy;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

public class M2 extends KeyAdapter {

	int x2, y2, n2, d2, t2, invincible2, scientists2, lives2;
	int meez[][];
	int toedoe[];
	int[] gt2, gv2, ga2, gp2;
	boolean restart2, toggle2;
	boolean[] gu2;
	boolean[][][] gblk2;
	double[] gbnd2, gx2, gy2, gdx2, gdy2;
	int meezx[] = { 0, 0, -1, 1 };
	int meezy[] = { -1, 1, 0, 0 };
	int benh;
	double nx2;
	double ny2;
	//
	double dx2;
	double dy2;
	double a2;
	//
	boolean bullet2;
	int citem2;
	boolean hit2;
	int rx2,ry2;

	private M2() throws Exception {
		JFrame gameFrame = initFrame();
		do {
			prepare();
			while (!restart2) {
				Graphics2D g = (Graphics2D) gameFrame.getBufferStrategy().getDrawGraphics();
				toggle2 = !toggle2;
				doTheGame(g);
				gameFrame.getBufferStrategy().show();
				Thread.sleep(20);
			}
		} while (true);
	}

	private void doTheGame(Graphics2D g) {
		whitpeTheScreen(g);
		drawMaze(g);
		rebirth();
		gameObjloop(g);
		gameOver(g);
		moreStuff(g);
	}

	private void gameObjloop(Graphics2D g) {
		for (int gobj = 0; gobj < 255; gobj++) {
			if (!gu2[gobj])
				continue;
			if ((Math.abs(gx2[0] - gx2[gobj]) > 40) && (gt2[gobj] != 2))
				continue;
			if ((Math.abs(gy2[0] - gy2[gobj]) > 40) && (gt2[gobj] != 2))
				continue;
			doGobjes(g, gobj);
		}
	}

	private void prepare() {
		initializeVars();
		initializeVars2();
		generateMaze();
		placeCItem();
		morePlaceCItem();
	}

	private void doGobjes(Graphics2D g, int gameobj) {
		nextPosition(gameobj);
		movement(gameobj);
		mazeCollisions(gameobj);
		collisionsWithOther(gameobj);
		stuff(gameobj);
		drawThings(g, gameobj);
	}

	private void nextPosition(int gobj) {
		nx2 = gx2[gobj] + gdx2[gobj];
		ny2 = gy2[gobj] + gdy2[gobj];
		//
		dx2 = gx2[0] - gx2[gobj];
		dy2 = gy2[0] - gy2[gobj];
		a2 = Math.atan2(dy2, dx2);
		//
		bullet2 = false;
	}

	private void rebirth() {
		if (invincible2 > 0)
			invincible2--;
		if ((!gu2[0]) && (invincible2 == 100)) {
			if (lives2-- > 0) {
				gu2[0] = true;
			}
		}
	}

	private void drawMaze(Graphics2D g) {
		for (x2 = 1; x2 < 19; ++x2) {
			for (y2 = 1; y2 < 19; ++y2) {
				if ((meez[x2][y2] & 1) != 0) /* This cell has a top wall */
					g.drawLine(x2 * 10, y2 * 10, x2 * 10 + 10, y2 * 10);
				if ((meez[x2][y2] & 2) != 0) /* This cell has a bottom wall */
					g.drawLine(x2 * 10, y2 * 10 + 10, x2 * 10 + 10, y2 * 10 + 10);
				if ((meez[x2][y2] & 4) != 0) /* This cell has a left wall */
					g.drawLine(x2 * 10, y2 * 10, x2 * 10, y2 * 10 + 10);
				if ((meez[x2][y2] & 8) != 0) /* This cell has a right wall */
					g.drawLine(x2 * 10 + 10, y2 * 10, x2 * 10 + 10, y2 * 10 + 10);
			}
		}
	}

	private void whitpeTheScreen(Graphics2D g) {
		//
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 512, 512);
		g.setColor(Color.LIGHT_GRAY);
		g.translate(256, 256);
		g.scale(20, 20);
		g.translate(-gx2[0], -gy2[0]);
	}

	private void morePlaceCItem() {
		for (x2 = 2; x2 < 19; ++x2) {
			for (y2 = 2; y2 < 19; ++y2) {
				if (citem2 >= 255) continue; 
				if (Math.random() < 0.25 + y2 / 40.0) {
					if ((meez[x2][y2] & 3) == 2) { placeItem5();
					} else if ((meez[x2][y2] & 3) == 3) { morePlaceItem8();
					} else if ((meez[x2][y2] & 12) == 12) { morePlaceItem9();
					} else if (y2 > 5) { placeItem6(); }
				}
			}
		}
	}

	private void morePlaceItem9() {
		for (t2 = 0; t2 < (y2 < 10 ? 1 : 2); t2++) {
			placeItem9();
		}
	}

	private void morePlaceItem8() {
		for (t2 = 0; t2 < (y2 < 10 ? 1 : 2); t2++) {
			placeItem8();
		}
	}

	private void placeItem6() {
		gt2[citem2] = gv2[citem2] = 6; // Turret
		gu2[citem2] = true;
		gx2[citem2] = x2 * 10 + 5;
		gy2[citem2] = y2 * 10 + 5;
		gbnd2[citem2] = 1;
		citem2++;
	}

	private void placeItem9() {
		gt2[citem2] = gv2[citem2] = 9; // horizontal laser
		gu2[citem2] = true;
		gx2[citem2] = x2 * 10;
		gy2[citem2] = y2 * 10 + 2 + t2 * 5;
		gp2[citem2] = (int) (50 * Math.random());
		citem2++;
	}

	private void placeItem8() {
		gt2[citem2] = gv2[citem2] = 8; // vertical laser
		gu2[citem2] = true;
		gx2[citem2] = x2 * 10 + 2 + t2 * 5;
		gp2[citem2] = (int) (50 * Math.random());
		gy2[citem2] = y2 * 10;
		citem2++;
	}

	private void placeItem5() {
		gt2[citem2] = gv2[citem2] = 5; // block 8*8
		gu2[citem2] = true;
		gx2[citem2] = x2 * 10 + 5;
		gy2[citem2] = y2 * 10 + 5;
		gbnd2[citem2] = 8;
		citem2++;
	}

	private void placeCItem() {
		citem2 = 30;
		for (t2 = 0; t2 < 4; t2++) {
			while (true) {
				x2 = (int) (Math.random() * 19);
				y2 = 10 + (int) (Math.random() * 9);
				if ((meez[x2][y2] & 3) == 3) {
					gt2[citem2] = 10; // person
					gv2[citem2] = 10;
					gu2[citem2] = true;
					gx2[citem2] = x2 * 10 + 5;
					gy2[citem2] = y2 * 10 + 8.5;
					gbnd2[citem2] = 1;
					citem2++;
					break;
				}
			}
		}
	}

	private void generateMaze() {
		t2 = 0;
		outerwalls();
		x2 = 1 + (int) (Math.random() * 18);
		y2 = 1 + (int) (Math.random() * 18);
		placeSomething();
		while (t2 > 0) {
			somethingGenerateMaze();
			toedoe[n2] = toedoe[--t2];
			findEmptySpot();
			placeSomthing();
		}
	}

	private void somethingGenerateMaze() {
		n2 = (int) (Math.random() * t2);
		x2 = toedoe[n2] >> 16; /* the top 2 bytes of the data */
		y2 = toedoe[n2] & 65535; /* the bottom 2 bytes of the data */
	}

	private void placeSomthing() {
		meez[x2][y2] &= ~((1 << d2) | 32);
		meez[x2 + meezx[d2]][y2 + meezy[d2]] &= ~(1 << (d2 ^ 1));
		for (d2 = 0; d2 < 4; ++d2) {
			if ((meez[x2 + meezx[d2]][y2 + meezy[d2]] & 16) != 0) {
				toedoe[t2++] = ((x2 + meezx[d2]) << 16) | (y2 + meezy[d2]);
				meez[x2 + meezx[d2]][y2 + meezy[d2]] &= ~16;
			}
		}
	}

	private void findEmptySpot() {
		do {
			d2 = (int) (Math.random() * 4);
		} while ((meez[x2 + meezx[d2]][y2 + meezy[d2]] & 32) != 0);
	}

	private void placeSomething() {
		meez[x2][y2] &= ~48;
		for (d2 = 0; d2 < 4; ++d2) {
			if ((meez[x2 + meezx[d2]][y2 + meezy[d2]] & 16) != 0) {
				toedoe[t2++] = ((x2 + meezx[d2]) << 16) | (y2 + meezy[d2]);
				meez[x2 + meezx[d2]][y2 + meezy[d2]] &= ~16;
			}
		}
	}

	private void outerwalls() {
		for (x2 = 0; x2 < 20; ++x2) {
			for (y2 = 0; y2 < 20; ++y2) {
				meez[x2][y2] = (x2 == 0 || x2 == 19 || y2 == 0 || y2 == 19) ? 32 : 63;
			}
		}
	}

	private void initializeVars2() {
		gt2[0] = 1; // player
		gv2[0] = 1; // player
		gu2[0] = true;
		gx2[0] = 15;
		gy2[0] = 15;
		gbnd2[0] = 1;
		initializeVars4();
		initializeVars3();
	}

	private void initializeVars4() {
		for (t2 = 1; t2 < 5; t2++) {
			gt2[t2] = 2; // player bullet
			gv2[t2] = 2;
			gbnd2[t2] = 0.2;
			gt2[t2 + 5] = 4; // Explosion
			gv2[t2 + 5] = 4;
		}
	}

	private void initializeVars3() {
		for (t2 = 10; t2 < 20; t2++) {
			gt2[t2] = 11; // enemy bullet
			gv2[t2] = 2;
			gbnd2[t2] = 0.1;
			gt2[t2 + 10] = 12; // enemy bullet Explosion
			gv2[t2 + 10] = 4;
		}
	}

	private void initializeVars() {
		initializeVars5();
		initializeVars6();
		invincible2 = 100;
		lives2 = 3;
		scientists2 = 0;
		benh = 0;
		restart2 = false;
		toggle2 = false;
	}

	private void initializeVars6() {
		gblk2 = new boolean[256][8][8];
		gbnd2 = new double[256]; // Bounding Sphere
		gx2 = new double[256]; // X coordinates
		gy2 = new double[256]; // Y coordinates
		gdx2 = new double[256]; // dX
		gdy2 = new double[256]; // dY
		toedoe = new int[20 * 20];
		meez = new int[20][20];
	}

	private void initializeVars5() {
		gu2 = new boolean[256]; // In use
		gt2 = new int[256]; // Type
		gv2 = new int[256]; // Visual Type
		ga2 = new int[256]; // animation frame
		gp2 = new int[256]; // general purpose counter.
	}

	private JFrame initFrame() {
		JFrame geemFrame = new JFrame();
		geemFrame.setResizable(false);
		geemFrame.setSize(512, 512);
		geemFrame.setVisible(true);
		geemFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		geemFrame.createBufferStrategy(2);
		geemFrame.addKeyListener(this);
		return geemFrame;
	}

	private void moreStuff(Graphics2D g) {
		g.translate(10, 10);
		g.scale(0.01, 0.01);
		g.setColor(new Color(0, 128, 0, 128));
		g.fillRect(-5, -5, 210, 210);
		for (t2 = 0; t2 < 255; t2++) {
			drawSomething(g);
		}
		for (t2 = 0; t2 < 4; t2++) {
			drawLivesScientitst(g);
		}
	}

	private void drawSomething(Graphics2D g) {
		if (gu2[t2]) {
			n2 = gv2[t2];
			if ((n2 == 10) || (n2 == 1)) {
				g.setColor(n2 == 1 ? Color.WHITE : Color.CYAN);
				g.fillRect((int) gx2[t2], (int) gy2[t2], 10, 10);
			}
		}
	}

	private void drawLivesScientitst(Graphics2D g) {
		if (lives2 > t2) {
			g.setColor(Color.ORANGE);
			g.fillRect(-4 + t2 * 20, 205, 15, 15);
		}
		if (scientists2 > t2) {
			g.setColor(Color.WHITE);
			g.fillRect(186 - t2 * 20, 205, 15, 15);
		}
	}

	private void gameOver(Graphics2D g) {
		g.translate(gx2[0], gy2[0]);
		g.setColor(Color.WHITE);
		g.setFont(g.getFont().deriveFont(4f));
		if ((lives2 < 0) || (scientists2 == 4)) {
			g.drawString(lives2 < 0 ? "Game Over" : "Well Done", -10, 0);
			restart2 = k[KeyEvent.VK_SPACE];
			gx2[0] += 0.1;
			if (gx2[0] > 210) {
				gx2[0] = -10;
			}
		}
	}

	private void drawThings(Graphics2D g, int gobj) {
		translate(g, gobj);
		switch (gv2[gobj]) {
		case 1: drawHeli(g, gobj); break;
		case 2: drawBullet(g); break;
		case 4: drawExplosion(g, gobj); break;
		case 5: drawBlocks(g, gobj); break;
		case 6: drawTurret(g, gobj); break;
		case 7: drawMissile(g, gobj); break;
		case 8: drawVerticalLaser(g, gobj); break;
		case 9: drawHorizontalLazer(g, gobj); break;
		case 10: drawScientist(g); break;
		}
		translateBack(g, gobj);
		//
	}

	private void translateBack(Graphics2D g, int gobj) {
		g.scale(10, 10);
		g.translate(-gx2[gobj], -gy2[gobj]);
	}

	private void translate(Graphics2D g, int gobj) {
		g.translate(gx2[gobj], gy2[gobj]);
		g.scale(0.1, 0.1);
		g.setColor(Color.WHITE);
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
		if (gp2[gobj] % 100 > 50) {
			g.setColor(toggle2 ? Color.RED : Color.YELLOW);
			g.drawLine(10, 0, 90, 0);
		}
	}

	private void drawVerticalLaser(Graphics2D g, int gobj) {
		// vertical laser
		g.drawLine(0, 0, 0, 10);
		g.drawLine(0, 90, 0, 100);
		if (gp2[gobj] % 100 > 50) {
			g.setColor(toggle2 ? Color.RED : Color.YELLOW);
			g.drawLine(0, 10, 0, 90);
		}
	}

	private void drawMissile(Graphics2D g, int gobj) {
		// Missile
		a2 = Math.atan2(gdy2[gobj], gdx2[gobj]);
		g.rotate(a2);
		g.drawLine(1, -1, 4, 0);
		g.drawLine(1, 1, 4, 0);
		g.drawLine(1, 1, 1, -1);
		g.setColor(toggle2 ? Color.RED : Color.ORANGE);
		g.drawLine(0, -1, -2, 0);
		g.drawLine(0, 1, -2, 0);
		// g.drawLine(0, 1, 0,-1);
		g.rotate(-a2);
		//
	}

	private void drawTurret(Graphics2D g, int gobj) {
		// Turrets
		a2 = Math.toRadians(ga2[gobj]);
		g.setColor(Color.RED);
		g.drawOval(-5, -5, 10, 10);
		g.rotate(a2);
		g.setColor(Color.YELLOW);
		g.drawLine(5, 0, 8, 0);
		g.rotate(-a2);
		//
	}

	private void drawBlocks(Graphics2D g, int gobj) {
		// Blocks
		// g.setColor(Color.CYAN);
		for (y2 = 0; y2 < 8; y2++) {
			for (x2 = 0; x2 < 8; x2++) {
				if (!gblk2[gobj][y2][x2]) {
					g.fillRect(-43 + x2 * 11, -43 + y2 * 11, 9, 9);
				}
			}
		}
	}

	private void drawExplosion(Graphics2D g, int gobj) {
		// Explosion
		switch (ga2[gobj]) {
		case 0:
			drawRed(g, gobj);
			break;
		case 1:
			drawOrange(g);
			break;
		case 2:
			drawYellow(g);
			break;
		default:
			drawDefault(g, gobj);
			break;
		}
		ga2[gobj]--;
	}

	private void drawDefault(Graphics2D g, int gobj) {
		for (t2 = 0; t2 < 8; t2++) {
			g.setColor(new Color(255, 128 + (int) (127 * Math.random()), 0, (int) (255 * Math.random())));
			g.fillOval((int) (-12 * Math.random()), (int) (-6 * Math.random()), 12, 6);
		}
		if (ga2[gobj] > 8) {
			for (t2 = 0; t2 < 3; t2++) {
				g.setColor(new Color(128 + (int) (127 * Math.random()), 128 + (int) (127 * Math.random()), 255));
				g.drawLine(0, 0, -ga2[gobj] / 2 + (int) (ga2[gobj] * Math.random()),
						-ga2[gobj] / 2 + (int) (ga2[gobj] * Math.random()));
			}
		}
	}

	private void drawYellow(Graphics2D g) {
		g.setColor(Color.YELLOW);
		g.fillOval(-8, -3, 16, 6);
	}

	private void drawOrange(Graphics2D g) {
		g.setColor(Color.ORANGE);
		g.fillOval(-6, -3, 12, 6);
	}

	private void drawRed(Graphics2D g, int gobj) {
		g.setColor(Color.RED);
		g.fillOval(-3, -2, 6, 4);
		gu2[gobj] = false;
	}

	private void drawBullet(Graphics2D g) {
		// Bullet
		g.fillRect(-1, -1, 2, 2);
		//
	}

	private void drawHeli(Graphics2D g, int gobj) {
		g.rotate(gdx2[gobj] * 0.4);
		toggleHeli(g);
		someMoreHeli(g);
		if (gdx2[0] >= 0.01) {
			heliOne(g);
		} else if (gdx2[0] < -0.01) {
			heliToo(g);
		} else {
			heliThree(g);
		}
		g.rotate(-gdx2[gobj] * 0.4);
	}

	private void toggleHeli(Graphics2D g) {
		if (invincible2 > 0) {
			g.setColor(toggle2 ? Color.CYAN : Color.BLUE);
		}
	}

	private void someMoreHeli(Graphics2D g) {
		g.drawOval(-5, -5, 10, 10);
		g.drawLine(0, -5, 0, -7);
		g.drawLine(toggle2 ? -10 : 10, -7, toggle2 ? 3 : -3, -7);
	}

	private void heliThree(Graphics2D g) {
		gdx2[0] = 0;
		g.drawLine(0, 5, -5, 7);
		g.drawLine(0, 5, 5, 7);
	}

	private void heliToo(Graphics2D g) {
		gdx2[0] += 0.01;
		g.drawLine(5, 0, 12, 0);
		g.drawLine(-5, 7, 5, 7);
		g.drawLine(-3, 7, 0, 5);
		g.drawLine(11, toggle2 ? -1 : 1, 13, toggle2 ? 1 : -1);
	}

	private void heliOne(Graphics2D g) {
		gdx2[0] += -0.01;
		g.drawLine(-5, 0, -12, 0);
		g.drawLine(-5, 7, 5, 7);
		g.drawLine(3, 7, 0, 5);
		g.drawLine(-11, toggle2 ? -1 : 1, -13, toggle2 ? 1 : -1);
	}

	private void stuff(int gobj) {
		if ((gt2[gobj] == 1) && (bullet2)) {
			for (t2 = 0; t2 < 255; t2++) {
				if ((gt2[t2] == 2) && (!gu2[t2])) {
					gu2[t2] = true; gx2[t2] = gx2[0]; gy2[t2] = gy2[0]; benh = 10;
					if (gdx2[0] == 0) {
						gdx2[t2] = 0;
						gdy2[t2] = 0.3;
					} else {
						gdx2[t2] = gdx2[0] > 0 ? 0.4 : -0.4;
						gdy2[t2] = gdy2[0] + Math.abs(gdx2[0] / 8);
					}
					break;
				}
			}
		}
	}

	private void collisionsWithOther(int gobj) {
		for (int gcol = 0; gcol < 255; gcol++) {
			if (!gu2[gcol]) continue; 
			if (gcol == gobj) continue;
			hit2 = false; hitSomething(gobj, gcol);
			if (!hit2) {
				if (anotherConditionalSomething(gcol,gobj)) continue;
				calcDxDy(gobj, gcol);
				if (someConditional(gobj, gcol)) {
					if (gt2[gcol] == 5) { getRxRy(gobj, gcol); if (outofBounds()) continue; somethingHit(gobj, gcol); } else { hit2 = true; }
				}
			} 
			if (hit2) {
				doContinue(gobj, gcol);
			}
		}
	}
	
	private boolean anotherConditionalSomething(int gcol, int gobj) {
		return (gbnd2[gcol] == 0) || (gbnd2[gobj] == 0);
	}

	private boolean outofBounds() {
		return ((rx2 < 0) || (ry2 < 0)) || ((rx2 > 7) || (ry2 > 7));
	}

	private boolean someConditional(int gobj, int gcol) {
		return dx2 * dx2 + dy2 * dy2 < (gbnd2[gcol] + gbnd2[gobj]) * (gbnd2[gcol] + gbnd2[gobj]);
	}
	
	public boolean doContinue(int gobj, int gcol) {
		if (gt2[gobj] == 1) { if (gt2[gcol] == 2) return true;
		if (gt2[gcol] == 10) { gu2[gcol] = false; pickupScientist(); return true; }
		if (invincible2 > 0) { if (gt2[gcol] != 5) gu2[gcol] = false; return true; }
		andAnotherThingHit(gobj);
		} else if (anotherConditionExtraction(gobj, gcol)) {
			anotherThingHit(gobj, gcol);
		}
		return false;
	}

	private boolean anotherConditionExtraction(int gobj, int gcol) {
		return (gt2[gobj] == 2) && ((gt2[gcol] == 3) || (gt2[gcol] == 6) || (gt2[gcol] == 11));
	}

	private void pickupScientist() {
		scientists2++;
		lives2++;
	}

	private void calcDxDy(int gobj, int gcol) {
		dx2 = gx2[gcol] - gx2[gobj];
		dy2 = gy2[gcol] - gy2[gobj];
	}

	private void getRxRy(int gobj, int gcol) {
		rx2 = (int) Math.floor(0.88 * (gx2[gobj] - gx2[gcol]) + 4);
		ry2 = (int) Math.floor(0.88 * (gy2[gobj] - gy2[gcol]) + 4);
	}

	private void andAnotherThingHit(int gobj) {
		gu2[gobj] = false;
		gu2[6] = true;
		gx2[6] = gx2[gobj];
		gy2[6] = gy2[gobj];
		ga2[6] = 16;
		invincible2 = 200;
	}

	private void anotherThingHit(int gobj, int gcol) {
		gu2[gcol] = false;
		gu2[gobj] = false;
		gu2[gobj + 5] = true;
		gx2[gobj + 5] = gx2[gcol];
		gy2[gobj + 5] = gy2[gcol];
		ga2[gobj + 5] = 12;
	}

	private void hitSomething(int gobj, int gcol) {
		if ((gt2[gcol] == 8) || (gt2[gcol] == 9)) {
			if (gp2[gcol] % 100 > 50) {
				if (gt2[gcol] == 8) {
					if ((gy2[gobj] - gy2[gcol] < 9) && (gy2[gobj] - gy2[gcol] > 0)) {
						hit2 = (Math.abs(gx2[gcol] - gx2[gobj]) < 1);
					}
				} else {
					if ((gx2[gobj] - gx2[gcol] < 9) && (gx2[gobj] - gx2[gcol] > 1)) {
						hit2 = (Math.abs(gy2[gcol] - gy2[gobj]) < 1);
					}
				}
			}
		}
	}

	private void somethingHit(int gobj, int gcol) {
		if (!gblk2[gcol][ry2][rx2]) {
			gblk2[gcol][ry2][rx2] = true;
			switch (gt2[gobj]) {
			case 1:
				hit2 = true;
				break;
			case 2:
				// plr bullet
				gu2[gobj + 5] = true;
				gx2[gobj + 5] = gx2[gobj];// +rx*1.125-4;
				gy2[gobj + 5] = gy2[gobj];// +ry*1.125-4;
				ga2[gobj + 5] = 4;
			case 11:
				// enemy bullet
				gu2[gobj] = false;
				break;
			}
		}
	}

	private void mazeCollisions(int gobj) {
		whichCell();
		//
		if (isCollistion()) {
			gdx2[gobj] = 0;
			gdy2[gobj] = 0;
			if (gt2[gobj] == 2) {
				somethingMazeColl(gobj);
			}
			if (gt2[gobj] == 11) {
				anotherThingMazeColl(gobj);
			}
		} else {
			gx2[gobj] = nx2;
			gy2[gobj] = ny2;
		}
	}

	private boolean isCollistion() {
		return ((t2 < 1) && ((meez[x2][y2] & 1) != 0)) || ((t2 >= 9) && ((meez[x2][y2] & 2) != 0))
				|| ((d2 < 1) && ((meez[x2][y2] & 4) != 0)) || ((d2 >= 9) && ((meez[x2][y2] & 8) != 0));
	}

	private void whichCell() {
		d2 = ((int) nx2) % 10;
		t2 = ((int) ny2) % 10;
		x2 = ((int) nx2) / 10;
		y2 = ((int) ny2) / 10;
	}

	private void anotherThingMazeColl(int gobj) {
		gu2[gobj] = false;
		gu2[gobj + 10] = true;
		gx2[gobj + 10] = gx2[gobj];
		gy2[gobj + 10] = gy2[gobj];
		ga2[gobj + 10] = 1;
	}

	private void somethingMazeColl(int gobj) {
		gu2[gobj] = false;
		gu2[gobj + 5] = true;
		gx2[gobj + 5] = gx2[gobj];
		gy2[gobj + 5] = gy2[gobj];
		ga2[gobj + 5] = 1;
	}

	private void movement(int gobj) {
		switch (gt2[gobj]) {
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
			gp2[gobj]++;
			break;
		}
	}

	private void movementEleven(int gobj) {
		if (gv2[gobj] == 7) {
			gdx2[gobj] += (gdx2[gobj] < 0.15 * Math.cos(a2) ? 0.005 : -0.005);
			gdy2[gobj] += (gdy2[gobj] < 0.15 * Math.sin(a2) ? 0.005 : -0.005);
		}
	}

	private void movementSix(int gobj) {
		//
		ga2[gobj] = (int) Math.toDegrees(a2);
		//
		if (gp2[gobj] <= 0) {
			if (Math.abs(dx2 * dx2 + dy2 * dy2) < 200) {
				gp2[gobj] = 64;
				for (t2 = 0; t2 < 255; t2++) {
					if ((gt2[t2] == 11) && (!gu2[t2])) {
						doMovementSix(gobj);
						break;
					}
				}
			}
		} else {
			gp2[gobj]--;
		}
	}

	private void doMovementSix(int gobj) {
		gu2[t2] = true;
		gx2[t2] = gx2[gobj];
		gy2[t2] = gy2[gobj];
		gdx2[t2] = 0.2 * Math.cos(a2);
		gdy2[t2] = 0.2 * Math.sin(a2);
		gv2[t2] = gy2[gobj] > 150 ? 7 : 2;
	}

	private void movementOne() {
		doBullet();
		decreaseBinh();
		//
		down();
		up();
		handleD();
		handleA();
		//
		tilt();
		//
	}

	private void doBullet() {
		bullet2 = k[KeyEvent.VK_SPACE] && (benh == 0);
	}

	private void decreaseBinh() {
		if (benh > 0)
			benh--;
	}

	private void tilt() {
		if (gdy2[0] > 0.02) {
			gdy2[0] += -0.01;
		} else if (gdy2[0] < 0) {
			gdy2[0] += 0.01;
		} else {
			gdy2[0] = 0.01;
		}
	}

	private void handleD() {
		if ((k[KeyEvent.VK_D]) && (gdx2[0] < 0.3))
			gdx2[0] += 0.05;
	}

	private void handleA() {
		if ((k[KeyEvent.VK_A]) && (gdx2[0] > -0.3))
			gdx2[0] += -0.05;
	}

	private void down() {
		if ((k[KeyEvent.VK_S]) && (gdy2[0] < 0.2))
			gdy2[0] += 0.05;
	}

	private void up() {
		if ((k[KeyEvent.VK_W]) && (gdy2[0] > -0.2))
			gdy2[0] += -0.05;
	}

	public void keyPressed(KeyEvent e) {
		k[e.getKeyCode()] = true;
	}

	public void keyReleased(KeyEvent e) {
		k[e.getKeyCode()] = false;
	}

	private boolean[] k = new boolean[65538];

	public static void main(String[] args) throws Exception {
		new M2();
	}
}
