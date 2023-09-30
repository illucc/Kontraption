package net.illuc.kontraption.registry

interface RegistrySupplier<T> {

    val name: String
    fun get(): T

}