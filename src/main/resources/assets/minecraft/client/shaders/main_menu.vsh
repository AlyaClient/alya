#version 120

attribute vec2 aPosition;
varying vec2 vUv;

void main() {
    vUv = 0.5 * aPosition + 0.5;
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    gl_TexCoord[0] = gl_MultiTexCoord0;
}
