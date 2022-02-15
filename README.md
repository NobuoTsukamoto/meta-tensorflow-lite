# meta-tensorflow-lite

Yocto layer for the TensorFlow Lite interpreter with Python / C++.

## The official website is:
- [TensorFlow Lite guide](https://www.tensorflow.org/lite/guide)
- [Python quickstart](https://www.tensorflow.org/lite/guide/python)
- [TensorFlow Lite](https://github.com/tensorflow/tensorflow/tree/master/tensorflow/lite)

## Reference
- [Building TensorFlow Lite Standalone Pip](https://github.com/tensorflow/tensorflow/tree/master/tensorflow/lite/tools/pip_package)
- [Build TensorFlow Lite with CMake](https://github.com/tensorflow/tensorflow/blob/master/tensorflow/lite/g3doc/guide/build_cmake.md)

## Available BSP
Please note that it is not official support.
| BSP                                                              | Build status                                                                                                   |
| :--------------------------------------------------------------- | :------------------------------------------------------------------------------------------------------------- |
| [meta-raspberrypi](https://github.com/agherzan/meta-raspberrypi) | [![Bitbake raspberrypi](https://github.com/NobuoTsukamoto/meta-tensorflow-lite/actions/workflows/build_rpi.yml/badge.svg?branch=honister)](https://github.com/NobuoTsukamoto/meta-tensorflow-lite/actions/workflows/build_rpi.yml) |
| [meta-riscv](https://github.com/riscv/meta-riscv)                | [![Bitbake qemuriscv](https://github.com/NobuoTsukamoto/meta-tensorflow-lite/actions/workflows/build_riscv.yml/badge.svg?branch=honister)](https://github.com/NobuoTsukamoto/meta-tensorflow-lite/actions/workflows/build_riscv.yml) |

## Available recipes
- [Python3 interpreter](recipes-framework/tensorflow-lite/python3-tensorflow-lite_2.8.0.bb)
- [C++ API shared library](recipes-framework/tensorflow-lite/cpp-tensorflow-lite_2.8.0.bb)

## How to

### Quick start for the Raspberry Pi AArch64 (core-image-weston)
```
# Clone repositories and oe-init-build-env
$ git clone git://git.yoctoproject.org/poky.git
$ git clone git://git.yoctoproject.org/meta-raspberrypi
$ git clone git://git.openembedded.org/meta-openembedded
$ git clone https://github.com/NobuoTsukamoto/meta-tensorflow-lite.git
$ source poky/oe-init-build-env build

# Add layer
$ bitbake-layers add-layer ../meta-openembedded/meta-oe/
$ bitbake-layers add-layer ../meta-openembedded/meta-python/
$ bitbake-layers add-layer ../meta-openembedded/meta-networking/
$ bitbake-layers add-layer ../meta-openembedded/meta-multimedia/
$ bitbake-layers add-layer ../meta-raspberrypi/
$ bitbake-layers add-layer ../meta-tensorflow-lite/

# Add the package to 'conf/auto.conf' file. 
MACHINE ?= "raspberrypi4-64"
IMAGE_INSTALL:append = " python3-tensorflow-lite cpp-tensorflow-lite"

# Build
$ bitbake core-image-weston
```

### Quick start for the qemuriscv64
```
# Clone repositories and oe-init-build-env
$ git clone https://github.com/openembedded/bitbake.git
$ git clone https://github.com/openembedded/openembedded-core.git
$ git clone https://github.com/openembedded/meta-openembedded.git
$ git clone https://github.com/riscv/meta-riscv.git
$ git clone https://github.com/NobuoTsukamoto/meta-tensorflow-lite.git
$ openembedded-core/oe-init-build-env build

# Add layer
$ bitbake-layers add-layer ../meta-openembedded/meta-oe/
$ bitbake-layers add-layer ../meta-openembedded/meta-python/
$ bitbake-layers add-layer ../meta-openembedded/meta-networking/
$ bitbake-layers add-layer ../meta-openembedded/meta-multimedia/
$ bitbake-layers add-layer ../meta-riscv/
$ bitbake-layers add-layer ../meta-tensorflow-lite/

# Add the package to 'conf/auto.conf' file. 
MACHINE ?= "qemuriscv64"
IMAGE_INSTALL:append = " python3-tensorflow-lite  cpp-tensorflow-lite"

# Build
$ bitbake core-image-full-cmdline
```
