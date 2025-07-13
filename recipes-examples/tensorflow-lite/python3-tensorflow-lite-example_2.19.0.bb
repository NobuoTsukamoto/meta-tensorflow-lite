DESCRIPTION = "TensorFlow Lite Python image classification demo"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4158a261ca7f2525513e31ba9c50ae98"

# Compute branch info from ${PV} as Base PV...
BPV = "${@'.'.join(d.getVar('PV').split('.')[0:2])}"
DPV = "${@'.'.join(d.getVar('PV').split('.')[0:3])}"

SRCREV_tensorflow = "e36baa302922ea3c7131b302c2996bd2051ee5c4"

SRC_URI[model.sha256sum] = "1ccb74dbd9c5f7aea879120614e91617db9534bdfaa53dfea54b7c14162e126b"
SRC_URI[label.sha256sum] = "366a2d53008df0d2a82b375e2020bbc57e43bbe19971370e47b7f74ea0aaab91"

SRC_URI = " \
    git://github.com/tensorflow/tensorflow.git;name=tensorflow;branch=r${BPV};protocol=https \
    https://storage.googleapis.com/download.tensorflow.org/models/mobilenet_v1_2018_02_22/mobilenet_v1_1.0_224.tgz;name=model \
    https://storage.googleapis.com/download.tensorflow.org/models/mobilenet_v1_1.0_224_frozen.tgz;name=label \
    file://001-label_image_py.patch \
"

RDEPENDS:${PN} += " \
    python3-tensorflow-lite \
    python3-pillow \
"

do_install:append() {
    install -d ${D}${datadir}/tensorflow/lite/examples/python
    install -m 644 ${S}/tensorflow/lite/examples/python/label_image.py ${D}${datadir}/tensorflow/lite/examples/python/
    install -m 644 ${S}/tensorflow/lite/examples/label_image/testdata/grace_hopper.bmp ${D}${datadir}/tensorflow/lite/examples/python/
    install -m 644 ${UNPACKDIR}/mobilenet_v1_1.0_224.tflite ${D}${datadir}/tensorflow/lite/examples/python/
    install -m 644 ${UNPACKDIR}/mobilenet_v1_1.0_224/labels.txt ${D}${datadir}/tensorflow/lite/examples/python/
}

FILES:${PN} += "${datadir}/tensorflow/lite/examples/python/*"
