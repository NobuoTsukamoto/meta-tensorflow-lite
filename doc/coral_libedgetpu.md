# bitbake with libedgetpu

## Reference

- [Building with only a Makefile - google-coral/libedgetpu](https://github.com/google-coral/libedgetpu/blob/master/makefile_build/README.md)

## How to
Build sample on Raspberry Pi 4 AArch64 (core-image-weston).

### Clone repositories and oe-init-build-env.
```
git clone git://git.yoctoproject.org/poky.git
git clone git://git.yoctoproject.org/meta-raspberrypi
git clone git://git.openembedded.org/meta-openembedded
git clone https://github.com/NobuoTsukamoto/meta-tensorflow-lite.git
source poky/oe-init-build-env build
```

### Add layer
```
bitbake-layers add-layer ../meta-openembedded/meta-oe/
bitbake-layers add-layer ../meta-openembedded/meta-python/
bitbake-layers add-layer ../meta-openembedded/meta-networking/
bitbake-layers add-layer ../meta-openembedded/meta-multimedia/
bitbake-layers add-layer ../meta-raspberrypi/
bitbake-layers add-layer ../meta-tensorflow-lite/
```

### Create conf/auto.conf file and write config
Add `python3-tensorflow-lite` and `libedgetpu-nnn` recipes to `conf/auto.conf` file.  
- Note: nnn is `max` or `std`.
  - libedgetpu-std: with reduced operating frequency ()
  - libedgetpu-max: with maximum operating frequency
```
IMAGE_INSTALL:append = " python3-tensorflow-lite libedegtpu-std"
```

### Bitbake
```
MACHINE=raspberrypi4-64 bitbake core-image-weston
```

### Warning
If you are using Coral USB Accelerator, please check the following notes.  

- [Install with maximum operating frequency (optional) - Coarl Get started with the USB Accelerator](https://coral.ai/docs/accelerator/get-started/#runtime-on-linux)
- [Warning - google-coral/libedgetpu](https://github.com/google-coral/libedgetpu#warning)