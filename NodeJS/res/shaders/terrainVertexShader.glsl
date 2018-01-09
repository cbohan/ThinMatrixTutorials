#version 400

#include "res//shaders//_include_matrices.glsl"
#include "res//shaders//_include_vertLighting.glsl"
#include "res//shaders//_include_vertIn.glsl"
#include "res//shaders//_include_vertFog.glsl"

out vec2 vert_textureCoords;
out vec4 vert_normal;

uniform vec4 clipPlane;

void main(void) {
	vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
	vec4 positionRelativeToCamera =  viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCamera;
	vert_normal = transformationMatrix * vec4(normal, 0.0);	
	vert_textureCoords = textureCoords;
	
	doLighting(worldPosition);
	doFog(positionRelativeToCamera);
	
	gl_ClipDistance[0] = dot(worldPosition, clipPlane);
}