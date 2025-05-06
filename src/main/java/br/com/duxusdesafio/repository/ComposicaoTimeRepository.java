package br.com.duxusdesafio.repository;

import br.com.duxusdesafio.model.ComposicaoTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComposicaoTimeRepository extends JpaRepository<ComposicaoTime, Long> {
    @Modifying
    @Query("DELETE FROM ComposicaoTime c WHERE c.time.id = :timeId")
    void deleteByTimeId(@Param("timeId") Long timeId);

    List<ComposicaoTime> findByTimeId(Long timeId);
}
