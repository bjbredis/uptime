# Measure uptime of a Redis instance

Meant to measure the time it is unavailable. Push the app to your foundation with a bound redis service.

## How to use this?
Just point your browser to the main url of the app in CF. 

### Routes: 
* `GET /` - main page with a timed ajax request to `/try`
* `GET /reset`   - reset the counters
* `GET /largest` - return the largest interval between requests
* `GET /try` - hit by the ajax reqeust on `/`
