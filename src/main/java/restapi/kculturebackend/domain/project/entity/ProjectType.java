package restapi.kculturebackend.domain.project.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 프로젝트 유형
 */
@Getter
@RequiredArgsConstructor
public enum ProjectType {
    DRAMA("드라마"),
    MOVIE("영화"),
    MUSICAL("뮤지컬"),
    THEATER("연극"),
    CF("광고"),
    MUSIC_VIDEO("뮤직비디오"),
    WEB_DRAMA("웹드라마"),
    OTT("OTT 시리즈"),
    SHORT_FILM("단편영화"),
    DOCUMENTARY("다큐멘터리"),
    OTHER("기타");

    private final String displayName;
}

