
const http = require('http');
var data;
exports.handler = function(context, event) {

    var _event = JSON.parse(JSON.stringify(event));
    var _data = bin2string(_event.body.data);

    if((_data+"").startsWith("CSV")){

        var spl= _data.split(";"); // CSV;id_sensor;state
        var json_obj_send = "{id_sensor:'"+spl[1]+"',state:'"+spl[2]+"',area:'2'}";
        data = json_obj_send;
    }else{
        data=_data; // Is already defined
    }


    context.logger.info("a");
    var options = {
        host: '192.168.1.199',
        port: 8080,
        path: '/unisasmartparkingrest/api/updateState',
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Content-Length': data.length
        }
    };

    var req = http.request(options, function(res) {
        context.logger.info('STATUS: ' + res.statusCode);
        context.logger.info('HEADERS: ' + JSON.stringify(res.headers));
        res.setEncoding('utf8');
        res.on('data', function (chunk) {
            context.logger.info('BODY: ' + chunk);
            context.callback("OK");
        });
    });

    req.on('error', function(e) {
        context.logger.info('problem with request: ' + e.message);
        context.callback("KO")
    });
    req.write(data);
    req.end();


};

function bin2string(array){
    var result = "";
    for(var i = 0; i < array.length; ++i){
        result+= (String.fromCharCode(array[i]));
    }
    return result;
}
