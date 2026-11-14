"""Test production path codec/cache against real MTR geometry, with network/game fixtures."""
from pathlib import Path
import os, subprocess, tempfile, shutil
repo=Path(__file__).resolve().parents[2]
jar=next((repo/'.gradle/loom-cache/remapped_mods/remapped/local/mtr/MTR-fabric-65f153da/4.0.5').glob('*.jar'))
logging=list((Path.home()/'.gradle/caches/modules-2/files-2.1/org.apache.logging.log4j').rglob('*.jar'))
logging+=list((Path.home()/'.gradle/caches/modules-2/files-2.1/org.slf4j/slf4j-api').rglob('*.jar'))
cache=Path.home()/'.gradle/caches/modules-2/files-2.1'
for group in ['org.mozilla', 'com.ibm.icu', *[p.name for p in cache.glob('org.graalvm*')]]:
    logging+=list((cache/group).rglob('*.jar'))
logging=[p for p in logging if not p.name.endswith("-sources.jar")]
with tempfile.TemporaryDirectory(prefix='rte-path-test-') as tmp:
    root=Path(tmp)
    (root/'logs').mkdir()
    shutil.copytree(Path(__file__).parent/'fixtures',root,dirs_exist_ok=True)
    for name in ('scripting/compat/PathGeometryCodec','scripting/compat/PathDataCompat','scripting/compat/RailCompat','scripting/engine/ScriptBackend','scripting/engine/RhinoScriptBackend','scripting/engine/GraalScriptBackend','scripting/compat/LegacyConnectionCache','network/PacketVehiclePath','render/train/VehiclePathCache','bridge/network/RtePacketReader','bridge/network/RtePacketWriter'):
        target=root/'cn/jstxjf_/rte'/f'{name}.java'
        target.parent.mkdir(parents=True,exist_ok=True)
        shutil.copyfile(repo/'common/src/main/java/cn/jstxjf_/rte'/f'{name}.java',target)
    request=repo/'common/src/main/java/cn/jstxjf_/rte/network/PacketRequestVehiclePath.java'
    text=request.read_text(encoding='utf-8-sig')
    text=text[:text.index('    @Override\n    protected void handleServer')]+'}\n'
    (root/'cn/jstxjf_/rte/network/PacketRequestVehiclePath.java').write_text(text,encoding='utf-8')
    cp=os.pathsep.join(map(str,[root/'out',jar,*logging]))
    args=root/'sources.txt'
    args.write_text('\n'.join('"'+str(s).replace('\\','/')+'"' for s in root.rglob('*.java')),encoding='utf-8')
    subprocess.run(['javac','--release','17','-encoding','UTF-8','-cp',cp,'-d',str(root/'out'),'@'+str(args)],check=True)
    subprocess.run(['java','-cp',cp,'PathGeometryTest'],check=True)
    subprocess.run(['java','-cp',cp,'ConnectionCacheTest'],check=True,cwd=root)
