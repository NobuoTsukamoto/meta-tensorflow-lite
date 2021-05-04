DESCRIPTION = "TensorFlow Lite Standalone Pip"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=64a34301f8e355f57ec992c2af3e5157"
# Compute branch info from ${PV} as Base PV...
BPV = "${@'.'.join(d.getVar('PV').split('.')[0:2])}"
DPV = "${@'.'.join(d.getVar('PV').split('.')[0:3])}"
# Since they tag off of something resembling ${PV}, use it.
SRCREV = "v${PV}"

SRC_URI = " \
    git://github.com/tensorflow/tensorflow.git;branch=r${BPV} \
    file://001-Change-curl-to-wget-command.patch \
    file://001-TensorFlow-Lite_Makefile.patch \
    file://001-Remove-toolchain-setup-and-pybind11.patch \
"

SRC_URI_append_riscv64 += " \
    file://link-atomic-lib.patch \
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

inherit setuptools3

do_configure(){
	${S}/tensorflow/lite/tools/make/download_dependencies.sh
}

do_compile() {
    LOCAL_BUILD_DIR="${S}/tensorflow/lite/tools/pip_package/gen/tflite_pip/python3"
    VERSION_SUFFIX=${VERSION_SUFFIX:-}
    export TENSORFLOW_DIR="${S}"
    TENSORFLOW_LITE_DIR="${S}/tensorflow/lite"
    TENSORFLOW_VERSION=$(grep "_VERSION = " "${TENSORFLOW_DIR}/tensorflow/tools/pip_package/setup.py" | cut -d= -f2 | sed "s/[ '-]//g")
    export PACKAGE_VERSION="${TENSORFLOW_VERSION}${VERSION_SUFFIX}"

    # Build source tree.
    rm -rf "${LOCAL_BUILD_DIR}" && mkdir -p "${LOCAL_BUILD_DIR}/tflite_runtime"
    cp -r "${TENSORFLOW_LITE_DIR}/tools/pip_package/debian" \
          "${TENSORFLOW_LITE_DIR}/tools/pip_package/setup.py" \
          "${TENSORFLOW_LITE_DIR}/tools/pip_package/MANIFEST.in" \
          "${TENSORFLOW_LITE_DIR}/python/interpreter_wrapper" \
          "${LOCAL_BUILD_DIR}"
    cp "${TENSORFLOW_LITE_DIR}/python/interpreter.py" \
       "${LOCAL_BUILD_DIR}/tflite_runtime"
    echo "__version__ = '${PACKAGE_VERSION}'" >> "${LOCAL_BUILD_DIR}/tflite_runtime/__init__.py"
    echo "__git_version__ = '$(git -C "${TENSORFLOW_DIR}" describe)'" >> "${LOCAL_BUILD_DIR}/tflite_runtime/__init__.py"

    # Build python wheel.
    cd "${LOCAL_BUILD_DIR}"
    echo ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN}

    if [ ${TARGET_ARCH} = "aarch64" ]; then
        echo "build aarch64"
        export TENSORFLOW_TARGET=aarch64
        export TARGET=aarch64

        STAGING_INCDIR=${STAGING_INCDIR} \
        STAGING_LIBDIR=${STAGING_LIBDIR} \
        ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} setup.py \
        bdist --plat-name=linux-aarch64 bdist_wheel --plat-name=linux-aarch64

    elif [ ${TARGET_ARCH} = "arm" ]; then
        echo "build arm"
        export TENSORFLOW_TARGET=rpi
        export TARGET=rpi

        STAGING_INCDIR=${STAGING_INCDIR} \
        STAGING_LIBDIR=${STAGING_LIBDIR} \
        ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} setup.py \
        bdist --plat-name=linux-armv7l bdist_wheel --plat-name=linux-armv7l
    elif [ ${TARGET_ARCH} = "riscv64" ]; then
        echo "build riscv64"
        export TENSORFLOW_TARGET=riscv64
        export TARGET=riscv64

        STAGING_INCDIR=${STAGING_INCDIR} \
        STAGING_LIBDIR=${STAGING_LIBDIR} \
        ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} setup.py \
        bdist --plat-name=linux-riscv64 bdist_wheel --plat-name=linux-riscv64
    fi
}

do_install() {
    echo "Generating pip package"
    LOCAL_BUILD_DIR="${S}/tensorflow/lite/tools/pip_package/gen/tflite_pip/python3"
    cd "${LOCAL_BUILD_DIR}"
    
    install -d ${D}/${PYTHON_SITEPACKAGES_DIR}

    echo ${D}/${PYTHON_SITEPACKAGES_DIR}

    TAGING_INCDIR=${STAGING_INCDIR} \
    STAGING_LIBDIR=${STAGING_LIBDIR} \
    PYTHONPATH=${D}${PYTHON_SITEPACKAGES_DIR} \
    ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} -m pip install --disable-pip-version-check -v \
    -t ${D}/${PYTHON_SITEPACKAGES_DIR} --no-cache-dir --no-deps \
    ${S}/tensorflow/lite/tools/pip_package/gen/tflite_pip/python3/dist/tflite_runtime-${DPV}-*.whl
}

FILES_${PN}-dev = ""
INSANE_SKIP_${PN} += "dev-so \
                     "
FILES_${PN} += "${libdir}/* ${datadir}/*"
