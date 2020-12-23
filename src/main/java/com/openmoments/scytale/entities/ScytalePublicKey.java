package com.openmoments.scytale.entities;

import java.util.Objects;

public final class ScytalePublicKey {
    private final Long id;
    private final String publicKey;

    public ScytalePublicKey(Long id, String publicKey) {
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
        ScytalePublicKey scytalePublicKey1 = (ScytalePublicKey) o;
        return id.equals(scytalePublicKey1.id) && publicKey.equals(scytalePublicKey1.publicKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, publicKey);
    }

    @Override
    public String toString() {
        return "PublicKey{" + "id=" + id + ", publicKey='" + publicKey + "'}";
    }
}
