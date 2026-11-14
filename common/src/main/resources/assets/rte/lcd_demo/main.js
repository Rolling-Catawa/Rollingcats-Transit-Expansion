importPackage(java.awt);
importPackage(java.awt.geom);

include(Resources.id("rte:scripts/display_helper.js"));

const PANEL_W = 1617;
const PANEL_H = 280;
const LEFT_W = 680;

const CEIL_Y = 2.137;
const WALL_INNER = 0.183;
const WALL_OUTER = 0.371;
const PANEL_TILT = 55;

const HALF_WIDTH = 1.0;
const CAR_LENGTH = 22;

function buildSlotConfig() {
    const halfWidth = HALF_WIDTH;
    const carLength = CAR_LENGTH;

    const xIn = halfWidth - WALL_INNER;
    const xOut = halfWidth - WALL_OUTER;
    const xMid = (xIn + xOut) / 2;

    const leftPos = getCubeVertices([xOut, CEIL_Y, -0.5], [xIn, CEIL_Y, 0.5], [xMid, CEIL_Y, 0], 0, 0, -PANEL_TILT);
    const rightPos = getCubeVertices([-xOut, CEIL_Y, 0.5], [-xIn, CEIL_Y, -0.5], [-xMid, CEIL_Y, 0], 0, 0, PANEL_TILT);

    const usable = carLength - 4;
    const count = Math.max(2, Math.min(6, Math.round(usable / 5)));
    const offsets = [];
    for (let i = 0; i < count; i++) {
        const t = count === 1 ? 0.5 : i / (count - 1);
        offsets.push([0, 0, -usable / 2 + usable * t]);
    }

    return {
        "version": 1,
        "texSize": [PANEL_W, 1024],
        "slots": [
            { "name": "left", "texArea": [0, 0, PANEL_W, PANEL_H], "pos": [leftPos], "offsets": offsets },
            { "name": "right", "texArea": [0, 512, PANEL_W, PANEL_H], "pos": [rightPos], "offsets": offsets }
        ]
    };
}

function pickFont(size, bold) {
    return new Font("Dialog", bold ? Font.BOLD : Font.PLAIN, size);
}

