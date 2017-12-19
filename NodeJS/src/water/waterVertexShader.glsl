#version 400

in vec3 position;
in vec3 normal;

out vec4 vert_screenPosition;
out vec4 vert_worldPosition;
out vec4 vert_normal;
out vec3 vert_toLightVector[16];
out vec3 vert_toCameraVector;
out float vert_visibility;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[16];
uniform float fogDensity;
uniform float fogGradient;

void main(void) {
	vert_worldPosition = transformationMatrix * vec4(position, 1.0);
	vec4 positionRelativeToCamera =  viewMatrix * vert_worldPosition;
	vert_screenPosition = projectionMatrix * positionRelativeToCamera;
	gl_Position = vert_screenPosition;
	vert_normal = transformationMatrix * vec4(normal, 0.0);	
	
	for(int i = 0; i < 16; i++) {
		vert_toLightVector[i] = lightPosition[i] - vert_worldPosition.xyz;
	}
	
	vert_toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - vert_worldPosition.xyz;
	
	float distance = length(positionRelativeToCamera.xyz);
	vert_visibility = exp(-pow((distance*fogDensity), fogGradient));
	vert_visibility = clamp(vert_visibility, 0, 1);	
}