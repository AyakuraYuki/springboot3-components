# springboot3-components

Spring Boot application components

## Requirements

- Spring Boot 3.5.0
- JDK 21

## Modules

- boot
- errors

## Build

```shell
CI_COMMIT_REF_NAME=x.y.z # build version, like 1.0.0
mvn -B -T 1C clean package -Drevision=${CI_COMMIT_REF_NAME}
```

or

```shell
# x.y.z is the build version, like 1.0.0
VERSION=x.y.z make package
```

Optional parameters:

- `-DskipTests`: compile tests without running those
- `-Dmaven.test.skip=true` - skip tests in compiling and running
- `-Dautoconfig.skip` - skip the AutoConfig plugin
