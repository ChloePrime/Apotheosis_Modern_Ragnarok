## 枪包适配指南：
在你的枪械的data文件内加上如下键值对：
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
  "is_heavy_melee_weapon": true
}
```
完整的data文件应该看起来像这样：
```json5
{
  ......
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
    "is_heavy_melee_weapon": true
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