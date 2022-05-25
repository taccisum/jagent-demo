# javaagent demo

Simple demo about javaagent usage(with javassist).

## How

    $ mvn clean package
    $ java -javaagent:$PWD/target/myagent.jar="myargs" \
        -classpath $PWD/target/classes:$PWD/target/lib/javassist-3.29.0-GA.jar \
        com.github.tac.jagent.demo.Main hello

不出意外的话，可以看到 `com.github.tac.jagent.demo.Main#sayhello` 的行为已经被修改了

### Fat agent

Fat agent 位于 `/path/to/target/myfatagent.jar`，其中已经将 agent 本身的依赖打包进去了，因此使用时无需再在 `-classpath` 中指定依赖包的路径

    $ java -javaagent:$PWD/target/myfatagent.jar="myargs" \
        -classpath $PWD/target/classes \
        com.github.tac.jagent.demo.Main hello
