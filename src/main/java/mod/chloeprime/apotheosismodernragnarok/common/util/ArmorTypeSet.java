package mod.chloeprime.apotheosismodernragnarok.common.util;

import net.minecraft.world.entity.EquipmentSlot;

import java.util.LinkedHashSet;

public class ArmorTypeSet extends LinkedHashSet<EquipmentSlot> {
    public ArmorTypeSet() {
    }

    public ArmorTypeSet(int initialCapacity) {
        super(initialCapacity);
    }
}
