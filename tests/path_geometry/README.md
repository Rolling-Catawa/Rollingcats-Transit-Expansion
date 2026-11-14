# Path / connection regression

Run `python tests/path_geometry/run.py` from the repository after a Fabric build, using JDK 17 or newer and the existing Gradle dependency cache.

The runner compiles current production PathGeometryCodec, PathDataCompat, RailCompat, PacketVehiclePath, VehiclePathCache, LegacyConnectionCache, and both script backends. Request read/write methods are extracted from the current PacketRequestVehiclePath; server world traversal is not exercised.

- Real MTR 4.0.5 RailMath: straight/curved, sloped, forward/reverse samples, explicit direction, metadata retention.
- Fixture packet transport: legacy/new responses, request capability, bounds, malformed geometry, cache reuse and cleanup.
- Real Rhino and Graal engines: legacy ConnInfo cache refresh, stable reuse, same-size replacement, unrelated LCD state preserved.

Minecraft rendering, multiplayer packet fragmentation, platform-side detection and visible door lamps still require in-game verification. No saves or resource packs are modified.
