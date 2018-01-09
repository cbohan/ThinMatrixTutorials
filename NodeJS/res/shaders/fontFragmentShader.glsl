#version 330

in vec2 vert_textureCoords;

out vec4 out_Color;

uniform vec3 color;
uniform sampler2D fontAtlas;

void main(void){
	out_Color = vec4(color, texture(fontAtlas, vert_textureCoords).a);
}