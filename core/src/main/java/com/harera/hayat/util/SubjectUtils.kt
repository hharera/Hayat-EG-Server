package com.harera.hayat.util

object SubjectUtils {

    fun getSubject(subject: String): Subject {
        return if (RegexUtils.isPhoneNumber(subject)) {
            Subject.PhoneNumber(subject)
        } else if (RegexUtils.isEmail(subject)) {
            Subject.Email(subject)
        } else if (RegexUtils.isUsername(subject)) {
            Subject.Username(subject)
        } else {
            Subject.Other
        }
    }
}