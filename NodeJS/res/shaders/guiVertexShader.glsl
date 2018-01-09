#version 140

in vec2 position;

out vec2 textureCoords;

uniform vec2 translation;
uniform vec2 scale;

void main(void){

	gl_Position = vec4(((position * scale) + translation), 0.0, 1.0);
	gl_Position.x = gl_Position.x * 2.0 - 1.0;
	gl_Position.y = (gl_Position.y * 2.0 - 1.0) * -1;
	textureCoords = vec2((position.x+1.0)/2.0, 1 - (position.y+1.0)/2.0);
}
