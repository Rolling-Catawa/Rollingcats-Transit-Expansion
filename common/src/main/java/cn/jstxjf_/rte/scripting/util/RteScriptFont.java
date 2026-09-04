package cn.jstxjf_.rte.scripting.util;

import java.awt.Font;

public class RteScriptFont extends Font {

    public RteScriptFont(Font base) {
        super(base);
    }

    @Override
    public Font deriveFont(int style) {
        if (style < 0 || style > (BOLD | ITALIC)) {
            return deriveFont((float) style);
        }
        return super.deriveFont(style);
    }
}
