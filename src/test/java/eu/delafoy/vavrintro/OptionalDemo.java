package eu.delafoy.vavrintro;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.vavr.api.VavrAssertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import io.vavr.control.Option;

class OptionalDemo {

	@Test
	void shouldBehaveMostlyTheSameWhenSome() {
		var optionVavr = Option.of("texte");
		assertThat(optionVavr).contains("texte");

		var optionJava = Optional.of("texte");
		assertThat(optionJava).contains("texte");
	}

	@Test
	void shouldBehaveMostlyTheSameWhenEmpty() {
		var optionVavr = Option.of(null);
		assertThat(optionVavr).isSameAs(Option.none());
		assertThat(optionVavr).isEmpty();

		var optionJava = Optional.ofNullable(null);
		assertThat(optionJava).isSameAs(Optional.empty());
		assertThat(optionJava).isEmpty();
	}
}
