package com.Football.football.Repositories;

import com.Football.football.Tables.StatystykiZawodnika;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface StatystykiZawodnikaRepository extends CrudRepository<StatystykiZawodnika, Long> {


    Optional<StatystykiZawodnika> getStatystykiZawodnikaById(Long id);
    List<StatystykiZawodnika> getStatystykiZawodnikaByImieContaining(String name);
}
