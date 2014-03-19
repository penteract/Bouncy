package main;

import java.awt.Graphics2D;

public interface Updateable {
	abstract void tick();
	abstract void tock();
	abstract void paint(Graphics2D g);
}
