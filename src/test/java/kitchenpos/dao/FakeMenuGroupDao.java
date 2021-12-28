package kitchenpos.dao;

import kitchenpos.domain.MenuGroup;

import java.util.*;


public class FakeMenuGroupDao implements MenuGroupDao {
    private Map<Long, MenuGroup> map = new HashMap<>();
    private Long key = 1L;

    @Override
    public MenuGroup save(MenuGroup menuGroup) {
        menuGroup.createId(key);
        map.put(key, menuGroup);
        key++;
        return menuGroup;
    }

    @Override
    public Optional<MenuGroup> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList<>(map.values());
    }

    @Override
    public boolean existsById(Long id) {
        return map.containsKey(id);
    }
}