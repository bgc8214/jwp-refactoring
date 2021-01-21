package kitchenpos.common.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PositiveQuantityTest {

	@Test
	@DisplayName("수량을 0 으로 생성하려고 하면 IllegalArgumentException 을 throw 해야한다.")
	void nullPrice() {
		//when-then
		assertThatThrownBy(() -> new PositiveQuantity(0))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("수량을 음수로 생성하려고 하면 IllegalArgumentException 을 throw 해야한다.")
	void negativePrice() {
		//when-then
		assertThatThrownBy(() -> new PositiveQuantity(-200))
			.isInstanceOf(IllegalArgumentException.class);
	}
}