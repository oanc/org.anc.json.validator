package org.anc.json.schema.validator

import com.github.fge.jsonschema.core.report.ProcessingReport
import org.anc.json.compiler.SchemaCompiler
import org.anc.json.validator.Validator
import org.junit.*
import static org.junit.Assert.*

/**
 * @author Keith Suderman
 */
class ValidatorTest {

    Validator validator

    @After
    void cleanup() {
        validator = null
    }

    @Test
    void testNumberSchema() {
        String schema = '{"type":"number"}'
        String instance = '42'
        validator = new Validator(schema)
        assertTrue validator.validate(instance).isSuccess()
    }

    @Ignore
    void testInvalidNumber() {
        String schema = '{"type":"number"}'
        String instance = '{}'
        validator = new Validator(schema)
        assertFalse validator.validate(instance).isSuccess()
    }

    @Ignore
    void testProperties() {
        String schema = """
{
    "properties":{
        "i": { "type":"integer" },
        "s": { "type": "string" }
    }
}
"""
        String instance = """
{
    "i":0,
    "s":"a string"
}
"""
        validator = new Validator(schema)
        assertTrue validator.validate(instance).isSuccess()
    }

    @Ignore
    void testInvalidValue() {
        String schema = '{"type":"number","minimum":42}'
        String instance = '0'
        validator = new Validator(schema)

    }

    @Test
    void testUnknownKeyWord() {
        String schema = """
{
    "type":"number",
    "min":42
}
"""
        String instance = '0'
        validator = new Validator(schema)
        assertTrue validator.validate(instance).isSuccess()
    }

    @Test
    void testAlternateSyntax() {
        String schemaText = """
title "Alternate Syntax"
type object
properties {
    foo { type string }
    bar { type number; minimum 1 }
}
"""
        String instance = """
{
    "foo":"a string value.",
    "bar":42
}
"""
        SchemaCompiler compiler = new SchemaCompiler()
        String json = compiler.compile(schemaText)
        validator = new Validator(json)
        check validator.validate(instance), true
    }

    @Test
    void testAlternateSyntaxInvalidNumber() {
        println "ValidatorTest.testAlternateSyntaxInvalidNumber"
        String source = """
properties {
    foo { type string }
    bar { type number; minimum 1 }
}
"""
        String instance = """
{
    "foo": "a string value",
    "bar": 0
}
"""
        SchemaCompiler compiler = new SchemaCompiler()
        String json = compiler.compile(source)
        validator = new Validator(json)
        def result = validator.validate(instance)
        check(result, false)
//        println result
//        assertFalse result.isSuccess()
    }

    private Validator getSchemaValidator() {
        URL url = Thread.currentThread().contextClassLoader.getResource('json-schema-draft-04.json')
        assertNotNull url
        return new Validator(url)
    }

    @Test
    void validateASchema() {
        println "ValidatorTest.validateASchema"
        validator = getSchemaValidator()

        String schema = """
type object
properties {
    age {
        type integer
        minimum "six"
    }
}
"""
        SchemaCompiler compiler = new SchemaCompiler()
        compiler.draftVersion = 4
        ProcessingReport report = validator.validate(compiler.compile(schema))
//        println "Report: " + report.toString()
//        assertFalse report.isSuccess()
        check(report, false)
    }

//    @Test
    void invalidConstraint() {
        println "ValidatorTest.invalidConstraint"
        String schema = """
type object
properties {
    i {
        type number
        foo 10
    }
}
"""
        String instance = '{"name":1}'
        validator = getSchemaValidator()
//        validator = Validator.alternateSyntax(schema)
//        check(validator.validate(schema), false)
        String json = new SchemaCompiler().compile(schema)
        println json
        ProcessingReport report = validator.validate(json)
        check(report, false)
//        println "Report: " + report.toString()
//        int count = 0
//        def it = report.iterator()
//        while (it.hasNext()) {
//            ++count
//            println "${count}: ${it.next().toString()}"
//        }
//        assertTrue count > 0
    }

    @Test
    void testArray() {
        SchemaCompiler compiler = new SchemaCompiler()
        compiler.draftVersion = 4
//        String source = """
//type object
//properties {
//    a {
//        type array
//        items { type integer; minimum 0 }
//    }
//}
//"""
        String source = """
{
    "title": "Example invalid schema",
    "type": "array",
    "items": { "type":"number", "minimum":1 }
}

"""
        String good = '[1, 2, 3]'
        String bad1 = '[0,1,2]'
        String bad2 = '["one", 2, 3]'

//        validator = Validator.alternateSyntax(source)
        validator = new Validator(source)
        check validator.validate(good), true
        check validator.validate(bad1), false
        check validator.validate(bad2), false
    }

    @Test
    void definitionTest() {
        String source = """
title 'Definition test.'
type array
items { \$ref '#/definitions/item' }
definitions {
    item {
        type number
        minimum 1
    }
}
"""
        String good = '[1,2,3]'
        String bad = '[0,1,2]'

        validator = Validator.alternateSyntax(source)
        check validator.validate(good), true
        check validator.validate(bad), false
    }

    @Test
    void testBogusInstance() {
        String schema = """
type object
properties {
    firstName { type string }
    lastName { type string }
    age { type integer }
}
additionalProperties false
"""
        String instance = """
{
    "name":"value",
    "type":"token"
}
"""
        validator = Validator.alternateSyntax(schema)
        ProcessingReport report = check(validator.validate(instance), false)
//        ProcessingReport report = validator.validate(instance)
        println report.toString()
    }


    ProcessingReport check(ProcessingReport report, boolean expected) {
        if (report.isSuccess() == expected) {
            return report
        }
        fail report.toString()

    }
}
