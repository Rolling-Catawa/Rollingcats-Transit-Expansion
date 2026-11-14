package cn.jstxjf_.rte.render.train;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;
import org.mtr.mapping.holder.ClientWorld;
import org.mtr.mapping.holder.SpriteProvider;
import org.mtr.mapping.mapper.ParticleFactoryExtension;

public class SteamSmokeParticle extends TextureSheetParticle {

    SteamSmokeParticle(ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
        super(clientLevel, d, e, f);
        this.scale(3.0f);
        this.setSize(0.25f, 0.25f);
        this.lifetime = this.random.nextInt(10) + 30;
        this.gravity = 3.0E-6f;
        this.hasPhysics = true;
        this.xd = g;
        this.yd = h + (double)(this.random.nextFloat() / 500.0f);
        this.zd = i;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime || this.alpha <= 0.0f) {
            this.remove();
            return;
        }
        this.xd += (double)(this.random.nextFloat() / 5000.0f * (float)(this.random.nextBoolean() ? 1 : -1));
        this.zd += (double)(this.random.nextFloat() / 5000.0f * (float)(this.random.nextBoolean() ? 1 : -1));
        this.yd -= (double)this.gravity;
        this.move(this.xd, this.yd, this.zd);

        if (this.age < this.lifetime * 0.4) {
            this.quadSize += 0.2;
        } else {
            this.quadSize += 0.1;
        }
        if (this.y == this.yo) {
            this.remove();
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static ParticleFactoryExtension createFactory(SpriteProvider spriteProvider) {
        return new ParticleFactoryExtension((ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) -> {
            final SteamSmokeParticle particle = new SteamSmokeParticle((ClientLevel) clientWorld.data, d, e, f, g, h, i);
            particle.setAlpha(1F);
            particle.pickSprite((SpriteSet) spriteProvider.data);
            return new org.mtr.mapping.holder.Particle(particle);
        }, spriteProvider) {};
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType particleOptions, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            SteamSmokeParticle particle = new SteamSmokeParticle(clientLevel, d, e, f, g, h, i);
            particle.setAlpha(1f);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}
