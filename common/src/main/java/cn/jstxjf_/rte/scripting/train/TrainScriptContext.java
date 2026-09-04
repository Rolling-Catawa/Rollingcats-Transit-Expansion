package cn.jstxjf_.rte.scripting.train;

import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.scripting.AbstractScriptContext;
import cn.jstxjf_.rte.scripting.AbstractDrawCalls.DrawCallMap;
import cn.jstxjf_.rte.scripting.util.client.DynamicModelHolder;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.gfx.math.Matrices;
import cn.jstxjf_.rte.gfx.math.Vector3f;
import cn.jstxjf_.rte.gfxext.model.ModelCluster;
import cn.jstxjf_.rte.gfxext.reuse.DrawScheduler;
import org.mtr.mod.client.MinecraftClientData;
import org.mtr.mod.data.VehicleExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

@SuppressWarnings("unused")
public class TrainScriptContext extends AbstractScriptContext {

    public VehicleExtension train;
    public TrainWrapper trainExtra;
    TrainWrapper trainExtraWriting;

    public void rebind(VehicleExtension vehicle) {
        this.train = vehicle;
        trainExtra.rebind(vehicle);
        trainExtraWriting.rebind(vehicle);
    }

    public TrainDrawCalls scriptResult;
    private TrainDrawCalls scriptResultWriting;
    public final DrawCallMap[] drawCalls;

    public TrainScriptContext(VehicleExtension train) {
        this.train = train;
        this.scriptResult = new TrainDrawCalls(train.vehicleExtraData.immutableVehicleCars.size());
        this.scriptResultWriting = new TrainDrawCalls(train.vehicleExtraData.immutableVehicleCars.size());
        this.trainExtra = new TrainWrapper(train);
        this.trainExtraWriting = new TrainWrapper(train);
        this.drawCalls = new DrawCallMap[train.vehicleExtraData.immutableVehicleCars.size()];
        for (int i = 0; i < drawCalls.length; i++) {
            drawCalls[i] = new DrawCallMap();
        }
    }

    public void commitCar(int car, DrawScheduler drawScheduler, Matrix4f basePose, Matrix4f worldPose, int light) {
        drawCalls[car].commit(drawScheduler, basePose, worldPose, light);
        scriptResult.commitCar(car, drawScheduler, basePose, worldPose, light);
    }

    public void commitConn(int car, DrawScheduler drawScheduler, Matrix4f connPose, Matrix4f worldPose, int light) {
        scriptResult.commitConn(car, drawScheduler, connPose, worldPose, light);
    }

    @Override
    public void renderFunctionFinished() {
        synchronized (this) {
            final TrainDrawCalls temp = scriptResultWriting;
            scriptResultWriting = scriptResult;
            scriptResult = temp;
            scriptResultWriting.reset();
        }
    }

    @Override
    public Object getWrapperObject() {
        return trainExtra;
    }

    @Override
    public boolean isBearerAlive() {
        return MinecraftClientData.getInstance().vehicles.stream().anyMatch(vehicle -> vehicle.getId() == train.getId());
    }

    private int rte$skippedSwaps = 0;

    public void extraFinished() {
        cn.jstxjf_.rte.scripting.train.StationTimingProbe.sample(trainExtraWriting);
        synchronized (this) {
            if (scriptStatus != null && !scriptStatus.isDone() && ++rte$skippedSwaps < 3) {
                return;
            }
            rte$skippedSwaps = 0;
            final TrainWrapper temp = trainExtraWriting;
            trainExtraWriting = trainExtra;
            trainExtra = temp;
            trainExtraWriting.reset();
        }
    }

    public TrainWrapper getTrainExtraWriting() {
        return trainExtraWriting;
    }

    public void drawCarModel(ModelCluster model, int carIndex, Matrices poseStack) {
        scriptResultWriting.addCarModel(carIndex, model, poseStack == null ? Matrix4f.IDENTITY : poseStack.getAsMatrix4f().copy());
    }

    public void drawCarModel(DynamicModelHolder model, int carIndex, Matrices poseStack) {
        scriptResultWriting.addCarModel(carIndex, model, poseStack == null ? Matrix4f.IDENTITY : poseStack.getAsMatrix4f().copy());
    }

    public void drawConnModel(ModelCluster model, int carIndex, Matrices poseStack) {
        scriptResultWriting.addConnModel(carIndex, model, poseStack == null ? Matrix4f.IDENTITY : poseStack.getAsMatrix4f().copy());
    }

    public void drawConnModel(DynamicModelHolder model, int carIndex, Matrices poseStack) {
        scriptResultWriting.addConnModel(carIndex, model, poseStack == null ? Matrix4f.IDENTITY : poseStack.getAsMatrix4f().copy());
    }

    public void drawConnStretchTexture(ResourceLocation location, int carIndex) {
        scriptResultWriting.drawConnStretchTexture(carIndex, new org.mtr.mapping.holder.Identifier(location));
    }

    public void playCarSound(ResourceLocation sound, int carIndex, float x, float y, float z, float volume, float pitch) {
        if (!ClientConfig.enableTrainSound) {
            return;
        }
        scriptResultWriting.addCarSound(carIndex, SoundEvent.createVariableRangeEvent(sound), new Vector3f(x, y, z), volume, pitch);
    }

    public long worldTime() {
        final net.minecraft.client.multiplayer.ClientLevel level = Minecraft.getInstance().level;
        return level != null ? level.getGameTime() : 0;
    }

    public long dayTime() {
        final net.minecraft.client.multiplayer.ClientLevel level = Minecraft.getInstance().level;
        return level != null ? level.getDayTime() % 24000 : 0;
    }

    public String weather() {
        final net.minecraft.client.multiplayer.ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return "clear";
        if (level.isThundering()) return "thunder";
        if (level.isRaining()) return "rain";
        return "clear";
    }

    public float rainLevel() {
        final net.minecraft.client.multiplayer.ClientLevel level = Minecraft.getInstance().level;
        return level != null ? level.getRainLevel(1.0f) : 0;
    }

    public double playerX() {
        final LocalPlayer player = Minecraft.getInstance().player;
        return player != null ? player.getX() : 0;
    }

    public double playerY() {
        final LocalPlayer player = Minecraft.getInstance().player;
        return player != null ? player.getY() : 0;
    }

    public double playerZ() {
        final LocalPlayer player = Minecraft.getInstance().player;
        return player != null ? player.getZ() : 0;
    }

    public cn.jstxjf_.rte.gfx.math.Vector3f playerPosition() {
        final LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return new cn.jstxjf_.rte.gfx.math.Vector3f(0, 0, 0);
        return new cn.jstxjf_.rte.gfx.math.Vector3f((float) player.getX(), (float) player.getY(), (float) player.getZ());
    }

    public void playAnnSound(ResourceLocation sound, float volume, float pitch) {
        if (!ClientConfig.enableTrainSound) {
            return;
        }
        Minecraft.getInstance().execute(() -> {
            final LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && org.mtr.mod.client.VehicleRidingMovement.isRiding(train.getId())) {
                Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(
                    sound,
                    SoundSource.BLOCKS,
                    volume,
                    pitch,
                    SoundInstance.createUnseededRandom(),
                    false,
                    0,
                    SoundInstance.Attenuation.NONE,
                    0,
                    0,
                    0,
                    true
                ));
            }
        });
    }
}
