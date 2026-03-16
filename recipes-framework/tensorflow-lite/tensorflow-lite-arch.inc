# Common architecture settings for TensorFlow Lite
# This includes architecture-specific EXTRA_OECMAKE overrides and TENSORFLOW_TARGET_ARCH definitions.

# Note:
# XNNPack is valid only on aarch64 and RISC-V .
# In the case of arm 32bit, it will be turned off because the build will be
# an error depending on the combination of target CPUs.
EXTRA_OECMAKE:append:arm = " -DTFLITE_ENABLE_RUY=ON"
EXTRA_OECMAKE:append:aarch64 = " -DTFLITE_ENABLE_XNNPACK=ON"
EXTRA_OECMAKE:append:riscv32 = " -DTFLITE_ENABLE_XNNPACK=ON"
EXTRA_OECMAKE:append:riscv64 = " -DTFLITE_ENABLE_XNNPACK=ON"
EXTRA_OECMAKE:append:x86-64 = " -DTFLITE_ENABLE_XNNPACK=ON"

TENSORFLOW_TARGET_ARCH = "${TARGET_ARCH}"
TENSORFLOW_TARGET_ARCH:raspberrypi = "armv6"
TENSORFLOW_TARGET_ARCH:raspberrypi0 = "armv6"
TENSORFLOW_TARGET_ARCH:raspberrypi0-wifi = "armv6"
TENSORFLOW_TARGET_ARCH:raspberrypi-cm = "armv6"
TENSORFLOW_TARGET_ARCH:raspberrypi2 = "armv7"
TENSORFLOW_TARGET_ARCH:raspberrypi3 = "armv7"
TENSORFLOW_TARGET_ARCH:raspberrypi4 = "armv7"
TENSORFLOW_TARGET_ARCH:raspberrypi-cm3 = "armv7"
TENSORFLOW_TARGET_ARCH:raspberrypi0-2w-64 = "aarch64"
TENSORFLOW_TARGET_ARCH:raspberrypi3-64 = "aarch64"
TENSORFLOW_TARGET_ARCH:raspberrypi4-64 = "aarch64"
TENSORFLOW_TARGET_ARCH:raspberrypi5 = "aarch64"
TENSORFLOW_TARGET_ARCH:riscv32 = "riscv32"
TENSORFLOW_TARGET_ARCH:riscv64 = "riscv64"
