package net.st1ch.minecraftacademy.education;

import java.util.List;

public class EducationLevel {
    public final String name;
    public final List<String> layout;
    public final String spawnFacing;

    public EducationLevel(String name, List<String> layout, String spawnFacing) {
        this.name = name;
        this.layout = layout;
        this.spawnFacing = spawnFacing;
    }
}
