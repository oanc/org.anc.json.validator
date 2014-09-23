package org.anc.json.schema.validator

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.fge.jackson.JacksonUtils
import com.github.fge.jsonschema.cfg.ValidationConfiguration
import com.github.fge.jsonschema.core.report.ListReportProvider
import com.github.fge.jsonschema.core.report.LogLevel
import com.github.fge.jsonschema.core.report.ProcessingMessage
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.core.report.ReportProvider
import com.github.fge.jsonschema.main.JsonSchema
import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.github.fge.jsonschema.main.JsonSchemaFactoryBuilder
import com.github.fge.jsonschema.processors.syntax.SyntaxValidator
import org.junit.*
import static org.junit.Assert.*


/**
 * @author Keith Suderman
 */
class SchemaSyntaxTest {

    @Test
    void test() {
        ObjectMapper mapper = new ObjectMapper()
//        String schemaSource = this.class.getResource('/json-schema-draft-04.json').text
//        JsonNode schemaNode = mapper.readValue(schemaSource, JsonNode)
//        ReportProvider reportProvider = new ListReportProvider(LogLevel.INFO, LogLevel.WARNING)
//        JsonSchemaFactory factory = new JsonSchemaFactoryBuilder().setReportProvider(reportProvider).freeze()
//        JsonSchema schema = factory.getJsonSchema(schemaNode)

        SyntaxValidator validator = new SyntaxValidator(ValidationConfiguration.byDefault())

        String instance = """
{
    "title":"Test",
    "type":"number",
    "version":"1.0.0"
}
"""

//        ProcessingReport report = schema.validate(mapper.readValue(instance, JsonNode))
        ProcessingReport report = validator.validateSchema(mapper.readValue(instance, JsonNode))
        List<ProcessingMessage> messages = report.toList()

        //assertFalse report.isSuccess()
        assertTrue messages.size() > 0
        if (messages.size() == 0) {
            println "OK"
            fail "No error messages even though validation failed"
        }
        else {
            messages.each {
                println JacksonUtils.prettyPrint(it.asJson())
            }
        }
    }
}
