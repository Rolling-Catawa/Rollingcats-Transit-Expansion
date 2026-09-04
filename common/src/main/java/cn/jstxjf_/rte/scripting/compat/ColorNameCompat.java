package cn.jstxjf_.rte.scripting.compat;

@SuppressWarnings("unused")
public class ColorNameCompat {

    public final int color;
    public final String name;

    public ColorNameCompat(int color, String name) {
        this.color = color;
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public String getName() {
        return name;
    }
}
