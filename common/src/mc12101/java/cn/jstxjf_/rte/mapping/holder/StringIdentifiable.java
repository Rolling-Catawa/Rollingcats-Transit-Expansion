package cn.jstxjf_.rte.mapping.holder;

public interface StringIdentifiable extends net.minecraft.util.StringRepresentable {

    public java.lang.String asString2();

    public default java.lang.String getSerializedName() {
        return this.asString2();
    }
}
