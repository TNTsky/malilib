package fi.dy.masa.malilib.network.payload;

import fi.dy.masa.malilib.network.PayloadTypes;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record S2CDataPayload(Identifier id, PacketByteBuf data) implements CustomPayload
{
    public static final Id<S2CDataPayload> TYPE;
    public static final PacketCodec<PacketByteBuf, S2CDataPayload> CODEC;

    private S2CDataPayload(PacketByteBuf buf)
    {
        this(buf.readIdentifier(), (PacketByteBuf) buf.readNullable((bufx) -> bufx.readBytes(PayloadTypes.MAX_TOTAL_PER_PACKET_S2C)));
    }

    public S2CDataPayload(Identifier id, PacketByteBuf data)
    {
        this.id = id;
        this.data = data;
    }

    private void write(PacketByteBuf buf)
    {
        buf.writeIdentifier(this.id);
        buf.writeBytes(this.data);
    }

    @Override
    public Id<? extends CustomPayload> getId() { return TYPE; }
    static
    {
        TYPE = new Id<>(PayloadTypes.getPayloadId(PayloadTypes.getNamespace(), "s2c-data"));
        CODEC = CustomPayload.codecOf(S2CDataPayload::write, S2CDataPayload::new);
    }
}
