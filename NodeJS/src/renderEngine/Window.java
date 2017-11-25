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
	static int windowWidth;
	static int windowHeight;
	
	static double mouseDeltaScroll;
	static double mouseX, mouseY;
	static double mouseDeltaX, mouseDeltaY;
	static DoubleBuffer mouseXBuffer, mouseYBuffer;
	static long window;
	static boolean mouseLeftDown = false;
	static boolean mouseLeftClicked = false, mouseLeftLatch = false;
	
	public static void init(int width, int height) {
		//Initialize GLFW.
		if (glfwInit() == false) 
			throw new IllegalStateException("Failed to initialize GLFW.");
		
		//Create a window.
		windowWidth = width;
		windowHeight = height;
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
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
		}));
		
		//Initialize OpenGL.
		GL.createCapabilities();
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
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
	}
	
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
