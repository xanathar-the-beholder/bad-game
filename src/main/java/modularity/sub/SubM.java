package modularity.sub;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import modularity.M;

public class SubM {

	int x, y, n, d,t;
	M m;
	
	public SubM(M m) {
		this.m = m;
	}
	
	public double drawGame(int invincible, int[] gv, int[] ga, int[] gp, boolean toggle, boolean[] gu,
			boolean[][][] gblk, double[] gx, double[] gy, double[] gdx, double[] gdy, Graphics2D g, int gobj,
			double a) {
		g.translate(gx[gobj],gy[gobj]);
		g.scale(0.1,0.1);
		g.setColor(Color.WHITE);
		switch (gv[gobj]) {
		case 1:
			g.rotate(gdx[gobj]*0.4);
			//
			// Draw Heli
			//
			if (invincible>0) {
				g.setColor(toggle?Color.CYAN:Color.BLUE);
			}
			//
			g.drawOval(-5,-5,10,10);
			g.drawLine(0,-5,0,-7);
			g.drawLine(toggle?-10:10,-7,toggle?3:-3,-7);
			if (gdx[0]>=0.01) {
				gdx[0]+=-0.01;
				g.drawLine(-5, 0, -12, 0);
				g.drawLine(-5, 7, 5, 7);
				g.drawLine( 3, 7, 0, 5);
				g.drawLine(-11,toggle?-1:1,-13,toggle?1:-1);
			} else if (gdx[0]<-0.01) {
				gdx[0]+=0.01;
				g.drawLine(5, 0, 12, 0);
				g.drawLine(-5, 7, 5, 7);
				g.drawLine(-3, 7, 0, 5);
				g.drawLine(11,toggle?-1:1,13,toggle?1:-1);
			} else {
				gdx[0]=0;
				g.drawLine(0, 5, -5, 7);
				g.drawLine(0, 5, 5, 7);
			}
			//
			g.rotate(-gdx[gobj]*0.4);
			break;
		case 2:
			// Bullet
			g.fillRect(-1,-1,2,2);
			//
			break;
		case 4:
			// Explosion
			switch (ga[gobj]) {
			case 0:
				g.setColor(Color.RED);
				g.fillOval(-3,-2, 6, 4);
				gu[gobj]=false;
				break;
			case 1:
				g.setColor(Color.ORANGE);
				g.fillOval(-6,-3, 12, 6);
				break;					
			case 2:
				g.setColor(Color.YELLOW);
				g.fillOval(-8,-3, 16, 6);
				break;		
			default:
				for (t=0;t<8;t++) {
					g.setColor(new Color(255,128+(int)(127*Math.random()),0,(int)(255*Math.random())));
					g.fillOval((int)(-12*Math.random()),(int)(-6*Math.random()), 12, 6);
				}
			    if (ga[gobj]>8) {
			    	for (t=0;t<3;t++) {
			    		g.setColor(new Color(128+(int)(127*Math.random()),128+(int)(127*Math.random()),255));
			    		g.drawLine(0, 0,-ga[gobj]/2+(int)(ga[gobj]*Math.random()),-ga[gobj]/2+(int)(ga[gobj]*Math.random()));
			    	}
			    }
				break;		
			}
			ga[gobj]--;
			break;
		case 5:
			// Blocks
			//g.setColor(Color.CYAN);
			for (y=0;y<8;y++) {
				for (x=0;x<8;x++) {
					if (!gblk[gobj][y][x]) {
						g.fillRect(-43+x*11,-43+ y*11, 9,9);
					}
				}
			}
			break;
		case 6:
			// Turrets
			a=Math.toRadians(ga[gobj]);
			g.setColor(Color.RED);
			g.drawOval(-5,-5,10,10);
			g.rotate(a);
			g.setColor(Color.YELLOW);
			g.drawLine(5,0,8,0);
			g.rotate(-a);
			//
			break;
		case 7:
			// Missile
			a=Math.atan2(gdy[gobj],gdx[gobj]);
			g.rotate(a);
			g.drawLine(1, -1, 4, 0);
			g.drawLine(1, 1, 4, 0);
			g.drawLine(1, 1, 1,-1);
			g.setColor(toggle?Color.RED:Color.ORANGE);
			g.drawLine(0, -1, -2, 0);
			g.drawLine(0, 1, -2, 0);
			//g.drawLine(0, 1, 0,-1);
			g.rotate(-a);
			//
			break;
		case 8:
			// vertical laser
			g.drawLine(0,0,0,10);
			g.drawLine(0,90,0,100);
			if (gp[gobj]%100>50) {
				g.setColor(toggle?Color.RED:Color.YELLOW);
				g.drawLine(0,10,0,90);
			}
			break;
		case 9:
			// horizontal laser
			g.drawLine(0,0,10,0);
			g.drawLine(90,0,100,0);
			if (gp[gobj]%100>50) {
				g.setColor(toggle?Color.RED:Color.YELLOW);
				g.drawLine(10,0,90,0);
			}
			break;
		case 10:
			// person 2 rescue
			g.setColor(Color.LIGHT_GRAY);
			g.drawOval(-2, -2, 4, 4);
			g.drawLine(0,2,0,6);
			g.drawLine(-2,4,2,4);
			g.drawLine(0,6,-2,8);
			g.drawLine(0,6,2,8);
			//
			break;
		}
		g.scale(10,10);
		g.translate(-gx[gobj],-gy[gobj]);
		//
		return a;
	}
	public void handleMazeCollisions(int[][] maze, int[] gt, int[] ga, boolean[] gu, double[] gx, double[] gy, double[] gdx,
			double[] gdy, int gobj, double nx, double ny) {
		d=((int)nx)%10;
		t=((int)ny)%10;
		x=((int)nx)/10;
		y=((int)ny)/10;
		//
		if (((t<1)&&((maze[x][y] & 1) != 0))||((t>=9)&&((maze[x][y] & 2) != 0))||((d<1)&&((maze[x][y] & 4) != 0))||((d>=9)&&((maze[x][y] & 8) != 0))) {
			gdx[gobj]=0;
			gdy[gobj]=0;
			if (gt[gobj]==2) {
				gu[gobj]=false;
				gu[gobj+5]=true;
				gx[gobj+5]=gx[gobj];
				gy[gobj+5]=gy[gobj];
				ga[gobj+5]=1;					
			}
			if (gt[gobj]==11) {
				gu[gobj]=false;
				gu[gobj+10]=true;
				gx[gobj+10]=gx[gobj];
				gy[gobj+10]=gy[gobj];
				ga[gobj+10]=1;					
			}
		} else {
			gx[gobj]=nx;
			gy[gobj]=ny;
		}
	}
	public boolean updateGameObject(int[] gt, int[] gv, int[] ga, int[] gp, boolean[] gu, double[] gx, double[] gy,
			double[] gdx, double[] gdy, int binh, int gobj, double dx, double dy, double a, boolean bullet) {
		switch (gt[gobj]) {
		case 1:
			bullet=m.getK()[KeyEvent.VK_SPACE]&&(binh==0);
			if (binh>0) binh--;
			//
			if ((m.getK()[KeyEvent.VK_W]) &&(gdy[0]>-0.2)) gdy[0]+=-0.05;
			if ((m.getK()[KeyEvent.VK_S]) && (gdy[0]<0.2)) gdy[0]+=0.05;
			if ((m.getK()[KeyEvent.VK_A]) && (gdx[0]>-0.3)) gdx[0]+=-0.05;
			if ((m.getK()[KeyEvent.VK_D]) && (gdx[0]<0.3)) gdx[0]+=0.05;
			//
			if (gdy[0]>0.02) {
				gdy[0]+=-0.01;
			} else if (gdy[0]<0) {
				gdy[0]+=0.01;
			} else {
				gdy[0]=0.01;
			}
			//
			break;
		case 6:
			//
			ga[gobj]=(int)Math.toDegrees(a);
			//
			if (gp[gobj]<=0) {
				if (Math.abs(dx*dx+dy*dy)<200) {
					gp[gobj]=64;
					for (t=0;t<255;t++) {
						if ((gt[t]==11)&&(!gu[t])) {
							gu[t]=true;
							gx[t]=gx[gobj];
							gy[t]=gy[gobj];
							gdx[t]=0.2*Math.cos(a);
							gdy[t]=0.2*Math.sin(a);
							gv[t]=gy[gobj]>150?7:2;
							break;
						}
					}
				}
			} else {
				gp[gobj]--;
			}
			break;
		case 11:
			if (gv[gobj]==7) {
				gdx[gobj]+=(gdx[gobj]<0.15*Math.cos(a)?0.005:-0.005);
				gdy[gobj]+=(gdy[gobj]<0.15*Math.sin(a)?0.005:-0.005);
			}
			break;
		case 8:
		case 9:
			gp[gobj]++;
			break;
		}
		return bullet;
	}
	public void placeItems(int[][] maze, int[] gt, int[] gv, int[] gp, boolean[] gu, double[] gbnd, double[] gx,
			double[] gy) {
		int citem=30;
		for (t=0;t<4;t++) {
			while(true) {
				x=(int)(Math.random()*19);
				y=10+(int)(Math.random()*9);
				if ((maze[x][y]&3)==3) {
					gt[citem]=10; // person
					gv[citem]=10;
					gu[citem]=true;
					gx[citem]=x*10+5;
					gy[citem]=y*10+8.5;
					gbnd[citem]=1;
					citem++;
					break;
				}
			}
		}
		for (x = 2; x < 19; ++x) {
			for (y = 2; y < 19; ++y) {
				if (citem>=255) continue; // out of cells.
				if (Math.random()<0.25+y/40.0) {
					if ((maze[x][y]&3)==2) { // open from above, closed bottom.
						gt[citem]=gv[citem]=5; // block 8*8
						gu[citem]=true;
						gx[citem]=x*10+5;
						gy[citem]=y*10+5;
						gbnd[citem]=8;
						citem++;
					} else if ((maze[x][y]&3)==3) {
						for (t=0;t<(y<10?1:2);t++) {
							gt[citem]=gv[citem]=8; // vertical laser
							gu[citem]=true;
							gx[citem]=x*10+2+t*5;
							gp[citem]=(int)(50*Math.random());
							gy[citem]=y*10;
							citem++;
						}						
					} else if ((maze[x][y]&12)==12) {
						for (t=0;t<(y<10?1:2);t++) {
							gt[citem]=gv[citem]=9; // horizontal laser
							gu[citem]=true;
							gx[citem]=x*10;
							gy[citem]=y*10+2+t*5;
							gp[citem]=(int)(50*Math.random());
							citem++;
						}
					} else if (y>5) {
						gt[citem]=gv[citem]=6; // Turret
						gu[citem]=true;
						gx[citem]=x*10+5;
						gy[citem]=y*10+5;
						gbnd[citem]=1;
						citem++;
					}
				} 
			}
		}
	}
	public void generateMaze(int[][] maze, int[] todo, int[] gt, int[] gv, boolean[] gu, double[] gbnd, double[] gx,
			double[] gy, int[] mazex, int[] mazey) {
		gt[0]=1; // player
		gv[0]=1; // player
		gu[0]=true;
		gx[0]=15;
		gy[0]=15;
		gbnd[0]=1;
		for (t=1;t<5;t++) {
			gt[t]=2; // player bullet
			gv[t]=2;
			gbnd[t]=0.2;
			gt[t+5]=4; // Explosion
			gv[t+5]=4;
		}
		for (t=10;t<20;t++) {
			gt[t]=11; // enemy bullet
			gv[t]=2;
			gbnd[t]=0.1;
			gt[t+10]=12; // enemy bullet Explosion
			gv[t+10]=4;
		}		
		//
		// Generate maze
		//
		t=0;
		for (x = 0; x < 20; ++x) {
			for (y = 0; y < 20; ++y) {
				maze[x][y]=(x == 0 || x == 19 || y == 0 || y == 19)?32:63;
			}
		}
		x = 1 + (int) ( Math.random() * 18);
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
	}
	
