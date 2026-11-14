package cn.jstxjf_.rte.scripting.compat;

import org.mtr.core.data.Platform;
import org.mtr.core.data.TransportMode;

@SuppressWarnings("unused")
public class PlatformCompat {

    public final double id;
    public final long idLong;
    public final String name;
    public final int color;
    public final TransportMode transportMode;
    public final long dwellTime;

    private final Platform delegate;

    public PlatformCompat(Platform delegate) {
        this.delegate = delegate;
        this.idLong = delegate.getId();
        this.id = this.idLong;
        this.name = delegate.getName();
        this.color = delegate.getColor();
        this.transportMode = delegate.getTransportMode();
        this.dwellTime = Math.max(0, delegate.getDwellTime() / 500);
    }

    public PlatformCompat(long id) {
        this.delegate = null;
        this.idLong = id;
        this.id = id;
        this.name = "";
        this.color = 0;
        this.transportMode = TransportMode.TRAIN;
        this.dwellTime = 0;
    }

    public Platform getMtr4Platform() {
        return delegate;
    }

    public long getDwellTime() {
        return dwellTime;
    }

    public long getDwellTimeMillis() {
        return delegate != null ? delegate.getDwellTime() : 0;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public double getId() {
        return id;
    }
}
