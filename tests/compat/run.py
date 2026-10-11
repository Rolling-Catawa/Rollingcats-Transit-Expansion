"""Compile current compatibility sources against isolated fixtures, not Minecraft.

Usage: python tests/compat/run.py --java-home <JDK 17+ directory>
No game files or existing worlds are modified.
"""
import argparse
from pathlib import Path
import shutil
import subprocess
import tempfile

parser = argparse.ArgumentParser()
parser.add_argument('--java-home', type=Path, required=True)
args = parser.parse_args()
repo = Path(__file__).resolve().parents[2]
fixtures = Path(__file__).resolve().parent / 'fixtures'
with tempfile.TemporaryDirectory(prefix='rte-compat-') as directory:
    root = Path(directory)
    shutil.copytree(fixtures, root, dirs_exist_ok=True)
    for name in ('CompatArbiter', 'JcmCompat'):
        target = root / 'cn/jstxjf_/rte/util' / (name + '.java')
        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(repo / 'common/src/main/java' / target.relative_to(root), target)
    suffix = '.exe' if (args.java_home / 'bin/java.exe').exists() else ''
    sources = sorted(root.rglob('*.java'))
    argfile = root / 'sources.txt'
    argfile.write_text('\n'.join('"' + str(p).replace('\\', '/') + '"' for p in sources), encoding='utf-8')
    subprocess.run([str(args.java_home / ('bin/javac' + suffix)), '--release', '17',
                    '-encoding', 'UTF-8', '-d', str(root / 'out'), '@' + str(argfile)], check=True)
    for scenario in ('present', 'absent'):
        subprocess.run([str(args.java_home / ('bin/java' + suffix)), '-cp', str(root / 'out'),
                        'CompatTest', scenario], check=True)
forbidden = ('InitClient.REGISTRY_CLIENT.eventRegistryClient.', 'Init.REGISTRY.eventRegistry.')
offenders = []
for module in ('common', 'fabric', 'forge'):
    for source in sorted((repo / module / 'src').rglob('*.java')):
        text = source.read_text(encoding='utf-8')
        for line_number, line in enumerate(text.splitlines(), 1):
            if any(pattern in line for pattern in forbidden):
                offenders.append(f'{source.relative_to(repo)}:{line_number}: {line.strip()}')
if offenders:
    print('FAIL: MTR registry events are single-slot on Forge; register on RTE.REGISTRY or use platform events')
    print('\n'.join(offenders))
    raise SystemExit(1)
print('PASS: no event listeners registered on MTR registries')
modify_args = []
for module in ('common', 'fabric', 'forge'):
    for source in sorted((repo / module / 'src').rglob('*.java')):
        for line_number, line in enumerate(source.read_text(encoding='utf-8').splitlines(), 1):
            if '@ModifyArgs' in line:
                modify_args.append(f'{source.relative_to(repo)}:{line_number}: {line.strip()}')
if modify_args:
    print('FAIL: @ModifyArgs synthetic Args classes cannot load on Forge; use @ModifyArg with the full argument list')
    print('\n'.join(modify_args))
    raise SystemExit(1)
print('PASS: no @ModifyArgs injectors')
