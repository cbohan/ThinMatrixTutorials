package textures;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

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
	private byte[][][] data;
	
	public Texture(int id, int width, int height) {
		this.id = id;
		this.width = width;
		this.height = height;
		this.data = new byte[width][height][4];
	}
	
	public Texture(String filename){
		this(filename, false, true);
	}
	
	public Texture(String fileName, boolean clamp, boolean mip) {
		if (checkForCustomTextureFile(fileName) == false) { 
			System.err.println("Creating new custom texture file for: " + fileName);
			
			ByteBuffer pixels = loadImageFileToByteBuffer(fileName);
			createCustomTextureFile(fileName, pixels);
		}
		
		ByteBuffer pixels = loadCustomTextureFile(fileName);
		
		id = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, id);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		
		if (clamp) {
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
		}
		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_SRGB8_ALPHA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
		
		if (mip) {
			//Enable mipmapping.
			glGenerateMipmap(GL_TEXTURE_2D);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, 0);
			
			//Enable anisotropic filtering.
			if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic){
				float anisotropicAmount = Math.min(8, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
				glTexParameterf(GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, anisotropicAmount);
			}
		}
	}
	
	private ByteBuffer loadCustomTextureFile(String fileName) {
		String customFileName = generateCustomFileName(fileName);
		ByteBuffer fileContents = getFileAsByteBuffer(customFileName);
		ByteBuffer pixelData = BufferUtils.createByteBuffer(fileContents.limit() - 8 - 4 - 4);
		width = fileContents.getInt(8);
		height = fileContents.getInt(12);
		for (int i = 0; i < pixelData.limit(); i++) 
			pixelData.put(fileContents.get(i + 8 + 4 + 4));
		pixelData.flip();
		return pixelData;
	}
	
	private void createCustomTextureFile(String fileName, ByteBuffer pixels) {
		String customFileName = generateCustomFileName(fileName);
		File originalFile = new File(fileName);
		long lastModified = originalFile.lastModified();
		
		ByteBuffer customFile = BufferUtils.createByteBuffer(pixels.limit() + 8 + 4 + 4); //data + lastmodified + width/height
		customFile.putLong(lastModified);
		customFile.putInt(width);
		customFile.putInt(height);
		customFile.put(pixels);
		customFile.flip();
		
		try {
			FileOutputStream out = new FileOutputStream(customFileName);
			for (int i = 0; i < pixels.limit(); i++)
				out.write(customFile.get());
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean checkForCustomTextureFile(String fileName) {
		String customFileName = generateCustomFileName(fileName);
		
		File customFile = new File(customFileName);
		if (customFile.exists() == false || customFile.isDirectory())
			return false;
		
		File originalFile = new File(fileName);
		long lastModified = originalFile.lastModified();
		
		ByteBuffer buffer = getFileAsByteBuffer(customFileName);
		try {
			long customLastModified = buffer.getLong(0);
			if (lastModified != customLastModified)
				return false;
		} catch (Exception e) {
			return false;
		}
		
		return true;
	} 
	
	private String generateCustomFileName(String fileName) {
		String customFileName = fileName;
		if (customFileName.indexOf(".") > 0)
			customFileName = customFileName.substring(0, fileName.lastIndexOf("."));
		customFileName += ".ctex";
		
		return customFileName;
	}
	
	private ByteBuffer getFileAsByteBuffer(String fileName) {
		byte[] customFileData = null;
		try {
			customFileData = Files.readAllBytes(Paths.get(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		ByteBuffer buffer = BufferUtils.createByteBuffer(customFileData.length);
		buffer.put(customFileData);
		buffer.flip();
		return buffer;
	}
	
	private ByteBuffer loadImageFileToByteBuffer(String fileName) {
		BufferedImage bufferedImage = null;
		
		try {
			bufferedImage = ImageIO.read(new File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		width = bufferedImage.getWidth();
		height = bufferedImage.getHeight();
		
		int[] pixels_raw = new int[width * height];
		
		pixels_raw = bufferedImage.getRGB(0, 0, width, height, null, 0, width);
		data = new byte[width][height][4];
		
		ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				int pixel = pixels_raw[i*width + j];
				data[i][j][0] = (byte)((pixel >> 16) & 0xFF);
				data[i][j][1] = (byte)((pixel >> 8) & 0xFF);
				data[i][j][2] = (byte)((pixel >> 0) & 0xFF);
				data[i][j][3] = (byte)((pixel >> 24) & 0xFF);
				
				pixels.put((byte)((pixel >> 16) & 0xFF)); //red
				pixels.put((byte)((pixel >> 8) & 0xFF)); //green
				pixels.put((byte)((pixel >> 0) & 0xFF)); //blue
				pixels.put((byte)((pixel >> 24) & 0xFF)); //alpha
			}
		}
		pixels.flip();
		
		return pixels;
	}
	
	public void bind(int textureUnit) {
		glActiveTexture(GL_TEXTURE0 + textureUnit);
		glBindTexture(GL_TEXTURE_2D, id);
	}
	
	public void cleanUp() {
		glDeleteTextures(id);
	}
	
	public int getId() { return id; }
	public int getNumberOfRows() { return numberOfRows; }
	public void setNumberOfRows(int rows) { numberOfRows = rows; }
	public boolean getHasTransparency() { return hasTransparency; }
	public void setHasTransparency(boolean transparent) { hasTransparency = transparent; }
	public boolean getOverrideNormals() { return overrideNormals; }
	public void setOverrideNormals(boolean override) { overrideNormals = override; }
	public byte getRed(int x, int y) { return data[x][y][0]; }
	public byte getGreen(int x, int y) { return data[x][y][1]; }
	public byte getBlue(int x, int y) { return data[x][y][2]; }
	public byte getAlpha(int x, int y) { return data[x][y][3]; }
}
