'use strict';

var traverse = function (schema, opts, cb) {

    cb = opts.cb || cb;
    var pre = (typeof cb == 'function') ? cb : cb.pre || function() {};
    var post = cb.post || function() {};

    _traverse(opts, pre, post, schema, '', schema);
};

traverse.keywords = {
    additionalItems: true,
    items: true,
    contains: true,
    additionalProperties: true,
    propertyNames: true,
    not: true
};

traverse.arrayKeywords = {
    items: true,
    allOf: true,
    anyOf: true,
    oneOf: true
};

traverse.propsKeywords = {
    definitions: true,
    properties: true,
    patternProperties: true,
    dependencies: true
};

traverse.skipKeywords = {
    default: true,
    enum: true,
    const: true,
    required: true,
    maximum: true,
    minimum: true,
    exclusiveMaximum: true,
    exclusiveMinimum: true,
    multipleOf: true,
    maxLength: true,
    minLength: true,
    pattern: true,
    format: true,
    maxItems: true,
    minItems: true,
    uniqueItems: true,
    maxProperties: true,
    minProperties: true
};

function _traverse(opts, pre, post, schema, jsonPtr, rootSchema) {

    if (schema && typeof schema == 'object' && !Array.isArray(schema)) {
        pre(opts, schema, jsonPtr, rootSchema);
        for (var key in schema) {
            var jp;
            var sch = schema[key];
            if (Array.isArray(sch)) {
                if (key in traverse.arrayKeywords) {
                    for (var i=0; i<sch.length; i++) {
                        jp = jsonPtr + '/' + key + '/' + i;
                        _traverse(opts, pre, post, sch[i], jp, rootSchema);
                    }
                }
            } else if (key in traverse.propsKeywords) {
                if (sch && typeof sch == 'object') {
                    for (var prop in sch) {
                        opts.lineage.push(prop);
                        jp = jsonPtr + '/' + key + '/' + escapeJsonPtr(prop);
                        _traverse(opts, pre, post, sch[prop], jp, rootSchema);
                        opts.lineage.pop();
                    }
                }
            } else if (key in traverse.keywords || (opts.allKeys && !(key in traverse.skipKeywords))) {
                jp = jsonPtr + '/' + key;
                _traverse(opts, pre, post, sch, jp, rootSchema, jsonPtr);
            }
        }
        post(opts, schema, jsonPtr, rootSchema);
    }
}

function get_fully_qualified_name(lineage) {
    return lineage.join(".");
}

function get_type(schema) {

    if (schema.hasOwnProperty("type")) {
        var type = schema["type"];
        if (type === "string" && schema.hasOwnProperty("format"))
            return "date";
        return type;
    }
}

function get_description(schema) {

    var description = '';
    if (schema.hasOwnProperty("rcsb_description")) {
        var desc_arr = schema.rcsb_description;
        var desc_o = desc_arr.find(function (o) { return o.context === "dictionary"});
        if (typeof desc_o == 'object')
            description = desc_o.text;
    }
    if (description === '' && schema.hasOwnProperty("description")) {
        description = schema.description;
    }
    return description;
}

function get_attribute_description(opts, schema) {
    if (schema.hasOwnProperty("type") &&
        ( schema.type === "string" || schema.type === "integer"
            || schema.type === "number") || schema.type === "boolean") {
        var field_name = get_fully_qualified_name(opts.lineage);
        var description = get_description(schema);
        opts.attributes.push({field_name: field_name, description: description});
    }
}

function escapeJsonPtr(str) {
    return str.replace(/~/g, '~0').replace(/\//g, '~1');
}
