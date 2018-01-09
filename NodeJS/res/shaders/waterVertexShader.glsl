#version 400

#include "res//shaders//_include_matrices.glsl"
#include "res//shaders//_include_vertLighting.glsl"
#include "res//shaders//_include_vertIn.glsl"
#include "res//shaders//_include_vertFog.glsl"

out vec4 vert_screenPosition;
out vec4 vert_worldPosition;
out vec4 vert_normal;

void main(void) {
	vert_worldPosition = transformationMatrix * vec4(position, 1.0);
	vec4 positionRelativeToCamera =  viewMatrix * vert_worldPosition;
	vert_screenPosition = projectionMatrix * positionRelativeToCamera;
	gl_Position = vert_screenPosition;
	vert_normal = transformationMatrix * vec4(normal, 0.0);	
	
	doLighting(vert_worldPosition);
	doFog(positionRelativeToCamera);
}