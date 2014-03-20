package main;

import java.util.*;

public abstract class PhOb implements Updateable {
	//anything with mass, charge, extra charge, position and velocity
	public int radius;
	double mass;
	double x,y;
	double vx,vy;
	double q1,q2,mq;
	double dvs[][]={{0,0},{0,0}};
	public static final int numloops=3;
	ArrayList<Double> EPEs;
	static ArrayList<PhOb> all=new ArrayList<PhOb>();
	
	PhOb(double m, double x, double y, double vx, double vy){
		mass=m;
		this.x=x;
		this.y=y;
		this.vx=vx;
		this.vy=vy;
		this.q1=0;
		this.q2=0;
		mq=Math.sqrt(q1*q1+q2*q2);
	}
	
	PhOb(double m, double x, double y, double vx, double vy, double q1, double q2){
		mass=m;
		this.x=x;
		this.y=y;
		this.vx=vx;
		this.vy=vy;
		this.q1=q1;
		this.q2=q2;
		mq=Math.sqrt(q1*q1+q2*q2);
		MainApplet.all.add(this);
	}
	
	protected void initEPE(){
		EPEs=new ArrayList<Double>();
		for(int i=0;i<all.size();i++){
			double e=EPEwith(all.get(i));
			all.get(i).EPEs.add(e);
			EPEs.add(e);
		}
		EPEs.add(0.0);
		all.add(this);
	}
	
	public double KPE(){
		return mass*(vx*vx+vy*vy);
	}
	
	public double EPEwith(PhOb Other){
		double dx = x-Other.x;
		double dy = y-Other.y;
		double d=Math.sqrt((dx*dx+dy*dy));
		return MainApplet.k*(q1*Other.q1+q2*Other.q2)/d;
	}
	
	public double EPE(){
		//TODO sort out repulsion
		double E=0;
		for(PhOb j:all) if(j!=this){
			if(MainApplet.WALL==1 || MainApplet.WALL==0){
				double dx = x-j.x;
				double dy = y-j.y;
				double d=Math.sqrt((dx*dx+dy*dy));
				E+=MainApplet.k*(q1*j.q1+q2*j.q2)/d;
			}
			if(MainApplet.WALL==2) for (int i1=-3;i1<=3;i1++) for (int j1=-3;j1<=3;j1++){
				double dx = x-j.x+i1*MainApplet.D.width;
				double dy = y-j.y+j1*MainApplet.D.height;
				double d=Math.sqrt((dx*dx+dy*dy));
				E+=MainApplet.k*(q1*j.q1+q2*j.q2)/d;
			}
		}
		return E;
	}
	
	public double mh(){
		return mass*vx;
	}
	
	public double mv(){
		return mass*vy;
	}
	
	abstract public boolean[][] touching(PhOb other);

	public void tick() {
		double fx=0,fy=0;
		/*for(int i=0;i<all.size();i++){
			PhOb pi=all.get(i);
			if (pi!=this){
				double dx = x-pi.x;
				double dy = y-pi.y;
				double k2=EPEwith(pi);
				double v1sq=vx*vx+vy*vy;
				double v2sq=v1sq+(EPEs.get(i)-k2)/mass;
			}
		}*/
		for(PhOb j:all) if(j!=this){
			if(MainApplet.WALL==1 || MainApplet.WALL==0){
				double dx = x-j.x;
				double dy = y-j.y;
				double d=Math.sqrt((dx*dx+dy*dy));
				double rt=radius+j.radius;
				double rep=Math.pow(rt,MainApplet.repulsion)*mq*j.mq/Math.pow(d,MainApplet.repulsion);
				double F=MainApplet.k*((q1*j.q1+q2*j.q2)+rep)/(d*d*d);
				//System.out.println(F);
				//if(touching(j)[1][1])){
					fx+=F*dx;//extra division by d happens in F
					fy+=F*dy;
				//}
			}
			if(MainApplet.WALL==2) for (int i1=-2;i1<=2;i1++) for (int j1=-2;j1<=2;j1++){
				double dx = x-j.x+i1*MainApplet.D.width;
				double dy = y-j.y+j1*MainApplet.D.height;
				double d=Math.sqrt(dx*dx+dy*dy);
				double rt=radius+j.radius;
				double rep=Math.pow(rt,MainApplet.repulsion)*mq*j.mq/Math.pow(d,MainApplet.repulsion);
				double F=MainApplet.k*((q1*j.q1+q2*j.q2)+rep)/(d*d*d);
				//System.out.println(F);
				//if(!(i1<=1 && i1>=-1 && j1<=1 && j1>=-1 && touching(pj)[i1+1][j1+1])){
					fx+=F*dx;//extra division by d happens in F
					fy+=F*dy;
				//}
			}
		}
		dvs[0][0]+=fx/mass/MainApplet.TPS;
		dvs[0][1]+=fy/mass/MainApplet.TPS;
	}

	public void tock() {
		for(int i=0;i<dvs.length;i++){
			vx+=dvs[i][0];
			vy+=dvs[i][1];
			dvs[i][0]=0;
			dvs[i][1]=0;
		}
		vx*=MainApplet.mu;
		vy*=MainApplet.mu;
		x+=vx/MainApplet.TPS;
		y+=vy/MainApplet.TPS;
		if(MainApplet.WALL==2){
			x+=MainApplet.D.width;
			x%=MainApplet.D.width;
			y+=MainApplet.D.height;
			y%=MainApplet.D.height;
		}
	}
}
