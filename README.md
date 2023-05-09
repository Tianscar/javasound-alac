# Java Implementation of Apple Lossless Decoder

This is a fork of [Java-Apple-Lossless-decoder](https://github.com/soiaf/Java-Apple-Lossless-decoder), with JavaSound SPI support.

JALD is a Java implementation of Apple Lossless decoder. 
It is ported from v0.2.0 of the Apple Lossless decoder written by David Hammerton.
It supports both 16-bit and 24-bit Apple Lossless files.

## Add the library to your project (gradle)
1. Add the Maven Central repository (if not exist) to your build file:
```groovy
repositories {
    ...
    mavenCentral()
}
```

2. Add the dependency:
```groovy
dependencies {
    ...
    implementation 'com.tianscar.javasound:jald:0.2.1'
}
```

## Usage
[Tests and Examples](/src/test/java/com/beatofthedrum/alacdecoder/test)  
[Command-line interfaces](/src/test/com/beatofthedrum/alacdecoder/cli)

## License
[BSD 3-Clause](/LICENSE)  
[audios for test](/src/test/resources) originally created by [ProHonor](https://github.com/Aislandz), authorized [me](https://github.com/Tianscar) to use. 2023 (c) ProHonor, all rights reserved.