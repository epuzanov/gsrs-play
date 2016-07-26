$(document).ready(function () {
    var target = new Bloodhound({
        datumTokenizer: function(d) {
            return Bloodhound.tokenizers.whitespace(d.key);
        },
        queryTokenizer: Bloodhound.tokenizers.whitespace,
        remote: '/api/suggest/Target?q=%QUERY',
    });
    var disease = new Bloodhound({
        datumTokenizer: function(d) {
            return Bloodhound.tokenizers.whitespace(d.key);
        },
        queryTokenizer: Bloodhound.tokenizers.whitespace,
        remote: '/api/suggest/Disease?q=%QUERY',
    });
    var gene = new Bloodhound({
        datumTokenizer: function(d) {
            return Bloodhound.tokenizers.whitespace(d.key);
        },
        queryTokenizer: Bloodhound.tokenizers.whitespace,
        remote: '/api/suggest/Gene?q=%QUERY',
    });
    var mesh = new Bloodhound({
        datumTokenizer: function(d) {
            return d.key.split(/\//);
        },
        queryTokenizer: Bloodhound.tokenizers.whitespace,
        remote: '/api/suggest/MeSH?q=%QUERY',
    });
    
    target.initialize();
    disease.initialize();
    gene.initialize();
    mesh.initialize();
    $('.typeahead').typeahead({
        hint: true,
        highlight: true,
        minLength: 2
    }, {
        name: 'Target',
        displayKey: 'key',
        source: target.ttAdapter(),
        templates: {
            suggestion: Handlebars.compile('<p><img width="20" src="/ix/assets/ncats/images/Target-badge-square.png"/> {{key}}</p>')
        }       
    }, {
        name: 'Disease',
        displayKey: 'key',
        source: disease.ttAdapter(),
        templates: {
            suggestion: Handlebars.compile('<p><img width="20" src="/ix/assets/ncats/images/Disease-badge-square.png"/> {{key}}</p>')
        }
    }, {
        name: 'Gene',
        displayKey: 'key',
        source: gene.ttAdapter(),
        templates: {
            header: '<h3>Gene</h3>'
        }
    }, {
        name: 'MeSH',
        displayKey: 'key',
        source: mesh.ttAdapter(),
        templates: {
            suggestion: Handlebars.compile('<p><strong>{{key}}</strong></p>')
        }
    });

    $('.typeahead').on("typeahead:selected", function (evt, val, d) {
        console.log('typeahead selected: '+val+' type: '+d);

        for (var f in val) {
            console.log(f+' '+val[f]);
        }
        if (val.key.indexOf('/') > 0) {
            /* this is mesh term, so don't quote */
        }
        else {
            $('.typeahead').typeahead('val', '"'+val.key+'"');
        }
    });
    
    $('.typeahead').on("typeahead:closed", function (evt) {
        console.log('typeahead closed!');
    });
});
