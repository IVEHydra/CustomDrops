package me.ivehydra.customdrops.utils;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;

import java.util.Optional;

public class MaterialUtils {

    public static Material parse(String string) {
        if(string.isEmpty()) return null;
        Optional<XMaterial> xMaterial = XMaterial.matchXMaterial(string.toUpperCase());
        return xMaterial.map(XMaterial::get).orElse(null);
    }

}
