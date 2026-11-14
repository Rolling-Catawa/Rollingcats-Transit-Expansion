package cn.jstxjf_.rte.scripting.compat;

import org.mtr.core.data.RoutePlatformData;

@SuppressWarnings("unused")
public class RoutePlatformCompat {

    public final long platformId;

    private final RoutePlatformData delegate;

    public RoutePlatformCompat(RoutePlatformData delegate) {
        this.delegate = delegate;
        this.platformId = delegate.getPlatform() != null ? delegate.getPlatform().getId() : 0;
    }

    public RoutePlatformCompat(long platformId) {
        this.delegate = null;
        this.platformId = platformId;
    }

    public String getCustomDestination() {
        return delegate != null ? delegate.getCustomDestination() : "";
    }

    public org.mtr.core.data.Platform getPlatform() {
        return delegate != null ? delegate.getPlatform() : null;
    }
}