	public void drawRadar(int[] gv, boolean[] gu, double[] gx, double[] gy, Graphics2D g) {
		g.translate(10,10);
		g.scale(0.01,0.01);
		g.setColor(new Color(0,128,0,128));
		g.fillRect(-5,-5,210,210);
		for (t=0;t<255;t++) {
			if (gu[t]) {
				n=gv[t];
				if ((n==10)||(n==1)) {
					g.setColor(n==1?Color.WHITE:Color.CYAN);
					g.fillRect((int)gx[t],(int)gy[t], 10,10);
				} 
			}
		}
	}

	public boolean drawGameOver(int scientists, int lives, double[] gx, double[] gy, Graphics2D g) {
		boolean restart = false;
		g.translate(gx[0],gy[0]);
		g.setColor(Color.WHITE);
		g.setFont(g.getFont().deriveFont(4f));
		if((lives<0)||(scientists==4)) {
			g.drawString(lives<0?"Game Over":"Well Done",-10,0);
			restart =m.getK()[KeyEvent.VK_SPACE];
			gx[0]+=0.1;
			if (gx[0]>210) {
				gx[0]=-10;
			}
		}
		return restart;
	}

	public void drawLives(int scientists, int lives, Graphics2D g) {
		for (t=0;t<4;t++) {
			if (lives>t) {
				g.setColor(Color.ORANGE);
				g.fillRect(-4+t*20,205,15,15);
			}
			if (scientists>t) {
				g.setColor(Color.WHITE);
				g.fillRect(186-t*20,205,15,15);
			}
		}
	}

