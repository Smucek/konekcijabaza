$(document).ready(function(){

    function createTableRow(vehicle){
        var html = `
        <tr>
            <td>${vehicle.brand}</td>
            <td>${vehicle.model}</td>
            <td>${vehicle.plate}</td>
            <td>${vehicle.category}</td>
            <td>${vehicle.registration_date}</td>
            <td>${vehicle.registration_end_date}</td>
        </tr>`
        ;

        return html;
    };

    $(document).on("click", ".addbtn", function(){
        $("add-section").toggle();
       });

    $(document).on("click", ".showallbtn", function(){
    
    var url = 'http://localhost:8090/vehicles';
    $("tbody").html("");

    $.ajax({
    url: url,
           type: "GET",
           processData: false,
           contentType: "application/json",
           data: {},
           headers: {},
           success: function(res) {
               console.log(res);
    
               res.forEach(function(vehicle){
                   $("tbody").append(createTableRow(vehicle));
               }    
               );
           },
           error: function(err) {
               console.log(`Error on executing request: ${err.message}`);
           }
    });
    });

$(document).on("click", ".searchbtn", function(){
    var searchTerm = document.getElementById("search").value;
    //alert(searchTerm)

    var url = `http://localhost:8090/vehicle?searchTerm=${searchTerm}`;
$.ajax({
url: url,
       type: "GET",
       processData: false,
       contentType: "application/json",
       data: {},
       headers: {},
       success: function(res) {
           console.log(res);

           $("tbody").html("");
           res.forEach(function(vehicle){
               if (searchTerm === ""){
                $("tbody").html("");
               }
               else {
               $("tbody").append(createTableRow(vehicle));
               }
           }    
           );
       },
       error: function(err) {
           console.log(`Error on executing request: ${err.message}`);
       }
});
});
$(document).on("click", ".addnewbtn", function(){
    var vehicleData = {
       "brand" : document.getElementById("brand").value,
       "model" : document.getElementById("model").value,
       "plate" : document.getElementById("plate").value,
       "category" : document.getElementById("category").value,
       "registration_date" : document.getElementById("registration_date").value,
       "registration_end_date" : document.getElementById("registration_end_date").value
    };

    var url = `http://localhost:8090/vehicleAdd`;
$.ajax({
url: url,
       type: "POST",
       dataType: JSON,
       contentType: "application/json",
       data: {vehicleData},
       headers: {},
       success: function( data, status ){
      
    },
    error: function(err) {
        console.log(`Error on executing request: ${err.message}`);
    }
});
});
});







