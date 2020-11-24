const axios = require('axios');

module.exports = {
     getListSensors(area) {
       
        
        const endpoint = 'http://87.17.2.193:8080/unisasmartparkingrest/api/getArea/area='+area;
        //const url = endpoint + '?query=' + encodeURIComponent(sparqlQuery);
        const url = endpoint ;
        console.log(url); // in case you want to try the query in a web browser

        var config = {
            timeout: 6500, // timeout api call before we reach Alexa's 8 sec timeout, or set globally via axios.defaults.timeout
            headers: {'Accept': 'application/json'}
        };

        async function getJsonResponse(url, config){
            const res = await axios.get(url, config);
            return res.data;
        }

        return getJsonResponse(url, config).then((result) => {
            return result;
        }).catch((error) => {
            return area+" "+error;
        });
     },
     countSensorsFree(list_sensors){
         var count=0;
         if(list_sensors!=null && list_sensors.length>0){
             for(var i=0;i<list_sensors.length;++i){
                 if(list_sensors[i].state=='free')
                    ++count;
             }
         }
         return count;
     }
}
