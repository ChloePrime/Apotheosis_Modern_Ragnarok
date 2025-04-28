## 枪包适配指南：
在你的枪械的data文件内**按需**加上如下键值对(以下键值对全部支持留空不填)：
```json5
"apotheosis_modern_ragnarok": {
  // 该枪械/刀剑的附魔性能。原版工具材料的数值参考：
  // 铁=9，金=25，下界合金=15
  "enchantment_value": 25,
  // 为1时，强制让该tacz武器被识别为专用近战武器
  // 为-1时，强制让该tacz武器被识别为枪械
  // 为0时，让系统自动决定
  "force_melee_weapon": 1,
  // 为true时，该武器被识别为神化重型武器（斧头）
  // 只对专用近战武器有效，对枪械的近战武器（肘击，刺刀等）无效。
  "is_heavy_melee_weapon": true,
  // 强制指定该武器的神化装备类型（3.3.0新增）
  // 典型值参考：
  // "apotheosis_modern_ragnarok:bolt_action" 栓动大狙，享受最高的数值加成
  // "apotheosis_modern_ragnarok:shotgun"     霰弹枪，享受最低的（单弹片）数值加成
  // "apotheosis_modern_ragnarok:full_auto"   全自动武器，享受和半自动一样的，中等的数值加成
  // "apotheosis_modern_ragnarok:semi_auto"   半自动武器，享受和全自动一样的，中等的数值加成
  // "sword"                                  剑/轻型武器
  // "heavy_weapon"                           斧头/重型武器
  "loot_category_override": "apotheosis_modern_ragnarok:bolt_action"
  // 词条黑名单，3.2.0版本新增
  "disabled_affixes": [
    // 这将让这把武器无法在重铸台上获得 "轻便的"（增加速度）这个词条
    "apotheosis_modern_ragnarok:all_gun/attribute/lightweight"
  ],
  // 附魔黑名单，3.2.0版本新增
  "disabled_enchantments": [
    // 这将让这把武器无法拥有 "投影魔术" 附魔
    "apotheosis_modern_ragnarok:projection_magic"
  ]
}
```
完整的data文件应该看起来像这样：
```json5
{
  ......
  "apotheosis_modern_ragnarok": {
    // 这里的内容参考上面的示例
    ......
  },
  "ammo": "namespace:path",
  "ammo_amount": 666,
  "extended_mag_ammo_amount": [
    667,
    668,
    669
  ],
  "bolt": "open_bolt",
  ......
}
```
注：适配后，即使玩家没有安装神化枪械也不会影响枪包正常加载哦~