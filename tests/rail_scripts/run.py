"""Offline tests for rail script logic classes that do not depend on Minecraft."""
from pathlib import Path
import os, subprocess, tempfile, shutil, sys

repo = Path(__file__).resolve().parents[2]
SOURCES = [
    'util/RailEditRules',
    'mechanism/RailPose',
    'mechanism/MechanismTransform',
    'mechanism/RailScriptStore',
    'mechanism/Mechanism',
    'mechanism/MechanismRegistry',
    'mechanism/MechanismGate',
    'mechanism/MechanismEvent',
    'mechanism/MessageRateLimiter',
    'mechanism/TrainProximityTracker',
    'mechanism/RailScriptPolicy',
]
cache = Path.home() / '.gradle/caches/modules-2/files-2.1'
libs = []
for group in ['com.google.code.gson']:
    libs += [p for p in (cache / group).rglob('*.jar') if not p.name.endswith('-sources.jar')]
java_home = None
if '--java-home' in sys.argv:
    java_home = Path(sys.argv[sys.argv.index('--java-home') + 1])
javac = str(java_home / 'bin' / 'javac') if java_home else 'javac'
java = str(java_home / 'bin' / 'java') if java_home else 'java'
with tempfile.TemporaryDirectory(prefix='rte-rail-scripts-') as tmp:
    root = Path(tmp)
    shutil.copytree(Path(__file__).parent / 'fixtures', root / 'src', dirs_exist_ok=True)
    for name in SOURCES:
        target = root / 'src/cn/jstxjf_/rte' / f'{name}.java'
        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(repo / 'common/src/main/java/cn/jstxjf_/rte' / f'{name}.java', target)
    cp = os.pathsep.join(map(str, [root / 'out', *libs]))
    args = root / 'sources.txt'
    args.write_text('\n'.join('"' + s.as_posix() + '"' for s in (root / 'src').rglob('*.java')), encoding='utf-8')
    subprocess.run([javac, '--release', '17', '-encoding', 'UTF-8', '-cp', cp, '-d', str(root / 'out'), '@' + str(args)], check=True)
    subprocess.run([java, '-cp', cp, 'RailScriptsTest'], check=True, cwd=root)
