DESCRIPTION = "LiteRT "
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
# Compute branch info from ${PV} as Base PV...
BPV = "${@'.'.join(d.getVar('PV').split('.')[0:2])}"
DPV = "${@'.'.join(d.getVar('PV').split('.')[0:3])}"
TF_MAJOR = "${@(d.getVar('PV').split('.') + ['0', '0', '0'])[0]}"
TF_MINOR = "${@(d.getVar('PV').split('.') + ['0', '0', '0'])[1]}"
TF_PATCH = "${@(d.getVar('PV').split('.') + ['0', '0', '0'])[2]}"
SRCREV_FORMAT = "litert_tensorflow"

SRCREV_litert = "ea79caffdd0f52cd44f203674f18a16a3cb861ad"
SRCREV_tensorflow = "a481b10260dfdf833a1b16007eead49c1d7febf3"

SRC_URI = " \
    git://github.com/google-ai-edge/LiteRT.git;name=litert;branch=${PV};protocol=https \
    git://github.com/tensorflow/tensorflow.git;name=tensorflow;destsuffix=tensorflow;branch=r2.21;protocol=https \
    file://0001-update_flatbuffers_ver_litert.patch \
    file://0001-update_flatbuffers_ver_tensorflow.patch;patchdir=${UNPACKDIR}/tensorflow \
"

DEPENDS = " \
    flatbuffers-native \
"

inherit cmake

OECMAKE_SOURCEPATH = "${S}/litert"

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
TENSORFLOW_TARGET_ARCH:intel-corei7-64 = "x86_64"
TENSORFLOW_TARGET_ARCH:intel-skylake-64 = "x86_64"


EXTRA_OECMAKE:append = " \
    -DTFLITE_ENABLE_XNNPACK=ON \
    -DTFLITE_HOST_TOOLS_DIR=${WORKDIR}/recipe-sysroot-native/usr/bin/ \
    -DFETCHCONTENT_FULLY_DISCONNECTED=OFF \
    -DTENSORFLOW_SOURCE_DIR=${UNPACKDIR}/tensorflow \
    -DTENSORFLOW_TARGET_ARCH=${TENSORFLOW_TARGET_ARCH} \
"

do_configure[network] = "1"

INSANE_SKIP:${PN}-dbg += "buildpaths"
