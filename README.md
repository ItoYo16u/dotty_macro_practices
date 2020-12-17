# dotty (macro) playground

## about
Repo to try dotty's macro.

/src/main/scala

- json: [WIP] add toJson extension to case classes
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
