package main;

/*
<applet code="SampleApplet.class" CodeBase="" width=300 height=400></applet>
*/

import java.awt.*;
import java.awt.geom.*;

public class Circ extends PhOb{
	public int radius;
	public static final int DENSITY=1;
	//public static final Color[] cols={Color.blue,Color.red,Color.green,Color.cyan,Color.yellow,Color.magenta};
	Color col;
	
	private void setcol(){
		col=Color.getHSBColor((float) (Math.atan2(q1,q2)/Math.PI/2+1), 1,(float) (Math.sqrt(q1*q1+q2*q2)/mass));
	}
	
	public Circ(double m, double x, double y, double vx, double vy, int r) {
		super(m, x, y, vx, vy);
		radius=r;
		setcol();
		initEPE();
	}
	
	public Circ(double x, double y, double vx, double vy, int r) {
		super(r*r*DENSITY, x, y, vx, vy);
		radius=r;
		col=Color.black;
		initEPE();
	}
	
	public Circ(double m, double x, double y, double vx, double vy, int r, double q1, double q2) {
		super(m, x, y, vx, vy,q1,q2);
		radius=r;
		setcol();
		initEPE();
	}
	
	public Circ(double x, double y, double vx, double vy, int r, double q1, double q2) {
		super(r*r*DENSITY, x, y, vx, vy,q1,q2);
		radius=r;
		setcol();
		initEPE();
	}
	
	@Override
	public void tick() {
		super.tick();
		if(MainApplet.WALL==1){
			if(y-radius<0){
				vy=Math.abs(vy);
				//y-=y-radius;
			}
			if(y+radius>MainApplet.D.height){
				vy=-Math.abs(vy);
				//y-=y+radius-MainApplet.D.height;
			}
			if(x-radius<0){
				vx=Math.abs(vx);
				//x-=x-radius;
			}
			if(x+radius>MainApplet.D.width){
				vx=-Math.abs(vx);
				//x-=x+radius-MainApplet.D.width;
			}
		}
	}

	@Override
	public void paint(Graphics2D g) {
		g.setPaint(col);
		if(MainApplet.WALL==2){
			for (int i=-1;i<=1;i++) for (int j=-1;j<=1;j++){
				Shape S=new Ellipse2D.Double( (i*MainApplet.D.width+x+vx*MainApplet.wait/1000-radius), (j*MainApplet.D.height+y+vy*MainApplet.wait/1000-radius), radius*2, radius*2);
				g.fill(S);
			}
		}
		else{
			Shape S=new Ellipse2D.Double( (x+vx*MainApplet.wait/1000-radius), (y+vy*MainApplet.wait/1000-radius), radius*2, radius*2);
			g.fill(S);
		}
	}
	
	public double sqr(double a){
		return a*a;
	}
	
	public void flip(){
		double temp=x;
		x=y;
		y=temp;
		temp=vx;
		vx=vy;
		vy=temp;
		for(int i=0;i<dvs.length;i++){
			temp=dvs[i][0];
			dvs[i][0]=dvs[i][1];
			dvs[i][1]=temp;
		}
		
	}
	
	public void collide(Circ other){
		double x1,y1,x2,y2,ux,uy,v1x,v1y,v2x,v2y,m1,m2,c,k,e, A,B,C;
		x1=x;
		y1=y;
		x2=other.x;
		y2=other.y;
		ux=vx-other.vx;
		uy=vy-other.vy;
		m1=mass;
		m2=other.mass;
		c=m2/m1;
		k=(x1-x2)/(y1-y2);
		e=m1*(sqr(ux)+sqr(uy));
		A=(k*k+1)*c*(c+1);
		B=-2*c*(k*ux+uy);
		C=ux*ux+uy*uy-e/m1;
		v2y=((-B+((y1-y2<0)?(+Math.sqrt(B*B-4*A*C)):(-Math.sqrt(B*B-4*A*C))))/(2*A));
		v2x=k*v2y;
		v1y=uy-c*v2y;
		v1x=ux-c*v2x;
		/*vx=v1x+other.vx;
		vy=v1y+other.vy;
		other.vx=v2x+other.vx;
		other.vy=v2y+other.vy;*/

		dvs[1][0]+=((v1x+other.vx)-vx)*MainApplet.e;
		dvs[1][1]+=((v1y+other.vy)-vy)*MainApplet.e;
		other.dvs[1][0]+=v2x*MainApplet.e;
		other.dvs[1][1]+=v2y*MainApplet.e;
		
		/*double vxm,vym;
		vxm=(vx*mass+other.vx*other.mass)/(mass+other.mass);
		vym=(vy*mass+other.vy*other.mass)/(mass+other.mass);
		vx=(vx-vxm)*MainApplet.e+vxm;
		other.vx=(other.vx-vxm)*MainApplet.e+vxm;
		vy=(vy-vym)*MainApplet.e+vym;
		other.vy=(other.vy-vym)*MainApplet.e+vym;*/

		/*vxm=(vx*mass+other.vx*other.mass)/(mass+other.mass);
		vym=(vy*mass+other.vy*other.mass)/(mass+other.mass);
		dvs[1][0]+=(vx-vxm)*(MainApplet.e-1);
		other.dvs[1][0]+=(other.vx-vxm)*(MainApplet.e-1);
		dvs[1][1]+=(vy-vym)*(MainApplet.e-1);
		other.dvs[1][1]+=(other.vy-vym)*(MainApplet.e-1);*/
	}

	@Override
	public boolean[][] touching(PhOb other) {
		boolean [][] ret={{false,false,false},{false,false,false},{false,false,false}};
		if (other instanceof Circ){
			Circ c=(Circ)other;
			for (int i=-1;i<=1;i++) for (int j=-1;j<=1;j++){
				double dx = x-c.x+i*MainApplet.D.width;
				double dy = y-c.y+j*MainApplet.D.height;
				if(dx*dx+dy*dy<0.1*(radius+c.radius)*(radius+c.radius)) ret[i+1][j+1]=true;
			}
		}
		return ret;
	}
}
