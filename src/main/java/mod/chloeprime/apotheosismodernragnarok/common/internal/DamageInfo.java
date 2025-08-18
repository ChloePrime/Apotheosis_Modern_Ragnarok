package mod.chloeprime.apotheosismodernragnarok.common.internal;

public interface DamageInfo extends BloodBulletUser {
    float amr$getOriginalDamage();
    void amr$setOriginalDamage(float value);
    boolean amr$isAttackFailed();
    void amr$setAttackFailed(boolean failed);
}
