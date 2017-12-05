#version 400

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

out vec2 vert_textureCoords;
out vec4 vert_normal;
out vec3 vert_toLightVector;
out vec3 vert_toCameraVector;
out float vert_visibility;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition;
uniform float overrideNormals;
uniform float fogDensity;
uniform float fogGradient;
uniform float textureNumberOfRows;
uniform vec2 textureOffset;

void main(void) {
	vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
	vec4 positionRelativeToCamera =  viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCamera;
	vec4 actualNormal = transformationMatrix * vec4(normal, 0.0);
	if (overrideNormals > .5) {
		actualNormal = vec4(0, 1, 0, 0);
	}
	vert_normal = actualNormal;	
	vert_textureCoords = (textureCoords / textureNumberOfRows) + textureOffset;
	
	vert_toLightVector = lightPosition - worldPosition.xyz;
	vert_toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;
	
	float distance = length(positionRelativeToCamera.xyz);
	vert_visibility = exp(-pow((distance*fogDensity), fogGradient));
	vert_visibility = clamp(vert_visibility, 0, 1);
}