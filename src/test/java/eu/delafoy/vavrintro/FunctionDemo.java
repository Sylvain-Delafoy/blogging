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
	void uneFonction0VavrAmélioreSupplier() {
		Supplier<Integer> java = () -> 4;
		assertThat(java.get()).isEqualTo(4);

		Function0<Integer> vavr = (() -> 4);
		assertThat(vavr.apply()).isEqualTo(4);
		// Vavr essaie de faire implémenter a ses types leur équivalent java
		assertThat(vavr.get()).isEqualTo(4);

		// Tout en apportant quelques méthodes manquant dans Java.
		assertThat(vavr.andThen(x -> x + 2).apply()).isEqualTo(6);
	}

	@Test
	void uneFonctionEstGénéralementExecutéeAChaqueAppel() {
		AtomicInteger compteur = new AtomicInteger();
		Function1<Integer, Integer> fonctionBrute = (Integer i) -> {
			compteur.incrementAndGet();
			return i * 2;
		};

		assertThat(fonctionBrute.apply(3)).isEqualTo(6);
		assertThat(compteur).hasValue(1);
		assertThat(fonctionBrute.apply(3)).isEqualTo(6);
		assertThat(compteur).hasValue(2);
	}

	@Test
	void uneFonctionMemoïséeNeFaitPasLeTravailDeuxFois() {
		AtomicInteger compteur = new AtomicInteger();
		// Ici, il faut "aider" java pour pouvoir appeller une méthode sur la fonction.
		Function1<Integer, Integer> fonctionMemoïzée = API.Function((Integer i) -> {
			compteur.incrementAndGet();
			return i * 2;
		}).memoized();
		assertThat(fonctionMemoïzée.apply(3)).isEqualTo(6);
		assertThat(compteur).hasValue(1);
		assertThat(fonctionMemoïzée.apply(4)).isEqualTo(8);
		assertThat(compteur).hasValue(2);
		assertThat(fonctionMemoïzée.apply(3)).isEqualTo(6);
		assertThat(compteur).hasValue(2);
	}

	// Il s'agit de la version Vavr des set qui sont des structures immutables
	private static final Set<String> ARTICLES_PERMANENTS = API.Set("Pain", "Croissant", "Pain au chocolat");
	private static final Set<String> EXTRAS_NOËL = API.Set("Buche Chocolat");
	private static final Set<String> EXTRAS_ÉPIPHANIE = API.Set("Galette");

	@Test
	void lesFonctionsPeuventÊtreCurrifiées() throws Exception {
		// Un catalogue est un ensemble de (noms de) produits permanents et saisonniers.
		Function2<Set<String>, Set<String>, Set<String>> créerUnCatalogue = API.Function(Set<String>::addAll);

		// La currification permet de créer une fonction qui prends en paramètre les
		// articles saisonniers et retourne le catalogue complet.
		Function1<Set<String>, Set<String>> créerLeCatalogueSaisonnier = créerUnCatalogue
				.curried()
				.apply(ARTICLES_PERMANENTS);

		Set<String> catalogueNoël = créerLeCatalogueSaisonnier.apply(EXTRAS_NOËL);
		assertThat(catalogueNoël).containsExactlyInAnyOrder(
				"Pain", "Croissant", "Pain au chocolat",
				"Buche Chocolat");

		Set<String> catalogueÉpiphanie = créerLeCatalogueSaisonnier.apply(EXTRAS_ÉPIPHANIE);
		assertThat(catalogueÉpiphanie).containsExactlyInAnyOrder(
				"Pain", "Croissant", "Pain au chocolat",
				"Galette");

		assertThat(ARTICLES_PERMANENTS).containsExactlyInAnyOrder(
				"Pain", "Croissant", "Pain au chocolat");
	}
}
