#version 400

in vec4 vert_worldPosition;
in vec4 vert_screenPosition;
in vec4 vert_normal;
in vec3 vert_toLightVector[16];
in vec3 vert_toCameraVector;
in float vert_visibility;

out vec4 out_Color;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D waterDUDVMap;
uniform sampler2D waterNormalMap;
uniform sampler2D waterDepthMap;
uniform float moveFactor;
uniform vec3 lightColor[16];
uniform float lightAttenuationFactor[16];
uniform vec3 skyColor;
uniform float nearPlane;
uniform float farPlane;

const float waveStrength = 0.01;

void main(void) {
	vec2 ndc = (vert_screenPosition.xy/vert_screenPosition.w)/2.0 + 0.5;
	vec2 reflectCoords = vec2(ndc.x, -ndc.y);
	vec2 refractCoords = ndc;
	
	float waterDepthTex = texture(waterDepthMap, refractCoords).r;
	float floorDistance = 2.0 * nearPlane * farPlane / (farPlane + nearPlane - (2.0 * waterDepthTex - 1.0) * (farPlane - nearPlane));
	float distanceToWater = 2.0 * nearPlane * farPlane / (farPlane + nearPlane - (2.0 * gl_FragCoord.z - 1.0) * (farPlane - nearPlane));
	float waterDepth = floorDistance - distanceToWater;
	
	vec2 textureCoords = vert_worldPosition.xz * .025;
	vec2 distoredTexCoords = texture(waterDUDVMap, vec2(textureCoords.x + moveFactor, textureCoords.y)).rg * 0.1;
	distoredTexCoords = textureCoords + vec2(distoredTexCoords.x, distoredTexCoords.y + moveFactor);
	vec2 totalDistortion = (texture(waterDUDVMap, distoredTexCoords).rg * 2.0 - 0.5) * waveStrength;
	totalDistortion *= clamp(waterDepth / 20.0, 0.0, 1.0);
	vec3 normal = normalize(texture(waterNormalMap, distoredTexCoords).rbg * 2.0 - 1.0);
	
	reflectCoords += totalDistortion;
	refractCoords += totalDistortion;
	reflectCoords.x = clamp(reflectCoords.x, .001, .999);
	reflectCoords.y = clamp(reflectCoords.y, -.999, -.001);
	refractCoords = clamp(refractCoords, .001, .999);
	
	vec4 reflectColor = texture(reflectionTexture, reflectCoords);
	vec4 refractColor = texture(refractionTexture, refractCoords);
	refractColor = mix(refractColor, vec4(0.0, 0.3, 0.5, 1.0), clamp(waterDepth / 100.0, 0, 1));
	
	vec3 viewVector = normalize(vert_toCameraVector);
	float refractiveFactor = dot(viewVector, vec3(0.0, 1.0, 0.0));
	refractiveFactor = pow(refractiveFactor, .3);
	
	vec3 totalSpecular = vec3(0, 0, 0);
	vec3 unitVectorToCamera = normalize(vert_toCameraVector);
	for(int i = 0; i < 16; i++) {
		float distance = length(vert_toLightVector[i]);
		float attenuation = 1.0 / (1.0 + (distance * distance * lightAttenuationFactor[i]));
		vec3 unitLightVector = normalize(vert_toLightVector[i]);
		vec3 lightDirection = -unitLightVector;
		vec3 reflectedLightDirection = reflect(lightDirection, normal);
		float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
		specularFactor = max(specularFactor, 0);
		float dampedFactor = pow(specularFactor, 8);
		totalSpecular += .5 * dampedFactor * lightColor[i] * attenuation;
	}
	

	out_Color = mix(reflectColor + vec4(totalSpecular, 0), refractColor, refractiveFactor);
	out_Color = mix(out_Color, vec4(0.0, 0.3, 0.5, 1.0), .1);
	out_Color.a = clamp(waterDepth / 5.0, 0.0, 1.0);
}