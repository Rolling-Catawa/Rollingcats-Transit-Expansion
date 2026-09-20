import cn.jstxjf_.rte.scripting.compat.*;
import cn.jstxjf_.rte.network.PacketVehiclePath;
import cn.jstxjf_.rte.render.train.VehiclePathCache;
import org.mtr.core.data.*;
import org.mtr.core.tool.Angle;
import org.mtr.libraries.it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.mtr.mapping.tool.*;
import java.util.*;

public class PathGeometryTest {
    private static int checks;
    private static void check(boolean ok,String label){checks++;if(!ok)throw new AssertionError(label);}
    private static void near(double a,double b,String label){check(Double.isFinite(a)&&Math.abs(a-b)<1e-6,label+": "+a+" != "+b);}
    private static void point(ScriptVector a, org.mtr.core.tool.Vector b,String label){near(a.x,b.x,label+" x");near(a.y,b.y,label+" y");near(a.z,b.z,label+" z");}
    private static PacketVehiclePath roundtrip(PacketVehiclePath p){PacketBufferSender s=new PacketBufferSender();p.write(s);return new PacketVehiclePath(new PacketBufferReceiver(s.bytes()));}
    private static void reject(Runnable r,String label){boolean rejected=false;try{r.run();}catch(IllegalArgumentException e){rejected=true;}check(rejected,label);}
    public static void main(String[] args) {
        PacketBufferSender oldRequest=new PacketBufferSender();oldRequest.writeLong(99);
        var request=new cn.jstxjf_.rte.network.PacketRequestVehiclePath(new PacketBufferReceiver(oldRequest.bytes()));
        PacketBufferSender negotiated=new PacketBufferSender();request.write(negotiated);
        PacketBufferReceiver legacyFields=new PacketBufferReceiver(negotiated.bytes());
        check(legacyFields.readLong()==99&&!legacyFields.readBoolean(), "old request gets legacy response capability");
        PacketBufferSender newRequest=new PacketBufferSender();new cn.jstxjf_.rte.network.PacketRequestVehiclePath(99).write(newRequest);
        PacketBufferReceiver modernFields=new PacketBufferReceiver(newRequest.bytes());
        check(modernFields.readLong()==99&&modernFields.readBoolean(), "new request opts into geometry");
        PathData example=null;
        for(boolean curve:new boolean[]{false,true})for(boolean reverse:new boolean[]{false,true}){
            Position a=new Position(100,32,200),b=new Position(curve?140:100,36,240);
            Rail rail=Rail.newRail(a,Angle.S,b,curve?Angle.W:Angle.N,Rail.Shape.QUADRATIC,16,new ObjectArrayList<>(),80,60,true,false,false,true,false,TransportMode.TRAIN);
            check(rail.isValid(),"valid test rail");
            Position start=reverse?b:a,end=reverse?a:b;
            PathData original=new PathData(new PathData(rail,5678,20000,3,start,end),900,900+rail.railMath.getLength());
            PathData decoded=PathGeometryCodec.decode(PathGeometryCodec.encode(original));
            PathDataCompat compat=new PathDataCompat(decoded);
            check(decoded.reversePositions==reverse,"direction retained");
            check(decoded.getSavedRailBaseId()==5678&&decoded.getStopIndex()==3&&decoded.getDwellTime()==20000,"stop metadata retained");
            check(compat.dwellTime==40&&compat.dwellTimeMillis==20000,"legacy dwell half-seconds");
            check(compat.startingPos.equals(start)&&compat.endingPos.equals(end),"legacy directed path endpoints");
            near(decoded.getStartDistance(),900,"distance retained");
            near(compat.rail.getLength(),rail.railMath.getLength(),"length retained");
            for(double fraction:new double[]{0,.25,.5,.75,1}){
                double local=rail.railMath.getLength()*fraction;
                point(compat.rail.getPosition(local),original.getPosition(local),"directed rail sample");
                point(compat.getPosition(local),original.getPosition(local),"path sample");
                point(compat.rail.getPosition(local,false),rail.railMath.getPosition(local,false),"explicit canonical direction");
            }
            if(curve&&!reverse){
                var mid=compat.rail.getPosition(rail.railMath.getLength()/2);
                check(Math.abs(mid.x-(a.getX()+b.getX()+1)/2.0)>1,"curve not replaced by linear chord");
            }
            example=original;
        }
        String json=PathGeometryCodec.encode(example);
        var segment=new PacketVehiclePath.Segment(example.getRailLength(),60,false,20000,5678,example.getStartDistance(),example.getEndDistance(),json);
        roundtrip(new PacketVehiclePath(10,List.of(segment),true)).runClient();
        var first=VehiclePathCache.getGeometry(10);
        check(first!=null&&first.get(0).data!=null,"new packet supplies geometry");
        point(first.get(0).rail.getPosition(5),example.getPosition(5),"network geometry position");
        roundtrip(new PacketVehiclePath(10,List.of(segment),true)).runClient();
        check(first==VehiclePathCache.getGeometry(10),"identical response reuses snapshot");
        roundtrip(new PacketVehiclePath(11,List.of(segment))).runClient();
        check(VehiclePathCache.get(11)!=null,"old response retains station metadata");
        check(VehiclePathCache.getGeometry(11)==null,"metadata never used for geometry");
        var invalid=new PacketVehiclePath.Segment(20,80,false,0,0,0,20,"not json");
        roundtrip(new PacketVehiclePath(12,List.of(invalid),true)).runClient();
        check(VehiclePathCache.get(12)!=null&&VehiclePathCache.getGeometry(12)==null,"bad geometry fallback");
        roundtrip(new PacketVehiclePath(13,List.of(segment,invalid),true)).runClient();
        check(VehiclePathCache.getGeometry(13)==null,"partial geometry not handed to scripts");
        for(int n:new int[]{2049,Integer.MAX_VALUE,Integer.MIN_VALUE}){
            PacketBufferSender s=new PacketBufferSender();s.writeLong(1);s.writeInt(n);
            reject(()->new PacketVehiclePath(new PacketBufferReceiver(s.bytes())),"reject oversized path count");
        }
        PacketBufferSender s=new PacketBufferSender();s.writeLong(1);s.writeInt(-2);
        s.writeDouble(20);s.writeLong(80);s.writeBoolean(false);s.writeLong(0);s.writeLong(0);s.writeDouble(0);s.writeDouble(20);s.writeInt(8193);
        reject(()->new PacketVehiclePath(new PacketBufferReceiver(s.bytes())),"reject geometry before allocation");
        List<PathData> source=new ArrayList<>(List.of(example));
        var view=PathDataCompat.liveView(()->source);
        var old=view.get(0);
        source.set(0,PathGeometryCodec.decode(json));
        check(view.get(0)!=old,"same-size in-place path replacement");
        source.add(example);
        check(view.size()==2&&view.get(1).data==example,"in-place path growth");
        VehiclePathCache.retainAll(Set.of());
        check(VehiclePathCache.getGeometry(10)==null&&VehiclePathCache.get(11)==null,"cache cleanup");
        roundtrip(new PacketVehiclePath(14,List.of(segment),true)).runClient();
        VehiclePathCache.clear();
        check(VehiclePathCache.get(14)==null,"explicit world/reload cache clear");
        System.out.println("PASS: "+checks+" path geometry/protocol/cache assertions (real MTR rail math; fixture packet transport)");
    }
}
