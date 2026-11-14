package cn.jstxjf_.rte.mechanism;

public final class MechanismEventInfo {

    public final String type;
    public long train;
    public double distance;
    public long eta = -1;
    public String requiredRail = "";
    public String rail = "";
    public String nextStation = "";
    public String destination = "";
    public String routeName = "";
    public int x;
    public int y;
    public int z;
    public int oldPower;
    public int newPower;
    public String player = "";
    public String item = "";
    public String message = "";
    public String timer = "";
    public String from = "";
    public boolean aligned;

    public MechanismEventInfo(String type) {
        this.type = type;
    }
}
