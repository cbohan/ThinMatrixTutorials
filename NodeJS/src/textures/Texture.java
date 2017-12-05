package textures;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public class Texture {
	private int id;
	private int width;
	private int height;
	private int numberOfRows = 1;
	private boolean hasTransparency = false;
	private boolean overrideNormals = false;
	
	public Texture(String fileName) {
		BufferedImage bufferedImage;
		
		try {
			bufferedImage = ImageIO.read(new File(fileName));
			width = bufferedImage.getWidth();
			height = bufferedImage.getHeight();
			
			int[] pixels_raw = new int[width * height];
			pixels_raw = bufferedImage.getRGB(0, 0, width, height, null, 0, width);
			
			ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);
			for(int i = 0; i < width; i++) {
				for(int j = 0; j < height; j++) {
					int pixel = pixels_raw[i*width + j];
					pixels.put((byte)((pixel >> 16) & 0xFF)); //red
					pixels.put((byte)((pixel >> 8) & 0xFF)); //green
					pixels.put((byte)((pixel >> 0) & 0xFF)); //blue
					pixels.put((byte)((pixel >> 24) & 0xFF)); //alpha
				}
			}
			pixels.flip();
			
			id = glGenTextures();
			glBindTexture(GL_TEXTURE_2D, id);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
			
			//Enable mipmapping.
			glGenerateMipmap(GL_TEXTURE_2D);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, 0);
			
			//Enable anisotropic filtering.
			if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic){
				float anisotropicAmount = Math.min(8, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
				glTexParameterf(GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, anisotropicAmount);
			}
		} catch(IOException e) {
			System.err.println("Failed to load image: " + fileName);
			e.printStackTrace();
		}
	}
	
	public void bind(int textureUnit) {
		glActiveTexture(GL_TEXTURE0 + textureUnit);
		glBindTexture(GL_TEXTURE_2D, id);
	}
	
	public void cleanUp() {
		glDeleteTextures(id);
	}
	
	public int getNumberOfRows() { return numberOfRows; }
	public void setNumberOfRows(int rows) { numberOfRows = rows; }
	public boolean getHasTransparency() { return hasTransparency; }
	public void setHasTransparency(boolean transparent) { hasTransparency = transparent; }
	public boolean getOverrideNormals() { return overrideNormals; }
	public void setOverrideNormals(boolean override) { overrideNormals = override; }
}
