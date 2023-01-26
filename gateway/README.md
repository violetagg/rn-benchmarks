## How to run the application

```shell
./gradlew run
```

```shell
cd build/libs
java -jar gateway-1.0-SNAPSHOT-all.jar
```

This command will run the application with
- Reactor Core 3.5.4
- Reactor Netty 1.1.5
- HTTP/1.1 as a supported protocol

## How to change Reactor Core/Netty dependency version

```shell
./gradlew run -PforceReactorNettyVersion=1.1.6-SNAPSHOT
```

```shell
./gradlew run -PforceReactorCoreVersion=3.5.5-SNAPSHOT
```

## How to enable HTTP/2

```shell
./gradlew run -Phttp2
```

```shell
cd build/libs
java -Dhttp2 -jar gateway-1.0-SNAPSHOT-all.jar
```

By default, the server is configured to support `HTTP/1.1`

## How to change the port on which the server listens

```shell
./gradlew run -Pport=9090
```

```shell
cd build/libs
java -Dport=9090 -jar gateway-1.0-SNAPSHOT-all.jar
```

By default, the server listens on port `8080`.

## How to change the server host

```shell
./gradlew run -Phost=x.x.x.x
```

```shell
cd build/libs
java -Dhost=x.x.x.x -jar gateway-1.0-SNAPSHOT-all.jar
```

By default, the server host is configured to be `127.0.0.1`.