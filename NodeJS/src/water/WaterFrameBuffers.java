package water;

import postprocessing.FrameBufferObject;
import textures.Texture;
import window.Window;

public class WaterFrameBuffers {
	private int REFLECTION_WIDTH = Window.getWidth();
	private int REFLECTION_HEIGHT = Window.getHeight();
	
	private int REFRACTION_WIDTH = Window.getWidth();
	private int REFRACTION_HEIGHT = Window.getHeight();
	
	private FrameBufferObject reflectionFBO;
	private Texture reflectionTexture;
	
	private FrameBufferObject refractionFBO;
	private Texture refractionTexture;
	private Texture refractionDepthTexture;
		
	public WaterFrameBuffers() {
		initializeReflectionFrameBuffer();
		initializeRefractionFrameBuffer();
	}
	
	public void bindReflectionFBO() {
		reflectionFBO.bind(REFLECTION_WIDTH, REFLECTION_HEIGHT);
	}
	
	public void bindRefractionFBO() {
		refractionFBO.bind(REFRACTION_WIDTH, REFRACTION_HEIGHT);
	}
	
	public void unbindCurrentFBO() {
		reflectionFBO.unbind();
	}
	
	public Texture getReflectionTexture() { return reflectionTexture; }
	public Texture getRefractionTexture() { return refractionTexture; }
	public Texture getRefractionDepthTexture() { return refractionDepthTexture; }
	
	private void initializeReflectionFrameBuffer() {
		reflectionFBO = new FrameBufferObject();
		reflectionTexture = reflectionFBO.createTextureAttachment(REFLECTION_WIDTH, REFLECTION_HEIGHT);
		reflectionFBO.createDepthBufferAttachment(REFLECTION_WIDTH, REFLECTION_HEIGHT);
		reflectionFBO.unbind();
	}
	
	private void initializeRefractionFrameBuffer() {
		refractionFBO = new FrameBufferObject();
		refractionTexture = refractionFBO.createTextureAttachment(REFRACTION_WIDTH, REFRACTION_HEIGHT);
		refractionDepthTexture = refractionFBO.createDepthTextureAttachment(REFRACTION_WIDTH, REFRACTION_HEIGHT);
		refractionFBO.unbind();
	}
	
	public void cleanUp() {
		reflectionFBO.cleanUp();
		refractionFBO.cleanUp();
		reflectionTexture.cleanUp();
		refractionTexture.cleanUp();
		refractionDepthTexture.cleanUp();
	}
}
