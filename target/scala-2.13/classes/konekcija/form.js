$(document).ready(function(){




var url = 'http://localhost:8090/vehicles';
$.ajax({
url: url,
       type: "GET",
       processData: false,
       contentType: "application/json",
       data: {},
       headers: {},
       success: function(res) {
           console.log(res);

           res.array.forEach(function(vehicle){
               $("tbody").append(createTableRow(vehicle));
           }    
           );
       },
       error: function(err) {
           console.log(`Error on executing request: ${err.message}`);
       }
});

});
