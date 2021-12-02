$(document).ready(function() {

    $('#searchBox').on('input', () => {
        let query = $("#searchBox").val();
        let href = `/lookup/${query}`;
        $('form').prop('action', href.toString())
    });

    let wsURL = $('body').data('ws-url');
    let ws = new WebSocket(wsURL)

    ws.onopen = function (ev) {
        console.log('Connection opened', ev);
        let { pathname } = new URL(window.location);

        switch (true) {
            case pathname.includes('/thread'): {
                let thread = $('h3').attr('data-search')
                // -- //
                console.log('thread --', thread);

                let data = JSON.stringify({ type: 'thread', query: thread });
                ws.send(data);
            }

            default: {
                // let d = $('section').map(function () {
                //     return $(this).attr('data-search');
                // }).toArray();

                // if (d.length) {
                //     let data = JSON.stringify({ type: 'main', query: d });
                //     ws.send(data);
                // }
            }
        }

    }
    ws.onclose = function (ev) {
        console.log('Connection closed', ev);
    }
    ws.onerror = function (ev) {
        console.log('Connection errored', ev);
    }
    ws.onmessage = function (event) {
        let message;
        message = JSON.parse(event.data);
        message.data = JSON.parse(message.data);
        console.log('message --', message);
    };

});
