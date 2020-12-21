[![Codacy Badge](https://api.codacy.com/project/badge/Grade/3436b5e072f14939b0407cad4baa743d)](https://app.codacy.com/gh/SoftmedTanzania/hdr-mediator-emr?utm_source=github.com&utm_medium=referral&utm_content=SoftmedTanzania/hdr-mediator-emr&utm_campaign=Badge_Grade)
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

To build and launch our mediator, run

```
  mvn install
  java -jar target/hdr-mediator-emr-0.1.0-jar-with-dependencies.jar
```

