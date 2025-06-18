/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.rcsbsequencecoordinates.collectors.utils;

import org.bson.Document;
import org.rcsb.graphqlschema.schema.SchemaConstants;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/7/24, Wednesday
 **/
public class GroupFilterOperator {

    private final Set<String> filter;

    public GroupFilterOperator(List<String> filter){
        if(filter == null || filter.isEmpty())
            this.filter = null;
        else
            this.filter = new HashSet<>(filter);
    }

    public boolean contains(Document alignment){
        if(filter == null)
            return true;
        return filter.contains( alignment.getString(SchemaConstants.Field.TARGET_ID) );
    }

}
