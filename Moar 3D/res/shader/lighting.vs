uniform float time;

varying vec3 worldNormal;
varying vec4 worldVertex;
varying vec2 texCoord;

void main() {
    worldNormal = gl_Normal;
    worldVertex = gl_Vertex;
    gl_Position = gl_ModelViewProjectionMatrix * worldVertex;
    texCoord = gl_MultiTexCoord0.xy;
}