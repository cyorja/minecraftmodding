package com.example.examplemod.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Water elemental from ocean monuments. Fights like a blaze (charge up, then a volley of
 * bolts) and dodges like a breeze (surges sideways after each volley), but lives in water
 * and slowly dries out on land the way a dolphin does.
 */
public class BrineEntity extends Monster {
    private static final EntityDataAccessor<Boolean> DATA_CHARGED = SynchedEntityData.defineId(BrineEntity.class, EntityDataSerializers.BOOLEAN);
    private static final int MAX_MOISTNESS = 2400;

    // No more than MAX_NEARBY brines within NEARBY_RADIUS blocks
    private static final int MAX_NEARBY = 4;
    private static final double NEARBY_RADIUS = 24.0;

    private int moistness = MAX_MOISTNESS;

    public BrineEntity(EntityType<? extends BrineEntity> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.025F, 0.1F, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
        this.xpReward = 10;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.MOVEMENT_SPEED, 0.7)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    public static boolean checkBrineSpawnRules(EntityType<? extends BrineEntity> type, LevelAccessor level,
                                                    EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        // Brines only come from the spawner in the monument treasure room (see OceanMonumentCoreRoomMixin).
        // The cap keeps that spawner from flooding the room: a vanilla spawner releases up to 4 per cycle.
        return level.getDifficulty() != Difficulty.PEACEFUL
                && level.getFluidState(pos.below()).is(FluidTags.WATER)
                && level.getEntitiesOfClass(BrineEntity.class, new AABB(pos).inflate(NEARBY_RADIUS)).size() < MAX_NEARBY;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(4, new BrineAttackGoal(this));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0));
        this.goalSelector.addGoal(7, new RandomSwimmingGoal(this, 1.0, 40));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        // Ignore friendly fire from other brines so stray bolts don't start infighting
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, BrineEntity.class).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(DATA_CHARGED, false);
    }

    public boolean isCharged() {
        return this.entityData.get(DATA_CHARGED);
    }

    private void setCharged(boolean charged) {
        this.entityData.set(DATA_CHARGED, charged);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isInWater() ? SoundEvents.GUARDIAN_AMBIENT : SoundEvents.BREEZE_IDLE_AIR;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return this.isInWater() ? SoundEvents.GUARDIAN_HURT : SoundEvents.BREEZE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isInWater() ? SoundEvents.GUARDIAN_DEATH : SoundEvents.BREEZE_DEATH;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader level) {
        // The default check rejects any spot containing liquid
        return level.isUnobstructed(this);
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    @Override
    public void aiStep() {
        // Hover and sink slowly out of water, like a blaze
        if (!this.isInWater() && !this.onGround() && this.getDeltaMovement().y < 0.0) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.6, 1.0));
        }

        if (this.level().isClientSide()) {
            if (this.isInWater()) {
                int count = this.isCharged() ? 4 : 1;
                for (int i = 0; i < count; i++) {
                    this.level().addParticle(ParticleTypes.BUBBLE, this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), 0.0, 0.05, 0.0);
                }
            } else {
                this.level().addParticle(ParticleTypes.DRIPPING_WATER, this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), 0.0, 0.0, 0.0);
            }
        }

        super.aiStep();
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        if (this.isInWaterOrRain()) {
            this.moistness = MAX_MOISTNESS;
        } else if (--this.moistness <= 0 && this.tickCount % 20 == 0) {
            this.hurtServer(level, this.damageSources().dryOut(), 1.0F);
        }

        super.customServerAiStep(level);
    }

    @Override
    protected void travelInWater(Vec3 input, double baseGravity, boolean isFalling, double oldY) {
        this.moveRelative(this.getSpeed(), input);
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
        if (this.getTarget() == null) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.005, 0.0));
        }
    }

    /**
     * Blaze-style volley: charge up, fire three bolts in quick succession, then rest. After each
     * volley the brine surges sideways, the way a breeze hops around its target.
     */
    private static class BrineAttackGoal extends Goal {
        private static final double PREFERRED_RANGE = 10.0;

        private final BrineEntity mob;
        private int attackStep;
        private int attackTime;
        private int lastSeen;

        BrineAttackGoal(BrineEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.mob.getTarget();
            return target != null && target.isAlive() && this.mob.canAttack(target);
        }

        @Override
        public void start() {
            this.attackStep = 0;
        }

        @Override
        public void stop() {
            this.mob.setCharged(false);
            this.lastSeen = 0;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            this.attackTime--;
            LivingEntity target = this.mob.getTarget();
            if (target == null) return;

            boolean hasLineOfSight = this.mob.getSensing().hasLineOfSight(target);
            this.lastSeen = hasLineOfSight ? 0 : this.lastSeen + 1;
            double distance = this.mob.distanceToSqr(target);
            double followRange = this.mob.getAttributeValue(Attributes.FOLLOW_RANGE);

            if (distance < 4.0) {
                if (!hasLineOfSight) return;
                if (this.attackTime <= 0) {
                    this.attackTime = 20;
                    this.mob.doHurtTarget(getServerLevel(this.mob), target);
                }
                this.mob.getNavigation().moveTo(target, 1.0);
            } else if (distance < followRange * followRange && hasLineOfSight) {
                // Hold position at range instead of crowding the target
                if (distance > PREFERRED_RANGE * PREFERRED_RANGE) {
                    this.mob.getNavigation().moveTo(target, 1.0);
                } else {
                    this.mob.getNavigation().stop();
                }

                if (this.attackTime <= 0) {
                    this.attackStep++;
                    if (this.attackStep == 1) {
                        this.attackTime = 40;
                        this.mob.setCharged(true);
                        this.mob.playSound(SoundEvents.BREEZE_CHARGE, 1.0F, 0.6F);
                    } else if (this.attackStep <= 4) {
                        this.attackTime = 6;
                    } else {
                        this.attackTime = 80;
                        this.attackStep = 0;
                        this.mob.setCharged(false);
                        this.surge(target);
                    }

                    if (this.attackStep > 1) {
                        this.shoot(target, distance);
                    }
                }

                this.mob.getLookControl().setLookAt(target, 10.0F, 10.0F);
            } else if (this.lastSeen < 5) {
                this.mob.getNavigation().moveTo(target, 1.0);
            }
        }

        private void shoot(LivingEntity target, double distance) {
            double spread = Math.sqrt(Math.sqrt(distance)) * 0.25;
            double xd = target.getX() - this.mob.getX();
            double yd = target.getY(0.5) - this.mob.getY(0.5);
            double zd = target.getZ() - this.mob.getZ();
            Vec3 direction = new Vec3(
                    this.mob.getRandom().triangle(xd, 2.297 * spread), yd, this.mob.getRandom().triangle(zd, 2.297 * spread));

            WaterBoltEntity bolt = new WaterBoltEntity(this.mob.level(), this.mob, direction.normalize());
            bolt.setPos(bolt.getX(), this.mob.getY(0.5) + 0.3, bolt.getZ());
            this.mob.level().addFreshEntity(bolt);
            this.mob.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 0.8F + this.mob.getRandom().nextFloat() * 0.4F);
        }

        private void surge(LivingEntity target) {
            Vec3 toTarget = target.position().subtract(this.mob.position()).multiply(1.0, 0.0, 1.0).normalize();
            Vec3 sideways = new Vec3(-toTarget.z, 0.0, toTarget.x).scale(this.mob.getRandom().nextBoolean() ? 1.0 : -1.0);
            double lift = this.mob.isInWater() ? 0.1 : 0.3;
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(sideways.scale(0.5)).add(0.0, lift, 0.0));
            this.mob.needsSync = true;
            this.mob.playSound(SoundEvents.PLAYER_SPLASH, 0.6F, 1.4F);
        }
    }
}
