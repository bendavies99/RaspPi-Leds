## [1.1.13](https://github.com/bendavies99/RaspPi-Leds/compare/v1.1.12...v1.1.13) (2020-07-26)


### Bug Fixes

* Allow ReactiveLEDs to have unlimited leds ([31d6e46](https://github.com/bendavies99/RaspPi-Leds/commit/31d6e46482c0723f64d4392a726ab06023908af6))

## [1.1.12](https://github.com/bendavies99/RaspPi-Leds/compare/v1.1.11...v1.1.12) (2020-07-12)


### Bug Fixes

* Ensure that a strip is rendered blank if off ([cda704c](https://github.com/bendavies99/RaspPi-Leds/commit/cda704c1afaa3bcc076901261f95f08cb70212ab))

## [1.1.11](https://github.com/bendavies99/RaspPi-Leds/compare/v1.1.10...v1.1.11) (2020-07-12)


### Bug Fixes

*  Ping mqtt broker every 5 seconds, add gpio pin 9 to strip ([33d3b06](https://github.com/bendavies99/RaspPi-Leds/commit/33d3b06c513cde4bc3fbb8f5f5485a6e5b64f6af))

## [1.1.10](https://github.com/bendavies99/RaspPi-Leds/compare/v1.1.9...v1.1.10) (2020-07-12)


### Bug Fixes

* Brightness and Color change really buggy, and when the strip is off render black for interference ([10fcacf](https://github.com/bendavies99/RaspPi-Leds/commit/10fcacf327455420d9631baa82e7f1f8ba5241c6))
* Issues regarding MQTT Connectivity ([ccd6952](https://github.com/bendavies99/RaspPi-Leds/commit/ccd6952a3f4b4f7213ad4fc9627f8900fa87fb15))

## [1.1.9](https://github.com/bendavies99/RaspPi-Leds/compare/v1.1.8...v1.1.9) (2020-06-28)


### Bug Fixes

* pin 21 use pwm channel 0 ([a2ed854](https://github.com/bendavies99/RaspPi-Leds/commit/a2ed854d7f7ccfc04477f6260778114c7c3b6e04))

## [1.1.8](https://github.com/bendavies99/RaspPi-Leds/compare/v1.1.7...v1.1.8) (2020-06-28)


### Bug Fixes

* Use different for different strips ([17df8b5](https://github.com/bendavies99/RaspPi-Leds/commit/17df8b5e923f17200dee84fe4871678f78e62ab1))

## [1.1.7](https://github.com/bendavies99/RaspPi-Leds/compare/v1.1.6...v1.1.7) (2020-06-28)


### Bug Fixes

* Use higher DMA channels when adding more strips ([3eb976d](https://github.com/bendavies99/RaspPi-Leds/commit/3eb976d5899bc6451aa021fcca220982adf02a02))

## [1.1.6](https://github.com/bendavies99/RaspPi-Leds/compare/v1.1.5...v1.1.6) (2020-06-28)


### Bug Fixes

* Brightness returning to normal was quite slow, and solid was not rendering from off to on ([e50e7be](https://github.com/bendavies99/RaspPi-Leds/commit/e50e7be9478ecc5197f01cefd99b000920309903))

## [1.1.5](https://github.com/bendavies99/RaspPi-Leds/compare/v1.1.4...v1.1.5) (2020-06-26)


### Bug Fixes

* Quicker brightness change when decaying ([77e72c3](https://github.com/bendavies99/RaspPi-Leds/commit/77e72c3d7070527d57915575f8ba9c8777f88d2b))

## [1.1.4](https://github.com/bendavies99/RaspPi-Leds/compare/v1.1.3...v1.1.4) (2020-06-25)


### Bug Fixes

* Made brightness transitions faster ([d79ba80](https://github.com/bendavies99/RaspPi-Leds/commit/d79ba8026b114b66127de477fdc6350961a2b30f))

## [1.1.3](https://github.com/bendavies99/RaspPi-Leds/compare/v1.1.2...v1.1.3) (2020-06-25)


### Bug Fixes

* remove logging from brightness change ([07331fd](https://github.com/bendavies99/RaspPi-Leds/commit/07331fdbd32e6b23d81289bea4baec8929232b89))
* remove logging from brightness change and close the UDP Socket ([e162e1c](https://github.com/bendavies99/RaspPi-Leds/commit/e162e1c04b5e4f21fb8af0cd859acb9840e1b38c))

## [1.1.2](https://github.com/bendavies99/RaspPi-Leds/compare/v1.1.1...v1.1.2) (2020-06-25)


### Bug Fixes

* fixed brightness and color changes ([096befd](https://github.com/bendavies99/RaspPi-Leds/commit/096befd93c820cc68c098ed119f276768aaee78f))

## [1.1.1](https://github.com/bendavies99/RaspPi-Leds/compare/v1.1.0...v1.1.1) (2020-06-25)


### Bug Fixes

* Dont swap over the rpi library as this may have to be built on the pi using the program ([29a4d03](https://github.com/bendavies99/RaspPi-Leds/commit/29a4d0302aaf128d97c23482ab0b8bcc71cf5503))
* Solid only renders if the color has changed ([dc21102](https://github.com/bendavies99/RaspPi-Leds/commit/dc21102686ecd4a9bd3c42f35f197e8ccca03db4))
* Strip use solid by default ([d318112](https://github.com/bendavies99/RaspPi-Leds/commit/d3181127f7ee94b7ce51bc91fc56ac452351545d))
* Uncommented PollingService ([9ad6e7e](https://github.com/bendavies99/RaspPi-Leds/commit/9ad6e7ea332339fbbf5abc2a9de16c165885ded1))
* Updated FPS to 30 fps to improve performance ([d1b6c3d](https://github.com/bendavies99/RaspPi-Leds/commit/d1b6c3de98afb59ad7dcf881d30e43f01cc95917))


### Performance Improvements

* Lowered FPS down to 15 FPS ([c70df2c](https://github.com/bendavies99/RaspPi-Leds/commit/c70df2c27332fd84687aad8d9b235e2791117247))
* Removed sync from strip methods ([ae87e46](https://github.com/bendavies99/RaspPi-Leds/commit/ae87e46070d8855d86819044911b63df09996ac0))


### Reverts

* Removed FPS Sleep from StripPanel ([4d36ccc](https://github.com/bendavies99/RaspPi-Leds/commit/4d36ccce71b07392b173194594224ce3cbf588c4))

# [1.1.0](https://github.com/bendavies99/RaspPi-Leds/compare/v1.0.8...v1.1.0) (2020-06-24)


### Bug Fixes

* Fixed performance issues ([f296cb4](https://github.com/bendavies99/RaspPi-Leds/commit/f296cb4784de32a73562074638c4196e8f5c6808))


### Features

* Reactive Effect for listening to music ([8ab455e](https://github.com/bendavies99/RaspPi-Leds/commit/8ab455e217938d72367a9f397fda62eab15c1665))

## [1.0.8](https://github.com/bendavies99/RaspPi-Leds/compare/v1.0.7...v1.0.8) (2020-06-21)


### Bug Fixes

* Separated out the brightness and color change ([fa7636d](https://github.com/bendavies99/RaspPi-Leds/commit/fa7636d146e643817b5542616ef7ef5c2472ec17))

## [1.0.7](https://github.com/bendavies99/RaspPi-Leds/compare/v1.0.6...v1.0.7) (2020-06-21)


### Bug Fixes

* Updater not working after restart ([c7c7de6](https://github.com/bendavies99/RaspPi-Leds/commit/c7c7de6de6d92b448bf62f81b5189806ed80e62a))

## [1.0.6](https://github.com/bendavies99/RaspPi-Leds/compare/v1.0.5...v1.0.6) (2020-06-21)


### Bug Fixes

* Allow spi pin ([2bc8673](https://github.com/bendavies99/RaspPi-Leds/commit/2bc867335bcaef75e22fa97346b915ed803a0eb2))

## [1.0.5](https://github.com/bendavies99/RaspPi-Leds/compare/v1.0.4...v1.0.5) (2020-06-20)


### Bug Fixes

* Brightness was not being set ([0befac2](https://github.com/bendavies99/RaspPi-Leds/commit/0befac269759c375840eafa1da3920acc398b666))

## [1.0.4](https://github.com/bendavies99/RaspPi-Leds/compare/v1.0.3...v1.0.4) (2020-06-20)


### Bug Fixes

* Added synchronised to all Strip functions ([d648186](https://github.com/bendavies99/RaspPi-Leds/commit/d648186c583b543eb252a2337af972724b76d385))

## [1.0.3](https://github.com/bendavies99/RaspPi-Leds/compare/v1.0.2...v1.0.3) (2020-06-19)


### Bug Fixes

* Version not being added to jar file ([b98215b](https://github.com/bendavies99/RaspPi-Leds/commit/b98215bf83b21279906d126af5ba3f3a06b3fb3b))

## [1.0.2](https://github.com/bendavies99/RaspPi-Leds/compare/v1.0.1...v1.0.2) (2020-06-19)


### Bug Fixes

* Use Java 8 for travis ([c62816c](https://github.com/bendavies99/RaspPi-Leds/commit/c62816c16ddd41ac2e954ba157d4cc3f03965903))

## [1.0.1](https://github.com/bendavies99/RaspPi-Leds/compare/v1.0.0...v1.0.1) (2020-06-19)


### Bug Fixes

* File paths regarding release assets ([767080e](https://github.com/bendavies99/RaspPi-Leds/commit/767080e93d9c1449a45ec54016d82828a62a348e))

# 1.0.0 (2020-06-19)


### Bug Fixes

* add node modules to git ignore ([66bcbf9](https://github.com/bendavies99/RaspPi-Leds/commit/66bcbf998eb5a2d0aebda90998066d38f9af40dd))
* add package-json to project ([2c69463](https://github.com/bendavies99/RaspPi-Leds/commit/2c69463e5f82c200c693cb784433530b7b144066))
* change mod +x to gradlew ([9a46bd9](https://github.com/bendavies99/RaspPi-Leds/commit/9a46bd9f3bfae5e67c609da415c9e54abce0f282))
* Starting at version 1 ([90dda51](https://github.com/bendavies99/RaspPi-Leds/commit/90dda518232a6e35f1b8b17121d5e1ac2f80a3b6))
* Updated rpi-library to match one on maven repo ([bda4647](https://github.com/bendavies99/RaspPi-Leds/commit/bda4647112ee3b08042ab08ca07ba5b1c0dd1d37))
* Versioning not being used because of plugin ([e0696c3](https://github.com/bendavies99/RaspPi-Leds/commit/e0696c32da1de0405d6521eb86753a90985ee986))


### Features

* Added bintray publishing ([540cb29](https://github.com/bendavies99/RaspPi-Leds/commit/540cb29b2b0e2e575f7ce79bd71b46d045d42f10))
* Added effects, updater, mqtt support, and HA support ([9a00574](https://github.com/bendavies99/RaspPi-Leds/commit/9a00574be407ffbb96c3c954814ebc721b4f6f54))
* **config:** Added config ([bc85c97](https://github.com/bendavies99/RaspPi-Leds/commit/bc85c9761841eb93be0a2975f8e11bf301fe8f5e))
