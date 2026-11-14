# Compatibility regression

Run from the repository root:

```powershell
python tests/compat/run.py --java-home 'C:/Program Files/Eclipse Adoptium/jdk-21.0.11.10-hotspot'
```

Requires Python 3 and JDK 17 or newer; Gradle and Minecraft are not required.
The runner compiles the **current** `CompatArbiter.java` and `JcmCompat.java`
with `--release 17`. Fixtures replace the logger, client settings, platform
discovery and JCM API. Each installed/absent scenario uses a fresh JVM.
Temporary compiled files are removed automatically. Fixtures are outside
the production source sets and are not included in the mod.

## Result — 2026-09-05

32 assertions passed: 28 installed-addon assertions and 4 absent-addon
assertions. Coverage includes legacy migration, per-mod overrides,
configuration round-trip, healthy/failing/null scripts, distinct instance
classes, frame scan caching, reload invalidation, bridge backoff, recovery
after the retry deadline, log suppression and keeping RTE enabled when the
other mod is absent. The retry test advances the private deadline rather
than waiting five seconds; it separately checks the scheduled delay.

These are isolated logic tests, **not** an in-game compatibility pass.
The lifecycle test calls `reset()` directly, not Minecraft's F3+T handler.

Local JCM `2.2.4+1.20.1` bytecode was also inspected with `javap`:
`getScriptManager`, `getInstanceManager`, `getInstances`, `getScript`,
`duringFailCooldown`, `getCapturedScriptException` and the private `objs`
field match the bridge's assumptions. This validates API shape only.

## Blocked / not executed

- Full Fabric/Forge compilation: the configured Gradle 9.7.1 distribution
  is not completely cached. A diagnostic attempt with cached Gradle 8.12.1
  in offline mode failed at root `build.gradle:2`: Loom `1.17.491` could not
  be resolved. No Gradle/plugin versions were changed for this test.
- Game startup, actual F3+T, doors, floor alignment, rail rendering and
  node clicks: no game client was running, and a current build was not
  produced. Older compiled classes/JARs were not substituted as evidence.
- S1 `1.0.9+build.3` and FangSu `1.1.1-fix2`: local JAR identity/mixin
  metadata inspected; their world simulation and injection interactions
  remain untested.
- Dedicated-server/client arbitration consistency: remains unresolved in
  production code; no passing multiplayer result is claimed.

Local evidence from this run (generated, not shipped):
`build/compat-regression.txt`, `build/compat-addon-inventory.json`,
`build/compat-jcm-api.txt`.
