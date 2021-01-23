# dotty playground

## about
Playground repository to try new features that dotty offers such as macro, extension and 
new type class syntax(`summon` and `given`).

## memo
### Macro
- Currently, Dotty macro only supports so called ``def macro``. Dotty macro has yet to support Macro Annotation.
- Dotty macro has concept of ``quotes`` and ``splicing`` 
to programmatically manipulate AST in compile time.

- ``quotes``(= ``'``) extracts AST as ``Expr`` class which is pattern-matchable
and ``splicing``=(=``$``) evaluates ``Expr``.

- Macros with Quoted expression require to correctly use ``level``. 
  If ``level`` is invalid, it is impossible to run macros.
- Some functionalities in Scala 2.x's macro such as TypeTag is updated in ``scala.quoted`` package.

/src/main/scala

- json: add toJson extension to case classes
  - for example: ``Person("id","john",1,",Box("1")).toJson`` 
    should returns 
    {"id":"id","name":"jhon","age":1,"box": {"id": "1"}}
- macro:
  - debugSingle: show expr of argument and value
## sbt project compiled with Dotty

### Usage

This is a normal sbt project, you can compile code with `sbt compile` and run it
with `sbt run`, `sbt console` will start a Dotty REPL.

For more information on the sbt-dotty plugin, see the
[dotty-example-project](https://github.com/lampepfl/dotty-example-project/blob/master/README.md).
