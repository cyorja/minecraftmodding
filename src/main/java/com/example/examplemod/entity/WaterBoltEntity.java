package com.example.examplemod.entity;

import com.example.examplemod.ModEntities;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Brine projectile. Keeps its speed underwater (but fizzles in open air), and on hit it
 * knocks the target back, steals some of their air and slows them down.
 */
public class WaterBoltEntity extends AbstractHurtingProjectile implements ItemSupplier {
    private static final ItemStack DISPLAY_ITEM = new ItemStack(Items.HEART_OF_THE_SEA);
    private static final float DAMAGE = 4.0F;
    private static final int AIR_STOLEN = 90;

    public WaterBoltEntity(EntityType<? extends WaterBoltEntity> type, Level level) {
        super(type, level);
    }

    public WaterBoltEntity(Level level, LivingEntity owner, Vec3 direction) {
        super(ModEntities.WATER_BOLT.get(), owner, direction, level);
    }

    @Override
    public ItemStack getItem() {
        return DISPLAY_ITEM;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    protected @Nullable ParticleOptions getTrailParticle() {
        return this.isInWater() ? null : ParticleTypes.FALLING_WATER;
    }

    @Override
    protected float getInertia() {
        return 0.9F;
    }

    @Override
    protected float getLiquidInertia() {
        return 0.95F;
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        Entity target = hitResult.getEntity();
        LivingEntity owner = this.getOwner() instanceof LivingEntity living ? living : null;
        DamageSource damageSource = this.damageSources().mobProjectile(this, owner);
        if (!target.hurtServer(serverLevel, damageSource, DAMAGE)) return;

        EnchantmentHelper.doPostAttackEffects(serverLevel, target, damageSource);
        if (target instanceof LivingEntity living) {
            Vec3 push = this.getDeltaMovement();
            living.knockback(0.6, -push.x, -push.z);
            living.setAirSupply(Math.max(0, living.getAirSupply() - AIR_STOLEN));
            living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 1), owner);
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SPLASH, this.getX(), this.getY(), this.getZ(), 20, 0.25, 0.25, 0.25, 0.1);
            serverLevel.sendParticles(ParticleTypes.BUBBLE_POP, this.getX(), this.getY(), this.getZ(), 8, 0.2, 0.2, 0.2, 0.05);
            this.playSound(SoundEvents.GENERIC_SPLASH, 0.8F, 1.2F);
            this.discard();
        }
    }
}
