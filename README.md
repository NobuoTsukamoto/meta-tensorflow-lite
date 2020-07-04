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

# Pybind11 is required to build the TensorFlow Lite Pip package, 
# so add "native nativesdk" to python-pybind11 recipe
# Edit 'meta-python/recipes-devtools/python/python-pybind11.inc' file and
# add 'BBCLASSEXTEND = "native nativesdk"'
  diff --git a/meta-python/recipes-devtools/python/python-pybind11.inc b/meta-python/recipes-devtools/python/python-pybind11.inc
  index d1d53e125..7f6e3adad 100644
  --- a/meta-python/recipes-devtools/python/python-pybind11.inc
  +++ b/meta-python/recipes-devtools/python/python-pybind11.inc
  @@ -5,3 +5,6 @@ LIC_FILES_CHKSUM = "file://LICENSE;md5=beb87117af69fd10fbf9fb14c22a2e62"
 
  SRC_URI[md5sum] = "23fdca8191b16ce3e7f38fb9e4252b2d"
  SRC_URI[sha256sum] = "72e6def53fb491f7f4e92692029d2e7bb5a0783314f20d80222735ff10a75758"
  +
  +BBCLASSEXTEND = "native nativesdk"
  +

  # Add the package to 'conf/local.conf' file. 
    MACHINE ?= "raspberrypi4-64"
    IMAGE_INSTALL_append = " python3-tensorflow-lite"

  # Build
  $ bitbake core-image-weston
```

## Notes
- At this time, only aarch64 has been confirmed to be built.
