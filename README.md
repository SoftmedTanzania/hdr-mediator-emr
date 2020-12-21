[![Build Status](https://travis-ci.com/SoftmedTanzania/hdr-mediator-emr.svg?branch=master)](https://travis-ci.com/github/SoftmedTanzania/hdr-mediator-emr)
# Tanzania EMR-HDR mediator
An [OpenHIM](http://openhim.org/) mediator for processing data received from emr and sending it to Tanzania Health Data Repository (HDR).

# Getting Started
Clone the repository and run `npm install`

Open up `src/main/resources/mediator.properties` and supply your OpenHIM config details and save:

```
  mediator.name=HDR-Mediator
  # you may need to change this to 0.0.0.0 if your mediator is on another server than HIM Core
  mediator.host=localhost
  mediator.port=4000
  mediator.timeout=60000

  core.host=localhost
  core.api.port=8080
  # update your user information if required
  core.api.user=root@openhim.org
  core.api.password=openhim-password
```

To build and launch our mediator, run.

```
  mvn install
  java -jar target/hdr-mediator-emr-0.1.0-jar-with-dependencies.jar
```

