package restapi.kculturebackend.domain.actor.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 필모그래피 작품 유형
 */
@Getter
@RequiredArgsConstructor
public enum FilmographyType {
    DRAMA("드라마"),
    MOVIE("영화"),
    MUSICAL("뮤지컬"),
    THEATER("연극"),
    CF("광고"),
    MUSIC_VIDEO("뮤직비디오"),
    WEB_DRAMA("웹드라마"),
    OTT("OTT"),
    OTHER("기타");

    private final String displayName;
}

