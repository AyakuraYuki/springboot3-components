package cc.ayakurayuki.spring.components.utility.cryptography.rsa;

public record RSARawKey(
    String modulus,
    String exponent
) {}
