#version 400 core

in vec2 pass_texture_coord;
in vec3 surfaceNormal;
in vec3 toLightVector;
in vec3 toCameraVector;
in float visibility;

out vec4 out_colour;

uniform sampler2D textureSampler;
uniform vec3 lightColour;
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColour;

void main(void) {
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

    vec4 textureColour = texture(textureSampler, pass_texture_coord);
    if (textureColour.a < 0.5) {
        discard;
    }

    out_colour = vec4(diffuse, 1.0) * textureColour + vec4(specular, 1.0);
    out_colour = mix(vec4(skyColour, 1.0), out_colour, visibility);
}