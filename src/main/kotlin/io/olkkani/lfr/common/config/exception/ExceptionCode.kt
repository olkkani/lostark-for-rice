package io.olkkani.lfr.common.config.exception

import org.springframework.http.HttpStatus


enum class ExceptionCode(val status: HttpStatus, val message : String) {
    /* 400 BAD_REQUEST : 잘못된 요청 */
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "리프레시 토큰이 유효하지 않습니다"),
    MISMATCH_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "리프레시 토큰의 유저 정보가 일치하지 않습니다"),
    CANNOT_FOLLOW_MYSELF(HttpStatus.BAD_REQUEST, "자기 자신은 팔로우 할 수 없습니다"),

    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    INVALID_AUTH_TOKEN(HttpStatus.UNAUTHORIZED, "권한 정보가 없는 토큰입니다"),
    UNAUTHORIZED_MEMBER(HttpStatus.UNAUTHORIZED, "현재 내 계정 정보가 존재하지 않습니다"),

    /* FORBIDDEN: 거부됍 */
    FETCH_ERROR_LOA(HttpStatus.FORBIDDEN, "LOSTARK API 로 부터 응답이 없습니다."),

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저 정보를 찾을 수 없습니다"),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "로그아웃 된 사용자입니다"),
    NOT_FOLLOW(HttpStatus.NOT_FOUND, "팔로우 중이지 않습니다"),

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "데이터가 이미 존재합니다"),

}