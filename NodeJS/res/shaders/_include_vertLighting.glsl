//vert_lighting
out vec3 vert_toLightVector[16];
out vec3 vert_toCameraVector;
uniform vec3 lightPosition[16];
uniform float overrideNormals;

vec4 doOverrideNormals(vec4 actualNormals) {
	if (overrideNormals > .5) {
		return vec4(0, 1, 0, 0);
	} else {
		return actualNormals;
	}
}

void doLighting(vec4 worldPosition) {
	for(int i = 0; i < 16; i++) {
		vert_toLightVector[i] = lightPosition[i] - worldPosition.xyz;
	}
	
	vert_toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;
}