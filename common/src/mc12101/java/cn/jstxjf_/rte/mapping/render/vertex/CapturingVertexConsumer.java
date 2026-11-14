package cn.jstxjf_.rte.mapping.render.vertex;

import com.mojang.blaze3d.vertex.VertexConsumer;
import cn.jstxjf_.rte.mapping.holder.Vector3f;
import cn.jstxjf_.rte.mapping.render.batch.MaterialProperties;
import cn.jstxjf_.rte.mapping.render.model.RawMesh;
import cn.jstxjf_.rte.mapping.render.model.RawModel;

public final class CapturingVertexConsumer implements VertexConsumer {

	private RawMesh buildingMesh;
	private Vertex buildingVertex;

	public final RawModel rawModel = new RawModel();

	public void beginStage(MaterialProperties materialProperties) {
		commitVertex();
		buildingMesh = rawModel.getRawMesh(materialProperties);
	}

	public void finish() {
		commitVertex();
	}

	private void commitVertex() {
		if (buildingVertex != null && buildingMesh != null) {
			buildingMesh.addVertex(buildingVertex);
		}
		buildingVertex = null;
	}

	@Override
	public VertexConsumer addVertex(float x, float y, float z) {
		commitVertex();
		buildingVertex = new Vertex();
		buildingVertex.position = new Vector3f(x, y, z);
		return this;
	}

	@Override
	public VertexConsumer setColor(int red, int green, int blue, int alpha) {
		return this;
	}

	@Override
	public VertexConsumer setUv(float u, float v) {
		if (buildingVertex != null) {
			buildingVertex.u = u;
			buildingVertex.v = v;
		}
		return this;
	}

	@Override
	public VertexConsumer setUv1(int u, int v) {
		return this;
	}

	@Override
	public VertexConsumer setUv2(int u, int v) {
		return this;
	}

	@Override
	public VertexConsumer setNormal(float x, float y, float z) {
		if (buildingVertex != null) {
			buildingVertex.normal = new Vector3f(x, y, z);
		}
		return this;
	}
}
