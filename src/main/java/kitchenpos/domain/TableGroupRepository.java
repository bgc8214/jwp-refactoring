package kitchenpos.domain;

import javax.persistence.Table;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TableGroupRepository extends JpaRepository<TableGroup, Long> {

}
