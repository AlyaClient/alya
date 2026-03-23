package dev.thoq.util.movement;

import dev.thoq.util.IUtil;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import org.lwjgl.util.vector.Vector2f;

@SuppressWarnings("unused")
public final class MovementUtil implements IUtil {
  public static final float SPRINT_SPEED = 0.1533f;
  public static final float WALK_SPEED = 0.0888f;

  public static boolean isMoving() {
    return (MC.thePlayer.movementInput.moveForward != 0F
        || MC.thePlayer.movementInput.moveStrafe != 0F);
  }

  public static void jump() {
    MC.thePlayer.jump();
  }

  public static float getMoveYaw() {
    final Vector2f
        from = new Vector2f((float) MC.thePlayer.lastTickPosX, (float) MC.thePlayer.lastTickPosZ),
        to = new Vector2f((float) MC.thePlayer.posX, (float) MC.thePlayer.posZ),
        diff = new Vector2f(to.getX() - from.getX(), to.getY() - from.getY());
    return (float)
        Math.toDegrees((Math.atan2(-diff.getX(), diff.getY()) + MathHelper.PI2) % MathHelper.PI2);
  }

  public static double getMoveSpeed() {
    return Math.sqrt(
        MC.thePlayer.motionX * MC.thePlayer.motionX + MC.thePlayer.motionZ * MC.thePlayer.motionZ);
  }

  public static double getDirection() {
    float rotationYaw = MC.thePlayer.rotationYaw;
    if (MC.thePlayer.moveForward < 0) rotationYaw += 180;
    float forward = 1;
    if (MC.thePlayer.moveForward < 0) forward = -0.5F;
    else if (MC.thePlayer.moveForward > 0) forward = 0.5F;
    if (MC.thePlayer.moveStrafing > 0) rotationYaw -= 90 * forward;
    else if (MC.thePlayer.moveStrafing < 0) rotationYaw += 90 * forward;
    return Math.toRadians(rotationYaw);
  }

  public static float getSpeed() {
    return (float)
        Math.sqrt(
            MC.thePlayer.motionX * MC.thePlayer.motionX
                + MC.thePlayer.motionZ * MC.thePlayer.motionZ);
  }

  public static double getAllowedHDistNCP() {
    double hDist = MC.thePlayer.capabilities.getWalkSpeed();
    if (MC.thePlayer.isPotionActive(Potion.moveSlowdown))
      hDist /=
          1.0 + 0.05 * (MC.thePlayer.getActivePotionEffect(Potion.moveSlowdown).getAmplifier() + 1);
    if (MC.thePlayer.isPotionActive(Potion.moveSpeed))
      hDist *=
          1.0 + 0.05 * (MC.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
    return hDist * 4.8;
  }

  public static void setSpeed(final double speed) {
    if (MC.thePlayer.movementInput.moveForward == 0F) return;
    final double yaw = getDirection();
    MC.thePlayer.motionX = -MathHelper.sin((float) yaw) * speed;
    MC.thePlayer.motionZ = MathHelper.cos((float) yaw) * speed;
  }

  public static void setSpeed(final double speed, double strafePercentage) {
    strafePercentage /= 100;
    strafePercentage = Math.min(1, Math.max(0, strafePercentage));
    if (!isMoving()) return;
    final double currentSpeed = getMoveSpeed();
    if (currentSpeed == 0) {
      setSpeed(speed);
      return;
    }
    final double scale = speed / currentSpeed;
    final double forwardX = -MathHelper.sin((float) getDirection()) * speed;
    final double forwardZ = MathHelper.cos((float) getDirection()) * speed;
    final double strafeX = MC.thePlayer.motionX * scale;
    final double strafeZ = MC.thePlayer.motionZ * scale;
    MC.thePlayer.motionX = forwardX + (strafeX - forwardX) * strafePercentage;
    MC.thePlayer.motionZ = forwardZ + (strafeZ - forwardZ) * strafePercentage;
  }
}
