package kitchenpos.table.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import kitchenpos.order.domain.Order;
import kitchenpos.order.domain.Orders;
import kitchenpos.table.exception.CannotChangeNumberOfGuestException;
import kitchenpos.table.exception.CannotChangeTableEmptyException;
import kitchenpos.table.exception.CannotUngroupOrderTableException;

@Entity
public class OrderTable {

    public static final String THIS_IS_A_GROUP_ORDER_TABLE = "단체 지정된 주문테이블인 경우 빈 테이블로 변경이 불가능합니다.";
    public static final String THERE_IS_AN_ONGOING_ORDER = "진행중(조리 or 식사)인 경우 빈 테이블로 변경이 불가능하다.";
    public static final String THE_NUMBER_OF_GUESTS_CANNOT_BE_NEGATIVE = "변경하려는 손님 수는 음수일 수 없다.";
    public static final String THERE_IS_AN_EMPTY_ORDER_TABLE = "빈 테이블의 주문 테이블은 손님 수를 변경할 수 없습니다.";
    public static final String THERE_IS_A_HISTORY_OF_ORDERS_AT_AN_ONGOING = "진행중(조리 or 식사)인 단계의 주문 이력이 존재할 경우 해제가 불가능하다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_group_id", foreignKey = @ForeignKey(name = "fk_order_table_table_group"))
    private TableGroup tableGroup;

    @Column(nullable = false)
    private int numberOfGuests;

    @Column(nullable = false)
    private boolean empty;

    @Embedded
    private Orders orders = new Orders();

    public OrderTable() {
    }

    public OrderTable(Long id) {
        this.id = id;
    }

    public OrderTable(Long id, int numberOfGuests) {
        this.id = id;
        this.numberOfGuests = numberOfGuests;
        this.empty = true;
    }

    public OrderTable(Long id, int numberOfGuests, Order order) {
        this.id = id;
        this.numberOfGuests = numberOfGuests;
        this.empty = true;
        this.orders.addOrder(order);
    }

    public OrderTable(int numberOfGuests, boolean empty) {
        this.numberOfGuests = numberOfGuests;
        this.empty = empty;
    }

    public OrderTable(Long id, int numberOfGuests, boolean empty) {
        this.id = id;
        this.numberOfGuests = numberOfGuests;
        this.empty = empty;
    }

    public OrderTable(Long id, Long tableGroupId, int numberOfGuests) {
        this.id = id;
        if (tableGroupId != null) {
            this.tableGroup = new TableGroup(tableGroupId);
        }
        this.numberOfGuests = numberOfGuests;
        this.empty = true;
    }

    public OrderTable(Long id, Long tableGroupId, Integer numberOfGuests, boolean empty) {
        this.id = id;
        this.tableGroup = new TableGroup(tableGroupId);
        this.numberOfGuests = numberOfGuests;
        this.empty = empty;
    }

    public OrderTable(TableGroup tableGroup, Integer numberOfGuests, boolean empty) {
        this.tableGroup = tableGroup;
        this.numberOfGuests = numberOfGuests;
        this.empty = empty;
    }

    public OrderTable(Long id, TableGroup tableGroup, Integer numberOfGuests, boolean empty) {
        this.id = id;
        this.tableGroup = tableGroup;
        this.numberOfGuests = numberOfGuests;
        this.empty = empty;
    }

    public void addOrder(Order order) {
        orders.addOrder(order);
        order.toOrderTable(this);
    }

    public Long getId() {
        return id;
    }

    public TableGroup getTableGroup() {
        return tableGroup;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void ungroup() {
        if (isNotCompleted()) {
            throw new CannotUngroupOrderTableException(THERE_IS_A_HISTORY_OF_ORDERS_AT_AN_ONGOING);
        }
        this.tableGroup = null;
    }

    public void changeEmpty(boolean empty) {
        validationChangeEmpty();
        this.empty = empty;
    }

    private void validationChangeEmpty() {
        if (Objects.nonNull(tableGroup)) {
            throw new CannotChangeTableEmptyException(THIS_IS_A_GROUP_ORDER_TABLE);
        }
        if (isNotCompleted()) {
            throw new CannotChangeTableEmptyException(THERE_IS_AN_ONGOING_ORDER);
        }
    }

    public void changeNumberOfGuests(int numberOfGuests) {
        validationChangeNumberOfGuests(numberOfGuests);
        this.numberOfGuests = numberOfGuests;
    }

    private void validationChangeNumberOfGuests(int numberOfGuests) {
        if (numberOfGuests < 0) {
            throw new CannotChangeNumberOfGuestException(THE_NUMBER_OF_GUESTS_CANNOT_BE_NEGATIVE);
        }
        if (empty) {
            throw new CannotChangeNumberOfGuestException(THERE_IS_AN_EMPTY_ORDER_TABLE);
        }
    }

    public void toTableGroup(TableGroup tableGroup) {
        this.tableGroup = tableGroup;
    }

    public boolean isNotCompleted() {
        return orders.isNotCompleted();
    }
}