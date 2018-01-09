#version 140

in vec2 textureCoords;

out vec4 out_Color;

uniform vec3 color;
uniform float useColor;
uniform sampler2D guiTexture;

void main(void){

	out_Color = mix(texture(guiTexture,textureCoords), vec4(color, 1), useColor);

}
