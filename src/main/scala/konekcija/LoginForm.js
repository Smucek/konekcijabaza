$(document).on("click", ".loginbtn", function(event){
    
    event.preventDefault();
   // location.replace("Vehicles.html")

   
    var username = document.getElementById("username").value;
    var pass = document.getElementById("pass").value;


    $.ajax({
    url: `http://localhost:8090/getUser?username=${username}&pass=${pass}`,
           type: "GET",
           processData: false,
           contentType: "application/json",
           data: {},
           headers: {},
           success: function(res) {
               console.log(res);
console.log(username);
console.log(pass);
              //location.replace("Vehicles.html");
            
           },
           
           error: function(err) {
               console.log(`Error on executing request: ${err.message}`);
           }
        });
    });
   
   