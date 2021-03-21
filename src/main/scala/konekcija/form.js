$(document).ready(function(){

    function createTableRow(vehicle){
        var html = `
        <tr>
            <td contenteditable=false style="display:none">${vehicle.id}</td>
            <td contenteditable=false>${vehicle.brand}</td>
            <td contenteditable=false>${vehicle.model}</td>
            <td contenteditable=false>${vehicle.plate}</td>
            <td contenteditable=false>${vehicle.category}</td>
            <td contenteditable=false>${vehicle.registration_date}</td>
            <td contenteditable=false>${vehicle.registration_end_date}</td>
            <td><button type="button" class="delbtn" ><img src="trashicon.png" width="20" height="20"></button></a></td>
            <td><button type="button" class="editbtn" id="editbtn" ><img src="editicon.png" width="20" height="20"></button></a></td>
            <td><button type="button" class="confirmbtn" id="confirmbtn" data-toggle="popover" data-content="License plates already exists!"><img src="yesicon.png" width="10" height="10"></button></a></td>
            <td><button type="button" class="rejectbtn" id="rejectbtn"><img src="noicon.png" width="10" height="10"></button></a></td>
        </tr>`
        ;

        return html;
    };

    //EDIT VEHICLE

    $(document).on("click", ".editbtn", function() {

        $(document).find(".editbtn").hide();
        $(document).find(".delbtn").hide();

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
            "id" : $(currentTD).parents('tr').find("td:eq(0)").text(),
            "brand" : $(currentTD).parents("tr").find("td:eq(1)").text(),
            "model" : $(currentTD).parents("tr").find("td:eq(2)").text(),
            "plate" : $(currentTD).parents("tr").find("td:eq(3)").text(),
            "category" : $(currentTD).parents("tr").find("td:eq(4)").text(),
            "registration_date" : $(currentTD).parents("tr").find("td:eq(5)").text(),
            "registration_end_date" : $(currentTD).parents("tr").find("td:eq(6)").text()
         };
            console.log(editedVehicleData);

            var idEdited = Number($(currentTD).parents('tr').find("td:eq(0)").text());
            var url = `http://localhost:8090/vehicleEdit/${idEdited}`;
            $.ajax({
                   url: url,
                   type: "PUT",
                   dataType: "JSON",
                   contentType: "application/json",
                   data: JSON.stringify(editedVehicleData),
                   headers: {},
                   processData: false,
                   success: function( data, status ){
                    console.log(data)
                    $("tbody").html("");
                },
                error: function(err) {
                    console.log(`Error on executing request: ${err.message}`);
                    // $(document).ready(function(){
                    //         $("[data-toggle='popover']").popover();    
                    // });
                }
            });
        });

        $('.table tbody').on('click', '.rejectbtn', function() {

            $(document).find(".editbtn").show();
            $(document).find(".delbtn").show();
    
            var currentTD = $(this).parents('tr').find('td');
    
                $(currentTD).find('.confirmbtn').hide();
                $(currentTD).find('.rejectbtn').hide();
    
                if ($(this).prop('contenteditable', true)) {
                $.each(currentTD, function() {
                 ($(this).prop('contenteditable', false))
                });
            };
        });
    });

    //SHOW FORM FOR ADDING VEHICLES      

    $(document).ready(function() {
        $("#addbtn").click(function() {
          $("#addForm").show();
          $("#delForm").hide();
        });
      });

    $(document).ready(function() {
        $("#cancelbtn").click(function() {
          $("#addForm").hide();
          $(".plateduplicate").hide();
        });
      });

      $(document).ready(function() {
        $("#delbtn").click(function() {
          $("#delForm").show();
        });
      });

      $(document).ready(function() {
        $("#cancelDel").click(function() {
          $("#delForm").hide();
          $(document).find(".editbtn").show();
          $(document).find(".delbtn").show();
        });
      });

    //SHOW ALL VEHICLES

    $(document).on("click", ".showallbtn", function(){
    
    var url = 'http://localhost:8090/vehicles';
    $("tbody").html("");
    $("#delForm").hide();
    $("#search").val('');


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
    $('form :input').val('');
    $(".plateduplicate").hide();
    });

//SEARCH VEHICLES

    var timeout = null; //timeout for delaying search input and send request after writing is done

