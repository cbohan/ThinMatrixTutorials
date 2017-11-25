package textures;

public class TerrainTexturePack {
	private Texture backgroundTexture;
	private Texture rTexture;
	private Texture gTexture;
	private Texture bTexture;
	
	public TerrainTexturePack(String backgroundFileName, String rFileName, String gFileName, String bFileName) {
		backgroundTexture = TextureLoader.loadTexture(backgroundFileName);
		rTexture = TextureLoader.loadTexture(rFileName);
		gTexture = TextureLoader.loadTexture(gFileName);
		bTexture = TextureLoader.loadTexture(bFileName);
	}
	
	public Texture getBackgroundTexture() { return backgroundTexture; }
	public Texture getRTexture() { return rTexture; }
	public Texture getGTexture() { return gTexture; }
	public Texture getBTexture() { return bTexture; }
}
