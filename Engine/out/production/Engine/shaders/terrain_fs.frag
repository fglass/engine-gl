#version 400 core

in vec2 pass_texture_coord;
in vec3 surfaceNormal;
in vec3 toLightVector;
in vec3 toCameraVector;
in float visibility;

out vec4 out_colour;

uniform sampler2D bgTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;

uniform vec3 lightColour;
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColour;

void main(void) {

    vec4 blendMapColour = texture(blendMap, pass_texture_coord);
    float backTextureAmount = 1 - (blendMapColour.r + blendMapColour.g + blendMapColour.b);
    vec2 tiledCoord = pass_texture_coord * 40.0;

    vec4 bgTextureColour = texture(bgTexture, tiledCoord) * backTextureAmount;
    vec4 rTextureColour = texture(rTexture, tiledCoord) * blendMapColour.r;
    vec4 gTextureColour = texture(gTexture, tiledCoord) * blendMapColour.g;
    vec4 bTextureColour = texture(bTexture, tiledCoord) * blendMapColour.b;

    vec4 finalColour = bgTextureColour + rTextureColour + gTextureColour + bTextureColour;

    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitLightVector = normalize(toLightVector);

    float product = dot(unitNormal, unitLightVector);
    float brightness = max(product, 0.2);
    vec3 diffuse = brightness * lightColour;

    vec3 unitCameraVector = normalize(toCameraVector);
    vec3 lightDirection = -unitLightVector;
    vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);

    float specularFactor = dot(reflectedLightDirection, unitCameraVector);
    specularFactor = max(specularFactor, 0.0);
    float dampedFactor = pow(specularFactor, shineDamper);
    vec3 specular = dampedFactor * reflectivity * lightColour;

    out_colour = vec4(diffuse, 1.0) * finalColour + vec4(specular, 1.0);
    out_colour = mix(vec4(skyColour, 1.0), out_colour, visibility);
}