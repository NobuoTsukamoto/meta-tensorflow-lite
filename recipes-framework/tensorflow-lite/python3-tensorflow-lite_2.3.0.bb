DESCRIPTION = "TensorFlow Lite pip package"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE;md5=64a34301f8e355f57ec992c2af3e5157"
SRCREV = "99fea8da0d98fb271b60b58cfa5755f2bd430079"

SRC_URI = " \
    git://github.com/tensorflow/tensorflow.git;branch=r2.3 \
    file://001-Change-curl-to-wget-command.patch \
    file://001-TensorFlow-Lite_Makefile.patch \
    file://001-Remove-toolchain-setup.patch \
"

S = "${WORKDIR}/git"

DEPENDS += "gzip-native \
            unzip-native \
            zlib \
            python3 \
            python3-native \
            python3-numpy-native \
            python3-pybind11-native \
            python3-pip-native \
            python3-wheel-native \
"

RDEPENDS_${PN} += " \
    python3 \
    python3-numpy \
    python3-pybind11 \
"

inherit python3native 

export PYTHON_BIN_PATH="${PYTHON}"
export PYTHON_LIB_PATH="${STAGING_LIBDIR_NATIVE}/${PYTHON_DIR}/site-packages"

do_configure(){
	${S}/tensorflow/lite/tools/make/download_dependencies.sh
}

do_compile () {
    echo ${STAGING_LIBDIR_NATIVE}

    if [ ${TARGET_ARCH} = "aarch64" ]; then
        export TENSORFLOW_TARGET=aarch64
        export TARGET=aarch64
        ${S}/tensorflow/lite/tools/pip_package/build_pip_package.sh
    elif [ ${TARGET_ARCH} = "armv7l" ]; then
        export TENSORFLOW_TARGET=rpi
        export TARGET=rpi
    fi
    
    ${S}/tensorflow/lite/tools/pip_package/build_pip_package.sh

}

do_install() {
    echo "Generating pip package"
    install -d ${D}/${PYTHON_SITEPACKAGES_DIR}
    
    ${STAGING_BINDIR_NATIVE}/pip3 install --disable-pip-version-check -v \
        -t ${D}/${PYTHON_SITEPACKAGES_DIR} --no-cache-dir --no-deps \
        ${S}/tensorflow/lite/tools/pip_package/gen/tflite_pip/${TOPDIR}/tmp/work/aarch64-poky-linux/python3-tensorflow-lite/2.3.0-r0/recipe-sysroot-native/usr/bin/python3-native/python3/dist/tflite_runtime-2.3.0rc0-*.whl
}

FILES_${PN}-dev = ""
INSANE_SKIP_${PN} += "dev-so \
                     "
FILES_${PN} += "${libdir}/* ${datadir}/*"