package kitchenpos.table.application;

import kitchenpos.table.domain.OrderTable;
import kitchenpos.table.domain.TableGroup;
import kitchenpos.table.domain.TableGroupRepository;
import kitchenpos.table.dto.TableGroupRequest;
import kitchenpos.table.dto.TableGroupResponse;
import kitchenpos.table.exception.EmptyOrderTableGroupException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TableGroupService {
    private final TableService tableService;
    private final TableGroupRepository tableGroupRepository;
    private final TableGroupValidator tableGroupValidator;
    private final ApplicationEventPublisher applicationEventPublisher;

    public TableGroupService(final TableService tableService, final TableGroupValidator tableGroupValidator, final TableGroupRepository tableGroupRepository
            , final ApplicationEventPublisher applicationEventPublisher) {
        this.tableService = tableService;
        this.tableGroupValidator = tableGroupValidator;
        this.tableGroupRepository = tableGroupRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional
    public TableGroupResponse group(final TableGroupRequest request) {
        tableGroupValidator.validateOrderTables(request);

        final TableGroup savedTableGroup = tableGroupRepository.save(new TableGroup());
        final List<OrderTable> orderTables = tableService.findAllById(request.getOrderTableIds());
        final List<Long> orderTableIds = orderTables.stream()
                .map(OrderTable::getId)
                .collect(Collectors.toList());

        applicationEventPublisher.publishEvent(TableEvent.Grouped.from(savedTableGroup.getId(), orderTableIds));
        return TableGroupResponse.of(savedTableGroup, orderTables);
    }

    @Transactional
    public void ungroup(final Long tableGroupId) {
        final TableGroup tableGroup = findByTableGroupId(tableGroupId);
        applicationEventPublisher.publishEvent(TableEvent.Ungrouped.from(tableGroup.getId()));
        tableGroupRepository.delete(tableGroup);
    }

    private TableGroup findByTableGroupId(final Long id) {
        return tableGroupRepository.findById(id)
                .orElseThrow(EmptyOrderTableGroupException::new);
    }
}