#version 400

in vec2 vert_textureCoords;
in vec4 vert_normal;
in vec3 vert_toLightVector[16];
in vec3 vert_toCameraVector;
in float vert_visibility;

out vec4 out_Color;

uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D splatMap;

uniform vec3 lightColor[16];
uniform float lightAttenuationFactor[16];
uniform float shineDamping;
uniform float reflectivity;
uniform vec3 skyColor;

void main(void) {
	vec4 splatMapColor = texture(splatMap, vert_textureCoords);
	float backTextureAmount = 1 - (splatMapColor.r + splatMapColor.g + splatMapColor.b);
	vec2 tiledCoords = vert_textureCoords * 40.0;
	vec4 backgroundTextureColor = texture(backgroundTexture, tiledCoords) * backTextureAmount;
	vec4 rTextureColor = texture(rTexture, tiledCoords) * splatMapColor.r;
	vec4 gTextureColor = texture(gTexture, tiledCoords) * splatMapColor.g;
	vec4 bTextureColor = texture(bTexture, tiledCoords) * splatMapColor.b;

	vec4 albedo = backgroundTextureColor + rTextureColor + gTextureColor + bTextureColor;
	
	vec3 unitNormal = normalize(vert_normal.xyz);
	vec3 unitVectorToCamera = normalize(vert_toCameraVector);
	
	vec4 color = vec4(0, 0, 0, 0);
	for(int i = 0; i < 16; i++) {
		float distance = length(vert_toLightVector[i]);
		float attenuation = 1.0 / (1.0 + (distance * distance * lightAttenuationFactor[i]));
	
		vec3 unitLightVector = normalize(vert_toLightVector[i]);
		float nDotL = dot(unitNormal, unitLightVector);
		color = color + max(nDotL, 0.0) * vec4(lightColor[i], 1) * albedo * attenuation;
		
		vec3 lightDirection = -unitLightVector;
		vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
		float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
		specularFactor = max(specularFactor, 0);
		float dampedFactor = pow(specularFactor, shineDamping);
		vec3 finalSpecular = reflectivity * dampedFactor * lightColor[i];
		color = color + vec4(finalSpecular, 1.0) * attenuation;
	}
	
	color.w = 1;
	out_Color = mix(vec4(skyColor, 1.0), color, vert_visibility);
	//out_Color = vec4(unitNormal, 1);
}