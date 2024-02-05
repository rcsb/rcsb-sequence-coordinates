/*
 *  Copyright (c) 2024 - now, RCSB PDB and contributors, licensed under MIT, See LICENSE file for more info.
 */

package org.rcsb.graphqlschema.schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : joan
 * @mailto : joan.segura@rcsb.org
 * @created : 2/5/24, Monday
 **/

public class GraphQLNameUtils {

    private static final Logger logger = LoggerFactory.getLogger(GraphQLNameUtils.class);

    private GraphQLNameUtils() {}

    // Names in GraphQL are limited to this ASCII subset of possible characters: /[_A-Za-z][_0-9A-Za-z]*/.
    // See GraphQL <a href="https://graphql.github.io/graphql-spec/June2018/#Name">documentation</>.
    private static final String LEGAL_GRAPHQL_NAME_REGEX = "^[_A-Za-z][_0-9A-Za-z]*$";
    private static final String ILLEGAL_CHARACTER_SET_REGEX = "[^_0-9A-Za-z]";

    // Names cannot start with characters other than this subset of characters: /[_A-Za-z]/.
    private static final String ILLEGAL_FIRST_CHARACTER_REGEX = "[^_A-Za-z].*$";

    // Names prefixed with '__' are reserved for meta‐fields accessible in the schema introspection queries.
    // See GraphQL <a href="https://graphql.github.io/graphql-spec/June2018/#sec-Reserved-Names">Reserved Names</>
    // documentation.
    private static final String RESERVED_FOR_INTROSPECTION_REGEX = "^__[^_].*$";

    /**
     * Modifies name to be compliant with the GraphQL language specification.
     * See <a href="https://graphql.github.io/graphql-spec/June2018/#Name">GraphQL spec for names</>.
     *
     * @param name the name to be converted to legal GraphQL name.
     * @return name where characters, that are not allowed by GraphQL language, are replaced with '_'.
     */
    public static String ensureNameIsLegal(String name) {

        if (!name.matches(LEGAL_GRAPHQL_NAME_REGEX) || name.matches(RESERVED_FOR_INTROSPECTION_REGEX)) {

            String modifiedName = name.replaceAll(ILLEGAL_CHARACTER_SET_REGEX, "_");

            if (modifiedName.matches(RESERVED_FOR_INTROSPECTION_REGEX))
                modifiedName = "_" + modifiedName;

            if (modifiedName.matches(ILLEGAL_FIRST_CHARACTER_REGEX))
                modifiedName = "_" + modifiedName;

            logger.warn("Field name {} violates naming rules for GraphQL language. This name will be changed to {}.", name, modifiedName);
            return modifiedName;
        }
        return name;
    }
}
