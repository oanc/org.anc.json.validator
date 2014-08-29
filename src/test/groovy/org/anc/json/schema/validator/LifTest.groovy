package org.anc.json.schema.validator

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.fge.jsonschema.cfg.ValidationConfiguration
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.processors.syntax.SyntaxValidator
import org.anc.json.schema.SchemaCompiler
import org.junit.*
import static org.junit.Assert.*

/**
 * @author Keith Suderman
 */
class LifTest {
    @Test
    void parseSchema() {
        URL url = Thread.currentThread().contextClassLoader.getResource('lif.schema')
        assertNotNull url
        SchemaCompiler compiler = new SchemaCompiler()
        String source = compiler.compile(url)
        JsonNode node = new ObjectMapper().readValue(source, JsonNode)
        ValidationConfiguration cfg = ValidationConfiguration.byDefault()
        SyntaxValidator validator = new SyntaxValidator(cfg)
        ProcessingReport report = validator.validateSchema(node)
        println report.toString()
        assertTrue report.isSuccess()
    }
}
