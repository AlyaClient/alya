#version 120

uniform sampler2D texture;
uniform vec2 resolution;
uniform vec2 direction;
uniform float radius;

void main() {
    vec2 uv = gl_FragCoord.xy / resolution;
    vec2 texelSize = 1.0 / resolution;

    float w0 = 0.227027;
    float w1 = 0.1945946;
    float w2 = 0.1216216;
    float w3 = 0.054054;
    float w4 = 0.016216;

    vec4 result = texture2D(texture, uv) * w0;

    float blurRadius = max(radius, 1.0);

    vec2 offset1 = direction * texelSize * 1.0 * blurRadius;
    vec2 offset2 = direction * texelSize * 2.0 * blurRadius;
    vec2 offset3 = direction * texelSize * 3.0 * blurRadius;
    vec2 offset4 = direction * texelSize * 4.0 * blurRadius;

    result += texture2D(texture, uv + offset1) * w1;
    result += texture2D(texture, uv - offset1) * w1;
    result += texture2D(texture, uv + offset2) * w2;
    result += texture2D(texture, uv - offset2) * w2;
    result += texture2D(texture, uv + offset3) * w3;
    result += texture2D(texture, uv - offset3) * w3;
    result += texture2D(texture, uv + offset4) * w4;
    result += texture2D(texture, uv - offset4) * w4;

    gl_FragColor = result;
}
