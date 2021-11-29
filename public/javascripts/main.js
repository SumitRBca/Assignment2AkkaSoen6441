$(document).ready(function() {

    $('#searchBox').on('input', function() {
        var href = '/lookup/'+$("#searchBox").val();
        $('form').prop('action', href.toString())
    });

    let URL = $('body').data('ws-url');
    let ws = new WebSocket(URL)

    ws.onopen = function (ev) {}
    ws.onclose = function (ev) {}
    ws.onerror = function (ev) {}
    ws.onmessage = function (event) {
        let message;
        message = JSON.parse(event.data);
    };

});
