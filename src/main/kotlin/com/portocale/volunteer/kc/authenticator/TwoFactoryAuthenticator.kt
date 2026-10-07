package com.portocale.volunteer.kc.authenticator

import com.portocale.volunteer.kc.ValidateOtpRequest
import com.portocale.volunteer.kc.VolunteerUserAdapter.Companion.TWO_FACTORY_ENABLED
import com.portocale.volunteer.kc.repository.BackendRepository
import com.portocale.volunteer.kc.repository.CommonModels.Companion.FormAttributes
import java.time.Duration
import java.time.OffsetDateTime
import org.keycloak.authentication.AuthenticationFlowContext
import org.keycloak.authentication.Authenticator
import org.keycloak.models.KeycloakSession
import org.keycloak.models.RealmModel
import org.keycloak.models.UserModel
import org.keycloak.models.utils.FormMessage
import org.keycloak.storage.StorageId

private const val TWO_FACTORY_FORM_TPL = "two_factory.ftl"

private const val TTL_ATTRIBUTE = "ttl"

private const val REALM_ATTRIBUTE = "realm"

private const val VALIDITY_ATTRIBUTE = "validity"

class TwoFactoryAuthenticator(
  private val repository: BackendRepository
) : Authenticator {

  override fun authenticate(context: AuthenticationFlowContext) {
    val user = context.user
    val twoFaEnabled = user.getFirstAttribute(TWO_FACTORY_ENABLED).toBoolean()
    val authSession = context.authenticationSession

    if (twoFaEnabled) {
      val sessionTtl = authSession.getAuthNote(TTL_ATTRIBUTE)
      val resend: String = context.session.context.uri.requestUri.query.substringAfter("&resend=")
      val validity: OffsetDateTime
      if (sessionTtl == null || resend.equals("true", true)) {
        repository.triggerOtp(StorageId(user.id).externalId)
        val otpValidity = OffsetDateTime.now().plusMinutes(5)
        /* Set the response to auth note to be able to read them from session later */
        authSession.setAuthNote(TTL_ATTRIBUTE, otpValidity.toString())
        validity = otpValidity
      } else {
        validity = OffsetDateTime.parse(sessionTtl)
      }
      val countdown = Duration.between(OffsetDateTime.now(), validity)

      authSession.setAuthNote(VALIDITY_ATTRIBUTE, countdown.seconds.toString())
      context . challenge (context.form()
        .setAttribute(REALM_ATTRIBUTE, context.realm)
        .setAttribute(VALIDITY_ATTRIBUTE, countdown.seconds.toString())
        .createForm(TWO_FACTORY_FORM_TPL))
      return
    }
    context.success()
  }

  override fun action(context: AuthenticationFlowContext) {
    val authSession = context.authenticationSession

    val formData = context.httpRequest.decodedFormParameters
    val otpCode = formData.getFirst(FormAttributes.OTP_CODE.value)
    val ttl = authSession.getAuthNote(TTL_ATTRIBUTE)
    val countdown = Duration.between(OffsetDateTime.now(), OffsetDateTime.parse(ttl))


    val user = context.user

    if (otpCode.isNullOrEmpty() || user == null || ttl == null) {
      context.challenge(
        context.form()
          .addError(FormMessage("volMissingInput"))
          .setAttribute(REALM_ATTRIBUTE, context.realm)
          .setAttribute(VALIDITY_ATTRIBUTE, countdown.seconds)
          .createForm(TWO_FACTORY_FORM_TPL)
      )
      return
    }
    if (OffsetDateTime.parse(ttl).isBefore(OffsetDateTime.now())) {
      context.challenge(
        context.form()
          .addError(FormMessage("volCodeExpired"))
          .createForm(TWO_FACTORY_FORM_TPL)
      )
      return
    }
    if (!repository.validateOtp(
        userId = StorageId(user.id).externalId,
        ValidateOtpRequest(
          otp = otpCode
        )
      )
    ) {
      context.challenge(
        context.form()
          .addError(FormMessage("volOtpValidationError"))
          .setAttribute(REALM_ATTRIBUTE, context.realm)
          .setAttribute(VALIDITY_ATTRIBUTE, countdown.seconds)
          .createForm(TWO_FACTORY_FORM_TPL)
      )
      return
    }
    context.success()
  }

  override fun requiresUser(): Boolean {
    return false
  }

  override fun configuredFor(
    session: KeycloakSession?,
    realm: RealmModel?,
    user: UserModel?
  ): Boolean {
    return false
  }

  override fun setRequiredActions(
    session: KeycloakSession?,
    realm: RealmModel?,
    user: UserModel?
  ) {
  }

  override fun close() {
  }

}
