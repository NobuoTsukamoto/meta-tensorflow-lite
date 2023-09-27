require recipes-framework/coral/libedgetpu-common.inc

RCONFLICTS:${PN} = " libedgetpu-std"

EXTRA_OEMAKE = " libedgetpu"

do_install:prepend() {
    install -d ${D}/${libdir}
    install -m 0755 ${S}/out/direct/k8/libedgetpu.so.1 ${D}/${libdir}

    install -d ${D}/etc/udev/rules.d/
    install -m 644 ${S}/debian/edgetpu-accelerator.rules ${D}/etc/udev/rules.d/99-edgetpu-accelerator.rules
}
