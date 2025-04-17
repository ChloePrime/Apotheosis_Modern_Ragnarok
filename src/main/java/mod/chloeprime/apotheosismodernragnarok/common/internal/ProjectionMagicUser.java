package mod.chloeprime.apotheosismodernragnarok.common.internal;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface ProjectionMagicUser {
    /**
     * @return true if state changed from not activated to activated.
     */
    boolean amr$activateProjectionMagic();

    void amr$deactivateProjectionMagic();

    long amr$getProjectionMagicStartEta();

    void amr$setProjectionMagicStartEta(long value);
}
