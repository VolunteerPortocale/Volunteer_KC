package com.portocale.volunteer.kc.repository

class CommonModels {
  companion object {
    enum class FormAttributes(val value: String) {
      USERNAME("username"),
      OLD_PASSWORD("oldPassword"),
      NEW_PASSWORD("newPassword"),
      CONFIRM_PASSWORD("confirmPassword"),
      OTP_CODE("otp")
    }
  }
}
