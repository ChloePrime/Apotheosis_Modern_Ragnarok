package mod.chloeprime.apotheosismodernragnarok.common.internal;

public interface DamageInfo extends BloodBulletUser {
    float amr$getOriginalDamage();
    void amr$setOriginalDamage(float value);
    boolean amr$isAttackFailed();
    void amr$setAttackFailed(boolean failed);

    default void amr$recordNewHighestDamage(float amount) {
        if (amr$getOriginalDamage() < amount) {
            amr$setOriginalDamage(amount);
        }
    }
}
