package org.rcsb.utils;

public class RangeMethods {

    public static Range intersection(Range a, Range x){
        if(a.bottom() > x.top() || x.bottom() > x.top())
            return new Range(-1,0);
        return new Range(Math.max(x.bottom(), a.bottom()), Math.min(x.top(), a.top()));
    }

    public static int mapIndex(int query, Range queryRange, Range taretRange){
        if(!queryRange.contains(query) || queryRange.size() != taretRange.size())
            return -1;
        return (query - queryRange.bottom()) + taretRange.bottom();
    }

}
