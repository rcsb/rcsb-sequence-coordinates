package org.rcsb.graphqlschema.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.leangen.graphql.metadata.strategy.query.OperationInfoGenerator;
import io.leangen.graphql.metadata.strategy.query.OperationInfoGeneratorParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Introspector;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;

/**
 * A custom operation name generator based on the ideas discussed in https://github.com/leangen/graphql-spqr/issues/32
 * <p>
 * It will use the JsonProperty annotations to name fields and methods, or if absent it will do camel case to snake
 * case conversion.
 *
 *
 * @author Jose Duarte
 */
public class CustomOperationNameGenerator implements OperationInfoGenerator {

    private static final Logger logger = LoggerFactory.getLogger(CustomOperationNameGenerator.class);

    /**
     * Retrieve the field name from the getter Method (starting with "get" or "is")
     * @param method the getter method (starting with "get" or "is")
     * @return the decapitalized name
     */
    private static String getFieldNameFromGetter(Method method) {

        // implemented from ideas in : https://stackoverflow.com/questions/13192734/getting-a-property-field-name-using-getter-method-of-a-pojo-java-bean/13514566
        String name;
        String methodName = method.getName();

        if (methodName.startsWith("get")) {
            name = Introspector.decapitalize(methodName.substring(3));
        } else if (methodName.startsWith("is")) {
            name = Introspector.decapitalize(methodName.substring(2));
        } else {
            logger.warn("Method does not start with get or is, using the method name '{}' directly", methodName);
            name = methodName;
        }
        return GraphQLNameUtils.ensureNameIsLegal(name);
    }

    @Override
    public String name(OperationInfoGeneratorParams params) {

        // snake case name generator (from json annotations or doing snake casing on the fly)

        String name = null;
        if (params.getElement().isAnnotationPresent(JsonProperty.class)) {
            name = params.getElement().getAnnotation(JsonProperty.class).value();
        } else {
            for (AnnotatedElement e : params.getElement().getElements()) {
                if (e instanceof Field) {
                    Field field = (Field) e;
                    name = LOWER_CAMEL.to(LOWER_UNDERSCORE, field.getName());
                    break;
                } else if (e instanceof Method) {
                    Method method = (Method) e;
                    name = LOWER_CAMEL.to(LOWER_UNDERSCORE, getFieldNameFromGetter(method));
                    break;
                } else {
                    logger.warn("Element was neither Field nor Method, will return null for generateQueryName");
                }
            }
        }
        return name == null ? null : GraphQLNameUtils.ensureNameIsLegal(name);
    }

    @Override
    public String description(OperationInfoGeneratorParams params) {

        // graphql schema descriptions to be taken from JsonPropertyDescription annotations

        String description = null;
        if (params.getElement().isAnnotationPresent(JsonPropertyDescription.class))
            description = params.getElement().getAnnotation(JsonPropertyDescription.class).value();

        return description;
    }

    @Override
    public String deprecationReason(OperationInfoGeneratorParams params) {
        return null;
    }
}