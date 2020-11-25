$(document).ready(function(){

    function createTableRow(vehicle){
        var html = `
        <tr>
            <td contenteditable=false class="brand" id="brand">${vehicle.brand}</td>
            <td contenteditable=false>${vehicle.model}</td>
            <td contenteditable=false>${vehicle.plate}</td>
            <td contenteditable=false>${vehicle.category}</td>
            <td contenteditable=false>${vehicle.registration_date}</td>
            <td contenteditable=false>${vehicle.registration_end_date}</td>
            <td><button type="button" class="delbtn" ><img src="trashicon.png" width="20" height="20"></button></a></td>
            <td><button type="button" class="editbtn" id="editbtn" ><img src="editicon.png" width="20" height="20"></button></a></td>
            <td><button type="button" class="confirmbtn" id="confirmbtn"><img src="yesicon.png" width="10" height="10"></button></a></td>
            <td><button type="button" class="rejectbtn" id="rejectbtn"><img src="noicon.png" width="10" height="10"></button></a></td>
        </tr>`
        ;

        return html;
    };

    $(document).on("click", ".editbtn", function() {

        var currentTD = $(this).parents('tr').find('td');

            $(currentTD).find('.confirmbtn').show();
            $(currentTD).find('.rejectbtn').show();

            if ($(this).prop('contenteditable', false)) {
            $.each(currentTD, function() {
             ($(this).prop('contenteditable', true))
            });
        };

        

     $('.table tbody').on('click', '.confirmbtn', function() {
         
         var editedVehicleData = {
            "brand" : $(currentTD).parents("tr").find("td:eq(0)").text(),
            "model" : $(currentTD).parents("tr").find("td:eq(1)").text(),
            "plate" : $(currentTD).parents("tr").find("td:eq(2)").text(),
            "category" : $(currentTD).parents("tr").find("td:eq(3)").text(),
            "registration_date" : $(currentTD).parents("tr").find("td:eq(4)").text(),
            "registration_end_date" : $(currentTD).parents("tr").find("td:eq(5)").text()
         };
            console.log(editedVehicleData);
        });
    });


      

    $(document).ready(function() {
        $("#addbtn").click(function() {
          $("#addForm").show();
        });
      });

    $(document).ready(function() {
        $("#cancelbtn").click(function() {
          $("#addForm").hide();
        });
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
    $("#addForm").hide();
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
$(document).on("click", ".addnewbtn", function(event){
    event.preventDefault();

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
       dataType: "JSON",
       contentType: "application/json",
       data: JSON.stringify(vehicleData),
       headers: {},
       processData: false,
       success: function( data, status ){
        console.log(data)
    },
    error: function(err) {
        console.log(`Error on executing request: ${err.message}`);
    }
});
$("#addForm").hide();
$('form :input').val('');
});
});







