package main;

import java.applet.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class MainApplet extends Applet implements Runnable {

	private static final int maxFPS = 1000;
	public static final int TPS = 100;
	public static int WALL = 1;
	public static int num = 0;
	public static final double SPEED=5;
	public static final double e=1;
	public static final double mu=Math.pow(0.995,1.0/TPS);
	public static final double k=100;
	public static final boolean collisions=true;
	public static final double[] q1s={1,-0.5,-0.5,-1,0.5,0.5,0,Math.sqrt(3)/2,-Math.sqrt(3)/2,0,Math.sqrt(3)/2,-Math.sqrt(3)/2};
	public static final double[] q2s={0,Math.sqrt(3)/2,-Math.sqrt(3)/2,0,Math.sqrt(3)/2,-Math.sqrt(3)/2,1,-0.5,-0.5,-1,0.5,0.5,0};
	public static List<Updateable> all;
	Thread t;
	public static Dimension D,D2;
	Image bufi;
	Graphics2D bufg;
	long nextTick,now,lastTick;
	public static long wait;
	double kpe=0;
	double kpe2=0;
	
	public void init(){
		D=new Dimension(400,400);
		resize((int) (D.width*1.5),(int) (D.height*1.5));
		D2=getSize();
		bufi=createImage(D2.width, D2.height);
		bufg=(Graphics2D) bufi.getGraphics();
		bufg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		bufg.translate((int) (D.width*0.25),(int) (D.height*0.25));
		all = new ArrayList<Updateable>();
		setBackground(Color.white);
		setForeground(Color.blue);
		//for (int i=0;i<100;i++){randCirc(1,Math.random()*90);}
		for (int i=0;i<36;i++){randCirc(7/*(int)((Math.random()+0.5)*10)*/,50);}
		//new Circ(60,150,0,0,10,90,0);
		//new Circ(380,150,0,0,10,-90,0);
		//new Circ(200,200,0,0,5,-50,0);
		
		nextTick=(long) (System.currentTimeMillis()*SPEED);
		
		t=new Thread(this);
		t.start();
	}
	
	public Circ randCirc(int r,double speed){
		double x = 0,y=0;
		boolean coll=true;
		while (coll){
			x=Math.random()*D.width;
			y=Math.random()*D.height;
			coll=false;
			for (Updateable i: all) {if (i.getClass()==Circ.class){
				Circ c=(Circ)i;
				if(sqr(c.x-x)+sqr(c.y-y)<=sqr(c.radius+r)){
					coll=true;
				}
			}
			}
		}
		double thv,thq,mv,mq;
		thv=Math.random()*Math.PI*2;
		thq=Math.random()*Math.PI*2;
		mv=Math.sqrt(Math.random());
		mq=1;//Math.sqrt(Math.random());
		int qt=(num++)%6;
		//return new Circ(x,y,speed*Math.cos(thv)*mv,speed*Math.sin(thv)*mv,r,Math.sin(thq)*mq*r*r,Math.cos(thq)*mq*r*r);
		return new Circ(x,y,speed*Math.cos(thv)*mv,speed*Math.sin(thv)*mv,r,q1s[qt]*r*r/1.25,q2s[qt]*r*r/1.25);
	}
	
	public void update(Graphics g){
		paint((Graphics2D)bufg);
		g.drawImage(bufi, 0, 0, this);
	}
	
	public void paint(Graphics2D g){
        g.setColor(Color.white);
        g.fill(new Rectangle2D.Double(-(int) (D.width*0.25),-(int) (D.height*0.25),(int) (D.width*1.5),(int) (D.height*1.5))); 
        g.setColor(Color.blue);
        g.draw(new Rectangle2D.Double(0,0,D.width,D.height)); 
        g.drawString(Double.toString(kpe), 0, 20);
        g.drawString(Double.toString(kpe2), 0, 40);
		for (Updateable i: all) {
			i.paint(g);
		}
	}
	
	public static double sqr(double a){
		return a*a;
	}

	
	public void phy(){
		for (Updateable i: all) {
			i.tick();
		}
		if (collisions) for (int i=0;i<all.size();i++) if (all.get(i).getClass()==Circ.class){
			
			Circ c=(Circ)all.get(i);
			for (Updateable j:all.subList(i+1, all.size())) if (j.getClass()==Circ.class){
				Circ d=(Circ)j;
				if(WALL==2) for (int i1=-1;i1<=1;i1++) for (int j1=-1;j1<=1;j1++){
					if(sqr(c.x-d.x+i1*D.width)+sqr(c.y-d.y+j1*D.height)<=sqr(c.radius+d.radius)){
						c.x+=i1*D.width;
						c.y+=j1*D.height;
						boolean isz=c.vy-d.vy==0;
						if (isz){
							c.flip();
							d.flip();
						}
						c.collide(d);
						if (isz){
							c.flip();
							d.flip();
						}
					}
				}
				if(WALL==1){
					if(sqr(c.x-d.x)+sqr(c.y-d.y)<=sqr(c.radius+d.radius)){
						boolean isz=c.vy-d.vy==0;
						if (isz){
							c.flip();
							d.flip();
						}
						c.collide(d);
						if (isz){
							c.flip();
							d.flip();
						}
					}
				}
			}
		}
		for (Updateable i: all) {
			i.tock();
		}
		
		
		kpe=0;
		kpe2=0;
		for (Updateable i: all) {
			if (i.getClass()==Circ.class){
				Circ c=(Circ)i;
				kpe+=c.KPE()+c.EPE();
				kpe2+=c.mv();
			}
		}
	}
	
	@Override
	public void run() {

		while (true){
			now=(long) (System.currentTimeMillis()*SPEED);
			if (nextTick<=now){
				phy();
				lastTick=nextTick;
				nextTick=nextTick+1000/TPS;
			}
			wait=now-lastTick;
			repaint();
			if(now%1000<10)
				try{Thread.sleep(1000/maxFPS);}
					catch (InterruptedException e){;}
		}

	}

}
