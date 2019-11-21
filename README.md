# Measure uptime of a Redis instance

Meant to measure the time it is unavailable. Push the app to your foundation with a bound redis service.
`mvn clean package; cf push`

## How can I use this app locally?
Compile locally and then start the app.
* `mvn clean package` to compile
* `java -jar ./target/uptime-0.0.1-SNAPSHOT.jar` to run the app
* Edit the redis connection details in `resources/application.properties` as necessary

## How to use this?
Just point your browser to the main url of the app in CF. If running locally, the http port will be 8080; if running in PCF it will be port 80.

### Routes: 
* `GET /` - main page with a timed ajax request to `/try`
* `GET /reset`   - reset the counters
* `GET /largest` - return the largest interval between requests
* `GET /try` - hit by the ajax reqeust on `/`
