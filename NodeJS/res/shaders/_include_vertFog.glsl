//vert_fog
uniform float fogDensity;
uniform float fogGradient;
out float vert_visibility;

void doFog(vec4 positionRelativeToCamera) {
	float distance = length(positionRelativeToCamera.xyz);
	vert_visibility = exp(-pow((distance*fogDensity), fogGradient));
	vert_visibility = clamp(vert_visibility, 0, 1);
}