package com.toep;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;

import javax.swing.JPanel;

import com.toep.game.Main;

public abstract class Renderer extends JPanel {



	private static final long serialVersionUID = 1L;
	BufferedImage img;
	public int[] data;		
	public int tick = 0;
	public int backgroundColor = 0xff000000;
	public int fontColor = 0xffaeaeae;
	protected int mouseX, mouseY;
	public Renderer() {
		img = new BufferedImage((int) (Main.WIDTH), (int) (Main.HEIGHT),
				BufferedImage.TYPE_INT_ARGB);
		data = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
		System.out.println("Initialized image data..");
	}

	public int[] getPixels(){return data;}
	protected void setPixels(int index, int col) {
		data[index] = col;
	}
	protected int getDataLength() {
		return data.length;
	}
	public abstract void paint();
	
	public void setDefaultBackgroundColor(int i) {
		backgroundColor = i;
		
	}
	public int getDefaultBackgroundColor() {
		return backgroundColor;
		
	}
	public void drawRect(int width, int height, int x, int y, int col) {
		drawToArr(x, y, width, height, col);
	}

	public void drawString(String str, int x, int y, int col) {
		for (int i = 0; i < str.length(); i++) {
			drawToArr(Font.letters[Font.fontLegend.indexOf(str.charAt(i))], x + (i << 3),
					y, 8, 8, col);
		}
	}
	public void drawSprite(Sprite sprite, int i, int j){
		for (int x = 0; x < sprite.width; x++) {
			for (int y = 0; y < sprite.height; y++) {
				if (i + j * Main.WIDTH + x + y * Main.WIDTH > 0
						&& i + j * Main.WIDTH + x + y * Main.WIDTH < this.data.length)
					data[i + j * Main.WIDTH + x + y * Main.WIDTH] = sprite.pixels[x+y*sprite.width];
			}
		}
	}

	private void drawToArr(int[] data, int i, int j, int width, int height,
			int col) {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (data[x + (y * width)] != 0xffff00ff
						&& i + j * Main.WIDTH + x + y * Main.WIDTH > 0
						&& i + j * Main.WIDTH + x + y * Main.WIDTH < this.data.length && i+x < Main.WIDTH)
					this.data[i + j * Main.WIDTH + x + y * Main.WIDTH] = col;
			}
		}
	}

	private void drawToArr(int i, int j, int width, int height, int col) {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (i + j * Main.WIDTH + x + y * Main.WIDTH > 0
						&& i + j * Main.WIDTH + x + y * Main.WIDTH < data.length)
					data[i + j * Main.WIDTH + x + y * Main.WIDTH] = col;
			}
		}
	}
	


	public void render() {
		Graphics g = getGraphics();	
		g.drawImage(img, 0, 0, Main.WIDTH, Main.HEIGHT, null);
	}

	public void clear() {
		for (int i = 0; i < data.length; i++) {
 			data[i] = backgroundColor;
		}
	}

	public abstract void update(boolean[] keys);

	/*
	 int x = e.getX() - 8;
	 int y = e.getY() - 31;
	 */
	public abstract void mousePressed(MouseEvent e);

	public abstract void keyDown(boolean[] keys);

	public static boolean keys(int key) {
		return GameLoop.listener.keys[key];
	}

	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

}
