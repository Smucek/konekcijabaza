$(document).on("click", ".loginbtn", function(event){
    
    event.preventDefault();
   // location.replace("Vehicles.html")

   
    var username = document.getElementById("username").value;
    var pass = document.getElementById("pass").value;
    var day = new Date;
    var enDay = day.getDate();
    var month = new Date;
    var enMonth = month.getMonth();
    var enPass = btoa(`${enDay}${enMonth}:${pass}:scalarules`)

    $.ajax({
    url: `http://localhost:8090/getUser?username=${username}&pass=${enPass}`,
           type: "GET",
           processData: false,
           contentType: "application/json",
           data: {},
           headers: {},
           success: function(res) {
               console.log(res);
/*console.log(username);
console.log(pass);
console.log(enPass);*/

              location.replace("Vehicles.html");
            
           },
           
           error: function(err) {
               console.log(`Error on executing request: ${err.message}`);
           }
        });
    });
   
   