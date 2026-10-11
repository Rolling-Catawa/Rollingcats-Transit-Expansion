package cn.jstxjf_.rte.render.train;

public interface ExpiringCachedResource {

    boolean rte$expire(long now);
}
