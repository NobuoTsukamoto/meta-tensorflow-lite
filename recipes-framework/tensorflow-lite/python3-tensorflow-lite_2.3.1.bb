DESCRIPTION = "TensorFlow Lite pip package"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE;md5=64a34301f8e355f57ec992c2af3e5157"
SRCREV = "fcc4b966f1265f466e82617020af93670141b009"

SRC_URI = " \
    git://github.com/tensorflow/tensorflow.git;branch=r2.3 \
    file://001-Change-curl-to-wget-command.patch \
    file://001-TensorFlow-Lite_Makefile.patch \
    file://001-Remove-toolchain-setup-and-pybind11.patch \
"

S = "${WORKDIR}/git"

DEPENDS += "gzip-native \
            unzip-native \
            zlib \
            python3 \
            python3-native \
            python3-numpy-native \
            python3-pip-native \
            python3-wheel-native \
            python3-pybind11 \
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
    echo ${TARGET_ARCH}

    if [ ${TARGET_ARCH} = "aarch64" ]; then
        echo "build aarch64"
        export TENSORFLOW_TARGET=aarch64
        export TARGET=aarch64
    elif [ ${TARGET_ARCH} = "arm" ]; then
        echo "build arm"
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
        ${S}/tensorflow/lite/tools/pip_package/gen/tflite_pip${WORKDIR}/recipe-sysroot-native/usr/bin/python3-native/python3/dist/tflite_runtime-2.3.1-*.whl
}

FILES_${PN}-dev = ""
INSANE_SKIP_${PN} += "dev-so \
                     "
FILES_${PN} += "${libdir}/* ${datadir}/*"
