# javaagent demo

Simple demo about javaagent usage(with javassist).

## How

    $ mvn clean package
    $ java -javaagent:$PWD/target/myagent.jar="myargs" \
        -classpath $PWD/target/classes:$PWD/target/lib/javassist-3.29.0-GA.jar \
        com.github.tac.jagent.demo.Main hello



