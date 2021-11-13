$(document).ready(function(){

    $('#searchBox').on('input', function() {
        var href = '/lookup/'+$("#searchBox").val();
        $('form').prop('action', href.toString())
    });

});
