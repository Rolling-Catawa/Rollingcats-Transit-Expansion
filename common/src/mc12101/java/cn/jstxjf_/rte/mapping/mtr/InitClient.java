package cn.jstxjf_.rte.mapping.mtr;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.mapping.registry.RegistryClient;

public final class InitClient {

	public static final RegistryClient REGISTRY_CLIENT = new RegistryClient(RTE.REGISTRY);

	private InitClient() {
	}
}
