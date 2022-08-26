package io.unthrottled.doki.build.jvm.tools

object GroupToNameMapping {

  private val nameMapping =
    mapOf(
      "Azur Lane" to "AzurLane: ",
      "Kill la Kill" to "KillLaKill: ",
      "Blend S" to "BlendS: ",
      "Guilty Crown" to "GuiltyCrown: ",
      "Code Geass" to "CodeGeass: ",
      "Helpful Fox Senko-san" to "Senko-san: ",
      "Charlotte" to "Charlotte: ",
      "Toaru Majutsu no Index" to "Railgun: ",
      "The Rising of Shield Hero" to "ShieldHero: ",
      "Chuunibyou" to "Chuunibyou: ",
      "Re Zero" to "Re:Zero: ",
      "One Punch Man" to "OPM: ",
      "Shokugeki no Soma" to "Shokugeki: ",
      "Haikyu!!" to "Haikyu: ",
      "That Time I Get ReIncarnated As A Slime" to "Slime: ",
      "Love Live" to "LoveLive: ",
      "Literature Club" to "DDLC: ",
      "KonoSuba" to "KonoSuba: ",
      "Darling in the Franxx" to "Franxx: ",
      "Bunny Senpai" to "BunnySenpai: ",
      "Steins Gate" to "SG: ",
      "Gate" to "Gate: ",
      "Quintessential Quintuplets" to "QQ: ",
      "Fate" to "TypeMoon: ",
      "Type-Moon" to "TypeMoon: ",
      "Daily Life With A Monster Girl" to "MonsterMusume: ",
      "Vocaloid" to "Vocaloid: ",
      "DanganRonpa" to "DR: ",
      "High School DxD" to "DxD: ",
      "Sword Art Online" to "SAO: ",
      "Lucky Star" to "LS: ",
      "Evangelion" to "EVA: ",
      "EroManga Sensei" to "EroManga: ",
      "Miss Kobayashi's Dragon Maid" to "DM: ",
      "OreGairu" to "OreGairu: ",
      "OreImo" to "OreImo: ",
      "The Great Jahy Will Not Be Defeated" to "JahySama: ",
      "Future Diary" to "FutureDiary: ",
      "Kakegurui" to "Kakegurui: ",
      "Monogatari" to "Monogatari: ",
      "Don't Toy with me Miss Nagatoro" to "DTWMMN: ",
      "Miscellaneous" to "Misc: ",
      "Yuru Camp" to "YuruCamp: ",
      "NekoPara" to "NekoPara: ",
    )

  fun getLafNamePrefix(groupName: String): String =
    nameMapping[groupName]
      ?: throw IllegalArgumentException("No group to name mapping for $groupName")
}
