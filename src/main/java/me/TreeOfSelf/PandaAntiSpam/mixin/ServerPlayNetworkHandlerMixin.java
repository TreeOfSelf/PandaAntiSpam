package me.TreeOfSelf.PandaAntiSpam.mixin;

import me.TreeOfSelf.PandaAntiSpam.PandaAntiSpam;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandSignedPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerPlayNetworkHandlerMixin {

	@Unique
	private int pandaAntiSpam$messages = 0;
	@Unique
	private long pandaAntiSpam$lastMessage = 0;

	@Inject(method = "handleChat", at = @At("HEAD"), cancellable = true)
	private void pandaAntiSpam$onChat(ServerboundChatPacket packet, CallbackInfo ci) {
		ServerPlayer player = ((ServerGamePacketListenerImpl) (Object) this).player;
		if (player.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.ADMINS))) {
			return;
		}

		if (System.currentTimeMillis() - pandaAntiSpam$lastMessage > PandaAntiSpam.config.cooldownTime) {
			pandaAntiSpam$lastMessage = System.currentTimeMillis();
			pandaAntiSpam$messages = 0;
		}

		if (pandaAntiSpam$messages >= PandaAntiSpam.config.messageLimit) {
			pandaAntiSpam$sendWarning(player);
			ci.cancel();
		} else {
			pandaAntiSpam$messages++;
		}
	}

	@Inject(method = "handleChatCommand", at = @At("HEAD"), cancellable = true)
	private void pandaAntiSpam$onChatCommand(ServerboundChatCommandPacket packet, CallbackInfo ci) {
		pandaAntiSpam$onCommandLike(packet.command(), ci);
	}

	@Inject(method = "handleSignedChatCommand", at = @At("HEAD"), cancellable = true)
	private void pandaAntiSpam$onSignedChatCommand(ServerboundChatCommandSignedPacket packet, CallbackInfo ci) {
		pandaAntiSpam$onCommandLike(packet.command(), ci);
	}

	@Unique
	private void pandaAntiSpam$onCommandLike(String command, CallbackInfo ci) {
		ServerPlayer player = ((ServerGamePacketListenerImpl) (Object) this).player;
		if (player.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.ADMINS))) {
			return;
		}

		if (System.currentTimeMillis() - pandaAntiSpam$lastMessage > PandaAntiSpam.config.cooldownTime) {
			pandaAntiSpam$lastMessage = System.currentTimeMillis();
			pandaAntiSpam$messages = 0;
		}

		if (command.startsWith("tell")
				|| command.startsWith("msg")
				|| command.startsWith("w")
				|| command.startsWith("me")) {
			if (pandaAntiSpam$messages >= PandaAntiSpam.config.messageLimit) {
				pandaAntiSpam$sendWarning(player);
				ci.cancel();
			}
			pandaAntiSpam$messages++;
		}
	}

	@Unique
	private static void pandaAntiSpam$sendWarning(ServerPlayer player) {
		if (!PandaAntiSpam.config.warningMessage.isEmpty()) {
			player.sendSystemMessage(Component.literal(PandaAntiSpam.config.warningMessage));
		}
	}
}
