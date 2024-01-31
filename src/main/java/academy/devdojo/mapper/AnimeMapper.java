package academy.devdojo.mapper;

import academy.devdojo.domain.Anime;
import academy.devdojo.request.AnimePostRequest;
import academy.devdojo.request.AnimePutRequest;
import academy.devdojo.response.AnimeGetResponse;
import academy.devdojo.response.AnimePostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AnimeMapper {
    AnimeMapper INSTANCE = Mappers.getMapper(AnimeMapper.class);
    Anime toAnime(AnimePostRequest request);
    Anime toAnime(AnimePutRequest request);
    AnimePostResponse toAnimePostResponse(Anime anime);
    AnimeGetResponse toAnimeGetResponse(Anime anime);
    List<AnimeGetResponse> toAnimeGetResponseList(List<Anime> animes);
}
