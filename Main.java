package com.toep.game;

import com.toep.GameLoop;
import com.toep.Renderer;

public class Main {
	public static int WIDTH = 800;
	public static int HEIGHT = 800;
	public static void main(String[] args) {
		
		Renderer renderer = new Screen();
		
		GameLoop gl = new GameLoop(WIDTH, HEIGHT);
		System.out.println("setting renderer..");
		gl.setRenderer(renderer);
		gl.start();
	}
}
