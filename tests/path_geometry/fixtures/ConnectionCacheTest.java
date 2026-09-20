import cn.jstxjf_.rte.scripting.compat.*;
import cn.jstxjf_.rte.scripting.engine.*;
import java.util.*;
import java.nio.file.Path;
public class ConnectionCacheTest {
    public static class Train {
        public List<PathDataCompat> values=new ArrayList<>();
        public List<PathDataCompat> path(){return values;}
        public Object rte$getPathSource(){return values;}
    }
    private static void call(ScriptBackend backend,String fn,Object state,Train train)throws Exception{
        backend.invoke(List.of(backend.getFunction(fn)),new Object[]{null,state,train});
    }
    public static void main(String[] args)throws Exception {
        for(ScriptBackend backend:List.of(new RhinoScriptBackend(s->true),new GraalScriptBackend(s->true,Path.of(".").toAbsolutePath()))) {
            try(backend){
                backend.eval(LegacyConnectionCache.SOURCE,"test:hook");
                backend.eval("""
                    function create(ctx,state,train){state.ConnInfo={HalfCarLength:10,pathDatas:[],pathLength:[]};state.lcd={ticks:123};}
                    function first(ctx,state,train){if(state.ConnInfo.pathLength.length!==2||state.ConnInfo.pathLength[1]!==30)throw Error('length rebuild');if(state.ConnInfo.pathDatas.length!==2||state.ConnInfo.pathDatas[1].endDistance!==30)throw Error('Java List array access');if(state.ConnInfo.pathDatas[0].rail.railType!='PLATFORM'||state.ConnInfo.pathDatas[0].rail.railType.speedLimit!==80)throw Error('RailType compatibility');state.saved=state.ConnInfo.pathLength;}
                    function same(ctx,state,train){if(state.saved!==state.ConnInfo.pathLength)throw Error('unnecessary rebuild');}
                    function changed(ctx,state,train){if(state.ConnInfo.pathLength[0]!==12||state.ConnInfo.pathLength[1]!==35)throw Error('same-size replacement');if(state.lcd.ticks!==123||state.ConnInfo.HalfCarLength!==10)throw Error('unrelated state reset');}
                    ""","test:checks");
                Train train=new Train();
                train.values.add(PathDataCompat.synthetic(10,80,true,0,0,0,0,10));
                train.values.add(PathDataCompat.synthetic(20,80,false,0,0,0,10,30));
                Object state=backend.newStateObject();
                call(backend,"create",state,train);
                call(backend,LegacyConnectionCache.FUNCTION_NAME,state,train);call(backend,"first",state,train);
                call(backend,LegacyConnectionCache.FUNCTION_NAME,state,train);call(backend,"same",state,train);
                train.values=new ArrayList<>(List.of(PathDataCompat.synthetic(12,80,false,0,0,0,0,12),PathDataCompat.synthetic(23,80,false,0,0,0,12,35)));
                call(backend,LegacyConnectionCache.FUNCTION_NAME,state,train);call(backend,"changed",state,train);
                System.out.println("PASS: legacy connection cache on "+backend.engineName()+" (refresh, stable reuse, same-size replacement, LCD preservation)");
            }
        }
    }
}
