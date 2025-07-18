DESCRIPTION = "TensorFlow Lite Standalone Pip"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=4158a261ca7f2525513e31ba9c50ae98"
# Compute branch info from ${PV} as Base PV...
BPV = "${@'.'.join(d.getVar('PV').split('.')[0:2])}"
DPV = "${@'.'.join(d.getVar('PV').split('.')[0:3])}"

SRCREV_tensorflow = "e36baa302922ea3c7131b302c2996bd2051ee5c4"

SRC_URI = " \
    git://github.com/tensorflow/tensorflow.git;name=tensorflow;branch=r${BPV};protocol=https \
    file://001-Set-CMAKE-SYSTEM-PROCESSOR.patch \
    file://001-protobuf.cmake.patch \
    file://001-flatbuffers.cmake.patch \
    file://001-Add-Wno-incompatible-pointer-types-flag-to-xnnpack.cmake.patch \
"

SRC_URI:append:riscv32 = " \
    file://001-Disable-XNNPACK-RISC-V-Vector-micro-kernels.patch \
    file://001-RISCV32_pthreads.patch \
"

DEPENDS += "\
    python3-pip-native \
    python3-wheel-native \
    python3-numpy \
    python3-pybind11 \
    protobuf-native \
    flatbuffers-native \
    patchelf-native \
"

RDEPENDS:${PN} += " \
    python3 \
    python3-numpy \
"

inherit setuptools3 cmake

TENSORFLOW_LITE_BUILD_DIR = "${S}/tensorflow/lite/tools/pip_package/gen/tflite_pip/python3"
TENSORFLOW_LITE_DIR = "${S}/tensorflow/lite"

PYBIND11_INCLUDE = "${PYTHON_INCLUDE_DIR}/pybind11"
NUMPY_INCLUDE = "${PKG_CONFIG_SYSROOT_DIR}/${PYTHON_SITEPACKAGES_DIR}/numpy/_core/include"

OECMAKE_SOURCEPATH = "${TENSORFLOW_LITE_DIR}"
OECMAKE_TARGET_COMPILE　= "_pywrap_tensorflow_interpreter_wrapper"
OECMAKE_C_FLAGS += "-I${PYTHON_INCLUDE_DIR} -I${PYBIND11_IN} -I${NUMPY_INCLUDE}"
OECMAKE_CXX_FLAGS += "-I${PYTHON_INCLUDE_DIR} -I${PYBIND11_INCLUDE} -I${NUMPY_INCLUDE}"
EXTRA_OECMAKE = " \
  -DFETCHCONTENT_FULLY_DISCONNECTED=OFF \
  -DTFLITE_ENABLE_XNNPACK=OFF \
"

# Note:
# XNNPack is valid only on 64bit.
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

# Note:
# Download the submodule using FetchContent_Populate.
# Therefore, turn off FETCHCONTENT_FULLY_DISCONNECTED.
EXTRA_OECMAKE:append = " \
  -DTENSORFLOW_TARGET_ARCH=${TENSORFLOW_TARGET_ARCH} \
  -DTFLITE_HOST_TOOLS_DIR=${WORKDIR}/recipe-sysroot-native/usr/bin/ \
"

do_configure[network] = "1"

do_configure:append() {
    if [ -e ${S}/tensorflow/lite/tools/pip_package/riscv32_pthread.patch ]; then
        cd ${B}/pthreadpool-source/src
        patch < ${S}/tensorflow/lite/tools/pip_package/riscv32_pthread.patch
    fi
}

do_compile:prepend() {
    TENSORFLOW_VERSION=${DPV}
    export PACKAGE_VERSION="${TENSORFLOW_VERSION}"
    export PROJECT_NAME="tflite_runtime"

    rm -rf "${TENSORFLOW_LITE_BUILD_DIR}" && mkdir -p "${TENSORFLOW_LITE_BUILD_DIR}/tflite_runtime"
    cp -r "${TENSORFLOW_LITE_DIR}/tools/pip_package/debian" \
          "${TENSORFLOW_LITE_DIR}/tools/pip_package/MANIFEST.in" \
          "${TENSORFLOW_LITE_DIR}/python/interpreter_wrapper" \
          "${TENSORFLOW_LITE_BUILD_DIR}"
    cp "${TENSORFLOW_LITE_DIR}/tools/pip_package/setup_with_binary.py" "${TENSORFLOW_LITE_BUILD_DIR}/setup.py"
    cp "${TENSORFLOW_LITE_DIR}/python/interpreter.py" \
       "${TENSORFLOW_LITE_DIR}/python/metrics/metrics_interface.py" \
       "${TENSORFLOW_LITE_DIR}/python/metrics/metrics_portable.py" \
       "${TENSORFLOW_LITE_BUILD_DIR}/tflite_runtime"
    echo "__version__ = '${PACKAGE_VERSION}'" >> "${TENSORFLOW_LITE_BUILD_DIR}/tflite_runtime/__init__.py"
    echo "__git_version__ = '$(git -C "${S}" describe)'" >> "${TENSORFLOW_LITE_BUILD_DIR}/tflite_runtime/__init__.py"
}

do_compile:append() {
    cp "${B}/_pywrap_tensorflow_interpreter_wrapper.so" \
        "${TENSORFLOW_LITE_BUILD_DIR}/tflite_runtime"

    cd "${TENSORFLOW_LITE_BUILD_DIR}"
    echo ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN}

    if [ ${TARGET_ARCH} = "aarch64" ]; then
        echo "build aarch64"
        export TENSORFLOW_TARGET=aarch64
        export TARGET=aarch64
    elif [ ${TARGET_ARCH} = "arm" ]; then
        echo "build arm"
        export TENSORFLOW_TARGET=rpi
        export TARGET=rpi
    elif [ ${TARGET_ARCH} = "riscv64" ]; then
        echo "build riscv64"
        export TENSORFLOW_TARGET=riscv64
        export TARGET=riscv64
    elif [ ${TARGET_ARCH} = "riscv32" ]; then
        echo "build riscv32"
        export TENSORFLOW_TARGET=riscv32
        export TARGET=riscv32
    fi

    STAGING_INCDIR=${STAGING_INCDIR} \
    STAGING_LIBDIR=${STAGING_LIBDIR} \
    ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} setup.py \
    bdist bdist_wheel
}

do_install() {
    echo "Generating pip package"
    cd "${TENSORFLOW_LITE_BUILD_DIR}"

    install -d ${D}/${PYTHON_SITEPACKAGES_DIR}

    echo ${D}/${PYTHON_SITEPACKAGES_DIR}

    TAGING_INCDIR=${STAGING_INCDIR} \
    STAGING_LIBDIR=${STAGING_LIBDIR} \
    ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} -m pip install --disable-pip-version-check -v \
    -t ${D}/${PYTHON_SITEPACKAGES_DIR} --no-cache-dir --no-deps \
    ${S}/tensorflow/lite/tools/pip_package/gen/tflite_pip/python3/dist/tflite_runtime-${DPV}*.whl

    patchelf --clear-execstack ${D}${PYTHON_SITEPACKAGES_DIR}/tflite_runtime/_pywrap_tensorflow_interpreter_wrapper.so
}

FILES:${PN}-dev = ""
INSANE_SKIP:${PN} += "dev-so buildpaths"
FILES:${PN} += "${libdir}/* ${datadir}/*"
