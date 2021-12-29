package kitchenpos.menu.application;

import java.util.List;
import kitchenpos.menu.domain.Menu;
import kitchenpos.menu.domain.MenuRepository;
import kitchenpos.menu.domain.MenuValidator;
import kitchenpos.menu.dto.MenuRequest;
import kitchenpos.menu.dto.MenuResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuValidator menuValidator;

    public MenuService(
        final MenuRepository menuRepository,
        final MenuValidator menuValidator
    ) {
        this.menuRepository = menuRepository;
        this.menuValidator = menuValidator;
    }

    @Transactional
    public MenuResponse create(final MenuRequest menuRequest) {
        Menu menu = menuRequest.toMenu();
        menu.registerMenu(menuValidator);

        return MenuResponse.of(menuRepository.save(menu));
    }

    public List<MenuResponse> list() {
        return MenuResponse.toList(menuRepository.findAll());
    }
}