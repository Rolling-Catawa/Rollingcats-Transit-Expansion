package cn.jstxjf_.rte.gfxext.model.loader;

import cn.jstxjf_.rte.gfxext.model.RawMesh;
import cn.jstxjf_.rte.gfxext.model.RawModel;
import cn.jstxjf_.rte.gfxext.reuse.AtlasManager;
import cn.jstxjf_.rte.gfxext.util.ResourceUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

// Use standard Java IO/Net for reading resources in 1.20

public class NmbModelLoader {

    public static RawModel loadModel(ResourceManager resourceManager, ResourceLocation location, AtlasManager atlasManager) throws IOException {
        InputStream is = ResourceUtil.getResource(resourceManager, location)
            .orElseThrow(() -> new IOException("Resource not found: " + location))
            .open();
        DataInputStream dis = new DataInputStream(is);
        dis.skipNBytes(8);
        int versionMajor = dis.readInt();
        int versionMinor = dis.readInt();

        byte[] dContent;
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] key = dis.readNBytes(32);
            SecretKeySpec aesKey = new SecretKeySpec(key, "AES");
            byte[] iv = Arrays.copyOfRange(sha256.digest(key), 0, 16);
            IvParameterSpec aesIv = new IvParameterSpec(iv);

            int len = dis.readInt();
            byte[] eContent = dis.readNBytes(len);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, aesKey, aesIv);
            dContent = cipher.doFinal(eContent);
        } catch (Exception ex) {
            throw new IOException(ex);
        } finally {
            dis.close();
        }

        RawModel model = new RawModel(new DataInputStream(new ByteArrayInputStream(dContent)));
        model.sourceLocation = location;
        if (atlasManager != null) {
            for (RawMesh mesh : model.meshList.values()) {
                atlasManager.applyToMesh(mesh);
            }
        }

        return model;
    }
}
