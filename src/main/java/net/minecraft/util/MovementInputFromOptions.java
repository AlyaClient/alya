package net.minecraft.util;

import dev.thoq.Alya;
import dev.thoq.event.events.PlayerInputEvent;
import dev.thoq.event.events.PlayerMoveEvent;
import net.minecraft.client.settings.GameSettings;

public class MovementInputFromOptions extends MovementInput {

  private final GameSettings gameSettings;

  public MovementInputFromOptions(GameSettings gameSettingsIn) {
    this.gameSettings = gameSettingsIn;
  }

  public void updatePlayerMoveState() {
    final PlayerMoveEvent playerMoveEvent = new PlayerMoveEvent();
    Alya.getInstance().getEventBus().dispatch(playerMoveEvent);
    if (playerMoveEvent.isCanceled()) {
      return;
    }

    this.moveStrafe = 0.0F;
    this.moveForward = 0.0F;

    if (this.gameSettings.keyBindForward.isKeyDown()) {
      ++this.moveForward;
    }

    if (this.gameSettings.keyBindBack.isKeyDown()) {
      --this.moveForward;
    }

    if (this.gameSettings.keyBindLeft.isKeyDown()) {
      ++this.moveStrafe;
    }

    if (this.gameSettings.keyBindRight.isKeyDown()) {
      --this.moveStrafe;
    }

    this.jump = this.gameSettings.keyBindJump.isKeyDown();
    this.sneak = this.gameSettings.keyBindSneak.isKeyDown();

    final PlayerInputEvent playerInputEvent = new PlayerInputEvent();
    Alya.getInstance().getEventBus().dispatch(playerInputEvent);
    if (playerInputEvent.isCanceled()) {
      return;
    }

    if (this.sneak) {
      this.moveStrafe = (float) ((double) this.moveStrafe * 0.3D);
      this.moveForward = (float) ((double) this.moveForward * 0.3D);
    }
  }
}
