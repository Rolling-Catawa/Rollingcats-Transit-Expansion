package cn.jstxjf_.rte.mapping.render.model;

import cn.jstxjf_.rte.mapping.render.batch.MaterialProperties;
import cn.jstxjf_.rte.mapping.render.object.IndexBuffer;
import cn.jstxjf_.rte.mapping.render.object.VertexBuffer;

import java.io.Closeable;

public final class Mesh implements Closeable {

	public final VertexBuffer vertexBuffer;
	public final IndexBuffer indexBuffer;

	public final MaterialProperties materialProperties;

	public Mesh(VertexBuffer vertexBuffer, IndexBuffer indexBuffer, MaterialProperties materialProperties) {
		this.vertexBuffer = vertexBuffer;
		this.indexBuffer = indexBuffer;
		this.materialProperties = materialProperties;
	}

	@Override
	public void close() {
		vertexBuffer.close();
		indexBuffer.close();
	}
}
