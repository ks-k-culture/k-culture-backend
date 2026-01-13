package restapi.kculturebackend.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import restapi.kculturebackend.domain.favorite.entity.FavoriteType;

/**
 * String to FavoriteType 변환기
 * 대소문자 구분 없이 변환
 */
@Component
public class StringToFavoriteTypeConverter implements Converter<String, FavoriteType> {

    @Override
    public FavoriteType convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        // 대소문자 무시하고 변환
        return FavoriteType.valueOf(source.toUpperCase());
    }
}
