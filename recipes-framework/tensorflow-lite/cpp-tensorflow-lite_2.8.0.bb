DESCRIPTION = "TensorFlow Lite CPP "
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=4158a261ca7f2525513e31ba9c50ae98"
# Compute branch info from ${PV} as Base PV...
BPV = "${@'.'.join(d.getVar('PV').split('.')[0:2])}"
DPV = "${@'.'.join(d.getVar('PV').split('.')[0:3])}"
# Since they tag off of something resembling ${PV}, use it.
SRCREV = "v${PV}"

SRC_URI = " \
    git://github.com/tensorflow/tensorflow.git;branch=r${BPV};protocol=https \
    file://001-v2.8-Disable-XNNPACKPack-CMakeFile.patch \
    file://001-v2.8_riscv_download.patch \
"

inherit cmake

S = "${WORKDIR}/git"

OECMAKE_SOURCEPATH = "${S}/tensorflow/lite"
EXTRA_OECMAKE = "-DBUILD_SHARED_LIBS=ON"

# Note:
# XNNPack is valid only on 64bit. 
# In the case of arm 32bit, it will be turned off because the build will be
# an error depending on the combination of target CPUs.
HOST_ARCH:raspberrypi = "armv6"
HOST_ARCH:raspberrypi0 = "armv6"
HOST_ARCH:raspberrypi0-wifi = "armv6"
HOST_ARCH:raspberrypi-cm = "armv6"

HOST_ARCH:raspberrypi2 = "armv7"
HOST_ARCH:raspberrypi3 = "armv7"
HOST_ARCH:raspberrypi4 = "armv7"
HOST_ARCH:raspberrypi-cm3 = "armv7"

HOST_ARCH:raspberrypi0-2w-64 = "aarch64"
TUNE_CCARGS:raspberrypi0-2w-64  = ""
EXTRA_OECMAKE:append:raspberrypi0-2w-64 = "-DTFLITE_ENABLE_XNNPACK=ON"
HOST_ARCH:raspberrypi3-64 = "aarch64"
TUNE_CCARGS:raspberrypi3-64 = ""
EXTRA_OECMAKE:append:raspberrypi3-64 = "-DTFLITE_ENABLE_XNNPACK=ON"
HOST_ARCH:raspberrypi4-64 = "aarch64"
TUNE_CCARGS:raspberrypi4-64 = ""
EXTRA_OECMAKE:append:raspberrypi4-64 = "-DTFLITE_ENABLE_XNNPACK=ON"

do_install() {
    install -d ${D}/${libdir}
    install -m 0755 ${B}/libtensorflow-lite.so  ${D}/${libdir}/

    install -m 0755 ${B}/_deps/farmhash-build/libfarmhash.so ${D}/${libdir}
    install -m 0755 ${B}/_deps/fft2d-build/libfft2d_fftsg2d.so ${D}/${libdir}
    install -m 0755 ${B}/_deps/fft2d-build/libfft2d_fftsg.so ${D}/${libdir}
    if [ -e ${B}/_deps/ruy-build/libruy.so ]; then
        install -m 0755 ${B}/_deps/ruy-build/libruy.so ${D}/${libdir}
    fi
    if [ -e ${B}/_deps/xnnpack-build/libXNNPACK.so ]; then
        install -m 0755 ${B}/_deps/xnnpack-build/libXNNPACK.so ${D}/${libdir}
    fi
    if [ -e ${B}/pthreadpool/libpthreadpool.so ]; then
        install -m 0755 ${B}/pthreadpool/libpthreadpool.so ${D}/${libdir}
    fi
    if [ -e ${B}/_deps/cpuinfo-build/libcpuinfo.so ]; then
        install -m 0755 ${B}/_deps/cpuinfo-build/libcpuinfo.so ${D}/${libdir}
    fi

    install -d ${D}${includedir}/tensorflow/lite
    install -d ${D}${includedir}/tensorflow/lite/c
    install -d ${D}${includedir}/tensorflow/lite/kernels
    install -d ${D}${includedir}/tensorflow/lite/core/api
    install -d ${D}${includedir}/tensorflow/lite/delegates/nnapi
    install -d ${D}${includedir}/tensorflow/lite/nnapi
    install -d ${D}${includedir}/tensorflow/lite/experimental/resource
    install -d ${D}${includedir}/tensorflow/lite/schema
    install -d ${D}${includedir}/tensorflow/lite/internal
    install -d ${D}${includedir}/flatbuffers

	install -m 644 ${S}/tensorflow/lite/*.h ${D}${includedir}/tensorflow/lite
	install -m 644 ${S}/tensorflow/lite/kernels/*.h ${D}${includedir}/tensorflow/lite/kernels
    install -m 644 ${S}/tensorflow/lite/core/api/*.h ${D}${includedir}/tensorflow/lite/core/api
    install -m 644 ${S}/tensorflow/lite/core/*.h ${D}${includedir}/tensorflow/lite/core
    install -m 644 ${S}/tensorflow/lite/nnapi/*.h ${D}${includedir}/tensorflow/lite/nnapi
    install -m 644 ${S}/tensorflow/lite/c/*.h ${D}${includedir}/tensorflow/lite/c
    install -m 644 ${S}/tensorflow/lite/internal/*.h ${D}${includedir}/tensorflow/lite/internal
    install -m 644 ${S}/tensorflow/lite/schema/*.h ${D}${includedir}/tensorflow/lite/schema
    install -m 644 ${S}/tensorflow/lite/experimental/resource/*.h ${D}${includedir}/tensorflow/lite/experimental/resource

    install -m 644 ${B}/flatbuffers/include/flatbuffers/*.h ${D}${includedir}/flatbuffers
}

FILES:${PN}-dev = "${includedir} ${libdir}/libtensorflowlite.so "
FILES:${PN} += "${libdir}/*.so"