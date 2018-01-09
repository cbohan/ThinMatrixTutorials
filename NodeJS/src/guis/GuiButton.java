package guis;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;

import guis.fonts.FontLoader;
import guis.fonts.FontType;
import guis.fonts.GUIText;
import guis.fonts.TextMaster;
import window.Window;

public class GuiButton {
	public interface ButtonClickCallback {
		void onClick();
	}
	
	private static List<GuiButton> activeButtons = new ArrayList<GuiButton>();
	
	private GUIText text;
	private GuiTexture texture;
	private ButtonClickCallback onClick;
	
	public GuiButton(String text, float x, float y, float width, float height) {
		FontType calibri = FontLoader.load("Calibri");
		this.text = new GUIText(text, 1, calibri, new Vector2f(x, y + (height / 2.0f) - (calibri.getLineHeight() / 2.0f)), 
				width, true);
		this.texture = new GuiTexture(new Vector3f(.05f, .05f, .05f), x, y, width, height);
		this.text.setColour(.9f, .9f, .9f);
	}
	
	public void setOnClickCallback(ButtonClickCallback callback) { onClick = callback; }
	public void clearOnClickCallback() { onClick = null; }
	
	public static void update() {
		for (GuiButton button : activeButtons) {
			button.getGuiTexture().setColor(new Vector3f(.05f, .05f, .05f));
			
			if (Window.getNormalizedMouseX() < button.getGuiTexture().getPosition().x)
				continue;
			if (Window.getNormalizedMouseX() > button.getGuiTexture().getPosition().x + button.getGuiTexture().getScale().x)
				continue;
			if (Window.getNormalizedMouseY() < button.getGuiTexture().getPosition().y)
				continue;
			if (Window.getNormalizedMouseY() > button.getGuiTexture().getPosition().y + button.getGuiTexture().getScale().y)
				continue;
			
			if (Window.getLeftMouseClicked() && button.onClick != null) {
				button.onClick.onClick();
				button.getGuiTexture().setColor(new Vector3f(.25f, .25f, .25f));
			} else {
				button.getGuiTexture().setColor(new Vector3f(.15f, .15f, .15f));
			}
		}
	}
	
	public void show() {
		//TextMaster.loadText(text);
		GuiRenderer.addGUI(texture);
		GuiButton.activeButtons.add(this);
	}
	
	public void hide() {
		//TextMaster.removeText(text);
		GuiRenderer.removeGUI(texture);
		GuiButton.activeButtons.remove(this);
	}
	
	public GuiTexture getGuiTexture() { return texture; }
	public GUIText getGUIText() { return text; }
}
