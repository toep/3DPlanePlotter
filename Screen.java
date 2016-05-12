package com.toep.game;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

import com.toep.Font;
import com.toep.Renderer;

public class Screen extends Renderer  {
	private static final float MAIN_WIDTH_2 = Main.WIDTH / 2;
	private static final float MAIN_HEIGHT_2 = Main.HEIGHT / 2;
	float[] sin = new float[500];
	float[] zBuffer;

	float PI_2 = (float) (2 * Math.PI);
	float PIO2 = (float) (Math.PI / 2);
	float yRot = 0, zRot = 5, xRot = 0;
	float cx, cy, sy, sx, cz, sz;
	int size = 1;
	
	int xLL = -size, xUL = size,
			yLL = -size, yUL = size;
	float maxFuncValue = 0;
	private float zScale = 1;
	float xScale = 600/(xUL-xLL);
	float yScale = 600/(yUL-yLL);
	float[][] funcSaved = new float[(int) ((xUL - xLL) * xScale)][(int) ((yUL - yLL) * yScale)];
	float[][] funcdx = new float[funcSaved.length][funcSaved[0].length];
	float[][] funcdy = new float[funcSaved.length][funcSaved[0].length];

	private boolean grid = true;
	private float xScalarMult = 200.0f / (xUL - xLL);
	private float yScalarMult = 200.0f / (yUL - yLL);
	private boolean color = false;
	
	/*Here you enter the function you would like to have displayed*/
	private float func(float x, float y) {
		final int FUNC = 2;
		
		switch(FUNC){
		case 0:return (x*x+y*y-1)*(x*x+y*y-1)*(x*x+y*y-1)-x*x*y*y*y;
		case 1:return x*x-y*y;
		case 2:return x * y * y * y - y * x * x * x;
		case 3:return cos(x*y);
		case 4:return sin(x*y);
		case 5:return x*x-y*x*y;
		case 6:return abs(cos(x*y));
		case 7:return (float) sin(cos(x)*y);
		case 8:return y/(1-x*x);
		default: return 0;
		}
	}
	public Screen() {

		for (int i = 0; i < sin.length; i++) {
			sin[i] = (float) Math
					.sin((double) (i * 2.0f * Math.PI / sin.length));
		}
		setDefaultBackgroundColor(0xff111111);
		System.out.println("init screen");
		
		for (float i = 0; i < funcSaved.length; i++) {
			for (float j = 0; j < funcSaved[0].length; j++) {
				funcSaved[(int) i][(int) j] = func((xUL - xLL) * i
						/ funcSaved.length + xLL, (yUL - yLL) * j
						/ funcSaved[0].length + yLL);
				if (funcSaved[(int) i][(int) j] > maxFuncValue)
					maxFuncValue = funcSaved[(int) i][(int) j];
				// System.out.println(funcSaved[i][j]);
			}
		}
		int dxx = 1;
		for (int i = 0; i < funcSaved.length - dxx; i++) {
			for (int j = 0; j < funcSaved[0].length - dxx; j++) {
				funcdx[i][j] = (funcSaved[i + dxx][j] - funcSaved[i][j])
						* xScale;
				funcdy[i][j] = (funcSaved[i][j + dxx] - funcSaved[i][j])
						* yScale;
			}
			zBuffer = new float[data.length];
		}

		for (int i = 0; i < funcdx.length; i++)
			funcdx[funcdx.length - 1][i] = funcdx[funcdx.length - 2][i];
		for (int i = 0; i < funcdy.length; i++)
			funcdy[i][funcdy.length - 1] = funcdy[i][funcdy.length - 2];
	}

	/*a faster version of Math.sin. Is precalculated*/
	public float sin(double rad) {
		while (rad < 0)
			rad += PI_2;
		while (rad > PI_2)
			rad -= PI_2;
		int index = (int) ((rad / PI_2) * (sin.length));
		if (index == 500)
			index = 0;
		return sin[index];
	}

	public float cos(float rad) {
		return sin(rad + PIO2);
	}

	public float abs(float d) {
		return Math.abs(d);
	}

