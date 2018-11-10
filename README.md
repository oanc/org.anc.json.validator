JSON Schema Validator JSON
=========================


### Build Status

[![Master Status](http://grid.anc.org:9080/travis/svg/oanc/org.anc.json.validator?branch=master)](https://travis-ci.org/oanc/org.anc.json.validator)
[![Develop Status](http://grid.anc.org:9080/travis/svg/oanc/org.anc.json.validator?branch=develop)](https://travis-ci.org/oanc/org.anc.json.validator)

### Maven

```xml
<dependency>
    <groupId>org.anc.json</groupId>
    <artifactId>validator</artifactId>
    <version>${see below}</version>
</dependency>
```

Latest version : 
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.anc.json/validator/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/org.anc.json/validator)

The LAPPS Grid JSON Schema Validator can be used as a libary in other applications or can be used as a stand-alone program from the command line.  Under the hood the JSON validation is done by the [json-schema-validator](https://github.com/java-json-tools/json-schema-validator) project on GitHub.

## As a library

The Java library contains two classes of interest:

1. **Validator** <br/>
validates a JSON instance document against a JSON schema
1. **SchemaValidator** <br/>
validate a JSON Schema document against the [Draft-04 JSON Schema specification](http://json-schema.org/specification-links.html#draft-4).
## From the command line