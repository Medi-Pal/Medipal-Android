package com.example.medipal.navigation

enum class Route(val route: String){
    LANDING("Landing"),
    LOGIN("Login"),
    OTP("Otp"),
    HOME("Home"),
    SOS("Sos"),
    QRCODE("Scanner"),
    NOTIFICATION("Notification"),
    PRESCRIPTION_LIST("PrescriptionList"),
    PRESCRIPTION_DETAIL("PrescriptionDetail/{prescriptionId}"),
    USER_DETAILS_SCREEN("UserDetails"),
    PROFILE("Profile"),
    EDIT_PROFILE("EditProfile"),
    PRIVACY_POLICY("PrivacyPolicy"),
    SETTINGS("Settings"),
    ARTICLES("Articles"),
    ARTICLE_DETAIL("ArticleDetail?title={title}&imageRes={imageRes}&content={content}&readTime={readTime}"),
    DOCTORS("DoctorsList"),
    DOCTOR_DETAIL("DoctorDetail?doctorId={doctorId}")
}