#ifdef GL_ES
precision highp float;
#endif

uniform float time;
uniform vec2 resolution;
uniform vec2 rectPos;
uniform vec2 rectSize;
uniform float cornerRadius;
uniform vec4 fillColor;
uniform vec4 borderColor;
uniform float borderWidth;
uniform float shadowBlur;
uniform vec2 shadowOffset;
uniform vec4 shadowColor;
uniform float glowIntensity;
uniform vec4 glowColor;

float roundedRectSDF(vec2 centerPos, vec2 size, float radius) {
    return length(max(abs(centerPos) - size + radius, 0.0)) - radius;
}

float softShadow(float dist, float blur) {
    return 1.0 - smoothstep(-blur, blur, dist);
}

void main() {
    vec2 uv = gl_FragCoord.xy;
    vec2 center = rectPos + rectSize * 0.5;
    vec2 pixelPos = uv - center;
    float dist = roundedRectSDF(pixelPos, rectSize * 0.5, cornerRadius);
    vec2 shadowPos = pixelPos - shadowOffset;
    float shadowDist = roundedRectSDF(shadowPos, rectSize * 0.5, cornerRadius);
    float shadow = softShadow(shadowDist, shadowBlur) * shadowColor.a;
    float glowDist = max(0.0, dist);
    float glow = exp(-glowDist * 0.5) * glowIntensity;
    float alpha = 1.0 - smoothstep(-1.0, 1.0, dist);
    float borderDist = abs(dist);
    float borderAlpha = 1.0 - smoothstep(borderWidth - 0.5, borderWidth + 0.5, borderDist);
    float highlzghtDist = roundedRectSDF(pixelPos + vec2(0.0, rectSize.y * 0.15), rectSize * 0.5 * vec2(0.95, 0.3), cornerRadius * 0.8);
    float highlight = (1.0 - smoothstep(-1.0, 2.0, highlightDist)) * 0.15;
    vec4 finalColor = vec4(0.0);

    finalColor = mix(finalColor, shadowColor, shadow * (1.0 - alpha));
    finalColor = mix(finalColor, glowColor, glow * (1.0 - alpha) * glowColor.a);

    vec4 fill = fillColor;
    fill.rgb += highlight;

    finalColor = mix(finalColor, fill, alpha);
    finalColor = mix(finalColor, borderColor, borderAlpha * alpha * borderColor.a);
    finalColor.a = max(max(shadow, glow * glowColor.a), alpha);
    
    gl_FragColor = finalColor;
}
