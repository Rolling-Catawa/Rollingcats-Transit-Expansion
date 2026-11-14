import argparse
import hashlib
import json
import shutil
import zipfile
from pathlib import Path


def properties(text):
    result = {}
    for line in text.splitlines():
        if line and not line.lstrip().startswith(("#", "!")) and "=" in line:
            key, value = line.split("=", 1)
            result[key.strip()] = value.strip()
    return result


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--input", required=True, type=Path)
    parser.add_argument("--output", required=True, type=Path)
    parser.add_argument("--version", required=True)
    parser.add_argument("--build-id", required=True)
    parser.add_argument("--commit", required=True)
    parser.add_argument("--build-time", required=True)
    parser.add_argument("--minecraft", required=True, action="append")
    args = parser.parse_args()

    expected = {
        f"rte-{platform}-{args.version}+{minecraft}.jar": (platform, minecraft)
        for minecraft in args.minecraft
        for platform in ("fabric", "forge")
    }
    jars = {path.name: path for path in args.input.glob("*.jar")}
    missing = sorted(set(expected) - set(jars))
    unexpected = sorted(set(jars) - set(expected))
    if missing or unexpected:
        raise SystemExit(f"Distribution mismatch; missing={missing}, unexpected={unexpected}")

    required = {
        "rte/rte-build.properties",
        "rte.mixins.json",
        "assets/rte/lang/en_us.json",
        "assets/rte/lang/zh_cn.json",
        "cn/jstxjf_/rte/util/RailCollisionManager.class",
        "cn/jstxjf_/rte/mixin/EntityMixin.class",
        "cn/jstxjf_/rte/render/RailCollisionPreviewRenderer.class",
    }
    args.output.mkdir(parents=True, exist_ok=True)
    for old in args.output.iterdir():
        if old.is_file():
            old.unlink()

    rows = []
    for name in sorted(expected):
        platform, minecraft = expected[name]
        source = jars[name]
        with zipfile.ZipFile(source) as archive:
            broken = archive.testzip()
            if broken:
                raise SystemExit(f"Corrupt entry {broken} in {name}")
            names = set(archive.namelist())
            absent = sorted(required - names)
            if absent:
                raise SystemExit(f"Missing entries in {name}: {absent}")
            for json_name in ("rte.mixins.json", "assets/rte/lang/en_us.json", "assets/rte/lang/zh_cn.json"):
                json.loads(archive.read(json_name).decode("utf-8"))
            stamp = properties(archive.read("rte/rte-build.properties").decode("utf-8"))
            checks = {
                "version": args.version,
                "buildId": args.build_id,
                "commit": args.commit[:12],
                "clean": "true",
                "buildTime": args.build_time,
                "platform": platform,
            }
            for key, value in checks.items():
                if stamp.get(key) != value:
                    raise SystemExit(f"{name}: {key}={stamp.get(key)!r}, expected {value!r}")
            metadata_name = "fabric.mod.json" if platform == "fabric" else "META-INF/mods.toml"
            if metadata_name not in names:
                raise SystemExit(f"Missing {metadata_name} in {name}")
            metadata = archive.read(metadata_name).decode("utf-8")
            marker = f'"minecraft": "~{minecraft}"' if platform == "fabric" else f'versionRange = "[{minecraft}]"'
            if marker not in metadata:
                raise SystemExit(f"Minecraft metadata mismatch in {name}")

        destination = args.output / name
        shutil.copy2(source, destination)
        digest = hashlib.sha256(destination.read_bytes()).hexdigest()
        rows.append((name, destination.stat().st_size, digest))

    lines = [
        f"RTE {args.version} | Minecraft {', '.join(args.minecraft)}",
        f"Build {args.build_id} | Commit {args.commit[:12]}",
        f"Built {args.build_time}",
        "-" * 78,
    ]
    lines.extend(f"{name:<48} {size / 1048576:8.1f} MB  {digest}" for name, size, digest in rows)
    (args.output / "CHECKSUMS.txt").write_text("\n".join(lines) + "\n", encoding="utf-8")
    print("\n".join(lines))


if __name__ == "__main__":
    main()