function argbToColor(argb) {
    return new Color((argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF);
}

const dhBase = new DisplayHelper(buildSlotConfig());

function create(ctx, state, train) {
    state.dh = dhBase.create();
    state.lastSignature = null;
}

function computeSignature(train) {
    const stations = collectStations(train);
    let nextIndex = 0;
    try {
        nextIndex = train.getAllPlatformsNextIndex();
    } catch (e) {
        nextIndex = 0;
    }
    let speedBucket = 0;
    let distanceBucket = 0;
    try {
        speedBucket = Math.round(train.speed() * 20 * 3.6 * 2);
        distanceBucket = Math.round(train.getRailProgress(0));
    } catch (e) {
    }
    return train.isOnRoute() + "|" + (train.doorValue() > 0) + "|" + nextIndex + "|" + destinationOf(train)
        + "|" + speedBucket + "|" + distanceBucket + "|" + stations.join(",");
}

function dispose(ctx, state, train) {
    if (state.dh != null) {
        state.dh.close();
    }
}

function collectStations(train) {
    const platforms = train.getAllPlatforms();
    const list = [];
    if (platforms == null) {
        return list;
    }
    for (let i = 0; i < platforms.size(); i++) {
        const p = platforms.get(i);
        let name = "";
        if (p != null) {
            try {
                name = p.stationName();
            } catch (e) {
                name = "";
            }
        }
        list.push(name == null ? "" : name);
    }
    return list;
}

function routeColorOf(train) {
    const platforms = train.getAllPlatforms();
    if (platforms != null && platforms.size() > 0 && platforms.get(0) != null && platforms.get(0).route != null) {
        return argbToColor(platforms.get(0).route.color);
    }
    return new Color(0x19, 0x76, 0xD2);
}

function destinationOf(train) {
    const platforms = train.getAllPlatforms();
    if (platforms != null && platforms.size() > 0) {
        const last = platforms.get(platforms.size() - 1);
        if (last != null) {
            try {
                const d = last.destinationName;
                if (d != null && d.length() > 0) {
                    return d;
                }
                return last.stationName();
            } catch (e) {
                return "";
            }
        }
    }
    return "";
}

function splitName(raw) {
    if (raw == null) {
        return ["", ""];
    }
    const idx = raw.indexOf("|");
    if (idx < 0) {
        return [raw, ""];
    }
    return [raw.substring(0, idx), raw.substring(idx + 1)];
}

function fitText(g, text, maxWidth) {
    if (text == null || text.length == 0) {
        return "";
    }
    if (g.getFontMetrics().stringWidth(text) <= maxWidth) {
        return text;
    }
    let out = text;
    while (out.length > 1 && g.getFontMetrics().stringWidth(out + "…") > maxWidth) {
        out = out.substring(0, out.length - 1);
    }
    return out + "…";
}

function fmt1(v) {
    return (Math.round(v * 10) / 10).toFixed(1);
}

function safeCall(fn, fallback) {
    try {
        const v = fn();
        return v == null ? fallback : v;
    } catch (e) {
        return fallback;
    }
}

function drawPanel(g, train) {
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, PANEL_W, PANEL_H);

    const routeColor = routeColorOf(train);
    const stations = collectStations(train);
    const platforms = train.getAllPlatforms();
    const onRoute = train.isOnRoute();
    const doorOpen = train.doorValue() > 0;
    const reversed = safeCall(function () { return train.isReversed(); }, false);
    const speedKph = safeCall(function () { return train.speed() * 20 * 3.6; }, 0);
    const nextIndex = safeCall(function () { return train.getAllPlatformsNextIndex(); }, 0);
    const routeName = safeCall(function () { return train.routeName(); }, "");
    const toNext = safeCall(function () { return train.distanceToNextPlatform(); }, -1);

    g.setColor(routeColor);
    g.fillRect(0, 0, PANEL_W, 12);
    g.fillRect(0, PANEL_H - 12, PANEL_W, 12);

    if (stations.length == 0) {
        g.setColor(new Color(0x55, 0x55, 0x55));
        g.setFont(pickFont(34, false));
        g.drawString("无有效路线  No Route", 40, 62);
        g.setColor(new Color(0x88, 0x88, 0x88));
        g.setFont(pickFont(40, false));
        g.drawString("请在铁路仪表板中设置路线并清除列车", 40, 170);
        return;
    }

    const safeIndex = Math.min(Math.max(nextIndex, 0), stations.length - 1);

    let header;
    if (!onRoute) {
        header = "等待发车  Waiting for departure";
    } else if (nextIndex >= stations.length) {
        header = "回厂  Returning to depot";
    } else if (doorOpen) {
        header = "本站  This station";
    } else {
        header = "下一站  Next station";
    }

    if (routeName.length > 0) {
        g.setColor(routeColor);
        g.setFont(pickFont(30, true));
        const rw = g.getFontMetrics().stringWidth(routeName);
        g.fillRect(34, 32, rw + 24, 40);
        g.setColor(Color.WHITE);
        g.drawString(routeName, 46, 62);
        g.setColor(new Color(0x55, 0x55, 0x55));
        g.setFont(pickFont(34, false));
        g.drawString(header, 46 + rw + 24, 62);
    } else {
        g.setColor(new Color(0x55, 0x55, 0x55));
        g.setFont(pickFont(34, false));
        g.drawString(header, 40, 62);
    }

    const mainName = splitName(stations[safeIndex]);
    g.setColor(Color.BLACK);
    g.setFont(pickFont(84, true));
    g.drawString(fitText(g, mainName[0], LEFT_W), 40, 150);
    if (mainName[1].length > 0) {
        g.setColor(new Color(0x44, 0x44, 0x44));
        g.setFont(pickFont(36, false));
        g.drawString(fitText(g, mainName[1], LEFT_W), 40, 192);
    }

    const info = platforms != null && safeIndex < platforms.size() ? platforms.get(safeIndex) : null;
    const platformName = info == null ? "" : safeCall(function () { return info.platformName(); }, "");
    let sub = "终点方向 For " + splitName(destinationOf(train))[0];
    if (platformName.length > 0) {
        sub = sub + "   站台 " + platformName;
    }
    g.setColor(new Color(0x77, 0x77, 0x77));
    g.setFont(pickFont(26, false));
    g.drawString(fitText(g, sub, LEFT_W), 40, 224);

    const statusParts = [];
    statusParts.push(fmt1(speedKph) + " km/h");
    if (toNext >= 0 && !doorOpen) {
        statusParts.push("距下站 " + Math.round(toNext) + " m");
    }
    if (doorOpen) {
        const left = safeCall(function () { return train.doorLeftOpen[0]; }, false);
        const right = safeCall(function () { return train.doorRightOpen[0]; }, false);
        statusParts.push(left && right ? "两侧开门" : left ? "左侧开门 Left" : right ? "右侧开门 Right" : "开门中");
    }
    if (reversed) {
        statusParts.push("反向运行");
    }
    statusParts.push(train.trainCars() + " 节编组");
    g.setColor(new Color(0x99, 0x99, 0x99));
    g.setFont(pickFont(22, false));
    g.drawString(fitText(g, statusParts.join("  ·  "), LEFT_W), 40, 256);

    const stripY = 246;
    const stripLeft = 760;
    const stripRight = PANEL_W - 60;
    const count = stations.length;
    const step = count > 1 ? (stripRight - stripLeft) / (count - 1) : 0;

    g.setColor(new Color(0xCC, 0xCC, 0xCC));
    g.fillRect(stripLeft, stripY - 4, stripRight - stripLeft, 8);
    if (count > 1 && safeIndex > 0) {
        g.setColor(routeColor);
        g.fillRect(stripLeft, stripY - 4, Math.round(step * safeIndex), 8);
    }

    for (let i = 0; i < count; i++) {
        const cx = count > 1 ? Math.round(stripLeft + step * i) : stripLeft;
        if (i < safeIndex) {
            g.setColor(new Color(0xAA, 0xAA, 0xAA));
        } else if (i == safeIndex) {
            g.setColor(new Color(0xE5, 0x39, 0x35));
        } else {
            g.setColor(routeColor);
        }
        g.fillOval(cx - 11, stripY - 11, 22, 22);
        g.setColor(Color.WHITE);
        g.fillOval(cx - 5, stripY - 5, 10, 10);

        const nm = splitName(stations[i]);
        g.setColor(i == safeIndex ? new Color(0xE5, 0x39, 0x35) : Color.BLACK);
        g.setFont(pickFont(24, i == safeIndex));
        const lw = g.getFontMetrics().stringWidth(nm[0]);
        g.drawString(nm[0], cx - lw / 2, stripY - 46);
        if (nm[1].length > 0) {
            g.setFont(pickFont(17, false));
            g.setColor(i == safeIndex ? new Color(0xE5, 0x39, 0x35) : new Color(0x88, 0x88, 0x88));
            const lw2 = g.getFontMetrics().stringWidth(nm[1]);
            g.drawString(nm[1], cx - lw2 / 2, stripY - 26);
        }

        if (i == 0 || i == count - 1) {
            g.setColor(new Color(0x99, 0x99, 0x99));
            g.setFont(pickFont(18, false));
            const tag = i == 0 ? "起点" : "终点";
            const tw = g.getFontMetrics().stringWidth(tag);
            g.drawString(tag, cx - tw / 2, stripY + 34);
        }
    }
}

