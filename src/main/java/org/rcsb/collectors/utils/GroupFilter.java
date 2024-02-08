/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.collectors.utils;

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
public class GroupFilter {

    private final Set<String> filter;

    public GroupFilter(List<String> filter){
        this.filter = new HashSet<>(filter);
    }

    public boolean contains(Document alignment){
        return filter.contains( alignment.getString(SchemaConstants.Field.TARGET_ID) );
    }

}
