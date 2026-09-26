package com.example.jwt_demo.controller;


import com.example.jwt_demo.Common.ErrorResponse;
import com.example.jwt_demo.Common.ai.*;
import com.example.jwt_demo.Enums.Warnings;
import com.example.jwt_demo.GlobalExseptions.Exseptions.ValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/Ai")
public class AiController {

    private final ObjectMapper mapper;

    public AiController(ObjectMapper mapper) {
        this.mapper = mapper;
    }


    @PostMapping("/getAiFillText")
    public <T> T fillDataUsingAi(@RequestBody AiQuestion aiQuestion)  {



        Class<T> referenceClass = (Class<T>) switch (aiQuestion.getReferenceToDataNeeded()) {
            case "Materials" -> MaterialAiDto.class;
            case "Employees" -> EmployeeAiDto.class;
            case "Orders" -> OrderAiDto.class;
            case "Products" -> ProductAiDto.class;
            default -> throw new IllegalArgumentException("Unknown type");
        };


        StringBuilder question = new StringBuilder(aiQuestion.getPrompt());

        question.append("""
        
Return ONLY valid JSON.
No explanations.
No markdown.
No text outside the JSON.

You MUST follow these rules exactly.

==================== DATA TYPES ====================

Every field in the JSON belongs to exactly one of these categories:

1. GENERATED FIELD
2. LIST FIELD
3. ENUM FIELD
4. NULL FIELD
5. NORMAL EXISTING VALUE


==================== GENERATED VALUE RULES ====================

The following values are GENERATION REQUESTS.

String:
    "None"

Integer / int / Long / long:
    0

Double / double / Float / float:
    0.0

When a field contains one of these generation placeholders,
you MUST generate an appropriate value based on:

- the field name
- the USER PROMPT
- the field type

Generated values must remain the correct JSON type.


==================== GENERATED STRING ====================

If a String field contains exactly:

"None"

then it is a GENERATION REQUEST.

You MAY generate a new String value.

The generated String MUST:
- be appropriate for the field name
- follow the USER PROMPT
- remain a String

Example:

"materialName": "None"

may become:

"materialName": "Oak Wood"


==================== GENERATED INTEGER ====================

If an Integer, int, Long, or long field contains:

0

then it is a GENERATION REQUEST.

You MAY generate a new integer.

The generated value MUST:
- be an integer
- be appropriate for the field name
- follow the USER PROMPT

NEVER return a decimal for an integer field.

Example:

"amount": 0

may become:

"amount": 25


==================== GENERATED DECIMAL ====================

If a Double, double, Float, or float field contains:

0.0

then it is a GENERATION REQUEST.

You MAY generate a new decimal number.

Example:

"price": 0.0

may become:

"price": 25.99"


==================== NULL FIELD ====================

If a field contains:

null

you MUST return:

null

Do NOT generate a value.

Do NOT replace null.

Do NOT infer a value.

Example:

"materialName": null

MUST remain:

"materialName": null


==================== LIST FIELDS ====================

A List is a collection of available application data.

The behavior of a List depends on its contents.


==================== LIST WITH EXISTING VALUES ====================

If a List contains actual objects or values, those are the
ONLY values/objects that may be selected.

The List is a CLOSED SET.

If an object/value is not present in the provided List:

IT DOES NOT EXIST.

Therefore:

IT CANNOT BE RETURNED.


==================== LIST OBJECT SELECTION ====================

When selecting an existing object from a List:

COPY THE OBJECT EXACTLY.

Do NOT modify it.

Do NOT regenerate it.

Do NOT improve it.

Do NOT translate it.

Do NOT change any field.

Do NOT change any value.

Do NOT change IDs.

Do NOT change relationships.

Do NOT change nested objects.

Do NOT change nested Lists.


For example, if the provided List is:

[
    {
        "name": "Bob",
        "role": "gos",
        "lastname": "Admin"
    }
]

then the ONLY valid selected object is:

{
    "name": "Bob",
    "role": "gos",
    "lastname": "Admin"
}


These are FORBIDDEN:

{
    "name": "Alice",
    "role": "gos",
    "lastname": "Admin"
}

{
    "name": "Bob",
    "role": "engineer",
    "lastname": "Admin"
}

{
    "name": "Bob",
    "role": "gos",
    "lastname": "Smith"
}


The USER PROMPT may determine WHICH existing object is selected.

The USER PROMPT CANNOT modify the selected object.


==================== LIST AUTO_FILL ====================

A List may contain the special value:

"auto_Fill"

"auto_Fill" means:

FILL THIS LIST FIELD AUTOMATICALLY.

When a List contains "auto_Fill", the AI MAY generate
appropriate content for that List according to:

- the List object's structure
- the field names
- the field types
- the USER PROMPT


"auto_Fill" is NOT an actual value that must be returned
unless the structure specifically requires it.

It is an instruction to automatically generate the contents
of that List.


==================== AUTO_FILL RULES ====================

When "auto_Fill" is present in a List object:

The AI may generate values for fields according to their types.

For example:

[
    {
        "id": null,
        "stepName": "auto_Fill",
        "stepDescription": "auto_Fill"
    }
]

means the List item should be automatically filled.

The AI may generate:

[
    {
        "id": null,
        "stepName": "Cut material",
        "stepDescription": "Cut the material according to the required dimensions."
    }
]


However:

- null MUST remain null
- existing non-placeholder values MUST remain unchanged
- ENUM values MUST be selected from their provided values
- "None" means generate a String
- 0 means generate an Integer/Long
- 0.0 means generate a Decimal
- "auto_Fill" means automatically fill that field/object


==================== AUTO_FILL DOES NOT OVERRIDE IMMUTABILITY ====================

"auto_Fill" only allows automatic generation for the field/object
where "auto_Fill" is explicitly present.

It does NOT give permission to modify other existing values.

Example:

{
    "id": 25,
    "name": "auto_Fill",
    "description": "Existing description"
}

The valid result may be:

{
    "id": 25,
    "name": "Generated name",
    "description": "Existing description"
}

The ID MUST NOT change.

The existing description MUST NOT change.


==================== LIST CLOSED-WORLD RULE ====================

If a List does NOT contain "auto_Fill":

ONLY existing objects/values in that List may be selected.

The AI MUST NOT create new List objects.

The AI MUST NOT invent values.

The AI MUST NOT combine values from different objects.

The AI MUST NOT modify a selected object.


==================== ENUM FIELDS ====================

ENUM fields are ALWAYS CLOSED SETS.

The application will provide the available ENUM values.

The AI MUST ALWAYS select EXACTLY ONE ENUM value.

There are NO exceptions.

The AI MUST NOT:
- generate an ENUM value
- return null for an ENUM
- return multiple ENUM values
- invent an ENUM value
- modify an ENUM value
- translate an ENUM value
- create a similar ENUM value
- return "None"
- return "auto_Fill"


If the application provides:

["WOOD", "METAL", "PLASTIC"]

the AI MUST select exactly ONE:

"WOOD"

OR

"METAL"

OR

"PLASTIC"


The USER PROMPT determines which provided ENUM value
is the most appropriate.

The USER PROMPT CANNOT create a new ENUM value.


==================== NORMAL EXISTING VALUES ====================

If a field contains a value that is NOT:

"None"

and is NOT:

0

and is NOT:

0.0

and is NOT:

null

and is NOT:

"auto_Fill"

then the value is EXISTING DATA.

Existing data MUST NOT be changed.


==================== IMMUTABLE DATA ====================

The following are ALWAYS immutable unless explicitly marked
as a generation or auto-fill placeholder:

- IDs
- relationships
- existing String values
- existing numeric values
- existing List objects
- existing List object fields
- existing List object values
- existing nested objects
- existing nested Lists


==================== WHAT THE AI MAY GENERATE ====================

The AI may generate a new value ONLY when:

String = "None"

Integer = 0

Long = 0

Double = 0.0

Float = 0.0

OR

A field/object is explicitly marked with:

"auto_Fill"


Nothing else may be generated.


==================== WHAT THE AI MUST NEVER CHANGE ====================

The AI MUST NEVER change:

- an existing List object
- an existing List object's fields
- an existing List object's values
- an ID
- an existing ENUM value
- a relationship
- a normal existing value
- a null value


==================== JSON STRUCTURE ====================

The JSON structure MUST remain exactly the same.

Do NOT:
- add fields
- remove fields
- rename fields
- change object nesting
- change List structure
- convert Lists to Strings
- convert Objects to Strings
- convert Objects to Arrays
- convert Arrays to Objects

Java List = JSON array []

Java Object = JSON object {}

Nested List = nested JSON array []


==================== DECISION PROCESS ====================

Before generating the final JSON, internally determine
what type every field is.

For every field:

IF String == "None"
    -> GENERATE a new String

IF Integer/Long == 0
    -> GENERATE a new integer

IF Double/Float == 0.0
    -> GENERATE a new decimal

IF value == null
    -> RETURN null

IF field is ENUM
    -> SELECT EXACTLY ONE provided ENUM value

IF field is List AND it contains "auto_Fill"
    -> AUTOMATICALLY FILL according to the List object's structure

IF field is List AND it does NOT contain "auto_Fill"
    -> SELECT ONLY existing objects/values from that List
    -> COPY selected objects EXACTLY

IF value contains any other existing value
    -> KEEP the existing value unchanged


==================== CRITICAL RULE ====================

GENERATING DATA and SELECTING DATA are DIFFERENT OPERATIONS.

"None", 0 and 0.0 mean:

GENERATE A NEW VALUE.

"auto_Fill" means:

AUTOMATICALLY GENERATE/FILL THE MARKED FIELD OR LIST OBJECT.

A List containing existing values means:

SELECT ONLY FROM THE EXISTING VALUES.

An ENUM means:

SELECT EXACTLY ONE VALUE FROM THE PROVIDED ENUM VALUES.

Existing values mean:

KEEP THE EXISTING VALUE.

null means:

KEEP null.


==================== USER PROMPT ====================

Follow the USER PROMPT ONLY when:

1. deciding what generated values should be created;
2. deciding how an "auto_Fill" field should be filled;
3. deciding which existing List object/value should be selected;
4. deciding which ONE provided ENUM value should be selected.

The USER PROMPT NEVER gives permission to:

- modify existing data;
- create List objects when "auto_Fill" is absent;
- invent List values;
- modify selected List objects;
- invent ENUM values;
- return more than one ENUM value;
- replace null;
- modify IDs;
- modify relationships.


==================== PROVIDED APPLICATION DATA ====================

""");


        question.append("""

==================== END APPLICATION DATA ====================

Now process the USER PROMPT.

Generate values ONLY for:

- String fields containing "None"
- Integer/Long fields containing 0
- Double/Float fields containing 0.0
- fields or List objects explicitly containing "auto_Fill"

For List fields:

IF "auto_Fill" exists:
    -> automatically fill the marked field/object

OTHERWISE:
    -> select ONLY existing objects/values
    -> copy selected objects EXACTLY
    -> NEVER modify them
    -> NEVER create new objects

For ENUM fields:

-> ALWAYS select EXACTLY ONE value
-> ONLY use values provided by the application
-> NEVER invent an ENUM value
-> NEVER return null
-> NEVER return multiple values

For null:

-> return null

For all other existing values:

-> keep them unchanged

Return ONLY valid JSON.
""");









        T value = null;


        try {

            System.out.println("1");

            HttpClient client = HttpClient.newHttpClient();

            String json = mapper.writeValueAsString(
                    Map.of(
                            "model", "qwen3:4b-instruct",
                            "prompt", question,
                            "stream", false
                    )
            );

            System.out.println("2");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:11434/api/generate"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .timeout(Duration.ofSeconds(20))
                    .build();

            System.out.println("3");

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            System.out.println("4");

            System.out.println("================================");
            System.out.println(response.body());
            System.out.println("=================================");

            System.out.println("5");


            AiResponse aiResponse = mapper.readValue(response.body(), AiResponse.class);

            System.out.println("6");

            value = mapper.readValue(aiResponse.getResponse(), referenceClass);

            System.out.println("7");



        }catch (Exception e){

           // fillDataUsingAi(aiQuestion);
            System.out.println("AI RESPONSE FAILED RETRYING");
//           throw  new ValidationException("Something went wrong with your Ai request RETRYING ", Warnings.ERROR);
            System.out.println(e);

        }



        return value;

    }








}
