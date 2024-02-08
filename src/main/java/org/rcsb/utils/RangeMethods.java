/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.utils;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/5/24, Monday
 **/

public class RangeMethods {

    public static Range intersection(Range a, Range x){
        if(a.bottom() > x.top() || x.bottom() > x.top())
            return new Range(0,-1);
        return new Range(Math.max(x.bottom(), a.bottom()), Math.min(x.top(), a.top()));
    }

    public static int mapIndex(int query, Range queryRange, Range taretRange){
        if(!queryRange.contains(query) || queryRange.size() != taretRange.size())
            return -1;
        return (query - queryRange.bottom()) + taretRange.bottom();
    }

}
