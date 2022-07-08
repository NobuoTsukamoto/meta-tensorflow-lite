require recipes-framework/coral/libedgetpu-common.inc

RCONFLICTS:${PN} = " libedgetpu-max"

EXTRA_OEMAKE = " libedgetpu-throttled"

do_install:prepend() {
    install -d ${D}/${libdir}
    install -m 0755 ${S}/out/throttled/k8/libedgetpu.so.1 ${D}/${libdir}

    install -d ${D}/etc/udev/rules.d/
    install -m 644 ${S}/debian/edgetpu-accelerator.rules ${D}/etc/udev/rules.d/99-edgetpu-accelerator.rules
}
