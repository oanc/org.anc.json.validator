/*-
 * Copyright 2014 The American National Corpus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.anc.json.validator

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.fge.jsonschema.core.report.ListReportProvider
import com.github.fge.jsonschema.core.report.LogLevel
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.core.report.ReportProvider
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.github.fge.jsonschema.main.JsonSchemaFactoryBuilder
import groovy.cli.picocli.CliBuilder
import groovy.util.logging.Slf4j
import org.anc.json.compiler.SchemaCompiler;

//import javax.annotation.processing.Processor

/**
 * @author Keith Suderman
 */
@Slf4j
class Validator {
    ObjectMapper mapper
    JsonSchema schema
    JsonNode schemaNode

    public Validator(URL url) {
        this(url.text)
    }

    public Validator(File file) {
        this(file.text)
    }

    public Validator(String schemaText) {
        mapper = new ObjectMapper()
        schemaNode = getNode(schemaText)
        buildSchema()
    }

    public Validator(JsonNode node) {
        schemaNode = node
        buildSchema()
    }

    public static Validator alternateSyntax(URL url) {
        return alternateSyntax(url.text)
    }

    public static Validator alternateSyntax(File file) {
        return alternateSyntax(file.text)
    }

    public static Validator alternateSyntax(String source) {
        SchemaCompiler compiler = new SchemaCompiler()
        return new Validator(compiler.compile(source))
    }

    private void buildSchema() {
        ReportProvider reportProvider = new ListReportProvider(LogLevel.INFO, LogLevel.FATAL)
        JsonSchemaFactory factory = new JsonSchemaFactoryBuilder().setReportProvider(reportProvider).freeze()
        schema = factory.getJsonSchema(schemaNode)
    }

    private JsonNode getNode(File file) {
        log.debug("Getting JsonNode from file: {}", file.path)
        return mapper.readValue(file.text, JsonNode.class)
    }

    private JsonNode getNode(URL url) {
        log.debug("Getting JsonNode from URL: {}", url.toString())
        return mapper.readValue(url.text, JsonNode.class)
    }

    private JsonNode getNode(String json) {
        log.debug("Getting JsonNode from a String.")
        return mapper.readValue(json, JsonNode.class)
    }

    ProcessingReport validate(String instanceText) {
        log.info("Validating instance text.")
        return validate(getNode(instanceText))
    }

    ProcessingReport validate(JsonNode instance) {
        // Processor
//        ValidationConfiguration cfg = ValidationConfiguration.byDefault()
//        SyntaxValidator syntaxValidator = new SyntaxValidator(cfg)
//        final Processor processor =
//                syntaxValidator.getProcessor();
//        // Report
//        final ListProcessingReport report = new
//                ListProcessingReport(LogLevel.DEBUG, LogLevel.WARNING);
//        // SchemaTree
//        final SchemaTree tree = Dereferencing.CANONICAL.newTree(schemaNode);
//        ValueHolder<SchemaTree> valueHolder = ValueHolder.hold(tree)
//        // Validate; invalid --> ProcessingException
//        processor.process(report, valueHolder);
//        return report
        log.info("Validating JsonNode instance")
        return schema.validate(instance)
    }

    ProcessingReport validate(URL url) {
        log.info("Validating the URL {}", url.toString())
        return validate(getNode(url))
    }

    ProcessingReport validate(File file) {
        log.info("Validating the file {}", file.path)
        return validate(getNode(file))
    }


//    static void usage() {
//        println """
//USAGE
//
//java -jar jsonv-${Version.version}.jar /path/to/schema [/path/to/json/instance]"
//
//If only the JSON schema is provided it will be checked to ensure it is a valid
//JSON schema.  If a schema and a JSON instance are provided the instance will
//be validated with the schema.
//
//"""
//    }

    static void main(args) {
        def cli = new CliBuilder()
        cli.usage = 'java -jar Validator.jar [-j|-a] -s <schema> [-i <instance.]'
        cli.header = """
Validates a JSON instance or schema.

OPTIONS
"""
        cli.footer = """
If a json instance is not provided the schema will be validated
with the Draft v4.0 JSON Schema specification.  Otherwise the
instance will will be validated with the schema.

Copyright 2018 American National Corpus.

"""
        cli.j(longOpt:'json', required:false, 'schema is specified in json.')
        cli.a(longOpt:'alt', required:false,  'schema is specified using alternate syntax.')
        cli.s(longOpt:'schema', required:false, args:1, 'schema to use for validation.')
        cli.i(longOpt:'instance', required:false, args:1, 'json instance to validate.')
        cli.v(longOpt:'version', required:false, 'displays the current version number.')
        cli.h(longOpt:'help', required:false, 'this usage message.')
        def params = cli.parse(args)
        if (!params) {
            return
        }

        if (params.h) {
            cli.usage()
            return
        }

        if (params.v) {
            println """
LAPPS JSON Validator v${Version.version}
Copyright 2018 American National Corpus.

"""
            return
        }

        if (!params.s) {
            println "\nERROR: no schema specified.\n"
            cli.usage()
            return
        }

        String source
        if (params.j) {
            source = new File(params.s).text
        }
        else {
            SchemaCompiler compiler = new SchemaCompiler()
            source = compiler.compile(new File(params.s).text)
        }

        if (params.i) {
            // Validate the instance with the schema
            println "Validating the instance with the specified schema."
            Validator validator = new Validator(source)
            println validator.validate(new File(params.i))
        }
        else {
            // Otherwise validate the schema against the spec.
            println "Validating the schema with the current draft specification."
//            URL draft04 = Thread.currentThread().contextClassLoader.getResource('json-schema-draft-04.json')
//            if (!draft04) {
//                println "fatal error: unable to load the JSON specification."
//                return
//            }
//            Validator validator = new Validator(draft04)
//            println validator.validate(source)
//            JsonNode node = new ObjectMapper().readValue(source, JsonNode)
//            ValidationConfiguration cfg = ValidationConfiguration.byDefault()
//            SyntaxValidator validator = new SyntaxValidator(cfg)
            SchemaValidator validator = new SchemaValidator()
            ProcessingReport report = validator.validate(source)
            List<ProcessingMessage> messages = report.toList()
            int nMessages = messages.size()
            if (nMessages == 0) {
                println "OK"
            }
            else {
                println "There are ${nMessages} errors and/or warnings"
                messages.each { message ->
                    println message.toString()
                }
            }
//            report.each { ProcessingMessage message ->
//                println JacksonUtils.prettyPrint(message.asJson())
//            }
        }
    }
}
