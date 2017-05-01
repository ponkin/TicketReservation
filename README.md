# Ticket reservation system

## Build
Run `sbt packageBin` to create distribution zip(see target/universal directory).
Or you can build Docker image with `sbt docker:publishLocal` and then run as usual local docker image.

## Run
Run with `sbt run` or as usual docker image or extract arhive and run `bin/ticketreservation`.

## Config
If you are using zip distribution then all configs are available in `conf/application.conf` file.
For `sbt run` take a look in `src/resources/reference.conf`

Have fun!
