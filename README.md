# meta-tensorflow-lite

Yocto layer for the TensorFlow Lite interpreter with Python / C++.

## The official website is:
- [TensorFlow Lite guide](https://www.tensorflow.org/lite/guide)
- [Python quickstart](https://www.tensorflow.org/lite/guide/python)
- [TensorFlow Lite](https://github.com/tensorflow/tensorflow/tree/master/tensorflow/lite)
- [Google Coral](https://coral.ai/)
- [Edge TPU runtime library (libedgetpu)](https://github.com/google-coral/libedgetpu)

## Reference
- [Building TensorFlow Lite Standalone Pip](https://github.com/tensorflow/tensorflow/tree/master/tensorflow/lite/tools/pip_package)
- [Build TensorFlow Lite with CMake](https://github.com/tensorflow/tensorflow/blob/master/tensorflow/lite/g3doc/guide/build_cmake.md)
- [Building with only a Makefile - libedgetpu](https://github.com/google-coral/libedgetpu/blob/master/makefile_build/README.md)

## Available BSP
Please note that it is not official support.
| BSP                                                              | Build status                                                                                                   |
| :--------------------------------------------------------------- | :------------------------------------------------------------------------------------------------------------- |
| [meta-raspberrypi](https://github.com/agherzan/meta-raspberrypi) | [![Bitbake raspberrypi](https://github.com/NobuoTsukamoto/meta-tensorflow-lite/actions/workflows/build_rpi.yml/badge.svg?branch=main)](https://github.com/NobuoTsukamoto/meta-tensorflow-lite/actions/workflows/build_rpi.yml) |
| [meta-riscv](https://github.com/riscv/meta-riscv)                | [![Bitbake qemuriscv](https://github.com/NobuoTsukamoto/meta-tensorflow-lite/actions/workflows/build_riscv.yml/badge.svg?branch=main)](https://github.com/NobuoTsukamoto/meta-tensorflow-lite/actions/workflows/build_riscv.yml) |

## Available recipes
- framework
  - python3-tensorflow-lite  
    [Python3 interpreter](recipes-framework/tensorflow-lite/python3-tensorflow-lite_2.9.0.bb)
  - libtensorflow-lite  
    [C++ API shared library](recipes-framework/tensorflow-lite/libtensorflow-lite_2.9.0.bb)
  - libedgetpu-max / libedgetpu-std  
    [bitbake with libedgetpu](./doc/coral_libedgetpu.md)
- examples
  - python3-tensorflow-lite-example  
    [TensorFlow Lite Python image classification demo](./doc/python3-tensorflow-lite-example.md)
  - tensorflow-lite-label-image  
    [TensorFlow Lite C++ image classification demo](./doc/tensorflow-lite-label-image.md)
  - tensorflow-lite-minimal  
    [TensorFlow Lite C++ minimal example](./doc/tensorflow-lite-minimal.md)
- tools
  - tensorflow-lite-benchmark  
    [TFLite Model Benchmark Tool with C++ Binary](./doc/tensorflow-lite-benchmark.md)

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
FORTRAN:forcevariable = ",fortran"
MACHINE ?= "raspberrypi4-64"
IMAGE_INSTALL:append = " python3-tensorflow-lite libtensorflow-lite"

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
$ source openembedded-core/oe-init-build-env build

# Add layer
$ bitbake-layers add-layer ../meta-openembedded/meta-oe/
$ bitbake-layers add-layer ../meta-openembedded/meta-python/
$ bitbake-layers add-layer ../meta-openembedded/meta-networking/
$ bitbake-layers add-layer ../meta-openembedded/meta-multimedia/
$ bitbake-layers add-layer ../meta-riscv/
$ bitbake-layers add-layer ../meta-tensorflow-lite/

# Add the package to 'conf/auto.conf' file. 
FORTRAN:forcevariable = ",fortran"
MACHINE ?= "qemuriscv64"
IMAGE_INSTALL:append = " python3-tensorflow-lite  libtensorflow-lite"

# Build
$ bitbake core-image-full-cmdline
```
