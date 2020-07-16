# meta-tensorflow-lite (build pip package)

Yocto layer for the TensorFlow Lite interpreter with Python.  

The official website is:
- [TensorFlow Lite guide](https://www.tensorflow.org/lite/guide)
- [Python quickstart](https://www.tensorflow.org/lite/guide/python)
- [TensorFlow Lite](https://github.com/tensorflow/tensorflow/tree/master/tensorflow/lite)

Reference
- [Building TensorFlow Lite Standalone Pip](https://github.com/tensorflow/tensorflow/tree/master/tensorflow/lite/tools/pip_package)

## How to

Quick start for the Raspberry Pi AArch64 (core-image-weston)
```
# Clone repositories and oe-init-build-env
$ git clone git://git.yoctoproject.org/poky.git
$ git clone git://git.yoctoproject.org/meta-raspberrypi
$ git clone git://git.openembedded.org/meta-openembedded
$ git clone https://github.com/NobuoTsukamoto/meta-tensorflow-lite.git
$ source poky/oe-init-build-env rpi-build

# Add layer
$ bitbake-layers add-layer ../meta-openembedded/meta-oe/
$ bitbake-layers add-layer ../meta-openembedded/meta-python/
$ bitbake-layers add-layer ../meta-openembedded/meta-networking/
$ bitbake-layers add-layer ../meta-openembedded/meta-multimedia/
$ bitbake-layers add-layer ../meta-raspberrypi/
$ bitbake-layers add-layer ../meta-tensorflow-lite/

# Add the package to 'conf/local.conf' file. 
  MACHINE ?= "raspberrypi4-64"
  IMAGE_INSTALL_append = " python3-tensorflow-lite"

# Build
$ bitbake core-image-weston
```

## Notes
- Check only raspberrypi4-64 (aarch64) and raspberrypi4 (armv7l)

