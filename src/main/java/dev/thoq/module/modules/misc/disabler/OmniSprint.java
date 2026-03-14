package dev.thoq.module.modules.misc.disabler;

import dev.thoq.event.EventHandler;
import dev.thoq.event.events.PacketSendEvent;
import dev.thoq.module.Submodule;
import dev.thoq.module.modules.misc.DisablerModule;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public final class OmniSprint extends Submodule {

    private static boolean serverSprintState = false;

    public OmniSprint(final DisablerModule parent) {
        super("OmniSprint", parent);
    }

    @EventHandler
    private void onPacketSendEvent(final PacketSendEvent event) {
        if(MC.thePlayer == null || MC.getNetHandler() == null) {
            return;
        }

        if(event.getPacket() instanceof C0BPacketEntityAction) {
            C0BPacketEntityAction packet = (C0BPacketEntityAction) event.getPacket();
            if(packet.getAction() == C0BPacketEntityAction.Action.START_SPRINTING) {
                if(serverSprintState) {
                    MC.getNetHandler().addToSendQueue(new C0BPacketEntityAction(MC.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                    serverSprintState = false;
                }
                event.cancel();
            } else if(packet.getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                event.cancel();
            }
        }
    }


}
