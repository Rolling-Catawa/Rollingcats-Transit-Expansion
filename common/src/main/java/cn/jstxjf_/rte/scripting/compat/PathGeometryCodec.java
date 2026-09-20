package cn.jstxjf_.rte.scripting.compat;

import org.mtr.core.data.PathData;
import org.mtr.core.data.Position;
import org.mtr.core.data.Rail;
import org.mtr.core.serializer.JsonReader;
import org.mtr.core.tool.Utilities;
import org.mtr.libraries.com.google.gson.JsonObject;
import org.mtr.libraries.com.google.gson.JsonParser;

public final class PathGeometryCodec {
    private PathGeometryCodec() {
    }

    public static String encode(PathData path) {
        final JsonObject object = new JsonObject();
        object.add("path", Utilities.getJsonObjectFromData(path));
        object.add("rail", Utilities.getJsonObjectFromData(path.getRail()));
        return object.toString();
    }

    public static PathData decode(String json) {
        final JsonObject object = JsonParser.parseString(json).getAsJsonObject();
        final PathData metadata = new PathData(new JsonReader(object.get("path")));
        final Rail rail = new Rail(new JsonReader(object.get("rail")));
        final Position start = metadata.reversePositions
            ? metadata.getOrderedPosition2() : metadata.getOrderedPosition1();
        final Position end = metadata.reversePositions
            ? metadata.getOrderedPosition1() : metadata.getOrderedPosition2();
        return new PathData(rail, metadata.getSavedRailBaseId(), metadata.getDwellTime(),
            metadata.getStopIndex(), metadata.getStartDistance(), metadata.getEndDistance(),
            start, rail.getStartAngle(start), end, rail.getStartAngle(end));
    }
}
