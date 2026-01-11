package restapi.kculturebackend.domain.job.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 작품구인 카테고리
 */
@Getter
@RequiredArgsConstructor
public enum JobCategory {
    SHORT_FILM("단편영화"),
    FEATURE_FILM("장편영화"),
    WEB_DRAMA("웹드라마"),
    ADVERTISEMENT("광고"),
    MUSIC_VIDEO("뮤직비디오"),
    OTHER("기타");

    private final String displayName;
}
