package fi.dy.masa.malilib.mixin;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import fi.dy.masa.malilib.event.WorldLoadHandler;
import fi.dy.masa.malilib.network.handler.client.ClientNetworkListener;
import fi.dy.masa.malilib.network.payload.PayloadManager;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler
{
    @Shadow private ClientWorld world;

    @Nullable private ClientWorld worldBefore;

    @Inject(method = "onGameJoin", at = @At("HEAD"))
    private void onPreJoinGameHead(GameJoinS2CPacket packet, CallbackInfo ci)
    {
        // Need to grab the old world reference at the start of the method,
        // because the next injection point is right after the world has been assigned,
        // since we need the new world reference for the callback.
        this.worldBefore = this.world;
        PayloadManager.getInstance().resetPayloads();
        PayloadManager.getInstance().verifyPayloads();
    }

    @Inject(method = "onGameJoin", at = @At(value = "INVOKE",
                target = "Lnet/minecraft/client/MinecraftClient;joinWorld(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/gui/screen/DownloadingTerrainScreen$class_9678;)V"))
    private void onPreGameJoin(GameJoinS2CPacket packet, CallbackInfo ci)
    {
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPre(this.worldBefore, this.world, MinecraftClient.getInstance());
    }

    @Inject(method = "onGameJoin", at = @At("RETURN"))
    private void onPostGameJoin(GameJoinS2CPacket packet, CallbackInfo ci)
    {
        ((WorldLoadHandler) WorldLoadHandler.getInstance()).onWorldLoadPost(this.worldBefore, this.world, MinecraftClient.getInstance());
        this.worldBefore = null;

        PayloadManager.getInstance().registerHandlers();
    }

    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    private void malilib_onCustomPayload(CustomPayload packet, CallbackInfo ci)
    {
        if (!MinecraftClient.getInstance().isOnThread())
        {
            return;
        }

        ClientNetworkListener.getInstance().handleClientPayload((ClientPlayNetworkHandler) (Object) this, packet, ci);
    }
}