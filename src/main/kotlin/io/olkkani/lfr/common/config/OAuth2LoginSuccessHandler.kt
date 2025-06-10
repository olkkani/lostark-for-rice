package io.olkkani.lfr.common.config

//@Component
//class OAuth2LoginSuccessHandler(
//    private val jwtTokenProvider: JwtTokenProvider,
//    private val tokenRepo: TokenRepo
//) : AuthenticationSuccessHandler {
//
//    override fun onAuthenticationSuccess(
//        request: HttpServletRequest,
//        response: HttpServletResponse,
//        authentication: Authentication
//    ) {
//        val accessToken = jwtTokenProvider.createToken(authentication.name)
//        val refreshToken = TsidCreator.getTsid().toString()
//
//        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
//        response.addCookie(createHttpOnlyCookie(refreshToken))
//    }
//
//    fun saveToken(refreshToken: RefreshToken){
//        tokenRepo.save(refreshToken)
//    }
//
//    fun validateToken(token: String): Boolean {
//        return jwtTokenProvider.validateToken(token)
//    }
//    fun validateRefreshToken(token: String, deviceId: String): Boolean {
//
//        return jwtTokenProvider.validateToken(token)
//    }
//
//    fun getUserNameByToken(token: String): String {
//        return jwtTokenProvider.getUserNameByToken(token)
//    }
//
//    private fun createHttpOnlyCookie(value: String): Cookie {
//        return Cookie("refresh_token", value).apply {
//            maxAge = 604800  // 7 days
//            secure = true
//            isHttpOnly = true
//            path = "/"
//        }
//    }
//}