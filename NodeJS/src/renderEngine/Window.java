package renderEngine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.nio.DoubleBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import entities.Camera;

public class Window {
	private static int windowWidth;
	private static int windowHeight;
	
	private static double mouseDeltaScroll;
	private static double mouseX, mouseY;
	private static double mouseDeltaX, mouseDeltaY;
	private static DoubleBuffer mouseXBuffer, mouseYBuffer;
	private static long window;
	
	private static boolean mouseLeftDown = false;
	private static boolean mouseLeftClicked = false, mouseLeftLatch = false;
	private static boolean mouseRightDown = false;
	private static boolean mouseRightClicked = false, mouseRightLatch = false;
	
	private static long lastFrameTime;
	private static float deltaTime;
	
	public static void init(int width, int height) {
		//Initialize GLFW.
		if (glfwInit() == false) 
			throw new IllegalStateException("Failed to initialize GLFW.");
		
		//Create a window.
		windowWidth = width;
		windowHeight = height;
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_SAMPLES, 4);
		window = glfwCreateWindow(windowWidth, windowHeight, "", 0, 0);
		if (window == 0)
			throw new IllegalStateException("Failed to create a window.");
		
		GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(window, (vidMode.width() - windowWidth) / 2, (vidMode.height() - windowHeight) / 2);
		
		glfwShowWindow(window);
		
		glfwMakeContextCurrent(window);
		
		glfwSetScrollCallback(window, GLFWScrollCallback.create((win, dx, dy) -> {
			mouseDeltaScroll = dy;
		}));
		
		mouseXBuffer = BufferUtils.createDoubleBuffer(1);
		mouseYBuffer = BufferUtils.createDoubleBuffer(1);
		
		glfwSetMouseButtonCallback(window, GLFWMouseButtonCallback.create((win, button, action, mods) -> {
			if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
				mouseLeftDown = true;
				mouseLeftClicked = true;
				mouseLeftLatch = false;
			}
			if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_RELEASE)
				mouseLeftDown = false;
			
			if (button == GLFW_MOUSE_BUTTON_RIGHT && action == GLFW_PRESS) {
				mouseRightDown = true;
				mouseRightClicked = true;
				mouseRightLatch = false;
			}
			if (button == GLFW_MOUSE_BUTTON_RIGHT && action == GLFW_RELEASE)
				mouseRightDown = false;
		}));
		
		//Initialize OpenGL.
		GL.createCapabilities();
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		
		//Initialize timer.
		lastFrameTime = getCurrentTime();
	}
	
	public static boolean shouldClose() {
		return glfwWindowShouldClose(window);
	}
	
	public static int getWidth() { return windowWidth; }
	public static int getHeight() { return windowHeight; }
	public static float getAspectRatio() { return (float)windowWidth / (float)windowHeight; }
	
	public static boolean getKey(int key) {
		if (glfwGetKey(window, key) == GL_TRUE) 
			return true;
		else 
			return false;
	}
	
	public static double getMouseDeltaScroll() { return mouseDeltaScroll; }
	public static double getMouseX() { return mouseX; }
	public static double getMouseY() { return mouseY; }
	public static double getNormalizedMouseX() { return mouseX / (double)windowWidth; }
	public static double getNormalizedMouseY() { return mouseY / (double)windowHeight; }
	public static double getMouseDeltaX() { return mouseDeltaX; }
	public static double getMouseDeltaY() { return mouseDeltaY; }
	public static double getNormalizedMouseDeltaX() { return mouseDeltaX / (double)windowWidth; }
	public static double getNormalizedMouseDeltaY() { return mouseDeltaY / (double)windowHeight; }
	
	public static boolean getLeftMouseDown() { return mouseLeftDown; }
	public static boolean getRightMouseDown() { return mouseRightDown; }
	
	public static void pollEvents() {
		mouseDeltaScroll = 0;
		double lastMouseX = mouseX;
		double lastMouseY = mouseY;
		
		glfwPollEvents();
		glfwGetCursorPos(window, mouseXBuffer, mouseYBuffer);
		mouseX = mouseXBuffer.get(0);
		mouseY = mouseYBuffer.get(0);
		mouseDeltaX = mouseX - lastMouseX;
		mouseDeltaY = mouseY - lastMouseY;
		
		if (mouseLeftClicked == true && mouseLeftLatch == false) 
			mouseLeftLatch = true;
		else if (mouseLeftClicked == true && mouseLeftLatch == true) 
			mouseLeftClicked = false;
		
		if (mouseRightClicked == true && mouseRightLatch == false)
			mouseRightLatch = true;
		else if (mouseRightClicked == true && mouseRightLatch == true)
			mouseRightClicked = false;
		
		long currentFrameTime = getCurrentTime();
		deltaTime = (currentFrameTime - lastFrameTime) / 1000f;
		lastFrameTime = currentFrameTime;
	}
	
	public static float getDeltaTime() { return deltaTime; }
	public static long getCurrentTime() { return  System.nanoTime() / 1000000; }
	
	public static void clearBuffer(Camera camera) {
		glClearColor(camera.getSkyRed(), camera.getSkyGreen(), camera.getSkyBlue(), 1f);
		glEnable(GL_DEPTH_TEST);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);	
	}
	
	public static void swapBuffers() {
		glfwSwapBuffers(window);			
	}
	
	public static void cleanUp() {
		glfwDestroyWindow(window);
		glfwTerminate();
	}
}
