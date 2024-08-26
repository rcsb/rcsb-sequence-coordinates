
function load(index, path, cb) {
    var oReq = new XMLHttpRequest();
    oReq.addEventListener("load", function() { cb(index, this.responseText); });
    oReq.open("GET", path);
    oReq.send();
}

function render_txt(index, text) {
    var container = document.getElementById(index);
    container.setAttribute("class", "example");
    var qiElement = document.createElement("pre");
    var codeElement = document.createElement("code");
    codeElement.innerHTML = text;
    qiElement.appendChild(codeElement);
    container.appendChild(qiElement);
}

function render_json(index, text) {
    var container = document.getElementById(index);
    container.setAttribute("class", "example");
    var qiElement = document.createElement("pre");
    var codeElement = document.createElement("code");
    codeElement.innerHTML = syntax_highlight(text);
    qiElement.appendChild(codeElement);
    container.appendChild(qiElement);
}

function render_get_query(index, query) {
    var query_string = query.split("=")[1].split("&")[0];
    render_txt(index, query_string);
    var variables = query.split("=")[2];
    const editor_url = "/graphiql/index.html";
    add_open_in_editor_link(index, editor_url, query_string, variables);
}

function add_open_in_editor_link(index, base_url, query, variables) {
    var container = document.getElementById(index);
    var ref = document.createElement("a");
    if(variables)
        ref.href = base_url + '?query=' + encodeURI(query) + '&variables='+encodeURI(variables);
    else
        ref.href = base_url + '?query=' + encodeURI(query) + '&variables='+encodeURI('{}');

    ref.setAttribute("class", "button");
    ref.setAttribute("target", "_blank");
    ref.innerHTML = 'open in editor';
    container.appendChild(ref);
}

function syntax_highlight(json) {

    if (typeof json != 'string') {
        json = JSON.stringify(json, undefined, 2);
    }

    json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
    return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
        var cls = 'number';
        if (/^"/.test(match)) {
            if (/:$/.test(match)) {
                cls = 'key';
            } else {
                cls = 'string';
            }
        } else if (/true|false/.test(match)) {
            cls = 'boolean';
        } else if (/null/.test(match)) {
            cls = 'null';
        }
        return '<span class="' + cls + '">' + match + '</span>';
    });
}

function render_data_attributes(index, input) {

    var opts = {
        allKeys: true,
        cb: {pre:get_attribute_description},
        attributes: [],
        lineage: []
    };
    var schema = JSON.parse(input);
    traverse(schema, opts);

    var table = document.getElementById(index);
    for (var i=0; i < opts.attributes.length; i++) {

        var data = opts.attributes[i];
        var row = document.createElement("tr");

        var c1 = document.createElement("td");
        c1.setAttribute("class", "attr-td-1");
        c1.innerText = data.field_name;
        row.appendChild(c1);

        var c4 = document.createElement("td");
        c4.setAttribute("class", "attr-td-4");
        c4.innerText = data.description;
        row.appendChild(c4);

        table.appendChild(row);
    }
}