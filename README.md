# Java Implementation of Apple Lossless Decoder
This is a fork of [Java-Apple-Lossless-decoder](https://github.com/soiaf/Java-Apple-Lossless-decoder), with JavaSound SPI support.

This library is a Java implementation of Apple Lossless decoder. It is ported from v0.2.0 of the Apple Lossless decoder written by David Hammerton. It supports both 16-bit and 24-bit Apple Lossless files.

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
    implementation 'com.tianscar.javasound:javasound-alac:0.2.2'
}
```

## Usage
[Tests and Examples](/src/test/java/com/beatofthedrum/alacdecoder/test)  
[Command-line interfaces](/src/test/java/com/beatofthedrum/alacdecoder/cli)

Note you need to download test audios [here](https://github.com/Tianscar/fbodemo1) and put them to /src/test/resources to run the test code properly!

## License
[BSD 3-Clause](/LICENSE)

### Dependencies
| Library                                                                    | License | Comptime | Runtime |
|----------------------------------------------------------------------------|---------|----------|---------|
| [JavaSound ResLoader SPI](https://github.com/Tianscar/javasound-resloader) | MIT     | Yes      | Yes     |
