package guis.fonts;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class FontLoader {
	private static Map<String, FontType> fontMap = new HashMap<String, FontType>();
	private static final String fontPath = "res//fonts//";
	
	public static FontType load(String font) {
		if (fontMap.containsKey(font) == false) {
			FontType fontType = new FontType(Paths.get(fontPath, font + ".png").toString(), 
					Paths.get(fontPath, font + ".fnt").toString());
			fontMap.put(font, fontType);
		}
		
		return fontMap.get(font);
	}
}
