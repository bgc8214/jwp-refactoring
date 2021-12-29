package kitchenpos.menu.application;


import static kitchenpos.menu.application.fixture.MenuFixture.요청_메뉴;
import static kitchenpos.menu.application.fixture.MenuProductFixture.메뉴상품;
import static kitchenpos.menu.application.fixture.MenuProductFixture.요청_메뉴상품_치킨;
import static kitchenpos.menugroup.application.fixture.MenuGroupFixture.메뉴그룹_치킨류;
import static kitchenpos.product.application.fixture.ProductFixture.후리이드치킨;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;
import kitchenpos.menu.domain.Menu;
import kitchenpos.menu.domain.MenuProduct;
import kitchenpos.menu.domain.MenuRepository;
import kitchenpos.menu.domain.MenuValidator;
import kitchenpos.menu.dto.MenuRequest;
import kitchenpos.menu.dto.MenuResponse;
import kitchenpos.menugroup.domain.MenuGroup;
import kitchenpos.menugroup.domain.MenuGroupRepository;
import kitchenpos.product.domain.Product;
import kitchenpos.product.domain.ProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("메뉴 관리 기능")
@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;
    @Mock
    private MenuGroupRepository menuGroupRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuValidator menuValidator;

    @InjectMocks
    private MenuService menuService;

    @Test
    @DisplayName("`메뉴`를 등록할 수 있다.")
    void create() {
        // given
        Product 치킨 = 후리이드치킨();
        MenuProduct 메뉴_치킨 = 메뉴상품(치킨);
        MenuGroup 메뉴_그룹 = 메뉴그룹_치킨류();
        MenuRequest menuRequest = 요청_메뉴("메뉴이름", 14000, 1L, Collections.singletonList(요청_메뉴상품_치킨()));

        Menu 등록_메뉴 = menuRequest.toMenu();

        given(menuRepository.save(any())).willReturn(등록_메뉴);

        // when
        MenuResponse 등록된_메뉴 = menuService.create(menuRequest);

        // then
        메뉴생성_검증_됨();
        메뉴등록_됨(등록된_메뉴);
    }

    @Test
    @DisplayName("`메뉴`의 목록을 조회할 수 있다.")
    void 메뉴_목록_조회() {
        // given
        Product 치킨 = 후리이드치킨();
        MenuProduct 메뉴_치킨 = 메뉴상품(치킨);
        MenuGroup 메뉴_그룹 = 메뉴그룹_치킨류();
        Menu 등록_메뉴 = Menu.of("메뉴", 14000, 메뉴_그룹.getId(), Collections.singletonList(메뉴_치킨));

        given(menuRepository.findAll()).willReturn(Collections.singletonList(등록_메뉴));

        // when
        List<MenuResponse> 메뉴목록 = menuService.list();

        // then
        메뉴목록_조회됨(메뉴목록);
    }

    private void 메뉴등록_됨(MenuResponse menuResponse) {
        assertThat(menuResponse).isNotNull();
    }

    private void 메뉴목록_조회됨(List<MenuResponse> 메뉴목록) {
        assertThat(메뉴목록).isNotEmpty();
    }

    private void 메뉴생성_검증_됨() {
        verify(menuValidator).validateRegister(any(Menu.class));
    }
}