#version 120
uniform float time;
uniform vec2 resolution;

#define STAR 1.0
#define MOUSE_INFLUENCE 0.2
#define MOUSE_GLOW 0.05
#define TRAIL_STRENGTH 0.8
#define FLARE 4.0
#define COLOR vec3(0.92, 0.61, 0.96)
#define STAR_NUM 12.0
#define STAR_AMP 1.0
#define STAR_SPEED 0.01
#define STAR_VEL vec2(0.0, 1.0)
#define STAR_FREQ 8.0
#define STAR_EXP 1.5
#define DITHER 0.001
#define DITHER_RES 128.0
#define STAR_STRETCH 1.2
#define STAR_CURVE 0.5

vec3 gamma_encode(vec3 lrgb) { return sqrt(lrgb); }

vec2 turbulence(vec2 p, float freq, float num) {
    mat2 rot = mat2(0.6, -0.8, 0.8, 0.6);
    vec2 turb = vec2(0.0);
    for (float i = 0.0; i < STAR_NUM; i++) {
        if (i >= num) break;
        vec2 pos = p + turb + STAR_SPEED * i * time * STAR_VEL;
        float phase = freq * (pos * rot).y + STAR_SPEED * time * freq;
        turb += rot[0] * sin(phase) / freq;
        rot *= mat2(0.6, -0.8, 0.8, 0.6);
        freq *= STAR_EXP;
    }
    return turb;
}

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

vec3 star(inout vec2 p) {
    vec2 suv = p * 2.0 - 1.0;
    vec2 top = suv - vec2(0.0, 1.0);
    top.y *= STAR_STRETCH * resolution.y / resolution.x;
    float factor = 1.0 + 0.4 * sin(9.0 * suv.x) * sin(5.0 * (suv.y + 5.0 * time * STAR_SPEED));
    vec2 turb = top + factor * STAR_AMP * turbulence(top, STAR_FREQ, STAR_NUM);
    turb.y -= STAR_CURVE * suv.x * suv.x;
    float fade = max(4.0 * suv.x * suv.x - suv.y + 1.2, 0.001);
    float atten = fade * max(0.5 * turb.y, -turb.y);
    float ft = 0.4 * time;
    vec2 fp = 8.0 * (turb + 0.5 * STAR_VEL * ft);
    fp *= mat2(0.4, -0.3, 0.3, 0.4);
    float f = cos(fp.x) * sin(fp.y) - 0.5;
    float flare = f * f + 0.5 * suv.x * suv.x - 1.5 * turb.y + 0.6 * cos(0.42 * ft + 1.6 * turb.x) * cos(0.31 * ft - turb.x);
    vec3 col = 0.1 * COLOR * (STAR / (atten * atten) + FLARE / (flare * flare));
    const vec3 chrom = vec3(0.0, 0.1, 0.2);
    col *= exp(p.y *
               cos(turb.x * 5.0 + 0.4 * (time + turb.y * 1.0) + chrom) *
               cos(turb.x * 7.0 - 0.5 * (time - turb.y * 1.5) + chrom) *
               cos(turb.x * 9.0 + 0.6 * (time + turb.y * 2.0) + chrom)
    );
    return col;
}

void main() {
    vec2 vUv = gl_FragCoord.xy / resolution;
    vec2 duv = 0.9 * gl_FragCoord.xy / DITHER_RES * mat2(0.8, -0.6, 0.6, 0.8);
    float dither = hash(duv + time * 0.1) - 0.5;
    vec2 suv = vUv * 2.0 - 1.0;
    vec2 starUv = vUv;
    vec3 col = star(starUv);
    float vig = 1.0 - abs(suv.x);
    vig *= 0.5 + 0.5 * suv.y;
    col *= vig * vig;
    col /= 1.0 + col;
    col = clamp(col, 0.0, 1.0);
    col = gamma_encode(col);
    float xx = suv.x + 0.03;
    xx = max(1.0 - 1e1 * xx * xx / max(0.5 + 1.5 * starUv.y, 0.1), 0.0);
    float light = max(0.5 + 0.5 * starUv.y, 0.0) * xx;
    vec3 hue = mix(vec3(0.5, 0.2, 0.2), vec3(0.3, 0.3, 0.6), 1.0 + suv.y);
    vec3 rim = 12.0 * light * light * light * light * (0.5 + 0.5 * suv.y) * hue;
    rim /= (1.0 + rim);
    col += (1.0 - col) * rim * rim;
    col += DITHER * dither;
    gl_FragColor = vec4(col, 1.0);
}