$(document).on("keyup", "#search", function(){
    var searchTerm = document.getElementById("search").value;
    //alert(searchTerm)
    clearTimeout(timeout)
    timeout = setTimeout(function() {


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
    console.log(searchTerm)
    }, 500)
});

//ADD VEHICLE

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

    console.log(vehicleData);

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
        $("#addForm").hide();
        $('form :input').val('');
        $(".plateduplicate").hide();

    },
    error: function(err) {
        console.log(`Error on executing request: ${err.message}`);
        $(".plateduplicate").show();
    
    }
});
});

//DELETE VEHICLE

$(document).on("click", ".delbtn", function() {

    $("#delForm").show();
    $(document).find(".editbtn").hide();
    $(document).find(".delbtn").hide();

    var currentTD = $(this).parents('tr').find('td');

     //   $(currentTD).find('.confirmbtn').show();
      //  $(currentTD).find('.rejectbtn').show();

 $(document).on('click', '.confirmDel', function() {
    $("#search").val('');
    event.preventDefault();

     var delVehicle = {
        "id" : $(currentTD).parents('tr').find("td:eq(0)").text(),
     };
        console.log(delVehicle);

        var idToDelete = Number($(currentTD).parents('tr').find("td:eq(0)").text());
        var url = `http://localhost:8090/vehicleDel/${idToDelete}`;
        $.ajax({
               url: url,
               type: "DELETE",
               dataType: "JSON",
               contentType: "application/json",
            //    data: JSON.stringify(idToDelete),
               headers: {},
               processData: false,
               success: function( data, status ){
                console.log(data)
                $("#delForm").hide();
                $("tbody").html("");
            },
            error: function(err) {
                console.log('err', err);
                console.log(`Error on executing request: ${err.message}`);
                // $(document).ready(function(){
                //         $("[data-toggle='popover']").popover();    
                // });
            }
        });
    });

    $('.table tbody').on('click', '.rejectbtn', function() {

        $(document).find(".editbtn").show();
        $(document).find(".delbtn").show();

        var currentTD = $(this).parents('tr').find('td');

            $(currentTD).find('.confirmbtn').hide();
            $(currentTD).find('.rejectbtn').hide();

    });
});

$(document).ready(function(){

    $.ajax({
        url: 'https://freegeoip.app/json/',
        type: "GET",
        dataType: "JSON",
        contentType: "application/json",
        success: function(geo) {
            var city = geo.city;
            var country = geo.region_code;
            var lat = geo.latitude;
            var lon = geo.longitude;
            console.log(lat);
            console.log(lon);

            $.ajax({
                url: `http://www.7timer.info/bin/api.pl?lon=${lon}&lat=${lat}&product=astro&output=json`,
                type: "GET",
                dataType: "JSON",
                contentType: "application/json",
                success: function(data) {
        
                    var response = data;
                    
                    var temperature = response.dataseries[0].temp2m;
                    
                    var i = 0;
                    var text = "Trenutna temperatura u " + city + ", " + country + " je " + temperature + " °C";
                    var speed = 50;
                    
                     // if (i < text.lenght) {
                        function typeWriter() {
                            if (i < text.length) {
                        document.getElementById("thirdParty").innerHTML += text.charAt(i); 
                        i++;
                        setTimeout(typeWriter, speed);
                        }
                    }
                        typeWriter();
                  // document.getElementById("thirdParty").innerHTML += text.charAt(i);
                  // i++;
                  //  setTimeout(speed);
                  // }
                
                    console.log(response);
                    console.log(temperature);
                
                }
            });
        }
    }); 
});

$(document).ready(function(){

    $.ajax({
        url: 'http://localhost:8090/mostCommonBrand',
               type: "GET",
               processData: false,
               contentType: "application/json",
               data: {},
               headers: {},
               success: function(res) {
                   console.log(res);
        
                    text = `Najzastupljeniji brandovi vozila u bazi su ${res.first}, ${res.second}, ${res.third}, ${res.fourth}, ${res.fifth}`
                   document.getElementById("mostCommonBrand").innerHTML += text; 
               },
               error: function(err) {
                   console.log(`Error on executing request: ${err.message}`);
               }
        });

});

});
