package com.openmoments.scytale.entities;

import java.util.Objects;

public final class PublicKey {
    private Long id;
    private String publicKey;

    public PublicKey(Long id, String publicKey) {
        this.id = id;
        this.publicKey = publicKey;
    }

    public Long getId() {
        return id;
    }

    public String getPublicKey() {
        return publicKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublicKey publicKey1 = (PublicKey) o;
        return id.equals(publicKey1.id) && publicKey.equals(publicKey1.publicKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, publicKey);
    }
}
