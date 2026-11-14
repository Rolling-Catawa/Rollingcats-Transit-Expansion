package cn.jstxjf_.rte.scripting.compat;

import org.mtr.core.data.Siding;
import org.mtr.core.data.TransportMode;

@SuppressWarnings("unused")
public class SidingCompat {

    public final long id;
    public final String name;
    public final int color;
    public final TransportMode transportMode;
    public final double railLength;

    private final Siding delegate;

    public SidingCompat(Siding delegate) {
        this.delegate = delegate;
        this.id = delegate.getId();
        this.name = delegate.getName();
        this.color = delegate.getColor();
        this.transportMode = delegate.getTransportMode();
        this.railLength = delegate.getRailLength();
    }

    private SidingCompat(long id) {
        this.delegate = null;
        this.id = id;
        this.name = "";
        this.color = 0;
        this.transportMode = TransportMode.TRAIN;
        this.railLength = 0;
    }

    public static SidingCompat stub(long id) {
        return new SidingCompat(id);
    }

    public boolean isResolved() {
        return delegate != null;
    }

    public Siding getMtr4Siding() {
        return delegate;
    }

    public double getAccelerationConstant() {
        if (delegate == null) return 0;
        return delegate.getAcceleration();
    }

    public boolean getIsManual() {
        if (delegate == null) return false;
        return delegate.getIsManual();
    }

    public long getMaxVehicles() {
        if (delegate == null) return 0;
        return delegate.getMaxVehicles();
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