	public void handleBullet(boolean[] gu, double[] gx, double[] gy, double[] gdx, double[] gdy) {
		gu[t]=true;
		gx[t]=gx[0];
		gy[t]=gy[0];
		if (gdx[0]==0) {
			gdx[t]=0;
			gdy[t]=0.3;
		} else {
			gdx[t]=gdx[0]>0?0.4:-0.4;
			gdy[t]=gdy[0]+Math.abs(gdx[0]/8);
		}
	}

	public int saveScientist(int scientists, boolean[] gu, int gcol) {
		gu[gcol]=false;
		scientists++;
		return scientists;
	}

	public void dunno(int[] ga, boolean[] gu, double[] gx, double[] gy, int gobj, int gcol) {
		gu[gcol]=false;
		gu[gobj]=false;
		gu[gobj+5]=true;
		gx[gobj+5]=gx[gcol];
		gy[gobj+5]=gy[gcol];
		ga[gobj+5]=12;
	}

	public int respawn(int[] ga, boolean[] gu, double[] gx, double[] gy, int gobj) {
		int invincible;
		gu[gobj]=false;
		gu[6]=true;
		gx[6]=gx[gobj];
		gy[6]=gy[gobj];
		ga[6]=16;
		invincible=200;
		return invincible;
	}

	public boolean collisionDetection(int[] gt, int[] gp, double[] gx, double[] gy, int gobj, int gcol, boolean hit) {
		if ((gt[gcol]==8)||(gt[gcol]==9)) {
			if (gp[gcol]%100>50) {
				if (gt[gcol]==8) {
					if((gy[gobj]-gy[gcol]<9)&&(gy[gobj]-gy[gcol]>0)) {
						hit=(Math.abs(gx[gcol]-gx[gobj])<1);
					}
				} else {
					if ((gx[gobj]-gx[gcol]<9)&&(gx[gobj]-gx[gcol]>1)) {
						hit=(Math.abs(gy[gcol]-gy[gobj])<1);
					}
				}
			}
		}
		return hit;
	}
}
