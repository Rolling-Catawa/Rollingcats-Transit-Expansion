package cn.jstxjf_.rte.scripting.compat;

import org.mtr.core.data.Depot;
import org.mtr.core.data.Siding;

@SuppressWarnings("unused")
public class DepotCompat {

    public final long id;
    public final String name;
    public final int color;

    private final Depot delegate;

    public DepotCompat(Depot delegate) {
        this.delegate = delegate;
        this.id = delegate.getId();
        this.name = delegate.getName();
        this.color = delegate.getColor();
    }

    public DepotCompat(Siding siding) {
        final Depot area = siding.area;
        this.delegate = area;
        if (area != null) {
            this.id = area.getId();
            this.name = area.getName();
            this.color = area.getColor();
        } else {
            this.id = 0;
            this.name = "";
            this.color = 0;
        }
    }

    private DepotCompat() {
        this.delegate = null;
        this.id = 0;
        this.name = "";
        this.color = 0;
    }

    public static DepotCompat unknown() {
        return new DepotCompat();
    }

    public Depot getMtr4Depot() {
        return delegate;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public long getId() {
        return id;
    }
}
