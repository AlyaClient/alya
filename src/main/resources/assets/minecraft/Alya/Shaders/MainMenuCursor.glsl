#version 120

uniform float time;
uniform vec2 resolution;
uniform float deltaTime;
uniform float logoScale;
uniform vec2 mouse;
uniform vec2 mouseVelocity;

#define TRAIL_FALLOFF 9000.0
#define FADE_EXP vec4(0.02, 0.02, 0.1, 0.1)
#define SCROLL_SPEED 0.0005
#define DISTORT_SPEED 0.02
#define LOGO_TWIRL 0.4
#define LOGO_PULL 0.1
#define LOGO_SCALE 0.5
#define LOGO_RATIO 2.08
#define TURB_NUM 8.0
#define TURB_AMP 0.6
#define TURB_SPEED 0.5
#define TURB_VEL vec2(0.1, 0.0)
#define TURB_FREQ 50.0
#define TURB_EXP 1.3

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

vec2 turbulence(vec2 p) {
    mat2 rot = mat2(0.6, -0.8, 0.8, 0.6);
    vec2 turb = vec2(0.0);
    float freq = TURB_FREQ;
    for (float i = 0.0; i < TURB_NUM; i++) {
        vec2 pos = p + TURB_SPEED * i * time * TURB_VEL;
        float phase = freq * (pos * rot).y + TURB_SPEED * time * freq * 0.1;
        turb += rot[0] * sin(phase) / freq;
        rot *= mat2(0.6, -0.8, 0.8, 0.6);
        freq *= TURB_EXP;
    }
    return turb;
}

void main() {
    vec2 vUv = gl_FragCoord.xy / resolution;
    vec2 ratio = min(resolution.yx / resolution.xy, 1.0);

    float dt = deltaTime > 0.0 ? deltaTime : 0.016;
    vec2 uMouse = mouse;
    vec2 uMouseVelocity = mouseVelocity;
    float uLogoScale = logoScale > 0.0 ? logoScale : 0.5;

    vec2 scale = max(uLogoScale, 1.0 - (LOGO_RATIO / 4.0)) * ratio * vec2(LOGO_RATIO, -1.0);
    vec2 logoUV = 0.5 + (vUv - 0.5) / scale;

    vec4 logo = vec4(0.0);
    if (logoUV.x >= 0.0 && logoUV.x <= 1.0 && logoUV.y >= 0.0 && logoUV.y <= 1.0) {
        logo = vec4(logoUV.x * 0.6, logoUV.y * 0.6, 0.0, 0.0);
    }

    float delta = 144.0 * dt;
    vec2 scroll = SCROLL_SPEED * vec2(vUv.x - 0.5, -1.0) * ratio;
    vec2 turb = turbulence((vUv + scroll) / ratio);
    vec2 distort = DISTORT_SPEED * turb;
    distort -= LOGO_TWIRL * (logo.rg - 0.6) * mat2(0.0, -1.0, 1.0, 0.0) * (logo.g - 0.5) * logo.b;
    distort -= LOGO_PULL * (logo.rg - 0.6) * logo.b * logo.b;

    vec2 distortedUv = vUv + delta * scroll + delta * distort * ratio;
    vec4 prev = vec4(0.5, 0.5, 0.0, 0.0);

    vec2 trailA = vUv + 0.01 * delta * turb * ratio - uMouse;
    vec2 trailB = -uMouseVelocity;
    float trailD = dot(trailB, trailB);
    vec2 trailDif = trailA / ratio;
    float falloff = 0.0;

    if (trailD > 0.0) {
        float f = clamp(dot(trailA, trailB) / trailD, 0.0, 1.0);
        trailDif -= f * trailB / ratio;
        falloff = (1.0 - logo.b) / (1.0 + TRAIL_FALLOFF * dot(trailDif, trailDif));
        falloff *= min(trailD / (0.001 + trailD), 1.0);
    }

    vec2 suv = (uMouse - uMouseVelocity) * 2.0 - 1.0;
    float vig = 1.0 - abs(suv.y);
    vig *= 0.5 + 0.5 * suv.x;

    vec2 nuv = gl_FragCoord.xy / 64.0 + time * vec2(7.1, 9.1);
    float noise = hash(nuv);

    vec4 fade = pow(vec4(noise), FADE_EXP);
    fade = exp(-2.0 * fade * dt);
    vec4 decay = mix(vec4(0.5, 0.5, 0.0, 0.0), prev, fade);

    vec4 col = decay;
    vec2 vel = (-trailB) / (0.01 + length(trailB));
    col.rg -= (0.5 - abs(decay.rg - 0.5)) * (falloff * vel);
    col.ba += falloff * (1.0 - decay.ba) * vec2(1.0, vig * vig);
    col += (noise - 0.5) / 255.0;

    gl_FragColor = col;
}
