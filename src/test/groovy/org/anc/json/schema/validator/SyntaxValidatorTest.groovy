package org.anc.json.schema.validator

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.fge.jsonschema.cfg.ValidationConfiguration
import com.github.fge.jsonschema.core.report.ProcessingMessage
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.main.JsonSchema
import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.github.fge.jsonschema.processors.syntax.SyntaxValidator
import org.anc.json.compiler.SchemaCompiler
import org.junit.*
import static org.junit.Assert.*

/**
 * @author Keith Suderman
 */
class SyntaxValidatorTest {

    SchemaCompiler compiler
    ObjectMapper mapper

    @Before
    void setup() {
        compiler = new SchemaCompiler()
        compiler.draftVersion = 4
        compiler.prettyPrint = false

        mapper = new ObjectMapper()
    }

    @After
    void cleanup() {
        compiler = null
        mapper = null
    }

    JsonNode getNode(String json) {
        return mapper.readValue(json, JsonNode.class)
    }

    @Test
    void testSyntaxValidator() {
        ValidationConfiguration cfg = ValidationConfiguration.byDefault()
        SyntaxValidator validator = new SyntaxValidator(cfg)
//        Processor processor = validator.processor
//        ProcessingReport report = new ListProcessingReport(LogLevel.DEBUG, LogLevel.WARNING)

        String source = """
type number
min 0
"""
        String json = compiler.compile(source)
        JsonNode node = getNode(json)
        ProcessingReport report = validator.validateSchema(node)
        def it = report.iterator()
        int count = 0
        while (it.hasNext()) {
            ++count
            ProcessingMessage message = it.next()
            println "$count ${message.toString()}"
        }
        assertTrue count > 0
    }

    @Test
    void testJsonSchema() {
        String source = """
type number
min 0
"""
        URL url = this.class.getResource('/json-schema-draft-04.json')
        assertNotNull url
        ObjectMapper mapper
        JsonNode schemaNode = getNode(url.text)
        JsonNode sourceNode = getNode(compiler.compile(source))
        JsonSchema schema = JsonSchemaFactory.byDefault().getJsonSchema(schemaNode)
        ProcessingReport report = schema.validate(sourceNode)
        println "Report : ${report.isSuccess()}"

    }
}
