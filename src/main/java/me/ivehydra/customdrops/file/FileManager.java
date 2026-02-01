package me.ivehydra.customdrops.file;

import java.util.HashMap;
import java.util.Map;

public class FileManager {

    private final Map<String, CustomFile> files = new HashMap<>();

    public void createFile(String folder, String name) {
        String key = folder == null ? name : folder + "/" + name;

        CustomFile file = new CustomFile(folder, name);
        files.put(key, file);
    }

    public CustomFile getFile(String folder, String name) {
        String key = folder + "/" + name;
        return files.get(key);
    }

    public void reloadAll() { files.values().forEach(CustomFile::reload); }

    public void saveAll() { files.values().forEach(CustomFile::save); }

}
