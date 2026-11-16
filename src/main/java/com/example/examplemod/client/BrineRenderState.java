package com.example.examplemod.client;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class BrineRenderState extends LivingEntityRenderState {
    // Smoothed horizontal speed relative to the body, in blocks/tick (positive = forward / to its right)
    public float forwardSpeed;
    public float rightSpeed;
}
