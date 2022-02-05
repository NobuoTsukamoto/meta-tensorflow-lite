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
"

inherit cmake

S = "${WORKDIR}/git"

DEPENDS += " \
            unzip-native \
            python3-native \
            python3-numpy-native \
"

# XNNPACK is building all types of cpus and chooses the right one
# on runtime, so passing any tune values just causes the compilation to
# fail.
TUNE_CCARGS = ""

OECMAKE_SOURCEPATH = "${S}/tensorflow/lite"
EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"

do_install() {
    install -d ${D}/${libdir}
    install -m 0644 ${B}/libtensorflow-lite.so  ${D}/${libdir}/
    ln -s -r ${D}/${libdir}/libtensorflow-lite.so ${D}/${libdir}/libtensorflowlite.so

    install -m 0644 ${B}/_deps/farmhash-build/libfarmhash.so ${D}/${libdir}
    install -m 0644 ${B}/_deps/fft2d-build/libfft2d_fftsg2d.so ${D}/${libdir}
    install -m 0644 ${B}/_deps/ruy-build/libruy.so ${D}/${libdir}
    install -m 0644 ${B}/_deps/xnnpack-build/libXNNPACK.so ${D}/${libdir}
    install -m 0644 ${B}/_deps/fft2d-build/libfft2d_fftsg.so ${D}/${libdir}
    install -m 0644 ${B}/pthreadpool/libpthreadpool.so ${D}/${libdir}
    install -m 0644 ${B}/cpuinfo/libcpuinfo.so ${D}/${libdir}

    install -d ${D}${includedir}/tensorflow/lite
    install -d ${D}${includedir}/tensorflow/lite/c
    install -d ${D}${includedir}/tensorflow/lite/kernels
    install -d ${D}${includedir}/tensorflow/lite/core/api
    install -d ${D}${includedir}/tensorflow/lite/delegates/nnapi
    install -d ${D}${includedir}/tensorflow/lite/nnapi
    install -d ${D}${includedir}/tensorflow/lite/experimental/resource
    install -d ${D}${includedir}/tensorflow/lite/schema
    install -d ${D}${includedir}/flatbuffers

    install -m 644 ${S}/tensorflow/lite/interpreter.h ${D}${includedir}/tensorflow/lite/interpreter.h
    install -m 644 ${S}/tensorflow/lite/model.h ${D}${includedir}/tensorflow/lite/model.h
    install -m 644 ${S}/tensorflow/lite/optional_debug_tools.h ${D}${includedir}/tensorflow/lite/optional_debug_tools.h
    install -m 644 ${S}/tensorflow/lite/allocation.h ${D}${includedir}/tensorflow/lite/allocation.h
    install -m 644 ${S}/tensorflow/lite/c/common.h ${D}${includedir}/tensorflow/lite/c/common.h
    install -m 644 ${S}/tensorflow/lite/c/c_api_types.h ${D}${includedir}/tensorflow/lite/c/c_api_types.h
    install -m 644 ${S}/tensorflow/lite/kernels/register.h ${D}${includedir}/tensorflow/lite/kernels/register.h
    install -m 644 ${S}/tensorflow/lite/core/api/error_reporter.h ${D}${includedir}/tensorflow/lite/core/api/error_reporter.h
    install -m 644 ${S}/tensorflow/lite/core/api/profiler.h ${D}${includedir}/tensorflow/lite/core/api/profiler.h
    install -m 644 ${S}/tensorflow/lite/core/subgraph.h ${D}${includedir}/tensorflow/lite/core/subgraph.h
    install -m 644 ${S}/tensorflow/lite/core/api/verifier.h ${D}${includedir}/tensorflow/lite/core/api/verifier.h
    install -m 644 ${S}/tensorflow/lite/core/api/op_resolver.h ${D}${includedir}/tensorflow/lite/core/api/op_resolver.h
    install -m 644 ${S}/tensorflow/lite/core/macros.h ${D}${includedir}/tensorflow/lite/core/macros.h
    install -m 644 ${S}/tensorflow/lite/external_cpu_backend_context.h ${D}${includedir}/tensorflow/lite/external_cpu_backend_context.h
    install -m 644 ${S}/tensorflow/lite/memory_planner.h ${D}${includedir}/tensorflow/lite/memory_planner.h
    install -m 644 ${S}/tensorflow/lite/stderr_reporter.h ${D}${includedir}/tensorflow/lite/stderr_reporter.h
    install -m 644 ${S}/tensorflow/lite/type_to_tflitetype.h ${D}${includedir}/tensorflow/lite/type_to_tflitetype.h
    install -m 644 ${S}/tensorflow/lite/mutable_op_resolver.h ${D}${includedir}/tensorflow/lite/mutable_op_resolver.h
    install -m 644 ${S}/tensorflow/lite/interpreter_builder.h ${D}${includedir}/tensorflow/lite/interpreter_builder.h
    install -m 644 ${S}/tensorflow/lite/model_builder.h ${D}${includedir}/tensorflow/lite/model_builder.h
    install -m 644 ${S}/tensorflow/lite/string_type.h ${D}${includedir}/tensorflow/lite/string_type.h
    install -m 644 ${S}/tensorflow/lite/util.h ${D}${includedir}/tensorflow/lite/util.h
    install -m 644 ${S}/tensorflow/lite/portable_type_to_tflitetype.h ${D}${includedir}/tensorflow/lite/portable_type_to_tflitetype.h
    install -m 644 ${S}/tensorflow/lite/delegates/nnapi/nnapi_delegate.h ${D}${includedir}/tensorflow/lite/delegates/nnapi/nnapi_delegate.h
    install -m 644 ${S}/tensorflow/lite/nnapi/NeuralNetworksTypes.h ${D}${includedir}/tensorflow/lite/nnapi/NeuralNetworksTypes.h
    install -m 644 ${S}/tensorflow/lite/nnapi/nnapi_implementation.h ${D}${includedir}/tensorflow/lite/nnapi/nnapi_implementation.h
    install -m 644 ${S}/tensorflow/lite/experimental/resource/resource_base.h ${D}${includedir}/tensorflow/lite/experimental/resource/resource_base.h
    install -m 644 ${S}/tensorflow/lite/schema/schema_generated.h ${D}${includedir}/tensorflow/lite/schema/schema_generated.h

	install -m 644 ${S}/tensorflow/lite/*.h ${D}${includedir}/tensorflow/lite
    install -m 644 ${B}/flatbuffers/include/flatbuffers/*.h ${D}${includedir}/flatbuffers
}

FILES_${PN}-dev = ""

INSANE_SKIP_${PN} = "dev-so"
INSANE_SKIP_${PN}-dev += "dev-elf"
FILES_${PN} += "${libdir}/* ${includedir}/*"
