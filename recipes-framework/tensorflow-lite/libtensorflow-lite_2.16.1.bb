DESCRIPTION = "TensorFlow Lite CPP "
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=4158a261ca7f2525513e31ba9c50ae98"
# Compute branch info from ${PV} as Base PV...
BPV = "${@'.'.join(d.getVar('PV').split('.')[0:2])}"
DPV = "${@'.'.join(d.getVar('PV').split('.')[0:3])}"

SRCREV_tensorflow = "5bc9d26649cca274750ad3625bd93422617eed4b"

SRC_URI = " \
    git://github.com/tensorflow/tensorflow.git;name=tensorflow;branch=r${BPV};protocol=https \
    file://001-Fix-neon-sse-file-name-filter.patch \
"

SRC_URI:append:riscv32 = " \
    file://001-RISCV32_pthreads.patch \
    file://001-Disable-XNNPACK-RISC-V-Vector-micro-kernels.patch \
    file://001-Fix-RISCV-cpuinfo.patch \
"

SRC_URI:append:riscv64 = " \
    file://001-Disable-XNNPACK-RISC-V-Vector-micro-kernels.patch \
    file://001-Fix-RISCV-cpuinfo.patch \
"

inherit cmake

S = "${WORKDIR}/git"

DEPENDS = " \
    libgfortran \
    libeigen \
    abseil-cpp \
"

OECMAKE_SOURCEPATH = "${S}/tensorflow/lite"
EXTRA_OECMAKE = "-DBUILD_SHARED_LIBS=ON "

# Note:
# XNNPack is valid only on 64bit. 
# In the case of arm 32bit, it will be turned off because the build will be
# an error depending on the combination of target CPUs.
EXTRA_OECMAKE:append:raspberrypi = " -DTFLITE_ENABLE_XNNPACK=OFF"
EXTRA_OECMAKE:append:raspberrypi0 = " -DTFLITE_ENABLE_XNNPACK=OFF"
EXTRA_OECMAKE:append:raspberrypi0-wifi = " -DTFLITE_ENABLE_XNNPACK=OFF"
EXTRA_OECMAKE:append:raspberrypi-cm = " -DTFLITE_ENABLE_XNNPACK=OFF"
EXTRA_OECMAKE:append:raspberrypi2 = " -DTFLITE_ENABLE_XNNPACK=OFF"
EXTRA_OECMAKE:append:raspberrypi3 = " -DTFLITE_ENABLE_XNNPACK=OFF"
EXTRA_OECMAKE:append:raspberrypi4 = " -DTFLITE_ENABLE_XNNPACK=OFF"
EXTRA_OECMAKE:append:raspberrypi-cm3 = " -DTFLITE_ENABLE_XNNPACK=OFF"

# Note:
# Download the submodule using FetchContent_Populate.
# Therefore, turn off FETCHCONTENT_FULLY_DISCONNECTED.
EXTRA_OECMAKE:append = " \
  -DFETCHCONTENT_FULLY_DISCONNECTED=OFF \
"

do_configure[network] = "1"

do_configure:prepend() {
    rm -rf ${S}/tensorflow/lite/tools/cmake/modules/Findabsl.cmake
    rm -rf ${S}/tensorflow/lite/tools/cmake/modules/FindEigen3.cmake
}

do_configure:append() {
    if [ -e ${S}/tensorflow/lite/tools/pip_package/riscv32_pthread.patch ]; then
        cd ${B}/pthreadpool-source/src
        patch < ${S}/tensorflow/lite/tools/pip_package/riscv32_pthread.patch
    fi
}

