package cn.jstxjf_.rte.compat;

import java.io.IOException;
import java.io.InputStream;

@FunctionalInterface
public interface StreamSupplier {

    InputStream get() throws IOException;
}
