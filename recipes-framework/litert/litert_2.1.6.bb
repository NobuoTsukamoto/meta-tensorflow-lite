DESCRIPTION = "LiteRT "
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
# Compute branch info from ${PV} as Base PV...
BPV = "${@'.'.join(d.getVar('PV').split('.')[0:2])}"
DPV = "${@'.'.join(d.getVar('PV').split('.')[0:3])}"
TF_MAJOR = "${@(d.getVar('PV').split('.') + ['0', '0', '0'])[0]}"
TF_MINOR = "${@(d.getVar('PV').split('.') + ['0', '0', '0'])[1]}"
TF_PATCH = "${@(d.getVar('PV').split('.') + ['0', '0', '0'])[2]}"
PV = "2.1.6"
SRCREV_FORMAT = "litert_tensorflow"

SRCREV_litert = "1461b6b2def31713f5c71446eab844aae05d02e9"
SRCREV_tensorflow = "b8a17154d80e4d7d2ce9419e38f5f6ae208e2137"

SRC_URI = "git://github.com/google-ai-edge/LiteRT.git;name=litert;branch=${PV};protocol=https;lfs=0 \
           git://github.com/tensorflow/tensorflow.git;name=tensorflow;destsuffix=tensorflow;nobranch=1;protocol=https \
           file://0001-update_flatbuffers_ver_litert.patch \
           file://0002-only-enable-npu-backends-with-valid-headers.patch \
           file://0003-fix-abseil-bmi2-include-for-gcc-16.patch \
           file://0001-update_flatbuffers_ver_tensorflow.patch;patchdir=${UNPACKDIR}/tensorflow \
           "

DEPENDS = " \
    flatbuffers-native \
"

inherit cmake

# pthreadpool's Linux futex fallback expects SYS_futex, but riscv32 time64
# syscall headers only expose SYS_futex_time64.
CFLAGS:append:riscv32 = " -DSYS_futex=SYS_futex_time64"

# XNNPACK 0.0.0-20250606 enables its RVV microkernels by default and builds
# them with a hard-coded RV64 ABI (-march=rv64gcv -mabi=lp64d).  They cannot
# be linked into a riscv32 target, so use the scalar kernels on RV32.
EXTRA_OECMAKE:append:riscv32 = " -DXNNPACK_ENABLE_RISCV_VECTOR=OFF"

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
    -DLITERT_ENABLE_NPU=OFF \
    -DLITERT_ENABLE_QUALCOMM=OFF \
    -DLITERT_ENABLE_SAMSUNG=OFF \
    -DNEUROPILOT_HEADERS_DIR=${UNPACKDIR}/disabled-neuropilot \
    -DQAIRT_HEADERS_DIR=${UNPACKDIR}/disabled-qairt \
    -DLITECORE_HEADERS_DIR=${UNPACKDIR}/disabled-litecore \
    -DCMAKE_POLICY_VERSION_MINIMUM=3.5 \
"

do_configure[network] = "1"

INSANE_SKIP:${PN}-dbg += "buildpaths"
