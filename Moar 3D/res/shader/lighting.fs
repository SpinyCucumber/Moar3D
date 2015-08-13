uniform vec3 lightPosition; //In world
uniform vec3 color;
uniform sampler2D texture;

varying vec3 worldNormal;
varying vec4 worldVertex;
varying vec2 texCoord;

void main() {
	
	vec3 worldPosition = worldVertex.xyz;
	vec3 camPosition = (gl_ModelViewMatrix * worldVertex).xyz; //Eye-space
    vec3 camNormal = gl_NormalMatrix * worldNormal; //Eye-space
    
	vec3 lightDir = normalize(worldPosition - lightPosition); //In world
	float diffuse = max(0.0, dot(lightDir, worldNormal));
	float light = pow(diffuse, 1.0);
	
    gl_FragColor = vec4(light * color, 1.0);
    
}