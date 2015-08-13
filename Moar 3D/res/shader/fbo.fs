const vec2 vec = vec2(0.001);
const mat3 kernel = mat3(1.0, 2.0, 1.0,
						2.0, 4.0, 2.0,
						1.0, 2.0, 1.0) / 16.0;

uniform sampler2D texture, depth;

varying vec4 vertex;
varying vec2 texCoord;

void main() {
    vec3 color = vec3(0.0);
    for(int x = 0; x < 3; x++) {
    	for(int y = 0; y < 3; y++) {
    		vec2 modTexCoord = texCoord + vec * (vec2(x, y) - 1.0);
    		color += kernel[x][y] * texture2D(texture, modTexCoord).rgb;
    	}
    }
    gl_FragColor = vec4(color, 1.0); 
}