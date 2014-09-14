package org.anc.json.validator

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.fge.jsonschema.cfg.ValidationConfiguration
import com.github.fge.jsonschema.core.report.ListProcessingReport
import com.github.fge.jsonschema.core.report.LogLevel
import com.github.fge.jsonschema.core.report.ProcessingMessage
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.processors.syntax.SyntaxValidator
import groovy.util.logging.Slf4j
import org.anc.json.compiler.SchemaCompiler

/**
 * Used to determine if a JSON document is also a valid JSON Schema as
 * defined by http://json-schema.org/draft-04/schema
 *
 * @author Keith Suderman
 */
@Slf4j
class SchemaValidator {
    SyntaxValidator validator
    SchemaCompiler compiler
    ObjectMapper mapper

    public SchemaValidator() {
        ValidationConfiguration cfg = ValidationConfiguration.byDefault()
        validator = new SyntaxValidator(cfg)
        compiler = new SchemaCompiler()
        mapper = new ObjectMapper()
    }

    public ProcessingReport validate(JsonNode node) {
        return validator.validateSchema(node)
    }

    public ProcessingReport validate(String source) {
        if (!source.startsWith("{")) {
            log.info('Compiling alternate syntax.')
            try {
                source = compiler.compile(source)
            }
            catch (Exception e) {
                log.error('Unable to compile alternate syntax.', e)
                ProcessingReport report = new ListProcessingReport()
                ProcessingMessage message = new ProcessingMessage()
                message.message = e.message
                message.logLevel = LogLevel.FATAL
                report.fatal(message)
                return report
            }
            log.debug('compilation complete.')
        }
        log.info('Validating the schema')
        ProcessingReport report
        try {
            report = validator.validateSchema(mapper.readValue(source, JsonNode))
            log.debug("Validating complete.")
        }
        catch (Exception e) {
            log.error("Unable to validate the schema", e)
            report = new ListProcessingReport()
            ProcessingMessage message = new ProcessingMessage().setMessage(e.message)
            report.fatal(message)
        }
        log.info("Returning the validation report.")
        return report
    }

}
