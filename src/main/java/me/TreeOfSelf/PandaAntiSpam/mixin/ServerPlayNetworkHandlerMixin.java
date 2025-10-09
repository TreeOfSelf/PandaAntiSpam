package me.TreeOfSelf.PandaAntiSpam.mixin;

import me.TreeOfSelf.PandaAntiSpam.PandaAntiSpam;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {

	@Unique
	public int messages = 0;
	@Unique
	public long lastMessage = 0;

	@Inject(method = "onChatMessage", at = @At(value = "HEAD"), cancellable = true)
	private void onGameMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {

		ServerPlayerEntity player = ((ServerPlayNetworkHandler) (Object) this).player;
		if (player.hasPermissionLevel(4)) return;

		if (System.currentTimeMillis() - lastMessage > PandaAntiSpam.config.cooldownTime) {
			lastMessage = System.currentTimeMillis();
			messages = 0;
		}

		if (messages >= PandaAntiSpam.config.messageLimit) {
			if (!PandaAntiSpam.config.warningMessage.isEmpty()) {
				player.sendMessage(Text.of(PandaAntiSpam.config.warningMessage));
			}
			ci.cancel();
		} else messages++;

	}

	@Inject(method = "onCommandExecution", at = @At(value = "HEAD"), cancellable = true)
	private void onCommandExecution(CommandExecutionC2SPacket packet, CallbackInfo ci) {

		String command = packet.command();
		ServerPlayerEntity player = ((ServerPlayNetworkHandler) (Object) this).player;
		if (player.hasPermissionLevel(4)) return;

		if (System.currentTimeMillis() - lastMessage > PandaAntiSpam.config.cooldownTime) {
			lastMessage = System.currentTimeMillis();
			messages = 0;
		}

		if(command.startsWith("tell")
				|| command.startsWith("msg") ||
				command.startsWith("w") ||
				command.startsWith("me")
		) {
			if (messages >= PandaAntiSpam.config.messageLimit) {
				if (!PandaAntiSpam.config.warningMessage.isEmpty()) {
					player.sendMessage(Text.of(PandaAntiSpam.config.warningMessage));
				}
				ci.cancel();
			} messages++;
		}

	}

}
