package com.flysolo.etrikedriver.models.contacts


const val CONTACT_COLLECTION = "contacts"
data class Contacts(
    val id : String ? = null,
    val name : String ? = null,
    val phone : String ? = null,
    val type : ContactType? = ContactType.PARENT
)

enum class ContactType {
    PARENT,
    GUARDIAN,
    SIBLING,
    SPOUSE,
    OTHER
}