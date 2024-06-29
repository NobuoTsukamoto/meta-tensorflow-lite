DESCRIPTION = "TensorFlow Lite C++ minimal example"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=4158a261ca7f2525513e31ba9c50ae98"
# Compute branch info from ${PV} as Base PV...
BPV = "${@'.'.join(d.getVar('PV').split('.')[0:2])}"
DPV = "${@'.'.join(d.getVar('PV').split('.')[0:3])}"

SRCREV_tensorflow = "810f233968cec850915324948bbbc338c97cf57f"

SRC_URI[model.sha256sum] = "1ccb74dbd9c5f7aea879120614e91617db9534bdfaa53dfea54b7c14162e126b"

SRC_URI = " \
    git://github.com/tensorflow/tensorflow.git;name=tensorflow;branch=r${BPV};protocol=https \
    file://001-minimal_cmake.patch \
    https://storage.googleapis.com/download.tensorflow.org/models/mobilenet_v1_2018_02_22/mobilenet_v1_1.0_224.tgz;name=model \
"

SRC_URI:append:riscv32 = " \
    file://001-minimal_cmake_add_link_libatomic.patch \
"

inherit cmake

S = "${WORKDIR}/git"

DEPENDS += "\
    libtensorflow-lite \
    flatbuffers \
"

EXTRA_OECMAKE:append:raspberrypi0-2w-64 = " -DTFLITE_ENABLE_XNNPACK=ON"
EXTRA_OECMAKE:append:raspberrypi3-64 = " -DTFLITE_ENABLE_XNNPACK=ON"
EXTRA_OECMAKE:append:raspberrypi4-64 = " -DTFLITE_ENABLE_XNNPACK=ON"
EXTRA_OECMAKE:append:raspberrypi5-64 = " -DTFLITE_ENABLE_XNNPACK=ON"
EXTRA_OECMAKE:append:riscv32 = " -DTFLITE_ENABLE_XNNPACK=ON"
EXTRA_OECMAKE:append:riscv64 = " -DTFLITE_ENABLE_XNNPACK=ON"

OECMAKE_SOURCEPATH = "${S}/tensorflow/lite/examples/minimal"

do_install() {
    install -d ${D}${datadir}/tensorflow/lite/examples/minimal
    install -m 755 ${B}/minimal ${D}${datadir}/tensorflow/lite/examples/minimal/
    install -m 644 ${WORKDIR}/mobilenet_v1_1.0_224.tflite ${D}${datadir}/tensorflow/lite/examples/minimal/
}

FILES:${PN} += "${datadir}/tensorflow/lite/examples/minimal/*"