do_install:append() {
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

    install -d ${D}${includedir}/tensorflow/core/util
    install -d ${D}${includedir}/tensorflow/core/platform
    install -d ${D}${includedir}/tensorflow/tsl/platform
    install -d ${D}${includedir}/tensorflow/tsl/util
    install -m 644 ${S}/tensorflow/core/util/*.h ${D}${includedir}/tensorflow/core/util
    install -m 644 ${S}/tensorflow/core/platform/*.h ${D}${includedir}/tensorflow/core/platform

    install -d ${D}${includedir}/tensorflow/lite
    install -d ${D}${includedir}/tensorflow/lite/acceleration/configuration
    install -d ${D}${includedir}/tensorflow/lite/acceleration/configuration/c
    install -d ${D}${includedir}/tensorflow/lite/c
    install -d ${D}${includedir}/tensorflow/lite/core
    install -d ${D}${includedir}/tensorflow/lite/core/c
    install -d ${D}${includedir}/tensorflow/lite/core/acceleration/configuration
    install -d ${D}${includedir}/tensorflow/lite/core/acceleration/configuration/c
    install -d ${D}${includedir}/tensorflow/lite/core/api
    install -d ${D}${includedir}/tensorflow/lite/core/async
    install -d ${D}${includedir}/tensorflow/lite/core/async/c
    install -d ${D}${includedir}/tensorflow/lite/core/async/interop
    install -d ${D}${includedir}/tensorflow/lite/core/async/interop/c
    install -d ${D}${includedir}/tensorflow/lite/core/experimental/acceleration/configuration/c
    install -d ${D}${includedir}/tensorflow/lite/core/kernels
    install -d ${D}${includedir}/tensorflow/lite/delegates
    install -d ${D}${includedir}/tensorflow/lite/delegates/coreml
    install -d ${D}${includedir}/tensorflow/lite/delegates/coreml/builders
    install -d ${D}${includedir}/tensorflow/lite/delegates/external
    install -d ${D}${includedir}/tensorflow/lite/delegates/flex
    install -d ${D}${includedir}/tensorflow/lite/delegates/gpu
    install -d ${D}${includedir}/tensorflow/lite/delegates/gpu/cl
    install -d ${D}${includedir}/tensorflow/lite/delegates/gpu/cl/default
    install -d ${D}${includedir}/tensorflow/lite/delegates/gpu/cl/kernels
    install -d ${D}${includedir}/tensorflow/lite/delegates/gpu/common
    install -d ${D}${includedir}/tensorflow/lite/delegates/gpu/common/google
    install -d ${D}${includedir}/tensorflow/lite/delegates/gpu/common/memory_management
    install -d ${D}${includedir}/tensorflow/lite/delegates/gpu/common/selectors
    install -d ${D}${includedir}/tensorflow/lite/delegates/gpu/common/task
    install -d ${D}${includedir}/tensorflow/lite/delegates/gpu/common/tasks
    install -d ${D}${includedir}/tensorflow/lite/delegates/gpu/common/tasks/special
    install -d ${D}${includedir}/tensorflow/lite/delegates/gpu/common/transformations
    install -d ${D}${includedir}/tensorflow/lite/delegates/gpu/gl
    install -d ${D}${includedir}/tensorflow/lite/delegates/gpu/gl/compiler
    install -d ${D}${includedir}/tensorflow/lite/delegates/gpu/gl/converters
    install -d ${D}${includedir}/tensorflow/lite/delegates/gpu/gl/kernels
    install -d ${D}${includedir}/tensorflow/lite/delegates/gpu/gl/runtime
    install -d ${D}${includedir}/tensorflow/lite/delegates/gpu/gl/workgroups
    install -d ${D}${includedir}/tensorflow/lite/delegates/gpu/metal
    install -d ${D}${includedir}/tensorflow/lite/delegates/hexagon
    install -d ${D}${includedir}/tensorflow/lite/delegates/hexagon/builders
    install -d ${D}${includedir}/tensorflow/lite/delegates/hexagon/hexagon_nn
    install -d ${D}${includedir}/tensorflow/lite/delegates/nnapi
    install -d ${D}${includedir}/tensorflow/lite/delegates/utils
    install -d ${D}${includedir}/tensorflow/lite/delegates/utils/dummy_delegate
    install -d ${D}${includedir}/tensorflow/lite/delegates/xnnpack
    install -d ${D}${includedir}/tensorflow/lite/examples/ios/camera
    install -d ${D}${includedir}/tensorflow/lite/examples/ios/simple
    install -d ${D}${includedir}/tensorflow/lite/examples/label_image
    install -d ${D}${includedir}/tensorflow/lite/experimental/acceleration/compatibility
    install -d ${D}${includedir}/tensorflow/lite/experimental/acceleration/configuration
    install -d ${D}${includedir}/tensorflow/lite/experimental/acceleration/configuration/c
    install -d ${D}${includedir}/tensorflow/lite/experimental/acceleration/mini_benchmark
    install -d ${D}${includedir}/tensorflow/lite/experimental/microfrontend
    install -d ${D}${includedir}/tensorflow/lite/experimental/microfrontend/lib
    install -d ${D}${includedir}/tensorflow/lite/experimental/remat
    install -d ${D}${includedir}/tensorflow/lite/experimental/resource
    install -d ${D}${includedir}/tensorflow/lite/internal
    install -d ${D}${includedir}/tensorflow/lite/java/src/main/native
    install -d ${D}${includedir}/tensorflow/lite/kernels
    install -d ${D}${includedir}/tensorflow/lite/kernels/ctc
    install -d ${D}${includedir}/tensorflow/lite/kernels/gradient
    install -d ${D}${includedir}/tensorflow/lite/kernels/internal
    install -d ${D}${includedir}/tensorflow/lite/kernels/internal/optimized
    install -d ${D}${includedir}/tensorflow/lite/kernels/internal/optimized/integer_ops
    install -d ${D}${includedir}/tensorflow/lite/kernels/internal/optimized/sparse_ops
    install -d ${D}${includedir}/tensorflow/lite/kernels/internal/reference
    install -d ${D}${includedir}/tensorflow/lite/kernels/internal/reference/integer_ops
    install -d ${D}${includedir}/tensorflow/lite/kernels/internal/reference/sparse_ops
    install -d ${D}${includedir}/tensorflow/lite/kernels/internal/utils
    install -d ${D}${includedir}/tensorflow/lite/kernels/parse_example
    install -d ${D}${includedir}/tensorflow/lite/kernels/perception
    install -d ${D}${includedir}/tensorflow/lite/kernels/shim
    install -d ${D}${includedir}/tensorflow/lite/nnapi
    install -d ${D}${includedir}/tensorflow/lite/nnapi/sl/include
    install -d ${D}${includedir}/tensorflow/lite/nnapi/sl/public
    install -d ${D}${includedir}/tensorflow/lite/objc/apis
    install -d ${D}${includedir}/tensorflow/lite/objc/apps/TestApp/TestApp
    install -d ${D}${includedir}/tensorflow/lite/objc/sources
    install -d ${D}${includedir}/tensorflow/lite/profiling
    install -d ${D}${includedir}/tensorflow/lite/profiling/telemetry
    install -d ${D}${includedir}/tensorflow/lite/profiling/telemetry/c
    install -d ${D}${includedir}/tensorflow/lite/python/analyzer_wrapper
    install -d ${D}${includedir}/tensorflow/lite/python/interpreter_wrapper
    install -d ${D}${includedir}/tensorflow/lite/python/metrics/wrapper
    install -d ${D}${includedir}/tensorflow/lite/python/optimize
    install -d ${D}${includedir}/tensorflow/lite/schema
    install -d ${D}${includedir}/tensorflow/lite/schema/builtin_ops_header
    install -d ${D}${includedir}/tensorflow/lite/schema/builtin_ops_list
    install -d ${D}${includedir}/tensorflow/lite/swift/docsgen/TensorFlowLiteSwift/TensorFlowLiteSwift
    install -d ${D}${includedir}/tensorflow/lite/toco
    install -d ${D}${includedir}/tensorflow/lite/toco/graph_transformations
    install -d ${D}${includedir}/tensorflow/lite/toco/logging
    install -d ${D}${includedir}/tensorflow/lite/toco/python
    install -d ${D}${includedir}/tensorflow/lite/toco/runtime
    install -d ${D}${includedir}/tensorflow/lite/toco/tensorflow_graph_matching
    install -d ${D}${includedir}/tensorflow/lite/toco/tflite
    install -d ${D}${includedir}/tensorflow/lite/tools
    install -d ${D}${includedir}/tensorflow/lite/tools/benchmark
    install -d ${D}${includedir}/tensorflow/lite/tools/benchmark/experimental/c
    install -d ${D}${includedir}/tensorflow/lite/tools/benchmark/ios/TFLiteBenchmark/TFLiteBenchmark
    install -d ${D}${includedir}/tensorflow/lite/tools/delegates
    install -d ${D}${includedir}/tensorflow/lite/tools/evaluation
    install -d ${D}${includedir}/tensorflow/lite/tools/evaluation/stages
    install -d ${D}${includedir}/tensorflow/lite/tools/evaluation/stages/utils
    install -d ${D}${includedir}/tensorflow/lite/tools/evaluation/tasks
    install -d ${D}${includedir}/tensorflow/lite/tools/optimize
    install -d ${D}${includedir}/tensorflow/lite/tools/optimize/calibration
    install -d ${D}${includedir}/tensorflow/lite/tools/optimize/calibration/builtin_logging_ops
    install -d ${D}${includedir}/tensorflow/lite/tools/optimize/calibration/custom_logging_ops
    install -d ${D}${includedir}/tensorflow/lite/tools/serialization
    install -d ${D}${includedir}/tensorflow/lite/tools/signature
    install -d ${D}${includedir}/tensorflow/lite/tools/strip_buffers
    install -d ${D}${includedir}/tensorflow/lite/tools/versioning
    install -m 644 ${S}/tensorflow/lite/*.h ${D}${includedir}/tensorflow/lite
    install -m 644 ${S}/tensorflow/lite/acceleration/configuration/*.h ${D}${includedir}/tensorflow/lite/acceleration/configuration
    install -m 644 ${S}/tensorflow/lite/acceleration/configuration/c/*.h ${D}${includedir}/tensorflow/lite/acceleration/configuration/c
    install -m 644 ${S}/tensorflow/lite/c/*.h ${D}${includedir}/tensorflow/lite/c
    install -m 644 ${S}/tensorflow/lite/core/*.h ${D}${includedir}/tensorflow/lite/core
    install -m 644 ${S}/tensorflow/lite/core/c/*.h ${D}${includedir}/tensorflow/lite/core/c
    install -m 644 ${S}/tensorflow/lite/core/acceleration/configuration/*.h ${D}${includedir}/tensorflow/lite/core/acceleration/configuration
    install -m 644 ${S}/tensorflow/lite/core/acceleration/configuration/c/*.h ${D}${includedir}/tensorflow/lite/core/acceleration/configuration/c
    install -m 644 ${S}/tensorflow/lite/core/api/*.h ${D}${includedir}/tensorflow/lite/core/api
    install -m 644 ${S}/tensorflow/lite/core/async/*.h ${D}${includedir}/tensorflow/lite/core/async
    install -m 644 ${S}/tensorflow/lite/core/async/c/*.h ${D}${includedir}/tensorflow/lite/core/async/c
    install -m 644 ${S}/tensorflow/lite/core/async/interop/*.h ${D}${includedir}/tensorflow/lite/core/async/interop
    install -m 644 ${S}/tensorflow/lite/core/async/interop/c/*.h ${D}${includedir}/tensorflow/lite/core/async/interop/c
    install -m 644 ${S}/tensorflow/lite/core/experimental/acceleration/configuration/c/*.h ${D}${includedir}/tensorflow/lite/core/experimental/acceleration/configuration/c
    install -m 644 ${S}/tensorflow/lite/core/kernels/*.h ${D}${includedir}/tensorflow/lite/core/kernels
    install -m 644 ${S}/tensorflow/lite/delegates/*.h ${D}${includedir}/tensorflow/lite/delegates
    install -m 644 ${S}/tensorflow/lite/delegates/coreml/*.h ${D}${includedir}/tensorflow/lite/delegates/coreml
    install -m 644 ${S}/tensorflow/lite/delegates/coreml/builders/*.h ${D}${includedir}/tensorflow/lite/delegates/coreml/builders
    install -m 644 ${S}/tensorflow/lite/delegates/external/*.h ${D}${includedir}/tensorflow/lite/delegates/external
    install -m 644 ${S}/tensorflow/lite/delegates/flex/*.h ${D}${includedir}/tensorflow/lite/delegates/flex
    install -m 644 ${S}/tensorflow/lite/delegates/gpu/*.h ${D}${includedir}/tensorflow/lite/delegates/gpu
    install -m 644 ${S}/tensorflow/lite/delegates/gpu/cl/*.h ${D}${includedir}/tensorflow/lite/delegates/gpu/cl
    install -m 644 ${S}/tensorflow/lite/delegates/gpu/cl/default/*.h ${D}${includedir}/tensorflow/lite/delegates/gpu/cl/default
    install -m 644 ${S}/tensorflow/lite/delegates/gpu/cl/kernels/*.h ${D}${includedir}/tensorflow/lite/delegates/gpu/cl/kernels
    install -m 644 ${S}/tensorflow/lite/delegates/gpu/common/*.h ${D}${includedir}/tensorflow/lite/delegates/gpu/common
    install -m 644 ${S}/tensorflow/lite/delegates/gpu/common/google/*.h ${D}${includedir}/tensorflow/lite/delegates/gpu/common/google
    install -m 644 ${S}/tensorflow/lite/delegates/gpu/common/memory_management/*.h ${D}${includedir}/tensorflow/lite/delegates/gpu/common/memory_management
    install -m 644 ${S}/tensorflow/lite/delegates/gpu/common/selectors/*.h ${D}${includedir}/tensorflow/lite/delegates/gpu/common/selectors
    install -m 644 ${S}/tensorflow/lite/delegates/gpu/common/task/*.h ${D}${includedir}/tensorflow/lite/delegates/gpu/common/task
    install -m 644 ${S}/tensorflow/lite/delegates/gpu/common/tasks/*.h ${D}${includedir}/tensorflow/lite/delegates/gpu/common/tasks
    install -m 644 ${S}/tensorflow/lite/delegates/gpu/common/tasks/special/*.h ${D}${includedir}/tensorflow/lite/delegates/gpu/common/tasks/special
    install -m 644 ${S}/tensorflow/lite/delegates/gpu/common/transformations/*.h ${D}${includedir}/tensorflow/lite/delegates/gpu/common/transformations
    install -m 644 ${S}/tensorflow/lite/delegates/gpu/gl/*.h ${D}${includedir}/tensorflow/lite/delegates/gpu/gl
    install -m 644 ${S}/tensorflow/lite/delegates/gpu/gl/compiler/*.h ${D}${includedir}/tensorflow/lite/delegates/gpu/gl/compiler
    install -m 644 ${S}/tensorflow/lite/delegates/gpu/gl/converters/*.h ${D}${includedir}/tensorflow/lite/delegates/gpu/gl/converters
    install -m 644 ${S}/tensorflow/lite/delegates/gpu/gl/kernels/*.h ${D}${includedir}/tensorflow/lite/delegates/gpu/gl/kernels
    install -m 644 ${S}/tensorflow/lite/delegates/gpu/gl/runtime/*.h ${D}${includedir}/tensorflow/lite/delegates/gpu/gl/runtime
    install -m 644 ${S}/tensorflow/lite/delegates/gpu/gl/workgroups/*.h ${D}${includedir}/tensorflow/lite/delegates/gpu/gl/workgroups
    install -m 644 ${S}/tensorflow/lite/delegates/gpu/metal/*.h ${D}${includedir}/tensorflow/lite/delegates/gpu/metal
    install -m 644 ${S}/tensorflow/lite/delegates/hexagon/*.h ${D}${includedir}/tensorflow/lite/delegates/hexagon
    install -m 644 ${S}/tensorflow/lite/delegates/hexagon/builders/*.h ${D}${includedir}/tensorflow/lite/delegates/hexagon/builders
    install -m 644 ${S}/tensorflow/lite/delegates/hexagon/hexagon_nn/*.h ${D}${includedir}/tensorflow/lite/delegates/hexagon/hexagon_nn
    install -m 644 ${S}/tensorflow/lite/delegates/nnapi/*.h ${D}${includedir}/tensorflow/lite/delegates/nnapi
    install -m 644 ${S}/tensorflow/lite/delegates/utils/*.h ${D}${includedir}/tensorflow/lite/delegates/utils
    install -m 644 ${S}/tensorflow/lite/delegates/utils/dummy_delegate/*.h ${D}${includedir}/tensorflow/lite/delegates/utils/dummy_delegate
    install -m 644 ${S}/tensorflow/lite/delegates/xnnpack/*.h ${D}${includedir}/tensorflow/lite/delegates/xnnpack
    install -m 644 ${S}/tensorflow/lite/examples/ios/camera/*.h ${D}${includedir}/tensorflow/lite/examples/ios/camera
    install -m 644 ${S}/tensorflow/lite/examples/ios/simple/*.h ${D}${includedir}/tensorflow/lite/examples/ios/simple
    install -m 644 ${S}/tensorflow/lite/examples/label_image/*.h ${D}${includedir}/tensorflow/lite/examples/label_image
    install -m 644 ${S}/tensorflow/lite/experimental/acceleration/compatibility/*.h ${D}${includedir}/tensorflow/lite/experimental/acceleration/compatibility
    install -m 644 ${S}/tensorflow/lite/experimental/acceleration/configuration/*.h ${D}${includedir}/tensorflow/lite/experimental/acceleration/configuration
    install -m 644 ${S}/tensorflow/lite/experimental/acceleration/configuration/c/*.h ${D}${includedir}/tensorflow/lite/experimental/acceleration/configuration/c
    install -m 644 ${S}/tensorflow/lite/experimental/acceleration/mini_benchmark/*.h ${D}${includedir}/tensorflow/lite/experimental/acceleration/mini_benchmark
    install -m 644 ${S}/tensorflow/lite/experimental/microfrontend/*.h ${D}${includedir}/tensorflow/lite/experimental/microfrontend
    install -m 644 ${S}/tensorflow/lite/experimental/microfrontend/lib/*.h ${D}${includedir}/tensorflow/lite/experimental/microfrontend/lib
    install -m 644 ${S}/tensorflow/lite/experimental/remat/*.h ${D}${includedir}/tensorflow/lite/experimental/remat
    install -m 644 ${S}/tensorflow/lite/experimental/resource/*.h ${D}${includedir}/tensorflow/lite/experimental/resource
    install -m 644 ${S}/tensorflow/lite/internal/*.h ${D}${includedir}/tensorflow/lite/internal
    install -m 644 ${S}/tensorflow/lite/java/src/main/native/*.h ${D}${includedir}/tensorflow/lite/java/src/main/native
    install -m 644 ${S}/tensorflow/lite/kernels/*.h ${D}${includedir}/tensorflow/lite/kernels
    install -m 644 ${S}/tensorflow/lite/kernels/ctc/*.h ${D}${includedir}/tensorflow/lite/kernels/ctc
    install -m 644 ${S}/tensorflow/lite/kernels/gradient/*.h ${D}${includedir}/tensorflow/lite/kernels/gradient
    install -m 644 ${S}/tensorflow/lite/kernels/internal/*.h ${D}${includedir}/tensorflow/lite/kernels/internal
    install -m 644 ${S}/tensorflow/lite/kernels/internal/optimized/*.h ${D}${includedir}/tensorflow/lite/kernels/internal/optimized
    install -m 644 ${S}/tensorflow/lite/kernels/internal/optimized/integer_ops/*.h ${D}${includedir}/tensorflow/lite/kernels/internal/optimized/integer_ops
    install -m 644 ${S}/tensorflow/lite/kernels/internal/optimized/sparse_ops/*.h ${D}${includedir}/tensorflow/lite/kernels/internal/optimized/sparse_ops
    install -m 644 ${S}/tensorflow/lite/kernels/internal/reference/*.h ${D}${includedir}/tensorflow/lite/kernels/internal/reference
    install -m 644 ${S}/tensorflow/lite/kernels/internal/reference/integer_ops/*.h ${D}${includedir}/tensorflow/lite/kernels/internal/reference/integer_ops
    install -m 644 ${S}/tensorflow/lite/kernels/internal/reference/sparse_ops/*.h ${D}${includedir}/tensorflow/lite/kernels/internal/reference/sparse_ops
    install -m 644 ${S}/tensorflow/lite/kernels/internal/utils/*.h ${D}${includedir}/tensorflow/lite/kernels/internal/utils
    install -m 644 ${S}/tensorflow/lite/kernels/parse_example/*.h ${D}${includedir}/tensorflow/lite/kernels/parse_example
    install -m 644 ${S}/tensorflow/lite/kernels/perception/*.h ${D}${includedir}/tensorflow/lite/kernels/perception
    install -m 644 ${S}/tensorflow/lite/kernels/shim/*.h ${D}${includedir}/tensorflow/lite/kernels/shim
    install -m 644 ${S}/tensorflow/lite/nnapi/*.h ${D}${includedir}/tensorflow/lite/nnapi
    install -m 644 ${S}/tensorflow/lite/nnapi/sl/include/*.h ${D}${includedir}/tensorflow/lite/nnapi/sl/include
    install -m 644 ${S}/tensorflow/lite/nnapi/sl/public/*.h ${D}${includedir}/tensorflow/lite/nnapi/sl/public
    install -m 644 ${S}/tensorflow/lite/objc/apis/*.h ${D}${includedir}/tensorflow/lite/objc/apis
    install -m 644 ${S}/tensorflow/lite/objc/apps/TestApp/TestApp/*.h ${D}${includedir}/tensorflow/lite/objc/apps/TestApp/TestApp
    install -m 644 ${S}/tensorflow/lite/objc/sources/*.h ${D}${includedir}/tensorflow/lite/objc/sources
    install -m 644 ${S}/tensorflow/lite/profiling/*.h ${D}${includedir}/tensorflow/lite/profiling
    install -m 644 ${S}/tensorflow/lite/profiling/telemetry/*.h ${D}${includedir}/tensorflow/lite/profiling/telemetry
    install -m 644 ${S}/tensorflow/lite/profiling/telemetry/c/*.h ${D}${includedir}/tensorflow/lite/profiling/telemetry/c
    install -m 644 ${S}/tensorflow/lite/python/analyzer_wrapper/*.h ${D}${includedir}/tensorflow/lite/python/analyzer_wrapper
    install -m 644 ${S}/tensorflow/lite/python/interpreter_wrapper/*.h ${D}${includedir}/tensorflow/lite/python/interpreter_wrapper
    install -m 644 ${S}/tensorflow/lite/python/metrics/wrapper/*.h ${D}${includedir}/tensorflow/lite/python/metrics/wrapper
    install -m 644 ${S}/tensorflow/lite/python/optimize/*.h ${D}${includedir}/tensorflow/lite/python/optimize
    install -m 644 ${S}/tensorflow/lite/schema/*.h ${D}${includedir}/tensorflow/lite/schema
    install -m 644 ${S}/tensorflow/lite/schema/builtin_ops_header/*.h ${D}${includedir}/tensorflow/lite/schema/builtin_ops_header
    install -m 644 ${S}/tensorflow/lite/schema/builtin_ops_list/*.h ${D}${includedir}/tensorflow/lite/schema/builtin_ops_list
    install -m 644 ${S}/tensorflow/lite/swift/docsgen/TensorFlowLiteSwift/TensorFlowLiteSwift/*.h ${D}${includedir}/tensorflow/lite/swift/docsgen/TensorFlowLiteSwift/TensorFlowLiteSwift
    install -m 644 ${S}/tensorflow/lite/toco/*.h ${D}${includedir}/tensorflow/lite/toco
    install -m 644 ${S}/tensorflow/lite/toco/graph_transformations/*.h ${D}${includedir}/tensorflow/lite/toco/graph_transformations
    install -m 644 ${S}/tensorflow/lite/toco/logging/*.h ${D}${includedir}/tensorflow/lite/toco/logging
    install -m 644 ${S}/tensorflow/lite/toco/python/*.h ${D}${includedir}/tensorflow/lite/toco/python
    install -m 644 ${S}/tensorflow/lite/toco/runtime/*.h ${D}${includedir}/tensorflow/lite/toco/runtime
    install -m 644 ${S}/tensorflow/lite/toco/tensorflow_graph_matching/*.h ${D}${includedir}/tensorflow/lite/toco/tensorflow_graph_matching
    install -m 644 ${S}/tensorflow/lite/toco/tflite/*.h ${D}${includedir}/tensorflow/lite/toco/tflite
    install -m 644 ${S}/tensorflow/lite/tools/*.h ${D}${includedir}/tensorflow/lite/tools
    install -m 644 ${S}/tensorflow/lite/tools/benchmark/*.h ${D}${includedir}/tensorflow/lite/tools/benchmark
    install -m 644 ${S}/tensorflow/lite/tools/benchmark/experimental/c/*.h ${D}${includedir}/tensorflow/lite/tools/benchmark/experimental/c
    install -m 644 ${S}/tensorflow/lite/tools/benchmark/ios/TFLiteBenchmark/TFLiteBenchmark/*.h ${D}${includedir}/tensorflow/lite/tools/benchmark/ios/TFLiteBenchmark/TFLiteBenchmark
    install -m 644 ${S}/tensorflow/lite/tools/delegates/*.h ${D}${includedir}/tensorflow/lite/tools/delegates
    install -m 644 ${S}/tensorflow/lite/tools/evaluation/*.h ${D}${includedir}/tensorflow/lite/tools/evaluation
    install -m 644 ${S}/tensorflow/lite/tools/evaluation/stages/*.h ${D}${includedir}/tensorflow/lite/tools/evaluation/stages
    install -m 644 ${S}/tensorflow/lite/tools/evaluation/stages/utils/*.h ${D}${includedir}/tensorflow/lite/tools/evaluation/stages/utils
    install -m 644 ${S}/tensorflow/lite/tools/evaluation/tasks/*.h ${D}${includedir}/tensorflow/lite/tools/evaluation/tasks
    install -m 644 ${S}/tensorflow/lite/tools/optimize/*.h ${D}${includedir}/tensorflow/lite/tools/optimize
    install -m 644 ${S}/tensorflow/lite/tools/optimize/calibration/*.h ${D}${includedir}/tensorflow/lite/tools/optimize/calibration
    install -m 644 ${S}/tensorflow/lite/tools/optimize/calibration/builtin_logging_ops/*.h ${D}${includedir}/tensorflow/lite/tools/optimize/calibration/builtin_logging_ops
    install -m 644 ${S}/tensorflow/lite/tools/optimize/calibration/custom_logging_ops/*.h ${D}${includedir}/tensorflow/lite/tools/optimize/calibration/custom_logging_ops
    install -m 644 ${S}/tensorflow/lite/tools/serialization/*.h ${D}${includedir}/tensorflow/lite/tools/serialization
    install -m 644 ${S}/tensorflow/lite/tools/signature/*.h ${D}${includedir}/tensorflow/lite/tools/signature
    install -m 644 ${S}/tensorflow/lite/tools/strip_buffers/*.h ${D}${includedir}/tensorflow/lite/tools/strip_buffers
    install -m 644 ${S}/tensorflow/lite/tools/versioning/*.h ${D}${includedir}/tensorflow/lite/tools/versioning

    rm -rf ${D}/${bindir}
    rm -rf ${D}/${libdir}/libflatbuffers.a
    rm -rf ${D}${includedir}/flatbuffers
    rm -rf ${D}/${libdir}/cmake/flatbuffers
    rm -rf ${D}/${libdir}/pkgconfig/flatbuffers.pc

}

FILES:${PN}-dev = "${includedir} ${libdir}/libtensorflowlite.so "
FILES:${PN} += "${libdir}/*.so"
FILES:${PN} += "${datadir}/eigen3/*"
FILES:${PN} += "${datadir}/cpuinfo/*"
FILES:${PN} += "${libdir}/cmake/NEON_2_SSE/*"
FILES:${PN} += "${libdir}/pkgconfig/libcpuinfo.pc"
FILES:${PN} += "${libdir}/cmake/gemmlowp/*"
