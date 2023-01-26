## How to tun the application

```shell
./gradlew gatlingRun-simulations.TextPlain
```

## How to change concurrent users per step

```shell
./gradlew gatlingRun-simulations.TextPlain -Pusers=128
```

By default, the concurrent users per step are `512`, which means the max users are `4096`

## How to enable HTTP/2

```shell
./gradlew gatlingRun-simulations.TextPlain -Phttp2
```

By default, the configured protocol is `HTTP/1.1`

## How to change the port on which the server listens

```shell
./gradlew gatlingRun-simulations.TextPlain -Pport=9090
```

By default, the port `8080` is used as a target server port.

## How to change the server host

```shell
./gradlew gatlingRun-simulations.TextPlain -Phost=x.x.x.x
```

By default, the host `127.0.0.1` is used as a target server host.