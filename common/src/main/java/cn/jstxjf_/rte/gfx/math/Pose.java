package cn.jstxjf_.rte.gfx.math;

public class Pose implements Posture {

    public final Matrix4f pose;
    public final Matrix3f normal;

    public Pose(Matrix4f pose, Matrix3f normal) {
        this.pose = pose;
        this.normal = normal;
    }

    public Pose(Matrix4f pose) {
        this(pose, new Matrix3f(pose));
    }

    public Pose() {
        this(new Matrix4f(), new Matrix3f());
    }

    @Override
    public Matrix4f getAsMatrix4f() {
        return pose;
    }

    @Override
    public Matrix3f getAsMatrix3f() {
        return normal;
    }

    @Override
    public Matrices getAsMatrices() {
        return new Matrices(pose);
    }

    @Override
    public Pose getAsPose() {
        return this;
    }

    @Override
    public PoseStack getAsPoseStack() {
        return new PoseStack(this);
    }
}
