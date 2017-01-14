JSON Circular Reference
=
License
-
Copyright 2017 Mike Baranski

http://mikeski.net (mike.baranski@gmail.com)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

About
-

This is the easy way to deal with circular object references
when using the Jackson V2 Java libraries.  The code is fairly
simple, and the unit test demonstrates the results.

Executing
-
This is a simple maven project.  To see it in action, simply clone
it, change into the project directory and run `mvn test`.

The problem
-

Given a Person bean and a Group bean that have a "many to many"
relationship, a circular reference is created by the Person having
a list of Groups and the Group having a list of Persons.

If we try to serialize these beans with Jackson's `ObjectMapper`,
we get a circular reference because each person that belongs to
a group is also in that group's list of people.  If we dump the 
whole object we get an infinite recursion exception:

>>com.fasterxml.jackson.databind.JsonMappingException: Infinite recursion (StackOverflowError) (through reference chain: java.util.ArrayList[0]->net.mikeski.domain.Group["people"]->java.util.ArrayList[0]->net.mikeski.domain.Person["groups"]->java.util.ArrayList[0]->net.mikeski.domain.Group["people"]-> ...

This can easilty be solved by adding an annotation that tells
Jackson to:
 
* Serialize each object 1 time
* Generate a unique id (a metadata field, if you will) for each
 serialized object
* If asked to serilize an object for a second time, Jackson
just writes the id generated for the object instead of the object
itself.

I typically use a UUID for the generated id field.  To do this for
the group class, this annotation does the trick:

    @JsonIdentityInfo(
        generator = ObjectIdGenerators.UUIDGenerator.class, 
        property = "@json_id"
    )
    public Class Group {
    ... code ...
    }
    
Now, when we serialize a person, we get this JSON:
    
    {
    	"id": 1,
    	"name": "Mike",
    	"groups": [{
    		"@json_id": "00e2b849-6c3f-41b9-ad4e-1c6b6286154b",
    		"id": 2,
    		"name": "First Group",
    		"people": [{
    			"id": 1,
    			"name": "Mike",
    			"groups": ["00e2b849-6c3f-41b9-ad4e-1c6b6286154b"]
    		}]
    	}]
    }
    
You can run the unit tests in this project and see that output.
This is a handy way to prevent circular references, because
you can see that the group `id: 2` is serilized one time and
given a `@json_id: 00e2b849-6c3f-41b9-ad4e-1c6b6286154b`.  

The
circular reference is fixed when the person is serialized
because instead of writing the Group as json it just writes
the id of the group, leaving it to the recipient to decompose
what the id means (if it needs it).

This allows the correct references to be preserved, and valid 
JSON to be generated for objects with circular references.

Decoding it is a pretty straightforward bit, in a REST client,
for instance.