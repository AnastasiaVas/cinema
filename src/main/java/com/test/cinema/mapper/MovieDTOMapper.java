package com.test.cinema.mapper;

import com.test.cinema.db.entites.MovieEntity;
import com.test.cinema.dto.MovieDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface MovieDTOMapper {

    MovieDTO movieEntityToMovieDTO(MovieEntity movieEntity);

    MovieEntity movieDTOToMovie(MovieDTO createUpdatePartnerDTO);

    @BeanMapping(nullValuePropertyMappingStrategy =  NullValuePropertyMappingStrategy.IGNORE)
    MovieEntity movieDTOToMovie(MovieDTO createUpdatePartnerDTO, @MappingTarget MovieEntity movieEntity);
}
