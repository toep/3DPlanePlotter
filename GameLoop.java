package com.toep;

import javax.swing.JFrame;

public class GameLoop extends JFrame implements Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Renderer renderer;

	static Listener listener;
	Thread thread;
	String title = "Unnamed";
	boolean running = false;
	boolean vsync = false;
	//public static int WIDTH = 600, HEIGHT = 460;

	public GameLoop(int width, int height) {
		
		setSize(width + 16, height + 39);
		setVisible(true);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
	}
	
	public void setRenderer(Renderer r){
		renderer = r;
		add(renderer);
		listener = new Listener(renderer);
		addKeyListener(listener);
		addMouseListener(listener);
		addMouseMotionListener(listener);
		System.out.println("renderer initialized!");
		
	}

	public synchronized void start() {

		running = true;
		thread = new Thread(this, "Display");
		System.out.println("Starting thread..");
		thread.start();
	}

	public synchronized void stop() {
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		
		
		
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double ns = 1000000000.0 / 60;
		double delta = 0;
		int frames = 0;
		int updates = 0;
		
		while (running) {
			if(renderer == null){
				System.err.println("Renderer is not initialized! call setRenderer() first!");
				continue;
			}
				
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				update();
				if(vsync){
					render(true);
					frames++;
				}
				delta--;
				updates++;
			}
			// clear?
			if(!vsync){
			render(true);
			frames++;
			}

			if (System.currentTimeMillis() - timer >= 1000) {
				// if(!isApplet)
				setTitle(title + " | " + ("FPS: " + frames + ", UPS: " + updates));
				frames = 0;
				updates = 0;
				timer = System.currentTimeMillis();
			}
		}
	}
	public void setGameTitle(String str){
		title = str;
	}
	private void render(boolean clear) {
		if (clear)
			renderer.clear();
		renderer.paint();

		renderer.render();
	}

	private void update() {
		
		renderer.update(listener.keys);
	}


}
