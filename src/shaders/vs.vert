#version 400 core

in vec3 position;
in vec2 textureCoord;
in vec3 normal;

out vec2 pass_texture_coord;
out vec3 surfaceNormal;
out vec3 toLightVector;
out vec3 toCameraVector;
out float visibility;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition;
uniform float useFakeLighting;
uniform int rows;
uniform vec2 offset;

const float density = 0.007;
const float gradient = 1.5;

void main(void) {
    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
    vec4 posRelativeToCam = viewMatrix * worldPosition;

    gl_Position = projectionMatrix * posRelativeToCam;
    pass_texture_coord = (textureCoord / rows) + offset;

    vec3 actual = normal;
    if (useFakeLighting > 0.5) {
        actual = vec3(0.0, 1.0, 0.0); // Point directly up
    }

    surfaceNormal = (transformationMatrix * vec4(actual, 0.0)).xyz;
    toLightVector = lightPosition - worldPosition.xyz;
    toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;

    float distance = length(posRelativeToCam.xyz);
    visibility = exp(-pow(distance * density, gradient));
    visibility = clamp(visibility, 0.0, 1.0);
}