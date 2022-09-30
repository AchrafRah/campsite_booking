Campsite booking app, compiles with Java 11.

To launch, simply run the DemoApplication class, it will expose the port 8080 with the following endpoints:

- GET /availabilities Optional parameters arrivalDate and departureDate in yyyy-MM-dd format
- POST /bookings create a new booking, must receive a ReservationRequest in body (JSON format)
- PUT /bookings/{id} Updates a booking, id is the reservation string, body must contain a valid ReservationRequest in
  Json format
- DELETE /bookings/{id} Deletes a booking, id is the reservation string

It launches with an embedded H2 database, the unique index on the column "reservationDate" for entity:
CampsiteBookingEntity,
ensures that only one booking per day is allowed.
If using an external database, please make sure the index is present in the database creation script.

A sample postman collection is provided under ./postman
dates are in yyyy-MM-dd, Iso Date format