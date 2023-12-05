package com.Football.football.Repositories;

import com.Football.football.Tables.PogrupowaneStatystykiZawodnikow;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PogrupowaneRepository extends CrudRepository<PogrupowaneStatystykiZawodnikow, Long> {
    Optional<PogrupowaneStatystykiZawodnikow> getPogrupowaneStatystykiZawodnikowById(Long id);
}
