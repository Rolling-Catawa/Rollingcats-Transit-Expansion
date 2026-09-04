package cn.jstxjf_.rte.gfx.model;

import cn.jstxjf_.rte.gfx.batch.MaterialProp;
import cn.jstxjf_.rte.gfx.object.IndexBuf;
import cn.jstxjf_.rte.gfx.object.VertBuf;

import java.io.Closeable;

public class Mesh implements Closeable {

    public VertBuf vertBuf;
    public IndexBuf indexBuf;

    public MaterialProp materialProp;

    public Mesh(VertBuf vertBuf, IndexBuf indexBuf, MaterialProp materialProp) {
        this.vertBuf = vertBuf;
        this.indexBuf = indexBuf;
        this.materialProp = materialProp;
    }

    @Override
    public void close() {
        vertBuf.close();
        indexBuf.close();
    }
}