	@Override
	public void paint() {
		//fill the zbuffer with max values each frame
		Arrays.fill(zBuffer, 0, zBuffer.length, Float.MAX_VALUE);
		drawString("Plotter", 5, 5, 0xffaeeeae);
		float dx = (xUL - xLL + .01f) / (float) funcSaved.length, dy = (yUL- yLL + .01f)/ (float) funcSaved[0].length;
		int x = 0, y = 0;
		
		for (float i = xLL; i <= xUL; i += dx) {
			for (float j = yLL; j <= yUL; j += dy) {
				float z = funcSaved[x][y++];
				plot(i, j, z, color(i, j, z));
			}
			x++;
			y = 0;
		}
	}
	public void mouseDragged(MouseEvent e) {
		int dx = e.getX()-mouseX;
		zRot+=dx/500f;
		if(zRot>PI_2)
			zRot-=PI_2;
		
		if(zRot<0)
			zRot+=PI_2;
		
		mouseX = e.getX();
		mouseY = e.getY();
	}
	/*Calculates the color of a point depending on a few flags. It calculates the normal for the point and shades it accordingly*/
	private int color(float x, float y, float z) {
		int xindex = (int) ((x - xLL) * (funcdx.length - 1) / (xUL - xLL));
		int yindex = (int) ((y - yLL) * (funcdy.length - 1) / (yUL - yLL));

		float nx = funcdx[xindex][yindex];
		float ny = funcdy[xindex][yindex];
		double nz = 1.0;
		
		double l = nx * nx + ny * ny + nz * nz;
		int ux = (int) ((nx / l) * 255.0 + 255) / 2;
		int uy = (int) ((ny / l) * 255.0 + 255) / 2;
		int uz = (int) ((nz / l) * 255.0 + 255) / 2;
		
		float borderWidth = 0.02f;
		if (x < xLL + borderWidth || y < yLL + borderWidth || x > xUL - borderWidth || y > yUL - borderWidth)
			return 0xff001234;
		
		if (grid) 
			if ((int) ((x+xLL) * 100) % 10 == 0 || (int) ((y+yLL) * 100) % 10 == 0)											
				return 0xff555555;
			if(color)
				return (0xff << 24 | ux << 16 | uy << 8 | uz);
			return 0xff << 24 | uz|uz<<8|uz<<16;
	}

	
	/*Plots a vector3 to a point on the screen with a specific color*/
	private void plot(float ox, float oy, float oz, int col) {
		float xx = ox;
		float yy = oy;
		float zz = oz;
		
		//Here I do some basic matrix rotation
		if (yRot != 0) {
			cy = cos(yRot);
			sy = sin(yRot);
			float nzz = zz * cy - xx * sy;
			xx = zz * sy + xx * cy;
			zz = nzz;
		}
		if (xRot != 0) {
			cx = cos(xRot);
			sx = sin(xRot);
			float nyy = yy * cx - zz * sx;
			zz = yy * sx + zz * cx;
			yy = nyy;
		}
		if (zRot != 0) {
			cz = cos(zRot);
			sz = sin(zRot);
			float nxx = xx * cz - yy * sz;
			yy = xx * sz + yy * cz;
			xx = nxx;
		}
		float dis = (float) disFromCam(xx, yy, zz);
		
		//here I calculate the 3d coordinates
		xx *= xScalarMult*2;
		yy *= yScalarMult*2;
		zz *= (200.0 / (zScale));
		yy += MAIN_WIDTH_2;
		zz = MAIN_HEIGHT_2 - zz;
		yy -= xx * 0.5f;
		zz += xx * 0.5f;

		int index = ((int) zz * Main.WIDTH + (int) yy);
		if (index < getDataLength() && index >= 0){
			if(zBuffer[index] > dis){
				data[index] = col;
				zBuffer[index] = dis;
			}
		}

	}

	/*calculates approximate distance from the camera, squared. Used in z buffering*/
	private double disFromCam(double xx, double yy, double zz) {
		return (xx-100)*(xx-100)+(yy-100)*(yy-100)+(zz-100)*(zz-100);
	}
	@Override
	public void update(boolean[] keys) {
		tick++;
		if (zRot > PI_2)
			zRot -= PI_2;
		if (zRot < 0)
			zRot += PI_2;
	
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int x = e.getX() - 8;
		int y = e.getY() - 32;
		System.out.println("x: " + x + ", y:" + y);
	}

	@Override
	public void keyDown(boolean[] keys) {
		final int KEY_RIGHT = 39;
		final int KEY_LEFT = 37;
		if (keys[KEY_RIGHT])
			zRot += .01f;
		if (keys[KEY_LEFT])
			zRot -= .01f;
		for(int i = 0; i < keys.length; i++)
			if(keys[i]) keyPressed((char)i);
		
	}

	private void keyPressed(char ch) {
		System.out.println(ch);
		if(ch == 'G'){
			grid = !grid;
		}
		if(ch == 'C'){
			color = !color;
		}
		
	}
}
