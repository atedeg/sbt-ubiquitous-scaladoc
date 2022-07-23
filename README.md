# ubiquitous-scaladoc

## Usage  
To use this plugin:
```
// TODO: code to include in sbt 
```

## Configuration format  
You can instruct the plugin on how to build the markdown tables via a `.ubidoc.yaml` (or `.ubidoc.yml`) configuration file that must be in the project's root. The file must have the following structure:

```yaml 
tables: <list of table objects>
ignored: <list of specifiers>
```

A `table object` has the following structure:
```yaml
name: "the table's name"
termName: "the name used for the term column, if not specified defaults to 'Term'"
definitionName: "the name used for the definition column, if not specified defaults to 'Definition'"
rows: <list of specifier>
```

A `specifier` is used to denote a an entity of the domain, when adding a specifier to the rows of a table its 
name will be included in the table's term column and its documentation will be included in the definition's column.
Currently the supported specifiers cover most of Scala's structures: 
```yaml 
//A list of all possible specifiers: 
- class: "ClassName"
- enum: "EnumName"
- case: "CaseName"
- trait: "TraitName"
- type: "TypeName"
- def: "DefName"
```

## How the plugin works

For each specifier in the table's rows the plugin looks at its documentation (scraped from the files generated by <TODO: @linda help>) 
and adds a row in the generated markdown table. The name of the entity is normalized by splitting it on uppercase letters so that it is no longer
in camel case.

If, by the end of the process that generates all tables there are leftover entities that were neither in any table nor in the ignored list
the plugin issues a warning listing all those entities. This is used as a sanity check since one may forget to include an important domain
concept. To avoid having any warning one must explicitly specify which entities have to be ignored by adding their specifiers to the 
`ignored` list.
