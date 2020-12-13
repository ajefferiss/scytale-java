package com.openmoments.scytale.entities;

import java.util.Objects;

public final class KeyStore {

    private Long id;
    private String name;

    public KeyStore(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyStore keyStore = (KeyStore) o;
        return id.equals(keyStore.id) && name.equals(keyStore.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "KeyStore{id='" + id + "', name='" + name + "'}";
    }
}
