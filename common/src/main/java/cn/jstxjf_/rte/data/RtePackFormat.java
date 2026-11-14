package cn.jstxjf_.rte.data;

public final class RtePackFormat {

    //#if MC>=12002
    public static final int VALUE = 22;
    //#elseif MC>=12000
    public static final int VALUE = 15;
    //#else
    public static final int VALUE = 9;
    //#endif

    private RtePackFormat() {
    }
}
