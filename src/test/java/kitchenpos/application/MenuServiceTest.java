package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.dto.MenuProductRequest;
import kitchenpos.dto.MenuProductResponse;
import kitchenpos.dto.MenuRequest;
import kitchenpos.dto.MenuResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {
    @Mock
    private MenuRepository menuDao;
    @Mock
    private MenuGroupRepository menuGroupDao;
    @Mock
    private ProductRepository productDao;
    @InjectMocks
    private MenuService menuService;

    private Product product1;
    private Product product2;
    private MenuGroup group;

    @BeforeEach
    void setUp() {
        product1 = new Product(1L, "product1", BigDecimal.valueOf(100));
        product2 = new Product(2L, "product2", BigDecimal.valueOf(500));
        group = new MenuGroup(1L, "group");
    }

    @Test
    @DisplayName("메뉴 그룹을 생성할 수 있다.")
    void create() {
        //given
        MenuRequest request = new MenuRequest("menu1", BigDecimal.valueOf(10000), 1L,
                Arrays.asList(new MenuProductRequest(product1.getId(), 10),
                        new MenuProductRequest(product2.getId(), 20)));
        Menu menu = new Menu("menu1", BigDecimal.valueOf(10000), new MenuGroup(1L, "group"));
        menu.addMenuProduct(product1, 10);
        menu.addMenuProduct(product2, 20);
        given(menuGroupDao.findById(any())).willReturn(Optional.of(group));
        given(menuDao.save(any())).willReturn(menu);
        given(productDao.findById(product1.getId())).willReturn(Optional.of(product1));
        given(productDao.findById(product2.getId())).willReturn(Optional.of(product2));

        //when
        MenuResponse savedMenu = menuService.create(request);

        //then
        assertThat(savedMenu.getProducts().stream().map(MenuProductResponse::getQuantity).collect(Collectors.toList()))
                .isNotEmpty().containsExactlyInAnyOrder(10, 20);

    }

    @Test
    @DisplayName("메뉴 그룹에 속하지 않으면 메뉴를 추가할 수 없다")
    void create_failed_2() {
        //given
        given(menuGroupDao.findById(any())).willReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> menuService.create(
                new MenuRequest("name", BigDecimal.TEN, 0L, Collections.emptyList()))).isExactlyInstanceOf(
                IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 상품의 금액보다 메뉴 가격이 크거나 같으면 메뉴로 추가할 수 없다")
    void create_failed_3() {
        //given
        given(menuGroupDao.findById(any())).willReturn(Optional.of(group));

        //then
        assertThatThrownBy(() -> menuService.create(new MenuRequest("name", BigDecimal.valueOf(10000), 0L,
                Arrays.asList(new MenuProductRequest(1L, 5), new MenuProductRequest(2L, 3))))).isExactlyInstanceOf(
                IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 상품 중 조회 되지 않는 경우가 있으면 메뉴로 추가할 수 없다")
    void create_failed_4() {
        //given
        given(menuGroupDao.findById(any())).willReturn(Optional.of(group));
        given(productDao.findById(3L)).willReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> menuService.create(new MenuRequest("name", BigDecimal.valueOf(10000), 0L,
                Arrays.asList(new MenuProductRequest(3L, 5), new MenuProductRequest(2L, 3))))).isExactlyInstanceOf(
                IllegalArgumentException.class);
    }

    @Test
    @DisplayName("전체 메뉴 그룹을 조회할 수 있다.")
    void list() {
        //given
        Menu menu = new Menu("menu1", BigDecimal.valueOf(10000), new MenuGroup(1L, "group"));
        menu.addMenuProduct(product1, 10);
        menu.addMenuProduct(product2, 20);
        given(menuDao.findAll()).willReturn(Arrays.asList(menu));

        //then
        assertThat(menuService.list()).isNotEmpty();
    }
}
