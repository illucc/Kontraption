package net.illuc.kontraption.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.illuc.kontraption.KontraptionParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nonnull;
import java.util.Locale;

public record BulletParticleData (Double posX, Double posY, Double posZ, Double scale) implements ParticleOptions {
    public static final ParticleOptions.Deserializer<BulletParticleData> DESERIALIZER = new ParticleOptions.Deserializer<BulletParticleData>() {
        @Nonnull
        @Override
        public BulletParticleData fromCommand(@Nonnull ParticleType<BulletParticleData> type, @Nonnull StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            Double x = reader.readDouble();
            reader.expect(' ');
            Double y = reader.readDouble();
            reader.expect(' ');
            Double z = reader.readDouble();
            reader.expect(' ');
            Double sc = reader.readDouble();
            return new BulletParticleData(x, y, z, sc);

        }

        @Nonnull
        @Override
        public BulletParticleData fromNetwork(@Nonnull ParticleType<BulletParticleData> type, FriendlyByteBuf buf) {
            return new BulletParticleData(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble());
        }
    };

    public static final Codec<BulletParticleData> CODEC = RecordCodecBuilder.create(val -> val.group(
            Codec.DOUBLE.fieldOf("x").forGetter(data -> data.posX),
            Codec.DOUBLE.fieldOf("y").forGetter(data -> data.posY),
            Codec.DOUBLE.fieldOf("z").forGetter(data -> data.posZ),
            Codec.DOUBLE.fieldOf("scale").forGetter(data -> data.scale)
    ).apply(val, BulletParticleData::new));





    @Nonnull
    @Override
    public ParticleType<?> getType() {
        return KontraptionParticleTypes.INSTANCE.getBULLET().get();
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
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", getType(), posX, posY, posZ, scale);
    }
}
