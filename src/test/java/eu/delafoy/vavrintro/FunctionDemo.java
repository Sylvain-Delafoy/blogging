package eu.delafoy.vavrintro;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import io.vavr.API;
import io.vavr.Function0;
import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.collection.Set;

class FunctionDemo {

	@Test
	void function0ShouldImproveSupplier() {
		Supplier<Integer> java = () -> 4;
		assertThat(java.get()).isEqualTo(4);

		Function0<Integer> vavr = API.Function(() -> 4);
		assertThat(vavr.apply()).isEqualTo(4);
		// Vavr essaie de faire implémenter a ses types leur équivalent java
		assertThat(vavr.get()).isEqualTo(4);

		// Tout en apportant quelques méthodes manquant dans Java.
		assertThat(vavr.andThen(x -> x + 2).apply()).isEqualTo(6);
	}

	private Function1<Integer, Integer> doStuffAndIncrement(AtomicInteger counter) {
		return t1 -> {
			counter.incrementAndGet();
			return t1 * 2;
		};
	}

	@Test
	void functionCallsCountsUsuallyKeepGrowing() {
		AtomicInteger counter = new AtomicInteger();
		Function1<Integer, Integer> noMemoize = doStuffAndIncrement(counter);

		assertThat(noMemoize.apply(3)).isEqualTo(6);
		assertThat(counter).hasValue(1);
		assertThat(noMemoize.apply(3)).isEqualTo(6);
		assertThat(counter).hasValue(2);
	}


	@Test
	void memoizedFunctionComputationsAreDoneOnlyWhenUsefull() {
		AtomicInteger counter = new AtomicInteger();
		Function1<Integer, Integer> memoized = doStuffAndIncrement(counter).memoized();
		assertThat(memoized.apply(3)).isEqualTo(6);
		assertThat(counter).hasValue(1);
		assertThat(memoized.apply(4)).isEqualTo(8);
		assertThat(counter).hasValue(2);
		assertThat(memoized.apply(3)).isEqualTo(6);
		assertThat(counter).hasValue(2);
	}


	// Il s'agit de la version Vavr des set qui sont des structures immutables
	private static final Set<String> ARTICLES_PERMANENTS = API.Set("Pain", "Croissant", "Pain au chocolat");
	private static final Set<String> EXTRAS_NOËL = API.Set("Buche Chocolat");
	private static final Set<String> EXTRAS_ÉPIPHANIE = API.Set("Galette");

	@Test
	void lesFonctionsPeuventÊtreCurrifiées() throws Exception {
		// Un catalogue est un ensemble de (noms de) produits permanents et saisonniers.
		Function2<Set<String>, Set<String>, Set<String>> créerUnCatalogue = API.Function(Set<String>::addAll);

		// La currification permet de créer une fonction qui prends en paramètre
		// les articles saisonniers et retourne le catalogue complet.
		Function1<Set<String>, Set<String>> creerLeCatalogueSaisonnier = créerUnCatalogue
				.curried()
				.apply(ARTICLES_PERMANENTS);

		Set<String> catalogueNoël = creerLeCatalogueSaisonnier.apply(EXTRAS_NOËL);
		assertThat(catalogueNoël).containsExactlyInAnyOrder(
				"Pain", "Croissant", "Pain au chocolat",
				"Buche Chocolat");

		Set<String> catalogueÉpiphanie = creerLeCatalogueSaisonnier.apply(EXTRAS_ÉPIPHANIE);
		assertThat(catalogueÉpiphanie).containsExactlyInAnyOrder(
				"Pain", "Croissant", "Pain au chocolat",
				"Galette");

		assertThat(ARTICLES_PERMANENTS).containsExactlyInAnyOrder(
				"Pain", "Croissant", "Pain au chocolat");

	}
}