function render(ctx, state, train) {
    for (let i = 0; i < train.trainCars(); i++) {
        ctx.drawCarModel(state.dh.model, i, null);
    }

    if (!train.shouldRender() || !train.shouldRenderDetail()) {
        return;
    }

    const signature = computeSignature(train);
    if (signature === state.lastSignature) {
        return;
    }
    state.lastSignature = signature;

    try {
        drawPanel(state.dh.graphicsFor("left"), train);
        drawPanel(state.dh.graphicsFor("right"), train);
        state.dh.upload();
    } catch (e) {
        print("[RTE LCD] render error: " + e);
    }
}

function getCubeVertices(p1, p2, center, rx, ry, rz) {
    const rxRad = rx * Math.PI / 180;
    const ryRad = ry * Math.PI / 180;
    const rzRad = rz * Math.PI / 180;
    const c = new Vector3f(center[0], center[1], center[2]);

    function build(a, b, d, e) {
        const arr = [a, b, d, e];
        const out = [];
        for (let i = 0; i < 4; i++) {
            const v = new Vector3f(arr[i][0], arr[i][1], arr[i][2]);
            v.sub(c);
            v.rotX(rxRad);
            v.rotY(ryRad);
            v.rotZ(rzRad);
            v.add(c);
            out.push([v.x(), v.y(), v.z()]);
        }
        return out;
    }

    if (p1[1] == p2[1]) {
        return build([p1[0], p1[1], p1[2]], [p2[0], p1[1], p1[2]], [p2[0], p2[1], p2[2]], [p1[0], p2[1], p2[2]]);
    } else if (p1[0] == p2[0]) {
        return build([p1[0], p1[1], p1[2]], [p1[0], p2[1], p1[2]], [p2[0], p2[1], p2[2]], [p2[0], p1[1], p2[2]]);
    } else {
        return build([p1[0], p1[1], p1[2]], [p2[0], p1[1], p2[2]], [p2[0], p2[1], p2[2]], [p1[0], p2[1], p1[2]]);
    }
}
