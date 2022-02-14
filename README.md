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
| BSP                                                              | Build site                                                                                                     |
| :--------------------------------------------------------------- | :------------------------------------------------------------------------------------------------------------- |
| [meta-raspberrypi](https://github.com/agherzan/meta-raspberrypi) | ![Build meta-raspberrypi](https://github.com/NobuoTsukamoto/meta-tensorflow-lite/actions/workflows/build_rpi.yml/badge.svg) |
| [meta-riscv](https://github.com/riscv/meta-riscv)                | ![Build mata-riscv](https://github.com/NobuoTsukamoto/meta-tensorflow-lite/actions/workflows/build_riscv.yml/badge.svg) |

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
  IMAGE_INSTALL:append = " python3-tensorflow-lite"

# Build
$ bitbake core-image-weston
```

### Quick start for the qemuriscv64
```
# Clone repositories and oe-init-build-env
$ git clone https://github.com/riscv/meta-riscv.git
$ git clone https://github.com/openembedded/openembedded-core.git
$ git clone https://github.com/openembedded/bitbake.git
$ git clone https://github.com/openembedded/meta-openembedded.git
$ bitbake-layers add-layer ../meta-tensorflow-lite/
$ openembedded-core/oe-init-build-env build

# Add layer
$ bitbake-layers add-layer ../meta-openembedded/meta-oe/
$ bitbake-layers add-layer ../meta-openembedded/meta-python/
$ bitbake-layers add-layer ../meta-openembedded/meta-networking/
$ bitbake-layers add-layer ../meta-openembedded/meta-multimedia/
$ bitbake-layers add-layer ../meta-raspberrypi/
$ bitbake-layers add-layer ../meta-tensorflow-lite/

# Add the package to 'conf/auto.conf' file. 
  MACHINE ?= "qemuriscv64"
  IMAGE_INSTALL:append = " python3-tensorflow-lite"

# Build
$ bitbake core-image-full-cmdline
```
