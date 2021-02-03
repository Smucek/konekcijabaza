# konekcijabaza
VEHICLE FLEET APP

ABOUT

Application to provide basic information for cars, and most importantly, start and expiry date of registration.

BUILT WITH

Main database is made in posgresql - all access sensibile informations are stored in ENVIROMENTAL VARIABLES.
Server-API is writen in SCALA, using Akka-Http library;
Client-part is writen in basic HTML/CSS, with JavaScript handling requests via AJAX(JQuery).

WHAT DOES IT DO?

Application provides CRUD operations, access to database, adding, deleting and editing vehicle data, as well as live search by any parameter.

ADDITIONAL INFO

Application provides info about current temperature through free third-party API-s (geolocation and 7timer);
Also, it shows simple usage of Schedulers by providing simple statistics showing top 5 car brands represented in database.

