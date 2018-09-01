package modularity;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

import modularity.sub.SubM;

public class M extends KeyAdapter  {

	public int x, y, n, d,t;

	private boolean[] k=new boolean[65538];

	SubM sub = new SubM(this);
	
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
			int invincible,scientists,lives;
			int maze[][];
			int todo[];
			int[] gt,gv,ga,gp;
			boolean restart,toggle;
			boolean[] gu;
			boolean[][][] gblk;
			double[] gbnd,gx,gy,gdx,gdy;
			int mazex[] = { 0, 0, -1, 1 };
			int mazey[] = { -1, 1, 0, 0 };
			int binh;
			gu=new boolean[256]; // In use
			gt=new int[256]; // Type
			gv=new int[256]; // Visual Type
			ga=new int[256]; // animation frame
			gp=new int[256]; // general purpose counter. 
			gblk=new boolean[256][8][8];
			gbnd=new double[256]; // Bounding Sphere
			gx=new double[256]; // X coordinates
			gy=new double[256]; // Y coordinates
			gdx=new double[256]; // dX
			gdy=new double[256]; // dY
			todo= new int[20*20];
			maze = new int[20][20];
			invincible=100;	
			lives=3;
			scientists=0;
			binh=0;
			restart=false;
			toggle =false;
			//
			//
			//
			// Build the Maze
			//
			//
			sub.generateMaze(maze, todo, gt, gv, gu, gbnd, gx, gy, mazex, mazey);
			//
			// End of maze generation
			//
			sub.placeItems(maze, gt, gv, gp, gu, gbnd, gx, gy);
			//
			//
			while (!restart) {
				//
				Graphics2D g = (Graphics2D) gameFrame.getBufferStrategy().getDrawGraphics();
				//
				//
				toggle=!toggle;
				//
				g.setColor(Color.BLACK);
				g.fillRect(0,0,512,512);
				g.setColor(Color.LIGHT_GRAY);
				g.translate(256,256);
				g.scale(20, 20);
				g.translate(-gx[0],-gy[0]);
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
				if(invincible>0) invincible--;
				if ((!gu[0])&&(invincible==100)) {
					if (lives-->0) {
						gu[0]=true;
					} 
				}
				for (int gobj=0;gobj<255;gobj++) {
					if (!gu[gobj]) continue;
					if ((Math.abs(gx[0]-gx[gobj])>40)&&(gt[gobj]!=2)) continue;
					if ((Math.abs(gy[0]-gy[gobj])>40)&&(gt[gobj]!=2)) continue;
					//
					// Next Position, if no collision.
					//
					double nx=gx[gobj]+gdx[gobj];
					double ny=gy[gobj]+gdy[gobj];
					//
					double dx=gx[0]-gx[gobj];
					double dy=gy[0]-gy[gobj];
					double a=Math.atan2(dy, dx);
					//
					boolean bullet=false;
					bullet = sub.updateGameObject(gt, gv, ga, gp, gu, gx, gy, gdx, gdy, binh, gobj, dx, dy, a, bullet);			
					//
					// Collision detection (with maze).
					//
					sub.handleMazeCollisions(maze, gt, ga, gu, gx, gy, gdx, gdy, gobj, nx, ny);
					//
					// Collision Detection with other.
					//
					for (int gcol=0;gcol<255;gcol++) {
						if (!gu[gcol]) continue;
						if (gcol==gobj) continue;
						boolean hit=false;
						hit = sub.collisionDetection(gt, gp, gx, gy, gobj, gcol, hit);
						if (!hit) {
							if (gbnd[gcol]==0) continue;
							if (gbnd[gobj]==0) continue;
							//
							dx=gx[gcol]-gx[gobj];
							dy=gy[gcol]-gy[gobj];
							if (dx*dx+dy*dy<(gbnd[gcol]+gbnd[gobj])*(gbnd[gcol]+gbnd[gobj])) {
								//
								// HIT!
								//
								if (gt[gcol]==5) {
									int rx=(int)Math.floor(0.88*(gx[gobj]-gx[gcol])+4);
									int ry=(int)Math.floor(0.88*(gy[gobj]-gy[gcol])+4);						
									if ((rx<0)||(ry<0)) continue;
									if ((rx>7)||(ry>7)) continue;
									if (!gblk[gcol][ry][rx]) {
										gblk[gcol][ry][rx]=true;
										switch (gt[gobj]) {
										case 1:
											hit=true;
											break;
										case 2:
											// plr bullet
											gu[gobj+5]=true;
											gx[gobj+5]=gx[gobj];//+rx*1.125-4;
											gy[gobj+5]=gy[gobj];//+ry*1.125-4;
											ga[gobj+5]=4;
										case 11:
											// enemy bullet
											gu[gobj]=false;
											break;
										}
									}
								} else {
									hit=true;
								}
							}
						}
						if (hit) {
							if (gt[gobj]==1) {
								if (gt[gcol]==2) continue;
								if (gt[gcol]==10) {
									scientists = sub.saveScientist(scientists, gu, gcol);
									lives++;
									continue;
								}
								if (invincible>0) {
									if (gt[gcol]!=5) gu[gcol]=false;
									continue;
								}
								invincible = sub.respawn(ga, gu, gx, gy, gobj);
							} else if ((gt[gobj]==2)&&((gt[gcol]==3)||(gt[gcol]==6)||(gt[gcol]==11))) {
								sub.dunno(ga, gu, gx, gy, gobj, gcol);
							} 
						}
					}
					//
					if ((gt[gobj]==1)&&(bullet)) {
						for (t=0;t<255;t++) {
							if ((gt[t]==2)&&(!gu[t])) {
								sub.handleBullet(gu, gx, gy, gdx, gdy);
								break;
							}
						}
					}
					//
					a = sub.drawGame(invincible, gv, ga, gp, toggle, gu, gblk, gx, gy, gdx, gdy, g, gobj, a);
				}
				//
				restart = sub.drawGameOver(scientists, lives, gx, gy, g);
				//
				sub.drawRadar(gv, gu, gx, gy, g);
				sub.drawLives(scientists, lives, g);
				//
				gameFrame.getBufferStrategy().show();
				Thread.sleep(20);
				//
			}
		} while (true);
	}

	
	
	public boolean[] getK() {
		return k;
	}
	
	public void keyPressed(KeyEvent e) {
		k[e.getKeyCode()]=true;
	}
	public void keyReleased(KeyEvent e) {
		k[e.getKeyCode()]=false;
	}

	public static void main(String[] args) throws Exception {
		new M();
	}
}
