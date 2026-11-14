import argparse
import hashlib
import json
import shutil
import urllib.parse
import urllib.request
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]


def versions(project, minecraft):
    query = urllib.parse.urlencode({"game_versions": json.dumps([minecraft])})
    request = urllib.request.Request(
        f"https://api.modrinth.com/v2/project/{project}/version?{query}",
        headers={"User-Agent": "Rollingcats-Transit-Expansion-CI/1.0"},
    )
    with urllib.request.urlopen(request) as response:
        return json.load(response)


def resolve(project, minecraft, loader, version_number):
    for version in versions(project, minecraft):
        if version["version_number"] == version_number and loader in version["loaders"]:
            files = version["files"]
            return next((file for file in files if file.get("primary")), files[0])
    raise RuntimeError(f"No {project} {version_number} {loader} artifact for Minecraft {minecraft}")


def download(file, destination):
    destination.parent.mkdir(parents=True, exist_ok=True)
    sha512 = file["hashes"]["sha512"]
    if destination.is_file() and hashlib.sha512(destination.read_bytes()).hexdigest() == sha512:
        print(f"ready {destination.relative_to(ROOT)}")
        return
    temporary = destination.with_suffix(destination.suffix + ".part")
    request = urllib.request.Request(file["url"], headers={"User-Agent": "Rollingcats-Transit-Expansion-CI/1.0"})
    with urllib.request.urlopen(request) as response, temporary.open("wb") as output:
        shutil.copyfileobj(response, output)
    if hashlib.sha512(temporary.read_bytes()).hexdigest() != sha512:
        temporary.unlink(missing_ok=True)
        raise RuntimeError(f"Checksum mismatch for {file['filename']}")
    temporary.replace(destination)
    print(f"downloaded {destination.relative_to(ROOT)}")


def write_pom(destination, group, artifact, version):
    if destination.is_file():
        return
    destination.write_text(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        "<project xmlns=\"http://maven.apache.org/POM/4.0.0\">\n"
        "  <modelVersion>4.0.0</modelVersion>\n"
        f"  <groupId>{group}</groupId>\n"
        f"  <artifactId>{artifact}</artifactId>\n"
        f"  <version>{version}</version>\n"
        "</project>\n",
        encoding="utf-8",
    )


MODERN = {
    "1.21.1": {"mtr": "4.1.0-beta.2", "loaders": ("fabric", "neoforge"), "msd": None},
}


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--minecraft", default="1.20.1")
    parser.add_argument("--mtr", default=None)
    parser.add_argument("--msd", default="1.4.5")
    args = parser.parse_args()
    modern = MODERN.get(args.minecraft)
    if args.mtr is None:
        args.mtr = modern["mtr"] if modern else "4.0.5"
    loaders = modern["loaders"] if modern else ("fabric", "forge")
    if modern:
        args.msd = modern["msd"]
    libs = ROOT / "libs"
    coordinate_version = args.mtr if args.minecraft == "1.20.1" else f"{args.mtr}-{args.minecraft}"
    expected = []
    for loader in loaders:
        artifact = f"MTR-{loader}"
        directory = libs / "repo" / "local" / "mtr" / artifact / coordinate_version
        expected.extend([
            directory / f"{artifact}-{coordinate_version}.jar",
            directory / f"{artifact}-{coordinate_version}.pom",
        ])
        if args.msd:
            expected.append(libs / f"MSD-{loader}-{args.minecraft}-{args.mtr}-{args.msd}.jar")
    if all(path.is_file() and path.stat().st_size > 0 for path in expected):
        for path in expected:
            print(f"ready {path.relative_to(ROOT)}")
        return
    for loader in loaders:
        artifact = f"MTR-{loader}"
        file = resolve(
            "minecraft-transit-railway",
            args.minecraft,
            loader,
            f"{loader.upper()}-{args.mtr}+{args.minecraft}",
        )
        directory = libs / "repo" / "local" / "mtr" / artifact / coordinate_version
        jar = directory / f"{artifact}-{coordinate_version}.jar"
        download(file, jar)
        write_pom(directory / f"{artifact}-{coordinate_version}.pom", "local.mtr", artifact, coordinate_version)
        if not args.msd:
            continue
        msd_file = resolve(
            "station-decoration",
            args.minecraft,
            loader,
            f"{args.mtr}-{args.msd}",
        )
        download(msd_file, libs / msd_file["filename"])


if __name__ == "__main__":
    main()
