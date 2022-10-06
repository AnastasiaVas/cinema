package com.test.cinema.controllers;

import com.test.cinema.dto.MovieDTO;
import com.test.cinema.dto.checks.CreateChecks;
import com.test.cinema.dto.checks.UpdateChecks;
import com.test.cinema.services.MoviesService;
import com.test.cinema.specification.SearchCriteria;
import com.test.cinema.utils.enums.CreateUpdate;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movie")
@AllArgsConstructor
public class MoviesController {

    private final MoviesService moviesService;

    @PostMapping
    public ResponseEntity<MovieDTO> createMovie(@RequestBody @Validated(CreateChecks.class) MovieDTO movieDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(moviesService.createOrUpdateMovie(movieDTO, CreateUpdate.CREATE));
    }

    @PatchMapping
    public ResponseEntity<MovieDTO> updateMovie(@RequestBody @Validated(UpdateChecks.class) MovieDTO movieDTO) {
        return ResponseEntity.ok().body(moviesService.createOrUpdateMovie(movieDTO, CreateUpdate.UPDATE));
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDTO> getMovieById(@PathVariable Integer movieId) {
        return ResponseEntity.ok().body(moviesService.findMovieById(movieId));
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<String> deleteMovieById(@PathVariable Integer movieId) {
        return ResponseEntity.ok().body(moviesService.deleteMovieById(movieId));
    }

    @PostMapping("/criteria")
    public ResponseEntity<List<MovieDTO>> findByCriteria(@RequestBody List<SearchCriteria> searchCriteria,
                                                         @RequestParam Integer pageNum,
                                                         @RequestParam Integer pageSize) {
        return ResponseEntity.ok().body(moviesService.findByFields(searchCriteria, pageNum, pageSize));
    }
}
