package net.illuc.kontraption.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import javax.annotation.Nonnull;
import mekanism.common.registries.MekanismParticleTypes;
import mekanism.common.util.MekanismUtils;
import net.illuc.kontraption.KontraptionParticleTypes;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

public record ThrusterParticleData(Double posX, Double posY, Double posZ, Double scale) implements ParticleOptions {

    public static final ParticleOptions.Deserializer<ThrusterParticleData> DESERIALIZER = new ParticleOptions.Deserializer<ThrusterParticleData>() {
        @Nonnull
        @Override
        public ThrusterParticleData fromCommand(@Nonnull ParticleType<ThrusterParticleData> type, @Nonnull StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            Double x = reader.readDouble();
            reader.expect(' ');
            Double y = reader.readDouble();
            reader.expect(' ');
            Double z = reader.readDouble();
            reader.expect(' ');
            Double sc = reader.readDouble();
            return new ThrusterParticleData(x, y, z, sc);

        }

        @Nonnull
        @Override
        public ThrusterParticleData fromNetwork(@Nonnull ParticleType<ThrusterParticleData> type, FriendlyByteBuf buf) {
            return new ThrusterParticleData(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble());
        }
    };

    public static final Codec<ThrusterParticleData> CODEC = RecordCodecBuilder.create(val -> val.group(
            Codec.DOUBLE.fieldOf("x").forGetter(data -> data.posX),
            Codec.DOUBLE.fieldOf("y").forGetter(data -> data.posX),
            Codec.DOUBLE.fieldOf("z").forGetter(data -> data.posX),
            Codec.DOUBLE.fieldOf("scale").forGetter(data -> data.posX)
    ).apply(val, ThrusterParticleData::new));





    @Nonnull
    @Override
    public ParticleType<?> getType() {
        return KontraptionParticleTypes.INSTANCE.getTHRUSTER().get();
        //return KontraptionParticleTypes.THRUSTER.get();
    }

    @Override
    public void writeToNetwork(@Nonnull FriendlyByteBuf buffer) {
        buffer.writeDouble(posX);
        buffer.writeDouble(posY);
        buffer.writeDouble(posZ);
        buffer.writeDouble(scale);
    }




    @Nonnull
    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", getType().getRegistryName(), posX, posY, posZ, scale);
    }



}