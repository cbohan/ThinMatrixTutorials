#version 400

in vec2 vert_textureCoords;
in vec4 vert_normal;
in vec3 vert_toLightVector;
in vec3 vert_toCameraVector;
in float vert_visibility;

out vec4 out_Color;

uniform sampler2D textureSampler;
uniform vec3 lightPosition;
uniform vec3 lightColor;
uniform float shineDamping;
uniform float reflectivity;
uniform vec3 skyColor;

void main(void) {
	vec4 albedo = texture(textureSampler, vert_textureCoords);
	
	if (albedo.a < .5){
		discard;
	}
	
	vec3 unitNormal = normalize(vert_normal.xyz);
	vec3 unitLightVector = normalize(vert_toLightVector);
	
	float nDotL = dot(unitNormal, unitLightVector);
	vec4 color = max(nDotL, 0.1) * vec4(lightColor, 1) * albedo;
	
	vec3 unitVectorToCamera = normalize(vert_toCameraVector);
	vec3 lightDirection = -unitLightVector;
	vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
	float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
	specularFactor = max(specularFactor, 0);
	float dampedFactor = pow(specularFactor, shineDamping);
	vec3 finalSpecular = reflectivity * dampedFactor * lightColor;
	color = color + vec4(finalSpecular, 1.0);
	
	out_Color = color;
	out_Color = mix(vec4(skyColor, 1.0), out_Color, vert_visibility);
}