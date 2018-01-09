#version 400

#include "res//shaders//_include_matrices.glsl"
#include "res//shaders//_include_vertLighting.glsl"
#include "res//shaders//_include_vertIn.glsl"
#include "res//shaders//_include_vertFog.glsl"

const int MAX_JOINTS = 50;
const int MAX_WEIGHTS = 3;

in ivec3 jointIndices;
in vec3 weights;

out vec2 vert_textureCoords;
out vec4 vert_normal;

uniform float textureNumberOfRows;
uniform vec2 textureOffset;
uniform vec4 clipPlane;
uniform mat4 jointTransforms[MAX_JOINTS];

void main(void) {
	vec4 totalLocalPos = vec4(0.0);
	vec4 totalNormal = vec4(0.0);
	
	for (int i = 0; i < MAX_WEIGHTS; i++) {
		mat4 jointTransform = jointTransforms[jointIndices[i]];
		vec4 posePosition = jointTransform * vec4(position, 1.0);
		totalLocalPos += posePosition * weights[i];
		
		vec4 poseNormal = jointTransform * vec4(normal, 0.0);
		totalNormal += poseNormal * weights[i];
	}

	vec4 worldPosition = transformationMatrix * totalLocalPos;
	vec4 positionRelativeToCamera =  viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCamera;
	vert_normal = transformationMatrix * totalNormal;
	vert_textureCoords = (textureCoords / textureNumberOfRows) + textureOffset;
	
	doLighting(worldPosition);
	doFog(positionRelativeToCamera);
	
	gl_ClipDistance[0] = dot(worldPosition, clipPlane);
}
