# jvis
Visual editor for JSON format

Main ideas:
- base on Jackson JSON framework (metadata + serialization\deserialization)
- include new schema-classes by classpath, no direct compilation or intermediate schema related data required 
- be simple 

Supporting right now:
- base types
- object fields
- polymorphic fields (interface + abstract classes)
- arrays (including array of polymorphic classes)
