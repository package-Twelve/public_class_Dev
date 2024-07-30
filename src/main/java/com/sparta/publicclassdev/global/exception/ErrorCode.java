package com.sparta.publicclassdev.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
	FAIL(500, "실패했습니다."),
	NOT_UNAUTHORIZED(401, "권한이 없습니다."),
	UNAUTHENTICATED(401, "로그인 후 이용해주세요."),
	UNAUTHORIZED_MANAGER(403, "매니저가 아닙니다."),
	INVALID_REQUEST(400, "잘못 된 요청입니다."),
	INCORRECT_PASSWORD(400, "입력하신 비밀번호가 일치하지 않습니다."),
	INCORRECT_MANAGER_KEY(400, "입력하신 MANAGER키가 일치하지 않습니다."),
	CHECK_EMAIL(400, "이메일을 확인해주세요"),
	INVALID_URL_ACCESS(400, "잘못된 URL 접근입니다."),
	USER_NOT_FOUND(400, "해당하는 유저를 찾을 수 없습니다."),
	USER_NOT_UNIQUE(409,"사용 중인 이메일입니다."),
	USER_NOT_TEAM(404, "해당 유저가 팀에 없습니다."),
	USER_ALREADY_TEAM(400, "해당 유저가 이미 팀에 있습니다."),
	TOKEN_EXPIRED(401, "토큰이 만료되었습니다."),
	INVALID_TOKEN(401, "잘못된 JWT 토큰입니다."),
	TOKEN_MISMATCH(401, "토큰이 일치하지 않습니다."),
	RE_LOGIN_REQUIRED(401, "재로그인 해주세요"),
	USER_WITHDRAW(403, "이미 탈퇴한 회원입니다."),
	NOT_SUPPORT_LANGUAGE(404, "지원되지 않는 언어입니다."),


	// Community
	NOT_FOUND_COMMUNITY_POST(400, "해당 게시물이 존재하지 않습니다."),
	NOT_FOUND_CATEGORY(400, "해당 카테고리가 존재하지 않습니다"),
	NOT_FOUND_COMMUNITY_COMMENT(400, "해당 댓글이 존재하지 않습니다."),

	// CodeReview
	NOT_FOUND_CODEREVIEW_POST(400, "해당 코드리뷰는 존재하지 않습니다."),
	NOT_FOUND_CODEREVIEW_COMMENT(400, "해당 코드리뷰 댓글은 존재하지 않습니다."),

	// File
	FILE_UPLOAD_FAILED(500, "파일 업로드에 실패했습니다."),
	FILE_DOWNLOAD_FAILED(500, "파일 다운로드에 실패했습니다."),

	// User
	NAME_NOT_UNIQUE(409,"사용 중인 이름입니다."),
	USER_LOGOUT(403, "이미 로그아웃한 회원입니다."),
	TOKEN_NOTFOUND(404, "토큰을 찾을 수 없습니다."),

	//Team
	TEAM_NOT_FOUND(404, "해당 팀을 찾을 수 없습니다"),

	//CodeKata
	NOT_FOUND_CODEKATA(404, "해당 코드카타를 찾을 수 없습니다.");

	private final Integer status;
	private final String message;
}