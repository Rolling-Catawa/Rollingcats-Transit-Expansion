package cn.jstxjf_.rte.data;

import org.mtr.core.data.PathData;

import java.util.List;

public interface IRoute {
    List<PathData> getPathData();
    void setPathData(List<PathData> pathData);
}

