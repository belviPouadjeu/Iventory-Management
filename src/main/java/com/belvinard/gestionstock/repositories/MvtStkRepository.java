package com.belvinard.gestionstock.repositories;

import com.belvinard.gestionstock.models.MvtStk;
import com.belvinard.gestionstock.models.SourceMvtStk;
import com.belvinard.gestionstock.models.TypeMvtStk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MvtStkRepository extends JpaRepository<MvtStk, Long> {
    List<MvtStk> findAllByArticleIdOrderByDateMvtDesc(Long articleId);
    List<MvtStk> findAllByEntrepriseId(Long entrepriseId);
    List<MvtStk> findByTypeMvt(TypeMvtStk typeMvt);
    List<MvtStk> findBySourceMvt(SourceMvtStk sourceMvt);
    List<MvtStk> findByDateMvtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
