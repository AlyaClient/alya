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

// Signed distance function for rounded rectangle
float roundedRectSDF(vec2 centerPos, vec2 size, float radius) {
    return length(max(abs(centerPos) - size + radius, 0.0)) - radius;
}

// Soft shadow function
float softShadow(float dist, float blur) {
    return 1.0 - smoothstep(-blur, blur, dist);
}

void main() {
    vec2 uv = gl_FragCoord.xy;
    vec2 center = rectPos + rectSize * 0.5;
    vec2 pixelPos = uv - center;
    
    // Calculate distance to rounded rectangle
    float dist = roundedRectSDF(pixelPos, rectSize * 0.5, cornerRadius);
    
    // Shadow calculation (offset position)
    vec2 shadowPos = pixelPos - shadowOffset;
    float shadowDist = roundedRectSDF(shadowPos, rectSize * 0.5, cornerRadius);
    float shadow = softShadow(shadowDist, shadowBlur) * shadowColor.a;
    
    // Glow effect (outer glow)
    float glowDist = max(0.0, dist);
    float glow = exp(-glowDist * 0.5) * glowIntensity;
    
    // Create smooth edges using smoothstep for anti-aliasing
    float alpha = 1.0 - smoothstep(-1.0, 1.0, dist);
    
    // Border effect with smooth transition
    float borderDist = abs(dist);
    float borderAlpha = 1.0 - smoothstep(borderWidth - 0.5, borderWidth + 0.5, borderDist);
    
    // Inner highlight for glass effect (top edge)
    float highlightDist = roundedRectSDF(pixelPos + vec2(0.0, rectSize.y * 0.15), rectSize * 0.5 * vec2(0.95, 0.3), cornerRadius * 0.8);
    float highlight = (1.0 - smoothstep(-1.0, 2.0, highlightDist)) * 0.15;
    
    // Combine all effects
    vec4 finalColor = vec4(0.0);
    
    // Add shadow first (behind everything)
    finalColor = mix(finalColor, shadowColor, shadow * (1.0 - alpha));
    
    // Add glow
    finalColor = mix(finalColor, glowColor, glow * (1.0 - alpha) * glowColor.a);
    
    // Add fill color
    vec4 fill = fillColor;
    fill.rgb += highlight; // Add inner highlight
    finalColor = mix(finalColor, fill, alpha);
    
    // Add border on top
    finalColor = mix(finalColor, borderColor, borderAlpha * alpha * borderColor.a);
    
    // Set final alpha
    finalColor.a = max(max(shadow, glow * glowColor.a), alpha);
    
    gl_FragColor = finalColor;
}

