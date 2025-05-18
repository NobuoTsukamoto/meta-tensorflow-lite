FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:riscv32 = " \
    file://0001-link-libatomic-to-spinlock_wait.patch \
 "