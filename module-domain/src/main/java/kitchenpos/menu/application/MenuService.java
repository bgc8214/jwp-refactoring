package kitchenpos.menu.application;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.springframework.stereotype.*;

import kitchenpos.menu.domain.*;
import kitchenpos.menu.dto.*;
import kitchenpos.menu.repository.*;

@Service
public class MenuService {
    private final MenuRepository menuRepository;
    private final MenuGroupService menuGroupService;
    private final ProductService productService;

    public MenuService(MenuRepository menuRepository, MenuGroupService menuGroupService, ProductService productService) {
        this.menuRepository = menuRepository;
        this.menuGroupService = menuGroupService;
        this.productService = productService;
    }

    public MenuResponse save(MenuRequest request) {
        MenuGroup menuGroup = menuGroupService.findById(request.getMenuGroupId());
        final Menu savedMenu = menuRepository.save(Menu.of(request.getName(), request.getPrice(), menuGroup));

        Map<Long, Product> productById = getProducts(request);

        if (Objects.nonNull(productById)) {
            request.getMenuProductRequests()
                .forEach(menuProductRequest -> savedMenu.addMenuProduct(
                    productById.get(menuProductRequest.getProductId()),
                    menuProductRequest.getQuantity()
                ));
        }

        return MenuResponse.from(menuRepository.save(savedMenu));
    }

    private Map<Long, Product> getProducts(MenuRequest request) {
        if (Objects.isNull(request.getMenuProductRequests())) {
            return Collections.emptyMap();
        }
        List<Long> menuProductIds = request.getMenuProductRequests()
            .stream()
            .map(MenuProductRequest::getProductId)
            .collect(Collectors.toList());

        return productService.findByIdIn(menuProductIds)
            .stream()
            .collect(Collectors.toMap(Product::getId, Function.identity()));
    }

    public List<MenuResponse> findAll() {
        return menuRepository.findAll()
            .stream()
            .map(MenuResponse::from)
            .collect(Collectors.toList());
    }
}